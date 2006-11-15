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

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author Jonathan
 */
public class FormatUtils {
    private static final  SimpleDateFormat fDateFull = new SimpleDateFormat(
	"EEEEEEEEE, MMMMMMMM dd, yyyy hh:mm:ss aa" );
    private static final  SimpleDateFormat fDateLong = new SimpleDateFormat(
	"MMMMMMMM dd, yyyy" );
    
    /**
     * Format date like as in this example: 
     * Wednesday, March 31, 2005 5:23:55 pm
     * 
     * @param d The date object to be formatted
     * @return The specified date formatted in the format shown above
     */
    public static String formatFullDate(final Date d) {
       return fDateFull.format(d);
    }
    
    /**
     * Format date like as in this example: 
     * March 31, 2005
     * 
     * @param d The date object to be formatted
     * @return The specified date formatted in the format shown above
     */
    public static String formatLongDate(Date d) {
       return fDateLong.format(d);
    }
    
    /**
     * Format a timespan as in this example:
     * 5 days, 2 hours, 9 minutes, 23 seconds
     * 
     * @param span
     * @return
     */
    public static String formatTimeSpanFull(final long span) {
        final int ALL_UNITS = 6;
        return formatTimeSpan(span, ALL_UNITS);
    }
    
    /**
     * Format a timespan as in this examples:
     * 5 days, 2 hours 
     * 9 minutes, 23 seconds
     * BUT NOT
     * 5 days, 2 hours, 9 minutes, 23 seconds
     * 
     * @param span
     * @param nUnits The maxiumum number of units that
     * 		will be shown
     * @return
     */
    public static String formatTimeSpan(final long span, final int nUnits) {
        // modulo the years, months, days, hours, minutes, seconds, etc.
        // and create parameters that allow only a certain number
        // of those things to be shown
        
        throw new RuntimeException("Unimplemented");
    }
}
