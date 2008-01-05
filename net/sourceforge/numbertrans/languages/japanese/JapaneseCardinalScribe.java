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
package net.sourceforge.numbertrans.languages.japanese;

import net.sourceforge.numbertrans.framework.base.MyriadNumeralSet;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.base.AbstractNumber.Context;
import net.sourceforge.numbertrans.framework.scribe.MyriadCardinalScribe;

public class JapaneseCardinalScribe extends MyriadCardinalScribe {

	public static final MyriadNumeralSet numerals =
			new MyriadNumeralSet(new String[] { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" },
					new String[] { "十", "百", "千", "万" });

	public JapaneseCardinalScribe() {
		super(numerals);
	}

	public static void main(String[] args) throws Exception {
		JapaneseCardinalScribe scribe = new JapaneseCardinalScribe();
		System.out.println(scribe.getCardinalString(new WholeNumber(1111, 0, Context.CARDINAL),
				Form.SHORT));
	}

}
