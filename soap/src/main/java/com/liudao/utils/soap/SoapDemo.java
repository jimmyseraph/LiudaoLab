package com.liudao.utils.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import static javax.xml.soap.SOAPConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SoapDemo {

	public static void main(String[] args) {
		MessageFactory messageFactory = null;
		SOAPMessage message = null;
		SOAPConnection connection = null;
		URL url = null;
		SOAPMessage response = null;
		try {
			messageFactory = MessageFactory.newInstance(SOAP_1_1_PROTOCOL);
			MimeHeaders mimeHeaders = new MimeHeaders();
			mimeHeaders.addHeader("SOAPAction", "http://www.webserviceX.NET/GetAirportInformationByCountry");
			mimeHeaders.addHeader("Content-Type","text/xml; charset=utf-8");
			InputStream in = SoapDemo.class.getClassLoader().getResourceAsStream("airport.xml");
			message = messageFactory.createMessage(mimeHeaders, in);
			SOAPConnectionFactory connectFactory = SOAPConnectionFactory.newInstance();
			connection = connectFactory.createConnection();
			url = new URL("http://www.webservicex.net/airport.asmx");
			response = connection.call(message, url);
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(xml2String(response));
	}
	
	public static String xml2String(SOAPMessage message){
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = null;
		Source source = null;
		String xmlString = "";
		ByteArrayOutputStream baos = null;
		try {
			tf = tff.newTransformer();
			source = message.getSOAPPart().getContent();
			baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			tf.transform(source, result);
			xmlString = new String(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlString;
	}

}
