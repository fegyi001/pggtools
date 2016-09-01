package pggtools.print;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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

    private PrintInfo printInfo;

    private Integer mapScale;
    private String outputFormat;
    private String srs;
    private String units;
    private Integer dpi;
    private String layout;
    private JSONArray layers;
    private JSONArray overviewLayers;
    private JSONArray pages;

    private Envelope envelope;

    /*
     * CONSTRUCTOR
     */

    PrintCreate(PrintInfo pi) {
        this.setPrintInfo(pi);
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
    private void consumeParams(JSONObject params, JSONArray errors) throws Exception {
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

    public void print(JSONObject params, boolean extendToFeatures, JSONArray errors) throws Exception {
        try {
            consumeParams(params, errors);
            if (errors.length() == 0) {
                requestPrintCreate(extendToFeatures, errors);
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
    private void requestPrintCreate(boolean extendToFeatures, JSONArray errors) throws Exception {
        try {
            if (getPrintInfo().getUrl() == null) {
                Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                }), "parameter", "the parameter 'url' is missing");
            }
            if (errors.length() == 0) {
                if (extendToFeatures) {
                    handleExtendToFeatures(errors);
                }
                // handle different versions
                switch (getPrintInfo().getVersion()) {
                case "2.0-SNAPSHOT":
                    String requestStr = getPrintInfo().getUrl() + "/pdf/create.json";
                    URL url = new URL(requestStr);
                    final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    try (OutputStream po = urlConnection.getOutputStream()) {
                        po.write(getParams().toString().getBytes(getPrintInfo().getEncoding()));
                        po.flush();
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode == 200) {
                            final InputStream is = urlConnection.getInputStream();
                            JSONObject responseObject = new JSONObject(
                                    Atool.readString(is, getPrintInfo().getEncoding(), null));
                            setResponseUrl(responseObject.getString("getURL"));
                        } else {
                            Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                            }), "URL error",
                                    "the following url returned an error code of " + responseCode + ": " + requestStr);
                        }
                    } catch (IOException e) {
                        Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                        }), "post print", e.getMessage());
                    }
                    break;
                default:
                    Atool.addErrorToErrors(errors, Atool.getCurrentMethodName(new Object() {
                    }), "version", "the version '" + getPrintInfo().getVersion() + "' is currently unsupported");
                    break;
                }
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /**
     * Handles the case when the user wishes to extend the print area to all the
     * vector features
     * 
     * @param urlString
     * @param version
     * @param encoding
     * @param errors
     * @throws Exception
     */
    private void handleExtendToFeatures(JSONArray errors) throws Exception {
        try {
            // the maximum extent that the print should cover based on the
            // features
            Envelope printArea = getPrintArea(errors);
            // set the print center to the center of the envelope
            JSONArray printCenter = new JSONArray();
            Coordinate center = new Coordinate((printArea.getMinX() + printArea.getMaxX()) / 2,
                    (printArea.getMinY() + printArea.getMaxY()) / 2);
            printCenter.put(center.x);
            printCenter.put(center.y);
            // TODO: handle multiple pages
            getPages().getJSONObject(0).put("center", printCenter);
            // find the best scale
            Double printScale = getPrintScale(printArea, center, errors);
            setMapScale(printScale.intValue());
            getParams().put("mapScale", printScale.intValue());
            getPages().getJSONObject(0).put("scale", printScale.intValue());
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /**
     * Gets the bounding area of the vector features
     * 
     * @param errors
     * @return
     * @throws Exception
     */
    private Envelope getPrintArea(JSONArray errors) throws Exception {
        Envelope envelope = new Envelope();
        try {
            // an ordered set for x and y coordinates
            SortedSet<Double> xSet = new TreeSet<>();
            SortedSet<Double> ySet = new TreeSet<>();
            for (int i = 0; i < getLayers().length(); i++) {
                JSONObject layer = getLayers().getJSONObject(i);
                if (layer.has("geoJson")) {
                    for (int f = 0; f < layer.getJSONObject("geoJson").getJSONArray("features").length(); f++) {
                        JSONObject feature = layer.getJSONObject("geoJson").getJSONArray("features").getJSONObject(f);
                        JSONObject geometry = feature.getJSONObject("geometry");
                        JSONArray coordinates = geometry.getJSONArray("coordinates");
                        String type = geometry.getString("type").toLowerCase();
                        // until now only multipolygon is supported
                        // TODO: add more geometry types
                        switch (type) {
                        case "multipolygon":
                            JSONArray polygons = coordinates.getJSONArray(0);
                            for (int p = 0; p < polygons.length(); p++) {
                                for (int c = 0; c < polygons.getJSONArray(p).length(); c++) {
                                    JSONArray coordinate = polygons.getJSONArray(p).getJSONArray(c);
                                    Coordinate coord = new Coordinate(coordinate.getDouble(0), coordinate.getDouble(1));
                                    xSet.add(coord.x);
                                    ySet.add(coord.y);
                                }
                            }
                        default:
                            break;
                        }
                    }
                }
            }
            // create an envelope of the minimum and maximum X,Y values
            envelope = new Envelope(xSet.first(), xSet.last(), ySet.first(), ySet.last());
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return envelope;
    }

    /**
     * Gets the optimal scale for the vector features
     * 
     * @param printArea
     * @param center
     * @param errors
     * @return
     * @throws Exception
     */
    private Double getPrintScale(Envelope printArea, Coordinate center, JSONArray errors) throws Exception {
        Double scale = null;
        try {
            // the selected layout's parameters (width, height in millimeters)
            JSONObject layoutParams = getLayoutParams(errors);
            Double mapWidth = Double.valueOf(layoutParams.getJSONObject("map").getInt("width"));
            Double mapHeight = Double.valueOf(layoutParams.getJSONObject("map").getInt("height"));
            Double width = getLayoutDimensionInMillimeters(mapWidth, getDpi(), errors);
            Double height = getLayoutDimensionInMillimeters(mapHeight, getDpi(), errors);
            // we have to start from the biggest zoom (1:smallest) and
            // repeatedly go to the next one
            SortedSet<Double> scaleSet = new TreeSet<>();
            for (int i = 0; i < getPrintInfo().getScales().length(); i++) {
                scaleSet.add(Double.valueOf(getPrintInfo().getScales().getJSONObject(i).getString("value")));
            }
            for (Double sc : scaleSet) {
                if (isScaleSufficient(sc, printArea, center, width, height, errors)) {
                    scale = sc;
                    break;
                }
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return scale;
    }

    /**
     * Gets a layout JSONObject based on a given layout string
     * 
     * @param errors
     * @return
     * @throws Exception
     */
    private JSONObject getLayoutParams(JSONArray errors) throws Exception {
        JSONObject layoutParams = new JSONObject();
        try {
            for (int i = 0; i < getPrintInfo().getLayouts().length(); i++) {
                JSONObject layout = getPrintInfo().getLayouts().getJSONObject(i);
                if (layout.getString("name").equals(getLayout())) {
                    layoutParams = layout;
                    break;
                }
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return layoutParams;
    }

    /**
     * Converts pdf units to millimeters 72 points == 1 inch == 25.4 mm
     * 
     * @param dimension
     * @param dpi
     * @param errors
     * @return
     * @throws Exception
     */
    private Double getLayoutDimensionInMillimeters(Double dimension, Integer dpi, JSONArray errors) throws Exception {
        Double value = null;
        try {
            Double multiplier = (25.4 / 72.0);
            value = dimension * multiplier;
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return value;
    }

    /**
     * Tells whether a given scale covers all the required area (defined by
     * vector features)
     * 
     * @param scale
     * @param printArea
     * @param center
     * @param width
     * @param height
     * @param errors
     * @return
     * @throws Exception
     */
    private boolean isScaleSufficient(Double scale, Envelope printArea, Coordinate center, Double width, Double height,
            JSONArray errors) throws Exception {
        boolean isSufficient = false;
        try {
            Double mapMinX = center.x - ((width * scale / 1000) / 2);
            Double mapMinY = center.y - ((height * scale / 1000) / 2);
            Double mapMaxX = center.x + ((width * scale / 1000) / 2);
            Double mapMaxY = center.y + ((height * scale / 1000) / 2);
            Envelope mapArea = new Envelope(mapMinX, mapMaxX, mapMinY, mapMaxY);
            if (mapArea.contains(printArea)) {
                isSufficient = true;
            }
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return isSufficient;
    }

    /**
     * Creates a template JSONObject from a template json file
     * 
     * @param errors
     * @return
     * @throws Exception
     */
    public JSONObject getPostTemplate(JSONArray errors) throws Exception {
        JSONObject template = new JSONObject();
        try {
            template = new JSONObject(new String(
                    Files.readAllBytes(Paths.get(this.getClass().getResource("postprinttemplate.json").toURI()))));
        } catch (Exception e) {
            Atool.addToErrors(errors, Atool.getCurrentMethodName(new Object() {
            }), e);
        }
        return template;
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
    private void setParams(JSONObject params) {
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
    private void setMapScale(Integer mapScale) {
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
    private void setOutputFormat(String outputFormat) {
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
    private void setOverviewLayers(JSONArray overviewLayers) {
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
    private void setLayers(JSONArray layers) {
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
    private void setPages(JSONArray pages) {
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
    private void setSrs(String srs) {
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
    private void setUnits(String units) {
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
    private void setDpi(Integer dpi) {
        this.dpi = dpi;
    }

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
    private void setLayout(String layout) {
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
    @SuppressWarnings("unused")
    private void setCustomParams(JSONObject customParams) {
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
    @SuppressWarnings("unused")
    private void setDefaultParams(JSONObject defaultParams) {
        this.defaultParams = defaultParams;
    }

    /**
     * @return the responseUrl
     */
    public String getResponseUrl() {
        return responseUrl;
    }

    /**
     * @param responseUrl
     *            the responseUrl to set
     */
    private void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }

    /**
     * @return the envelope
     */
    public Envelope getEnvelope() {
        return envelope;
    }

    /**
     * @param envelope
     *            the envelope to set
     */
    @SuppressWarnings("unused")
    private void setEnvelope(Envelope envelope) {
        this.envelope = envelope;
    }

    /**
     * @return the printInfo
     */
    public PrintInfo getPrintInfo() {
        return printInfo;
    }

    /**
     * @param printInfo
     *            the printInfo to set
     */
    private void setPrintInfo(PrintInfo printInfo) {
        this.printInfo = printInfo;
    }

}
