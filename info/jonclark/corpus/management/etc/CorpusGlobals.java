/*
 * Created on Jun 6, 2007
 */
package info.jonclark.corpus.management.etc;

import java.util.Properties;

public class CorpusGlobals {
    private int nGlobalFileCount = 0;
    
    public CorpusGlobals(Properties props) {
	
    }
    
    public void incrementGlobalFileCount() {
	nGlobalFileCount++;
    }
    
    public int getGlobalFileCount() {
	return nGlobalFileCount;
    }
}
