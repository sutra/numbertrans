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
package info.jonclark.database;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.HashSet;

/**
 * @author Jonathan
 */
public class DbUtils {
    
    /**
     * Outputs all elements of the ResultSet to the given
     * output stream in CSV (Comma Separated Value) format
     * 
     * @param in A ResultSet returned from a SQL query
     * @param out An OutputStream such as System.out or a FileOutputStream
     * @param delim The delimiter to use. The obvious answer is a comma.
     */
    public static void outputToCsv(ResultSet in, OutputStream stream, String delim) throws SQLException {
        PrintWriter out = new PrintWriter(stream);
        ResultSetMetaData md = in.getMetaData();
	    final int nCols = md.getColumnCount();
	    StringBuffer buf = new StringBuffer();
	    
	    // print out column headers
	    // SQL column numbers are 1-based!
	    for(int i=1; i<=nCols; i++)
	        buf.append(md.getColumnName(i) + delim);
	    out.println(buf.substring(0, buf.length()-1)); // trim off trailing "," 
	        
		while (in.next()) {
		    buf = new StringBuffer();
		    for(int i=1; i<=nCols; i++)
		        buf.append(in.getString(i) + delim);
		    out.println(buf.substring(0, buf.length()-1)); // trim off trailing "," 
		} // end reading the table
    }
    
    /**
     * That's right. We're about to load a whole Java class straight from a
     * database using nothing but reflection. How cool is that?
     * @throws SQLException
     * 
     *
     */
    /*
     * This doesn't seem nearly as cool as it did when I started coding it
     
    public static Object deserializeClassFromDatabase(ResultSet in, Class c)
    	throws SQLException
    {
        Field[] fields = c.getFields();
        HashSet<String> fieldNames = new HashSet<String>();
        for(Field f : fields)
            fieldNames.add(f.getName());
        
        ResultSetMetaData md = in.getMetaData();
        md.get
        
        
        
        
        // 1. find out what type of class we're about to instantiate
        // 2. get its fields
        // 3. see which of those fields are in our database
        // 4. for all fields that are 
    }
    */
}
