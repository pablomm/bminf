package es.uam.eps.bmi.search.vsm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.lucene.search.ScoreDoc;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingDocImpl;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class DocBasedVSMEngine extends AbstractVSMEngine {

	public DocBasedVSMEngine(Index index) {
		super(index);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Separamos por blancos la query
		String[] terms = query.split(" ");
		
		PriorityQueue<ScoreDoc> postHeap = new PriorityQueue<ScoreDoc>(terms.length);
		
		int numDocs = index.numDocs();
		// Ranking de busqueda
		RankingImpl ranking = new RankingImpl(index, cutoff);

		// Hashmap con los tfids (docId, tfidf (parcial))
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();
		// Iteramos sobre los documentos (los obtenemos a partir del ID)
		/*for (String term : terms) {
			// Obtenemos los postings de la palabra
			PostingsList postings = index.getPostings(term);
			// heap.addAll(postings);
			// LLenamos el heap con los postings
			for (Posting posting : postings)
				heap.add(posting);
		}
		
		for (Posting posting : heap) {
			int docId = posting.getDocID();
			// score parcial
			double score = scores.get(docId);
			
		}*/
		
		for (int id=0; id < numDocs; id++) {
			for (String term : terms) {
				// Obtenemos los postings de la palabra
				PostingsList postings = index.getPostings(term);
				
				// Obtenemos el numero de documentos donde aparece
				int docFreq = postings.size();
				
				for (Posting post : postings) {
					// Actualizamos el heap
					if (post.getDocID()==id) {
						
						// TF-IDF de la palabra en el documento
						double tfidf = tfidf(post.getFreq(), docFreq, numDocs);
						
						// Añadimos nuevo nodo al heap
						postHeap.add(new ScoreDoc(id, (float) tfidf));
						
						// Si llenamos el heap hacemos heapify
						if (postHeap.size()==numDocs) {
							
							ScoreDoc first = postHeap.element();
							
							// Obtenemos el score parcial
							double scoreParcial = scores.getOrDefault(first.doc, 0.0);
							// Actualizamos el valor
							scores.put(first.doc, scoreParcial + first.score);
							
							// Extraemos el min que hemos accedido
							postHeap.remove();
						}
						break;
					}
					// Si los post están ordenados por ID, en cuanto nos pasemos rompemos el bucle
					else if (post.getDocID()>id) break;
				}
			}
		}
		
		// Vaciamos el resto del heap
		while (!postHeap.isEmpty()) {
			ScoreDoc first = postHeap.element();
			
			// Obtenemos el score parcial
			double scoreParcial = scores.getOrDefault(first.doc, 0.0);
			// Actualizamos el valor
			scores.put(first.doc, scoreParcial + first.score);
			
			// Extraemos el min que hemos accedido
			postHeap.remove();
		}

		// Incluimos en el ranking las busquedas
		for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
			ranking.add(entry.getKey(), entry.getValue());
		}

		return ranking;
	}

}
