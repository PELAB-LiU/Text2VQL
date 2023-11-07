from pyecore.ecore import EClass, EEnum, EReference
from pyecore.resources import ResourceSet, URI


class MetaModel:
    def __init__(self, path, domain=None):
        self.path = path
        rset = ResourceSet()
        resource = rset.get_resource(URI(path))
        mm_root = resource.contents[0]
        rset.metamodel_registry[mm_root.nsURI] = mm_root
        self.root = mm_root
        self.domain = domain

    def get_metamodel_info(self) -> str:
        info = ""
        for classifier in self.root.eClassifiers:
            if isinstance(classifier, EClass):
                supertypes = ', '.join(s.name for s in classifier.eSuperTypes)
                if classifier.eSuperTypes:
                    info += f"class {classifier.name} extends {supertypes}" + " {\n"
                else:
                    info += f"class {classifier.name}" + " {\n"
                for feature in classifier.eStructuralFeatures:
                    keyword = "ref" if isinstance(feature, EReference) else "attr"
                    info += f"\t{keyword} {feature.eType.name}[{feature.lowerBound}, {feature.upperBound if feature.upperBound!= -1 else '*'}] {feature.name};\n"
                info += "}\n"
            elif isinstance(classifier, EEnum):
                info += f"enum {classifier.name}" + " {\n"
                for literal in classifier.eLiterals:
                    info += f"\t{literal.name};\n"
                info += "}\n"
        return info

    @property
    def metamodel_info(self) -> str:
        return self.get_metamodel_info()

    def number_of_elements(self) -> int:
        elements = 0
        for classifier in self.root.eClassifiers:
            if isinstance(classifier, EClass):
                elements += 1
                for _ in classifier.eStructuralFeatures:
                    elements += 1
            elif isinstance(classifier, EEnum):
                elements += 1
                for _ in classifier.eLiterals:
                    elements += 1
        return elements

    def get_elements(self):
        elements = []
        for classifier in self.root.eClassifiers:
            if isinstance(classifier, EClass):
                elements.append(classifier.name)
                for feature in classifier.eStructuralFeatures:
                    elements.append(feature.name)
            elif isinstance(classifier, EEnum):
                elements.append(classifier.name)
                for literal in classifier.eLiterals:
                    elements.append(literal.name)
        return elements
