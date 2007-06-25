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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Utilities for working with arrays
 */
public class ArrayUtils {

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(T[] arr) {
	if (arr == null) {
	    return "null";
	} else if (arr.length != 0) {
	    final StringBuilder builder = new StringBuilder("{\"");

	    for (int i = 0; i < arr.length - 1; i++)
		builder.append(arr[i] + "\",\"");

	    builder.append(arr[arr.length - 1] + "\"}");
	    return builder.toString();
	} else {
	    return "{empty}";
	}
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param arr
         *                The array to be converted into string form
         * @param nFirst
         *                the first element to be included
         * @param nLast
         *                the last element to be included
         * @return A string representation of the array
         */
    public static <T> String arrayToString(T[] arr, int nFirst, int nLast) {
	if (arr == null) {
	    return "null";
	} else if (nLast != nFirst) {
	    final StringBuilder builder = new StringBuilder("{\"");

	    for (int i = nFirst; i < nLast - 1; i++)
		builder.append(arr[i] + "\",\"");

	    builder.append(arr[nLast - 1] + "\"}");
	    return builder.toString();
	} else {
	    return "{empty}";
	}
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(int[] arr) {
	final StringBuilder builder = new StringBuilder("{");

	for (int i = 0; i < arr.length - 1; i++)
	    builder.append(arr[i] + ", ");

	builder.append(arr[arr.length - 1] + "}");
	return builder.toString();
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(long[] arr) {
	final StringBuilder builder = new StringBuilder("{");

	for (int i = 0; i < arr.length - 1; i++)
	    builder.append(arr[i] + ", ");

	builder.append(arr[arr.length - 1] + "}");
	return builder.toString();
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(float[] arr) {
	final StringBuilder builder = new StringBuilder("{");

	for (int i = 0; i < arr.length - 1; i++)
	    builder.append(arr[i] + ", ");

	builder.append(arr[arr.length - 1] + "}");
	return builder.toString();
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(double[] arr) {
	final StringBuilder builder = new StringBuilder("{");

	for (int i = 0; i < arr.length - 1; i++)
	    builder.append(arr[i] + ", ");

	builder.append(arr[arr.length - 1] + "}");
	return builder.toString();
    }

    /**
         * Convert an array into a string of the form {element1, element2,
         * element3... }
         * 
         * @param <T>
         *                The type of the array
         * @param arr
         *                The array to be converted into string form
         * @return A string representation of the array
         */
    public static <T> String arrayToString(boolean[] arr) {
	final StringBuilder builder = new StringBuilder("{");

	for (int i = 0; i < arr.length - 1; i++)
	    builder.append(arr[i] + ", ");

	builder.append(arr[arr.length - 1] + "}");
	return builder.toString();
    }

    /**
         * (Somewhat) efficiently convert an array into a vector (requires a
         * full copy of the array to the vector).
         * 
         * @param arr
         *                The array to be converted into a vector
         * @return A vector containing all the elements of arr
         */
    public static <T> ArrayList<T> arrayToArrayList(T[] arr) {
	ArrayList<T> v = new ArrayList<T>(arr.length);
	for (final T obj : arr)
	    v.add(obj);
	return v;
    }

    /**
         * Find out if a sorted array contains a value
         * 
         * @param arr
         *                The array to be searched
         * @param key
         *                The value to find in the array
         * @return True if the value is found in the array
         */
    public static boolean sortedArrayContains(int[] arr, int key) {
	return Arrays.binarySearch(arr, key) != -1;
    }

    /**
         * Find out if an unsorted array contains a value
         * 
         * @param arr
         *                The array to be searched
         * @param key
         *                The value to find in the array
         * @return True if the value is found in the array
         */
    public static boolean unsortedArrayContains(int[] arr, int key) {
	for (int i = 0; i < arr.length; i++)
	    if (arr[i] == key)
		return true;
	return false;
    }

    /**
         * Find out if an unsorted array contains a value
         * 
         * @param arr
         *                The array to be searched
         * @param key
         *                The value to find in the array
         * @return True if the value is found in the array
         */
    public static <T> boolean unsortedArrayContains(T[] arr, T key) {
	for (int i = 0; i < arr.length; i++)
	    if (arr[i].equals(key))
		return true;
	return false;
    }

    public static int findInUnsortedArray(int[] arr, int key) {
	return findInUnsortedArray(arr, key, 0);
    }

    public static int findInUnsortedArray(int[] arr, int key, int nStartIndex) {
	for (int i = nStartIndex; i < arr.length; i++)
	    if (arr[i] == key)
		return i;
	return -1;
    }

    /**
         * Find where in an array a value is. Searching starts from element 0.
         * 
         * @param arr
         *                The array to be searched
         * @param key
         *                The value to find in the array
         * @return The index in the array where the value was found or -1 if it
         *         was not found.
         */
    public static <T> int findInUnsortedArray(T[] arr, T key) {
	return findInUnsortedArray(arr, key, 0);
    }

    /**
         * Find where in an array a value is, given a starting index.
         * 
         * @param arr
         *                The array to be searched
         * @param key
         *                The value to find in the array
         * @param nStartIndex
         *                The first element that will be searched.
         * @return The index in the array where the value was found or -1 if it
         *         was not found.
         */
    public static <T> int findInUnsortedArray(T[] arr, T key, int nStartIndex) {
	for (int i = nStartIndex; i < arr.length; i++)
	    if (arr[i].equals(key))
		return i;
	return -1;
    }

    /**
         * Remove all non-unique entries from an AbstractList (such as a Vector)
         * that has already been sorted.
         * 
         * @param <T>
         * @param v
         */
    public static <T> void pruneNonUniqueFromSortedVector(final AbstractList<T> v) {
	if (v.size() != 0) {
	    T prevValue = v.get(0);
	    for (int i = 1; i < v.size(); i++) {
		T curValue = v.get(i);
		if (curValue.equals(prevValue)) {
		    v.remove(i);
		    i--;
		}
		prevValue = curValue;
	    }
	}
    }

    /**
         * A modification of Arrays.binarySearch that will search only a
         * specified range. If multiple keys of the same value are present, then
         * it is not defined which index will be returned.
         */
    public static <T extends Comparable<T>> int binarySearch(T[] a, T key, final int nFirst,
	    final int nLast) {
	assert nFirst >= 0;
	assert nLast <= a.length - 1;
	int low = nFirst;
	int high = nLast;

	while (low <= high) {
	    int mid = (low + high) >> 1;
	    int cmp = a[mid].compareTo(key);

	    if (cmp < 0)
		low = mid + 1;
	    else if (cmp > 0)
		high = mid - 1;
	    else
		return mid; // key found
	}
	return -(low + 1); // key not found.
    }

    /**
         * A modification of Arrays.binarySearch that will search only a
         * specified range. If multiple keys of the same value are present, then
         * this method will return the index of the first element with that key.
         * 
         * @param a
         * @param key
         * @param nFirst
         *                The first index to be searched
         * @param nLast
         *                The last index to be searched
         */
    public static <T extends Comparable<T>> int binarySearchForFirstIndex(T[] a, T key,
	    final int nFirst, final int nLast) {

	int nIndex = binarySearch(a, key, nFirst, nLast);
	while (nIndex >= nFirst && a[nIndex - 1].compareTo(key) == 0) {
	    nIndex--;
	}
	return nIndex;

    }

    /**
         * A modification of Arrays.binarySearch that will search only a
         * specified range. If multiple keys of the same value are present, then
         * this method will return the index of the first element with that key.
         * 
         * @param a
         * @param key
         */
    public static <T extends Comparable<T>> int binarySearchForFirstIndex(T[] a, T key) {

	int nIndex = Arrays.binarySearch(a, key);
	while (nIndex > 0 && a[nIndex - 1].compareTo(key) == 0) {
	    nIndex--;
	}
	return nIndex;

    }

    public static <T> ArrayList<T> toArrayList(T[] arr) {
	final ArrayList<T> list = new ArrayList<T>(arr.length);
	for (final T item : arr)
	    list.add(item);
	return list;
    }

    public static <T> Vector<T> toVector(T[] arr) {
	final Vector<T> list = new Vector<T>(arr.length);
	for (final T item : arr)
	    list.add(item);
	return list;
    }

    public static int indexOfMax(double[] arr) {
	double maxValue = Double.NEGATIVE_INFINITY;
	int nMax = 0;
	for (int i = 0; i < arr.length; i++) {
	    if (arr[i] > maxValue) {
		maxValue = arr[i];
		nMax = i;
	    }
	}
	return nMax;
    }

    /**
         * Find the index of the nth largest value in the array
         * 
         * @param arr
         * @param n
         * @return
         */
    public static int indexOfMax(double[] arr, int n) {
	if (n > arr.length)
	    throw new IllegalArgumentException("n > arr.length: " + n);

	double[] max = new double[n];
	int[] indexes = new int[n];
	for (int i = 0; i < n; i++)
	    max[i] = Double.NEGATIVE_INFINITY;

	for (int i = 0; i < arr.length; i++) {
	    for (int j = 0; j < n; j++) {
		if (arr[i] > max[j]) {
		    
		    // first copy all values less than this
		    for (int k = n - 1; k > j; k--) {
			max[k] = max[k - 1];
			indexes[k] = indexes[k - 1];
		    }
		    
		    // now insert our new "maximum" for this position
		    max[j] = arr[i];
		    indexes[j] = i;
		    break;
		}
	    }
	}

	return indexes[n - 1];
    }
    
    public static void main(String... args) {
	double[] x = new double[] {1,3,2,4};
	System.out.println(x[indexOfMax(x, 3)]);
    }
}
