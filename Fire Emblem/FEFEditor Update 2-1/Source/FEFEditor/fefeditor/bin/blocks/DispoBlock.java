package fefeditor.bin.blocks;

import fefeditor.bin.formats.FatesDispoFile;
import fefeditor.common.io.BinFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DispoBlock 
{
	private String pid = "Placeholder";
	private String ac = "";
	private String aiPositionOne = "";
	private String mi = "";
	private String aiPositionTwo = "";
	private String at = "";
	private String aiPositionThree = "";
	private String mv = "";
	private String aiPositionFour = "";
	
	private byte team = 0;
	private byte level = 0;
	
	private int skillFlag = 0;
	
	private byte[] unknownOne = new byte[4];
	private byte[] secondCoord = new byte[2];
	private byte[] firstCoord = new byte[2];
	private byte[] unknownTwo = new byte[2];
	private byte[] spawnBitflags = new byte[4];
	private byte[][] itemBitflags = new byte[5][4];
	private byte[] unknownThree = new byte[0x18];
	
	private String[] items = new String[5];
	private String[] skills = new String[5];
	
	public DispoBlock()
	{
		for(int x = 0; x < 5; x++)
		{
			items[x] = "";
			skills[x] = "";
		}
	}

	public DispoBlock(String pid)
	{
		for(int x = 0; x < 5; x++)
		{
			items[x] = "";
			skills[x] = "";
		}
		this.pid = pid;
	}
	
	public void read(BinFile file) throws IOException
	{
		pid = file.readStringFromPointer();
		unknownOne = file.readByteArray(4);
		team = file.readByte();
		level = file.readByte();
		secondCoord = file.readByteArray(2);
		firstCoord = file.readByteArray(2);
		unknownTwo = file.readByteArray(2);
		spawnBitflags = file.readByteArray(4);

		for(int x = 0; x < 5; x++)
		{
			items[x] = file.readStringFromPointer();
			itemBitflags[x] = file.readByteArray(4);
		}
		for(int x = 0; x < 5; x++)
			skills[x] = file.readStringFromPointer();
		
		skillFlag = file.readLittleInt();
		ac = file.readStringFromPointer();
		aiPositionOne = file.readStringFromPointer();
		mi = file.readStringFromPointer();
		aiPositionTwo = file.readStringFromPointer();
		at = file.readStringFromPointer();
		aiPositionThree = file.readStringFromPointer();
		mv = file.readStringFromPointer();
		aiPositionFour = file.readStringFromPointer();
		unknownThree = file.readByteArray(0x18);
	}
	
	public void write(FatesDispoFile file, int StringStart) throws Exception
	{
		file.writeLittleInt(file.getStringOffset(pid, file, StringStart));
		file.writeByteArray(unknownOne);
		file.writeByte(team);
		file.writeByte(level);
		file.writeByteArray(secondCoord);
		file.writeByteArray(firstCoord);
		file.writeByteArray(unknownTwo);
		file.writeByteArray(spawnBitflags);
		
		for(int x = 0; x < 5; x++)
		{
			file.writeLittleInt(file.getStringOffset(items[x], file, StringStart));
			file.writeByteArray(itemBitflags[x]);
		}
		for(int x = 0; x < 5; x++)
		{
			file.writeLittleInt(file.getStringOffset(skills[x], file, StringStart));
		}
		file.writeLittleInt(skillFlag);
		file.writeLittleInt(file.getStringOffset(ac, file, StringStart));
		file.writeLittleInt(file.getStringOffset(aiPositionOne, file, StringStart));
		file.writeLittleInt(file.getStringOffset(mi, file, StringStart));
		file.writeLittleInt(file.getStringOffset(aiPositionTwo, file, StringStart));
		file.writeLittleInt(file.getStringOffset(at, file, StringStart));
		file.writeLittleInt(file.getStringOffset(aiPositionThree, file, StringStart));
		file.writeLittleInt(file.getStringOffset(mv, file, StringStart));
		file.writeLittleInt(file.getStringOffset(aiPositionFour, file, StringStart));
		file.writeByteArray(unknownThree);
	}
	
	public ArrayList<Integer> getPointers(int blockStart)
	{
		ArrayList<Integer> pointers = new ArrayList<Integer>();
		if(!pid.equals(""))
			pointers.add(blockStart - 0x20);
		for(int x = 0; x < 5; x++)
		{
			if(!items[x].equals(""))
				pointers.add(blockStart + 0x14 + 8 * x - 0x20);
			if(!skills[x].equals(""))
				pointers.add(blockStart + 0x3C + 4 * x - 0x20);
		}
		if(!ac.equals(""))
			pointers.add(blockStart + 0x54 - 0x20);
		if(!aiPositionOne.equals(""))
			pointers.add(blockStart + 0x58 - 0x20);
		if(!mi.equals(""))
			pointers.add(blockStart + 0x5C - 0x20);
		if(!aiPositionTwo.equals(""))
			pointers.add(blockStart + 0x60 - 0x20);
		if(!at.equals(""))
			pointers.add(blockStart + 0x64 - 0x20);
		if(!aiPositionThree.equals(""))
			pointers.add(blockStart + 0x68 - 0x20);
		if(!mv.equals(""))
			pointers.add(blockStart + 0x6C - 0x20);
		if(!aiPositionFour.equals(""))
			pointers.add(blockStart + 0x70 - 0x20);
		return pointers;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAc() {
		return ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getMi() {
		return mi;
	}

	public void setMi(String mi) {
		this.mi = mi;
	}

	public String getAt() {
		return at;
	}

	public void setAt(String at) {
		this.at = at;
	}

	public String getMv() {
		return mv;
	}

	public void setMv(String mv) {
		this.mv = mv;
	}

	public byte getTeam() {
		return team;
	}

	public void setTeam(byte team) {
		this.team = team;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public int getSkillFlag() {
		return skillFlag;
	}

	public void setSkillFlag(int skillFlag) {
		this.skillFlag = skillFlag;
	}

	public byte[] getUnknownOne() {
		return unknownOne;
	}

	public void setUnknownOne(byte[] unknownOne) {
		this.unknownOne = unknownOne;
	}

	public byte[] getSecondCoord() {
		return secondCoord;
	}

	public void setSecondCoord(byte[] secondCoord) {
		this.secondCoord = secondCoord;
	}

	public byte[] getFirstCoord() {
		return firstCoord;
	}

	public void setFirstCoord(byte[] firstCoord) {
		this.firstCoord = firstCoord;
	}

	public byte[] getUnknownTwo() {
		return unknownTwo;
	}

	public void setUnknownTwo(byte[] unknownTwo) {
		this.unknownTwo = unknownTwo;
	}

	public byte[] getSpawnBitflags() {
		return spawnBitflags;
	}

	public void setSpawnBitflags(byte[] spawnBitflags) {
		this.spawnBitflags = spawnBitflags;
	}

	public byte[][] getItemBitflags() {
		return itemBitflags;
	}

	public void setItemBitflags(byte[][] itemBitflags) {
		this.itemBitflags = itemBitflags;
	}

	public String[] getItems() {
		return items;
	}

	public void setItem(String item, int index) {
		this.items[index] = item;
	}

	public String[] getSkills() {
		return skills;
	}

	public void setSkill(String skill, int index) {
		this.skills[index] = skill;
	}

	public String getAiPositionOne() {
		return aiPositionOne;
	}

	public void setAiPositionOne(String aiPositionOne) {
		this.aiPositionOne = aiPositionOne;
	}

	public String getAiPositionTwo() {
		return aiPositionTwo;
	}

	public void setAiPositionTwo(String aiPositionTwo) {
		this.aiPositionTwo = aiPositionTwo;
	}

	public String getAiPositionThree() {
		return aiPositionThree;
	}

	public void setAiPositionThree(String aiPositionThree) {
		this.aiPositionThree = aiPositionThree;
	}

	public String getAiPositionFour() {
		return aiPositionFour;
	}

	public void setAiPositionFour(String aiPositionFour) {
		this.aiPositionFour = aiPositionFour;
	}
}