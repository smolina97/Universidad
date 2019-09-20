package main.structures;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import main.MainData;
import main.decompilers.EventDecompiler;
import utils.LittleEndianFile;
import utils.SubheaderParser;

public class Decompiler extends LittleEndianFile
{
	private int referenceOffset;
	private int tableStart;
	
	private ArrayList<Integer> headerOffsets = new ArrayList<Integer>();
	private ArrayList<HeaderInformation> headers = new ArrayList<HeaderInformation>();
	private int lineNumbers = 1;
	private String script = "";
	
	public Decompiler(File file, String mode) throws Exception
	{
		super(file, mode);
		this.seek(0x1C);
		tableStart = this.readLittleInt();
		referenceOffset = this.readLittleInt();
		
		this.seek(tableStart);
		int offset = 0;
		while((offset = this.readLittleInt()) != 0)
			headerOffsets.add(offset);
		for(Integer i : headerOffsets)
		{
			HeaderInformation header = new HeaderInformation();
			this.seek(i);
			header.read(this);
			headers.add(header);
			script += "Header ev" + headers.indexOf(header) + header.getHeaderText();
			lineNumbers++;
			
			if(header.isHasSubheader())
			{
				if(MainData.getInstance().getSubheaders().containsKey(header.getEventType()))
				{
					this.seek(header.getSubheaderOffset());
					ArrayList<String> subheader = SubheaderParser.parseKnownSubheader(this, header.getEventType());
					script += "Subheader ev" + headers.indexOf(header) + System.lineSeparator();
					for(String s : subheader)
						script += s + System.lineSeparator();
					lineNumbers += subheader.size() + 1;	
				}
				else if(header.getUnknown() != 0)
				{
					this.seek(header.getUnknown());
					script += "Subheader ev" + headers.indexOf(header) + System.lineSeparator() + SubheaderParser.parseSpecialSubheader(this);
					lineNumbers += 3;
				}
				else
				{
					this.seek(header.getSubheaderOffset());
					ArrayList<String> subheader = SubheaderParser.parseSubheader(this, header.getEventOffset());
					script += "Subheader ev" + headers.indexOf(header) + System.lineSeparator();
					for(String s : subheader)
						script += s + System.lineSeparator();
					lineNumbers += subheader.size() + 1;	
				}
			}
			
			byte[] eventBytes = getEventBytes(header.getEventOffset());
			script += "Event ev" + headers.indexOf(header) + System.lineSeparator();
			lineNumbers++;
			//System.out.println("Decompiling event " + headers.indexOf(header));
			EventDecompiler eventDecompiler = new EventDecompiler();
			ArrayList<String> decompiled = eventDecompiler.parseEventString(eventBytes, this, lineNumbers);
			for(String s : decompiled)
				script += s + System.lineSeparator();
			lineNumbers += decompiled.size() + 1;
			if(headerOffsets.indexOf(i) != headerOffsets.size() - 1)
				script += System.lineSeparator(); 
		}
	}
	
	public byte[] getEventBytes(int offset) throws IOException
	{
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		this.seek(offset);
		while((readLittleShort()) != 0x54)
		{
			this.seek(this.getFilePointer() - 0x2);
			byte b = this.readByte();
			if(b == 0x47 || b == 0x1D || b == 0x4C || b == 0x46 || b == 0x49)
			{
				bytes.add(b);
				for(int x = 0; x < 2; x++)
					bytes.add(this.readByte());
			}
			else if(b == 0x1C)
			{
				bytes.add(b);
				bytes.add(this.readByte());
			}
			else if(b == 0x1F)
			{
				bytes.add(b);
				for(int x = 0; x < 4; x++)
					bytes.add(this.readByte());
			}
			else
				bytes.add(b);
		}
		return this.getByteArray(bytes);
	}

	public String getScript() {
		return script;
	}

	public int getReferenceOffset() {
		return referenceOffset;
	}
}
