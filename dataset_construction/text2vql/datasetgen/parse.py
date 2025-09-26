import sqlite3
import json
import re

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
        
    def split_to_blocks(self, data):
        blocks = re.split(r'(?=^\d)', data, flags=re.MULTILINE)
        return [block.strip() for block in blocks if block.strip()]

    def process_block(self, block):
        lines = block.splitlines()

        description = ""
        signature = ""
        query = ""

        # 1. Find description line (first line with number followed by alphanumeric)
        for i, line in enumerate(lines):
            match = re.match(r'^\s*\d+\s*[\W_]*\s*(.*\S)', line) #Regex verified with https://regexr.com/
            if match:
                description = match.group(1).strip()
                desc_end_line = i
                break

        # 2. Find signature line
        sig_line_index = None
        for i, line in enumerate(lines):
            if "Signature:" in line:
                signature = line.split("Signature:", 1)[1].strip()
                sig_line_index = i
                break

        # 3. Find query
        # 3a. Try to find query enclosed in ```
        query_match = re.search(r'```(.*?)```', text, re.DOTALL) # do not capture anything after the opening ``` in that line 
        if query_match:
            query = query_match.group(1).strip()
        else:
            # 3b. Fallback: lines after signature until a line ending with } or )
            # Java should always start with "import " or "public class" or "//"
            # Viatra should always start with "pattern" or "private pattern" or "//"
            # OCL should always start with alphabetic character?
            query_lines = []
            for line in lines[sig_line_index+1:]: # Maximum of signature and description (in case sig is missing)
                if line.strip() == "":
                    continue
                query_lines.append(line)
                if line.strip().endswith("}") or line.strip().endswith(")"): # This is incorrect: A line starting with these indicates that we left the scope of the block/function
                                                                             # If we dont find it, we should fall back to the last of these characters. 
                    break
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
        print(message.content[0].text)


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
    

