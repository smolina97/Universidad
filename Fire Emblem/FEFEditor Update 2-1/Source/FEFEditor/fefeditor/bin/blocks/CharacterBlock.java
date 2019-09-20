package fefeditor.bin.blocks;

import fefeditor.bin.formats.FatesPersonFile;
import fefeditor.common.io.BinFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CharacterBlock 
{
	private int attackPointerIndex;
	private int guardPointerIndex;
	private int attackPointer;
	private int guardPointer;
	
	private String pid = "";
	private String fid = "";
	private String aid = "";
	private String mPid = "";
	private String mPidH = "";
	private String combatMusic = "";
	private String enemyVoice = "";
	
	private byte baseLevel = 0;
	private byte internalLevel = 0;
	private byte enemyFlag = 0;
	private byte birthday = 0;
	private byte birthMonth = 0;
	private byte army = 0;
	private byte levelCap = 0;
	private byte amiibo = 0;
	
	private short identifier = 0;
	private short replacementIdentifier = 0;
	private short parentIdentifier = 0;
	private short parent = 0; //Used as ID if character is parent.
	private short child = 0;
	private short supportIndex = 0;
	private short supportIdentifier = 0;
	private short supportRoute = 0;
	
	private byte[] unknownBytes = new byte[3];
	private byte[] unknownLine = new byte[16];
	private byte[] skillFlags = new byte[2];
	private byte[] bitflags = new byte[8];
	private byte[] stats = new byte[8];
	private byte[] growths = new byte[8];
	private byte[] modifiers = new byte[8];
	private byte[] weaponRanks = new byte[8];
	
	private short[] personalSkills = new short[3];
	private short[] skills = new short[5];
	private short[] classes = new short[2];
	private short[] reclasses = new short[2];
	private short[] amiiboWeapons = new short[2];
	
	private int[] attackBonuses = new int[5];
	private int[] guardBonuses = new int[9];
	
	public CharacterBlock()
	{
		
	}
	
	public CharacterBlock(CharacterBlock c)
	{
		this.pid = c.getPid();
		this.fid = c.getFid();
		this.aid = c.getAid();
		this.mPid = c.getmPid();
		this.mPidH = c.getmPidH();
		this.combatMusic = c.getCombatMusic();
		this.enemyVoice = c.getEnemyVoice();
		
		this.baseLevel = c.getBaseLevel();
		this.internalLevel = c.getInternalLevel();
		this.enemyFlag = c.getEnemyFlag();
		this.birthday = c.getBirthday();
		this.birthMonth = c.getBirthMonth();
		this.army = c.getArmy();
		this.levelCap = c.getLevelCap();
		this.amiibo = c.getAmiibo();
		
		this.identifier = c.getIdentifier();
		this.replacementIdentifier = c.getReplacementIdentifier();
		this.parentIdentifier = c.getParentIdentifier();
		this.parent = c.getParent();
		this.child = c.getChild();
		this.supportIndex = c.getSupportIndex();
		this.supportIdentifier = c.getSupportIdentifier();
		this.supportRoute = c.getSupportRoute();
		
		this.unknownBytes = c.getUnknownBytes();
		this.unknownLine = c.getUnknownLine();
		this.skillFlags = c.getSkillFlags();
		this.bitflags = c.getBitflags();
		this.stats = c.getStats();
		this.growths = c.getGrowths();
		this.modifiers = c.getModifiers();
		this.weaponRanks = c.getWeaponRanks();
		
		this.personalSkills = c.getPersonalSkills();
		this.skills = c.getSkills();
		this.classes = c.getClasses();
		this.reclasses = c.getReclasses();
		this.amiiboWeapons = c.getAmiiboWeapons();
		
		this.attackBonuses = c.getAttackBonuses();
		this.guardBonuses = c.getGuardBonuses();
	}
	
	public void read(BinFile file) throws IOException
	{
		bitflags = file.readByteArray(8);
		pid = file.readStringFromPointer();
		fid = file.readStringFromPointer();
		aid = file.readStringFromPointer();
		mPid = file.readStringFromPointer();
		mPidH = file.readStringFromPointer();
		
		int originalLocation = (int) file.getFilePointer();
		file.seek(file.readLittleInt() + 0x20);
		attackBonuses = file.readIntArray(5);
		file.seek(originalLocation + 0x4);
		file.seek(file.readLittleInt() + 0x20);
		guardBonuses = file.readIntArray(9);
		file.seek(originalLocation + 0x8);
		
		identifier = file.readLittleShort();
		supportRoute = file.readByte();
		army = file.readByte();
		replacementIdentifier = file.readLittleShort();
		parentIdentifier = file.readLittleShort();
		classes = file.readShortArray(2);
		supportIdentifier = file.readLittleShort();
		baseLevel = file.readByte();
		internalLevel = file.readByte();
		enemyFlag = file.readByte();
		unknownBytes = file.readByteArray(3);
		
		stats = file.readByteArray(8);
		growths = file.readByteArray(8);
		modifiers = file.readByteArray(8);
		unknownLine = file.readByteArray(16);
		
		weaponRanks = file.readByteArray(8);
		skills = file.readShortArray(5);
		skillFlags = file.readByteArray(2);
		personalSkills = file.readShortArray(3);
		birthday = file.readByte();
		birthMonth = file.readByte();
		reclasses = file.readShortArray(2);
		parent = file.readLittleShort();
		child = file.readLittleShort();
		supportIndex = file.readLittleShort();
		levelCap = file.readByte();
		amiibo = file.readByte();
		combatMusic = file.readStringFromPointer();
		enemyVoice = file.readStringFromPointer();
		amiiboWeapons = file.readShortArray(2);
	}
	
	public void write(FatesPersonFile file) throws IOException
	{
		file.writeByteArray(bitflags);
		if(!pid.equals(""))
			file.writeLittleInt(file.indexOfLabel(pid) - 0x20);
		else
			file.writeLittleInt(0);
		if(!fid.equals(""))
			file.writeLittleInt(file.indexOfLabel(fid) - 0x20);
		else
			file.writeLittleInt(0);
		if(!aid.equals(""))
			file.writeLittleInt(file.indexOfLabel(aid) - 0x20);
		else
			file.writeLittleInt(0);
		if(!mPid.equals(""))
			file.writeLittleInt(file.indexOfLabel(mPid) - 0x20);
		else
			file.writeLittleInt(0);
		if(!mPidH.equals(""))
		{
			file.writeLittleInt(file.indexOfLabel(mPidH) - 0x20);	
		}
		else
		{
			file.writeLittleInt(0);	
		}
		file.writeLittleInt(this.getAttackPointer() - 0x20);
		file.writeLittleInt(this.getGuardPointer() - 0x20);
		
		file.writeLittleShort(identifier);
		file.write(supportRoute);
		file.write(army);
		file.writeLittleShort(replacementIdentifier);
		file.writeLittleShort(parentIdentifier);
		file.writeShortArray(classes);
		file.writeLittleShort(supportIdentifier);
		file.write(baseLevel);
		file.write(internalLevel);
		file.write(enemyFlag);
		file.writeByteArray(unknownBytes);
		file.writeByteArray(stats);
		file.writeByteArray(growths);
		file.writeByteArray(modifiers);
		file.writeByteArray(unknownLine);
		
		file.writeByteArray(weaponRanks);
		file.writeShortArray(skills);
		file.writeByteArray(skillFlags);
		file.writeShortArray(personalSkills);
		file.write(birthday);
		file.write(birthMonth);
		file.writeShortArray(reclasses);
		file.writeShort(parent);
		file.writeShort(child);
		file.writeShort(supportIndex);
		file.write(levelCap);
		file.write(amiibo);
		
		if(!combatMusic.equals(""))
			file.writeLittleInt(file.indexOfLabel(combatMusic) - 0x20);
		else
			file.writeLittleInt(0);
		if(!enemyVoice.equals(""))
			file.writeLittleInt(file.indexOfLabel(enemyVoice) - 0x20);
		else
			file.writeLittleInt(0);
		file.writeShortArray(amiiboWeapons);
	}
	
	public int getRequiredSpace() throws UnsupportedEncodingException
	{
		int space = 0;
		space += pid.getBytes("shift-jis").length;
		space += fid.getBytes("shift-jis").length;
		space += aid.getBytes("shift-jis").length;
		space += mPid.getBytes("shift-jis").length;
		space += mPidH.getBytes("shift-jis").length;
		space += combatMusic.getBytes("shift-jis").length;
		space += enemyVoice.getBytes("shift-jis").length;
		return space;
	}
	
	public int getPointerCount()
	{
		int count = 2;
		if(!pid.equals(""))
			count++;
		if(!fid.equals(""))
			count++;
		if(!aid.equals(""))
			count++;
		if(!mPid.equals(""))
			count++;
		if(!mPidH.equals(""))
			count++;
		if(!combatMusic.equals(""))
			count++;
		if(!enemyVoice.equals(""))
			count++;
		return count;
	}

	public int getAttackBonus(int i)
	{
		return attackBonuses[i];
	}
	
	public void setAttackBonus(int i, int val)
	{
		attackBonuses[i] = val;
	}
	
	public int[] getAttackBonuses()
	{
		return attackBonuses;
	}
	
	public int getGuardBonus(int i)
	{
		return guardBonuses[i];
	}
	
	public void setGuardBonus(int i, int val)
	{
		guardBonuses[i] = val;
	}
	
	public int[] getGuardBonuses()
	{
		return guardBonuses;
	}
	
	public short getReclass(int i)
	{
		return reclasses[i];
	}
	
	public void setReclass(int i, short val)
	{
		reclasses[i] = val;
	}
	
	public short getAmiiboWeapons(int i)
	{
		return amiiboWeapons[i];
	}
	
	public void setAmiiboWeapons(int i, short val)
	{
		amiiboWeapons[i] = val;
	}
	
	public short getClass(int i)
	{
		return classes[i];
	}
	
	public void setClass(int i, short val)
	{
		classes[i] = val;
	}
	
	public short getSkill(int i)
	{
		return skills[i];
	}
	
	public void setSkill(int i, short val)
	{
		skills[i] = val;
	}
	
	public short getPersonalSkill(int i)
	{
		return personalSkills[i];
	}
	
	public void setPersonalSkill(int i, short val)
	{
		personalSkills[i] = val;
	}
	
	public byte getWeaponRank(int i)
	{
		return weaponRanks[i];
	}
	
	public void setWeaponRank(int i, byte val)
	{
		weaponRanks[i] = val;
	}
	
	public byte getModifier(int i)
	{
		return modifiers[i];
	}
	
	public void setModifier(int i, byte val)
	{
		modifiers[i] = val;
	}
	
	public byte getSkillFlag(int i)
	{
		return skillFlags[i];
	}
	
	public void setSkillFlag(int i, byte val)
	{
		skillFlags[i] = val;
	}
	
	public byte getBitflag(int i)
	{
		return bitflags[i];
	}
	
	public void setBitflag(int i, byte val)
	{
		bitflags[i] = val;
	}
	
	public byte getStat(int i)
	{
		return stats[i];
	}
	
	public byte[] getUnknownBytes() {
		return unknownBytes;
	}

	public byte[] getUnknownLine() {
		return unknownLine;
	}

	public byte[] getSkillFlags() {
		return skillFlags;
	}

	public byte[] getBitflags() {
		return bitflags;
	}

	public byte[] getStats() {
		return stats;
	}

	public byte[] getGrowths() {
		return growths;
	}

	public byte[] getModifiers() {
		return modifiers;
	}

	public byte[] getWeaponRanks() {
		return weaponRanks;
	}

	public short[] getPersonalSkills() {
		return personalSkills;
	}

	public short[] getSkills() {
		return skills;
	}

	public short[] getClasses() {
		return classes;
	}

	public short[] getReclasses() {
		return reclasses;
	}

	public short[] getAmiiboWeapons() {
		return amiiboWeapons;
	}

	public void setStat(int i, byte val)
	{
		stats[i] = val;
	}
	
	public byte getGrowth(int i)
	{
		return growths[i];
	}
	
	public void setGrowth(int i, byte val)
	{
		growths[i] = val;
	}
	
	public byte getUnknownByte(int i)
	{
		return unknownBytes[i];
	}
	
	public void setUnknownByte(int i, byte val)
	{
		unknownBytes[i] = val;
	}
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getmPid() {
		return mPid;
	}

	public void setmPid(String mPid) {
		this.mPid = mPid;
	}

	public String getmPidH() {
		return mPidH;
	}

	public void setmPidH(String mPidH) {
		this.mPidH = mPidH;
	}

	public byte getBaseLevel() {
		return baseLevel;
	}

	public void setBaseLevel(byte baseLevel) {
		this.baseLevel = baseLevel;
	}

	public byte getInternalLevel() {
		return internalLevel;
	}

	public void setInternalLevel(byte internalLevel) {
		this.internalLevel = internalLevel;
	}

	public byte getEnemyFlag() {
		return enemyFlag;
	}

	public void setEnemyFlag(byte enemyFlag) {
		this.enemyFlag = enemyFlag;
	}

	public byte getBirthday() {
		return birthday;
	}

	public void setBirthday(byte birthday) {
		this.birthday = birthday;
	}

	public byte getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(byte birthMonth) {
		this.birthMonth = birthMonth;
	}

	public short getIdentifier() {
		return identifier;
	}

	public void setIdentifier(short identifier) {
		this.identifier = identifier;
	}

	public short getParentIdentifier() {
		return parentIdentifier;
	}

	public void setParentIdentifier(short parentIdentifier) {
		this.parentIdentifier = parentIdentifier;
	}

	public short getSupportIdentifier() {
		return supportIdentifier;
	}

	public void setSupportIdentifier(short supportIdentifier) {
		this.supportIdentifier = supportIdentifier;
	}

	public short getSupportRoute() {
		return supportRoute;
	}

	public void setSupportRoute(short supportRoute) {
		this.supportRoute = supportRoute;
	}

	public byte getArmy() {
		return army;
	}

	public void setArmy(byte army) {
		this.army = army;
	}

	public short getReplacementIdentifier() {
		return replacementIdentifier;
	}

	public void setReplacementIdentifier(short replacementIdentifier) {
		this.replacementIdentifier = replacementIdentifier;
	}

	public short getParent() {
		return parent;
	}

	public void setParent(short parent) {
		this.parent = parent;
	}

	public short getChild() {
		return child;
	}

	public void setChild(short child) {
		this.child = child;
	}

	public short getSupportIndex() {
		return supportIndex;
	}

	public void setSupportIndex(short supportIndex) {
		this.supportIndex = supportIndex;
	}

	public byte getLevelCap() {
		return levelCap;
	}

	public void setLevelCap(byte levelCap) {
		this.levelCap = levelCap;
	}

	public byte getAmiibo() {
		return amiibo;
	}

	public void setAmiibo(byte amiibo) {
		this.amiibo = amiibo;
	}

	public String getCombatMusic() {
		return combatMusic;
	}

	public void setCombatMusic(String combatMusic) {
		this.combatMusic = combatMusic;
	}

	public String getEnemyVoice() {
		return enemyVoice;
	}

	public void setEnemyVoice(String enemyVoice) {
		this.enemyVoice = enemyVoice;
	}
	
	public int getAttackPointerIndex() {
		return attackPointerIndex;
	}

	public void setAttackPointerIndex(int attackPointerIndex) {
		this.attackPointerIndex = attackPointerIndex;
	}

	public int getGuardPointerIndex() {
		return guardPointerIndex;
	}

	public void setGuardPointerIndex(int guardPointerIndex) {
		this.guardPointerIndex = guardPointerIndex;
	}

	public int getAttackPointer() {
		return attackPointer;
	}

	public void setAttackPointer(int attackPointer) {
		this.attackPointer = attackPointer;
	}

	public int getGuardPointer() {
		return guardPointer;
	}

	public void setGuardPointer(int guardPointer) {
		this.guardPointer = guardPointer;
	}
}