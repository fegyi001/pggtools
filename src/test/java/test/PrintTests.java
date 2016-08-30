package test;

import org.junit.Assert;
import org.junit.Test;

import pggtools.print.MapfishPrintTools;

public class PrintTests {

    private String url = "http://172.24.0.171:8080/ekozmu-print-servlet";
    
    @Test
    public void printInfoTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools();
            mpt.setUrl(url);
            mpt.requestPrintInfo();
            Assert.assertTrue(mpt.getPrintInfo().getJsonObject() != null);
            Assert.assertTrue(mpt.getErrors().length() == 0);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
    
    @Test
    public void printCreateTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools();
            mpt.setUrl(url);
            mpt.requestPrintInfo();
            
            //TODO
            Assert.assertTrue(true);
            
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
    
}
