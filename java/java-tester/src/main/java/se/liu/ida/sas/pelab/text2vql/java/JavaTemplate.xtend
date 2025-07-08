package se.liu.ida.sas.pelab.text2vql.java

class JavaTemplate{
    def generateJavaCode(String classname){
        return '''
        public class «classname» {
            public static void main(String[] args){
                System.out.println("Hello World!");
            }
        }
        '''
    }
}