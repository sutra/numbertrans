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

import java.util.List;

/**
 * @author Jonathan
 */
public class MathUtils {
	private static final double LOG_BASE10_OF_2 = Math.log10(2.0);

	public static double log2(double a) {
		return Math.log10(a) / LOG_BASE10_OF_2;
	}

	public static double logBase(double a, double base) {
		return Math.log10(a) / Math.log10(base);
	}

	public static int min(List<Integer> list) {
		int min = Integer.MAX_VALUE;
		for (final int n : list) {
			min = Math.min(min, n);
		}
		return min;
	}

	public static int max(List<Integer> list) {
		int max = Integer.MIN_VALUE;
		for (final int n : list) {
			max = Math.max(max, n);
		}
		return max;
	}
	
	public static int max(int[] list) {
		int max = Integer.MIN_VALUE;
		for (final int n : list) {
			max = Math.max(max, n);
		}
		return max;
	}
	
	public static int min(int[] list) {
		int min = Integer.MAX_VALUE;
		for (final int n : list) {
			min = Math.min(min, n);
		}
		return min;
	}

	public static double midpoint(List<Integer> list) {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (final int n : list) {
			max = Math.max(max, n);
			min = Math.min(min, n);
		}
		return ((double) (min + max)) / 2;
	}

	public static double midpoint(double min, double max) {
		return (min + max) / 2;
	}

	public static double average(List<Integer> list) {
		int sum = 0;
		for (final int n : list) {
			sum += n;
		}
		return (double) sum / (double) list.size();
	}
}
