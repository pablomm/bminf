package es.uam.eps.bmi.search.index.structure.lucene;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsListIterator;
import java.io.IOException;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.DocIdSetIterator;

/**
 *
 * @author pablo
 */
public class LucenePostingsIterator implements PostingsListIterator {
    PostingsEnum postings;
    int currentDoc;
    
    public LucenePostingsIterator(PostingsEnum p) throws IOException {
        postings = p;
        currentDoc = postings.nextDoc();
    }
    
    public boolean hasNext() {
        return currentDoc != DocIdSetIterator.NO_MORE_DOCS;
    }
    
    public Posting next() {
        try {
            Posting p = new Posting(postings.docID(),postings.freq());
            currentDoc = postings.nextDoc();
            return p;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
