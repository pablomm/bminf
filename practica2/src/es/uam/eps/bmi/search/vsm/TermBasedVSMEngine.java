package es.uam.eps.bmi.search.vsm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class TermBasedVSMEngine extends AbstractVSMEngine {

	public TermBasedVSMEngine(Index idx) {
		super(idx);

	}

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Separamos por blancos la query
		String[] terms = query.split(" ");

		int numDocs = index.numDocs();

		// Ranking de busqueda
		RankingImpl ranking = new RankingImpl(index, cutoff);

		// Hashmap con los tfids (docId, tfidf (parcial))
		HashMap<Integer, Double> scores = new HashMap<Integer, Double>();

		// Iteramos sobre las palabras de la query
		for (String term : terms) {

			// Obtenemos los postings de la palabra
			PostingsList postings = index.getPostings(term);

			// Obtenemos el numero de documentos donde aparece
			int docFreq = postings.size();

			// Iteramos sobre los postings de la palabra
			for (Posting posting : postings) {

				int docId = posting.getDocID();

				// Obtenemos el score parcial
				double scoreParcial = scores.getOrDefault(docId, 0.0);

				// TF-IDF de la palabra en el documento
				double tfidf = tfidf(posting.getFreq(), docFreq, numDocs);

				// Actualizamos el valor
				scores.put(docId, scoreParcial + tfidf);

			}

		}

		// Incluimos en el ranking las busquedas
		for (Map.Entry<Integer, Double> entry : scores.entrySet()) {
			ranking.add(entry.getKey(), entry.getValue());
		}

		return ranking;
	}

}
