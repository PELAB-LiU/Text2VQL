from text2vql.util.metamodel import MetaModel

SEED_METAMODEL = MetaModel('../seed/yakindu_simplified.ecore')

print(SEED_METAMODEL.get_metamodel_info())