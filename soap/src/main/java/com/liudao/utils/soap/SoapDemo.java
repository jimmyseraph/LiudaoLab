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
			messageFactory = MessageFactory.newInstance(SOAP_1_1_PROTOCOL);// 使用SOAP1.1协议
			MimeHeaders mimeHeaders = new MimeHeaders();
			// 定义SOAP协议头，主要是SOAPAction和Content-Type
			mimeHeaders.addHeader("SOAPAction", "http://www.webserviceX.NET/GetAirportInformationByCountry");
			mimeHeaders.addHeader("Content-Type","text/xml; charset=utf-8");
			// 从classpath路径中读取airport.xml文件作为SOAPMessage的内容
			InputStream in = SoapDemo.class.getClassLoader().getResourceAsStream("airport.xml");
			// 使用MessageFactory构建SOAPMessage
			message = messageFactory.createMessage(mimeHeaders, in);
			// 使用SOAPConnectionFactory建立SOAPConnection
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
	
	// 将SOAPMessage转换为String，方便输出
	public static String xml2String(SOAPMessage message){
		// SOAPMessage格式转换器
		TransformerFactory tff = TransformerFactory.newInstance();
		Transformer tf = null;
		Source source = null;
		String xmlString = "";
		ByteArrayOutputStream baos = null;
		try {
			tf = tff.newTransformer();
			// 主要讲SOAPPart部分的内容转为String
			source = message.getSOAPPart().getContent();
			baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			tf.transform(source, result);
			xmlString = new String(baos.toByteArray());
			// 将可能存在的一些实体字符集替换为对应字符，提高String可读性
			xmlString = xmlString.replace("&lt;", "<").replace("&gt;", ">");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlString;
	}

}
