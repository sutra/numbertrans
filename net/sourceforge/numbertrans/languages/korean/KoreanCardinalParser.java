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
package net.sourceforge.numbertrans.languages.korean;

import net.sourceforge.numbertrans.framework.parser.MyriadCardinalParser;
import net.sourceforge.numbertrans.languages.chinese.ChineseCardinalParser;

/**
 * A parser for Korean numbers.
 */
public class KoreanCardinalParser extends ChineseCardinalParser {

    private boolean useNorthKorean;

    public KoreanCardinalParser() {
	this(true, true);
    }

    public KoreanCardinalParser(boolean useNorthKorean, boolean useHanja) {
	super(useHanja, useHanja);

	this.useNorthKorean = useNorthKorean;

	addSinoKoreanCharacters();
    }

    private void addSinoKoreanCharacters() {
	values.put('영', 0L);
	if (useNorthKorean) {
	    values.put('령', 0L);
	    values.put('공', 0L);
	}

	values.put('일', 1L);
	values.put('이', 2L);
	values.put('삼', 3L);
	values.put('사', 4L);
	values.put('오', 5L);
	values.put('육', 6L);
	if (useNorthKorean) {
	    values.put('륙', 6L);
	}
	values.put('칠', 7L);
	values.put('팔', 8L);
	values.put('구', 9L);
	values.put('십', 10L);

	values.put('백', 100L);
	values.put('천', 1000L);

	values.put('만', 10000L); // 10E4

	// very large numbers
	values.put('억', 100000000L); // 10E8
	values.put('조', 1000000000000L); // 10E12
	values.put('경', 10000000000000000L); // 10E16

	// the below values overflow a long
	// values.put('해', 100000000L); // 10E20
	// values.put('자', 100000000L); // 10E24
	// values.put('양', 100000000L); // 10E28
	// values.put('구', 100000000L); // 10E32
	// values.put('간', 100000000L); // 10E36
	// values.put('정', 100000000L); // 10E40
	// values.put('재', 100000000L); // 10E44
	// values.put('극', 100000000L); // 10E48
    }
    
    public void addNativeKoreanCharacters() {
	
	// TODO: Resolve ambiguities between 2 systems
    
	values.put('하', 1L);
	values.put('나', MyriadCardinalParser.CHARACTER_VALUE_SKIP); // 1
	values.put('둘', 2L);
	values.put('셋', 3L);
	values.put('넷', 4L);
	values.put('다', 5L);
	values.put('여', 6L);
	values.put('섯', MyriadCardinalParser.CHARACTER_VALUE_SKIP); // 5&6
	values.put('일', 7L);
	values.put('곱', MyriadCardinalParser.CHARACTER_VALUE_SKIP); // 7
	values.put('여', 8L);
	values.put('덟', MyriadCardinalParser.CHARACTER_VALUE_SKIP); // 8
	values.put('아', 9L);
	values.put('홉', MyriadCardinalParser.CHARACTER_VALUE_SKIP); // 9
	values.put('열', 10L);
	
	values.put('스', 20L);
	values.put('서', 30L);
	values.put('마', 40L);
	values.put('쉰', 50L);
	values.put('예', 60L);
	values.put('일', 70L);
	values.put('여', 80L);
	values.put('아', 90L);
	

	values.put('x', 100L);
	values.put('x', 1000L);

	values.put('x', 10000L); // 10E4
    }
}
