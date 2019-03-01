package es.uam.eps.bmi.search.vsm;

import java.io.IOException;

import java.util.Comparator;

import java.util.Iterator;

import java.util.PriorityQueue;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class DocBasedVSMEngine extends AbstractVSMEngine {

	public DocBasedVSMEngine(Index index) {
		super(index);

	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Separamos por blancos la query
		String[] terms = this.parse(query);

		// Extraemos todas las listas de postings y las introducimos en un HEAP
		PriorityQueue<PostingsEHeap> postings = new PriorityQueue<PostingsEHeap>(terms.length);

		double tfidf = 0.0;
		int currentDocId = 0;
		

		int numDocs = index.numDocs();

		for (String term : terms) {
			PostingsList posting = index.getPostings(term);
			PostingsEHeap element = new PostingsEHeap(posting);
			postings.add(element);
		}

		RankingImpl ranking = new RankingImpl(index, cutoff);

		while (!postings.isEmpty()) {

			// Extraemos el elemento del Heap
			PostingsEHeap eHeap = postings.remove();

			// Extraemos el siguiente posting de la lista
			Posting post = eHeap.getNext();

			// Si es el mismo id
			if (post.getDocID() == currentDocId) {
				tfidf += tfidf(post.getFreq(), eHeap.size(), numDocs);

			} else {

				// Incluimos en el ranking el scorem si no es 0,
				// Para descartar el caso inicial
				if (tfidf != 0) {
					
					ranking.add(currentDocId, tfidf/index.getDocNorm(currentDocId));
				}

				currentDocId = post.getDocID();
				tfidf = tfidf(post.getFreq(), eHeap.size(), numDocs);

			}

			// Solo lo incluimos si le quedan postings
			if (eHeap.hasNext()) {
				postings.add(eHeap);
			}

		}

		// Ultimo resultado
		ranking.add(currentDocId, tfidf/index.getDocNorm(currentDocId));

		return ranking;

	}

}

/**
 * Estructura interna para almacenar las listas en un HEAP
 */
class PostingsEHeap implements Comparable<PostingsEHeap> {

	/**
	 * 
	 */
	private int docID;
	
	/**
	 * 
	 */
	private Iterator<Posting> iterator;
	
	/**
	 * 
	 */
	private Posting next;
	
	/**
	 * 
	 */
	private boolean hasNextFlag;
	
	/**
	 * 
	 */
	private int size;

	/**
	 * @param postings
	 */
	public PostingsEHeap(PostingsList postings) {
		this.iterator = postings.iterator();
		this.updateNext();
		this.size = postings.size();

	}

	/**
	 * @return
	 */
	public int size() {
		return this.size;
	}

	/**
	 * 
	 */
	private void updateNext() {
		if(this.iterator.hasNext()) {
			this.next = this.iterator.next();
			this.hasNextFlag = true;
			this.docID = this.next.getDocID();
		} else {
			this.next = null;
			this.hasNextFlag = false;
			this.docID = Integer.MAX_VALUE;
		}

	}

	/**
	 * Obtiene el siguiente posting de la iteracion y actualiza
	 * los indices internos
	 * @return Siguiente posting
	 */
	public Posting getNext() {
		Posting next_aux = this.next;
		this.updateNext();
		return next_aux;
	}

	
	public int getDocID() {
		return this.docID;
		
	}
	/**
	 * @return
	 */
	public boolean hasNext() {
		return this.hasNextFlag;
	}


	@Override
	public int compareTo(PostingsEHeap o) {

		return Integer.compare(this.docID, o.getDocID());
	}

}
