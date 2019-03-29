package es.uam.eps.bmi.search.index.freq.lucene;

import java.io.IOException;

import org.apache.lucene.index.TermsEnum;

import es.uam.eps.bmi.search.index.freq.TermFreq;

/**
 *
 * @author pablo
 */
public class LuceneTermFreq implements TermFreq {
    TermsEnum terms;
    public LuceneTermFreq (TermsEnum t) {
        terms = t;
    }

    public String getTerm() throws IOException {
        return terms.term().utf8ToString();
    }

    public long getFreq() throws IOException {
        return terms.totalTermFreq();
    }
}
