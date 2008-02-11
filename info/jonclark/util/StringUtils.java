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

import info.jonclark.lang.IntRange;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A few utility functions for strings. Use of intern() is recommended so that
 * equalities may be expressed as == instead of .equals()
 */
public class StringUtils {

	/**
	 * Count the occurances of <code>substring</code> within
	 * <code>searchable</code>
	 * 
	 * @param searchable
	 *            The string containing zero or more occurances of a character
	 *            <code>c</code>
	 * @param c
	 *            The character that we hope to find
	 * @return
	 */
	public static int countOccurances(String searchable, char c) {
		int nOccurances = 0;
		int nBegin = searchable.indexOf(c);
		while (nBegin != -1) {
			nOccurances++;
			nBegin = searchable.indexOf(c, nBegin + 1);
		}
		return nOccurances;
	}

	/**
	 * Count the occurances of any of the <code>delims</code> within
	 * <code>searchable</code>
	 * 
	 * @param searchable
	 *            The string containing zero or more occurances of one of the
	 *            <code>delims</code>
	 * @param delims
	 *            A string containing multiple single-character delimiters, any
	 *            of which could be a
	 * @return
	 */
	public static int countOccurancesOfAnyDelim(final String searchable, final String delims) {
		int nOccurances = 0;
		int nBegin = searchable.indexOf(delims);
		while (nBegin != -1) {
			nOccurances++;
			final int nEnd = nBegin + delims.length();
			nBegin = searchable.indexOf(delims, nEnd);
		}
		return nOccurances;
	}

	/**
	 * Count the occurances of <code>substring</code> within
	 * <code>searchable</code>
	 * 
	 * @param searchable
	 *            The string containing zero or more occurances of
	 *            <code>substring</code>
	 * @param substring
	 *            The substring that we hope to find
	 * @return
	 */
	public static int countOccurancesOfSingleDelim(final String searchable, final String substring) {
		int nOccurances = 0;
		int nBegin = searchable.indexOf(substring);
		while (nBegin != -1) {
			nOccurances++;
			final int nEnd = nBegin + substring.length();
			nBegin = searchable.indexOf(substring, nEnd);
		}
		return nOccurances;
	}

	/**
	 * Counts the space-delimited tokens in a string
	 * 
	 * @param str
	 * @return
	 */
	public static int countTokens(final String str) {
		return countOccurances(str, ' ') + 1;
	}

	public static String substringBetweenMatching(String str, char open, char close)
			throws ParseException {

		int start = str.indexOf(open);
		if (start == -1) {
			throw new ParseException("String does not start with " + open + ": " + str, 0);
		}
		int end = findMatching(str, start, open, close);

		assert end < str.length() : "Matching " + close + " out of range: " + end
				+ " for string of length " + str.length();

		return str.substring(start + 1, end);
	}

	public static String substringBetweenMatching(String str, String open, String close)
			throws ParseException {

		int start = str.indexOf(open);
		if (start == -1) {
			throw new ParseException("String does not start with " + open + ": " + str, 0);
		}
		int end = findMatching(str, start, open, close);

		assert end < str.length() : "Matching " + close + " out of range: " + end
				+ " for string of length " + str.length();

		return str.substring(start + 1, end);
	}

	public static int findMatching(String str, int start, char open, char close)
			throws ParseException {

		int n = indexOfMatching(str, start, open, close);

		if (n == -1)
			throw new ParseException("No matching " + close + " found for string: " + str, start);
		else
			return n;
	}

	public static int indexOfMatching(String str, int start, char open, char close) {

		assert start > -1 : "Negative starting index: " + start;
		assert str.charAt(start) == open : "Does not start with " + open + ": " + str;

		int nUnclosed = 1;
		for (int i = start + 1; i < str.length(); i++) {

			char c = str.charAt(i);
			if (c == open)
				nUnclosed++;
			else if (c == close)
				nUnclosed--;

			if (nUnclosed == 0)
				return i;
		}

		return -1;
	}

	public static int indexOfMatching(String str, int start, String open, String close) {

		assert start > -1 : "Negative starting index: " + start;
		assert str.startsWith(open, start) : "Does not start with " + open + ": " + str;

		int nUnclosed = 1;
		for (int i = start + 1; i < str.length(); i++) {

			if (str.startsWith(open, i)) {
				nUnclosed++;
				i += open.length() - 1;
			} else if (str.startsWith(close, i)) {
				nUnclosed--;
				i += close.length() - 1;
			}

			if (nUnclosed == 0)
				return i;
		}

		return -1;
	}

	public static int findMatching(String str, int start, String open, String close)
			throws ParseException {

		int n = indexOfMatching(str, start, open, close);
		if (n == -1)
			throw new ParseException("No matching " + close + " found for string: " + str, start);
		else
			return n;
	}

	/**
	 * Replace matching elements. e.g. changing matching double quotes into
	 * latex-style.
	 * <p>
	 * <b>WARNING:</b> This is currently not a very efficient algorithm,
	 * especially for large strings!
	 * 
	 * @param str
	 * @param open
	 * @param close
	 * @param replaceOpen
	 * @param replaceClose
	 * @return
	 */
	public static String replaceMatching(String str, String open, String close, String replaceOpen,
			String replaceClose) {

		int nPrev = 0;
		int nBegin = str.indexOf(open);
		while (nBegin != -1) {
			int nMatch = indexOfMatching(str, nBegin, open, close);
			if (nMatch == -1)
				break;

			str =
					str.substring(nPrev, nBegin) + replaceOpen
							+ str.substring(nBegin + open.length(), nMatch) + replaceClose
							+ str.substring(nMatch + close.length());
		}

		return str;
	}

	/**
	 * A quick and dirty way of doing context-sensitive variable substitutions.
	 * 
	 * @return
	 * @throws ParseException
	 * @throws TransformException
	 */
	public static String replaceBetweenMatching(String str, String open, String close,
			boolean outputOpen, boolean outputClose, Transducer transducer)
			throws TransformException {

		if (!str.contains(open) || !str.contains(close)) {
			return str;
		}

		StringBuilder builder = new StringBuilder();
		int nBegin = str.indexOf(open);
		int nEnd = 0;
		while (nBegin != -1) {

			// put everything else into the document
			String unaffectedPortion = str.substring(nEnd, nBegin);
			builder.append(unaffectedPortion);

			// don't include opening tag in output
			if (!outputOpen)
				nBegin += open.length();

			// find the end of this portion and loop over variable substitutions
			nEnd = str.indexOf(close, nBegin);
			if (nEnd == -1)
				throw new TransformException("matching " + close + " not found after " + nBegin);

			String between = str.substring(nBegin, nEnd);
			String replacement = transducer.transform(between);
			builder.append(replacement);

			// don't include closing tag in output
			if (!outputClose)
				nEnd += close.length();

			nBegin = str.indexOf(open, nEnd);
		}
		if (nEnd < str.length()) {
			String leftovers = str.substring(nEnd);
			builder.append(leftovers);
		}
		return builder.toString();
	}

	/**
	 * Counts the tokens in a string as delimited by a single character
	 * 
	 * @param str
	 * @param delims
	 * @return
	 */
	public static int countTokens(final String str, final char delim) {
		return countOccurances(str, delim) + 1;
	}

	/**
	 * Remove a specified number of characters from the end of a string.
	 * Functions much the same as String.substring(n) except that it works from
	 * the right side of the string backward.
	 * 
	 * @param in
	 *            The string that will have characters removed from it
	 * @param nCharsToCut
	 *            The number of characters that will be removed
	 * @return
	 */
	public static String cutCharsFromEnd(String in, int nCharsToCut) {
		if (in.length() > nCharsToCut)
			return in.substring(0, in.length() - nCharsToCut);
		else
			return in;
	}

	/**
	 * Returns a String of a character duplicated a given number of times.
	 * 
	 * @param c
	 *            The character.
	 * @param nTimes
	 *            The number of times it should be duplicated.
	 * @return A string of length nTimes containing only the character c.
	 */
	public static String duplicateCharacter(final char c, final int nTimes) {
		if (nTimes > 0) {
			final StringBuilder builder = new StringBuilder(nTimes);
			for (int i = 0; i < nTimes; i++)
				builder.append(c);
			return builder.toString();
		} else {
			return "";
		}
	}

	/**
	 * Replace all unicode characters in a string with their corresponding
	 * unicode escape sequences
	 * 
	 * @param unicode
	 *            A string possibly containing unicode characters
	 * @return A string having all ASCII characters with all unicode characters
	 *         from <code>unicode</code> replaced with their escape sequences
	 */
	public static String escapeUnicode(final String unicode) {
		String escaped = unicode;
		for (int i = 0; i < unicode.length(); i++) {
			if (unicode.charAt(i) > 0xFF) {
				final String replacement =
						"\\u" + forceNumberLength(Integer.toHexString(unicode.charAt(i)), 4);
				// System.out.println("Replacing " + unicode.charAt(i) + " with
				// " + replacement);
				escaped = replaceFast(escaped, unicode.charAt(i) + "", replacement);
			}
		}
		return escaped;
	}

	/**
	 * Forces a number (probably a hex or binary number) to have a certain
	 * number of digits. If a number has less than the required number of
	 * digits, leading zeros are prepended.
	 * 
	 * @param str
	 *            The number string
	 * @param nMinDigits
	 *            The minimum number of digits in the output string
	 * @return The inputted number with leading zeros prepended, if necessary
	 */
	public static String forceNumberLength(String str, final int nMinDigits) {
		if (str.length() < nMinDigits)
			str = duplicateCharacter('0', nMinDigits - str.length()) + str;
		return str;
	}

	public static String formatDouble(double d, int nMaxDecimalDigits) {
		String str = d + "";
		final int nDecimalPoint = str.indexOf('.');
		if (nDecimalPoint != -1) {
			final int len = Math.min(nDecimalPoint + nMaxDecimalDigits + 1, str.length());
			str = str.substring(0, len);
		}
		return str;
	}

	/**
	 * Get a nicely formatted percentage with 1 place of decimal accuracy
	 * 
	 * @param numerator
	 *            "Top" number for this percentage.
	 * @param denominator
	 *            "Bottom" number for this percentage.
	 * @return A value such as "12.2%"
	 */
	public static String getPercentage(double numerator, double denominator) {
		return formatDouble(numerator / denominator, 1) + "%";
	}

	/**
	 * Get a String representation of a StackTrace from a Throwable object
	 * without being forced to write it to stderr.
	 * 
	 * @param t
	 *            The <code>Throwable</code> object containing the stack trace
	 * @return A string representation of the StackTrace
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter s = new StringWriter();
		t.printStackTrace(new PrintWriter(s));
		return s.toString();
	}

	/**
	 * Determine if a string has a substring (of at least length zero) between a
	 * <code>prefix</code> and a <code>suffix</code>.
	 * 
	 * @param in
	 *            The string (possibly) containing <code>prefix</code> and
	 *            <code>suffix</code>
	 * @param prefix
	 *            The string occuring just before the substring to be returned.
	 * @param suffix
	 *            The string occuring just after the substring to be returned.
	 * @return True if the string contains <code>prefix</code> before
	 *         <code>suffix</code>, false otherwise.
	 */
	public static boolean hasSubstringBetween(final String in, final String prefix,
			final String suffix) {
		final int nBeginIndex = in.indexOf(prefix);
		if (nBeginIndex != -1) {
			final int nEndIndex = in.indexOf(suffix, nBeginIndex);
			return nEndIndex != -1;
		} else {
			return false;
		}
	}

	/**
	 * Ensures that all elements of the array <code>tokens</code> is a member
	 * of Java's internal String pool by calling String.intern() on them.
	 * 
	 * @param tokens
	 *            The array for which all member Strings will be pooled.
	 */
	public static void internTokens(final String[] tokens) {
		for (int i = 0; i < tokens.length; i++)
			tokens[i] = tokens[i].intern();
	}

	/**
	 * Determine if a string <code>str</code> is composed entirely of
	 * characters from a <code>vocabulary</code>.
	 * 
	 * @param str
	 *            The string to be tested.
	 * @param vocabulary
	 *            The vocabulary of characters that the string must be composed
	 *            of.
	 * @return True if str is composed entirely of characters from the
	 *         vocabulary.
	 */
	public static boolean isComposedOf(String str, String vocabulary) {
		for (int i = 0; i < str.length(); i++) {
			if (vocabulary.indexOf(str.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

	public static String nullToEmpty(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	/**
	 * Returns <code>true</code> if all elements of <code>ranges</code> are
	 * in ascending order according to their each range's first element.
	 * 
	 * @param ranges
	 * @return
	 */
	public static boolean rangesAreOrdered(final AbstractList<IntRange> ranges) {
		int nPrevFirst = Integer.MIN_VALUE;
		for (IntRange range : ranges) {
			if (range.first < nPrevFirst)
				return false;
			nPrevFirst = range.first;
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if no range has a starting position <= to the
	 * previous range in the vector. This method should be used in conjunction
	 * with <code>rangesAreOrdered()</code> to guarantee no overlaps. That is,
	 * this method does NOT check for all possible combination of overlaps by
	 * itself.
	 * 
	 * @param ranges
	 * @return
	 */
	public static boolean rangesDoNotOverlap(final AbstractList<IntRange> ranges) {
		int nPrevLast = Integer.MIN_VALUE;
		for (IntRange range : ranges) {
			if (range.first <= nPrevLast)
				return false;
			nPrevLast = range.last;
		}
		return true;
	}

	/**
	 * Removes a leading string from a target string.
	 * <p>
	 * e.g. Removing the leading string "abc" from "abcxyz" produces "xyz".
	 * 
	 * @param target
	 *            The target string, possibly containing the trailing string.
	 * @param leading
	 *            The leading string to be removed.
	 * @return The target minus the leading string, if it is found. Otherwise,
	 *         the target string is returned.
	 */
	public static String removeLeadingString(String target, String leading) {
		if (target.startsWith(leading)) {
			return target.substring(leading.length());
		} else {
			return target;
		}
	}

	/**
	 * Removes a trailing string from a target string.
	 * <p>
	 * e.g. Removing the traling string "xyz" from "abcxyz" produces "abc".
	 * 
	 * @param target
	 *            The target string, possibly containing the trailing string.
	 * @param trailing
	 *            The trailing string to be removed.
	 * @return The target minus the trailing string, if it is found. Otherwise,
	 *         the target string is returned.
	 */
	public static String removeTrailingString(String target, String trailing) {
		if (target.endsWith(trailing)) {
			return cutCharsFromEnd(target, trailing.length());
		} else {
			return target;
		}
	}

	/**
	 * Replaces each string at <code>oldArr[i]</code> with
	 * <code>replacementArr[i]</code> within <code>target</code>. Note that
	 * <code>oldArr.size()</code> and <code>replacementArr.size()</code>
	 * must be equal. Also, the ranges in <code>oldArr</code> must be in
	 * ascending order and must not contain any overlapping ranges.
	 * <p>
	 * FYI: This method was written with tokenization in mind. This allows all
	 * regions that are to be tokenized to be replaced at one time, thus
	 * requiring only a single buffer reallocation.
	 * 
	 * @param target
	 *            The string on which the replace operation will be performed
	 * @param oldArr
	 *            The strings that will be replaced
	 * @param replacementArr
	 *            The strings that will be substituted for <code>old</code>
	 * @return The string with all replacements made
	 */
	public static String replaceFast(String target, final AbstractList<IntRange> oldArr,
			final AbstractList<String> replacementArr) {
		char[] buffer = null;

		assert oldArr.size() == replacementArr.size() : "old.length != replacement.length";
		assert rangesAreOrdered(oldArr) : "oldArr not in ascending order.";
		assert rangesDoNotOverlap(oldArr) : "oldArr contains overlapping ranges.";

		// if any pair of old and replacement not of equal length, we need a new
		// buffer size
		boolean resultIsSameLength = true;
		for (int i = 0; i < oldArr.size() && resultIsSameLength; i++)
			resultIsSameLength &= oldArr.get(i).length() == replacementArr.get(i).length();

		if (resultIsSameLength) {
			// if the old and replacement strings are the same length
			// we can make this much faster

			// iterate through all the replacements being made
			for (int i = 0; i < oldArr.size(); i++) {
				final IntRange oldRange = oldArr.get(i);
				final String strReplacement = replacementArr.get(i);

				// only allocate a buffer if we make a replacement
				if (buffer == null)
					buffer = target.toCharArray();

				strReplacement.getChars(0, strReplacement.length(), buffer, oldRange.first);
			}

		} else {

			// calculate size of new buffer first so that we
			// minimize the amount of copying we do
			int nTotalLengthDelta = 0;
			for (int i = 0; i < oldArr.size(); i++)
				nTotalLengthDelta += replacementArr.get(i).length() - oldArr.get(i).length();
			buffer = new char[target.length() + nTotalLengthDelta];

			// now alternate copying from target and replacement
			int nPrevEnd = 0;
			int nDestCursor = 0;
			for (int i = 0; i < oldArr.size(); i++) {
				final int nBegin = oldArr.get(i).first;
				final String strReplacement = replacementArr.get(i);

				// get the target string, starting at the end of the
				// previous match with length of the string between this
				// match and the previous one; this string will be
				// copied to the destination cursor
				target.getChars(nPrevEnd, nBegin, buffer, nDestCursor);
				nDestCursor += nBegin - nPrevEnd;
				strReplacement.getChars(0, strReplacement.length(), buffer, nDestCursor);

				// update positions for next copy
				nDestCursor += strReplacement.length();
				nPrevEnd = oldArr.get(i).last;
			}
			target.getChars(nPrevEnd, target.length(), buffer, nDestCursor);
		}

		if (buffer == null)
			return target;
		else
			return new String(buffer);
	}

	/**
	 * Replaces all occurances of <code>old</code> with
	 * <code>replacement</code> within <code>target</code>.
	 * 
	 * @param target
	 *            The string on which the replace operation will be performed
	 * @param old
	 *            The strings that will be replaced
	 * @param replacement
	 *            The strings that will be substituted for <code>old</code>
	 * @return The string with all replacements made
	 */
	public static String replaceFast(String target, String old, String replacement) {
		char[] buffer = null;

		if (old.length() == replacement.length()) {

			// if the old and replacement strings are the same length
			// we can make this much faster
			int nBegin = target.indexOf(old);
			while (nBegin != -1) {

				// only allocate a buffer if we make a replacement
				if (buffer == null)
					buffer = target.toCharArray();

				replacement.getChars(0, replacement.length(), buffer, nBegin);

				final int nEnd = nBegin + old.length();
				nBegin = target.indexOf(old, nEnd);
			}

		} else {

			// calculate size of new buffer first so that we
			// minimize the amount of copying we do
			final int nOccurances = countOccurancesOfSingleDelim(target, old);
			if (nOccurances > 0) {
				int nDelta = replacement.length() - old.length();
				buffer = new char[target.length() + nDelta * nOccurances];

				// now alternate copying from target and replacement
				int nBegin = target.indexOf(old);
				int nPrevEnd = 0;

				int nDestCursor = 0;
				while (nBegin != -1) {
					// get the target string, starting at the end of the
					// previous match
					// with length of the string between this match and the
					// previous one;
					// this string will be copied to the destination cursor
					target.getChars(nPrevEnd, nBegin, buffer, nDestCursor);
					nDestCursor += nBegin - nPrevEnd;
					replacement.getChars(0, replacement.length(), buffer, nDestCursor);

					// update positions for next copy
					nDestCursor += replacement.length();
					nPrevEnd = nBegin + old.length();
					nBegin = target.indexOf(old, nPrevEnd);
				}
				target.getChars(nPrevEnd, target.length(), buffer, nDestCursor);

			}

		}

		if (buffer == null)
			return target;
		else
			return new String(buffer);
	}

	public static String replaceFast(String target, final String[] oldArr,
			final String[] replacementArr) {

		// TODO: Make this more efficient
		String result = target;
		assert oldArr.length == replacementArr.length;
		for (int i = 0; i < oldArr.length; i++) {
			result = StringUtils.replaceFast(result, oldArr[i], replacementArr[i]);
		}

		return result;
	}

	/**
	 * Takes only the first <code>n</code> characters of a string
	 * <code>str</code>.
	 * 
	 * @param str
	 *            The string to be restricted.
	 * @param n
	 *            The maximum number of characters to be accepted.
	 * @return <code>str</code> if the string is already less than
	 *         <code>n</code>, otherwise a string containing the first
	 *         <code>n</code> characters of <code>str</code>.
	 */
	public static String restrictLength(String str, int n) {
		if (str.length() < n)
			return str;
		else
			return str.substring(0, n);
	}

	/**
	 * Reverse the given string. This method is based off of reverse in
	 * StringBuilder so that it will remain compatible with unicode.
	 */
	public static void reverse(char[] str, int first, int length) {

		int endIndex = first + length;
		boolean hasSurrogate = false;
		int n = endIndex - 1;
		for (int j = (n - 1) >> 1; j >= first; --j) {
			char temp = str[j];
			char temp2 = str[n - j];
			if (!hasSurrogate) {
				hasSurrogate =
						(temp >= Character.MIN_SURROGATE && temp <= Character.MAX_SURROGATE)
								|| (temp2 >= Character.MIN_SURROGATE && temp2 <= Character.MAX_SURROGATE);
			}
			str[j] = temp2;
			str[n - j] = temp;
		}
		if (hasSurrogate) {
			// Reverse back all valid surrogate pairs
			for (int i = first; i < endIndex - 1; i++) {
				char c2 = str[i];
				if (Character.isLowSurrogate(c2)) {
					char c1 = str[i + 1];
					if (Character.isHighSurrogate(c1)) {
						str[i++] = c1;
						str[i] = c2;
					}
				}
			}
		}
	}

	public static String reverse(String str) {
		final char[] arr = str.toCharArray();
		reverse(arr, 0, arr.length);
		return new String(arr);
	}

	public static String reverse(String str, int first, int length) {
		final char[] arr = str.toCharArray();
		reverse(arr, first, length);
		return new String(arr);
	}

	/**
	 * Acts much like <code>tokenize()</code> except that the entire delimiter
	 * string must be present for a split to be made.
	 * 
	 * @param str
	 * @param delim
	 *            The delimiter substring to search for.
	 * @param nMaxSplits
	 *            The maximum size of the returned array. Even if not all
	 *            delimiters have been exhaustyed, the last element of the array
	 *            will contain the remaining portion of the input string.
	 * @return
	 */
	public static String[] split(String str, String delim, int nMaxSplits) {

		final int nOccurances = Math.min(countOccurancesOfSingleDelim(str, delim) + 1, nMaxSplits);
		final String[] tokens = new String[nOccurances];

		// begin and end positions OF THE LAST DELIM FOUND
		int nBegin = str.indexOf(delim);
		int nEnd = 0;

		for (int i = 0; i < nOccurances - 1; i++) {
			tokens[i] = str.substring(nEnd, nBegin);
			nEnd = nBegin + delim.length();

			nBegin = str.indexOf(delim, nEnd);
		}

		if (nOccurances > 0) {
			tokens[tokens.length - 1] = str.substring(nEnd);
		}

		return tokens;
	}

	/**
	 * Get the substring of <code>in</code> that occurs after the string
	 * <code>delim</code>
	 * 
	 * @param in
	 *            The string (possibly) containing <code>delim</code>
	 * @param delim
	 *            The string (possibly) contained in <code>in</code>
	 * @return The string after the delimiter if the delimiter was found,
	 *         otherwise the original string
	 */
	public static String substringAfter(String in, String delim) {
		return substringAfter(in, delim, false);
	}

	/**
	 * Get the substring of <code>in</code> that occurs after the string
	 * <code>delim</code>
	 * 
	 * @param in
	 *            The string (possibly) containing <code>delim</code>
	 * @param delim
	 *            The string (possibly) contained in <code>in</code>
	 * @param returnDelims
	 *            Should the delimiter <code>delim</code> be part of the
	 *            returned String?
	 * @return The string after the delimiter if the delimiter was found,
	 *         otherwise the original string
	 */
	public static String substringAfter(String in, String delim, boolean returnDelims) {
		int nIndex = in.indexOf(delim);
		if (nIndex != -1 && nIndex < in.length()) {
			if (returnDelims)
				return in.substring(nIndex);
			else
				return in.substring(nIndex + delim.length());
		} else {
			return in;
		}
	}

	public static String substringAfterLast(String in, String delim) {
		return substringAfterLast(in, delim, false);
	}

	public static String substringAfterLast(String in, String delim, boolean returnDelims) {
		int nIndex = in.lastIndexOf(delim);
		if (nIndex != -1 && nIndex < in.length()) {
			if (returnDelims)
				return in.substring(nIndex);
			else
				return in.substring(nIndex + delim.length());
		} else {
			return in;
		}
	}

	/**
	 * Get the substring of <code>in</code> that occurs before the string
	 * <code>delim</code>
	 * 
	 * @param in
	 *            The string (possibly) containing <code>delim</code>
	 * @param delim
	 *            The string (possibly) contained in <code>in</code>
	 * @return The string before the delimiter if the delimiter was found,
	 *         otherwise the original string
	 */
	public static String substringBefore(String in, String delim) {
		return substringBefore(in, delim, false);
	}

	/**
	 * Get the substring of <code>in</code> that occurs before the string
	 * <code>delim</code>
	 * 
	 * @param in
	 *            The string (possibly) containing <code>delim</code>
	 * @param delim
	 *            The string (possibly) contained in <code>in</code>
	 * @param returnDelims
	 *            Should the delimiter <code>delim</code> be part of the
	 *            returned String?
	 * @return The string before the delimiter if the delimiter was found,
	 *         otherwise the original string
	 */
	public static String substringBefore(String in, String delim, boolean returnDelims) {
		int nIndex = in.indexOf(delim);
		if (nIndex != -1) {
			if (returnDelims)
				return in.substring(0, nIndex + delim.length());
			else
				return in.substring(0, nIndex);
		} else {
			return in;
		}
	}

	public static String substringBeforeLast(String in, String delim) {
		return substringBeforeLast(in, delim, false);
	}

	public static String substringBeforeLast(String in, String delim, boolean returnDelims) {
		int nIndex = in.lastIndexOf(delim);
		if (nIndex != -1) {
			if (returnDelims)
				return in.substring(0, nIndex + delim.length());
			else
				return in.substring(0, nIndex);
		} else {
			return in;
		}
	}

	/**
	 * Extract the first occurance within an input string <code>in</code> of a
	 * string between a <code>prefix</code> and a <code>suffix</code>.
	 * 
	 * @param in
	 *            The string (possibly) containing <code>prefix</code> and
	 *            <code>suffix</code>
	 * @param prefix
	 *            The string occuring just before the substring to be returned.
	 * @param suffix
	 *            The string occuring just after the substring to be returned.
	 * @return The string between <code>prefix</code> and <code>suffix</code>
	 *         if both were found, otherwise the original string.
	 */
	public static String substringBetween(String in, String prefix, String suffix) {
		return substringBetween(in, prefix, suffix, false);
	}

	/**
	 * Extract the first occurance within an input string <code>in</code> of a
	 * string between a <code>prefix</code> and a <code>suffix</code>.
	 * 
	 * @param in
	 *            The string (possibly) containing <code>prefix</code> and
	 *            <code>suffix</code>
	 * @param prefix
	 *            The string occuring just before the substring to be returned.
	 * @param suffix
	 *            The string occuring just after the substring to be returned.
	 * @param returnDelims
	 *            Should the delimiters <code>prefix</code> and
	 *            <code>suffix</code> be part of the returned String?
	 * @return The string between <code>prefix</code> and <code>suffix</code>
	 *         if both were found, otherwise the original string.
	 */
	public static String substringBetween(String in, String prefix, String suffix,
			boolean returnDelims) {

		// substring after
		int nBeginIndex = 0;
		{ // scope nPrefixIndex
			int nPrefixIndex = in.indexOf(prefix);
			if (returnDelims) {
				if (nPrefixIndex != -1 && nPrefixIndex < in.length())
					nBeginIndex = nPrefixIndex;
				else
					return in;
			} else {
				if (nPrefixIndex != -1 && (nPrefixIndex + prefix.length()) < in.length())
					nBeginIndex = nPrefixIndex + prefix.length();
				else
					return in;
			}
		}

		// substring before
		int nSuffixIndex = in.indexOf(suffix, nBeginIndex);
		if (returnDelims) {
			if (nSuffixIndex != -1 && (nSuffixIndex + suffix.length()) < in.length())
				return in.substring(nBeginIndex, nSuffixIndex + suffix.length());
			else
				return in;
		} else {
			if (nSuffixIndex != -1 && nSuffixIndex < in.length())
				return in.substring(nBeginIndex, nSuffixIndex);
			else
				return in;
		}
	}

	public static ArrayList<String> allSubstringsBetween(String in, String prefix, String suffix,
			boolean returnDelims) {

		ArrayList<String> results = new ArrayList<String>();

		int i = 0;
		while (i < in.length()) {
			// substring after
			int nBeginIndex = 0;
			{ // scope nPrefixIndex
				int nPrefixIndex = in.indexOf(prefix, i);
				if (returnDelims) {
					if (nPrefixIndex != -1 && nPrefixIndex < in.length())
						nBeginIndex = nPrefixIndex;
					else
						break;
				} else {
					if (nPrefixIndex != -1 && (nPrefixIndex + prefix.length()) < in.length())
						nBeginIndex = nPrefixIndex + prefix.length();
					else
						break;
				}
			}

			// substring before
			int nSuffixIndex = in.indexOf(suffix, nBeginIndex);
			if (returnDelims) {
				if (nSuffixIndex != -1 && (nSuffixIndex + suffix.length()) < in.length()) {
					String str = in.substring(nBeginIndex, nSuffixIndex + suffix.length());
					results.add(str);
					i = nSuffixIndex + suffix.length();
				} else {
					break;
				}
			} else {
				if (nSuffixIndex != -1 && nSuffixIndex < in.length()) {
					String str = in.substring(nBeginIndex, nSuffixIndex);
					results.add(str);
					i = nSuffixIndex + suffix.length();
				} else {
					break;
				}
			}
		}

		return results;
	}

	public static float[] toFloatArray(String[] str) {
		float[] n = new float[str.length];
		for (int i = 0; i < str.length; i++) {
			n[i] = Float.parseFloat(str[i]);
		}
		return n;
	}

	public static String toHexString(int nValue, int nMinDigits) {
		return "0x" + StringUtils.forceNumberLength(Integer.toHexString(nValue), 4);
	}

	public static int[] toIntArray(String[] str) {
		int[] n = new int[str.length];
		for (int i = 0; i < str.length; i++) {
			n[i] = Integer.parseInt(str[i]);
		}
		return n;
	}

	/**
	 * Create an array of tokens given a String separated delimited by spaces.
	 * 
	 * @param str
	 *            The string to be tokenized.
	 * @return An array with the tokens produced from str.
	 */
	public static String[] tokenize(String str) {
		StringTokenizer tok = new StringTokenizer(str);
		String[] result = new String[tok.countTokens()];
		for (int i = 0; i < result.length; i++)
			result[i] = tok.nextToken();
		return result;
	}

	/**
	 * Tokenize a string except for components between special characters. The
	 * target use of this is tokenizing value lists in which some values might
	 * be quoted (note that not all values must be quoted).
	 * 
	 * @param str
	 * @param c
	 * @return
	 * @throws ParseException
	 */
	public static ArrayList<String> tokenizeQuotedValues(String str, String delims,
			String openQuoteChars, String closeQuoteChars, boolean returnDelims,
			boolean returnQuotes, int nMaxSplits) throws ParseException {

		ArrayList<String> tokens = new ArrayList<String>();

		int i;
		int nPrevEnd = 0;
		for (i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			int nQuoteIndex = openQuoteChars.indexOf(c);
			if (nQuoteIndex != -1) {
				char cCloseQuote = closeQuoteChars.charAt(nQuoteIndex);

				int nEnd = str.indexOf(cCloseQuote, i + 1);
				if (nEnd == -1)
					nEnd = str.length() - 1;
				String token;

				if (returnQuotes) {
					token = str.substring(i, nEnd + 1);
				} else {
					token = str.substring(i + 1, nEnd);
				}

				tokens.add(token);
				nPrevEnd = nEnd + 1;
				i = nEnd;

			} else if (delims.indexOf(c) != -1) {

				String token = str.substring(nPrevEnd, i);
				if (token.length() > 0)
					tokens.add(token);

				if (returnDelims) {
					tokens.add(str.substring(i, i + 1));
				}

				nPrevEnd = i + 1;

			}

			if (tokens.size() == nMaxSplits - 1) {
				break;
			}
		}

		if (nPrevEnd != str.length()) {
			String remaining = str.substring(nPrevEnd);
			tokens.add(remaining);
		}

		return tokens;
	}

	public static int indexOfDelim(String str, String delims, int start) {

		if (start >= str.length())
			return -1;

		for (int i = start; i < str.length(); i++) {
			if (delims.indexOf(str.charAt(i)) != -1)
				return i;
		}

		return -1;
	}

	/**
	 * Tokenize a string delimited by any of a given set of single-character
	 * delimiters. For instance, "A.B,C!D" delimited by ".,!" would produce the
	 * array [A,B,C,D]
	 * 
	 * @param str
	 *            The string to be tokenized.
	 * @param delims
	 *            The set of single-character delimiters to be used in
	 *            tokenizing.
	 * @return The array of tokens produced from str.
	 */
	public static String[] tokenize(String str, String delims) {
		StringTokenizer tok = new StringTokenizer(str, delims);
		String[] result = new String[tok.countTokens()];
		for (int i = 0; i < result.length; i++)
			result[i] = tok.nextToken();
		return result;
	}

	/**
	 * @param str
	 * @param delims
	 * @param nMaxSplits
	 *            The maximum size of the returned array. Even if not all
	 *            delimiters have been exhaustyed, the last element of the array
	 *            will contain the remaining portion of the input string.
	 * @return
	 */
	public static String[] tokenize(String str, String delims, int nMaxSplits) {
		return tokenize(str, delims, nMaxSplits, false);
	}

	public static String[] tokenize(String str, String delims, int nMaxSplits, boolean returnDelims) {
		StringTokenizer tok = new StringTokenizer(str, delims, returnDelims);
		final int nReturnedTokens = Math.min(nMaxSplits, tok.countTokens());

		int nCharIterator = 0;
		String[] result = new String[nReturnedTokens];
		for (int i = 0; i < nReturnedTokens - 1; i++) {
			result[i] = tok.nextToken();
			nCharIterator += result[i].length();
			if (!returnDelims)
				nCharIterator++;
		}

		if (nCharIterator < str.length())
			result[nReturnedTokens - 1] = str.substring(nCharIterator);

		return result;
	}

	/**
	 * Create a single string from an array of tokens, adding the specified
	 * string <code>delim</code> between each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @param delim
	 *            The string that will be placed between each token.
	 * @return The String form of the token array.
	 */
	public static String untokenize(final float[] tokens, final String delim) {
		final StringBuilder builder = new StringBuilder();
		for (final float token : tokens)
			builder.append(token + delim);
		
		if(builder.length() >= delim.length())
			builder.delete(builder.length() - delim.length(), builder.length());
		
		return builder.toString();
	}

	/**
	 * Create a single string from an array of tokens, adding the specified
	 * string <code>delim</code> between each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @param delim
	 *            The string that will be placed between each token.
	 * @return The String form of the token array.
	 */
	public static String untokenize(final int[] tokens, final String delim) {
		final StringBuilder builder = new StringBuilder();
		for (final int token : tokens)
			builder.append(token + delim);

		if(builder.length() >= delim.length())
			builder.delete(builder.length() - delim.length(), builder.length());
		
		return builder.toString();
	}

	public static String untokenizeEnglish(final String[] tokens) {
		final StringBuilder builder = new StringBuilder();

		assert tokens != null;

		if (tokens.length > 2) {
			for (int i = 0; i < tokens.length - 2; i++)
				builder.append(tokens[i] + ", ");
			builder.append(tokens[tokens.length - 2] + ", and " + tokens[tokens.length - 1]);
		} else if (tokens.length == 2) {
			builder.append(tokens[0] + " and " + tokens[1]);
		} else if (tokens.length == 1) {
			builder.append(tokens[0]);
		}
		return builder.toString().trim();
	}

	public static String untokenize(final String[][] tokens) {
		final StringBuilder builder = new StringBuilder();
		for (final String[] moreTokens : tokens) {
			for (final String token : moreTokens)
				builder.append(token + " ");
			builder.append("\n");
		}
		return builder.toString().trim();
	}

	public static String untokenize(final String[][] tokens, boolean includeBlankLines) {
		final StringBuilder builder = new StringBuilder();
		for (final String[] moreTokens : tokens) {
			for (final String token : moreTokens)
				builder.append(token + " ");
			if (includeBlankLines || moreTokens.length != 0)
				builder.append("\n");
		}
		return builder.toString().trim();
	}

	/**
	 * Create a single string from an array of tokens, adding a space between
	 * each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @return The String form of the token array.
	 */
	public static String untokenize(final String[] tokens) {
		final StringBuilder builder = new StringBuilder();
		for (final String token : tokens)
			builder.append(token + " ");
		return builder.toString().trim();
	}
	
	public static <T> String untokenize(final T[] tokens) {
		return untokenize(tokens, " ");
	}
	
	public static <T> String untokenize(final T[] tokens, String delim) {
		final StringBuilder builder = new StringBuilder();
		for (final T token : tokens)
			builder.append(token + delim);

		if(builder.length() >= delim.length())
			builder.delete(builder.length() - delim.length(), builder.length());
		
		return builder.toString();
	}

	public static <T> String untokenize(final Iterable<T> tokens) {
		return untokenize(tokens, " ");
	}

	public static <T> String untokenize(final Iterable<T> tokens, String delim) {
		final StringBuilder builder = new StringBuilder();
		for (final T token : tokens)
			builder.append(token + delim);

		if(builder.length() >= delim.length())
			builder.delete(builder.length() - delim.length(), builder.length());
		
		return builder.toString();
	}

	/**
	 * Create a single string from an array of tokens, starting with the element
	 * having index <code>nStartElement</code> adding the specified string
	 * <code>delim</code> between each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @param nStartElement
	 *            The array index of the first element that will be included in
	 *            the output string.
	 * @return The String form of the given part of the token array.
	 */
	public static String untokenize(final String[] tokens, int nStartElement) {
		final StringBuilder builder = new StringBuilder();
		for (int i = nStartElement; i < tokens.length; i++)
			builder.append(tokens[i] + " ");
		return builder.toString().trim();
	}

	/**
	 * Create a single string from an array of tokens, starting with the element
	 * having index <code>nStartElement</code> and ending with
	 * <code>nLastElement</code> inclusive, adding the specified string
	 * <code>delim</code> between each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @param nStartElement
	 *            The array index of the first element that will be included in
	 *            the output string.
	 * @param nLastElement
	 *            The array index of the last element that will be included in
	 *            the output string.
	 * @return The String form of the given part of the token array.
	 */
	public static String untokenize(final String[] tokens, int nStartElement, int nLastElement) {
		final StringBuilder builder = new StringBuilder();
		for (int i = nStartElement; i <= nLastElement; i++)
			builder.append(tokens[i] + " ");
		return builder.toString().trim();
	}

	/**
	 * Create a single string from an array of tokens, adding the specified
	 * string <code>delim</code> between each token.
	 * 
	 * @param tokens
	 *            The array of tokens to be untokenized.
	 * @param delim
	 *            The string that will be placed between each token.
	 * @return The String form of the token array.
	 */
	public static String untokenize(final String[] tokens, final String delim) {
		final StringBuilder builder = new StringBuilder();
		for (final String token : tokens)
			builder.append(token + delim);

		if(builder.length() >= delim.length())
			builder.delete(builder.length() - delim.length(), builder.length());
		
		return builder.toString();
	}

	public static String whitespaceToSpace(String str, boolean removeNewLines) {
		char[] arr = str.toCharArray();
		for (int i = 0; i < str.length(); i++) {
			if (Character.isSpaceChar(str.codePointAt(i))) {
				if (removeNewLines || arr[i] != '\n')
					arr[i] = ' ';
			}
		}
		return new String(arr);
	}

	public static String forceSuffix(String in, String suffix) {
		if (!in.endsWith(suffix))
			return in + suffix;
		else
			return in;
	}

	public static void trimTokens(String[] tokens) {
		for (int i = 0; i < tokens.length; i++)
			tokens[i] = tokens[i].trim();
	}

	public static void main(String[] args) throws Exception {
		// String str = "hi \"how are$you\"$dog 'yo'";
		// String str = "actor undergoer";
		String str = "(1,1),(2,2),(3,3)";
		// for (String s : tokenizeQuotedValues(str, "\t\n ", "\"", false,
		// false, 15)) {
		for (String s : tokenizeQuotedValues(str, ",", "(", ")", false, false, 15)) {
			System.out.println(s);
		}

	}
}
