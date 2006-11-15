/* Copyright (c) 2006, Marian Olteanu <marian_DOT_olteanu_AT_gmail_DOT_com>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
- Neither the name of the University of Texas at Dallas nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package info.jonclark.util;

import java.io.*;
import java.net.*;
import java.util.zip.*;

/**
 * Utilities for getting data from the web
 */
public class WebUtils {
    
    /**
     * Get an input stream for the data at a given URL
     * 
     * @param urlToRetrieve The URL from which we will get data
     * @return An input stream with the data at <code>urlToRetrieve</code>
     * @throws IOException
     */
	public static InputStream getUrlStream(String urlToRetrieve) throws IOException
	{
		URL url = new URL(urlToRetrieve);
		URLConnection conn = url.openConnection();
		InputStream in = conn.getInputStream();
		
		// Handle the file type correctly
		String encoding = conn.getContentEncoding();
		if(encoding != null) {
			if (encoding.equals("gzip"))
				in = new GZIPInputStream(in);
		}
		return in;
	}
	
	/**
	 * Main method, for demo purposes
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
	    
	    // now trying opening a zip file like the PG thing

	    InputStream stream = WebUtils.getUrlStream("http://www.gutenberg.org/feeds/catalog.rdf.zip");
	    BufferedReader in = new BufferedReader(new InputStreamReader(stream));
	    String line;
	    while( (line = in.readLine()) != null) {
	        System.out.println(line);
	    }
	    
	    
/*
	    InputStream stream = WebUtils.getUrlStream("http://www.gutenberg.org/feeds/catalog.rdf.zip");
	    BufferedZipInputStream in = new BufferedZipInputStream(stream);
	    in.nextEntry(); // only read first entry
	    String line;
	    while( (line = in.readLine()) != null) {
	        System.out.println(line);
	    }
	    */
	    
	}
}
