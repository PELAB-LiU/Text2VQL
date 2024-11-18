package se.liu.ida.sas.pelab.text2vql.testing.matchset

import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.Resource
import java.util.Map
import java.io.File
import org.eclipse.emf.common.util.URI
import org.eclipse.collections.api.block.procedure.Procedure
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1
import java.util.List
import javax.security.auth.callback.Callback
import org.eclipse.xtext.xbase.lib.Functions.Function2

abstract class ModelLoader{
	
	def <T> T fold(T seed, Function2<T, String, T> callback)
}
/*
class SharedModelLoader extends ModelLoader {
	val ResourceSet set;
	val Map<String, Resource> resourceMap
	new(ResourceSet resources, String directory){
		set = resources
		resourceMap = newHashMap
		val dir = new File(directory)
		val xmis = dir.listFiles.filter[file | file.isFile && file.name.endsWith(".xmi")]
		for(xmi : xmis){
			println(xmi.name)
			val model = set.getResource(URI.createFileURI(xmi.absolutePath),true)
			resourceMap.put(xmi.name, model)
		}
	}
	
	override forEach(Procedure1<String> callback) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}*/

class SeparateModelLoader extends ModelLoader{
	val ResourceSet set;
	val List<File> xmis
	new(ResourceSet resources, String directory){
		set = resources
		val dir = new File(directory)
		xmis = dir.listFiles.filter[file | file.isFile && file.name.endsWith(".xmi")].toList
	}
	override <T> fold(T seed, Function2<T, String, T> callback){
		xmis.fold(seed, [value, xmi |
			val model = set.getResource(URI.createFileURI(xmi.absolutePath),true)
			val updated =  callback.apply(value, xmi.name)
			set.resources.remove(model)
			updated
		])
		/*for(xmi : xmis){
			val model = set.getResource(URI.createFileURI(xmi.absolutePath),true)
			//println(xmi.name)
			callback.apply(xmi.name)
			set.resources.remove(model)
		}*/
	}
}