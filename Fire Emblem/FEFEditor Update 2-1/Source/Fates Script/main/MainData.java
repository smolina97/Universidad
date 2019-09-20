package main;

import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.stage.Stage;

public class MainData 
{
	private static MainData instance;
	
	private Stage stage;
	private boolean translate = true;
	private Hashtable<String, Byte[]> tags = new Hashtable<String, Byte[]>();
	private Hashtable<Integer, Byte[]> subheaders = new Hashtable<Integer, Byte[]>();
	private Hashtable<String, String> japaneseToEnglish = new Hashtable<String, String>();
	private Hashtable<String, String> englishToJapanese = new Hashtable<String, String>();
	
	private final String[] KEYWORDS = new String[] {
            "Header", "Subheader", "Event", "storein", "call",
            "int", "string", "byte", "end", "short", "checkval", 
            "raw", "null", "routine"
    };
	
	private final String[] INDICATORS = new String[] {
            "pass", "fail", "specialCheck", "goto", "unknownCheck"
    };
	
	private final String[] RESTRICTED = new String[] {
			"omit", "followFailure", "reduce"
	};

    private final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private final String INDICATOR_PATTERN = "\\b(" + String.join("|", INDICATORS) + ")\\b";
    private final String RESTRICTED_PATTERN = "\\b(" + String.join("|", RESTRICTED) + ")\\b";
    private final String PAREN_PATTERN = "\\(|\\)";
    private final String BRACE_PATTERN = "\\{|\\}";
    private final String BRACKET_PATTERN = "\\[|\\]";
    private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";

    private final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<INDICATOR>" + INDICATOR_PATTERN + ")"
            + "|(?<RESTRICTED>" + RESTRICTED_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
    );
	
	public MainData() throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(this.getClass().getResourceAsStream("data/Commands.xml"));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("Command");
        for(int x = 0; x < nList.getLength(); x++)
        {
        	Node node = nList.item(x);
        	if(!node.getAttributes().getNamedItem("name").getNodeValue().equals(""))
        	{
        		if(node.getAttributes().getNamedItem("tag").getNodeValue().equals(""))
        		{
        			tags.put(node.getAttributes().getNamedItem("name").getNodeValue(), new Byte[0]);
        		}
        		else
        		{
        			String[] splitString = node.getAttributes().getNamedItem("tag").getNodeValue().split(",");
            		Byte[] bytes = new Byte[splitString.length];
            		for(int y = 0; y < bytes.length; y++)
            		{
            			bytes[y] = Byte.parseByte(splitString[y], 16);
            		}
            		tags.put(node.getAttributes().getNamedItem("name").getNodeValue(), bytes);	
        		}
        	}
        }
        
        doc = dBuilder.parse(this.getClass().getResourceAsStream("data/Translate.xml"));
        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("Entry");
        for(int x = 0; x < nList.getLength(); x++)
        {
        	Node node = nList.item(x);
        	if(!node.getAttributes().getNamedItem("japanese").getNodeValue().equals(""))
        	{
        		japaneseToEnglish.put(node.getAttributes().getNamedItem("japanese").getNodeValue(), node.getAttributes().getNamedItem("english").getNodeValue());
        		englishToJapanese.put(node.getAttributes().getNamedItem("english").getNodeValue(), node.getAttributes().getNamedItem("japanese").getNodeValue());
        	}
        }
        
        doc = dBuilder.parse(this.getClass().getResourceAsStream("data/Subheaders.xml"));
        doc.getDocumentElement().normalize();
        nList = doc.getElementsByTagName("Subheader");
        for(int x = 0; x < nList.getLength(); x++)
        {
        	Node node = nList.item(x);
        	if(!node.getAttributes().getNamedItem("id").getNodeValue().equals(""))
        	{
        		int id = Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue());
        		String[] unparsedValues = node.getAttributes().getNamedItem("layout").getNodeValue().split(",");
        		Byte[] values = new Byte[unparsedValues.length];
        		for(int y = 0; y < values.length; y++)
        			values[y] = Byte.parseByte(unparsedValues[y]); 
        		subheaders.put(id, values);
        	}
        }
	}
	
	public static MainData getInstance() throws Exception
	{
		if(instance == null)
			instance = new MainData();
		return instance;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public Hashtable<String, Byte[]> getTags()
	{
		return tags;
	}

	public Hashtable<String, String> getEnglishToJapanese() {
		return englishToJapanese;
	}
	
	public Hashtable<String, String> getJapaneseToEnglish() {
		return japaneseToEnglish;
	}

	public boolean isTranslate() {
		return translate;
	}

	public void setTranslate(boolean translate) {
		this.translate = translate;
	}

	public Pattern getPATTERN() {
		return PATTERN;
	}

	public Hashtable<Integer, Byte[]> getSubheaders() {
		return subheaders;
	}
}
