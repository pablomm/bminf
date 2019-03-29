package es.uam.eps.bmi.search.ranking.impl;

import java.io.IOException;

import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.ranking.SearchRankingDoc;

/**
 *
 * @author pablo
 */
public class RankingDocImpl extends SearchRankingDoc {
    DocumentMap docMap;
    int docID;
    double score;
    
    RankingDocImpl (DocumentMap m, int id, double s) {
        docMap = m;
        docID = id;
        score = s;
    }
    public double getScore() {
        return score;
    }

    public int getDocID() {
        return docID;
    }

    public String getPath() throws IOException {
        return docMap.getDocPath(docID);
    }
}
