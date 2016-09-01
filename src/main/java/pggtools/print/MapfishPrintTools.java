package pggtools.print;

import org.json.JSONArray;

import pggtools.tools.PggAtool;

public class MapfishPrintTools {

    /*
     * PARAMETERS
     */
    private String url;
    private String version = "2.0-SNAPSHOT";
    private String encoding = "UTF-8";

    private JSONArray errors = new JSONArray();

    private PrintInfo printInfo;
    private PrintCreate printCreate;
    private PrintOut printOut;

    /*
     * CONSTRUCTORS
     */
    public MapfishPrintTools(String url, String version, String encoding) throws Exception {
        setUrl(url);
        if (version != null) {
            setVersion(version);
        }
        if (encoding != null) {
            setEncoding(encoding);
        }
        init();
    }

    /*
     * UTILS
     */

    /**
     * Initializes the printtool by creating PrintInfo and PrintCreate
     * 
     * @throws Exception
     */
    private void init() throws Exception {
        try {
            // initialize the PrintInfo
            PrintInfo pi = new PrintInfo(getUrl(), getVersion(), getEncoding());
            setPrintInfo(pi);
            pi.requestPrintInfo(getErrors());
            // initialize the PrintCreate
            PrintCreate pc = new PrintCreate(pi);
            setPrintCreate(pc);
        } catch (Exception e) {
            PggAtool.addToErrors(getErrors(), PggAtool.getCurrentMethodName(new Object() {
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
    private void setVersion(String version) {
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
    private void setUrl(String url) {
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
    private void setEncoding(String encoding) {
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
    private void setPrintInfo(PrintInfo printInfo) {
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
    @SuppressWarnings("unused")
    private void setErrors(JSONArray errors) {
        this.errors = errors;
    }

    /**
     * @return the printCreate
     */
    public PrintCreate getPrintCreate() {
        return printCreate;
    }

    /**
     * @param printCreate
     *            the printCreate to set
     */
    private void setPrintCreate(PrintCreate printCreate) {
        this.printCreate = printCreate;
    }

    /**
     * @return the printOut
     */
    public PrintOut getPrintOut() {
        return printOut;
    }

    /**
     * @param printOut
     *            the printOut to set
     */
    @SuppressWarnings("unused")
    private void setPrintOut(PrintOut printOut) {
        this.printOut = printOut;
    }

}
