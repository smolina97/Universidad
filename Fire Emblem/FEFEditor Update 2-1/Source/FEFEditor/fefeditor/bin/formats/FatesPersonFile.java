package fefeditor.bin.formats;

import fefeditor.bin.blocks.CharacterBlock;
import fefeditor.common.io.BinFile;
import fefeditor.common.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FatesPersonFile extends BinFile
{
	private ArrayList<CharacterBlock> characters = new ArrayList<CharacterBlock>();
	int pointerOneStart;
	int bonusStart;
	
	private short currentTableCount;
	
	public FatesPersonFile(File file, String mode) throws Exception
	{
		super(file, mode);
		parseHeaderInformation();
		parseTable();
	}
	
	public void parseHeaderInformation() throws IOException
	{
		this.seek(0x24);
		currentTableCount = this.readLittleShort();
		this.seek(0x30);
	}
	
	public void parseTable() throws Exception
	{
		for(int x = 0; x < currentTableCount; x++)
		{
			this.seek(0x30 + x * 0x98);
			characters.add(new CharacterBlock());
			characters.get(characters.size() -1).read(this);
		}
	}
	
	public boolean containsByPid(String pid)
	{
		for(CharacterBlock c : characters)
		{
			if(c.getPid().equals(pid))
				return true;
		}
		return false;
	}
	
	public CharacterBlock getByPid(String pid)
	{
		for(CharacterBlock c : characters)
		{
			if(c.getPid().equals(pid))
				return c;
		}
		return null;
	}
	
	public boolean containsLabel(String s) throws Exception {
        byte[] bytes = s.getBytes("shift-jis");
        int labelOffset = IOUtils.search(bytes, this.toByteArray());
        return labelOffset != -1;
    }
	
	public void writeFile(String fileName) throws Exception //This code needs an overhaul at some point...
	{
		int fileSize = 0x30 + characters.size() * 0x98;
		bonusStart = fileSize;
		int pointerOneCount = 1;
		int pointerTwoStart;
		int labelStart;
		this.seek(0x00);
		for(int x = 0; x < this.length(); x++)
			this.write(0x00);
		for(CharacterBlock c : characters)
			pointerOneCount += c.getPointerCount();
		
		ArrayList<Integer> attackIndexes = this.getBonusIndexes(true);
		ArrayList<Integer> guardIndexes = this.getBonusIndexes(false);
		if(bonusStart > this.length())
		{
			this.seek(this.length());
			while(bonusStart > this.length())
				this.writeByte(0x0);
		}
		this.seek(bonusStart);
		for(int i : attackIndexes)
		{
			fileSize += 0x14;
			characters.get(i).setAttackPointer((int) this.getFilePointer());
			this.writeIntArray(characters.get(i).getAttackBonuses());
		}
		for(int i : guardIndexes)
		{
			fileSize += 0x24;
			characters.get(i).setGuardPointer((int) this.getFilePointer());
			this.writeIntArray(characters.get(i).getGuardBonuses());
		}
		for(CharacterBlock c : characters)
		{
			c.setAttackPointer(characters.get(c.getAttackPointerIndex()).getAttackPointer());
			c.setGuardPointer(characters.get(c.getGuardPointerIndex()).getGuardPointer());
		}
		fileSize += 0x4;
		pointerOneStart = fileSize;
		pointerTwoStart = pointerOneStart + pointerOneCount * 4;
		labelStart = pointerTwoStart + characters.size() * 0x8;
		
		if(labelStart > this.length())
		{
			this.seek(this.length());
			while(bonusStart > this.length())
				this.writeByte(0x0);
		}
		
		this.seek(0x4);
		this.writeLittleInt(pointerOneStart - 0x20);
		this.writeLittleInt(pointerOneCount);
		this.writeLittleInt(characters.size());
		
		this.seek(labelStart);
		for(CharacterBlock c : characters)
			this.writeString(c.getPid());
		this.writeString(fileName);
		for(CharacterBlock c : characters)
		{
			if(!this.containsLabel(c.getFid()))
				this.writeString(c.getFid());
			if(!this.containsLabel(c.getAid()))
				this.writeString(c.getAid());
			if(!this.containsLabel(c.getmPid()))
				this.writeString(c.getmPid());
			if(!this.containsLabel(c.getmPidH()))
				this.writeString(c.getmPidH());
			if(!this.containsLabel(c.getCombatMusic()))
				this.writeString(c.getCombatMusic());
			if(!this.containsLabel(c.getEnemyVoice()))
				this.writeString(c.getEnemyVoice());
		}
		this.writeByte(0x0);
		
		this.seek(0x20);
		this.writeLittleInt(this.indexOfLabel(fileName) - 0x20);
		this.writeLittleShort((short) characters.size());
		this.writeLittleShort((short) 0x7A);
		
		int lastWrittenPointerOne = pointerOneStart;
		for(CharacterBlock c : characters)
		{
			int blockStart = 0x30 + 0x98 * characters.indexOf(c);
			this.seek(blockStart);
			c.write(this);
			
			this.seek(lastWrittenPointerOne);
			this.writeLittleInt(blockStart - 0x4);
			this.writeLittleInt(blockStart);
			lastWrittenPointerOne += 0x8;
			
			this.seek(pointerTwoStart + 0x8 * characters.indexOf(c));
			this.writeLittleInt(blockStart - 0x20);
			this.writeLittleInt(this.indexOfLabel(c.getPid()) - labelStart);
		}
		this.seek(lastWrittenPointerOne);
		this.writeLittleInt(0x0);
		for(CharacterBlock c : characters)
		{
			int blockStart = 0x30 + 0x98 * characters.indexOf(c);
			if(!c.getPid().equals(""))
			{
				this.writeLittleInt(blockStart - 0x18);
			}
			if(!c.getFid().equals(""))
			{
				this.writeLittleInt(blockStart - 0x14);
			}
			if(!c.getAid().equals(""))
			{
				this.writeLittleInt(blockStart - 0x10);
			}
			if(!c.getmPid().equals(""))
			{
				this.writeLittleInt(blockStart - 0xC);
			}
			if(!c.getmPidH().equals(""))
			{
				this.writeLittleInt(blockStart - 0x8);
			}
			if(!c.getCombatMusic().equals(""))
			{
				this.writeLittleInt(blockStart + 104);
			}
			if(!c.getEnemyVoice().equals(""))
			{
				this.writeLittleInt(blockStart + 108);
			}
		}
		this.seek(0x0);
		this.writeLittleInt((int) this.length());
	}
	
	public ArrayList<CharacterBlock> getCharacters()
	{
		return characters;
	}
	
	public ArrayList<Integer> getBonusIndexes(boolean attackBonus)
	{
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		for(int x = 0; x < characters.size(); x++)
		{
			if(bonusDoesNotExist(indexes, characters.get(x), attackBonus))
				indexes.add(x);
		}
		return indexes;
	}
	
	public boolean bonusDoesNotExist(ArrayList<Integer> indexes, CharacterBlock c, boolean attackBonus)
	{
		for(int i : indexes)
		{
			if(attackBonus)
			{
				if(Arrays.equals(characters.get(i).getAttackBonuses(), c.getAttackBonuses()))
				{
					c.setAttackPointerIndex(i);
					return false;	
				}
				else
					c.setAttackPointerIndex(characters.indexOf(c));
			}
			else
			{
				if(Arrays.equals(characters.get(i).getGuardBonuses(), c.getGuardBonuses()))
				{
					c.setGuardPointerIndex(i);
					return false;	
				}
				else
					c.setGuardPointerIndex(characters.indexOf(c));
			}
		}
		return true;
	}
	
	public byte[] getBonusRegion() throws IOException
	{
		int originalLocation = (int) this.getFilePointer();
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		this.seek(bonusStart);
		while(this.getFilePointer() < pointerOneStart - 0x4)
			bytes.add(readByte());
		this.seek(originalLocation);
		return IOUtils.byteListToArray(bytes);
	}
	
	public int indexOfLabel(String input) throws IOException
    {
    	byte[] bytes = input.getBytes("shift-jis");
    	ArrayList<Byte> temp = new ArrayList<Byte>();
    	temp.add((byte) 0x0);
    	for(byte b : bytes)
    		temp.add(b);
    	temp.add((byte) 0x0);
    	bytes = IOUtils.byteListToArray(temp);
    	int originalLocation = (int) this.getFilePointer();
    	int index = IOUtils.search(bytes, this.toByteArray());
    	this.seek(originalLocation);
    	return index + 1;
    }
	
	public int getBonusStart()
	{
		return bonusStart;
	}
}