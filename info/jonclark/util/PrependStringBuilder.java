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

/**
 * Similar to a standard {@link StringBuilder} except that it supports
 * <code>prepend()</code> instead of <code>append()</code>.
 */
public class PrependStringBuilder {

    /**
         * The characters of our String.
         */
    private char value[];

    /**
         * How many characters have been used.
         */
    private int count = 0;

    /**
         * Constructs a string builder with no characters in it and an initial
         * capacity of 16 characters.
         */
    public PrependStringBuilder() {
	this(16);
    }

    /**
         * Constructs a string builder with no characters in it and an initial
         * capacity specified by the <code>capacity</code> argument.
         * 
         * @param capacity
         *                the initial capacity.
         * @throws NegativeArraySizeException
         *                 if the <code>capacity</code> argument is less than
         *                 <code>0</code>.
         */
    public PrependStringBuilder(int capacity) {
	value = new char[capacity];
    }

    /**
         * Constructs a string builder initialized to the contents of the
         * specified string. The initial capacity of the string builder is
         * <code>16</code> plus the length of the string argument.
         * 
         * @param str
         *                the initial contents of the buffer.
         * @throws NullPointerException
         *                 if <code>str</code> is <code>null</code>
         */
    public PrependStringBuilder(String str) {
	this(str.length() + 16);
	prepend(str);
    }

    public PrependStringBuilder prepend(String str) {
	if (str == null)
	    str = "null";

	int len = str.length();
	if (len == 0)
	    return this;

	int newCount = count + len;
	if (newCount > value.length)
	    expandCapacity(newCount);

	// we must reverse everything coming in, since it will be reversed again
	// on its way out.
	str.getChars(0, len, value, count);
	StringUtils.reverse(value, count, count + len);
	count = newCount;
	return this;
    }
    
    public PrependStringBuilder prepend(char c) {
	int len = 1;

	int newCount = count + len;
	if (newCount > value.length)
	    expandCapacity(newCount);

	value[count] = c;
	count = newCount;
	return this;
    }

    /**
         * This implements the expansion semantics of ensureCapacity with no
         * size check or synchronization.
         */
    private void expandCapacity(int minimumCapacity) {
	int newCapacity = (value.length + 1) * 2;
	if (newCapacity < 0) {
	    newCapacity = Integer.MAX_VALUE;
	} else if (minimumCapacity > newCapacity) {
	    newCapacity = minimumCapacity;
	}
	char newValue[] = new char[newCapacity];
	System.arraycopy(value, 0, newValue, 0, count);
	value = newValue;
    }

    public int length() {
	return count;
    }
    
    public String toString() {
        // Create a copy, don't share the array
	StringUtils.reverse(value, 0, count);
	return new String(value, 0, count);
    }
}
