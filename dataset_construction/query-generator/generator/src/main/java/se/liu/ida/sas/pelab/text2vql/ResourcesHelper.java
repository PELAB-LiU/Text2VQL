package se.liu.ida.sas.pelab.text2vql;

import org.eclipse.emf.common.util.URI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ResourcesHelper {
    public static URI emfURI(String file){
        try {
            return URI.createURI(Objects.requireNonNull(
                            ResourcesHelper.class.getClassLoader().getResource(file)).toURI().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static java.net.URI javaURI(String file){
        try {
            return Objects.requireNonNull(
                    ResourcesHelper.class.getClassLoader().getResource(file)).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public static InputStream inputStream(String file){
        try {
            return Files.newInputStream(Paths.get(Objects.requireNonNull(
                    ResourcesHelper.class.getClassLoader().getResource(file)).toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String string(String file){
        try {
            return Files.readString(Paths.get(Objects.requireNonNull(
                    ResourcesHelper.class.getClassLoader().getResource(file)).toURI()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
