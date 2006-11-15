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
package info.jonclark.corpus;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import info.jonclark.util.*;

/**
 * @author Jonathan
 */
public class GutenbergCatalog implements Serializable {
    public static transient final String CATALOG_FEED_RDF_ZIP = "http://www.gutenberg.org/feeds/catalog.rdf.zip";
    public static transient final String FILES_ROOT = "http://www.gutenberg.org/files/";
    
    private transient final Logger log;
    private transient final Properties props;
    
    private final TreeSet<GutenbergDocument> set = new TreeSet<GutenbergDocument>();
    
    private static transient final int STATUS_EVERY_X_ETEXTS = 100;
    
    private final boolean requireCreator = true;
    private final boolean requireTitle = true;
    private final boolean requireLanguage = true;
    private final boolean keepDates = false; // keep the dates on authors' names?
    private final boolean useDatabase = false; // use a database or memory to store the catalog?
    
    private static transient final String strEtextOpen = "<pgterms:etext rdf:ID=\"";
    private static transient final String strEtextClose = "</pgterms:etext>";
    private static transient final String strCreatorOpen = "<dc:creator";
    private static transient final String strCreatorClose = "</dc:creator>";
    private static transient final String strNestedOpen = "<rdf:li";
    private static transient final String strNestedClose = "</rdf:li>";
    private static transient final String strTitleOpen = "<dc:title";
    private static transient final String strTitleClose = "</dc:title>";
    private static transient final String strFriendlyTitleOpen = "<pgterms:friendlytitle";
    private static transient final String strFriendlyTitleClose = "</pgterms:friendlytitle>";
    private static transient final String strLanguageOpen = "<dc:language><dcterms:ISO639-2><rdf:value";
    private static transient final String strLanguageClose = "</rdf:value></dcterms:ISO639-2></dc:language>";
    
    public GutenbergCatalog(Properties props, Logger gutenbergLog) {
        log = gutenbergLog;
        this.props = props;
    }
    
    public void updateCatalogFromWeb() throws IOException, GutenbergCatalogException, PropertiesException {
        // this is an excellent canidate a counter, if we know how many etexts we expect
        // do not use the XML parser b/c it will be too slow 
        
        // TODO: Check for required properties at program startup.
        // Move all checking there
        
	    InputStream stream = WebUtils.getUrlStream(CATALOG_FEED_RDF_ZIP);
	    BufferedZipInputStream in = new BufferedZipInputStream(stream);
	    in.nextEntry(); // only read first entry
	    String line = null;
	    
        boolean inEtext = false;

        // attributes of this text -- these will be their own class file
        GutenbergDocument doc = null;
        int nTexts = 0; // number of etext entires read so far
        int nIgnored = 0;
        
        // NOTE: Tags can span multiple lines
        // Each tag may read in the next line of its own accord
	    while( (line = in.readLine()) != null) {
	        
	        //System.out.println(line);
	        
	        int nIndex = -1;
	        String strResult = null;
	        if((nIndex = line.indexOf(strEtextOpen)) > -1) {
	            final int nBegin = nIndex + strEtextOpen.length();
	            final int nEnd = line.indexOf("\"", nBegin);
	            // TODO: Do we really need to pass in a new unique word counter here?
	            doc = new GutenbergDocument(log, props, new UniqueWordCounter(false));
	            doc.setId(line.substring(nBegin, nEnd));
	            inEtext = true;
	            //System.err.println(textId);
	        }
	        if(inEtext) {
	            if((nIndex = line.indexOf(strEtextClose)) > -1) {
	                final String id = doc.getId();
	                final String author = doc.getAuthor();
	                final String title = doc.getTitle();
	                final String language = doc.getLanguage();
	                
	                // do some error checking
	                if(id.equals("")) {
	                    log.finer("Ignoring an eText due to blank id");
	                    nIgnored++;
	                } else if(requireCreator && author.equals("")) {
	                    log.finer("Ignoring an eText " + id + " due to blank creator");
	                    nIgnored++;
	                } else if(requireTitle && title.equals("")) {
	                    log.finer("Ignoring an eText " + id + " due to blank title");
	                    nIgnored++;
	                } else if(requireLanguage && language.equals("")) {
		                   log.finer("Ignoring an eText " + id + " due to blank language");
		                   nIgnored++;
	                } else {
		                addEntryToCatalog(doc);
		                inEtext = false;
		                nTexts++;
		                if(nTexts % this.STATUS_EVERY_X_ETEXTS == 0)
		                    log.info(nTexts + " read so far (" + nIgnored + " ignored due to errors)");
	                }
	            } else if( (strResult = extractTag(in, line, strCreatorOpen, strCreatorClose)) != null) {
	                doc.setAuthor(strResult);
	            } else if( (strResult = extractTag(in, line, strTitleOpen, strTitleClose)) != null) {
	                doc.setTitle(strResult);
	            } else if( (strResult = extractTag(in, line, strFriendlyTitleOpen, strFriendlyTitleClose)) != null) {
	                doc.setFriendlyTitle(strResult);
	            } else if( (strResult = extractTag(in, line, strLanguageOpen, strLanguageClose)) != null) {
	                doc.setLanguage(strResult);
	            }
	            
                // THIS OPERATION REALLY NEEDS TO RESUME!
                // pass boolean to this function
                
                // catch exceptions and save what we have up to that point
                // resume maybe 1 author previous...
	        }
	    }
    }


    /**
     * Add a document to the catalog while performing an update
     * 
     * @param doc
     */
    private void addEntryToCatalog(GutenbergDocument doc) {
        // write attributes of this etext to a database here
        /*
        System.out.println("Etext: " + doc.getId());
        System.out.println("Author: " + doc.getAuthor());
        System.out.println("Title: " + doc.getTitle());
        System.out.println("Language: " + doc.getLanguage());
        */
        set.add(doc);
    }
    
    /**
     * This should never be called by the outside world
     * because we want to make use of caching. The
     * cache should be implemented as a separate class
     * 
     * @return
     */
    private static String getWebPathToDocument(GutenbergDocument doc) {
        final int ETEXT_LENGTH = 5; // the length of the string "etext"
        return FILES_ROOT + doc.getId().substring(ETEXT_LENGTH);
    }

    /**
     * Check if a tag starts on the current line. If it does, read the tag to its
     * completion and return its contents. 
     * 
     * @param in
     * @param line
     * @param doc
     * 
     * @return The inner contents of the tag. null if no tag begins on this line
     * 
     * @throws IOException
     * @throws GutenbergCatalogException
     */
    private String extractTag(final BufferedZipInputStream in, String line,
    		final String strOpenTag, final String strCloseTag)
    	throws IOException, GutenbergCatalogException
    {
        final int nIndex;
        if((nIndex = line.indexOf(strOpenTag)) > -1) {
            StringBuffer buf = new StringBuffer();
            final int nBegin = line.indexOf('>', nIndex + strOpenTag.length()) + 1;
            String strReturn;
           
            int nEnd = -1;
            if((nEnd = line.indexOf(strCloseTag, nBegin)) >= 0) {
                // the closing tag is on the same line as the opening tag
                strReturn = line.substring(nBegin, nEnd);
            } else {
                // this tag spans multiple lines
                // append the first line, starting after the tag
                buf.append(line.substring(nBegin));
                while( (line = in.readLine()) != null &&
                       (nEnd = line.indexOf(strCloseTag)) < 0) {
                    buf.append(" " + line);
                }
	            if(line == null) {
	                throw new GutenbergCatalogException("End of stream while reading inside tag."
	                        + "Current buffer: " + buf.toString());
	            }
                buf.append(" " + line.substring(0,nEnd));
                strReturn = buf.toString();
            }
            
            // check for nested tags in return string
            String strResult = null;
            if( (strResult = extractTag(in, strReturn, strNestedOpen, strNestedClose)) != null) {
                log.finer("Including only first author of multiple author work");
                strReturn = strResult;
            }
            
            return strReturn;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        Logger log = Logger.getLogger("gutenberg");
        log.setLevel(Level.INFO);
        GutenbergCatalog cat = new GutenbergCatalog(null, log);
        cat.updateCatalogFromWeb();
    }
}
