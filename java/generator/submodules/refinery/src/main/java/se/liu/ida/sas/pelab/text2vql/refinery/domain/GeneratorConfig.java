package se.liu.ida.sas.pelab.text2vql.refinery.domain;

public record GeneratorConfig(int seedless, int seeded) {
    public static GeneratorConfig def(){
        return new GeneratorConfig(200, 200);
    }
}
