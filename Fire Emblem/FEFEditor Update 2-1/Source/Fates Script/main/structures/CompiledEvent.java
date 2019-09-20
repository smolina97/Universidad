package main.structures;

import java.util.ArrayList;

import main.compilers.EventCompiler;
import utils.ScriptUtils;
import utils.SubheaderParser;

public class CompiledEvent 
{
	private HeaderInformation header;
	private String[] eventText;
	private int offset;
	private int subheaderOffset;
	private int eventOffset;
	private int eventStart;
	private ArrayList<Byte> subheaderBytes = new ArrayList<Byte>();
	private ArrayList<Byte> eventBytes = new ArrayList<Byte>();
	
	public CompiledEvent()
	{
		
	}
	
	public void compile(ScriptCompiler compiler) throws Exception
	{
		if(header.isHasSubheader())
			subheaderBytes = SubheaderParser.getSubheaderFromText(compiler, eventText, 1);
		String[] onlyEvent = new String[eventText.length - getEventLine()];
		for(int x = getEventLine(); x < eventText.length; x++)
		{
			onlyEvent[x - getEventLine()] = eventText[x];
		}
		EventCompiler eventCompiler = new EventCompiler();
		eventBytes = eventCompiler.createEvent(compiler, onlyEvent, 0, getEventLine() + eventStart);
	}
	
	public HeaderInformation getHeader() {
		return header;
	}

	public String[] getEventText() {
		return eventText;
	}
	
	public int getEventLine()
	{
		for(int x = 0; x < eventText.length; x++)
		{
			if(eventText[x].startsWith("Event"))
				return x + 1;
		}
		return -1;
	}

	public void setEventText(String[] eventText) throws Exception {
		//Eliminate whitespace first.
		this.eventText = eventText;
		parseHeaderInformation();
		for(int y = 0; y < eventText.length; y++)
		{
			eventText[y] = eventText[y].trim();
			if(eventText[y].startsWith("routine"))
				header.setRoutine(true);
		}
	}
	
	private void parseHeaderInformation()
	{
		if(eventText[0].startsWith("Header"))
		{
			header = new HeaderInformation();
			String[] parameters = ScriptUtils.parseEmbeddedParameters(eventText[0]);
			header.setEventType(Integer.parseInt(parameters[0]));
			header.setHasSubheader(Boolean.parseBoolean(parameters[1]));
			header.setUnknownCheck(Boolean.parseBoolean(parameters[2]));
		}
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ArrayList<Byte> getSubheaderBytes() {
		return subheaderBytes;
	}

	public ArrayList<Byte> getEventBytes() {
		return eventBytes;
	}

	public int getSubheaderOffset() {
		return subheaderOffset;
	}

	public void setSubheaderOffset(int subheaderOffset) {
		this.subheaderOffset = subheaderOffset;
	}

	public int getEventOffset() {
		return eventOffset;
	}

	public void setEventOffset(int eventOffset) {
		this.eventOffset = eventOffset;
	}

	public int getEventStart() {
		return eventStart;
	}

	public void setEventStart(int eventStart) {
		this.eventStart = eventStart;
	}
}
