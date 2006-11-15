/*
 * Created on Aug 2, 2006
 */
package info.jonclark.testcases;

import info.jonclark.util.ArrayUtils;
import junit.framework.TestCase;

public class ArrayUtilsTest extends TestCase {

    public void testArrayToString() {
        assertEquals(ArrayUtils.arrayToString(
                new String[] {"1", "2", "3"}),
                "{1, 2, 3}");
        assertEquals(ArrayUtils.arrayToString(
                new int[] {1, 2, 3}),
                "{1, 2, 3}");
        assertEquals(ArrayUtils.arrayToString(
                new boolean[] {true, true, false}),
                "{true, true, false}");
    }

    public void testArrayToVector() {
        
    }

    public void testSortedArrayContains() {
//        fail("Not yet implemented");
    }

    public void testUnsortedArrayContains() {
//        fail("Not yet implemented");
    }

}
