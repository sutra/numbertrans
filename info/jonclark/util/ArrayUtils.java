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

import java.util.*;

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
	if(arr == null) {
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
    public static <T> Vector<T> arrayToVector(T[] arr) {
	Vector<T> v = new Vector<T>(arr.length);
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
}
