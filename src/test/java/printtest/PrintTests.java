package printtest;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import pggtools.print.MapfishPrintTools;

public class PrintTests {

    private String url = "http://188.166.116.137:8080/print-servlet";

    @Test
    public void dummyTest() {
        Assert.assertTrue(true);
    }

//    @Test
    public void printInfoTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools();
            mpt.setUrl(url);
            mpt.requestPrintInfo();
            System.out.println(mpt.getPrintInfo().getJsonObject());
            Assert.assertTrue(mpt.getPrintInfo().getJsonObject() != null);
            Assert.assertTrue(mpt.getErrors().length() == 0);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

//    @Test
    public void printSimpleCreateTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools();
            mpt.setUrl(url);
            JSONObject params = new JSONObject(new String(
                    Files.readAllBytes(Paths.get(this.getClass().getResource("postprinttemplate.json").toURI()))));
            mpt.requestPrintCreate(params, false);
            UrlValidator urlValidator = new UrlValidator();
            System.out.println(mpt.getPrintCreate().getResponseUrl());
            Assert.assertTrue(urlValidator.isValid(mpt.getPrintCreate().getResponseUrl()));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
    
    @Test
    public void printExtendToFeaturesCreateTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools();
            mpt.setUrl(url);
            mpt.requestPrintInfo();
            JSONObject params = new JSONObject(new String(
                    Files.readAllBytes(Paths.get(this.getClass().getResource("postprinttemplate.json").toURI()))));
            mpt.requestPrintCreate(params, true);
            UrlValidator urlValidator = new UrlValidator();
            System.out.println(mpt.getPrintCreate().getResponseUrl());
//            Assert.assertTrue(urlValidator.isValid(mpt.getPrintCreate().getResponseUrl()));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
