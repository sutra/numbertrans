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
package info.jonclark.lang;

import java.util.Iterator;

/**
 * An array that allows inserting into a "next location," which will circle back
 * to the beginning of the buffer if space is available. This provides a way for
 * keeping data that is recent up to a certain point.
 * <p>
 * This class is not thread-safe.
 */
public class CircularBuffer<T> implements Iterable {
    private final T[] buffer;
    private int nFirst = 0;
    private int nLast = 0;
    private boolean bufferFull = false;
    private boolean bufferEmpty = true;

    public CircularBuffer(int nBufferSize) {
	this.buffer = allocateBuffer(nBufferSize);
    }

    @SuppressWarnings("unchecked")
    private T[] allocateBuffer(int nSize) {
	return (T[]) new Object[nSize];
    }

    /**
         * Get the ith youngest element.
         * 
         * @param i
         * @return
         */
    public T get(int i) {
	// TODO: bounds checking on get
	int nElement = (i + nFirst) % buffer.length;
	return buffer[nElement];
    }

    public T getFirst() {
	return buffer[nFirst];
    }

    public T getLast() {
	return buffer[nLast];
    }

    public void add(T element) {
	if (bufferFull)
	    nFirst++;
	nFirst %= buffer.length;

	if (!bufferEmpty)
	    nLast++;
	nLast %= buffer.length;

	if (!bufferFull && nLast + 1 == buffer.length)
	    bufferFull = true;
	if (bufferEmpty)
	    bufferEmpty = false;
	
	buffer[nLast] = element;
    }

    public int size() {
	if (bufferFull)
	    return buffer.length;
	else
	    return nLast - nFirst + 1;
    }

    private class CircularBufferIterator implements Iterator<T> {
	private final CircularBuffer<T> circ;
	private int nCurrent;

	public CircularBufferIterator(CircularBuffer<T> circ) {
	    this.circ = circ;
	    this.nCurrent = circ.nFirst;
	}

	public boolean hasNext() {
	    return nCurrent != circ.nLast;
	}

	public T next() {
	    return circ.buffer[nCurrent++];
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    public Iterator<T> iterator() {
	return new CircularBufferIterator(this);
    }
}
