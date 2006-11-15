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

import java.io.*;

/**
 * A matrix composed of <code>SparseArray</code> objects that
 * minimizes memory consumption for very large matrices when
 * not all elements must be set
 *
 * Created on January 30, 2006
 */
public class SparseMatrix<T> implements Serializable {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3689064041179984185L;
	
	private SparseArray<SparseArray<T>> rows = new SparseArray<SparseArray<T>>();
	
	/**
	 * Sets an element of this <code>SparseMatrix</code>
	 * If the element at the given ID does not already exist,
	 * it will be created.
	 * 
	 * @param nRow The row that will be set or created
	 * @param nCol The column that will be set or created
	 * @param obj The element that will be put at the specified location
	 */
	public void set(long nRow, long nCol, T obj) {
		SparseArray<T> row = null;
		if(rows.hasIndex(nRow)) {
			row = rows.get(nRow);
		} else {
			row = new SparseArray<T>();
			rows.set(nRow, row);
		}
		row.set(nCol, obj);
	}
	
	/**
	 * Get an element of this SparseMatrix
	 * 
	 * @param nRow The row at which the object is located
	 * @param nCol The column at which the object is located
	 * @return The object at the given location or <code>null</code> if the
	 * 			location has not previously been set
	 */
	public T get(long nRow, long nCol) {
		SparseArray<T> row = rows.get(nRow);
		if(row != null)
		    return row.get(nCol);
		else
		    return null;
	}
}
