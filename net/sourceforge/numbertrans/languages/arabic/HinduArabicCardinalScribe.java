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
package net.sourceforge.numbertrans.languages.arabic;

import net.sourceforge.numbertrans.framework.scribe.CharacterMappedCardinalScribe;

/**
 * A simple cardinal scribe for the Bengali language.
 */
public class HinduArabicCardinalScribe extends CharacterMappedCardinalScribe {

	public static final Form[] SUPPORTED_FORMS = { Form.SHORT };
	public static final char[] OTTOMAN_TURKISH_DIGITS =
			{ '٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩' };
	public static final char[] PERSIAN_URDU_DIGITS =
			{ '۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹' };

	public static enum DigitSet {
		OTTOMAN_TURKISH_DIGITS, PERSIAN_URDU_DIGITS
	};

	/**
	 * Create a new <code>HinduArabicCardinalScribe</code> object.
	 * 
	 * @param form
	 *            The form that will be generated by this CardinalScribe. See
	 *            the documentation for <code>Form</code> for more details.
	 */
	public HinduArabicCardinalScribe(HinduArabicCardinalScribe.DigitSet set) {
		// Messy syntax must be used to fulfil the requirement that super() is
		// the first statement.
		super(set == DigitSet.OTTOMAN_TURKISH_DIGITS ? OTTOMAN_TURKISH_DIGITS : PERSIAN_URDU_DIGITS);
	}
}
