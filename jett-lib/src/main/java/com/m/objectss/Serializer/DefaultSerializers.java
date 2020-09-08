
package com.m.objectss.Serializer;

import com.m.objectss.ClassResoverManager;
import com.m.objectss.Jett;
import com.m.objectss.JettException;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;


import static com.m.objectss.Jett.NULL;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 默认的系列化器
 */
public class DefaultSerializers {

	static abstract class DefaultSerialiser<T> implements ISerializer<T>
	{

	}

	//空对象解析器
	public static class NullSerializer implements ISerializer<ClassResoverManager.NullType>
	{
		@Override
		public void write(Jett kryo, Output output, ClassResoverManager.NullType object)
		{

		}

		@Override
		public ClassResoverManager.NullType read(Jett kryo, Input input, Class<ClassResoverManager.NullType> type)
		{
			return null;
		}
	}

	static public class VoidSerializer extends DefaultSerialiser {
	

		public void write (Jett jett, Output output, Object object) {

		}

		public Object read (Jett jett, Input input, Class type) {
			return null;
		}
	}

	static public class BooleanSerializer extends DefaultSerialiser<Boolean> {
		

		public void write (Jett jett, Output output, Boolean object) {
			output.writeBoolean(object);
		}

		public Boolean read (Jett jett, Input input, Class<Boolean> type) {
			return input.readBoolean();
		}
	}

	static public class ByteSerializer extends DefaultSerialiser<Byte> {
		

		public void write (Jett jett, Output output, Byte object) {
			output.writeByte(object);
		}

		public Byte read (Jett jett, Input input, Class<Byte> type) {
			return input.readByte();
		}
	}

	static public class CharSerializer extends DefaultSerialiser<Character> {
		

		public void write (Jett jett, Output output, Character object) {
			output.writeChar(object);
		}

		public Character read (Jett jett, Input input, Class<Character> type) {
			return input.readChar();
		}
	}

	static public class ShortSerializer extends DefaultSerialiser<Short> {
		

		public void write (Jett jett, Output output, Short object) {
			output.writeShort(object);
		}

		public Short read (Jett jett, Input input, Class<Short> type) {
			return input.readShort();
		}
	}

	static public class IntSerializer extends DefaultSerialiser<Integer> {
		

		public void write (Jett jett, Output output, Integer object) {
			output.writeInt(object, false);
		}

		public Integer read (Jett jett, Input input, Class<Integer> type) {
			return input.readInt(false);
		}
	}

	static public class LongSerializer extends DefaultSerialiser<Long> {
		

		public void write (Jett jett, Output output, Long object) {
			output.writeLong(object, false);
		}

		public Long read (Jett jett, Input input, Class<Long> type) {
			return input.readLong(false);
		}
	}

	static public class FloatSerializer extends DefaultSerialiser<Float> {
		

		public void write (Jett jett, Output output, Float object) {
			output.writeFloat(object);
		}

		public Float read (Jett jett, Input input, Class<Float> type) {
			return input.readFloat();
		}
	}

	static public class DoubleSerializer extends DefaultSerialiser<Double> {
		

		public void write (Jett jett, Output output, Double object) {
			output.writeDouble(object);
		}

		public Double read (Jett jett, Input input, Class<Double> type) {
			return input.readDouble();
		}
	}

	/** @see Output#writeString(String) */
	static public class StringSerializer extends DefaultSerialiser<String> {
		

		public void write (Jett jett, Output output, String object) {
			output.writeString(object);
		}

		public String read (Jett jett, Input input, Class<String> type) {
			return input.readString();
		}
	}

	/** Serializer for {@link BigInteger} and any subclass.
	 * @author Tumi <serverperformance@gmail.com> (enhacements) */
	static public class BigIntegerSerializer extends DefaultSerialiser<BigInteger> {
		

		public void write (Jett jett, Output output, BigInteger object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			BigInteger value = (BigInteger)object;
			// fast-path optimizations for BigInteger.ZERO constant
			if (value == BigInteger.ZERO) {
				output.writeVarInt(2, true);
				output.writeByte(0);
				return;
			}
			// default behaviour
			byte[] bytes = value.toByteArray();
			output.writeVarInt(bytes.length + 1, true);
			output.writeBytes(bytes);
		}

		public BigInteger read (Jett jett, Input input, Class<BigInteger> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			byte[] bytes = input.readBytes(length - 1);
			if (type != BigInteger.class && type != null) {
				// For subclasses, use reflection
				try {
					Constructor<BigInteger> constructor = type.getConstructor(byte[].class);
					if (!constructor.isAccessible()) {
						try {
							constructor.setAccessible(true);
						} catch (SecurityException se) {
						}
					}
					return constructor.newInstance(bytes);
				} catch (Exception ex) {
					throw new JettException(ex);
				}
			}
			if (length == 2) {
				// fast-path optimizations for BigInteger constants
				switch (bytes[0]) {
				case 0:
					return BigInteger.ZERO;
				case 1:
					return BigInteger.ONE;
				case 10:
					return BigInteger.TEN;
				}
			}
			return new BigInteger(bytes);
		}
	}

	/** Serializer for {@link BigDecimal} and any subclass.
	 * @author Tumi <serverperformance@gmail.com> (enhacements) */
	static public class BigDecimalSerializer extends DefaultSerialiser<BigDecimal> {
		private final BigIntegerSerializer bigIntegerSerializer = new BigIntegerSerializer();


		public void write (Jett jett, Output output, BigDecimal object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			BigDecimal value = (BigDecimal)object;
			// fast-path optimizations for BigDecimal constants
			if (value == BigDecimal.ZERO) {
				bigIntegerSerializer.write(jett, output, BigInteger.ZERO);
				output.writeInt(0, false); // for backwards compatibility
				return;
			}
			// default behaviour
			bigIntegerSerializer.write(jett, output, value.unscaledValue());
			output.writeInt(value.scale(), false);
		}

		public BigDecimal read (Jett jett, Input input, Class<BigDecimal> type) {
			BigInteger unscaledValue = bigIntegerSerializer.read(jett, input, BigInteger.class);
			if (unscaledValue == null) return null;
			int scale = input.readInt(false);
			if (type != BigDecimal.class && type != null) {
				// For subclasses, use reflection
				try {
					Constructor<BigDecimal> constructor = type.getConstructor(BigInteger.class, int.class);
					if (!constructor.isAccessible()) {
						try {
							constructor.setAccessible(true);
						} catch (SecurityException se) {
						}
					}
					return constructor.newInstance(unscaledValue, scale);
				} catch (Exception ex) {
					throw new JettException(ex);
				}
			}
			// fast-path optimizations for BigDecimal constants
			if (unscaledValue == BigInteger.ZERO && scale == 0) {
				return BigDecimal.ZERO;
			}
			// default behaviour
			return new BigDecimal(unscaledValue, scale);
		}
	}


	/** Serializer for {@link Date}, {@link java.sql.Date}, {@link Time}, {@link Timestamp} and any other subclass.
	 * @author Tumi <serverperformance@gmail.com> */
	static public class DateSerializer extends DefaultSerialiser<Date> {
		private Date create (Jett jett, Class<? extends Date> type, long time) throws JettException {
			if (type == Date.class || type == null) {
				return new Date(time);
			}
			if (type == Timestamp.class) {
				return new Timestamp(time);
			}
			if (type == java.sql.Date.class) {
				return new java.sql.Date(time);
			}
			if (type == Time.class) {
				return new Time(time);
			}
			// other cases, reflection
			try {
				// Try to avoid invoking the no-args constructor
				// (which is expected to initialize the instance with the current time)
				Constructor<? extends Date> constructor = type.getConstructor(long.class);
				if (!constructor.isAccessible()) {
					try {
						constructor.setAccessible(true);
					} catch (SecurityException se) {
					}
				}
				return constructor.newInstance(time);
			} catch (Exception ex) {
				// default strategy
				Date d = (Date)jett.newInstance(type);
				d.setTime(time);
				return d;
			}
		}

		public void write (Jett jett, Output output, Date object) {
			output.writeLong(object.getTime(), true);
		}

		public Date read (Jett jett, Input input, Class<Date> type) {
			return create(jett, type, input.readLong(true));
		}

		public Date copy (Jett jett, Date original) {
			return create(jett, original.getClass(), original.getTime());
		}
	}

	static public class EnumSerializer extends DefaultSerialiser<Enum> {


		private Object[] enumConstants;

		public EnumSerializer (Class<? extends Enum> type) {
			enumConstants = type.getEnumConstants();
			// We allow the serialization of the (abstract!) Enum.class (instead of an actual "user" enum),
			// which also creates an EnumSerializer instance during Jett.writeClass with the following trace:
			// ClassSerializer.write -> Jett.writeClass -> DefaultClassResolver.writeClass
			//  -> Jett.getDefaultSerializer -> ReflectionSerializerFactory.makeSerializer(jett, EnumSerializer, Enum.class)
			// This EnumSerializer instance is expected to be never called for write/read.
			if (enumConstants == null && !Enum.class.equals(type)) throw new IllegalArgumentException("The type must be an enum: " + type);
		}

		public void write (Jett jett, Output output, Enum object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.ordinal() + 1, true);
		}

		public Enum read (Jett jett, Input input, Class<Enum> type) {
			int ordinal = input.readVarInt(true);
			if (ordinal == NULL) return null;
			ordinal--;
			if (ordinal < 0 || ordinal > enumConstants.length - 1)
				throw new JettException("Invalid ordinal for enum \"" + type.getName() + "\": " + ordinal);
			Object constant = enumConstants[ordinal];
			return (Enum)constant;
		}
	}



	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CurrencySerializer extends DefaultSerialiser<Currency> {

		public void write (Jett jett, Output output, Currency object) {
			output.writeString(object == null ? null : object.getCurrencyCode());
		}

		public Currency read (Jett jett, Input input, Class<Currency> type) {
			String currencyCode = input.readString();
			if (currencyCode == null) return null;
			return Currency.getInstance(currencyCode);
		}
	}

	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class StringBufferSerializer extends DefaultSerialiser<StringBuffer> {

		public void write (Jett jett, Output output, StringBuffer object) {
			output.writeString(object);
		}

		public StringBuffer read (Jett jett, Input input, Class<StringBuffer> type) {
			String value = input.readString();
			if (value == null) return null;
			return new StringBuffer(value);
		}

		public StringBuffer copy (Jett jett, StringBuffer original) {
			return new StringBuffer(original);
		}
	}

	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class StringBuilderSerializer extends DefaultSerialiser<StringBuilder> {

		public void write (Jett jett, Output output, StringBuilder object) {
			output.writeString(object);
		}

		public StringBuilder read (Jett jett, Input input, Class<StringBuilder> type) {
			return input.readStringBuilder();
		}

		public StringBuilder copy (Jett jett, StringBuilder original) {
			return new StringBuilder(original);
		}
	}



	/** Serializer for {@link TimeZone}. Assumes the timezones are immutable.
	 * @author Tumi <serverperformance@gmail.com> */
	static public class TimeZoneSerializer extends DefaultSerialiser<TimeZone> {
		

		public void write (Jett jett, Output output, TimeZone object) {
			output.writeString(object.getID());
		}

		public TimeZone read (Jett jett, Input input, Class<TimeZone> type) {
			return TimeZone.getTimeZone(input.readString());
		}
	}

	/** Serializer for {@link GregorianCalendar}, java.util.JapaneseImperialCalendar, and sun.util.BuddhistCalendar.
	 * @author Tumi <serverperformance@gmail.com> */
	static public class CalendarSerializer extends DefaultSerialiser<Calendar> {
		// The default value of gregorianCutover.
		static private final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;

		TimeZoneSerializer timeZoneSerializer = new TimeZoneSerializer();

		public void write (Jett jett, Output output, Calendar object) {
			timeZoneSerializer.write(jett, output, object.getTimeZone()); // can't be null
			output.writeLong(object.getTimeInMillis(), true);
			output.writeBoolean(object.isLenient());
			output.writeInt(object.getFirstDayOfWeek(), true);
			output.writeInt(object.getMinimalDaysInFirstWeek(), true);
			if (object instanceof GregorianCalendar)
				output.writeLong(((GregorianCalendar)object).getGregorianChange().getTime(), false);
			else
				output.writeLong(DEFAULT_GREGORIAN_CUTOVER, false);
		}

		public Calendar read (Jett jett, Input input, Class<Calendar> type) {
			Calendar result = Calendar.getInstance(timeZoneSerializer.read(jett, input, TimeZone.class));
			result.setTimeInMillis(input.readLong(true));
			result.setLenient(input.readBoolean());
			result.setFirstDayOfWeek(input.readInt(true));
			result.setMinimalDaysInFirstWeek(input.readInt(true));
			long gregorianChange = input.readLong(false);
			if (gregorianChange != DEFAULT_GREGORIAN_CUTOVER)
				if (result instanceof GregorianCalendar) ((GregorianCalendar)result).setGregorianChange(new Date(gregorianChange));
			return result;
		}

		public Calendar copy (Jett jett, Calendar original) {
			return (Calendar)original.clone();
		}
	}


	/** Serializer for {@link Locale} (immutables).
	 * @author Tumi <serverperformance@gmail.com> */
	static public class LocaleSerializer extends DefaultSerialiser<Locale> {
		// Missing constants in j.u.Locale for common locale
		static public final Locale SPANISH = new Locale("es", "", "");
		static public final Locale SPAIN = new Locale("es", "ES", "");

		

		protected Locale create (String language, String country, String variant) {
			// Fast-path for default locale in this system (may not be in the Locale constants list)
			Locale defaultLocale = Locale.getDefault();
			if (isSameLocale(defaultLocale, language, country, variant)) return defaultLocale;
			// Fast-paths for constants declared in java.util.Locale :
			// 1. "US" locale (typical forced default in many applications)
			if (defaultLocale != Locale.US && isSameLocale(Locale.US, language, country, variant)) return Locale.US;
			// 2. Language-only constant locales
			if (isSameLocale(Locale.ENGLISH, language, country, variant)) return Locale.ENGLISH;
			if (isSameLocale(Locale.GERMAN, language, country, variant)) return Locale.GERMAN;
			if (isSameLocale(SPANISH, language, country, variant)) return SPANISH;
			if (isSameLocale(Locale.FRENCH, language, country, variant)) return Locale.FRENCH;
			if (isSameLocale(Locale.ITALIAN, language, country, variant)) return Locale.ITALIAN;
			if (isSameLocale(Locale.JAPANESE, language, country, variant)) return Locale.JAPANESE;
			if (isSameLocale(Locale.KOREAN, language, country, variant)) return Locale.KOREAN;
			if (isSameLocale(Locale.SIMPLIFIED_CHINESE, language, country, variant)) return Locale.SIMPLIFIED_CHINESE;
			if (isSameLocale(Locale.CHINESE, language, country, variant)) return Locale.CHINESE;
			if (isSameLocale(Locale.TRADITIONAL_CHINESE, language, country, variant)) return Locale.TRADITIONAL_CHINESE;
			// 2. Language with Country constant locales
			if (isSameLocale(Locale.UK, language, country, variant)) return Locale.UK;
			if (isSameLocale(Locale.GERMANY, language, country, variant)) return Locale.GERMANY;
			if (isSameLocale(SPAIN, language, country, variant)) return SPAIN;
			if (isSameLocale(Locale.FRANCE, language, country, variant)) return Locale.FRANCE;
			if (isSameLocale(Locale.ITALY, language, country, variant)) return Locale.ITALY;
			if (isSameLocale(Locale.JAPAN, language, country, variant)) return Locale.JAPAN;
			if (isSameLocale(Locale.KOREA, language, country, variant)) return Locale.KOREA;
			// if (isSameLocale(Locale.CHINA, language, country, variant)) // CHINA==SIMPLIFIED_CHINESE, see Locale.java
			// return Locale.CHINA;
			// if (isSameLocale(Locale.PRC, language, country, variant)) // PRC==SIMPLIFIED_CHINESE, see Locale.java
			// return Locale.PRC;
			// if (isSameLocale(Locale.TAIWAN, language, country, variant)) // TAIWAN==SIMPLIFIED_CHINESE, see Locale.java
			// return Locale.TAIWAN;
			if (isSameLocale(Locale.CANADA, language, country, variant)) return Locale.CANADA;
			if (isSameLocale(Locale.CANADA_FRENCH, language, country, variant)) return Locale.CANADA_FRENCH;

			return new Locale(language, country, variant);
		}

		public void write (Jett jett, Output output, Locale l) {
			output.writeAscii(l.getLanguage());
			output.writeAscii(l.getCountry());
			output.writeString(l.getVariant());
		}

		public Locale read (Jett jett, Input input, Class<Locale> type) {
			String language = input.readString();
			String country = input.readString();
			String variant = input.readString();
			return create(language, country, variant);
		}

		// Removed as Locale is declares as immutable
		// public Locale copy (Jett jett, Locale original) {
		// return create(original.getLanguage(), original.getDisplayCountry(), original.getVariant());
		// }

		protected static boolean isSameLocale (Locale locale, String language, String country, String variant) {
			try {
				return (locale.getLanguage().equals(language) && locale.getCountry().equals(country)
					&& locale.getVariant().equals(variant));
			} catch (NullPointerException npe) {
				// Shouldn't ever happen, no nulls
				return false;
			}
		}
	}

	/** Serializer for {@link Charset}. */
	public static class CharsetSerializer extends DefaultSerialiser<Charset> {

		

		public void write (Jett jett, Output output, Charset object) {
			output.writeString(object.name());
		}

		public Charset read (Jett jett, Input input, Class<Charset> type) {
			return Charset.forName(input.readString());
		}

	}

	/** Serializer for {@link URL}. */
	public static class URLSerializer extends DefaultSerialiser<URL> {


		public void write (Jett jett, Output output, URL object) {
			output.writeString(object.toExternalForm());
		}

		public URL read (Jett jett, Input input, Class<URL> type) {
			try {
				return new URL(input.readString());
			} catch (MalformedURLException e) {
				throw new JettException(e);
			}
		}

	}

}
