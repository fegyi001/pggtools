package printtest;

import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import pggtools.print.MapfishPrintTools;

public class PrintTests {

    private String url = "http://188.166.116.137:8080/print-servlet";
    private String encoding = "UTF-8";
    private String version = "2.0-SNAPSHOT";
    private String ekozmuUrl = "http://ekozmu-eles-geoserver.e-epites.hu:8080/ekozmu-print-servlet";
    private JSONObject ekozmuObject = new JSONObject(
            ""+/**~{*/""
	    + "{"
     + "\r\n       \"overviewLayers\": [{"
     + "\r\n           \"type\": \"Tms\","
     + "\r\n           \"opacity\": 1,"
     + "\r\n           \"baseURL\": \"http://geoxuj.lechnerkozpont.hu:8080/geoserver/gwc/service/tms\","
     + "\r\n           \"customParams\": {},"
     + "\r\n           \"singleTile\": false,"
     + "\r\n           \"maxExtent\": [421306.58134742436, 43986.223614953604, 949684.4366195255, 366630.91008161334],"
     + "\r\n           \"tileSize\": [256, 256],"
     + "\r\n           \"resolutions\": [1120, 560, 280, 140, 55.99999999999999, 27.999999999999996, 13.999999999999998, 6.999999999999999, 2.8, 1.4, 0.5599999999999999, 0.27999999999999997, 0.13999999999999999, 0.06999999999999999, 0.027999999999999997],"
     + "\r\n           \"tileOrigin\": {"
     + "\r\n               \"x\": 421306.58134742436,"
     + "\r\n               \"y\": 617426.2236149536"
     + "\r\n           },"
     + "\r\n           \"format\": \"png\","
     + "\r\n           \"layer\": \"osmWsp:osm_hungary_gray@LTK_EOV\""
     + "\r\n       }],"
     + "\r\n       \"mapScale\": 3000000,"
     + "\r\n       \"outputFormat\": \"pdf\","
     + "\r\n       \"mapComment\": \"\","
     + "\r\n       \"layers\": [{"
     + "\r\n           \"type\": \"Tms\","
     + "\r\n           \"opacity\": 1,"
     + "\r\n           \"baseURL\": \"http://geoxuj.lechnerkozpont.hu:8080/geoserver/gwc/service/tms\","
     + "\r\n           \"customParams\": {},"
     + "\r\n           \"singleTile\": false,"
     + "\r\n           \"maxExtent\": [421306.58134742436, 43986.223614953604, 949684.4366195255, 366630.91008161334],"
     + "\r\n           \"tileSize\": [256, 256],"
     + "\r\n           \"resolutions\": [1120, 560, 280, 140, 55.99999999999999, 27.999999999999996, 13.999999999999998, 6.999999999999999, 2.8, 1.4, 0.5599999999999999, 0.27999999999999997, 0.13999999999999999, 0.06999999999999999, 0.027999999999999997],"
     + "\r\n           \"tileOrigin\": {"
     + "\r\n               \"x\": 421306.58134742436,"
     + "\r\n               \"y\": 617426.2236149536"
     + "\r\n           },"
     + "\r\n           \"format\": \"png\","
     + "\r\n           \"layer\": \"osmWsp:osm_hungary_gray@LTK_EOV\""
     + "\r\n       }],"
     + "\r\n       \"pages\": [{"
     + "\r\n           \"center\": [688971, 203995],"
     + "\r\n           \"scale\": 3000000,"
     + "\r\n           \"rotation\": 0,"
     + "\r\n           \"mapTitle\": \"\","
     + "\r\n           \"mapComment\": \"\""
     + "\r\n       }],"
     + "\r\n       \"srs\": \"EPSG:23700\","
     + "\r\n       \"units\": \"m\","
     + "\r\n       \"mapFooter\": \"\","
     + "\r\n       \"layout\": \"A4 fekvő\","
     + "\r\n       \"dpi\": \"100\","
     + "\r\n       \"mapTitle\": \"\","
     + "\r\n       \"extent\": \"319371,-388485,1058571,796475\","
     + "\r\n       \"centerExtent\": \"688971,203995,688971,203995\""
     + "\r\n   }"
	+ "\r\n"/**}*/
    );

    @Test
    public void dummyTest() {
        Assert.assertTrue(true);
    }

    // @Test
    public void printInfoTest() {
        try {
            MapfishPrintTools mpt = new MapfishPrintTools(url, version, encoding);
            System.out.println(mpt.getPrintInfo().getPrintURL());
            Assert.assertTrue(mpt.getPrintInfo().getInfo() != null);
            Assert.assertTrue(mpt.getErrors().length() == 0);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

    // @Test
    public void printCreateSimpleTest() {
        JSONArray errors = new JSONArray();
        try {
            MapfishPrintTools mpt = new MapfishPrintTools(url, version, encoding);
            JSONObject params = mpt.getPrintCreate().getPostTemplate(errors);
            System.out.println(params);
            mpt.getPrintCreate().consumeParams(params, errors);
            mpt.getPrintCreate().print(false, errors);
            if (errors.length() == 0) {
                System.out.println(mpt.getPrintCreate().getResponseUrl());
                UrlValidator urlValidator = new UrlValidator();
                Assert.assertTrue(urlValidator.isValid(mpt.getPrintCreate().getResponseUrl()));
            } else {
                System.out.println(errors);
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

//    @Test
    public void printCreateSimpleEkozmuTest() {
        JSONArray errors = new JSONArray();
        try {
            MapfishPrintTools mpt = new MapfishPrintTools(ekozmuUrl, version, encoding);
            JSONObject params = ekozmuObject;
            System.out.println(params);
            mpt.getPrintCreate().consumeParams(params, errors);
            mpt.getPrintCreate().print(false, errors);
            if (errors.length() == 0) {
                System.out.println(mpt.getPrintCreate().getResponseUrl());
                UrlValidator urlValidator = new UrlValidator();
                Assert.assertTrue(urlValidator.isValid(mpt.getPrintCreate().getResponseUrl()));
            } else {
                System.out.println(errors);
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    // @Test
    public void printCreateExtendToFeaturesTest() {
        JSONArray errors = new JSONArray();
        try {
            MapfishPrintTools mpt = new MapfishPrintTools(url, version, encoding);
            // JSONObject params = new JSONObject(new String(
            // Files.readAllBytes(Paths.get(this.getClass().getResource("postprinttemplate.json").toURI()))));
            JSONObject params = mpt.getPrintCreate().getPostTemplate(errors);
            mpt.getPrintCreate().consumeParams(params, errors);
            mpt.getPrintCreate().print(true, errors);
            if (errors.length() == 0) {
                System.out.println(mpt.getPrintCreate().getResponseUrl());
                UrlValidator urlValidator = new UrlValidator();
                Assert.assertTrue(urlValidator.isValid(mpt.getPrintCreate().getResponseUrl()));
            } else {
                System.out.println(errors);
                Assert.assertTrue(false);
            }
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

}
