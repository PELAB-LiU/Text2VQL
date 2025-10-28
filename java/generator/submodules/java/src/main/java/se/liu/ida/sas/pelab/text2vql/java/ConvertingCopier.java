package se.liu.ida.sas.pelab.text2vql.java;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/*
 * This is mostly a copy of EcoreUtil.Copier
 * Key modification is that it uses a different factory
 */
public class ConvertingCopier extends LinkedHashMap<EObject, EObject> {
    private static final long serialVersionUID = 1L;

    /**
     * Whether proxies should be resolved during copying.
     */
    protected boolean resolveProxies = true;

    /**
     * Whether non-copied references should be used during copying.
     */
    protected boolean useOriginalReferences = true;

    /**
     * Creates an instance.
     */
    private final Map<String, EFactory> factories = new HashMap<>();

    public ConvertingCopier(URLClassLoader classLoader, Resource metamodels) {
        metamodels.getContents().forEach(eObject -> {
            if (eObject instanceof EPackage ePackage) {
                String packageName = ePackage.getName();
                String packageClassName = packageName.substring(0, 1).toUpperCase()
                        + packageName.substring(1).toLowerCase() + "Package";
                try {
                    //System.out.println(
                    //        "Factory (registered): " + EPackage.Registry.INSTANCE.getEFactory(ePackage.getNsURI()));
                    Class<?> pkgClass = classLoader.loadClass(packageName + "." + packageClassName);
                    System.err.println("java.lang.ClassCastException is expected for the next line for Factories.");
                    EPackage compiledPackage = (EPackage) pkgClass.getField("eINSTANCE").get(null);
                    //System.out.println("Factory (loaded): " + compiledPackage.getEFactoryInstance());
                    factories.put(packageName, compiledPackage.getEFactoryInstance());
                } catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
                        | NoSuchFieldException | SecurityException e) {
                    // e.printStackTrace();
                }

            }
        });
    }

    private EObject create(EClass eclass) {
        EFactory factory = factories.getOrDefault(eclass.getEPackage().getName(),
                eclass.getEPackage().getEFactoryInstance());

        EClassifier classifier = factory.getEPackage().getEClassifier(eclass.getName());
        return factory.create((EClass) classifier);
    }

    /**
     * Returns a collection containing a copy of each EObject in the given
     * collection.
     * 
     * @param eObjects the collection of objects to copy.
     * @return the collection of copies.
     */
    public <T> Collection<T> copyAll(Collection<? extends T> eObjects) {
        Collection<T> result = new ArrayList<T>(eObjects.size());
        for (Object object : eObjects) {
            @SuppressWarnings("unchecked")
            T t = (T) copy((EObject) object);
            if (t != null) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Returns a copy of the given eObject.
     * 
     * @param eObject the object to copy.
     * @return the copy.
     */
    public EObject copy(EObject eObject) {
        if (eObject == null) {
            return null;
        } else {
            EObject copyEObject = createCopy(eObject);
            if (copyEObject != null) {
                put(eObject, copyEObject);
                EClass eClass = eObject.eClass();
                for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
                    EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);
                    if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
                        if (eStructuralFeature instanceof EAttribute) {
                            copyAttribute((EAttribute) eStructuralFeature, eObject, copyEObject);
                        } else {
                            EReference eReference = (EReference) eStructuralFeature;
                            if (eReference.isContainment()) {
                                copyContainment(eReference, eObject, copyEObject);
                            }
                        }
                    }
                }

                copyProxyURI(eObject, copyEObject);
            }

            return copyEObject;
        }
    }

    /**
     * Copies the proxy URI from the original to the copy, if present.
     * 
     * @param eObject     the object being copied.
     * @param copyEObject the copy being initialized.
     */
    protected void copyProxyURI(EObject eObject, EObject copyEObject) {
        if (eObject.eIsProxy()) {
            ((InternalEObject) copyEObject).eSetProxyURI(((InternalEObject) eObject).eProxyURI());
        }
    }

    /**
     * Returns a new instance of the object's target class.
     * 
     * @param eObject the object to copy.
     * @return a new instance of the target class.
     * @see #getTarget(EObject)
     * @see EcoreUtil#create(EClass)
     */
    protected EObject createCopy(EObject eObject) {
        EClass eClass = getTarget(eObject);
        EObject newObject = eClass == null ? null : create(eClass);
        //System.out.println("Copy of " + eObject + " is " + newObject);
        return newObject;
    }

    /**
     * Returns the target class used to create a copy instance for the given
     * instance object.
     * 
     * @param eObject the object to be copied.
     * @return the target class used to create a copy instance.
     * @since 2.10
     */
    protected EClass getTarget(EObject eObject) {
        return getTarget(eObject.eClass());
    }

    /**
     * Returns the target class used to create a copy instance for objects of the
     * given source class.
     * 
     * @param eClass the source class.
     * @return the target class used to create a copy instance.
     * @see #getTarget(EStructuralFeature, EObject, EObject)
     */
    protected EClass getTarget(EClass eClass) {
        return eClass;
    }

    /**
     * Returns a setting for the feature and copy instance to be populated with the
     * original object's source feature's value.
     * 
     * @param eStructuralFeature the source feature.
     * @return the target feature used to populate a copy instance.
     * @see #getTarget(EStructuralFeature)
     * @see #getTarget(EObject)
     * @since 2.10
     */
    protected EStructuralFeature.Setting getTarget(EStructuralFeature eStructuralFeature, EObject eObject,
            EObject copyEObject) {
        EStructuralFeature targetEStructuralFeature = getTarget(eStructuralFeature);
        return targetEStructuralFeature == null ? null
                : ((InternalEObject) copyEObject).eSetting(targetEStructuralFeature);
    }

    /**
     * MODIFIED: Translates EStructuralFeature from source to target package
     * Returns the target feature used to populate a copy instance from the given
     * source feature.
     * 
     * @param eStructuralFeature the source feature.
     * @return the target feature used to populate a copy instance.
     * @see #getTarget(EClass)
     */
    protected EStructuralFeature getTarget(EStructuralFeature sourceEStructuralFeature) {
        EClass sourceClass = sourceEStructuralFeature.getEContainingClass();

        EPackage sourcePackage = sourceClass.getEPackage();
        EFactory targetFactory = factories.getOrDefault(sourcePackage.getName(), sourcePackage.getEFactoryInstance());

        EPackage targetPackage = targetFactory.getEPackage();
        EClass targetClass = (EClass) targetPackage.getEClassifier(sourceClass.getName());
        EStructuralFeature targetFeature = targetClass.getEStructuralFeature(sourceEStructuralFeature.getName());
        //System.out.println("Mapped:" + sourceEStructuralFeature + " --> " + targetFeature);
        return targetFeature;
    }

    /**
     * Called to handle the copying of a containment feature;
     * this adds a list of copies or sets a single copy as appropriate for the
     * multiplicity.
     * 
     * @param eReference  the feature to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject) {
        if (eObject.eIsSet(eReference)) {
            EStructuralFeature.Setting setting = getTarget(eReference, eObject, copyEObject);
            if (setting != null) {
                Object value = eObject.eGet(eReference);
                if (eReference.isMany()) {
                    @SuppressWarnings("unchecked")
                    List<EObject> target = (List<EObject>) value;
                    setting.set(copyAll(target));
                } else {
                    setting.set(copy((EObject) value));
                }
            }
        }
    }

    /**
     * Called to handle the copying of an attribute;
     * this adds a list of values or sets a single value as appropriate for the
     * multiplicity.
     * 
     * @param eAttribute  the attribute to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyAttribute(EAttribute eAttribute, EObject eObject, EObject copyEObject) {
        if (eObject.eIsSet(eAttribute)) {
            if (FeatureMapUtil.isFeatureMap(eAttribute)) {
                FeatureMap featureMap = (FeatureMap) eObject.eGet(eAttribute);
                copyFeatureMap(featureMap);
            } else {
                EStructuralFeature.Setting setting = getTarget(eAttribute, eObject, copyEObject);
                if (setting != null) {
                    copyAttributeValue(eAttribute, eObject, eObject.eGet(eAttribute), setting);
                }
            }
        }
    }

    /**
     * Call to handle copying the contained objects within a feature map.
     * 
     * @param featureMap the feature map the copy.
     * @since 2.10
     */
    protected void copyFeatureMap(FeatureMap featureMap) {
        for (int i = 0, size = featureMap.size(); i < size; ++i) {
            EStructuralFeature feature = featureMap.getEStructuralFeature(i);
            if (feature instanceof EReference && ((EReference) feature).isContainment()) {
                Object value = featureMap.getValue(i);
                if (value != null) {
                    // The containment references are hooked up later during copyReferences.
                    //
                    copy((EObject) value);
                }
            }
        }
    }

    /**
     * MODIFIED: Translates EEnum literals form source to traget package.
     * Called to handle copying of an attribute's value to the target setting.
     * 
     * @param eAttribute the attribute of the source object corresponding to the
     *                   value.
     * @param eObject    the object being copied.
     * @param value      the value to be copied.
     * @param setting    the feature-value pair that is the target of of the copy.
     * @since 2.10
     */
    protected void copyAttributeValue(EAttribute eAttribute, EObject eObject, Object value,
            EStructuralFeature.Setting setting) {
        if (value != null && eAttribute.getEAttributeType() instanceof EEnum sourceEnum) {
            EEnum targetEnum = (EEnum) setting.getEObject().eClass()
                    .getEStructuralFeature(eAttribute.getName())
                    .getEType();

            if (value instanceof EEnumLiteral literal) {
                EEnumLiteral targetLiteral = targetEnum.getEEnumLiteral(literal.getName());
                if (targetLiteral != null) {
                    //System.out.println("Literal: "+value+value.hashCode()+ " --> "+targetLiteral.getInstance()+targetLiteral.getInstance().hashCode());
                    setting.set(targetLiteral.getInstance());
                    return;
                }
            } else if (value instanceof Enumerator enumerator) {
                EEnumLiteral targetLiteral = targetEnum.getEEnumLiteral(enumerator.getName());
                if (targetLiteral != null) {
                    setting.set(targetLiteral.getInstance());
                    return;
                }
            }
        }
        setting.set(value);
    }

    /**
     * Hooks up cross references; it delegates to {@link #copyReference
     * copyReference}.
     */
    public void copyReferences() {
        for (Map.Entry<EObject, EObject> entry : entrySet()) {
            EObject eObject = entry.getKey();
            EObject copyEObject = entry.getValue();
            EClass eClass = eObject.eClass();
            for (int j = 0, size = eClass.getFeatureCount(); j < size; ++j) {
                EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(j);
                if (eStructuralFeature.isChangeable() && !eStructuralFeature.isDerived()) {
                    if (eStructuralFeature instanceof EReference) {
                        EReference eReference = (EReference) eStructuralFeature;
                        if (!eReference.isContainment() && !eReference.isContainer()) {
                            copyReference(eReference, eObject, copyEObject);
                        }
                    } else if (FeatureMapUtil.isFeatureMap(eStructuralFeature)) {
                        FeatureMap copyFeatureMap = (FeatureMap) getTarget(eStructuralFeature, eObject, copyEObject);
                        if (copyFeatureMap != null) {
                            FeatureMap featureMap = (FeatureMap) eObject.eGet(eStructuralFeature);
                            int copyFeatureMapSize = copyFeatureMap.size();
                            for (int k = 0, featureMapSize = featureMap.size(); k < featureMapSize; ++k) {
                                EStructuralFeature feature = featureMap.getEStructuralFeature(k);
                                if (feature instanceof EReference) {
                                    Object referencedEObject = featureMap.getValue(k);
                                    Object copyReferencedEObject = get(referencedEObject);
                                    if (copyReferencedEObject == null && referencedEObject != null) {
                                        EReference reference = (EReference) feature;
                                        if (!useOriginalReferences || reference.isContainment()
                                                || reference.getEOpposite() != null) {
                                            continue;
                                        }
                                        copyReferencedEObject = referencedEObject;
                                    }

                                    // If we can't add it, it must already be in the list so find it and move it to
                                    // the end.
                                    //
                                    if (!copyFeatureMap.add(feature, copyReferencedEObject)) {
                                        for (int l = 0; l < copyFeatureMapSize; ++l) {
                                            if (copyFeatureMap.getEStructuralFeature(l) == feature
                                                    && copyFeatureMap.getValue(l) == copyReferencedEObject) {
                                                copyFeatureMap.move(copyFeatureMap.size() - 1, l);
                                                --copyFeatureMapSize;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    copyFeatureMap.add(getTarget(featureMap.getEStructuralFeature(k)),
                                            featureMap.getValue(k));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called to handle the copying of a cross reference;
     * this adds values or sets a single value as appropriate for the multiplicity
     * while omitting any bidirectional reference that isn't in the copy map.
     * 
     * @param eReference  the reference to copy.
     * @param eObject     the object from which to copy.
     * @param copyEObject the object to copy to.
     */
    protected void copyReference(EReference eReference, EObject eObject, EObject copyEObject) {
        if (eObject.eIsSet(eReference)) {
            EStructuralFeature.Setting setting = getTarget(eReference, eObject, copyEObject);
            if (setting != null) {
                Object value = eObject.eGet(eReference, resolveProxies);
                if (eReference.isMany()) {
                    @SuppressWarnings("unchecked")
                    InternalEList<EObject> source = (InternalEList<EObject>) value;
                    @SuppressWarnings("unchecked")
                    InternalEList<EObject> target = (InternalEList<EObject>) setting;
                    if (source.isEmpty()) {
                        target.clear();
                    } else {
                        boolean isBidirectional = eReference.getEOpposite() != null;
                        int index = 0;
                        for (Iterator<EObject> k = resolveProxies ? source.iterator() : source.basicIterator(); k
                                .hasNext();) {
                            EObject referencedEObject = k.next();
                            EObject copyReferencedEObject = get(referencedEObject);
                            if (copyReferencedEObject == null) {
                                if (useOriginalReferences && !isBidirectional) {
                                    target.addUnique(index, referencedEObject);
                                    ++index;
                                }
                            } else {
                                if (isBidirectional) {
                                    int position = target.indexOf(copyReferencedEObject);
                                    if (position == -1) {
                                        target.addUnique(index, copyReferencedEObject);
                                    } else if (index != position) {
                                        target.move(index, copyReferencedEObject);
                                    }
                                } else {
                                    target.addUnique(index, copyReferencedEObject);
                                }
                                ++index;
                            }
                        }
                    }
                } else {
                    if (value == null) {
                        setting.set(null);
                    } else {
                        Object copyReferencedEObject = get(value);
                        if (copyReferencedEObject == null) {
                            if (useOriginalReferences && eReference.getEOpposite() == null) {
                                setting.set(value);
                            }
                        } else {
                            setting.set(copyReferencedEObject);
                        }
                    }
                }
            }
        }
    }
}