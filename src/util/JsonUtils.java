package util;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static <T> T saveToFile(Object obj, String filePath) {
        try {
            mapper.writeValue(new File(filePath), obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T loadFromFile(String filePath, TypeReference<T> typeReference) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            InputStream is = new ClassPathResource(filePath).getInputStream();

            if (is == null) {
                System.out.println("File " + filePath + " not found!");
                return null;
            }

            return mapper.readValue(is, typeReference);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
