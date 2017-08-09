package com.liudao.jmeter.soapSampler;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liudao.jmeter.utils.SoapUtils;

public class SoapSampler extends AbstractSampler{

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SoapSampler.class);
	
	private List<SOAPElement> elements = new ArrayList<>();
	private SOAPMessage message;
	private static final String PROPERTY_LIST = "soap_property_list";
	private static final String URL = "soap_url";
	private static final String XMLFILE = "soap_xml_file";
	private static final String SOAPACTION = "soap_action";
	
	public String getFilename() {
		return getPropertyAsString(SoapSampler.XMLFILE);
	}

	public void setFilename(String filename) {
		setProperty(SoapSampler.XMLFILE, filename);
	}

	public String getSoapAction() {
		return getPropertyAsString(SoapSampler.SOAPACTION);
	}

	public void setSoapAction(String soapAction) {
		setProperty(SoapSampler.SOAPACTION, soapAction);
	}

	public SoapSampler() {
		super();
	}

	public List<String> getProperties() {
		CollectionProperty cp = (CollectionProperty)getProperty(SoapSampler.PROPERTY_LIST);
		List<String> ls = new ArrayList<>();
		for(int i = 0; i < cp.size(); i++){
			ls.add(cp.get(i).getStringValue());
		}
		return ls;
	}

	public void setProperties(List<String> properties) {
		setProperty(new CollectionProperty(SoapSampler.PROPERTY_LIST, properties));
	}


	public String getUrl() {
		return getPropertyAsString(SoapSampler.URL);
	}


	public void setUrl(String url) {
		setProperty(SoapSampler.URL, url);
	}
	
	@Override
	public SampleResult sample(Entry e) {
		SampleResult sr = new SampleResult();
		sr.setSampleLabel(getName());
		sr.sampleStart();
		sr.setContentType("text/xml; charset=utf-8");
		sr.setDataType(SampleResult.TEXT);
		sr.setDataEncoding("utf-8");
		String filename = getFilename();
		String action = getSoapAction();
		if(filename == null || filename.equals("")){
			log.error("xmlfile is null");
			sr.setSuccessful(false);
			return sr;
		}
		SOAPConnectionFactory factory = null;
		SOAPConnection conn = null;
		message = SoapUtils.getSOAPMessage(filename, action);
		try {
			elements = SoapUtils.getParamElements(message.getSOAPBody());
			List<String> properties = getProperties();
			for(int i = 0; i < elements.size(); i++){
				elements.get(i).getFirstChild().setNodeValue(properties.get(i));
			}
			factory = SOAPConnectionFactory.newInstance();
			conn = factory.createConnection();
			String url = getUrl();
			
			SOAPMessage response = conn.call(message, new URL(url));
			
			sr.setResponseCodeOK();
			sr.setResponseMessageOK();
			sr.setResponseData(SoapUtils.xml2String(response).getBytes());
			sr.setSuccessful(true);
		} catch (Exception e1) {
			sr.setResponseCode("500");
			sr.setResponseMessage("internal error");
			log.error(e1.getMessage());
			sr.setSuccessful(false);
			e1.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (SOAPException e1) {
				e1.printStackTrace();
			}
			sr.sampleEnd();
		}
		return sr;
	}

}
