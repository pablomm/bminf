package es.uam.eps.bmi.search.index;

import java.io.IOException;

import es.uam.eps.bmi.search.index.freq.FreqVector;

/**
 *
 * @author pablo
 */
public interface ForwardIndex extends Index {
    public FreqVector getDocVector(int docID) throws IOException;
    public long getTermFreq(String term, int docID) throws IOException;
}
