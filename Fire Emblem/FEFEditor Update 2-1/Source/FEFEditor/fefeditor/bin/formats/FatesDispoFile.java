package fefeditor.bin.formats;

import fefeditor.bin.blocks.DispoBlock;
import fefeditor.bin.blocks.DispoFaction;
import fefeditor.common.io.BinFile;
import fefeditor.common.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FatesDispoFile  extends BinFile
{
	private ArrayList<DispoFaction> factions = new ArrayList<DispoFaction>();
	private ArrayList<Integer> pointers = new ArrayList<Integer>();
	private byte[] compiledLabels;
	
	public FatesDispoFile(File file, String mode) throws IOException
	{
		super(file, mode);
		parseFactions();
	}

	private void parseFactions() throws IOException
	{
		this.seek(0x20);
		while((this.readLittleInt() + this.readLittleInt()) != 0x0)
		{
			this.seek(this.getFilePointer() - 0x8);
			DispoFaction faction = new DispoFaction(this);
			factions.add(faction);
		}
	}
	
	public void writeFile() throws Exception
	{
		int pointerOneStart = (factions.size() + 1) * 0xC + 0x20;
		int blockStart = pointerOneStart;
		int labelStart;
		
		this.seek(0x0);
		this.setLength(0);
		compileLabels();
		
		for(DispoFaction f : factions)
		{
			pointerOneStart += f.getSpawns().size() * 0x8C;
			f.setWriteLocation(blockStart);
			blockStart += f.getSpawns().size() * 0x8C;
		}
		compilePointers(blockStart);
		labelStart = pointerOneStart + pointers.size() * 4;
		
		for(int x = 0; x < 0x20; x++)
			this.writeByte(0);
		writeFactionHeaders(labelStart);
		
		for(DispoFaction f : factions)
		{
			for(DispoBlock d : f.getSpawns())
				d.write(this, labelStart);
		}
		
		this.seek(pointerOneStart);
		for(Integer i : pointers)
		{
			if(i >= 0)
				this.writeLittleInt(i);
			else
				System.out.println("Error: Negative File Pointer");
		}
		
		for(int x = 1; x < compiledLabels.length; x++)
			this.writeByte(compiledLabels[x]);
		
		this.seek(0);
		this.writeLittleInt((int) this.length());
		this.writeLittleInt(pointerOneStart - 0x20);
		this.writeLittleInt(pointers.size());
		this.writeLittleInt(0);
	}
	
	private void writeFactionHeaders(int labelStart) throws Exception
	{
		for(DispoFaction f : factions)
		{
			this.writeLittleInt(this.getStringOffset(f.getName(), this, labelStart));
			this.writeLittleInt(f.getWriteLocation() - 0x20);
			this.writeLittleInt(f.getSpawns().size());
		}
		for(int x = 0; x < 3; x++)
		{
			this.writeLittleInt(0x0);
		}
	}
	
	public int getStringOffset(String input, FatesDispoFile file, int labelStart) throws Exception
	{
		int offset = IOUtils.search(IOUtils.getSearchBytes(input), file.getCompiledLabels());
		if(input.equals(""))
			return 0;
		if(offset != -1)
		{
			return offset - 0x20 + labelStart;	
		}
		else
			return 0;
	}
	
	private void compilePointers(int spawnStart) throws IOException
	{
		for(DispoFaction f : factions)
		{
			pointers.add(factions.indexOf(f) * 0xC);
			pointers.add(factions.indexOf(f) * 0xC + 4);
		}
		for(DispoFaction f : factions)
		{
			for(DispoBlock d : f.getSpawns())
			{
				int blockStart = f.getWriteLocation() + f.getSpawns().indexOf(d) * 0x8C;
				for(Integer i : d.getPointers(blockStart))
				{
					if(!pointers.contains(i))
						pointers.add(i);
				}
			}
		}
	}
	
	private void compileLabels() throws UnsupportedEncodingException
	{
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		
		
		for(DispoFaction f : factions)
		{
			if(!labels.contains(f.getName()))
				labels.add(f.getName());
		}
		for(DispoFaction f : factions)
		{
			for(DispoBlock d : f.getSpawns())
			{
				if(!labels.contains(d.getPid()))
					labels.add(d.getPid());
				for(int x = 0; x < 5; x++)
				{
					if(!labels.contains(d.getItems()[x]))
						labels.add(d.getItems()[x]);
				}
				for(int x = 0; x < 5; x++)
				{
					if(!labels.contains(d.getSkills()[x]))
						labels.add(d.getSkills()[x]);
				}
				
				if(!labels.contains(d.getAc()))
					labels.add(d.getAc());
				if(!labels.contains(d.getAiPositionOne()))
					labels.add(d.getAiPositionOne());
				if(!labels.contains(d.getMi()))
					labels.add(d.getMi());
				if(!labels.contains(d.getAiPositionTwo()))
					labels.add(d.getAiPositionTwo());
				if(!labels.contains(d.getAt()))
					labels.add(d.getAt());
				if(!labels.contains(d.getAiPositionThree()))
					labels.add(d.getAiPositionThree());
				if(!labels.contains(d.getMv()))
					labels.add(d.getMv());
				if(!labels.contains(d.getAiPositionFour()))
					labels.add(d.getAiPositionFour());
			}
		}

		bytes.add((byte) 0);
		for(String s : labels)
		{
			if(!s.equals(""))
			{
				byte[] temp = s.getBytes("shift-jis");
				for(Byte b : temp)
					bytes.add(b);
				bytes.add((byte) 0);	
			}
		}
		bytes.add((byte) 0);
		
		byte[] complete = new byte[bytes.size()];
		for(int x = 0; x < bytes.size(); x++)
		{
			complete[x] = bytes.get(x);
		}
		setCompiledLabels(complete);
	}
	
	public DispoFaction getByName(String input)
	{
		for(DispoFaction f : factions)
		{
			if(f.getName().equals(input))
				return f;
		}
		return factions.get(0);
	}
	
	public ArrayList<DispoFaction> getFactions() {
		return factions;
	}

	public byte[] getCompiledLabels() {
		return compiledLabels;
	}

	public void setCompiledLabels(byte[] compiledLabels) {
		this.compiledLabels = compiledLabels;
	}
}
