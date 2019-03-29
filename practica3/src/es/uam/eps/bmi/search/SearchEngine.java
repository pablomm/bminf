package es.uam.eps.bmi.search;

import java.io.IOException;

import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.ranking.SearchRanking;

/**
 *
 * @author pablo
 */
public interface SearchEngine {
    public SearchRanking search(String query, int cutoff) throws IOException;
    public DocumentMap getDocMap();
}
