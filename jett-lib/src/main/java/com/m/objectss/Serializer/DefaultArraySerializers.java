
package com.m.objectss.Serializer;


import com.m.objectss.Jett;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;




public class DefaultArraySerializers
{

	static public class ByteArraySerializer extends DefaultSerializers.DefaultSerialiser<byte[]>
	{


		public void write (Jett kryo, Output output, byte[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeBytes(object);
		}

		public byte[] read (Jett kryo, Input input, Class<byte[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readBytes(length - 1);
		}

		public byte[] copy (Jett kryo, byte[] original) {
			byte[] copy = new byte[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class IntArraySerializer extends DefaultSerializers.DefaultSerialiser<int[]> {


		public void write (Jett kryo, Output output, int[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeInts(object, false);
		}

		public int[] read (Jett kryo, Input input, Class<int[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readInts(length - 1, false);
		}

		public int[] copy (Jett kryo, int[] original) {
			int[] copy = new int[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class FloatArraySerializer extends DefaultSerializers.DefaultSerialiser<float[]> {


		public void write (Jett kryo, Output output, float[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeFloats(object);
		}

		public float[] read (Jett kryo, Input input, Class<float[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readFloats(length - 1);
		}

		public float[] copy (Jett kryo, float[] original) {
			float[] copy = new float[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class LongArraySerializer extends DefaultSerializers.DefaultSerialiser<long[]> {


		public void write (Jett kryo, Output output, long[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeLongs(object, false);
		}

		public long[] read (Jett kryo, Input input, Class<long[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readLongs(length - 1, false);
		}

		public long[] copy (Jett kryo, long[] original) {
			long[] copy = new long[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class ShortArraySerializer extends DefaultSerializers.DefaultSerialiser<short[]> {


		public void write (Jett kryo, Output output, short[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeShorts(object);
		}

		public short[] read (Jett kryo, Input input, Class<short[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readShorts(length - 1);
		}

		public short[] copy (Jett kryo, short[] original) {
			short[] copy = new short[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class CharArraySerializer extends DefaultSerializers.DefaultSerialiser<char[]> {


		public void write (Jett kryo, Output output, char[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeChars(object);
		}

		public char[] read (Jett kryo, Input input, Class<char[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readChars(length - 1);
		}

		public char[] copy (Jett kryo, char[] original) {
			char[] copy = new char[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class DoubleArraySerializer extends DefaultSerializers.DefaultSerialiser<double[]> {


		public void write (Jett kryo, Output output, double[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeDoubles(object);
		}

		public double[] read (Jett kryo, Input input, Class<double[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			return input.readDoubles(length - 1);
		}

		public double[] copy (Jett kryo, double[] original) {
			double[] copy = new double[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class BooleanArraySerializer extends DefaultSerializers.DefaultSerialiser<boolean[]> {


		public void write (Jett kryo, Output output, boolean[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			for (int i = 0, n = object.length; i < n; i++)
				output.writeBoolean(object[i]);
		}

		public boolean[] read (Jett kryo, Input input, Class<boolean[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			boolean[] array = new boolean[--length];
			for (int i = 0; i < length; i++)
				array[i] = input.readBoolean();
			return array;
		}

		public boolean[] copy (Jett kryo, boolean[] original) {
			boolean[] copy = new boolean[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class StringArraySerializer extends DefaultSerializers.DefaultSerialiser<String[]> {


		public void write (Jett kryo, Output output, String[] object) {
			if (object == null) {
				output.writeVarInt(Jett.NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			{
				for (int i = 0, n = object.length; i < n; i++)
					output.writeString(object[i]);
			}
		}

		public String[] read (Jett kryo, Input input, Class<String[]> type) {
			int length = input.readVarInt(true);
			if (length == Jett.NULL) return null;
			String[] array = new String[--length];
			{
				for (int i = 0; i < length; i++)
					array[i] = input.readString();
			}
			return array;
		}

		public String[] copy (Jett kryo, String[] original) {
			String[] copy = new String[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}


}
