from text2vql.metamodel import MetaModel

info = MetaModel('test_metamodel/railway.ecore').get_metamodel_info()
print(info)
print(MetaModel('test_metamodel/railway.ecore').number_of_elements())


