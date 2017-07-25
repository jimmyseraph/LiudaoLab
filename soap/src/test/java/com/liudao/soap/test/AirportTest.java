package com.liudao.soap.test;

import java.net.URL;

import javax.xml.soap.SOAPMessage;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.liudao.soap.tool.SoapUtils;
import com.liudao.soap.tool.TestModule;

import static org.testng.Assert.*;

public class AirportTest extends TestModule {
	
	@DataProvider(name="airportData")
	public Object[][] getData(){
		return new Object[][] {
			{"china","<AirportCode>LUM</AirportCode>"},
			{"japan","<AirportCode>MBE</AirportCode>"}
		};
	}
	
	@Test(dataProvider="airportData")
	public void testGetAirportInformationByCountry(String city,String expected) {
		SOAPMessage response = null;
		URL url = null;
		try {
			url = new URL("http://www.webservicex.net/airport.asmx");
			response = getResponse(
					"airport.xml", 
					url, 
					"http://www.webserviceX.NET/GetAirportInformationByCountry", 
					city);
			String actual = SoapUtils.xml2String(response);
			assertTrue(actual.contains(expected));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
