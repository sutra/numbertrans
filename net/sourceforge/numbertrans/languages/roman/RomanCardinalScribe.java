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
package net.sourceforge.numbertrans.languages.roman;

import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.scribe.CardinalScribe;

public class RomanCardinalScribe extends CardinalScribe {

	private static final char[] romanChars = { 'I', 'V', 'X', 'L', 'C', 'D', 'M' };
	private static final int[] romanValues = { 1, 5, 10, 50, 100, 500, 1000 };

	public RomanCardinalScribe() {
		assert romanChars.length == romanValues.length : "Non-parallel arrays: chars and values";
	}

	@Override
	public String getCardinalString(WholeNumber number, Form form) {

		long value = number.getValue();
		final StringBuilder builder = new StringBuilder();

		for (int i = romanValues.length - 1; i >= 0 && value > 0; i--) {
			boolean changed = true;

			// continue at this value until the output string is unchanged.
			while (changed && value > 0) {

				changed = false;
				if (value >= romanValues[i]) {

					value -= romanValues[i];
					builder.append(romanChars[i]);
					changed = true;

				} else {

					// try to match a combination like "IV"
					int prependValue = 0;
					String romanCombo = null;
					if (i > 0) {
						// don't try combinations like "VX"
						// we want a full order of magnitude difference
						if (romanValues[i - 1] % 10 == 0) {
							prependValue = romanValues[i] - romanValues[i - 1];
							romanCombo = "" + romanChars[i - 1] + romanChars[i];
						} else if (i > 1) {
							// Skip the "middle value" such as 5 and take
							// the next value, which will always be an order
							// of magnitude less (e.g. 1) than the current
							// value (e.g. 10)
							prependValue = romanValues[i] - romanValues[i - 2];
							romanCombo = "" + romanChars[i - 2] + romanChars[i];
						}
					}

					if (value >= prependValue && prependValue > 0) {

						value -= prependValue;
						builder.append(romanCombo);
						changed = true;
					}

				}
			} // end while changed

		} // end for romanValues

		return builder.toString();
	}

	public Form[] getSupportedForms() {
		return new Form[] { Form.SHORT };
	}
}
