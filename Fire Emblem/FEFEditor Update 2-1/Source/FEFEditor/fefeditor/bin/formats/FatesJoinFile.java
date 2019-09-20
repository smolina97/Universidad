package fefeditor.bin.formats;

import fefeditor.bin.blocks.JoinBlock;
import fefeditor.common.io.BinFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FatesJoinFile extends BinFile
{
	private ArrayList<JoinBlock> blocks = new ArrayList<JoinBlock>();
	private ArrayList<Integer> pointers = new ArrayList<Integer>();
	private byte[] compiledLabels;
	
	public FatesJoinFile(File file, String mode) throws IOException
	{
		super(file, mode);
	}
	
	public void readBlocks() throws IOException
	{
		int length;
		this.seek(0x20);
		length = this.readLittleInt();
		this.readLittleInt();
		for(int x = 0; x < length; x++)
		{
			this.seek(0x28 + 0x20 * x);
			blocks.add(new JoinBlock());
			blocks.get(x).read(this);
		}
	}
	
	public void writeFile() throws Exception
	{
		int pointerStart = blocks.size() * 0x20 + 0x24;
		int labelStart;
		
		this.setLength(0);
		compileLabels();
		for(JoinBlock b : blocks)
		{
			for(int i : b.getPointerOffsets(this, compiledLabels, blocks.indexOf(b) * 0x20 + 0x24))
			{
				if(!pointers.contains(i))
					pointers.add(i);
			}
		}
		labelStart = pointerStart + pointers.size() * 0x4;
		for(int x = 0; x < 0x20; x++)
			this.writeByte(0);
		this.writeLittleInt(blocks.size());
		for(JoinBlock b : blocks)
		{
			this.writeLittleInt(blocks.indexOf(b));
			b.write(this, compiledLabels, labelStart);	
		}
		pointerStart = (int) this.getFilePointer();
		for(Integer i : pointers)
			this.writeLittleInt(i);
		labelStart = (int) this.getFilePointer();
		for(int x = 1; x < compiledLabels.length; x++)
			this.writeByte(compiledLabels[x]);
		
		this.seek(0);
		this.writeLittleInt((int) this.length());
		this.writeLittleInt(pointerStart - 0x20);
		this.writeLittleInt(pointers.size());
		this.writeLittleInt(0);
	}
	
	public JoinBlock getByPid(String pid)
	{
		for(JoinBlock j : blocks)
		{
			if(j.getCharacter().equals(pid))
				return j;
		}
		return blocks.get(0);
	}
	
	public boolean containsByPid(String pid)
	{
		for(JoinBlock j : blocks)
		{
			if(j.getCharacter().equals(pid))
				return true;
		}
		return false;
	}
	
	public void addBlock(String pid, String birthright, String conquest, String revelation)
	{
		JoinBlock block = new JoinBlock();
		block.setCharacter(pid);
		block.setBirthrightJoin(birthright);
		block.setConquestJoin(conquest);
		block.setRevelationJoin(revelation);
		int[] temp = new int[3];
		for(int x = 0; x < temp.length; x++)
			temp[x] = -1;
		block.setUnknownValues(temp);
		blocks.add(block);
	}
	
	public void compileLabels() throws Exception
	{
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		for(JoinBlock b : blocks)
		{
			for(String s : b.getContainedLabels(this))
			{
				if(!labels.contains(s))
					labels.add(s);
			}
		}
		bytes.add((byte) 0);
		for(String s : labels)
		{
			byte[] temp = s.getBytes("shift-jis");
			for(int x = 0; x < temp.length; x++)
				bytes.add(temp[x]);
			bytes.add((byte)0);
		}
		bytes.add((byte)0);
		
		byte[] finalBytes = new byte[bytes.size()];
		for(int x = 0; x < finalBytes.length; x++)
			finalBytes[x] = bytes.get(x);
		compiledLabels = finalBytes;
	}

	public ArrayList<JoinBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<JoinBlock> blocks) {
		this.blocks = blocks;
	}
}
