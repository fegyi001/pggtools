package pggtools.print;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import pggtools.tools.PggAtool;

public class PrintInfo {

    /*
     * PARAMETERS
     */
    private String url;
    private String version;
    private String encoding;
    
    private JSONObject info;

    private String createURL;
    private String printURL;
    private JSONArray outputFormats;
    private JSONArray scales;
    private JSONArray dpis;
    private JSONArray layouts;

    /*
     * CONSTRUCTORS
     */
    public PrintInfo(String url, String version, String encoding) {
        this.url = url;
        this.version = version;
        this.encoding = encoding;
    }

    public PrintInfo(JSONObject obj) {
        this.info = obj;
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
            setInfo(obj);
            setCreateURL(obj.getString("createURL"));
            setPrintURL(obj.getString("printURL"));
            setOutputFormats(obj.getJSONArray("outputFormats"));
            setScales(obj.getJSONArray("scales"));
            setDpis(obj.getJSONArray("dpis"));
            setLayouts(obj.getJSONArray("layouts"));
        } catch (Exception e) {
            PggAtool.addToErrors(errors, PggAtool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /**
     * Requests the printInfo from the print-servlet
     * 
     * @param errors
     * @throws Exception
     */
    public void requestPrintInfo(JSONArray errors) throws Exception {
        try {
            // check parameter(s)
            if (getUrl() == null) {
                PggAtool.addErrorToErrors(errors, PggAtool.getCurrentMethodName(new Object() {
                }), "parameter", "the parameter 'url' is missing");
            }
            if (errors.length() == 0) {
                // handle different versions
                switch (getVersion()) {
                case "2.0-SNAPSHOT":
                    // request the printInfo
                    String requestStr = getUrl() + "/pdf/info.json";
                    URL url = new URL(requestStr);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    // check the response code, it should be 200 (no errors)
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream is = urlConnection.getInputStream();
                        consumeRequestResults(new JSONObject(PggAtool.readString(is, getEncoding(), null)), errors);
                    } else {
                        PggAtool.addErrorToErrors(errors, PggAtool.getCurrentMethodName(new Object() {
                        }), "URL error",
                                "the following url returned an error code of " + responseCode + ": " + requestStr);
                    }
                    break;
                default:
                    PggAtool.addErrorToErrors(errors, PggAtool.getCurrentMethodName(new Object() {
                    }), "version", "the version '" + getVersion() + "' is currently unsupported");
                    break;
                }
            }
        } catch (Exception e) {
            PggAtool.addToErrors(errors, PggAtool.getCurrentMethodName(new Object() {
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
    private void setCreateURL(String createURL) {
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
    private void setPrintURL(String printURL) {
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
    private void setOutputFormats(JSONArray outputFormats) {
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
    private void setScales(JSONArray scales) {
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
    private void setDpis(JSONArray dpis) {
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
    private void setLayouts(JSONArray layouts) {
        this.layouts = layouts;
    }

    /**
     * @return the info
     */
    public JSONObject getInfo() {
        return info;
    }

    /**
     * @param info
     *            the info to set
     */
    private void setInfo(JSONObject info) {
        this.info = info;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    @SuppressWarnings("unused")
    private void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    @SuppressWarnings("unused")
    private void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    @SuppressWarnings("unused")
    private void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
