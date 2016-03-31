// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Date;


public class TimeValue implements Cloneable, Comparable<TimeValue> {

	public static enum MEASURE {
		DAYS,
		HOURS,
		MINUTES,
		SECONDS,
		MILLISECONDS
	}

	public static long convert(long value, MEASURE initialMeasure, MEASURE finalMeasure) {
		return new TimeValue(value, initialMeasure).getAs(finalMeasure);
	}

	private final long _milliseconds;
	
	public TimeValue(Date date) {
		Checker.checkNull(date);
		_milliseconds = date.getTime();
	}
	
	public TimeValue(long value, MEASURE measure) {
		Checker.checkMinValue(value, 0);
		Checker.checkNull(measure);
		switch(measure) {
			case MILLISECONDS:
				_milliseconds = value;
				break;
			case SECONDS:
				_milliseconds = value * Factors.second;
				break;
			case MINUTES:
				_milliseconds = value * Factors.minute;
				break;
			case HOURS:
				_milliseconds = value * Factors.hour;
				break;
			case DAYS:
				_milliseconds = value * Factors.day;
				break;
			default:
				throw new IllegalArgumentException("invalid measure: "+measure);
		}
	}
	
	public TimeValue(String value) {
		Checker.checkEmpty(value);
		// Parse input String:
		value = value.replaceAll(" ", "");
		value = value.replaceAll("\t", "");
		if(Character.isDigit(value.charAt(0))) {
			int i = 0;
			while(Character.isDigit(value.charAt(i))) {
				i++;
			}
			if(i == 0) {
				throw new DateFormatException("invalid input String: "+value);
			}
			String number = value.substring(0, i);
			String measure = value.substring(i);
			long lValue = new Long(number);
			if(measure.equals("ms")) {
				_milliseconds = lValue;
			}
			else if(measure.equals("s")) {
				_milliseconds = lValue * Factors.second;
			}
			else if(measure.equals("m")) {
				_milliseconds = lValue * Factors.minute;
			}
			else if(measure.equals("h")) {
				_milliseconds = lValue * Factors.hour;
			}
			else if(measure.equals("d")) {
				_milliseconds = lValue * Factors.day;
			}
			else {
				throw new DateFormatException("invalid measure: "+measure);
			}
		}
		else {
			throw new DateFormatException("invalid input String: "+value);
		}
	}

	public long getAs(MEASURE measure) {
		Checker.checkNull(measure);
		if(measure == MEASURE.MILLISECONDS) {
			return _milliseconds;
		}
		else if(measure == MEASURE.SECONDS) {
			return _milliseconds/Factors.second;
		}
		else if(measure == MEASURE.MINUTES) {
			return _milliseconds/Factors.minute;
		}
		else if(measure == MEASURE.HOURS) {
			return _milliseconds/Factors.hour;
		}
		else if(measure == MEASURE.DAYS) {
			return _milliseconds/Factors.day;
		}
		else {
			throw new IllegalArgumentException("invalid measure: "+measure);
		}
	}

	public long days() {
		return getAs(MEASURE.DAYS);
	}

	public long hours() {
		return getAs(MEASURE.HOURS);
	}

	public long minutes() {
		return getAs(MEASURE.MINUTES);
	}

	public long seconds() {
		return getAs(MEASURE.SECONDS);
	}

	public long milliseconds() {
		return getAs(MEASURE.MILLISECONDS);
	}

	public TimeValue clone() {
		try {
			return (TimeValue)super.clone();
		}
		catch(CloneNotSupportedException cnse) {
			throw new IntegrityException(cnse);
		}
	}
	
	public boolean equals(Object o) {
		if(this == o) {
 			return true;
 		}
 		if(o == null) {
 			return false;
 		}
 		if(!(o instanceof TimeValue)) {
 			return false;
 		}
 		return _milliseconds == ((TimeValue)o)._milliseconds;
	}

	public int hashCode() {
		int hash = 7;
		int varCode = (int)(_milliseconds ^ (_milliseconds >> 32));
		hash = 31 * hash + varCode;
		return hash;
	}

	public int compareTo(TimeValue t) {
		Checker.checkNull(t);
		long result = _milliseconds - t._milliseconds;
		return result<0 ? -1 : 1;
	}

	public String toString() {
		// Try to find out what is the measure we should use:
		long value = days();
		if(value > 0) {
			return value+"d";
		}
		value = hours();
		if(value > 0) {
			return value+"h";
		}
		value = minutes();
		if(value > 0) {
			return value+"m";
		}
		value = seconds();
		if(value > 0) {
			return value+"s";
		}
		return milliseconds()+"ms";
	}

	private static class Factors {
		// public static final long year	= 1000*60*60*24*365;
		// public static final long month	= 1000*60*60*24*31;
		// public static final long week	= 1000*60*60*24*7;
		public static final long day	= 1000*60*60*24;
		public static final long hour	= 1000*60*60;
		public static final long minute	= 1000*60;
		public static final long second	= 1000;
	}
}
