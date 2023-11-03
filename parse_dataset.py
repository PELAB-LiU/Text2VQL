import glob
import os

from tqdm import tqdm

from text2vql.metamodel import MetaModel

FOLDER = 'ecore555'

metamodels = []
# for each *.ecore
for file in tqdm(glob.glob(os.path.join(FOLDER, "*.ecore")), desc="Parsing metamodels"):
    try:
        metamodel = MetaModel(file)
        info = metamodel.get_metamodel_info()
        if len(info.strip()) == 0:
            continue
        metamodels.append(metamodel)
    except:
        continue

print(f"Parsed {len(metamodels)} metamodels out of {len(glob.glob(os.path.join(FOLDER, '*.ecore')))}")

number_elements = [m.number_of_elements() for m in metamodels]

print(f"Average number of elements: {sum(number_elements) / len(number_elements):.2f}")

threshold = 30
print(f"Metamodels with less than {threshold} elements: {len([n for n in number_elements if n < threshold])}")
