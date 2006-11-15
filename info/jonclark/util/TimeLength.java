/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Date;

// TODO: Handle plural vs singular.
// TODO: handle abbreviates such as ms, s, etc.
// TODO: handle portuguese-style time inputs
// TODO: Use gregorian calendar?
// FIXME: This floating point math could potentially cause problems
// FIXME: Create documentation
// TODO: Fix accuracy of "month"
// TODO: Create some JUnit test cases for cases such as "10 years 29 days, 100 seconds, 9 milliseconds"


public class TimeLength {
    private final long length;
    private static final DecimalFormat format = new DecimalFormat("0.#");

    /**
         * @param millis
         *                The length of this time span in milliseconds
         */
    public TimeLength(long millis) {
	this.length = millis;
    }

    public TimeLength(long seconds, long millis) {
	this.length = seconds * 1000 + millis;
    }

    public TimeLength(long minutes, long seconds, long millis) {
	this.length = (minutes * 60 + seconds) * 1000 + millis;
    }

    public TimeLength(long hours, long minutes, long seconds, long millis) {
	this.length = ((hours * 60 + minutes) * 60 + seconds) * 1000 + millis;
    }

    public TimeLength(long days, long hours, long minutes, long seconds, long millis) {
	this.length = (((days * 24 + hours) * 60 + minutes) * 60 + seconds) * 1000 + millis;
    }

    public TimeLength(Date start, Date end) {
	this.length = end.getTime() - start.getTime();
    }

    public double getInYears() {
	return (double) length / (double) (365L * 24L * 60L * 60L * 1000L);
    }

    public double getInDays() {
	return (double) length / (double) (24L * 60L * 60L * 1000L);
    }

    public double getInHours() {
	return (double) length / (double) (60 * 60 * 1000);
    }

    public double getInMinutes() {
	return (double) length / (double) (60 * 1000);
    }

    public double getInSeconds() {
	return length / (1000);
    }

    /**
         * Note: This method is equivalent to <code>toLong()</code>
         * 
         * @return
         */
    public long getInMillis() {
	return length / (1000);
    }

    public long toLong() {
	return length;
    }

    public static TimeLength parseTimeLength(final String strInput) {
	// TODO: Do this much better...

	final String strNoSpaces = StringUtils.replaceFast(strInput, " ", "");

	// First, try this in short-hand notation

	// colonTokens in 1:23:45.678 are {1, 23, 45.678}
	final String[] colonTokens = StringUtils.tokenize(strNoSpaces, ":");

	if (colonTokens.length == 2 || colonTokens.length == 3) {
	    final int hours = colonTokens.length == 3 ? Integer.parseInt(colonTokens[0]) : 0;
	    final int minutes = Integer.parseInt(colonTokens[colonTokens.length - 2]);

	    // dotTokens in 1:23:45.678 are 45.678 as {45, 678}
	    final String[] dotTokens = StringUtils.tokenize(colonTokens[colonTokens.length - 1]);
	    final int seconds = Integer.parseInt(dotTokens[0]);
	    final int millis = dotTokens.length > 1 ? Integer.parseInt(dotTokens[1]) : 0;
	    return new TimeLength(hours, minutes, seconds, millis);
	} else if (colonTokens.length == 1) {
	    // Next, assume this is in natural language (English) notation

	    final String strNoCommas = StringUtils.replaceFast(strInput, ",", "");
	    final String strNoCommasOrAnds = StringUtils.replaceFast(strNoCommas, " and ", " ");
	    final String[] spaceTokens = StringUtils.tokenize(strNoCommasOrAnds);

	    // remove s from end of all tokens
	    for (int i = 0; i < spaceTokens.length; i++)
		spaceTokens[i] = StringUtils.removeTrailingString(spaceTokens[i], "s");
	    // we will use == for string comparisons
	    StringUtils.internTokens(spaceTokens);

	    if (spaceTokens.length % 2 == 0) {
		int decades = 0;
		int years = 0;
		int months = 0;
		int fortnights = 0;
		int weeks = 0;
		long days = 0;
		long hours = 0;
		long minutes = 0;
		long seconds = 0;
		long millis = 0;

		for (int i = 0; i < spaceTokens.length; i += 2) {
		    if (spaceTokens[i + 1] == "decade")
			decades = Integer.parseInt(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "year")
			years = Integer.parseInt(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "month")
			months = Integer.parseInt(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "fortnight")
			fortnights = Integer.parseInt(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "week")
			weeks = Integer.parseInt(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "day")
			days = Long.parseLong(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "hour")
			hours = Long.parseLong(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "minute")
			minutes = Long.parseLong(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "second")
			seconds = Long.parseLong(spaceTokens[i]);
		    else if (spaceTokens[i + 1] == "millisecond")
			millis = Long.parseLong(spaceTokens[i]);
		}

		final long daysTotal = decades * 3650 + years * 365
			+ (long) ((double) months * 365d / 12d) + fortnights * 14 + weeks * 7
			+ days;

		return new TimeLength((long) daysTotal, hours, minutes, seconds, millis);
	    } else {
		throw new IllegalArgumentException(
			"Odd number of tokens in natural language time length: " + strInput);
	    }
	} else {
	    throw new IllegalArgumentException("Unknown time length format: " + strInput);
	}
    }

    // TODO: method that will find biggest unit possible with a whole number
    // and
    // honor that. e.g. 12.3 days or 2.5 minutes

    /**
         * Finds the biggest unit possible that can be represented with a whole
         * number and outputs a String in that unit of time length as a
         * <code>double</code> to one decimal place of accuracy.
         * <p>
         * e.g. 12.3 days or 2.5 minutes.
         */
    public String toStringSingleUnit() {
	if (getInYears() > 1.0) {
	    return format.format(getInYears()) + " years";
	} else if (getInDays() > 1.0) {
	    return format.format(getInDays()) + " days";
	} else if (getInHours() > 1.0) {
	    return format.format(getInHours()) + " hours";
	} else if (getInMinutes() > 1.0) {
	    return format.format(getInMinutes()) + " minutes";
	} else if (getInSeconds() > 1.0) {
	    return format.format(getInSeconds()) + " seconds";
	} else {
	    return format.format(getInMillis()) + " milliseconds";
	}
    }
    
    public String toStringMultipleUnits(final int nMaxUnits) {
	// this algorithm might already exist elsewhere
	long millis = length;
	millis %= 1000;
	long seconds = (length / 1000);
	long minutes = seconds / 60;
	seconds %= 60;
	long hours = minutes / 60;
	minutes %= 60;
	long days = hours / 24;
	hours %= 24;
	long years = days / 365; // FIXME: not exactly...
	days %= 365;
	
	final StringBuilder builder = new StringBuilder();
	int nActualUnits = 0;
	
	if(years > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(years + " years, ");
	}
	if(days > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(days + " days, ");
	}
	if(hours > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(hours + " hours, ");
	}
	if(minutes > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(hours + " minutes, ");
	}
	if(seconds > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(seconds + " seconds, ");
	}
	if(millis > 0 && nActualUnits < nMaxUnits) {
	    nActualUnits++;
	    builder.append(millis + " milliseconds, ");
	}

	final String strResult = StringUtils.cutCharsFromEnd(builder.toString(), 2);
	return strResult;
    }    

    /**
         * Returns all unit components of the time length (including zero
         * values).
         * <p>
         * e.g. 10 years, 0 days, 0 hours, 0 minutes, 29 seconds
         * 
         * @return A string of the above format containing all <code>long</code>
         *         values.
         */
    public String toString() {
	// this algorithm might already exist elsewhere
	long millis = length;
	millis %= 1000;
	long seconds = (length / 1000);
	long minutes = seconds / 60;
	seconds %= 60;
	long hours = minutes / 60;
	minutes %= 60;
	long days = hours / 24;
	hours %= 24;
	long years = days / 365; // FIXME: not exactly...
	days %= 365;

	return years + " years, " + days + " days, " + hours + " hours, " + minutes + " minutes, "
		+ seconds + " seconds";
    }

    public static void main(String args[]) throws Exception {
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	TimeLength t = TimeLength.parseTimeLength(in.readLine());
	System.out.println(t.toString());
	System.out.println(t.toStringSingleUnit());
	System.out.println(t.toStringMultipleUnits(3));
    }
}
