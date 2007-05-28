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
package info.jonclark.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ReflectUtils {

    public static StackTraceElement getCurrentStackTraceElement() {
	return Thread.currentThread().getStackTrace()[3];
    }

    public static String getCurrentClassName() {
	return Thread.currentThread().getStackTrace()[3].getClassName();
    }

    public static String getCurrentMethodName() {
	return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    /**
         * @return null if there is no caller; otherwise the stack trace element
         *         of the caller's calller
         */
    public static StackTraceElement getCallingStackTraceElement() {
	final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	if (stack.length >= 5) {
	    return stack[4];
	} else {
	    return null;
	}
    }

    public static String getCallingClassName() {
	final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	if (stack.length >= 5) {
	    return stack[4].getClassName();
	} else {
	    return "Top of call stack: No caller";
	}
    }

    public static String getCallingMethodName() {
	final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
	if (stack.length >= 5) {
	    return stack[4].getMethodName();
	} else {
	    return "Top of call stack: No caller";
	}
    }

    /**
     * Get the type name for a generic argument
     * 
         * @param fieldOfGenericType
         *                A field which is of the type of the generic type in
         *                question. This can be obtained via a call to
         *                getClass().getDeclaredField("x").
         * @param nTypeArgument
         *                The zero-based index of the type argument (if multiple
         *                type arguments are possible)
         */
    public static String getGenericTypeName(Field fieldOfGenericType, int nTypeArgument) {
	// the following code is based on http://www-128.ibm.com/developerworks/java/library/j-cwt11085.html
	
	Type gtype = fieldOfGenericType.getGenericType();
	if (gtype instanceof ParameterizedType) {

	    // list the raw type information
	    ParameterizedType ptype = (ParameterizedType) gtype;
//	    Type rtype = ptype.getRawType();
//	    System.out.println("rawType is instance of " + rtype.getClass().getName());
//	    System.out.println(" (" + rtype + ")");

	    // list the actual type arguments
	    Type[] targs = ptype.getActualTypeArguments();
//	    System.out.println("actual type arguments are:");
	    if(nTypeArgument >= targs.length)
		throw new RuntimeException("class does not have a " + nTypeArgument + "th generic type");
	    
	    
	    String name = targs[nTypeArgument].toString();
	    name = StringUtils.substringAfter(name, "class ");
	    return name;
	} else {
	    throw new RuntimeException("getGenericType is not a ParameterizedType");
	}
    }

    public static void main(String... args) throws Exception {
	System.out.println(getCurrentMethodName());
	System.out.println(getCallingMethodName());
	System.out.println(getCurrentClassName());
	System.out.println(getCallingClassName());

	ReflectUtils ru = new ReflectUtils();

	Field field = ru.getClass().getDeclaredField("x");
	String genericType = getGenericTypeName(field, 0);
	System.out.println(genericType);
    }

    private final ArrayList<Field> x = new ArrayList<Field>();
}
