from datasets import load_dataset

final_dataset = load_dataset('antolin/text2vql', split='train')

print(final_dataset)

# calculate avg number of lines
num_lines = [len(example['pattern'].split('\n')) for example in final_dataset]
print(f'Average number of lines: {sum(num_lines) / len(num_lines)}')
print(f'Median number of lines: {sorted(num_lines)[len(num_lines) // 2]}')
print(f'Max number of lines: {max(num_lines)}')
print(f'Min number of lines: {min(num_lines)}')

# examples
print(final_dataset[0]['nl'])
print(final_dataset[0]['pattern'])
print(final_dataset[1]['nl'])
print(final_dataset[1]['pattern'])
print(final_dataset[-4]['nl'])
print(final_dataset[-4]['pattern'])
