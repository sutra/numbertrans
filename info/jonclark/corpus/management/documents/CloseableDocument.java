package info.jonclark.corpus.management.documents;

import java.io.IOException;

public interface CloseableDocument {
    public void close() throws IOException;
    
    public boolean isClosed();
}
