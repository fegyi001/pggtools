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

	@Test
	public void dummyTest() {
		Assert.assertTrue(true);
	}

	@Test
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

	@Test
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

	@Test
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
