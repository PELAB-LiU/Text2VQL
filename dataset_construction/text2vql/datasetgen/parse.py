import sqlite3
import json
import re
import sys 
from text2vql.util.args import makeParser
from text2vql.seed.util import AttrDict

class Parser:
    def __init__(self, db):
        self.db = db

    def pull_metamodel(self, cluster):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT s.model FROM samples s JOIN domains d ON s.model = d.model WHERE d.compiled = 1 AND s.cluster = ? LIMIT 1", (cluster,))
            result = cursor.fetchone()
            if result:
                return result[0]
            return None
    
    def insertEntry(self, metamodel, lang, feat, descript, signat, pattern, generation):
        with sqlite3.connect(self.db) as conn:
            cursor = conn.cursor()
            cursor.execute("INSERT INTO chatgpt (metamodel, lang, feat, descript, signat, pattern, generation) VALUES (?, ?, ?, ?, ?, ?, ?)", (metamodel, lang, feat, descript, signat, pattern, generation))
            conn.commit()

    def split_to_blocks(self, data):
        blocks = re.split(r'(?=^\d)', data, flags=re.MULTILINE)
        blocks =  [block.strip() for block in blocks if block.strip()]

        # ChatGPT sometimes generate notes after the queries. We remove that so that it doesn't mess up the parser.
        if blocks:
            last_block = blocks[-1].splitlines()
            for i in range(len(last_block)-1, -1, -1):
                if re.match(r'^\s*Notes?:?', last_block[i], re.IGNORECASE): 
                    last_block = last_block[:i]
                    break
            blocks[-1] = "\n".join(last_block).strip()
        
        return blocks

    def process_block(self, block, langhint=None, verbose=True):
        if verbose:
            print(f"[Processing]========================================================\n{block}")

        lines = block.splitlines()

        
        

        # 1. Find description line (first line with number followed by alphanumeric)
        description = None
        desc_end_line = 0
        for i, line in enumerate(lines):
            match = re.match(r'^\s*\d+\s*[\W_]*\s*(.*\S)', line) #Regex verified with https://regexr.com/
            if match:
                description = match.group(1).strip()
                desc_end_line = i
                if verbose:
                    print(f"Description found on line {desc_end_line}.")
                break

        if description is None:
            if verbose:
                print("Description not found. Skip further processing for this block.")
            return None
        
        # Todo multiline description? -> No multiline description.
        lines = lines[desc_end_line+1:]

        # 2. Find signature line
        signature = None
        sig_line_index = 0
        for i, line in enumerate(lines):
            if "Signature:" in line:
                signature = line.split("Signature:", 1)[1].strip()
                sig_line_index = i
                if verbose:
                    print(f"Signature found on line {sig_line_index}.")
                break
        

        if signature is None:
            if verbose:
                print("Signature not found. Continue processing.")

        lines = lines[sig_line_index+1:]

        # 3. Find query
        # 3a. Try to find query enclosed in ```
        query = None
        query_match = re.search(r'```[^\n]*\n(.*?)```', block, re.DOTALL)
        if query_match:
            query = query_match.group(1).strip()
            if verbose:
                print(f"Code found in codeblock.")
        else:
            # 3b. Fallback: lines after signature until a line ending with } or )
            if verbose:
                print(f"Attempt to resolve code withoput code block (langhint {langhint})")
            start_regex = {}
            # Java should always start with "import " or "public class" or comment
            start_regex["java"] = r'^(?:import\s|public\s+class|\/\/|\/\*)' #Regex verified with https://regexr.com/
            # Viatra should always start with "pattern" or "private pattern" or comment
            start_regex["vql"] =  r'^(?:pattern\b|private\s+pattern\b|\/\/|\/\*)' #Regex verified with https://regexr.com/
            # OCL should always start with alphabetic character? or comment
            start_regex["ocl"] =  r'^[A-Za-z]|--|\/\*' #Regex verified with https://regexr.com/
            start_regex[None] =  r'^[A-Za-z]|--|\/\*|\/\/' #Regex verified with https://regexr.com/
            
            code_start_line = 0 # Start at 0 (default) so if nothing is found then everything is included after signature or description. (Syntax checker should kill it from processing further.)
            for i, line in enumerate(lines):
                match = re.match(start_regex[langhint], line) 
                if match:
                    if verbose:
                        print(f"Start found on line {i}")
                    code_start_line = i
                    break
            
            ## Sometimes chatgpr forgets that a class is expected and only provides a function.
            #partial_java_code = False
            #if code_start_line<0:
            #    code_start_line = 0
            #    if langhint is None or langhint == 'java':
            #        for i, line in enumerate(lines):
            #            match = re.match(r'^(?:public\b|private\b|\/\/|\/\*)', line) #Regex verified with https://regexr.com/
            #            if match:
            #                if verbose:
            #                    print(f"Partial start found on line {i}")
            #                partial_java_code = True
            #                code_start_line = i
            #                break
            #
            
            end_regex = {}
            # Java line ending. (We expect a class and therefore it should be a line starting with '}')
            end_regex["java"] = r'^[}]'
            # VQL line ending. (We expect a pattern and therefore it should be a line starting with '}')
            end_regex["vql"] =  r'^[}]'
            # OCL line ending. (We expect something and therefore it should be a line starting with '}' or ')')
            end_regex["ocl"] =  r'^[)}]'
            end_regex[None] =  r'^[)}]'
            code_end_line = 0
            for i, line in reversed(list(enumerate(lines))): # Enumerate backward
                match = re.match(end_regex[langhint], line)
                if match:
                    if verbose:
                        print(f"End found on line {i}")
                    code_end_line = i
                    break
            
            query_lines = lines[code_start_line:code_end_line+1]
            #if partial_java_code:
            #    query_lines = ['public class Query {']+query_lines+['}']
            
            query = "\n".join(query_lines).strip()

        return {
            "description": description,
            "signature": signature,
            "query": query
        }

    def processResponse(self, line):
        params = line.custom_id.split("_")
        lang = params[1]
        feat = params[2]
        cluster = params[3]

        mm = self.pull_metamodel(cluster)
        print(f"{cluster} --> {mm}")

        message = next((msg for msg in line.response.body.output if msg.type=="message"), None)

        blocks = self.split_to_blocks(message.content[0].text)
        for block in blocks:
            parsed = self.process_block(block, langhint=lang)
            if parsed is not None:
                print(f"============================================================\n- Desc: {parsed["description"]}\n- Signature: {parsed["signature"]}\n- Query:```\n{parsed["query"]}\n```")
                self.insertEntry(mm, lang, feat, parsed["description"], parsed["signature"], parsed["query"], 0)
            else:
                print(f"Failed to parse block (or ignored).")


if __name__ == "__main__":
    parser = makeParser()
    parser.add_argument('--files', type=str, help='Coma separated list of output files')
    args = parser.parse_args()

    ai = Parser(args.db)

    with open(args.files, 'r') as f:
        for line in f:
            # Parse the JSON object from the line
            record = AttrDict(json.loads(line))
            # Do something with the record
            ai.processResponse(record)
    

