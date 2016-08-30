package pggtools.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class Atool {

    public static String readString(InputStream inputStream, String encoding, Integer bufferSize)
            throws UnsupportedEncodingException, IOException {
        char[] buffer = new char[(bufferSize == null ? 1024 : bufferSize)];
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream, encoding);
        int counter;
        while ((counter = reader.read(buffer)) > 0) {
            stringBuilder.append(buffer, 0, counter);
        }
        return stringBuilder.toString();
    }

    public static String getCurrentMethodName(Object object) {
        return object.getClass().getEnclosingMethod().getName();
    }

    public static String exceptionStacktraceToString(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

    public static void addToErrors(JSONArray errors, String name, Exception exception) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("name", name);
        errorJson.put("msg", exception.getMessage());
        errorJson.put("verbose", exceptionStacktraceToString(exception));
        errors.put(errorJson);
    }

    public static void addErrorToErrors(JSONArray errors, String name, String msg, String verbose) {
        JSONObject errorJson = new JSONObject();
        errorJson.put("name", name);
        errorJson.put("msg", msg);
        errorJson.put("verbose", verbose);
        errors.put(errorJson);
    }
    
    public static JSONObject getPostObject(HttpServletRequest request, JSONArray errors) throws Exception {
        JSONObject postObject = new JSONObject();
        StringBuilder jb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            postObject = new JSONObject(jb.toString());
        } catch (Exception e) {
            addToErrors(errors, getCurrentMethodName(new Object() {
            }), e);
        }
        return postObject;
    }
    
}
