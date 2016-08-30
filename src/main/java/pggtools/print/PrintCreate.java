package pggtools.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import pggtools.tools.Atool;

public class PrintCreate {

    /*
     * PARAMETERS
     */

    private JSONObject params;
    private JSONObject defaultParams = new JSONObject();
    // there can be a bunch of other parameters that only the user knows
    private JSONObject customParams = new JSONObject();
    String responseUrl;

    private Integer mapScale;
    private String outputFormat;
    private String srs;
    private String units;
    private Integer dpi;
    private String layout;
    private JSONArray layers;
    private JSONArray overviewLayers;
    private JSONArray pages;

    /*
     * CONSTRUCTOR
     */

    PrintCreate() {

    }

    /*
     * UTILS
     */

    /**
     * Parses a JSONObject into print parameters
     * 
     * @param params
     * @param errors
     * @throws Exception
     */
    public void consumeParams(JSONObject params, JSONArray errors) throws Exception {
        setParams(params);
        try {
            @SuppressWarnings("unchecked")
            Iterator<String> keys = params.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                switch (key) {
                case "mapScale":
                    setMapScale(params.getInt(key));
                    break;
                case "outputFormat":
                    setOutputFormat(params.getString(key));
                    break;
                case "srs":
                    setSrs(params.getString(key));
                    break;
                case "units":
                    setUnits(params.getString(key));
                    break;
                case "dpi":
                    setDpi(params.getInt(key));
                    break;
                case "layout":
                    setLayout(params.getString(key));
                    break;
                case "layers":
                    setLayers(params.getJSONArray(key));
                    break;
                case "overviewLayers":
                    setOverviewLayers(params.getJSONArray(key));
                    break;
                case "pages":
                    setPages(params.getJSONArray(key));
                    break;
                default:
                    getCustomParams().put(key, params.getString(key));
                    break;
                }
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /**
     * Performs the url request to the MapFish print-servlet
     * 
     * @param urlString
     * @param version
     * @param encoding
     * @param extendToFeatures:
     *            if set to true and if there are vector features then the map
     *            extent and scale will adjust to it
     * @param errors
     * @throws Exception
     */
    public void requestPrintCreate(String urlString, String version, String encoding, boolean extendToFeatures,
            JSONArray errors) throws Exception {
        try {
            if (urlString == null) {
                Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                }), "parameter", "the parameter 'url' is missing");
            }
            if (errors.length() == 0) {
                // handle different versions
                switch (version) {
                case "2.0-SNAPSHOT":
                    String requestStr = urlString + "/pdf/create.json";
                    URL url = new URL(requestStr);
                    final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    try (OutputStream po = urlConnection.getOutputStream()) {
                        po.write(getParams().toString().getBytes(encoding));
                        po.flush();
                    } catch (IOException e) {
                        Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                        }), "post print", e.getMessage());
                    }
                    // válasz beolvasás
                    final InputStream is = urlConnection.getInputStream();
                    JSONObject responseObject = new JSONObject(Atool.readString(is, encoding, null));
                    setResponseUrl(responseObject.getString("getURL"));
                    break;
                default:
                    Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                    }), "version", "the version '" + version + "' is currently unsupported");
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
     * @return the params
     */
    public JSONObject getParams() {
        return params;
    }

    /**
     * @param params
     *            the params to set
     */
    public void setParams(JSONObject params) {
        this.params = params;
    }

    /**
     * @return the mapScale
     */
    public Integer getMapScale() {
        return mapScale;
    }

    /**
     * @param mapScale
     *            the mapScale to set
     */
    public void setMapScale(Integer mapScale) {
        this.mapScale = mapScale;
    }

    /**
     * @return the outputFormat
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat
     *            the outputFormat to set
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @return the overviewLayers
     */
    public JSONArray getOverviewLayers() {
        return overviewLayers;
    }

    /**
     * @param overviewLayers
     *            the overviewLayers to set
     */
    public void setOverviewLayers(JSONArray overviewLayers) {
        this.overviewLayers = overviewLayers;
    }

    /**
     * @return the layers
     */
    public JSONArray getLayers() {
        return layers;
    }

    /**
     * @param layers
     *            the layers to set
     */
    public void setLayers(JSONArray layers) {
        this.layers = layers;
    }

    /**
     * @return the pages
     */
    public JSONArray getPages() {
        return pages;
    }

    /**
     * @param pages
     *            the pages to set
     */
    public void setPages(JSONArray pages) {
        this.pages = pages;
    }

    /**
     * @return the srs
     */
    public String getSrs() {
        return srs;
    }

    /**
     * @param srs
     *            the srs to set
     */
    public void setSrs(String srs) {
        this.srs = srs;
    }

    /**
     * @return the units
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units
     *            the units to set
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * @return the dpi
     */
    public Integer getDpi() {
        return dpi;
    }

    /**
     * @param dpi
     *            the dpi to set
     */
    public void setDpi(Integer dpi) {
        this.dpi = dpi;
    }

    // /**
    // * @return the customParams
    // */
    // public Map<String, String> getCustomParams() {
    // return customParams;
    // }
    //
    // /**
    // * @param customParams
    // * the customParams to set
    // */
    // public void setCustomParams(Map<String, String> customParams) {
    // this.customParams = customParams;
    // }

    /**
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

    /**
     * @param layout
     *            the layout to set
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * @return the customParams
     */
    public JSONObject getCustomParams() {
        return customParams;
    }

    /**
     * @param customParams
     *            the customParams to set
     */
    public void setCustomParams(JSONObject customParams) {
        this.customParams = customParams;
    }

    /**
     * @return the defaultParams
     */
    public JSONObject getDefaultParams() {
        return defaultParams;
    }

    /**
     * @param defaultParams
     *            the defaultParams to set
     */
    public void setDefaultParams(JSONObject defaultParams) {
        this.defaultParams = defaultParams;
    }

    /**
     * @return the responseUrl
     */
    public String getResponseUrl() {
        return responseUrl;
    }

    /**
     * @param responseUrl the responseUrl to set
     */
    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

}
