from pyecore.ecore import EClass, EEnum
from pyecore.resources import ResourceSet, URI


class MetaModel:
    def __init__(self, path):
        self.path = path
        rset = ResourceSet()
        resource = rset.get_resource(URI(path))
        mm_root = resource.contents[0]
        rset.metamodel_registry[mm_root.nsURI] = mm_root
        self.root = mm_root

    def get_metamodel_info(self) -> str:
        info = ""
        for classifier in self.root.eClassifiers:
            if isinstance(classifier, EClass):
                supertypes = ', '.join(s.name for s in classifier.eSuperTypes)
                if classifier.eSuperTypes:
                    info += f"EClass {classifier.name} -> {supertypes}\n"
                else:
                    info += f"EClass {classifier.name}\n"
                for feature in classifier.eStructuralFeatures:
                    info += f"\t{feature.name}[{feature.lowerBound}, {feature.upperBound if feature.upperBound!= -1 else '*'}]: {feature.eType.name}\n"
            elif isinstance(classifier, EEnum):
                info += f"EEnum {classifier.name}\n"
                for literal in classifier.eLiterals:
                    info += f"\t{literal.name}\n"
        return info

    @property
    def metamodel_info(self) -> str:
        return self.get_metamodel_info()
