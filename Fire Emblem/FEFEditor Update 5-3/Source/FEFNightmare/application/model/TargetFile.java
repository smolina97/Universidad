/*
 *  Nightmare 2.0 - General purpose file editor
 *
 *  Copyright (C) 2009 Hextator,
 *  hectorofchad (AIM) hectatorofchad@sbcglobal.net (MSN)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3
 *  as published by the Free Software Foundation
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  <Description> Wrapper for the file to be edited by modules.
 *  Cannot be instantiated.
 */

package application.model;

import application.data.PrefsSingleton;
import application.utils.CompressionUtils;
import application.utils.dsdecmp.JavaDSDecmp;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class TargetFile {
	private static boolean open = false;
	private static File originalFile;
	private static byte[] data;

	private TargetFile() {}

	public static boolean isOpen() {
		return open;
	}

	public static int size() {
		return data.length;
	}

	private static void verifyOpenFile() {
		if (!open)
			throw new RuntimeException("no file open");
	}

	static AccessType calculateAccessType(int significand) {
		int shiftAmt = 3;
		final int byteChk = 1 << shiftAmt;
		final int halfChk = 2 << shiftAmt;
		final int wordChk = 4 << shiftAmt;
		if (
			significand % wordChk == 0
		)
			return AccessType.WORD;
		else if (
			significand % halfChk == 0
		)
			return AccessType.HALF;
		else if (
			significand % byteChk == 0
		)
			return AccessType.BYTE;
		else
			return AccessType.BIT;
	}

	// The following six functions are for performing variable endian
	// data accesses on the file data; accesses supported are
	// 8, 16 and 32 bit read and write
	// All are tested and working
	public static byte getByte(int address) {
		verifyOpenFile();
		return data[address];
	}

	public static short getShort(int address) {
		verifyOpenFile();
		short tempShort;
		tempShort = (short) ((data[address + 1] & 0xFF) << 0x8);
		tempShort |= data[address] & 0xFF;
		return tempShort;
	}

	public static int getInt(int address) {
		verifyOpenFile();
		int tempInt;
		tempInt = (data[address + 3] & 0xFF) << 0x18;
		tempInt |= (data[address + 2] & 0xFF) << 0x10;
		tempInt |= (data[address + 1] & 0xFF) << 0x8;
		tempInt |= data[address] & 0xFF;
		return tempInt;
	}

	public static void putByte(int address, byte input) {
		verifyOpenFile();
		data[address] = input;
	}

	public static void putShort(int address, short input) {
		verifyOpenFile();
		data[address + 1] = (byte) ((int) input >> 0x8);
		data[address] = (byte) ((int) input & 0xFF);
	}

	public static void putInt(int address, int input) {
		verifyOpenFile();
		data[address + 3] = (byte) (input >> 0x18);
		data[address + 2] =
				(byte) ((input >> 0x10) & 0xFF);
		data[address + 1] =
				(byte) ((input >> 0x8) & 0xFF);
		data[address] = (byte) (input & 0xFF);
	}

	public static void putBytes(int address, byte[] values) {
		System.arraycopy(values, 0, data, address, values.length);
	}

	public static byte[] getBytes(int address, int length) {
		verifyOpenFile();
		byte[] output = new byte[length];
		System.arraycopy(data, address, output, 0, length);
		return output;
	}

	public static byte[] getData() {
		verifyOpenFile();
		return data;
	}

	public static void setData(byte[] bytes) {
		verifyOpenFile();
		data = bytes;
	}

	public static String getString(int index) throws UnsupportedEncodingException {
		int end = index;
		if(end > data.length)
			return "";
		while(data[end] != 0)
			end++;
		return new String(Arrays.copyOfRange(data, index, end), "shift-jis");
	}

	public static void open(File input) {
		// Either the file fails to load and nothing is there to save,
		// or the file loads successfully and thus has no changes
		// to it
		originalFile = input;

		// Sanity check
		if (input == null || !input.exists()) {
			open = false;
			throw new IllegalArgumentException("invalid file");
		}

		// Actually read the data
		try {
			data = Files.readAllBytes(input.toPath());

			// Decompress LZ files.
			if(input.getName().endsWith(".lz") && PrefsSingleton.getInstance().isCompressionEnabled())
				data = CompressionUtils.decompress(data);
		} catch (IOException e) {
			open = false;
			throw new IllegalArgumentException("stream error");
		}
		open = true;
	}

	public static void save(File where) {
		verifyOpenFile();

		// Compress for LZ files.
		byte[] dataToWrite = data;
		if(where.getName().endsWith(".lz"))
			dataToWrite = JavaDSDecmp.compressLZ11(data);

		// Actually write the data
		try {
			Files.write(where.toPath(), dataToWrite);
		} catch (IOException e) {
			throw new IllegalArgumentException("stream error");
		}
	}

	public static void save() {
		verifyOpenFile();

		// Sanity check
		if (originalFile == null || !originalFile.exists()) {
			return;
		}

		// Compress for LZ files.
		byte[] dataToWrite = data;
		if(originalFile.getName().endsWith(".lz"))
			dataToWrite = JavaDSDecmp.compressLZ11(data);

		// Actually write the data
		FileOutputStream outputFile;
		try {
			outputFile = new FileOutputStream(originalFile);
			outputFile.write(dataToWrite);
		} catch (IOException e) {
			throw new IllegalArgumentException("stream error");
		}
		try {
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeFile() {
		open = false;
		data = null;
	}
}
