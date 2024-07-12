package se.liu.ida.sas.pelab.querycomparison;

import java.io.File;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

/**
 * class SharedModelLoader extends ModelLoader {
 * val ResourceSet set;
 * val Map<String, Resource> resourceMap
 * new(ResourceSet resources, String directory){
 * set = resources
 * resourceMap = newHashMap
 * val dir = new File(directory)
 * val xmis = dir.listFiles.filter[file | file.isFile && file.name.endsWith(".xmi")]
 * for(xmi : xmis){
 * println(xmi.name)
 * val model = set.getResource(URI.createFileURI(xmi.absolutePath),true)
 * resourceMap.put(xmi.name, model)
 * }
 * }
 * 
 * override forEach(Procedure1<String> callback) {
 * throw new UnsupportedOperationException("TODO: auto-generated method stub")
 * }
 * 
 * }
 */
@SuppressWarnings("all")
public class SeparateModelLoader extends ModelLoader {
  private final ResourceSet set;

  private final List<File> xmis;

  public SeparateModelLoader(final ResourceSet resources, final String directory) {
    this.set = resources;
    final File dir = new File(directory);
    final Function1<File, Boolean> _function = (File file) -> {
      return Boolean.valueOf((file.isFile() && file.getName().endsWith(".xmi")));
    };
    this.xmis = IterableExtensions.<File>toList(IterableExtensions.<File>filter(((Iterable<File>)Conversions.doWrapArray(dir.listFiles())), _function));
  }

  @Override
  public <T extends Object> T fold(final T seed, final Function2<T, String, T> callback) {
    final Function2<T, File, T> _function = (T value, File xmi) -> {
      T _xblockexpression = null;
      {
        final Resource model = this.set.getResource(URI.createFileURI(xmi.getAbsolutePath()), true);
        final T updated = callback.apply(value, xmi.getName());
        this.set.getResources().remove(model);
        _xblockexpression = updated;
      }
      return _xblockexpression;
    };
    return IterableExtensions.<File, T>fold(this.xmis, seed, _function);
  }
}
