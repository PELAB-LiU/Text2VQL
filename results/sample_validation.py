"""LLM-judge labeling of a sample sheet.

Each row of the sheet (columns: metamodel, language, NL description, Query, Domain) is
classified by Claude Code (headless `claude -p`) into SE / R / A / C with a one-sentence
reason. Every call runs in a fresh, tool-less session from an empty temp directory, so no
CLAUDE.md, memory, settings or repository content reaches the judge.

The labels are written back into the given sheet as LLM_CATEGORY, LLM_NOTE and LLM_MODEL
columns (existing columns are left untouched). Rows that already carry a valid LLM label
are skipped, so an interrupted run can simply be restarted.

Example:
    python results/sample_validation.py dataset_construction/samples_daniel.xlsx --limit 3
"""
import argparse
import json
import os
import shutil
import subprocess
import tempfile

import openpyxl

CATEGORIES = ['SE', 'R', 'A', 'C']
LLM_COLUMNS = ['LLM_CATEGORY', 'LLM_NOTE', 'LLM_MODEL']

# Template placeholder -> sheet column ('Domain' holds the Ecore text of the metamodel)
PLACEHOLDERS = {
    'language': 'language',
    'metamodel': 'Domain',
    'nl': 'NL description',
    'query': 'Query',
}

DEFAULT_SYSTEM_PROMPT = 'You are a careful expert annotator of model queries over Ecore metamodels.'

# Placeholders: {language}, {metamodel}, {nl}, {query}, {examples} (filled by plain replacement)
# The four category definitions are fixed and must not be changed.
PROMPT_TEMPLATE = """\
Below are an Ecore metamodel (in textual notation), a natural language description of a query
over that metamodel, and a query in {language} that was written for this description.

<metamodel>
{metamodel}
</metamodel>

<description>
{nl}
</description>

<query language="{language}">
{query}
</query>
{examples}
Classify this sample into exactly one category, judging the description against the
metamodel and the query against the description.

The classification used four exclusive categories:
(1) specification error (SE), if natural language description is ambiguous, inconsistent or incomplete in the domain, 
(2) reject (R), if the query is not compliant with the natural language description, 
(3) acceptable (A), if the query is expected to deliver correct results, but experts would nevertheless write it differently (e.g., there are extra query parameters), and 
(4) correct (C), if the query is adjudicated to be fine as-is.

Answer with the category code (SE, R, A or C) and a note: one sentence giving the concrete
reason for the category, naming the relevant metamodel elements or query parts.
"""

OUTPUT_SCHEMA = {
    'type': 'object',
    'properties': {
        'category': {'type': 'string', 'enum': CATEGORIES},
        'note': {'type': 'string'},
    },
    'required': ['category', 'note'],
    'additionalProperties': False,
}


def resolve_claude_bin(claude_bin):
    candidate = claude_bin or os.environ.get('CLAUDE_BIN') or shutil.which('claude')
    if not candidate or not os.path.isfile(candidate):
        raise SystemExit('Claude CLI not found: pass --claude-bin or set $CLAUDE_BIN')
    return candidate


def render_prompt(template, row, examples):
    # Plain replacement instead of str.format, so braces in the template or queries are harmless
    prompt = template.replace('{examples}', examples)
    for placeholder, column in PLACEHOLDERS.items():
        value = row.get(column)
        prompt = prompt.replace('{' + placeholder + '}', '' if value is None else str(value))
    return prompt


def classify(claude_bin, model, system_prompt, prompt, timeout):
    env = dict(os.environ, CLAUDE_CODE_DISABLE_AUTO_MEMORY='1')
    with tempfile.TemporaryDirectory(prefix='sample_validation_') as cwd:
        proc = subprocess.run(
            [claude_bin, '-p',
             '--model', model,
             '--system-prompt', system_prompt,
             '--tools', '',
             '--setting-sources', '',
             '--no-session-persistence',
             '--output-format', 'json',
             '--json-schema', json.dumps(OUTPUT_SCHEMA)],
            input=prompt, capture_output=True, text=True, encoding='utf-8', errors='replace',
            cwd=cwd, env=env, timeout=timeout)
    if proc.returncode != 0:
        raise RuntimeError(f'claude exited with {proc.returncode}: {(proc.stderr or proc.stdout).strip()[:500]}')
    response = json.loads(proc.stdout)
    if response.get('is_error'):
        raise RuntimeError(f'claude reported an error: {str(response.get("result"))[:500]}')
    output = response.get('structured_output')
    if not isinstance(output, dict) or output.get('category') not in CATEGORIES:
        raise RuntimeError(f'invalid structured output: {str(output)[:500]}')
    return output['category'], output['note'].strip()


def main(args):
    claude_bin = resolve_claude_bin(args.claude_bin)
    system_prompt = DEFAULT_SYSTEM_PROMPT
    if args.system_prompt_file:
        with open(args.system_prompt_file, encoding='utf-8') as f:
            system_prompt = f.read()

    wb = openpyxl.load_workbook(args.sheet)
    ws = wb[args.sheet_name] if args.sheet_name else wb.active
    header = [cell.value for cell in ws[1]]
    for column in PLACEHOLDERS.values():
        if column not in header:
            raise SystemExit(f'Column "{column}" missing from {args.sheet}')
    for column in LLM_COLUMNS:
        if column not in header:
            header.append(column)
            ws.cell(row=1, column=len(header), value=column)
    col = {name: header.index(name) + 1 for name in LLM_COLUMNS}

    rows = list(range(2, ws.max_row + 1))
    if args.limit is not None:
        rows = rows[:args.limit]

    done, failed, skipped = 0, 0, 0
    for i in rows:
        row = {name: ws.cell(row=i, column=j + 1).value for j, name in enumerate(header)}
        if row['LLM_CATEGORY'] in CATEGORIES:
            skipped += 1
            continue
        if not row.get('NL description') and not row.get('Query'):
            continue

        prompt = render_prompt(PROMPT_TEMPLATE, row, examples='')
        for attempt in range(2):
            try:
                category, note = classify(claude_bin, args.model, system_prompt, prompt, args.timeout)
                break
            except (RuntimeError, subprocess.TimeoutExpired, json.JSONDecodeError) as e:
                category, note = 'ERROR', str(e)
                print(f'row {i}: attempt {attempt + 1} failed: {e}')

        ws.cell(row=i, column=col['LLM_CATEGORY'], value=category)
        ws.cell(row=i, column=col['LLM_NOTE'], value=note)
        ws.cell(row=i, column=col['LLM_MODEL'], value=args.model)
        wb.save(args.sheet)  # save after every row so an interrupted run loses nothing
        if category == 'ERROR':
            failed += 1
        else:
            done += 1
        print(f'row {i}/{ws.max_row}: {category} - {note}')

    print(f'labeled {done}, failed {failed}, already labeled {skipped}')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('sheet', type=str, help='sample xlsx, e.g. dataset_construction/samples_daniel.xlsx')
    parser.add_argument('--system-prompt-file', type=str, default=None)
    parser.add_argument('--model', type=str, default='claude-opus-5-5')
    parser.add_argument('--claude-bin', type=str, default=None, help='path to the claude CLI (default: $CLAUDE_BIN, then PATH)')
    parser.add_argument('--sheet-name', type=str, default=None, help='worksheet to label (default: active sheet)')
    parser.add_argument('--limit', type=int, default=None, help='only process the first n rows (dry runs)')
    parser.add_argument('--timeout', type=int, default=300, help='seconds per claude call')
    args = parser.parse_args()
    main(args)
