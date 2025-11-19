package se.liu.ida.sas.pelab.text2vql.utilities;

import java.io.File;

public class Text2VQLProjectStructure {
    public static File find(String folder) {
        return find("Text2VQL", folder);
    }
    public static File find(String root,String folder) {
        File current = new File(System.getProperty("user.dir"));
        while (current != null) {
            if (current.getName().equals(root)) {
                File child = new File(current, folder);
                if (child.exists()) {
                    return child;
                } else {
                    return null;
                }
            }
            current = current.getParentFile();
        }
        return null;
    }

    public static File createIn(String location, String subfolder) {
        return createIn("Text2VQL", location, subfolder);
    }
    public static File createIn(String root, String location, String subfolder) {
        File locationFile = Text2VQLProjectStructure.find(root, location);
        if(locationFile==null){
            return null;
        }
        File child = new File(locationFile, subfolder);
        if(!child.exists()){
            boolean created = child.mkdirs();
            if (!created) {
                return null;
            }
        }
        return child;
    }
}
