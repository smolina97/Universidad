package utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import main.MainData;
import main.structures.Decompiler;
import main.structures.ScriptCompiler;

public class SubheaderParser 
{	
	public static ArrayList<String> parseSubheader(Decompiler decompiler, int ending) throws Exception
	{
		ArrayList<String> decompiled = new ArrayList<String>();
		int parsedValue;
		while(decompiler.getFilePointer() < ending)
		{
			parsedValue = decompiler.readLittleInt();
			if(ScriptUtils.isPointer(parsedValue, decompiler.getReferenceOffset(), decompiler))
			{
				int original = (int) decompiler.getFilePointer();
				decompiler.seek(parsedValue + decompiler.getReferenceOffset());
				String s = decompiler.parseString();
				if(s.equals(""))
					decompiled.add("string(\"null\")");
				else
				{
					if(MainData.getInstance().isTranslate() && MainData.getInstance().getJapaneseToEnglish().containsKey(s))
					{
						s = MainData.getInstance().getJapaneseToEnglish().get(s);	
					}
					decompiled.add("string(\"" + s + "\")");
				}
				decompiler.seek(original);
			}
			else
				decompiled.add("int(" + Integer.toHexString(parsedValue) + ")");
		}
		decompiled.add("end");
		return decompiled;
	}
	
	public static ArrayList<String> parseKnownSubheader(Decompiler decompiler, int type) throws Exception
	{
		ArrayList<String> decompiled = new ArrayList<String>();
		Byte[] values = MainData.getInstance().getSubheaders().get(type);
		for(int x = 0; x < values.length; x++)
		{
			int parsedValue = decompiler.readLittleInt();
			if(values[x] == 0)
				decompiled.add("int(" + Integer.toHexString(parsedValue) + ")");
			else if(values[x] == 1)
			{
				int original = (int) decompiler.getFilePointer();
				decompiler.seek(parsedValue + decompiler.getReferenceOffset() - 1);
				byte check = decompiler.readByte();
				String s = decompiler.parseString();
				if(check != 0 || s.startsWith("ev::"))
				{
					decompiled.add("int(" + Integer.toHexString(parsedValue) + ")");
					decompiler.seek(original);
				}
				else
				{
					if(s.equals(""))
						decompiled.add("string(\"null\")");
					else
					{
						if(MainData.getInstance().isTranslate() && MainData.getInstance().getJapaneseToEnglish().containsKey(s))
						{
							s = MainData.getInstance().getJapaneseToEnglish().get(s);	
						}
						decompiled.add("string(\"" + s + "\")");
					}
					decompiler.seek(original);	
				}
			}
		}
		decompiled.add("end");
		return decompiled;
	}
	
	public static ArrayList<Byte> getSubheaderFromText(ScriptCompiler compiler, String[] input, int start) throws Exception
	{
		ArrayList<Byte> result = new ArrayList<Byte>();
		int x = start;
		while(!input[x].startsWith("end"))
		{
			if(input[x].startsWith("int"))
			{
				String parsed = ScriptUtils.parseSingleParameter(input[x]);
				if(parsed.toLowerCase().equals("ffffffff"))
				{
					for(int y = 0; y < 4; y++)
						result.add((byte) 0xFF);
				}
				else
				{
					int parsedInt = (int) Long.parseLong(parsed, 16);
					byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
							.putInt(parsedInt).array();
					for(byte b : bytes)
						result.add(b);	
				}
			}
			else if(input[x].startsWith("string"))
			{
				String s = ScriptUtils.parseSingleParameter(input[x]);
				if(MainData.getInstance().isTranslate() && MainData.getInstance().getEnglishToJapanese().containsKey(s) && !s.equals("null"))
					s = MainData.getInstance().getEnglishToJapanese().get(s);
				int offset;
				offset = compiler.getLabelOffset(s);
				byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
						.putInt(offset).array();
				for(byte b : bytes)
					result.add(b);
			}
			else if(input[x].startsWith("routine"))
			{
				String s = ScriptUtils.parseSingleParameter(input[x]);
				byte[] stringBytes = s.getBytes("shift-jis");
				for(byte b : stringBytes)
					result.add(b);
				result.add((byte) 0);
			}
			x++;
		}
		return result;
	}
	
	public static String parseSpecialSubheader(Decompiler decompiler) throws IOException
	{
		String result = "";
		result += "routine(\"" + decompiler.parseString() + "\")" + System.lineSeparator();
		result += "end" + System.lineSeparator();
		return result;
	}
}
