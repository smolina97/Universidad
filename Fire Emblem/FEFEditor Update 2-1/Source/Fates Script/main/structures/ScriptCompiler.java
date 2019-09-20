package main.structures;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import utils.LittleEndianFile;
import utils.ScriptUtils;

public class ScriptCompiler extends LittleEndianFile
{
	private String uncompiled;
	private ArrayList<CompiledEvent> events = new ArrayList<CompiledEvent>();
	private ArrayList<Byte> labels = new ArrayList<Byte>();
	private int pointerStart;
	private int lineNumbers = 1;
	private int nullCounter = 0;
	
	
	public ScriptCompiler(File file, String mode) throws IOException
	{
		super(file, mode);
		this.setLength(0);
		labels.add((byte) 0x0);
	}
	
	public void writeFile(String input) throws Exception
	{
		uncompiled = input;
		String[] unsplitEvents = uncompiled.split("\\r?\\n\\r?\\n");
		for(int x = 0; x < unsplitEvents.length; x++)
		{
			CompiledEvent e = new CompiledEvent();
			String[] temp = unsplitEvents[x].split("\\r?\\n");
			e.setEventText(temp);
			e.setEventStart(lineNumbers);
			events.add(e);
			lineNumbers += temp.length + 1;
		}
		
		writeHeader();
		for(CompiledEvent e : events)
		{
			e.setOffset((int) this.getFilePointer());
			e.compile(this);
			this.seek(e.getOffset());
			for(int x = 0; x < 24; x++)
				this.writeByte(0x0);
			
			e.setSubheaderOffset((int) this.getFilePointer());
			for(Byte b : e.getSubheaderBytes())
				this.writeByte((byte) b);
			e.setEventOffset((int) this.getFilePointer());
			for(int x = 0; x < e.getEventBytes().size(); x++)
			{
				byte b = (byte) e.getEventBytes().get(x);
				this.writeByte(b);
			}
			if(events.indexOf(e) != events.size() - 1)
			{
				while(this.getFilePointer() % 4 != 0)
					this.writeByte(0);	
			}
			int temp = (int) this.getFilePointer();
			
			this.seek(e.getOffset());
			this.writeLittleInt(e.getOffset());
			this.writeLittleInt(e.getEventOffset());
			this.writeLittleInt(e.getHeader().getEventType());
			this.writeLittleInt(events.indexOf(e));
			if(e.getHeader().isRoutine())
				this.writeLittleInt(e.getSubheaderOffset());
			else
				this.writeLittleInt(0);
			if(e.getHeader().isUnknownCheck())
				this.writeLittleInt(0);
			else if(e.getHeader().isHasSubheader() && !e.getHeader().isRoutine())
				this.writeLittleInt(e.getSubheaderOffset());
			else if(e.getHeader().isRoutine())
				this.writeLittleInt(0);
			else
				this.writeLittleInt(e.getEventOffset());
			this.seek(temp);
		}
		int reference = (int) this.getFilePointer();
		labels.remove(0);
		for(byte b : labels)
			this.writeByte(b);
		for(int x = 0; x < nullCounter; x++)
			this.writeByte(0);
		this.seek(0x1C);
		this.writeLittleInt(pointerStart);
		this.writeLittleInt(reference);
		this.seek(pointerStart);
		for(int x = 0; x < events.size(); x++)
		{
			this.writeLittleInt(events.get(x).getOffset());
		}
	}
	
	private void writeHeader() throws Exception
	{
		this.writeString("cmb");
		byte[] header = { 0x19, 0x08, 0x11, 0x20, 0x00, 0x00, 0x00, 0x00, 0x28, 0x00, 0x00, 0x00,
						  0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		this.writeByteArray(header);
		for(int x = 0; x < 3; x++)
			this.writeLittleInt(0);
		this.writeString(this.getFileName());
		while(this.getFilePointer() % 4 != 0)
			this.writeByte(0);
		pointerStart = (int) this.getFilePointer();
		for(int x = 0; x < events.size(); x++)
			this.writeLittleInt(0);
		this.writeLittleInt(0);
	}

	public ArrayList<Byte> getLabels() {
		return labels;
	}

	public int getLabelOffset(String s) throws UnsupportedEncodingException {
		int insertOffset = labels.size() - 1; // We added a 0 to the start of the list to help with searching for strings earlier.
		ArrayList<Byte> searchBytes = new ArrayList<Byte>();
		if(!s.equals("null"))
		{
			byte[] labelBytes = s.getBytes("shift-jis");
			for(byte b : labelBytes)
				searchBytes.add(b);
			searchBytes.add((byte) 0x0);
			
			int labelOffset = ScriptUtils.indexOf(ScriptUtils.byteArrayFromList(labels), ScriptUtils.getSearchBytes(s));
			if(labelOffset != -1)
				return labelOffset;
			else
			{
				for(byte b : searchBytes)
					labels.add(b);
				return insertOffset;
			}
		}
		else
		{
			searchBytes.add((byte) 0x0);
			searchBytes.add((byte) 0x0);
			//int labelOffset = this.search(ScriptUtils.byteArrayFromList(searchBytes), ScriptUtils.byteArrayFromList(labels));
			int labelOffset = ScriptUtils.indexOf(ScriptUtils.byteArrayFromList(labels), ScriptUtils.byteArrayFromList(searchBytes));
			if(labelOffset != -1)
				return labelOffset;
			else
			{
				nullCounter++;
				labels.add((byte) 0x0);
				return insertOffset;
			}
		}
	}
}
