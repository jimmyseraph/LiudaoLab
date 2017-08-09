package com.liudao.jmeter.soapSampler.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liudao.jmeter.soapSampler.SoapSampler;
import com.liudao.jmeter.utils.SoapUtils;

public class SoapSamplerGui extends AbstractSamplerGui implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(SoapSamplerGui.class);
	
	private JLabeledTextField fileTextField = new JLabeledTextField("xml file");  
	private JLabeledTextField urlTextField = new JLabeledTextField("service url"); 
	private JButton button = new JButton("parse request");
	private JLabeledTextField soapActionTextField = new JLabeledTextField("SoapAction");
	private JPanel demoPropertyPanel = new JPanel(new BorderLayout());;
	private List<JLabeledTextField> propertyTextFields = new ArrayList<>();
	private SOAPMessage soapMessage;
	private List<SOAPElement> elements = new ArrayList<>();
	
	public SoapSamplerGui() {
		super();
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		add(makeTitlePanel(),BorderLayout.NORTH);
		JPanel demoConfigurePanel = new JPanel(new BorderLayout());
		demoConfigurePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "SOAP Configure"));
		VerticalPanel configureVerticalPanel = new VerticalPanel(5, VerticalPanel.LEFT_ALIGNMENT); 
		JPanel configurePanel = new HorizontalPanel();
		button.setActionCommand("parse_xml");
		button.addActionListener(this);
		configurePanel.add(soapActionTextField);
		configurePanel.add(button);
		configureVerticalPanel.add(urlTextField);
		configureVerticalPanel.add(fileTextField);
		configureVerticalPanel.add(configurePanel);
		demoConfigurePanel.add(configureVerticalPanel);
		add(demoConfigurePanel,BorderLayout.CENTER);
	}

	@Override
	public String getLabelResource() {
		return "SOAP Sampler";
	}
	
	@Override
	public String getStaticLabel() {
		return "SOAP Sampler";
	}

	@Override
	public TestElement createTestElement() {
		SoapSampler sampler = new SoapSampler();
		modifyTestElement(sampler);
		return sampler;
	}

	@Override
	public void modifyTestElement(TestElement te) {
		super.configureTestElement(te);
		SoapSampler sampler = (SoapSampler)te;
		sampler.setUrl(urlTextField.getText());
		sampler.setFilename(fileTextField.getText());
		sampler.setSoapAction(soapActionTextField.getText());
		List<String> soapProperties = new ArrayList<>();
		if(propertyTextFields.size() != 0){
			for(int i = 0; i < propertyTextFields.size(); i++){
				soapProperties.add(propertyTextFields.get(i).getText());
			}
		}
		sampler.setProperties(soapProperties);
	}
	
	@Override
	public void configure(TestElement te) {
		super.configure(te);
		SoapSampler sampler = (SoapSampler)te;
		urlTextField.setText(sampler.getUrl());
		fileTextField.setText(sampler.getFilename());
		soapActionTextField.setText(sampler.getSoapAction());
		List<String> soapProperties = sampler.getProperties();
		if(soapProperties != null){
			if(soapProperties.size() != 0){
				addPropertyPanel(elements, soapProperties);
			}
		}
	}
	
	@Override
	public void clearGui() {
		super.clearGui();
		urlTextField.setText("");
		fileTextField.setText("");
		soapActionTextField.setText("");
		removePropertyPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals("parse_xml")){
			String file = fileTextField.getText();
			String soapAction = soapActionTextField.getText();
			if(!file.trim().equals("")){
				soapMessage = SoapUtils.getSOAPMessage(file.trim(), soapAction);
				try {
					elements = SoapUtils.getParamElements(soapMessage.getSOAPBody());
					addPropertyPanel(elements,null);
				} catch (SOAPException e1) {
					log.error(e1.getMessage());
					e1.printStackTrace();
				}
				
			}else{
				removePropertyPanel();
			}
		}
	}

	private void removePropertyPanel() {
		demoPropertyPanel.removeAll();
		remove(demoPropertyPanel);
	}

	private void addPropertyPanel(List<SOAPElement> elements,List<String> soapProperties) {
		demoPropertyPanel.removeAll();
		if(elements != null){
			demoPropertyPanel.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), "SOAP Property"));
			VerticalPanel propertyPanel = new VerticalPanel(
					8, VerticalPanel.LEFT_ALIGNMENT);
			propertyTextFields.clear();
			
			for(int i = 0; i < elements.size(); i++){
				JLabeledTextField propertyTextField = new JLabeledTextField(elements.get(i).getTagName());
				if(soapProperties != null){
					propertyTextField.setText(soapProperties.get(i));
				}
				propertyTextFields.add(propertyTextField);
				propertyPanel.add(propertyTextField);
			}
			demoPropertyPanel.add(propertyPanel, BorderLayout.NORTH);
			add(demoPropertyPanel,BorderLayout.SOUTH);
		}
		revalidate();
	}
}
