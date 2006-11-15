/*
 * Created on Aug 2, 2006
 */
package info.jonclark.testcases;

import info.jonclark.sourcecode.UnicodeEscaper;
import junit.framework.TestCase;

public class UnicodeEscaperTest extends TestCase {
    
    private final UnicodeEscaper ue = new UnicodeEscaper();

    public void testEscapeLine() {
        System.out.println(ue.processLine("/* 零 */ 零 /* 零 */ // 零"));
    }

}
