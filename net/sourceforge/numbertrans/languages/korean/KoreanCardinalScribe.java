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

import net.sourceforge.numbertrans.framework.base.MyriadNumeralSet;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.framework.base.GeneralNumber.Context;
import net.sourceforge.numbertrans.framework.scribe.MyriadCardinalScribe;

/**
 * NOTE: The Native Korean Numerals has no character for zero.
 * 
 * @author Jonathan
 */
public class KoreanCardinalScribe extends MyriadCardinalScribe {
    
    // TODO: Re-engineer MyriadNumeral Set to not use arrays

    public static final MyriadNumeralSet NATIVE_KOREAN_NUMERALS = new MyriadNumeralSet(
	    new String[] {null, "하나", "둘", "셋", "넷", "다섯", "여섯", "일곱", "여덟", "아홉" },
	    new String[] {"열", "온", "즈믄","드먼"}
	    );
    
    public static final MyriadNumeralSet SINO_HANGUL_KOREAN_NUMERALS = new MyriadNumeralSet(
	    new String[] {"영", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구" },
	    new String[] {"십", "백", "천","만"}
	    );
    
    // sinoHangul  = 10^8 = 억

    // TODO: Add north korean alternatives
    
    public KoreanCardinalScribe(Form form) {
	super(form, SINO_HANGUL_KOREAN_NUMERALS);
	
	// need to allow special cases for korean native 20, 30, etc.
    }
    
    /**
     * 
     * @param form
     * @param koreanNumeralSet Either NATIVE_KOREAN_NUMERALS or SINO_HANGUL_KOREAN_NUMERALS
     */
    public KoreanCardinalScribe(Form form, MyriadNumeralSet koreanNumeralSet) {
	super(form, koreanNumeralSet);
	
	// need to allow special cases for korean native 20, 30, etc.
    }
}
