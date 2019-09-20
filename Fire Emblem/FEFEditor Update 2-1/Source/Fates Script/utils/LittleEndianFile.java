package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LittleEndianFile
extends RandomAccessFile {
	private String fileName;
	
    public LittleEndianFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
        fileName = file.getName();
    }

    public void writeLittleInt(int input) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(input).array();
        reverseEndian(bytes);
        for(int x = 0; x < bytes.length; x++) 
            this.write(bytes[x]);
    }

    public int readLittleInt() throws IOException {
        byte[] bytes = new byte[4];
        int x = 0;
        while (x < 4) {
            bytes[x] = this.readByte();
            x++;
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public void writeLittleShort(short input) throws IOException
    {
    	byte[] bytes = ByteBuffer.allocate(2).putShort(input).array();
    	reverseEndian(bytes);
        for(int x = 0; x < bytes.length; x++) 
            this.write(bytes[x]);
    }
    
    public byte[] leIntToByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }
    
    public short readLittleShort() throws IOException {
        byte[] bytes = new byte[2];
        int x = 0;
        while (x < 2) {
            bytes[x] = this.readByte();
            x++;
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    
    public String readLabelFromPointer() throws IOException
    {
    	int offset = this.readLittleInt();
    	if(offset <= 0)
    		return "";
    	int original = (int) this.getFilePointer();
    	this.seek(offset);
    	String parsedString = this.parseString();
    	this.seek(original);
    	return parsedString;
    }
    
    public int indexOfLabel(String input) throws IOException
    {
    	byte[] bytes = input.getBytes("shift-jis");
    	ArrayList<Byte> temp = new ArrayList<Byte>();
    	temp.add((byte) 0x0);
    	for(byte b : bytes)
    		temp.add(b);
    	temp.add((byte) 0x0);
    	bytes = this.getByteArray(temp);
    	int originalLocation = (int) this.getFilePointer();
    	int index = this.search(bytes, this.getFileBytes());
    	this.seek(originalLocation);
    	return index + 1;
    }
    
    public void writeString(String input) throws Exception
    {
    	if(input.length() > 0)
    	{
    		byte[] bytes = input.getBytes("shift-jis");
        	for(int x = 0; x < bytes.length; x++)
        		this.writeByte(bytes[x]);
        	this.writeByte(0x00);	
    	}
    }
    
    public void writeShortArray(short[] input) throws IOException
    {
    	for(short s : input)
    		writeLittleShort(s);
    }
    
    public void writeByteArray(byte[] input) throws IOException
    {
    	for(byte b : input)
    		writeByte(b);
    }
    
    public void writeIntArray(int[] input) throws IOException
    {
    	for(int i : input)
    		writeLittleInt(i);
    }
    
    public byte[] readByteArray(int length) throws IOException
    {
    	byte[] bytes = new byte[length];
    	for(int x = 0; x < bytes.length; x++)
    		bytes[x] = this.readByte();
    	return bytes;
    }
    
    public short[] readShortArray(int length) throws IOException
    {
    	short[] shorts = new short[length];
    	for(int x = 0; x < shorts.length; x++)
    		shorts[x] = this.readLittleShort();
    	return shorts;
    }
    
    public int[] readIntArray(int length) throws IOException
    {
    	int[] ints = new int[length];
    	for(int x = 0; x < ints.length; x++)
    		ints[x] = this.readLittleInt();
    	return ints;
    }

    public String parseString() throws IOException {
        byte temp;
        ArrayList<Byte> bytes = new ArrayList<Byte>();
        while ((temp = this.readByte()) != 0) {
            bytes.add(Byte.valueOf(temp));
        }
        return new String(this.getByteArray(bytes), "shift-jis");
    }

    public byte[] getByteArray(List<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];
        int index = 0;
        while (index < byteList.size()) {
            byteArray[index] = byteList.get(index).byteValue();
            ++index;
        }
        return byteArray;
    }

    public void seekToLabel(String s) throws Exception {
        byte[] bytes = s.getBytes("shift-jis");
        this.seek(this.search(bytes, this.getFileBytes()));
    }

    public boolean containsLabel(String s) throws Exception {
        byte[] bytes = s.getBytes("shift-jis");
        int labelOffset = this.search(bytes, this.getFileBytes());
        return labelOffset != -1;
    }

    public byte[] getFileBytes() throws IOException {
        byte[] fileBytes = new byte[(int)this.length()];
        int originalLocation = (int) this.getFilePointer();
        this.seek(0);
        this.readFully(fileBytes);
        this.seek(originalLocation);
        return fileBytes;
    }
    
    public byte[] getFileBytes(int startPosition) throws IOException {
        byte[] fileBytes = new byte[(int) (this.length() - startPosition)];
        this.seek(startPosition);
        for(int x = 0; x < fileBytes.length; x++)
        	fileBytes[x] = readByte();
        return fileBytes;
    }

    public int search(byte[] searchedFor, byte[] input) {
        Object[] searchedForB = new Byte[searchedFor.length];
        int x = 0;
        while (x < searchedFor.length) {
            searchedForB[x] = Byte.valueOf(searchedFor[x]);
            ++x;
        }
        int idx = -1;
        ArrayDeque<Byte> q = new ArrayDeque<Byte>(input.length);
        int i = 0;
        while (i < input.length) {
            if (q.size() == searchedForB.length) {
                Object[] cur = q.toArray(new Byte[0]);
                if (Arrays.equals(cur, searchedForB)) {
                    idx = i - searchedForB.length;
                    break;
                }
                q.pop();
                q.addLast(Byte.valueOf(input[i]));
            } else {
                q.addLast(Byte.valueOf(input[i]));
            }
            ++i;
        }
        return idx;
    }

    public byte[] reverseEndian(byte[] input) {
        int i = 0;
        while (i < input.length / 2) {
            byte temp = input[i];
            input[i] = input[input.length - i - 1];
            input[input.length - i - 1] = temp;
            ++i;
        }
        return input;
    }
    
    //Might only replace one sequence, so it could use modification.
    public byte[] replaceBytePattern(byte[] input, byte[] pattern, byte[] replacement)
    {
    	if (pattern.length == 0)
            return input;
    	
    	ArrayList<Byte> result = new ArrayList<Byte>();
    	int temp;
    	for (temp = 0; temp <= input.length - pattern.length; temp++)
        {
            boolean foundMatch = true;
            for (int j = 0; j < pattern.length; j++)
            {
                if (input[temp + j] != pattern[j])
                {
                    foundMatch = false;
                    break;
                }
            }

            if (foundMatch)
            {
                result.addAll(this.toByteList(replacement));
                temp += pattern.length - 1;
            }
            else
            {
                result.add(input[temp]);
            }
        }
    	for (; temp < input.length; temp++ )
        {
            result.add(input[temp]);
        }
    	return this.getByteArray(result);
    }
    
    public ArrayList<Byte> toByteList(byte[] input)
    {
    	ArrayList<Byte> result = new ArrayList<Byte>();
    	for(int x = 0; x < input.length; x++)
    		result.add(input[x]);
    	return result;
    }
    
    public void findPointerTo(int input) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(input -= 32).array();
        int i = 0;
        while (i < bytes.length / 2) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
            ++i;
        }
        if(this.search(bytes, this.getFileBytes()) != -1)
        	this.seek(this.search(bytes, this.getFileBytes()));
    }
    
    public boolean containsPointerTo(int input) throws IOException
    {
    	byte[] bytes = ByteBuffer.allocate(4).putInt(input -= 32).array();
        int i = 0;
        while (i < bytes.length / 2) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
            ++i;
        }
        if(this.search(bytes, this.getFileBytes()) != -1)
        	return true;
        else
        	return false;
    }
    
    public void addLabel(String s) throws IOException {
        byte[] bytes = s.getBytes("shift-jis");
        this.seek(this.length());
        byte[] arrby = bytes;
        int n = arrby.length;
        int n2 = 0;
        while (n2 < n) {
            byte b = arrby[n2];
            this.write(b);
            ++n2;
        }
        this.write(0);
    }
    
    public void replaceLabel(String original, String replacement) throws IOException
    {
    	byte[] originalBytes = original.getBytes("shift-jis");
    	byte[] replacementBytes = replacement.getBytes("shift-jis");
    	if(originalBytes.length < replacementBytes.length)
    		return;	
    	int startIndex = this.search(originalBytes, this.getFileBytes());
    	if(startIndex == -1)
    		return;
    	this.seek(startIndex - 1);
    	if(this.readByte() != 0x00)
    		return;
    	for(int x = 0; x < replacementBytes.length; x++)
    		this.writeByte(replacementBytes[x]);
    	while(this.getFilePointer() < startIndex + originalBytes.length)
    		this.writeByte(0x00);
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}

