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
package net.sourceforge.numbertrans.framework.scribe;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.numbertrans.framework.base.AbstractNumber;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.scribe.CardinalScribe.Form;

/**
 * An interface for classes that support the generation of ordinal numbers in
 * some target language.
 */
public abstract class OrdinalScribe implements NumberScribe {

	public String getNumberString(AbstractNumber number, Form form) {
		assert number instanceof WholeNumber;
		return getOrdinalString((WholeNumber) number, form);
	}
	
	public List<String> getAllNumberStrings(AbstractNumber number) {
		assert number instanceof WholeNumber;
		WholeNumber whole = (WholeNumber) number;
		
		ArrayList<String> list = new ArrayList<String>();
		for(final Form form : getSupportedForms()) {
			list.add(getOrdinalString(whole, form));
		}
		return list;
	}


	/**
	 * Get a string representation of an whole number in a target language in
	 * ordinal form
	 * 
	 * @param number
	 *            The whole number that should be converted to string form
	 * @return The string representation of this whole number in ordinal form in
	 *         the target langauge
	 */
	public abstract String getOrdinalString(WholeNumber number, Form form);
}
