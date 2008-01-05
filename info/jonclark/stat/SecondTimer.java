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

import info.jonclark.util.FormatUtils;

/**
 * @author Jonathan
 */
public class SecondTimer {
    
    private long accumulatedTime = 0;
    private long startDate = 0;
    
    private final boolean catchOverlappingGo;
    /**
     * Does not catch "overlapping go" and does not "go" by default.
     * See next constructor for details.
     *
     */
    public SecondTimer() {
        catchOverlappingGo = true;
    }
    
    /**
     * 
     * @param catchOverlappingGo If true, calling go()
     * 			twice without calling pause() will
     * 			throw an exception
     */
    public SecondTimer(boolean catchOverlappingGo) {
	this.catchOverlappingGo = catchOverlappingGo;
    }
    
    /**
     * 
     * @param catchOverlappingGo If true, calling go()
     * 			twice without calling pause() will
     * 			throw an exception
     */
    public SecondTimer(boolean catchOverlappingGo, boolean goNow) {
	this.catchOverlappingGo = catchOverlappingGo;
	if(goNow)
	    go();
    }
    
    /**
     * Calculates the number of events that occured per second,
     * on average, during the time this timer was accumulating.
     * 
     * @param nEvents The number of events that occurred
     * 			while this SecondTimer has been accumulating
     * 			time.
     * @return
     */
    public String getEventsPerSecond(long nEvents) {
        if(getSeconds() > 0)
            return FormatUtils.FORMAT_2DECIMALS.format((double) nEvents / getSeconds());
        else
            return "Undefined";
    }
    
    /**
     * Calculates the number of seconds elapsed per event
     * on average, during the time this timer was accumulating.
     * 
     * @param nEvents The number of events that occurred
     * 			while this SecondTimer has been accumulating
     * 			time.
     * @return
     */
    public String getSecondsPerEvent(long nEvents) {
	if(nEvents > 0)
            return FormatUtils.FORMAT_2DECIMALS.format(getSeconds() / (double) nEvents);
        else
            return "Undefined";
    }
    
    /**
     * Gets the number of seconds accumulated by this
     * SecondTimer so far
     * 
     * @return
     */
    public double getSeconds() {
        return (double)getMilliseconds() / 1000.0;
    }
    
    /**
     * Gets the number of seconds accumulated by this
     * SecondTimer so far to 2 decimals accuracy.
     * 
     * @return
     */
    public String getSecondsFormatted() {
	return FormatUtils.FORMAT_2DECIMALS.format(getSeconds());
    }
    
    /**
     * Gets the number of milliseconds accumulated by
     * this second timer so far
     * 
     * @return
     */
    public long getMilliseconds() {
        if(startDate != 0)
            return accumulatedTime + System.currentTimeMillis() - startDate;
        else
            return accumulatedTime;
    }
    
    /**
     * If the timer 
     *
     * @throws RuntimeException if catchOverlappingGo
     * 			was set to true and this is the second
     * 			call to go() without a previous call to
     * 			pause()
     */
    public void go() {
        if(catchOverlappingGo && startDate != 0)
            throw new RuntimeException("Overlapping go detected.");
        else if(startDate == 0)
            startDate = System.currentTimeMillis();
    }
    
    /**
     * If the timer is currenting "going" then
     * this stops the accumulation of time. Otherwise,
     * it has no effect.
     *
     */
    public void pause() {
        accumulatedTime += System.currentTimeMillis() - startDate;
        startDate = 0;
    }
    
    /**
     * Resets the amount of time accumulated by
     * this timer to zero.
     *
     */
    public void reset() {
        accumulatedTime = 0;
    }
}
