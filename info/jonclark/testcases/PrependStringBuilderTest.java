/*
 * Created on Nov 24, 2006
 */
package info.jonclark.testcases;

import info.jonclark.util.PrependStringBuilder;
import junit.framework.TestCase;

public class PrependStringBuilderTest extends TestCase {

    public void testPrepend() {
	PrependStringBuilder builder = new PrependStringBuilder();
	builder.prepend("ghi");
	builder.prepend("def");
	builder.prepend("abc");
	assertEquals("abcdefghi",builder.toString());
	
	builder = new PrependStringBuilder();
	builder.prepend("c");
	builder.prepend("b");
	builder.prepend("a");
	assertEquals("abc", builder.toString());
    }

}
