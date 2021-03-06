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
package net.sourceforge.numbertrans.languages.english;

import info.jonclark.lang.PrependStringBuilder;
import info.jonclark.util.FormatUtils;
import info.jonclark.util.StringUtils;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.base.AbstractNumber.Context;
import net.sourceforge.numbertrans.framework.scribe.CardinalScribe;

public class EnglishCardinalScribe extends CardinalScribe {

	public static final Form[] SUPPORTED_FORMS =
			{ Form.SHORT, Form.LONG, Form.LONG_WITHOUT_HYPHEN, Form.SHORT_WITH_COMMAS };

	private final String zero = "zero";
	private final String[] ones =
			{ "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine" };
	private final String[] teens =
			{ "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
					"eighteen", "nineteen" };
	private final String[] tens =
			{ null, null, "twenty", "thirty", "fourty", "fifty", "sixty", "seventy", "eighty",
					"ninety" };
	private final String hundred = "hundred";
	private final String[] magnitudes = { "", "thousand", "million", "billion", "trillion" };

	/**
	 * Create a new <code>EnglishCardinalScribe</code> object.
	 * 
	 * @param form
	 *            The form that will be generated by this CardinalScribe. See
	 *            the documentation for <code>Form</code> for more details.
	 */
	public EnglishCardinalScribe() {

		// TODO: Allow using a thousands separator (use an enum for this)
		// TODO: Allow using the short form
		super();
	}

	@Override
	public String getCardinalString(WholeNumber number, Form form) {
		if (form == Form.SHORT || form == Form.SHORT_WITH_COMMAS) {
			String str;
			if (form == Form.SHORT_WITH_COMMAS)
				str = FormatUtils.formatLong(number.getValue());
			else
				str = number.getValue() + "";

			str = StringUtils.duplicateCharacter('0', number.getLeadingZeros()) + str;
			return str;
		} else if (form == Form.LONG || form == Form.LONG_WITHOUT_HYPHEN) {
			PrependStringBuilder builder = new PrependStringBuilder();
			boolean useHyphen = (form == Form.LONG);

			int iMagnitude = 0;
			long magnitude = 1000;
			long remaining = number.getValue();

			if (remaining == 0) {
				return zero;
			} else {

				while (remaining > 0) {

					long value = remaining % magnitude;
					int block = (int) (value / (magnitude / 1000));
					assert block < 100;

					if (block != 0) {
						builder.prepend(" " + magnitudes[iMagnitude]);
						prependHundredsBlock(builder, block, useHyphen);
					}

					remaining -= value;
					magnitude *= 1000;
					iMagnitude++;
				}

				return builder.toString().trim();
			}
		} else {
			throw new RuntimeException("Unsupported form: " + form);
		}
	}

	private void prependHundredsBlock(PrependStringBuilder builder, int hundredsBlock,
			boolean useHyphen) {

		if (hundredsBlock > 0) {
			int tensBlock = hundredsBlock % 100;
			int hundredsDigit = (hundredsBlock - tensBlock) / 100;

			prependTensBlock(builder, tensBlock, useHyphen);
			if (hundredsDigit > 0) {
				builder.prepend(" " + ones[hundredsDigit] + " " + hundred);
			}
		}
	}

	private void prependTensBlock(PrependStringBuilder builder, int tensBlock, boolean useHyphen) {

		if (tensBlock > 0) {
			if (tensBlock >= 10 && tensBlock < 20) {
				int index = tensBlock - 10;
				builder.prepend(" " + teens[index]);

			} else {

				int onesDigit = tensBlock % 10;
				int tensDigit = (tensBlock - onesDigit) / 10;

				if (tensDigit != 0 && onesDigit != 0) {
					if (useHyphen) {
						builder.prepend(" " + tens[tensDigit] + "-" + ones[onesDigit]);
					} else {
						builder.prepend(" " + tens[tensDigit] + " " + ones[onesDigit]);
					}
				} else if (onesDigit == 0) {
					builder.prepend(" " + tens[tensDigit]);
				} else {
					builder.prepend(" " + ones[onesDigit]);
				}
			}
		}
	}

	public Form[] getSupportedForms() {
		return SUPPORTED_FORMS;
	}

	public static void main(String[] args) throws Exception {
		EnglishCardinalScribe scribe = new EnglishCardinalScribe();
		WholeNumber num = new WholeNumber(43590874350987l, 0, Context.CARDINAL);
		String str = scribe.getCardinalString(num, Form.LONG);
		System.out.println(str);
	}
}
