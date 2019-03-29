package es.uam.eps.bmi.search.index.structure.lucene;

import java.io.IOException;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.search.DocIdSetIterator;

import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsListIterator;

/**
 *
 * @author pablo
 */
public class LucenePostingsIterator implements PostingsListIterator {
    protected PostingsEnum postings;
    protected int currentDoc;
    
    public LucenePostingsIterator(PostingsEnum p) throws IOException {
        postings = p;
        if (p == null) currentDoc = DocIdSetIterator.NO_MORE_DOCS;
        else currentDoc = postings.nextDoc();
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
