/**
 * @file    kyLinkPackage.java
 * @author  kyChu
 * @version V1.0.0
 * @date    2017/08/24
 * @brief   Communication package structure.
 */
package kyLink;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import kyLink.CRC.CalculateCRC;

public class kyLinkPackage implements Cloneable {
	/* file data cache size */
	public static final int MAIN_DATA_CACHE = 80;

	public static final byte kySTX1 = (byte)0x55;
	public static final byte kySTX2 = (byte)0xAA;

	/* -------- Heart-beat type -------- */
	public static final byte TYPE_LINK_HEARTBEAT = (byte)0x01;
	public static final byte TYPE_LINKRE_VER_Req = (byte)0x02;
	public static final byte TYPE_LINKER_VER_Resp = (byte)0x03;
	public static final byte TYPE_LINKER_NAME_Req = (byte)0x04;

	public static final String[] DataTypes = {"uint8_t", "uint16_t", "uint32_t", "float", "double", "uint64_t"};

	public byte stx1;
	public byte stx2;
	public byte dev_id;
	public byte msg_id;
	public short length;
	public byte[] rData;
	public short crc;

	public kyLinkPackage() {
		stx1 = kySTX1;
		stx2 = kySTX2;
		dev_id = 0;
		msg_id = 0;
		length = 0;
		rData = new byte[MAIN_DATA_CACHE];
		crc = 0;
	}

	public void setLength(short len) {
		length = len;
	}

	public void addBytes(byte[] c, int len, int pos) {
		System.arraycopy(c, 0, rData, pos, len);
	}
	public void addByte(byte c, int pos) {
		rData[pos] = c;
	}
	public void addFloat(float f, int pos) {
		int d = Float.floatToRawIntBits(f);
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addInteger(int d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 0), (byte)(d >> 8), (byte)(d >> 16), (byte)(d >> 24)};
		addBytes(c, 4, pos);
	}
	public void addCharacter(char d, int pos) {
		byte[] c = new byte[]{(byte)(d >> 8), (byte)(d >> 0)};
		addBytes(c, 2, pos);
	}
	public float readoutFloat(int pos) {
		byte[] b = {rData[pos + 3], rData[pos + 2], rData[pos + 1], rData[pos + 0]};
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
		float f = 0.0f;
		try {
			f = dis.readFloat();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}
	public double readoutDouble(int pos) {
		byte[] b = {rData[pos + 7], rData[pos + 6], rData[pos + 5], rData[pos + 4], rData[pos + 3], rData[pos + 2], rData[pos + 1], rData[pos + 0]};
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(b));
		double d = 0.0;
		try {
			d = dis.readDouble();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return d;
	}
	public long readoutLong(int pos) {
		long c1 = (rData[pos] & 0xFF) | ((rData[pos + 1] << 8) & 0xFF00) | ((rData[pos + 2] << 24) >>> 8) | (rData[pos + 3] << 24);
		long c2 = (rData[pos + 4] & 0xFF) | ((rData[pos + 5] << 8) & 0xFF00) | ((rData[pos + 6] << 24) >>> 8) | (rData[pos + 7] << 24);
		long c = (c2 & 0xFFFFFFFF) << 32 | c1; 
		return c;
	}
	public int readoutInteger(int pos) {
		int c = (rData[pos] & 0xFF) | ((rData[pos + 1] << 8) & 0xFF00) | ((rData[pos + 2] << 24) >>> 8) | (rData[pos + 3] << 24);
		return c;
	}
	public char readoutCharacter(int pos) {
		char c = (char) (rData[pos] & 0xFF | ((rData[pos + 1] << 8) & 0xFF00));
		return c;
	}
	public String readoutString(int pos, int len) {
		byte[] c = new byte[len];
		System.arraycopy(rData, pos, c, 0, len);
		return new String(c);
	}

	public double readoutTypedData(String type, int pos) {
		if(type.equals("uint8_t")) {
			return (double)(rData[pos] & 0xFF);
		} else if(type.equals("uint16_t")) {
			return (double)(readoutCharacter(pos));
		} else if(type.equals("uint32_t")) {
			return (double)(readoutInteger(pos));
		} else if(type.equals("float")) {
			return (double)readoutFloat(pos);
		} else if(type.equalsIgnoreCase("uint64_t")) {
			return (double)readoutLong(pos);
		} else if(type.equalsIgnoreCase("double")) {
			return (double)readoutDouble(pos);
		} else {
			return (0.0);
		}
	}

	public byte[] getCRCBuffer() {
		byte[] c = new byte[length + 4];
		System.arraycopy(rData, 0, c, 4, length);
		c[0] = dev_id;
		c[1] = msg_id;
		c[2] = (byte)(length & 0xFF);
		c[3] = (byte)(length >> 8);
		return c;
	}

	public byte[] getSendBuffer() {
		byte[] c = new byte[length + 8];
		c[0] = stx1;
		c[1] = stx2;
		c[2] = dev_id;
		c[3] = msg_id;
		c[4] = (byte)(length & 0xFF);
		c[5] = (byte)(length >> 8);
		System.arraycopy(rData, 0, c, 6, length);
		short crc = ComputeCRC16();
		c[length + 6] = (byte)(crc & 0xFF);
		c[length + 7] = (byte)(crc >> 8);
		return c;
	}

	public short ComputeCRC16() {
		return CalculateCRC.ComputeCRC16(getCRCBuffer(), length + 4);
	}

	public Object PackageCopy() throws CloneNotSupportedException {
		return super.clone();
	} 
}

/**
 * @ End of file.
 */
