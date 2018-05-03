package vy.phoebe;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.JOptionPane;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import vy.phoebe.estimator.BruteForceEstimator;
import vy.phoebe.estimator.Estimator;
import vy.phoebe.estimator.SeedGerminationEstimator;
import vy.phoebe.estimator.SeedGerminationEstimator2;
import vy.phoebe.regression.RModelFactory;
import vy.phoebe.regression.RModelFactoryImpl;
import vy.phoebe.util.ClipboardProcessor;
import vy.phoebe.util.Constants;

public class Config {

	private static ClipboardProcessor clipboardProcessor = null;
	
	
	private static RModelFactory rmFactory = null;
	
	
	private static HashMap<String, Estimator> estiMap = 
		new HashMap<String, Estimator>();

	
//////////////////////////////////////////////////////////////
	public static String ESTI_METHOD = "";
	
	
	public static int DECIMAL_PRECISION = 12;
	
	
	public static int MAX_RESULTS = 10;
	 
	 
	public static double FITNESS = 0;
	 
	 
	public static boolean LINEAR_MODEL = true;
	 
	 
	public static boolean SQUARE_MODEL = true;
	 
	 
	public static boolean CUBE_MODEL = true;
	 
	 
	public static boolean LOG_MODEL = true;
	 
	 
	public static boolean EXP_MODEL = true;
	 
	
	public static boolean PRODUCT_MODEL = true;
	 
	 
	public static boolean GROUP_FLEXIBLE = true;
	 
	 
	public static int GROUP_MAX_RESULTS = 0;
	 
	
//////////////////////////////////////////////////////////////

	static {
		try {
			clipboardProcessor = new ClipboardProcessor();
		}
		catch (Throwable e) {
			clipboardProcessor = null;
			e.printStackTrace();
		}
	}
		
	
	static {
		BruteForceEstimator bf = new BruteForceEstimator();
		estiMap.put(bf.getName(), bf);
		ESTI_METHOD = bf.getName();

		SeedGerminationEstimator2 sg2 = new SeedGerminationEstimator2();
		estiMap.put(sg2.getName(), sg2);

		SeedGerminationEstimator sg = new SeedGerminationEstimator();
		estiMap.put(sg.getName(), sg);
	}
	
	
	public static RModelFactory getRModelFactory() {
		if (rmFactory == null)
			rmFactory = new RModelFactoryImpl();
		
		return rmFactory;
	}
	
	
	public static ClipboardProcessor getClipboardProcessor() {
		return clipboardProcessor;
	}
	
	
	public static boolean doesSupportClipboard() {
		return clipboardProcessor != null;
	}
	
	
	public static boolean testClipboard() {
		if (!doesSupportClipboard()) {
			JOptionPane.showMessageDialog(
					null, 
					"Clipboard not supported", 
					"Clipboard not supported", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	
	public static Estimator getEstimator(String name) {
		return estiMap.get(name);
	}
	
	
	public static Estimator getCurrentEstimator() {
		return estiMap.get(ESTI_METHOD);
	}

	
	public static ArrayList<Estimator> getEstimatorList() {
		return new ArrayList<Estimator>(estiMap.values());
	}

	
	public static ArrayList<String> getEstimatorNameList() {
		return new ArrayList<String>(estiMap.keySet());
	}
	
	
	public static void reset() {
		ESTI_METHOD = getEstimatorNameList().get(0);
		DECIMAL_PRECISION = 12;
		MAX_RESULTS = 10;
		FITNESS = 0;
		LINEAR_MODEL = true;
		SQUARE_MODEL = true;
		CUBE_MODEL = true;
		LOG_MODEL = true;
		EXP_MODEL = true;
		PRODUCT_MODEL = true;
		GROUP_FLEXIBLE = true;
		GROUP_MAX_RESULTS = 0;
	}
	 
	
	public static void read() {
		Hashtable<String,Object> portList = new Hashtable<String, Object>();
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader xmlReader = null;
		String strTag = "";
		String strAtt = "";
		String strData = "";
		    
		int typeXMLNode=0;
		try {
	        	
			xmlReader = inputFactory.createXMLStreamReader(new FileReader(Constants.fileConfig));
			do {
				if(xmlReader.isStartElement()) {
					strTag = xmlReader.getLocalName();
					strAtt = xmlReader.getAttributeValue(0);
					typeXMLNode = xmlReader.next();
					if(xmlReader.hasText()) {
						strData = xmlReader.getText().trim();
						if(strTag.length() > 0 && 
							strData.length() > 0 && strAtt.length() > 0) {
                    	   
							if(strAtt.equals("int"))
								portList.put(strTag, Integer.parseInt(strData));
							else if(strAtt.equals("float"))
								portList.put(strTag, Float.parseFloat(strData));
							else if(strAtt.equals("double"))
								portList.put(strTag, Double.parseDouble(strData));
							else if(strAtt.equals("boolean"))
								portList.put(strTag, Boolean.parseBoolean(strData));
							else if(strAtt.equals("string"))
								portList.put(strTag, strData);
							else
								portList.put(strTag, strData);
						}
					}
				}
				else
					typeXMLNode = xmlReader.next();
	               
			}
			while (typeXMLNode != XMLStreamReader.END_DOCUMENT);
	    	
			xmlReader.close();
			
			if (portList.containsKey("esti_method"))
				ESTI_METHOD = (String)portList.get("esti_method");
			if (portList.containsKey("decimal_precision"))
				DECIMAL_PRECISION = (Integer)portList.get("decimal_precision");
			if (portList.containsKey("max_results"))
				MAX_RESULTS = (Integer)portList.get("max_results");
			if (portList.containsKey("fitness"))
				FITNESS = (Double)portList.get("fitness");
			if (portList.containsKey("linear_model"))
				LINEAR_MODEL = (Boolean)portList.get("linear_model");;
			if (portList.containsKey("square_model"))
				SQUARE_MODEL = (Boolean)portList.get("square_model");;
			if (portList.containsKey("cube_model"))
				CUBE_MODEL = (Boolean)portList.get("cube_model");;
			if (portList.containsKey("log_model"))
				LOG_MODEL = (Boolean)portList.get("log_model");;
			if (portList.containsKey("exp_model"))
				EXP_MODEL = (Boolean)portList.get("exp_model");;
			if (portList.containsKey("product_model"))
				PRODUCT_MODEL = (Boolean)portList.get("product_model");;
			if (portList.containsKey("group_flexible"))
				GROUP_FLEXIBLE = (Boolean)portList.get("group_flexible");;
			if (portList.containsKey("group_max_results"))
				GROUP_MAX_RESULTS = (Integer)portList.get("group_max_results");;
	           
		}
		catch(Exception ex) {
			Config.reset();
		}
	}
	 
	 
	public static void save() {

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlWriter = null;
        try
        {
        	xmlWriter = outputFactory.createXMLStreamWriter(new FileWriter(Constants.fileConfig));

            xmlWriter.writeStartDocument();
            xmlWriter.writeStartElement("config");
            
            xmlWriter.writeStartElement("esti_method");
            xmlWriter.writeAttribute("type", "string");
            xmlWriter.writeCharacters(Config.ESTI_METHOD + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("decimal_precision");
            xmlWriter.writeAttribute("type", "int");
            xmlWriter.writeCharacters(Config.DECIMAL_PRECISION + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("max_results");
            xmlWriter.writeAttribute("type", "int");
            xmlWriter.writeCharacters(Config.MAX_RESULTS + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("fitness");
            xmlWriter.writeAttribute("type", "double");
            xmlWriter.writeCharacters(Config.FITNESS + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("linear_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.LINEAR_MODEL + "");
            xmlWriter.writeEndElement();
                
            xmlWriter.writeStartElement("square_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.SQUARE_MODEL + "");
            xmlWriter.writeEndElement();
                
            xmlWriter.writeStartElement("cube_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.CUBE_MODEL + "");
            xmlWriter.writeEndElement();
                
            xmlWriter.writeStartElement("log_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.LOG_MODEL + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("exp_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.EXP_MODEL + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("product_model");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.PRODUCT_MODEL + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("group_flexible");
            xmlWriter.writeAttribute("type", "boolean");
            xmlWriter.writeCharacters(Config.GROUP_FLEXIBLE+"");
            xmlWriter.writeEndElement();

            xmlWriter.writeStartElement("group_max_results");
            xmlWriter.writeAttribute("type", "int");
            xmlWriter.writeCharacters(Config.GROUP_MAX_RESULTS + "");
            xmlWriter.writeEndElement();

            xmlWriter.writeEndElement();
            xmlWriter.writeEndDocument();

            xmlWriter.close();
        }
        catch(Exception e) {
        	
        }

    }


}
