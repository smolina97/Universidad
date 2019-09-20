package fefeditor.bin.blocks;

import fefeditor.bin.formats.FatesJoinFile;
import fefeditor.common.io.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JoinBlock 
{
	private String character;
	private String birthrightJoin;
	private String conquestJoin;
	private String revelationJoin;
	private int[] unknownValues = new int[3];
	
	public JoinBlock()
	{
		character = "PlaceholderName";
	}
	
	public JoinBlock(JoinBlock j)
	{
		this.character = "PlaceholderName";
		this.birthrightJoin = j.getBirthrightJoin();
		this.conquestJoin = j.getConquestJoin();
		this.revelationJoin = j.getRevelationJoin();
		this.unknownValues = j.getUnknownValues();
	}

	public void read(FatesJoinFile file) throws IOException
	{
		character = file.readStringFromPointer();
		birthrightJoin = file.readStringFromPointer();
		conquestJoin = file.readStringFromPointer();
		revelationJoin = file.readStringFromPointer();
		unknownValues = file.readIntArray(3);
	}
	
	public void write(FatesJoinFile file, byte[] compiledLabels, int labelStart) throws Exception
	{
		if(this.getLabelOffset(character, compiledLabels, file) != 0)
			file.writeLittleInt(this.getLabelOffset(character, compiledLabels, file) + labelStart);
		else
			file.writeLittleInt(0);
		if(this.getLabelOffset(birthrightJoin, compiledLabels, file) != 0)
			file.writeLittleInt(this.getLabelOffset(birthrightJoin, compiledLabels, file) + labelStart);
		else
			file.writeLittleInt(0);
		if(this.getLabelOffset(conquestJoin, compiledLabels, file) != 0)
			file.writeLittleInt(this.getLabelOffset(conquestJoin, compiledLabels, file) + labelStart);
		else
			file.writeLittleInt(0);
		if(this.getLabelOffset(revelationJoin, compiledLabels, file) != 0)
			file.writeLittleInt(this.getLabelOffset(revelationJoin, compiledLabels, file) + labelStart);
		else
			file.writeLittleInt(0);
		file.writeIntArray(unknownValues);
	}
	
	public ArrayList<String> getContainedLabels(FatesJoinFile file) throws Exception
	{
		ArrayList<String> strings = new ArrayList<String>();
		if(!character.equals(""))
			strings.add(character);
		if(!birthrightJoin.equals(""))
			strings.add(birthrightJoin);
		if(!conquestJoin.equals(""))
			strings.add(conquestJoin);
		if(!revelationJoin.equals(""))
			strings.add(revelationJoin);
		return strings;
	}
	
	public ArrayList<Integer> getPointerOffsets(FatesJoinFile file, byte[] search, int blockStart) throws Exception
	{
		ArrayList<Integer> pointers = new ArrayList<Integer>();
		if(this.getLabelOffset(character, search, file) != 0)
			pointers.add(blockStart + 4 - 0x20);
		if(this.getLabelOffset(birthrightJoin, search, file) != 0)
			pointers.add(blockStart + 8 - 0x20);
		if(this.getLabelOffset(conquestJoin, search, file) != 0)
			pointers.add(blockStart + 12 - 0x20);
		if(this.getLabelOffset(revelationJoin, search, file) != 0)
			pointers.add(blockStart + 16 - 0x20);
		return pointers;
	}
	
	public int getLabelOffset(String input, byte[] search, FatesJoinFile file) throws Exception
	{
		int offset = IOUtils.search(this.getSearchBytes(input), search);
		if(offset != -1)
			return offset - 0x20;	
		else
			return 0;
	}
	
	public byte[] getSearchBytes(String input) throws UnsupportedEncodingException
	{
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte) 0);
		for(Byte b : input.getBytes("shift-jis"))
			bytes.add(b);
		bytes.add((byte) 0);
		
		byte[] temp = new byte[bytes.size()];
		for(int x = 0; x < bytes.size(); x++)
			temp[x] = bytes.get(x);
		return temp;
	}
	
	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public String getBirthrightJoin() {
		return birthrightJoin;
	}

	public void setBirthrightJoin(String birthrightJoin) {
		this.birthrightJoin = birthrightJoin;
	}

	public String getConquestJoin() {
		return conquestJoin;
	}

	public void setConquestJoin(String conquestJoin) {
		this.conquestJoin = conquestJoin;
	}

	public String getRevelationJoin() {
		return revelationJoin;
	}

	public void setRevelationJoin(String revelationJoin) {
		this.revelationJoin = revelationJoin;
	}

	public int[] getUnknownValues() {
		return unknownValues;
	}

	public void setUnknownValues(int[] unknownValues) {
		this.unknownValues = unknownValues;
	}
}
