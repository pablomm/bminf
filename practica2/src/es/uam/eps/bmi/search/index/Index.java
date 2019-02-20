package es.uam.eps.bmi.search.index;

import es.uam.eps.bmi.search.index.structure.PostingsList;
import java.io.IOException;
import java.util.Collection;

/**
 *
 * @author pablo
 */
public interface Index {
    public int numDocs();
    public PostingsList getPostings(String term) throws IOException;
    public Collection<String> getAllTerms() throws IOException;
    public long getTotalFreq(String term) throws IOException;
    public long getDocFreq(String term) throws IOException;
    public String getDocPath(int docID) throws IOException;
    public double getDocNorm(int docID) throws IOException;
}
