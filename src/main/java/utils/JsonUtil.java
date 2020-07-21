package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final GsonBuilder BUILDER = new GsonBuilder();
    private static final Gson GSON = BUILDER.create();

    public static String toJsonStr(Object javaBean) {
        return GSON.toJson(javaBean);
    }

    public static <T> T toJavaBean(String jsonStr, Class<T> typeOfClass) {
        return GSON.fromJson(jsonStr, typeOfClass);
    }
}
