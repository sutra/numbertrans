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

import java.util.*;
import java.io.*;

/**
 * An array based on LinkedLists such that memory usage is minimized even for
 * very large arrays Note: Array indices are constant; list indicies are subject
 * to change. Created on January 30, 2006
 */
public class SparseArray<T> implements Serializable {
    // TODO: Optimize this by allowing searching
    // from both the head and the tail, whichever is closer

    // TODO: Add an Iterable interface

    /**
         * Comment for <code>serialVersionUID</code>
         */
    private static final long serialVersionUID = 1L;

    LinkedList<Node> list = new LinkedList<Node>(); // List of nodes
                                                        // (ordered by index)
    long nSize = 0;

    /**
         * Node for internal use of the SparseArray class only. Auto-inherits
         * the parameter T due to scope.
         */
    private class Node implements Serializable {

	/**
         * Comment for <code>serialVersionUID</code>
         */
	private static final long serialVersionUID = 3905801993896145973L;

	private long arrIndex;
	public T obj;

	public Node(long idx, T o) {
	    arrIndex = idx;
	    obj = o;
	}
    }

    /**
         * Write an object at the given array index
         * 
         * @param arrIndex
         *                The index of the object to add/replace
         * @param obj
         *                The object to write
         */
    public void set(long arrIndex, T obj) {
	// Make sure we have a 0th element
	if (list.size() > 0) {
	    ListIterator<Node> it = list.listIterator(0);
	    Node left = (Node) it.next();
	    // Seek to the correct node, or if it
	    // does not exist, the node before
	    while (it.hasNext() && left.arrIndex < arrIndex) {
		left = (Node) it.next();
	    }
	    if (left.arrIndex == arrIndex) {
		// Node already exists; overwrite object
		it.set(new Node(arrIndex, obj));
	    } else {
		// We need a new node; insert new object

		// Make sure we insert in the right place
		if (left.arrIndex > arrIndex)
		    it.previous();
		it.add(new Node(arrIndex, obj));
		nSize++;
	    }
	} else {
	    // There are no elements in this list,
	    // add the first
	    list.add(new Node(arrIndex, obj));
	    nSize++;
	}
    }

    /**
         * Get an object at a specified array index
         * 
         * @param arrIndex
         *                The array index of the desired object
         * @return The object at the specified array index
         */
    public T get(long arrIndex) throws RuntimeException {
	Node rtn = null; // node to be returned
	ListIterator<Node> it = list.listIterator(0);
	// Make sure we have a 0th element
	if (it.hasNext()) {
	    Node left = it.next(); // node to the left of the iterator
	    // Seek to the correct node, or if it
	    // does not exist, the node before
	    while (it.hasNext() && left.arrIndex < arrIndex) {
		left = it.next();
	    }

	    if (left.arrIndex == arrIndex) {
		// Node exists, we are ready to return it
		rtn = left;
	    } else {
		// Node does not exist, this is a problem
		rtn = null;
	    }
	} else {
	    // There is nothing in the list, therefore
	    // this index cannot exist
	    rtn = null;
	}

	if (rtn == null) {
	    throw new RuntimeException("No such element: Index " + arrIndex);
	} else {
	    return rtn.obj;
	}
    }

    /**
         * Determine if the specified index has already been assigned a value in
         * this sparse array
         * 
         * @param arrIndex
         * @return True if the index already exists in this sparse array
         */
    public boolean hasIndex(long arrIndex) {
	ListIterator it = list.listIterator(0);
	boolean rtn = false;
	// Make sure we have a 0th element
	if (it.hasNext()) {
	    Node left = (Node) it.next(); // node to the left of the
                                                // iterator
	    // Seek to the correct node, or if it
	    // does not exist, the node before
	    while (it.hasNext() && left.arrIndex < arrIndex) {
		left = (Node) it.next();
	    }

	    if (left.arrIndex == arrIndex) {
		// Node exists, we are ready to return it
		rtn = true;
	    } else {
		// Node does not exist, this is a problem
		rtn = false;
	    }
	} else {
	    // There is nothing in the list, therefore
	    // this index cannot exist
	    rtn = false;
	}
	return rtn;
    }

    /**
         * Returns the number of elements that have been set to a non-null value
         * 
         * @return
         */
    public long size() {
	return nSize;
    }

    public String toString() {
	ListIterator it = list.listIterator();
	StringBuffer buf = new StringBuffer();
	while (it.hasNext()) {
	    Node node = (Node) it.next();
	    buf.append(node.obj.toString() + "\r\n");
	}
	return buf.toString();
    }
}
