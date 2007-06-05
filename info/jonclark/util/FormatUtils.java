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

import info.jonclark.lang.PrependStringBuilder;

import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @author Jonathan
 */
public class FormatUtils {
    private static final SimpleDateFormat fDateFull = new SimpleDateFormat(
	    "EEEEEEEEE, MMMMMMMM dd, yyyy hh:mm:ss aa");
    private static final SimpleDateFormat fDateLong = new SimpleDateFormat("MMMMMMMM dd, yyyy");
    public static final DecimalFormat FORMAT_2DECIMALS = new DecimalFormat("#,###.##");
    public static final DecimalFormat FORMAT_4DECIMALS = new DecimalFormat("#,###.####");
    public static final DecimalFormat FORMAT_WHOLE = new DecimalFormat("#,###");

    /**
         * Format date like as in this example: Wednesday, March 31, 2005
         * 5:23:55 pm
         * 
         * @param d
         *                The date object to be formatted
         * @return The specified date formatted in the format shown above
         */
    public static String formatFullDate(final Date d) {
	return fDateFull.format(d);
    }

    /**
         * Format date like as in this example: March 31, 2005
         * 
         * @param d
         *                The date object to be formatted
         * @return The specified date formatted in the format shown above
         */
    public static String formatLongDate(Date d) {
	return fDateLong.format(d);
    }

    /**
         * Format a timespan as in this example: 5 days, 2 hours, 9 minutes, 23
         * seconds
         * 
         * @param span
         * @return
         */
    public static String formatTimeSpanFull(final long span) {
	final int ALL_UNITS = 6;
	return formatTimeSpan(span, ALL_UNITS);
    }

    /**
         * Format a timespan as in this examples: 5 days, 2 hours 9 minutes, 23
         * seconds BUT NOT 5 days, 2 hours, 9 minutes, 23 seconds
         * 
         * @param span
         * @param nUnits
         *                The maxiumum number of units that will be shown
         * @return
         */
    public static String formatTimeSpan(final long span, final int nUnits) {
	// modulo the years, months, days, hours, minutes, seconds, etc.
	// and create parameters that allow only a certain number
	// of those things to be shown

	throw new RuntimeException("Unimplemented");
    }

    /**
         * Formats a whole number, adding commas.
         * 
         * @param wholeNumber
         * @return
         */
    public static String formatLong(long wholeNumber) {
	return FORMAT_WHOLE.format(wholeNumber);
    }

    /**
         * Formats a decimal number, adding commas and providing up to 2 places
         * of decimal accuracy.
         * 
         * @param decimalNumber
         * @return
         */
    public static String formatDouble2(double decimalNumber) {
	return FORMAT_2DECIMALS.format(decimalNumber);
    }
    
    public static String formatDouble4(double decimalNumber) {
    	return FORMAT_4DECIMALS.format(decimalNumber);
        }

    public static String formatPhoneNumber(String phoneNumber) {
	// make a list of only the digits
	StringBuilder digits = new StringBuilder();
	for (int i = 0; i < phoneNumber.length(); i++) {
	    char c = phoneNumber.charAt(i);
	    if (Character.isDigit(c)) {
		digits.append(c);
	    }
	}

	// now format like this: (817) 939-1985
	int i = 0;
	PrependStringBuilder formatted = new PrependStringBuilder();

	if (digits.length() >= 4) {
	    formatted.prepend(digits.substring(0, 4));
	    i += 4;

	    if (digits.length() > 4) {
		formatted.prepend('-');
	    }
	}

	if (digits.length() >= 7) {
	    formatted.prepend(digits.substring(4, 7));
	    i += 3;

	    if (digits.length() > 7) {
		formatted.prepend(' ');
	    }
	}

	if (digits.length() >= 10) {
	    formatted.prepend("(" + digits.substring(7, 10) + ")");
	    i += 3;

	    if (digits.length() > 10) {
		formatted.prepend(' ');
	    }
	}

	// just stick whatever is left on the beginning
	formatted.prepend(digits.substring(i));
	
	return formatted.toString();
    }
}
