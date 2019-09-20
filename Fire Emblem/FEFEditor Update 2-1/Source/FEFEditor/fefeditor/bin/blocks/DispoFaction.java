package fefeditor.bin.blocks;

import fefeditor.common.io.BinFile;

import java.io.IOException;
import java.util.ArrayList;

public class DispoFaction 
{
	private String name;
	private int originalSize;
	private int factionOffset;
	private int writeLocation;
	private ArrayList<DispoBlock> spawns = new ArrayList<DispoBlock>();
	
	public DispoFaction(BinFile file) throws IOException
	{
		int originalOffset;
		name = file.readStringFromPointer();
		factionOffset = file.readLittleInt();
		originalSize = file.readLittleInt();
		originalOffset = (int) file.getFilePointer();
		
		file.seek(factionOffset + 0x20);
		for(int x = 0; x < originalSize; x++)
		{
			DispoBlock block = new DispoBlock();
			block.read(file);
			spawns.add(block);
		}
		file.seek(originalOffset);
	}

	public DispoFaction(String name)
	{
		this.name = name;
	}
	
	public boolean containsByPid(String input)
	{
		for(DispoBlock d : spawns)
		{
			if(d.getPid().equals(input))
				return true;
		}
		return false;
	}
	
	public DispoBlock getByPid(String input)
	{
		for(DispoBlock d : spawns)
		{
			if(d.getPid().equals(input))
				return d;
		}
		System.out.println("Error: Failed to find correct dispo block. Returning " + spawns.get(0).getPid());
		return spawns.get(0);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOriginalSize() {
		return originalSize;
	}

	public ArrayList<DispoBlock> getSpawns() {
		return spawns;
	}
	
	public void addSpawn(DispoBlock block) {
		spawns.add(block);
	}

	public int getWriteLocation() {
		return writeLocation;
	}

	public void setWriteLocation(int writeLocation) {
		this.writeLocation = writeLocation;
	}
}
