package main.structures;

import java.io.IOException;

public class HeaderInformation 
{
	private boolean hasSubheader;
	private boolean isRoutine = false;
	private boolean unknownCheck = false;
	private int eventType;
	private int eventIndex;
	private int identifier;
	private int unknown;
	private int eventOffset;
	private int subheaderOffset;
	
	public HeaderInformation()
	{
		
	}
	
	public void read(Decompiler decompiler) throws IOException
	{
		decompiler.seek(decompiler.getFilePointer() + 0x4);
		eventOffset = decompiler.readLittleInt();
		eventType = decompiler.readLittleInt();
		identifier = decompiler.readLittleInt();
		unknown = decompiler.readLittleInt();
		subheaderOffset = decompiler.readLittleInt();
		
		if(unknown == 0 && subheaderOffset == 0)
		{
			hasSubheader = false;
			unknownCheck = true;
		}
		else if(subheaderOffset == eventOffset && unknown == 0)
			hasSubheader = false;
		else if(unknown != 0)
		{
			hasSubheader = true;
			isRoutine = true;
		}
		else
			hasSubheader = true;
	}
	
	public String getHeaderText()
	{
		String result = "(";
		result += eventType + "," + hasSubheader + "," + unknownCheck + ")" + System.lineSeparator();
		return result;
	}

	public int getSubheaderOffset() {
		return subheaderOffset;
	}

	public void setSubheaderOffset(int subheaderOffset) {
		this.subheaderOffset = subheaderOffset;
	}

	public int getEventOffset() {
		return eventOffset;
	}

	public void setEventOffset(int eventOffset) {
		this.eventOffset = eventOffset;
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getEventIndex() {
		return eventIndex;
	}

	public void setEventIndex(int eventIndex) {
		this.eventIndex = eventIndex;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public boolean isHasSubheader() {
		return hasSubheader;
	}

	public void setHasSubheader(boolean hasSubheader) {
		this.hasSubheader = hasSubheader;
	}

	public int getIdentifier() {
		return identifier;
	}

	public boolean isRoutine() {
		return isRoutine;
	}

	public void setRoutine(boolean isRoutine) {
		this.isRoutine = isRoutine;
	}

	public boolean isUnknownCheck() {
		return unknownCheck;
	}

	public void setUnknownCheck(boolean unknownCheck) {
		this.unknownCheck = unknownCheck;
	}
}
