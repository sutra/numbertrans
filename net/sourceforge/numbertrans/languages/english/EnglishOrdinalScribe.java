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

import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.scribe.OrdinalScribe;
import net.sourceforge.numbertrans.framework.scribe.CardinalScribe.Form;

public class EnglishOrdinalScribe extends OrdinalScribe {

	public EnglishOrdinalScribe() {
		super();
	}

	public static final Form[] SUPPORTED_FORMS = { Form.SHORT };

	protected static final EnglishCardinalScribe shortCardinalScribe = new EnglishCardinalScribe();

	public String getOrdinalString(WholeNumber number, Form form) {
		if (form == Form.SHORT) {
			String base = shortCardinalScribe.getCardinalString(number, form);
			String suffix;

			// handle the special cases of 11th, 12th, and 13th
			if (base.length() > 1 && base.charAt(base.length() - 2) == '1') {
				suffix = "th";
			} else {

				char lastChar = base.charAt(base.length() - 1);
				switch (lastChar) {
				case '1':
					suffix = "st";
					break;
				case '2':
					suffix = "nd";
					break;
				case '3':
					suffix = "rd";
					break;
				default:
					suffix = "th";
					break;
				}
			}

			return base + suffix;
		} else {
			throw new RuntimeException("Unimplemented");
		}
	}

	public Form[] getSupportedForms() {
		return SUPPORTED_FORMS;
	}
}
