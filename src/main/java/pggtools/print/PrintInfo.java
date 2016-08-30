package pggtools.print;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import pggtools.tools.Atool;

public class PrintInfo {

    /*
     * PARAMETERS
     */

    private JSONObject jsonObject;

    private String createURL;
    private String printURL;
    private JSONArray outputFormats;
    private JSONArray scales;
    private JSONArray dpis;
    private JSONArray layouts;

    /*
     * CONSTRUCTORS
     */
    PrintInfo() {

    }

    PrintInfo(JSONObject obj) {
        this.jsonObject = obj;
        this.createURL = obj.getString("createURL");
        this.printURL = obj.getString("printURL");
        this.outputFormats = obj.getJSONArray("outputFormats");
        this.scales = obj.getJSONArray("scales");
        this.dpis = obj.getJSONArray("dpis");
        this.layouts = obj.getJSONArray("layouts");
    }

    /*
     * UTILS
     */

    /**
     * Parses a printInfo jsonObject for the current PrintInfo object
     * 
     * @param obj
     * @param errors
     * @throws Exception
     */
    public void consumeRequestResults(JSONObject obj, JSONArray errors) throws Exception {
        try {
            setJsonObject(obj);
            setCreateURL(obj.getString("createURL"));
            setPrintURL(obj.getString("printURL"));
            setOutputFormats(obj.getJSONArray("outputFormats"));
            setScales(obj.getJSONArray("scales"));
            setDpis(obj.getJSONArray("dpis"));
            setLayouts(obj.getJSONArray("layouts"));
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /**
     * Requests the printInfo from the print-servlet
     * 
     * @param urlString
     * @param version
     * @param encoding
     * @param errors
     * @throws Exception
     */
    public void requestPrintInfo(String urlString, String version, String encoding, JSONArray errors) throws Exception {
        try {
            // check parameter(s)
            if (urlString == null) {
                Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                }), "parameter", "the parameter 'url' is missing");
            }
            if (errors.length() == 0) {
                // handle different versions
                switch (version) {
                case "2.0-SNAPSHOT":
                    // request the printInfo
                    String requestStr = urlString + "/pdf/info.json";
                    URL url = new URL(requestStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    InputStream is = urlConnection.getInputStream();
                    consumeRequestResults(new JSONObject(Atool.readString(is, encoding, null)), errors);
                    break;
                default:
                    Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                    }), "version", "the version '" + version + "' is unsupported");
                    break;
                }
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    /**
     * @return the createURL
     */
    public String getCreateURL() {
        return createURL;
    }

    /**
     * @param createURL
     *            the createURL to set
     */
    public void setCreateURL(String createURL) {
        this.createURL = createURL;
    }

    /**
     * @return the printURL
     */
    public String getPrintURL() {
        return printURL;
    }

    /**
     * @param printURL
     *            the printURL to set
     */
    public void setPrintURL(String printURL) {
        this.printURL = printURL;
    }

    /**
     * @return the outputFormats
     */
    public JSONArray getOutputFormats() {
        return outputFormats;
    }

    /**
     * @param outputFormats
     *            the outputFormats to set
     */
    public void setOutputFormats(JSONArray outputFormats) {
        this.outputFormats = outputFormats;
    }

    /**
     * @return the scales
     */
    public JSONArray getScales() {
        return scales;
    }

    /**
     * @param scales
     *            the scales to set
     */
    public void setScales(JSONArray scales) {
        this.scales = scales;
    }

    /**
     * @return the dpis
     */
    public JSONArray getDpis() {
        return dpis;
    }

    /**
     * @param dpis
     *            the dpis to set
     */
    public void setDpis(JSONArray dpis) {
        this.dpis = dpis;
    }

    /**
     * @return the layouts
     */
    public JSONArray getLayouts() {
        return layouts;
    }

    /**
     * @param layouts
     *            the layouts to set
     */
    public void setLayouts(JSONArray layouts) {
        this.layouts = layouts;
    }

    /**
     * @return the jsonObject
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * @param jsonObject
     *            the jsonObject to set
     */
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

}
