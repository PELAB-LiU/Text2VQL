package se.liu.ida.sas.pelab.text2vql.vql.matchset;

import com.google.inject.Guice;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup;
import org.eclipse.viatra.query.patternlanguage.emf.EMFPatternLanguageStandaloneSetup.StandaloneParserWithSeparateModules;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParserBuilder;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.emf.EMFScope;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchEMFBackendFactory;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import org.eclipse.xtext.resource.XtextResourceSet;
import se.liu.ida.sas.pelab.text2vql.utilities.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Comparison {
    private final PatternParsingResults queries1;
    private final PatternParsingResults queries2;
    private final AdvancedViatraQueryEngine engine;
    private final ResourceSet resourceSet;

    public static void main(String[] args) throws IOException {
        Config cfg = new Config(args);

        ResourceSet rs = setup();

        loadMetamodel(cfg.asString("meta"), rs);

        Comparison instance = new Comparison(cfg, rs);
        instance.compare(cfg.asString("query1"), cfg.asString("query2"));
    }

    public void compare(String query1, String query2) {
        if (!isCompatible(query1, query2)) {
            System.out.println("Incompatible query header.");
            return;
        }
        List<Object[]> matches1 = transformMatches(matchesOf(queries1, query1));
        List<Object[]> matches2 = transformMatches(matchesOf(queries2, query2));

        List<Object[]> common = common(matches1, matches2);
        List<Object[]> onlyQuery1 = minus(matches1, common);
        List<Object[]> onlyQuery2 = minus(matches2, common);

        System.out.println("|A ∩ B|=" + common.size());
        System.out.println("|A \\ B|=" + onlyQuery1.size());
        System.out.println("|B \\ A|=" + onlyQuery2.size());
    }

    private List<Object[]> minus(List<Object[]> ls1, List<Object[]> ls2) {
        List<Object[]> result = new ArrayList<>();
        for (Object[] item1 : ls1) {
            boolean contained = ls2.stream().anyMatch(item2 -> Arrays.equals(item1, item2));
            if (!contained) {
                result.add(item1);
            }
        }
        return result;
    }

    private List<Object[]> common(List<Object[]> ls1, List<Object[]> ls2) {
        List<Object[]> result = new ArrayList<>();
        for (Object[] item1 : ls1) {
            for (Object[] item2 : ls2) {
                if (Arrays.equals(item1, item2)) {
                    result.add(item1);
                }
            }
        }
        return result;
    }

    private boolean isCompatible(String query1, String query2) {
        return queries1.getQuerySpecification(query1).get().getParameters().size() ==
                queries2.getQuerySpecification(query2).get().getParameters().size();
    }

    private Object[] asArray(IPatternMatch match) {
        int cnt = match.specification().getParameters().size();
        Object[] array = new Object[cnt];
        for (int i = 0; i < cnt; i++) {
            array[i] = match.get(i);
        }
        return array;
    }

    private List<Object[]> transformMatches(List<IPatternMatch> matches) {
        List<Object[]> result = new ArrayList<>();
        for (IPatternMatch match : matches) {
            result.add(asArray(match));
        }
        return result;
    }

    public Comparison(Config cfg, ResourceSet resourceSet) throws IOException {
        this.resourceSet = resourceSet;
        this.queries1 = parseFile(cfg.asString("queries1"));
        this.queries2 = parseFile(cfg.asString("queries2"));

        loadModel(cfg.asString("instance"));

        this.engine = (AdvancedViatraQueryEngine) ViatraQueryEngine.on(new EMFScope(resourceSet));
    }

    private static PatternParsingResults parseFile(String path) throws IOException {
        PatternParsingResults parsed = PatternParserBuilder.instance().parse(Files.readString(Path.of(path)));
        if (parsed.hasError()) {
            System.out.println("Input " + path + " contains errors.");
            parsed.getErrors().forEach(System.out::println);
            return null;
        }
        return parsed;
    }

    private static void loadMetamodel(String path, ResourceSet resourceSet) {
        File metaFile = new File(path);
        EPackage pkg = (EPackage) resourceSet.getResource(
                URI.createFileURI(metaFile.getAbsolutePath()), true).getContents().get(0);
        EPackage.Registry.INSTANCE.put(pkg.getNsURI(), pkg);
    }

    private static ResourceSet setup() {
        EMFPatternLanguageStandaloneSetup.doSetup();
        StandaloneParserWithSeparateModules runtimeModule = new StandaloneParserWithSeparateModules();
        XtextResourceSet resourceSet = Guice.createInjector(runtimeModule).getInstance(XtextResourceSet.class);

        ViatraQueryEngineOptions.setSystemDefaultBackends(
                ReteBackendFactory.INSTANCE, ReteBackendFactory.INSTANCE, LocalSearchEMFBackendFactory.INSTANCE);
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());

        return resourceSet;
    }

    private List<IPatternMatch> matchesOf(PatternParsingResults queries, String name) {
        List<IPatternMatch> matches = new ArrayList<>();
        queries.getQuerySpecification(name).ifPresent(specification -> {
            var matcher = engine.getMatcher(specification);
            matches.addAll(matcher.getAllMatches());
        });
        return matches;
    }

    private void loadModel(String path) {
        resourceSet.getResource(URI.createFileURI(path), true);
        resourceSet.getResources().forEach(System.out::println);
    }
}
