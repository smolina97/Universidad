package fefeditor.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ArrayUtils 
{
	public static long toInteger(byte[] raw)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(raw);
		return buffer.getLong(0);
	}
	
	public static byte[] toByteArray(int a)
	{
		final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
	    bb.order(ByteOrder.LITTLE_ENDIAN);
	    bb.putInt(a);
	    return bb.array();
	}

	public static byte[] toByteArray(short a)
	{
		final ByteBuffer bb = ByteBuffer.allocate(2);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putShort(a);
		return bb.array();
	}
}
