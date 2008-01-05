/*
 * Copyright (c) 2007, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
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
package net.sourceforge.numbertrans.framework.base;

/**
 * An abstract class to examines a string in some source language and identifies
 * numbers. Finders for all languages should inherit from this class.
 * <p>
 * This class carries the burden not only of identifying numbers, but also
 * tagging their context.
 * <p>
 * Note that many languages contain numbers that are ambiguous; that is, that
 * cannot be positively identified as just numbers and may have some other
 * semantic meaning. This class provides safety thresholds so that the user may
 * choose how aggressive to be in tuning the precision versus recall of this
 * class.
 */
public interface NumberFinder {

    /**
     * Returns true if the given string is unambiguously a number regardless of
     * context.
     * 
     * @param strToCheck
     * @return
     */
    public boolean isNumberAlways(String strToCheck);

    /**
     * Returns true if the given string can be a number in some context.
     * 
     * @param strToCheck
     * @return
     */
    public boolean isNumberSometimes(String strToCheck);

    public NumberMatch nextMatch(final String[] tokens, int nBeginIndex);
}
