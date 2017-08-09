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
	private JPanel soapPropertyPanel = new JPanel(new BorderLayout());;
	private List<JLabeledTextField> propertyTextFields = new ArrayList<>();
	private SOAPMessage soapMessage;
	private List<SOAPElement> elements = new ArrayList<>();
	
	public SoapSamplerGui() {
		super();
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout()); // 设置布局为JMeter内置的BorderLayout
		setBorder(makeBorder()); // 设置边框为默认边框
		add(makeTitlePanel(),BorderLayout.NORTH); // 添加JMeter内置的第一栏名称、注解部分
		JPanel soapConfigurePanel = new JPanel(new BorderLayout()); // 创建一个Panel
		soapConfigurePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "SOAP Configure")); // 设置Panel的边框为带有蚀刻风格的边框线，并且带有名称
		VerticalPanel configureVerticalPanel = new VerticalPanel(5, VerticalPanel.LEFT_ALIGNMENT); // 创建垂直对齐且左对齐的panel
		JPanel configurePanel = new HorizontalPanel(); // 创建水平对齐的Panel
		button.setActionCommand("parse_xml"); // 设置“parse request”按钮的触发命令为“parse_xml”
		button.addActionListener(this); // 添加按钮的触发监听器
		configurePanel.add(soapActionTextField); // 将SoapAction输入框添加到configurePanel中
		configurePanel.add(button); // 将“parse request”按钮添加到configurePanel的SoapAction输入框之后
		configureVerticalPanel.add(urlTextField); // 将"service url"输入框添加到configureVerticalPanel中
		configureVerticalPanel.add(fileTextField); // 将"xml file"输入框添加到configureVerticalPanel中
		configureVerticalPanel.add(configurePanel); // 将configurePanel添加到configureVerticalPanel中
		soapConfigurePanel.add(configureVerticalPanel); // 将configureVerticalPanel添加到soapConfigurePanel中
		add(soapConfigurePanel,BorderLayout.CENTER); // 将soapConfigurePanel添加到界面上
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
		super.configureTestElement(te); // 不可缺少，否则TestElement类无法获得名字
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
		super.configure(te); // 不可缺少，否则将会出现名字丢失
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
		if(action.equals("parse_xml")){ // 判断是否发生的行为名称是parse_xml
			String file = fileTextField.getText(); // 读取xml文件输入框中输入的路径和文件名
			String soapAction = soapActionTextField.getText(); // 读取soapaction输入框中输入的action
			if(!file.trim().equals("")){
				soapMessage = SoapUtils.getSOAPMessage(file.trim(), soapAction); //从文件中读出soapMessage
				try {
					elements = SoapUtils.getParamElements(soapMessage.getSOAPBody()); // 根据"?"获取所有参数节点
					addPropertyPanel(elements,null); // 根据参数节点添加属性panel
				} catch (SOAPException e1) {
					log.error(e1.getMessage());
					e1.printStackTrace();
				}
				
			}else{
				removePropertyPanel(); // 如果输入文件，则移除属性Panel
			}
		}
	}

	private void removePropertyPanel() {
		soapPropertyPanel.removeAll();
		remove(soapPropertyPanel);
	}

	private void addPropertyPanel(List<SOAPElement> elements,List<String> soapProperties) {
		soapPropertyPanel.removeAll();
		if(elements != null){
			soapPropertyPanel.setBorder(BorderFactory.createTitledBorder(
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
			soapPropertyPanel.add(propertyPanel, BorderLayout.NORTH);
			add(soapPropertyPanel,BorderLayout.SOUTH);
		}
		revalidate(); // 由于动态显示，所以需要重画界面
	}
}
