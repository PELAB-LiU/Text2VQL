package se.liu.ida.sas.pelab.querycomparison;

import org.eclipse.xtext.xbase.lib.Functions.Function2;

@SuppressWarnings("all")
public abstract class ModelLoader {
  public abstract <T extends Object> T fold(final T seed, final Function2<T, String, T> callback);
}
