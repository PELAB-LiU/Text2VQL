package se.liu.ida.sas.pelab.text2vql.comparison.input;

public record TestCase(String domain, Query truth, Query[] vql, Query[] ocl, Query[] java) {}
