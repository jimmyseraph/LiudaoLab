package com.liudao.soap.tool;

import java.net.URL;
import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.DOMException;
/**
 * TestModule用于被测试类继承
 * @author liudao
 *
 */
public abstract class TestModule {
	/**
	 * 根据测试子类所提供的参数，替换掉SOAPMessage中的？部分，并发送该请求，获得相应
	 * @param xmlFile SOAP的xml文件
	 * @param url SOAP服务的url地址
	 * @param action SOAPAction字符串
	 * @param args 由测试方法传入的参数化对象
	 * @return 返回请求的响应消息
	 */
	public SOAPMessage getResponse(String xmlFile, URL url, String action, String...args){
		SOAPMessage response = null;
		SoapUtils soap = new SoapUtils();
		SOAPMessage message = soap.getSOAPMessage(xmlFile,action);
		SOAPBody body = null;
		try {
			body = message.getSOAPBody();
			List<SOAPElement> elements = soap.getParamElements(body);
			int i = 0;
			for(SOAPElement element : elements){
				element.getFirstChild().setNodeValue(args[i++]);
			}
		} catch (SOAPException | DOMException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		//进行SOAP请求
		SOAPConnectionFactory factory = null;
		SOAPConnection conn = null;
		try {
			factory = SOAPConnectionFactory.newInstance();
			conn = factory.createConnection();
			response = conn.call(message, url);
		} catch (UnsupportedOperationException | SOAPException e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
