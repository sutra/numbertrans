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
package net.sourceforge.numbertrans.languages.chinese;

import net.sourceforge.numbertrans.framework.parser.MyriadCardinalParser;

/**
 * A parser for Chinse numbers
 */
public class ChineseCardinalParser extends MyriadCardinalParser {

	private final boolean useTraditional;
	private final boolean useSimplified;

	public ChineseCardinalParser() {
		this(true, true);
	}

	public ChineseCardinalParser(boolean useTraditional, boolean useSimplified) {
		this.useTraditional = useTraditional;
		this.useSimplified = useSimplified;

		addFinancialCharacters();
		addStandardCharacters();
		addObsoleteCharacters();
		addPhoneNumberCharacters();
		addVerticalCharacters();
	}

	private void addFinancialCharacters() {
		values.put('零', 0L);
		values.put('壹', 1L);
		if (useTraditional)
			values.put('貳', 2L);
		if (useSimplified)
			values.put('贰', 2L);
		if (useTraditional)
			values.put('叄', 3L);
		if (useSimplified)
			values.put('叁', 3L);

		values.put('肆', 4L);
		values.put('伍', 5L);
		values.put('陸', 6L);
		values.put('柒', 7L);
		values.put('捌', 8L);
		values.put('玖', 9L);
		values.put('拾', 10L);
		values.put('什', 10L);

		values.put('念', 20L);
		values.put('佰', 100L);
		values.put('仟', 1000L);
		values.put('萬', 10000L);
		values.put('億', 100000000L);
		values.put('兆', 1000000000000L);
		values.put('京', 10000000000000000L);
	}

	private void addStandardCharacters() {
		values.put('〇', 0L);
		values.put('一', 1L);
		values.put('二', 2L);
		values.put('三', 3L);
		values.put('四', 4L);
		values.put('五', 5L);
		values.put('六', 6L);
		values.put('七', 7L);
		values.put('八', 8L);
		values.put('九', 9L);
		values.put('十', 10L);

		values.put('廿', 20L);
		values.put('卄', 20L);
		values.put('卅', 30L);
		values.put('卌', 40L);

		values.put('百', 100L);
		values.put('千', 1000L);

		if (useTraditional)
			values.put('萬', 10000L);
		if (useSimplified)
			values.put('万', 10000L);
		if (useTraditional)
			values.put('億', 100000000L);
		if (useSimplified)
			values.put('亿', 100000000L);

		// very large numbers
		values.put('兆', 1000000000000L);
		values.put('京', 10000000000000000L);

		// also
		values.put('兩', 2L);
		values.put('參', 3L);

		// before measure words
		values.put('两', 2L);
		values.put('参', 3L);
	}

	private void addObsoleteCharacters() {
		values.put('弌', 1L);
		values.put('弍', 2L);
		values.put('弎', 2L);
	}

	private void addVerticalCharacters() {
		values.put('〡', 1L);
		values.put('〢', 2L);
		values.put('〣', 2L);
	}

	/**
	 * These characters, meaning "the smallest" are used in phone numbers, room
	 * numbers, etc. to prevent confusion between similar sounding words.
	 */
	private void addPhoneNumberCharacters() {
		if (useTraditional) {
			values.put('么', 1L);
		}
		if (useSimplified) {
			values.put('幺', 1L);
		}
	}
}
