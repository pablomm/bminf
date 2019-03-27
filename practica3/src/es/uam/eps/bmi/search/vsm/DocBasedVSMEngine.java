package es.uam.eps.bmi.search.vsm;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;
import java.io.IOException;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author pablo
 */
public class DocBasedVSMEngine extends AbstractVSMEngine {

    public DocBasedVSMEngine(Index index) {
        super(index);
    }
    
    public SearchRanking search(String q, int cutoff) throws IOException {
        String query[] = parse(q);
        RankingImpl ranking = new RankingImpl(index, cutoff);
        Iterator<Posting> postingsLists[] = new Iterator[query.length];
        long docFreqs[] = new long[query.length];
        PriorityQueue<QueryPosting> cosineHeap = new PriorityQueue<QueryPosting>();
        
        for (int i = 0; i < query.length; i++) {
            docFreqs[i] = index.getDocFreq(query[i]);
            if (docFreqs[i] > 0) {
                postingsLists[i] = index.getPostings(query[i]).iterator();
                cosineHeap.add(new QueryPosting(postingsLists[i].next(), i));
            }
        }
        int numDocs = index.numDocs();
        
        int currentDocID = cosineHeap.peek().posting.getDocID();
        double score = 0;
        while (!cosineHeap.isEmpty()) {
            QueryPosting qp = cosineHeap.poll();
            if (qp.posting.getDocID() != currentDocID) {
                ranking.add(currentDocID, score / index.getDocNorm(currentDocID));
                score = 0;
                currentDocID = qp.posting.getDocID();
            }
            score += tfidf(qp.posting.getFreq(), docFreqs[qp.qi], numDocs);
            if (postingsLists[qp.qi].hasNext()) {
                qp.posting = postingsLists[qp.qi].next();
                cosineHeap.add(qp);
            }
        }
        ranking.add(currentDocID, score / index.getDocNorm(currentDocID));
        
        return ranking;
    }

    class QueryPosting implements Comparable<QueryPosting> {
        Posting posting;
        int qi;

        QueryPosting(Posting p, int i) {
            posting = p;
            qi = i;
        }

        public int compareTo(QueryPosting qp) {
            return posting.getDocID() - qp.posting.getDocID();
        }
    }
}
