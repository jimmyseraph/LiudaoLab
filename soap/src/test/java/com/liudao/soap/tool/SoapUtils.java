package com.liudao.soap.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class SoapUtils {
	/**
	 * getSOAPMessage方法从指定的xml文件中获取SOAPMessage
	 * @param filename SOAP的xml文件，必须在test的classpath下
	 * @param action SOAPAction字符串
	 * @return 根据soap的xml文件生成的SOAPMessage对象
	 */
	public SOAPMessage getSOAPMessage(String filename,String action){
		SOAPMessage message = null;
		MessageFactory factory = null;
		try {
			factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			MimeHeaders headers = new MimeHeaders();
			headers.addHeader("SOAPAction", action);
			headers.addHeader("Content-Type","text/xml; charset=utf-8");
			message = factory.createMessage(headers,
					this.getClass().getClassLoader().getResourceAsStream(filename));
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * getParamElements方法将SOAPMessage的body部分的含有？字符的节点截取出来
	 * @param element SOAPMessage的body部分
	 * @return 返回含有？字符的节点List
	 */
	public List<SOAPElement> getParamElements(SOAPElement element){
		List<SOAPElement> elements = new ArrayList<>();
		Iterator<?> iterator = element.getChildElements();
		while(iterator.hasNext()){
			Object o = iterator.next();
			if(o instanceof SOAPElement){
				SOAPElement e = (SOAPElement)o;
				if(e.hasChildNodes()){
					if(e.getFirstChild().getNodeType() == Node.TEXT_NODE
							&& e.getFirstChild().getNodeValue().equals("?")){
						elements.add(e);
					}else{
						elements.addAll(getParamElements(e));
					}
				}
					
			}
		}
		return elements;
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
