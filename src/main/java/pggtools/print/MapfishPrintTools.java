package pggtools.print;

import org.json.JSONArray;
import org.json.JSONObject;

import pggtools.tools.Atool;

public class MapfishPrintTools {

    /*
     * PARAMETERS
     */
    private String url;
    private String version = "2.0-SNAPSHOT";
    private String encoding = "UTF-8";

    private PrintInfo printInfo;
    private PrintCreate printCreate;
    private PrintOut printOut;
    private JSONArray errors;

    /*
     * CONSTRUCTORS
     */
    public MapfishPrintTools() {

    }

    public MapfishPrintTools(String url, String version, String encoding) {
        this.url = url;
        this.version = version;
        this.encoding = encoding;
    }

    /*
     * UTILS
     */

    /**
     * Requests the printInfo from the print-servlet
     * 
     * @return
     * @throws Exception
     */
    public void requestPrintInfo() throws Exception {
        // set errors to empty
        setErrors(new JSONArray());
        try {
            PrintInfo pi = new PrintInfo();
            pi.requestPrintInfo(getUrl(), getVersion(), getEncoding(), getErrors());
            setPrintInfo(pi);
        } catch (Exception e) {
            Atool.addToErrors(getErrors(), Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }
    
    public void requestPrintCreate(JSONObject params, boolean extendToFeatures) throws Exception {
        setErrors(new JSONArray());
        try {
            PrintInfo pi = new PrintInfo();
            pi.requestPrintInfo(getUrl(), getVersion(), getEncoding(), getErrors());
            setPrintInfo(pi);
            PrintCreate pc = new PrintCreate();
            pc.consumeParams(params, errors);
            pc.requestPrintCreate(getUrl(), getVersion(), getEncoding(), extendToFeatures, getErrors());
            setPrintCreate(pc);
        } catch (Exception e) {
            Atool.addToErrors(getErrors(), Atool.getCurrentMethodName(new Object() {
            }), e);
        }
    }

    /*
     * GETTERS AND SETTERS
     */

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding
     *            the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
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
    public void setPrintInfo(PrintInfo printInfo) {
        this.printInfo = printInfo;
    }

    /**
     * @return the errors
     */
    public JSONArray getErrors() {
        return errors;
    }

    /**
     * @param errors
     *            the errors to set
     */
    public void setErrors(JSONArray errors) {
        this.errors = errors;
    }

    /**
     * @return the printCreate
     */
    public PrintCreate getPrintCreate() {
        return printCreate;
    }

    /**
     * @param printCreate the printCreate to set
     */
    public void setPrintCreate(PrintCreate printCreate) {
        this.printCreate = printCreate;
    }

    /**
     * @return the printOut
     */
    public PrintOut getPrintOut() {
        return printOut;
    }

    /**
     * @param printOut the printOut to set
     */
    public void setPrintOut(PrintOut printOut) {
        this.printOut = printOut;
    }

}
