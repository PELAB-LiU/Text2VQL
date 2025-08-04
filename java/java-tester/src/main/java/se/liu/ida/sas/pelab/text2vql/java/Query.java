package se.liu.ida.sas.pelab.text2vql.java;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.MultiStringMatcher.Match;

import yakindumm.*;

public class Query {
    // Main pattern
public static record Match(Region region, Entry entry) {}
                        
                            public Set<Match> entryInRegion(Resource resource) {
                                Set<Match> result = new HashSet<>();
                        
                                for (EObject root : resource.getContents()) {
                                    collectEntryInRegion(root, result);
                                }
                        
                                return result;
                            }
                        
                            private void collectEntryInRegion(EObject eObject, Set<Match> result) {
                                if (eObject instanceof Region r) {
                                    for (Vertex v : r.getVertices()) {
                                        if (v instanceof Entry e) {
                                            result.add(new Match(r, e));
                                        }
                                    }
                                }
                        
                                for (EObject child : eObject.eContents()) {
                                    collectEntryInRegion(child, result);
                                }
                            }
                        

}
