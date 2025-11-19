package se.liu.ida.sas.pelab.text2vql.server;

public record SyntaxCheckRequest(String wd, String metamodel, String query, String jar){}

