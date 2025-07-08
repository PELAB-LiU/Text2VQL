package se.liu.ida.sas.pelab.text2vql.yamtl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import com.google.common.collect.Table;

import yamtl.core.YAMTLModule.ExecutionPhase;
import yamtl.core.YAMTLModule;
import yamtl.core.YAMTLModuleGroovy;

import static yamtl.dsl.Rule.*;
import static yamtl.dsl.Helper.*;

public class QueryWrapper extends YAMTLModule {
	public QueryWrapper(EPackage inputpackage) {
		header().in("cd", inputpackage);//.out("db", DB);
        this.
        ruleStore(List.of(
        		rule("ClassToTable")
                        .in("route", "railway::Route")
                        .in("sensor", "railway::Sensor")
                        .filter(() -> {
                            Object route = fetch("route");
                            Object sensor = fetch("sensor");

                            // Route.follows → [SwitchPosition]
                            List<?> swPositions = (List<?>) getFeature(route, "follows");

                            for (Object swP : swPositions) {
                                // SwitchPosition.target → Switch
                                Object sw = getFeature(swP, "target");
                                if (sw == null) continue;

                                // Switch.monitoredBy → [Sensor]
                                List<?> sensors = (List<?>) getFeature(sw, "monitoredBy");
                                if (sensors.contains(sensor)) {
                                    // Route.requires → [Sensor]
                                    List<?> requiredSensors = (List<?>) getFeature(route, "requires");
                                    return !requiredSensors.contains(sensor);
                                }
                            }
                            return false;
                        })
                        .query()
                )
        );
	}
    private Object getFeature(Object object, String feature){
        var obj = (EObject) object;
        return obj.eGet(obj.eClass().getEStructuralFeature(feature));
    }
}
