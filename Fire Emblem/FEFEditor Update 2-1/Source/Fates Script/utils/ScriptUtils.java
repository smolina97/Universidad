package utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import main.structures.Decompiler;

public class ScriptUtils 
{
	public static String[] parseEventParameters(String input)
	{
		if(input.contains("("))
			input = input.substring(input.indexOf('(') + 1, input.length() - 1);
		ArrayList<String> preliminaryResults = new ArrayList<String>();
		while(input.contains(","))
		{
			String nextParam;
			if(input.startsWith("string"))
				nextParam = input.substring(0, input.indexOf("\")") + 1);
			else
				nextParam = input.substring(0, input.indexOf(')') + 1);	
			input = input.substring(input.indexOf(",") + 1);
			preliminaryResults.add(nextParam);
		}
		preliminaryResults.add(input);
		return preliminaryResults.toArray(new String[preliminaryResults.size()]);
	}
	
	public static String[] parseEmbeddedParameters(String input)
	{
		if(input.contains("("))
			input = input.substring(input.indexOf('(') + 1, input.length() - 1);
		String[] result = input.split(",");
		return result;
	}
	
	public static String parseSingleParameter(String input)
	{
		if(input.startsWith("string") || input.startsWith("routine"))
			return ScriptUtils.parseStringParameter(input);
		if(input.contains("("))
			input = input.substring(input.indexOf('(') + 1, input.length() - 1);
		return input;
	}
	
	public static String parseStringParameter(String input)
	{
		if(!(input.startsWith("string") || input.startsWith("routine")))
			return "";
		else
		{
			input = input.replace("string", "");
			input = input.replace("routine", "");
			input = input.substring(1, input.length() - 1);
			if(input.startsWith("\""))
				input = input.replace("\"", "");
			return input;
		}
	}
	
	public static short shortFromByteArray(byte[] input, int index)
	{
		byte[] bytes = new byte[2];
        int x = 0;
        while (x < 2) {
            bytes[x] = input[index + x];
            x++;
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();
	}
	
	public static String readCoordBytes(byte[] input, int index)
	{
		String result = "";
		for(int x = 0; x < 4; x++)
		{
			result += Long.toHexString(input[index + x] & 0xFF);
			if(x < 3)
				result += ",";
		}
		return result;
	}
	
	public static String buildByteLine(ArrayList<Byte> input)
	{
		String output = "raw(";
		for(Byte b : input)
		{
			if(input.indexOf(b) != input.size() - 1)
				output += Integer.toHexString(b & 0xFF) + ",";
			else
				output += Integer.toHexString(b & 0xFF);
		}
		input.clear();
		return (output + ")");	
	}
	
	public static byte[] getSearchBytes(String input) throws UnsupportedEncodingException
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
	
	public static byte[] byteArrayFromList(ArrayList<Byte> input)
	{
		byte[] output = new byte[input.size()];
		for(int x = 0; x < input.size(); x++)
			output[x] = input.get(x);
		return output;
	}
	
	public static byte[] byteArrayFromList(ArrayList<Byte> input, int startIndex)
	{
		byte[] output = new byte[input.size() - startIndex];
		for(int x = startIndex; x < input.size(); x++)
			output[x] = input.get(x);
		return output;
	}
	
	public static boolean isPointer(int value, int reference, Decompiler decompiler) throws IOException
	{
		int original = (int) decompiler.getFilePointer();
		if(decompiler.getFileName().contains("_Terrain") && (value == 0))
			return true;
		if(value < 2)
			return false;
		if((value + reference) >= decompiler.length())
			return false;
		decompiler.seek(reference + value - 1);
		byte temp = decompiler.readByte();
		decompiler.seek(original);
		if(temp != 0)
			return false;
		return true;
	}
	
	public static int indexOf(byte[] outerArray, byte[] smallerArray) {
	    for(int i = 0; i < outerArray.length - smallerArray.length+1; ++i) {
	        boolean found = true;
	        for(int j = 0; j < smallerArray.length; ++j) {
	           if (outerArray[i+j] != smallerArray[j]) {
	               found = false;
	               break;
	           }
	        }
	        if (found) return i;
	     }
	   return -1;  
	}
	
	public static void writeShortToList(ArrayList<Byte> input, short value)
	{
		byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
				.putShort(value).array();
		for(byte b : bytes)
			input.add(b);
	}
	
	public static void writeShortToList(ArrayList<Byte> input, short value, int index)
	{
		byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
				.putShort(value).array();
		for(int x = 0; x < 2; x++)
			input.set(index + x, bytes[x]);
	}
}
