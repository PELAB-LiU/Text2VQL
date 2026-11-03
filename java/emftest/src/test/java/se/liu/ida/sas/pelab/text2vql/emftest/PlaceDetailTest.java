package se.liu.ida.sas.pelab.text2vql.emftest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import placedetail.Address_component;
import placedetail.PlacedetailFactory;
import placedetail.PlacedetailPackage;

/**
 * Exercises EMF behaviour over the generated code of
 * jsonDiscoverer/zoo/googlePlaces/source4/source4.ecore (7524_source4.jar).
 */
class PlaceDetailTest {
    private static final PlacedetailFactory factory = PlacedetailFactory.eINSTANCE;
    private static final PlacedetailPackage pkg = PlacedetailPackage.eINSTANCE;

    @Test
    void unsetNamesAreNull() {
        Address_component component = factory.createAddress_component();

        assertNull(component.getLong_name());
        assertNull(component.getShort_name());
        assertNull(component.eGet(pkg.getAddress_component_Long_name()));
        assertNull(component.eGet(pkg.getAddress_component_Short_name()));
        assertFalse(component.eIsSet(pkg.getAddress_component_Long_name()));
        assertFalse(component.eIsSet(pkg.getAddress_component_Short_name()));
    }
}
