import torch
from peft import PeftModel
from tqdm import tqdm
from transformers import AutoModelForCausalLM, AutoTokenizer

# from test_model_ff import PROMPT_WITH_HEADER_CODE
from text2vql.metamodel import MetaModel

from transformers.trainer_utils import set_seed

set_seed(123)

PROMPT_WITH_HEADER_CODE = """{metamodel}
//{nl}
{header}
"""

PATH = "models/deepseek-coder/checkpoint-921"
BASE_MODEL = "deepseek-ai/deepseek-coder-6.7b-base"
METAMODEL = MetaModel('test_metamodel/railway.ecore')
model = AutoModelForCausalLM.from_pretrained(BASE_MODEL,
                                             trust_remote_code=True,
                                             torch_dtype=torch.float16,
                                             device_map="auto")

model = PeftModel.from_pretrained(model, PATH).eval()

tokenizer = AutoTokenizer.from_pretrained(PATH)

prompts = []

# Find queries

prompt_find_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                               nl="Four segments monitored by with the same sensor. Each segment is connected to the next one.",
                                               header="pattern connectedSegments4(sensor : Sensor, segment1 : Segment, segment2 : Segment, segment3 : Segment, segment4 : Segment){")
prompts.append(prompt_find_1)

# or queries


prompt_or_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                             nl="Semaphores with STOP or GO signals.",
                                             header="pattern stopOrGo(semaphore: Semaphore){")

prompt_or_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                             nl="TrackElements that are either connected or monitored by the same sensor.",
                                             header="pattern relatedTrackElements(track1: TrackElement, track2: "
                                                    "TrackElement){")

prompts.append(prompt_or_1)
prompts.append(prompt_or_2)

# negation

prompt_neg_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Switches not monitored by a sensor.",
                                              header="pattern switchNotMonitored(sw : Switch){")

prompt_neg_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Retrieve sensors that monitor a switch. The switch position that target that switch is followed by a route. That sensor is not required by the route.",
                                              header="pattern notRequiredRouteSensor(route : Route, sensor : Sensor, swP : SwitchPosition, sw : Switch){")

prompts.append(prompt_neg_1)
prompts.append(prompt_neg_2)

# agg

prompt_agg_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Total length of segments.",
                                              header="pattern totalLength(length: java Integer){")
prompt_agg_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Track elements monitored by at lest two sensors.",
                                              header="pattern monitoredBy2Sensors(track: TrackElement){")

prompts.append(prompt_agg_1)
prompts.append(prompt_agg_2)

# type checks

prompt_type_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                               nl="RailwayElements that are either Switches or SwitchPositions.",
                                               header="pattern switchOrSwitchPositionRailwayElemements(raliway: RailwayElement){")
prompt_type_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                               nl="Railway elements that are Switches.",
                                               header="pattern switchRailwayElements(railway: RailwayElement){")
prompts.append(prompt_type_1)
prompts.append(prompt_type_2)

# normal queries

prompt_normal_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                                 nl="Active routes with GO semaphore.",
                                                 header="pattern goRoute(route: Route){")
prompt_normal_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                                 nl="SwitchPositions where the target Switch's current position is different from the SwitchPosition's position.",
                                                 header="pattern misalignedSwitchPosition(swP : SwitchPosition){")

prompts.append(prompt_normal_1)
prompts.append(prompt_normal_2)

# mix queries

prompt_mix_1 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Regions where the sum of segments lengths is at least 50, or regions with at least 10 sensors.",
                                              header="pattern bigRegion(region: Region){")
prompt_mix_2 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Segments that are at least 7 units long or are switches.",
                                              header="pattern sevenSegmentOrActiveSwitch(track: TrackElement){")
prompt_mix_3 = PROMPT_WITH_HEADER_CODE.format(metamodel=METAMODEL.get_metamodel_info(),
                                              nl="Active route with GO semaphore but not monitored by any sensor.",
                                              header="pattern dangerousRoute(route: Route){")

prompts.append(prompt_mix_1)
prompts.append(prompt_mix_2)
prompts.append(prompt_mix_3)

for prompt in tqdm(prompts, desc='main loop'):
    print(prompt)
    sample = tokenizer([prompt], return_tensors="pt")

    with torch.no_grad():
        generated_sequences = model.generate(
            input_ids=sample["input_ids"].cuda(),
            attention_mask=sample["attention_mask"].cuda(),
            do_sample=True,
            max_new_tokens=512,
            num_return_sequences=5,
            temperature=0.4,
            pad_token_id=tokenizer.eos_token_id,
            eos_token_id=tokenizer.eos_token_id
        )

    generated_sequences = generated_sequences.cpu().numpy()
    generated_new_tokens = generated_sequences[:, sample["input_ids"].shape[1]:]

    for new_tokens in generated_new_tokens[0:1]:
        generated = tokenizer.decode(generated_new_tokens[0], skip_special_tokens=True)
        print(generated)
    print('-' * 100)
