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

import java.util.zip.*;
import java.io.*;

/**
 * A class to conveniently read text from ZIP files
 */
public class BufferedZipInputStream extends ZipInputStream {
    private ZipEntry currentEntry = null;
    
    /**
     * Construct a new BufferedZipInputStream from an InputStream object
     * 
     * @param stream The stream from which input will be read
     */
    public BufferedZipInputStream(InputStream stream) {
        super(stream);
    }
    
    /**
     * Move to the next entry in this ZIP file, if there is one.
     * Determines: Have we reached the end of the zip file?
     * 
     * @return True if this <code>BufferedZipInputStream</input>
     * 			has another entry
     */
    public boolean nextEntry() throws IOException {
        return super.getNextEntry() != null;
    }
    
    /**
     * Read a line from the current ZIP entry. nextEntry() must
     * be called before this method.
     * 
     * @return The next line. null if this is the end of the ZIP entry.
     * 
     * @throws IOException
     */
    public String readLine() throws IOException {
        if(currentEntry == null)
            throw new IOException("No current ZIP entry." + 
                    "Ensure that nextEntry() has been called and that it returned true.");
        
        int raw = super.read();
        if(raw != -1) {
	        StringBuffer buf = new StringBuffer();
	        do {
	            if(raw == '\r')
	                continue;
	            else if(raw == '\n')
	                break;
	            else
	                buf.append((char)raw);
	        } while((raw = super.read()) != -1);
	        
	        return buf.toString();
        } else {
            return null;
        }
    }
}
