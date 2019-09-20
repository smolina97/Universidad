package main.decompilers;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import main.MainData;
import main.structures.Decompiler;
import utils.ScriptUtils;

public class EventDecompiler 
{
	private final byte[] INTEGERVALS = { 0x01, 0x07, 0x19, 0x1A, 0x1F };
	private final byte[] CONDITIONALVALS = { 0x49, 0x4C, 0x4D, 0x4B };
	private final byte[] STRINGVALS = { 0x1C, 0x1D };
	private final byte[] RAWVALS = { 0x46, 0x03 };
	private final byte EVENTBYTE = 0x47;
	
	private ArrayList<String> decompiled = new ArrayList<String>();
	private ArrayList<SimpleEntry<String, Integer>> parameters = new ArrayList<SimpleEntry<String, Integer>>();
	private Hashtable<Integer, Integer> savedPoints = new Hashtable<Integer, Integer>();
	private Stack<Integer> conditionalIndexes = new Stack<Integer>();
	private Stack<Integer> conditionalLengths = new Stack<Integer>();
	private Stack<Boolean> specialCaseStack = new Stack<Boolean>();
	private int nests = 0;
	private int lineNumber;
	private boolean omitFlag = false;
	private int reductionNumber = -1; // Special case for B027.
	
	public EventDecompiler()
	{
		
	}
	
	public ArrayList<String> parseEventString(byte[] input, Decompiler decompiler, int line)
	{
		lineNumber = line;
		for(int x = 0; x < input.length; x++)
		{
			SimpleEntry<String, Integer> command = null;
			processConditionals(input, x);
			if(omitFlag)
			{
				decompiled.add(getWhiteSpace() + "omit");
				omitFlag = false;
			}
			if(input[x] == EVENTBYTE)
			{
				command = parseEventCommand(input, x, decompiler);
				parameters.clear();
				savedPoints.put(x, decompiled.size());
				decompiled.add(getWhiteSpace() + command.getKey());
			}
			for(byte b : RAWVALS)
			{
				if(command != null)
					break;
				if(input[x] == b)
				{
					command = parseKnownRaw(input, x);
					parameters.add(new SimpleEntry<String, Integer>(command.getKey(), x));
					savedPoints.put(x, decompiled.size());
					break;
				}
			}
			for(byte b : INTEGERVALS)
			{
				if(command != null)
					break;
				if(input[x] == b)
				{
					command = parseIntegerValue(input, x);
					parameters.add(new SimpleEntry<String, Integer>(command.getKey(), x));
					savedPoints.put(x, decompiled.size());
					break;
				}
			}
			for(byte b : STRINGVALS)
			{
				if(command != null)
					break;
				if(input[x] == b)
				{
					command = parseString(input, x, decompiler);
					parameters.add(new SimpleEntry<String, Integer>(command.getKey(), x));
					savedPoints.put(x, decompiled.size());
					break;
				}
			}
			for(byte b : CONDITIONALVALS)
			{
				if(command != null)
					break;
				if(input[x] == b)
				{
					if(x + 2 < input.length)
					{
						short val = ScriptUtils.shortFromByteArray(input, x + 1);
						if(val < 0)
						{
							dumpParameters();
							savedPoints.put(x, decompiled.size());
							command = parseReturn(input, x);
							decompiled.add(getWhiteSpace() + command.getKey());
							break;
						}
					}
					dumpParameters();
					savedPoints.put(x, decompiled.size());
					command = parseCondition(input, x);
					decompiled.add(getWhiteSpace() + command.getKey());
					nests++;
					break;
				}
			}
			if(command == null) // If no known byte sequence is found, pool unknown bytes into their own parameter.
			{
				command = parseRaw(input, x);
				parameters.add(new SimpleEntry<String, Integer>(command.getKey(), x));
				savedPoints.put(x, decompiled.size());
			}
			x += command.getValue();
		}
		
		processConditionals(input, input.length);
		nests = 0;
		conditionalLengths.clear();
		conditionalIndexes.clear();
		savedPoints.clear();
		dumpParameters();
		decompiled.add("end");
		lineNumber = 0;
		return decompiled;
	}
	
	public SimpleEntry<String, Integer> parseCondition(byte[] input, int index)
	{
		String result = "";
		int advance = 2;
		int length = (int) ScriptUtils.shortFromByteArray(input, index + 1);
		if(input[index] == 0x49)
			result += "fail {";
		else if(input[index] == 0x4C)
			result += "pass {";
		else if(input[index] == 0x4D)
			result += "specialCheck {";
		else if(input[index] == 0x4B)
			result += "unknownCheck {";
		SimpleEntry<Boolean, Integer> endData = hasEndConditional(input);
		conditionalLengths.push(length);
		conditionalIndexes.push(index + 1);
		SimpleEntry<Boolean, Integer> newEndData = hasEndConditional(input);
		if(endData.getKey() && newEndData.getKey()) // Special case scenario for A028 and B028.
		{
			if(endData.getValue().intValue() == newEndData.getValue().intValue() && length == 5)
			{
				conditionalLengths.pop();
				conditionalIndexes.pop();
				conditionalLengths.push(length - 3);
				conditionalIndexes.push(index + 1);
				specialCaseStack.push(true);
				return new SimpleEntry<String, Integer>(result, advance);	
			}
		}
		specialCaseStack.push(false);
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public SimpleEntry<String, Integer> parseEventCommand(byte[] input, int index, Decompiler decompiler)
	{
		String result = ValueReader.parseEventString(input, index + 1, decompiler);
		int advance = 2;
		try
		{
			if(MainData.getInstance().getTags().containsKey(result))
				advance += MainData.getInstance().getTags().get(result).length;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Build event line from parsed information.
		result += "(";
		for(int x = 0; x < parameters.size(); x++)
		{
			result += parameters.get(x).getKey();
			if(x < parameters.size() - 1)
				result += ",";
		}
		result += ")";
		
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public static SimpleEntry<String, Integer> parseIntegerValue(byte[] input, int index)
	{
		String result = "";
		int advance = 0;
		byte command = input[index];
		
		if(command == 0x01 || command == 0x07 || command == 0x19)
			advance = 1;
		else if(command == 0x1A)
			advance = 2;
		else if(command == 0x1F)
			advance = 4;
		if(command == 0x01)
			result += "call(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
		else if(command == 0x07)
			result += "storein(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
		else if(command == 0x19)
			result += "byte(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
		else if(command == 0x1A)
			result += "short(" + Long.toHexString(ScriptUtils.shortFromByteArray(input, index + 1)) + ")";
		else if(command == 0x1F)
			result += "coord(" + ScriptUtils.readCoordBytes(input, index + 1) + ")";
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public static SimpleEntry<String, Integer> parseString(byte[] input, int index, Decompiler decompiler)
	{
		String result = "";
		int advance = 0;
		byte command = input[index];
		if(command == 0x1C)
		{
			result += ValueReader.parseShortString(input, index + 1, decompiler);
			advance = 1;
		}
		else if(command == 0x1D)
		{
			result += ValueReader.parseFullString(input, index + 1, decompiler);
			advance += 2;
		}
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public SimpleEntry<String, Integer> parseKnownRaw(byte[] input, int index)
	{
		String result = "";
		int advance = 0;
		byte command = input[index];
		if(command == 0x46)
		{
			result += parseRaw(input, index, 2).getKey();
			advance = 1;
		}
		else if(command == 0x03)
		{
			if(input[index + 1] != 0x47)
			{
				result += parseRaw(input, index, 2).getKey();
				advance = 1;
			}
			else
			{
				result += parseRaw(input, index, 1).getKey();
				advance = 0;	
			}
		}
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public SimpleEntry<String, Integer> parseRaw(byte[] input, int index)
	{
		String result = "";
		int advance = 0;
		ArrayList<Byte> raw = new ArrayList<Byte>();
		while(!isKnownByte(input[index + advance]))
		{
			raw.add(input[index + advance]);
			advance++;
			if(index + advance >= input.length)
				break;
			if(conditionalLengths.size() > 0 && conditionalIndexes.size() > 0)
			{
				if(index + advance >= conditionalLengths.peek() + conditionalIndexes.peek())
					break;
			}
		}
		advance--;
		
		result += "raw(";
		for(int x = 0; x < raw.size(); x++)
		{
			result += Integer.toHexString(raw.get(x) & 0xFF);
			if(x < raw.size() - 1)
				result += ",";
			else
				result += ")";
		}
		
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	public SimpleEntry<String, Integer> parseRaw(byte[] input, int index, int length)
	{
		String result = "";
		int advance = length - 1;
		ArrayList<Byte> raw = new ArrayList<Byte>();
		for(int x = 0; x < length; x++)
			raw.add(input[index + x]);
		result += "raw(";
		for(int x = 0; x < raw.size(); x++)
		{
			result += Integer.toHexString(raw.get(x) & 0xFF);
			if(x < raw.size() - 1)
				result += ",";
			else
				result += ")";
		}
		
		return new SimpleEntry<String, Integer>(result, advance);
	}
	
	private boolean isKnownByte(byte input)
	{
		boolean isKnownByte = false;
		if(input == EVENTBYTE)
			return true;
		for(byte b : INTEGERVALS)
		{
			if(input == b)
				isKnownByte = true;
		}
		for(byte b : CONDITIONALVALS)
		{
			if(input == b)
				isKnownByte = true;
		}
		for(byte b : STRINGVALS)
		{
			if(input == b)
				isKnownByte = true;
		}
		return isKnownByte;
	}
	
	private void dumpParameters()
	{
		String result;
		for(SimpleEntry<String, Integer> s : parameters)
		{
			int index = s.getValue();
			savedPoints.replace(index, decompiled.size());
			result = getWhiteSpace() + s.getKey();
			decompiled.add(result);
		}
		parameters.clear();
	}
	
	private SimpleEntry<String, Integer> parseReturn(byte[] input, int index)
	{
		String result = "";
		int advance = 2;
		int returnPoint = (ScriptUtils.shortFromByteArray(input, index + 1) + 3) * -1; // Return markers will always use a negative value.
		if(savedPoints.containsKey(index - returnPoint))
		{
			int point = savedPoints.get(index - returnPoint);
			int line = point + lineNumber;
			
			//Special case fix for A002_OP1 bev file.
			if(input[index - returnPoint] != 0x47 && (decompiled.get(point).startsWith("ev::") || decompiled.get(point).startsWith("bev::")))
			{
				return this.parseRaw(input, index, 3);
			}
			
			result = "goto("  + line + ")";
			return new SimpleEntry<String, Integer>(result, advance);
		}
		else
		{
			return this.parseRaw(input, index, 3);
		}
	}
	
	private void processConditionals(byte[] input, int currentIndex)
	{
		if(conditionalLengths.size() > 0 && conditionalIndexes.size() > 0) // If the stack size is 0 we don't need to worry about adding in spaces.
		{
			int currentConditionalLength = conditionalLengths.peek();
			int currentConditionalIndex = conditionalIndexes.peek();
			SimpleEntry<Boolean, Integer> endData = hasEndConditional(input);
			if(endData.getKey() && endData.getValue() == currentIndex)
			{
				breakNest(input);
			}
			else if((currentConditionalIndex + currentConditionalLength) <= currentIndex)
			{
				if(currentIndex + 3 < input.length) // Fix for P006.
				{
					if(input[currentIndex] == 0x49 && input[currentIndex + 0x3] == 0x49)
					{
						short value = ScriptUtils.shortFromByteArray(input, currentIndex + 1);
						if(value > 0)
							omitFlag = true;
					}
				}
				breakNest(input);
				if(conditionalLengths.size() > 0) // Fix for B027 and B028.
				{
					int nextConditionalLength = conditionalLengths.pop();
					int nextConditionalIndex = conditionalIndexes.pop();
					int thirdConditionalLength = -1;
					int thirdConditionalIndex = -1;
					if(reductionNumber != -1)
					{
						currentConditionalLength += reductionNumber;
						reductionNumber = -1;
					}
					int extraLength = (currentConditionalLength + currentConditionalIndex) - (nextConditionalIndex + nextConditionalLength);
					if(extraLength > 0)
					{
						decompiled.add(getWhiteSpace() + "reduce(" + Long.toHexString(extraLength) + ")");
						
						if(conditionalLengths.size() > 0)
						{
							thirdConditionalLength = conditionalLengths.peek();
							thirdConditionalIndex = conditionalIndexes.peek();
							extraLength = (currentConditionalLength + currentConditionalIndex) - (thirdConditionalIndex + thirdConditionalLength);
							if(extraLength > 0)
							{
								reductionNumber = extraLength;
							}
						}
					}
					conditionalLengths.push(nextConditionalLength);
					conditionalIndexes.push(nextConditionalIndex);
				}
				processConditionals(input, currentIndex);
			}
		}
	}
	
	private SimpleEntry<Boolean, Integer> hasEndConditional(byte[] input)
	{
		if(conditionalLengths.size() > 0 && conditionalIndexes.size() > 0)
		{
			int currentConditionalLength = conditionalLengths.peek();
			int currentConditionalIndex = conditionalIndexes.peek();
			int checkIndex = currentConditionalLength + currentConditionalIndex - 3;
			if(checkIndex < input.length)
			{
				for(byte b : CONDITIONALVALS)
				{
					if(ScriptUtils.shortFromByteArray(input, checkIndex + 1) < 0)
						return new SimpleEntry<Boolean, Integer>(false, checkIndex); // The ending is a goto.
					if(input[checkIndex] == b)
						return new SimpleEntry<Boolean, Integer>(true, checkIndex); // Ends in an else statement.
				}	
			}	
		}
		return new SimpleEntry<Boolean, Integer>(false, -1);
	}
	
	private String getWhiteSpace()
	{
		String result = "";
		for(int x = 0; x < nests; x++)
			result += "    ";
		return result;
	}
	
	private String breakNest(byte[] input)
	{
		String result = "";
		boolean specialCase = specialCaseStack.peek();
		if(specialCase)
		{
			decompiled.add(getWhiteSpace() + "followFailure");
		}
		conditionalLengths.pop();
		conditionalIndexes.pop();
		specialCase = specialCaseStack.pop();
		dumpParameters();
		nests--;
		decompiled.add(getWhiteSpace() + "}");
		return result;
	}
}