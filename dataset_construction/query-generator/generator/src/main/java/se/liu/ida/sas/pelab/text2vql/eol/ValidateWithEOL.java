package se.liu.ida.sas.pelab.text2vql.eol;

import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.epl.EplModule;
import se.liu.ida.sas.pelab.text2vql.ResourcesHelper;
import se.liu.ida.sas.pelab.text2vql.helpers.RailwayLoader;

public class ValidateWithEOL {
    public static void main(String[] args) throws Exception {
        //var module = new EolModule();
        //module.parse(ResourcesHelper.javaURI("eol/sample.eol"));
        //module.execute();
        EplModule module = new EplModule();
        module.parse(ResourcesHelper.javaURI("eol/sample.epl"));

        InMemoryEmfModel model = new InMemoryEmfModel(RailwayLoader.loadRailway());
        module.getContext().getModelRepository().addModel(model);
        System.out.println(module.execute());
    }
}
