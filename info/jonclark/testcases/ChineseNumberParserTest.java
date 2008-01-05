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
package info.jonclark.testcases;

import net.sourceforge.numbertrans.framework.base.AbstractNumber;
import net.sourceforge.numbertrans.framework.base.WholeNumber;
import net.sourceforge.numbertrans.languages.chinese.ChineseCardinalParser;
import junit.framework.TestCase;

public class ChineseNumberParserTest extends TestCase {

    final ChineseCardinalParser parser = new ChineseCardinalParser();

    public void testGetNumberFromString() {
	assertEquals(new WholeNumber(123, 0, AbstractNumber.Context.CARDINAL), parser
		.getNumberFromString("一百二十三"));
	assertEquals(new WholeNumber(927, 0, AbstractNumber.Context.CARDINAL), parser
		.getNumberFromString("九百二十七"));
	assertEquals(new WholeNumber(2006, 0, AbstractNumber.Context.CARDINAL), parser
		.getNumberFromString("二零零六"));
	assertEquals(new WholeNumber(6, 1, AbstractNumber.Context.CARDINAL), parser
		.getNumberFromString("零六"));
	
	System.out.println(Integer.parseInt("༡൫"));
	
	
	
//	values.put('一', 1L);
//	values.put('二', 2L);
//	values.put('三', 3L);
//	values.put('四', 4L);
//	values.put('五', 5L);
//	values.put('六', 6L);
//	values.put('七', 7L);
//	values.put('八', 8L);
//	values.put('九', 9L);
//	values.put('十', 10L);
    }

}
