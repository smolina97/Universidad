package main.decompilers;

import main.MainData;
import main.structures.Decompiler;
import utils.ScriptUtils;

public class ValueReader 
{
	public static String parseShortString(byte[] input, int index, Decompiler decompiler)
	{
		String result = "";
		try
		{
			decompiler.seek(input[index] + decompiler.getReferenceOffset());
			String s = decompiler.parseString();
			if(s.equals(""))
				result = "string(\"null\")";
			else
			{
				if(MainData.getInstance().isTranslate() && MainData.getInstance().getJapaneseToEnglish().containsKey(s))
					s = MainData.getInstance().getJapaneseToEnglish().get(s);
				result = "string(\"" + s + "\")";
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	public static String parseFullString(byte[] input, int index, Decompiler decompiler)
	{
		String result = "";
		try
		{
			decompiler.seek(ScriptUtils.shortFromByteArray(input, index) + decompiler.getReferenceOffset());
			String s = decompiler.parseString();
			if(s.equals(""))
				result = "string(\"null\")";
			else
			{
				if(MainData.getInstance().isTranslate() && MainData.getInstance().getJapaneseToEnglish().containsKey(s))
					s = MainData.getInstance().getJapaneseToEnglish().get(s);
				result = "string(\"" + s + "\")";
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
	
	public static String parseEventString(byte[] input, int index, Decompiler decompiler)
	{
		String result = "";
		try
		{
			decompiler.seek(ScriptUtils.shortFromByteArray(input, index) + decompiler.getReferenceOffset());
			String s = decompiler.parseString();
			if(MainData.getInstance().isTranslate() && MainData.getInstance().getJapaneseToEnglish().containsKey(s))
				s = MainData.getInstance().getJapaneseToEnglish().get(s);
			result = s;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}
}
