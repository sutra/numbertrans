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
package info.jonclark.stat;

import java.text.SimpleDateFormat;
import java.util.Date;

import info.jonclark.lang.CircularBuffer;
import info.jonclark.util.TimeLength;

public class RemainingTimeEstimator {
    private final CircularBuffer<Long> eventLog;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
	    "MMMM dd, yyyy hh:mm aa");

    /**
         * Note: Try to choose an event window that is long enough such that
         * instantaneous jitter is eliminated while making the window small
         * enough that a single long pause does not skew the results.
         * 
         * @param nEventWindow
         */
    public RemainingTimeEstimator(int nEventWindow) {
	assert nEventWindow >= 3 : "Event window must be at least 3 to produce good results";

	this.eventLog = new CircularBuffer<Long>(nEventWindow);
    }

    public void recordEvent() {
	// Note: Autoboxing going on here...
	eventLog.add(System.currentTimeMillis());
    }

    private long getSpan() {
	if (eventLog.size() >= 2) {
	    return eventLog.getLast() - eventLog.getFirst();
	} else {
	    return 0;
	}
    }

    private double getSeconds() {
	long span = getSpan();
	return (double) span / 1000.0;
    }

    public String getEventsPerSecond(long nEvents) {
	if (getSeconds() > 0)
	    return SecondTimer.format.format(nEvents / getSeconds());
	else
	    return "Undefined";
    }

    public String getSecondsPerEvent(long nEvents) {
	if (nEvents > 0)
	    return SecondTimer.format.format(getSeconds() / nEvents);
	else
	    return "Undefined";
    }

    public TimeLength getRemainingTime(int nEventsRemaining) {
	long span = getSpan();
	int nRecentEventsDone = eventLog.size() - 1;
	long remainingTime;
	if (nRecentEventsDone > 0) {
	    remainingTime = span * nEventsRemaining / nRecentEventsDone;
	} else {
	    remainingTime = 0;
	}
	return new TimeLength(remainingTime);
    }

    // TODO: Test the accuracy of this method
    public long getEstimatedCompetionTime(int nEventsRemaining) {
	final TimeLength length = getRemainingTime(nEventsRemaining);
	long completion = System.currentTimeMillis() + length.getInMillis();
	return completion;
    }

    // TODO: Test the accuracy of this method
    public String getEstimatedCompetionTimeFormatted(int nEventsRemaining) {
	final long completion = getEstimatedCompetionTime(nEventsRemaining);
	return dateFormat.format(new Date(completion));
    }

    public static void main(String... args) throws Exception {
	final RemainingTimeEstimator est = new RemainingTimeEstimator(5);
	int n = 30;
	for (int i = 0; i < n; i++) {
	    est.recordEvent();
	    System.out.println(n - i + ": " + est.getRemainingTime(n - i));
	    System.out.println(n - i + ": " + est.getEstimatedCompetionTimeFormatted(n - i));
	    Thread.sleep(1000);
	}
    }
}
