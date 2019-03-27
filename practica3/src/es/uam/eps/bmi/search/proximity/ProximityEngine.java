package es.uam.eps.bmi.search.proximity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.lucene.LucenePositionalIndex;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;
import es.uam.eps.bmi.search.index.structure.positional.lucene.LucenePositionalPostingsList;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

/**
 * @author pablomm Implementacion de la busqueda proximal.
 *
 */
public class ProximityEngine extends AbstractEngine {

	/**
	 * Ranking de busqueda utilizado en la consulta
	 */
	private RankingImpl ranking;

	/**
	 * @param index Indice posicional de lucene
	 */
	public ProximityEngine(LucenePositionalIndex index) {
		super(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Parseamos la query
		String[] terms_parsed = this.parse(query);

		// Eliminamos duplicados de la consulta para no afectar a la busqueda posicional
		Set<String> terms = new HashSet<String>(Arrays.asList(terms_parsed));

		// Longitud de la query |q|
		int q = terms.size();

		// Inicializamos el ranking
		ranking = new RankingImpl(this.index, cutoff);

		// Heap para iterar sobre los postings de todos los documentos
		PriorityQueue<PositionalQueryPosting> postingHeap = new PriorityQueue<PositionalQueryPosting>();

		// Insertamos todos los postings ordenados por docID
		for (String term : terms) {
			// !! Hacemos un casting a lista de postings posicional de lucene
			LucenePositionalPostingsList lista_postings = (LucenePositionalPostingsList) index.getPostings(term);
			for (Posting p : lista_postings) {

				// !! Hacemos casting para tener los postings como postings posicionales
				postingHeap.add(new PositionalQueryPosting((PositionalPosting) p));
			}
		}

		// docID actual de la iteracion
		int docID = postingHeap.peek().getDocID();

		// Lista donde incluiremos los postings con un mismo docID
		ArrayList<PositionalPosting> postings_documento = new ArrayList<PositionalPosting>();

		// Iteramos hasta vaciar el heap de postings
		while (!postingHeap.isEmpty()) {

			PositionalQueryPosting qp = postingHeap.poll();

			// Caso un nuevo docID
			if (qp.getDocID() != docID) {

				// Si han aparecido todas las palabras en el docID
				if (postings_documento.size() == q) {
					// Calculamos su puuntuacion usando la busqueda proximal
					// E incluimos en el ranking
					proximal_query(postings_documento, docID);

				}

				// Actualizamos nuevo docID
				docID = qp.getDocID();
				// Vaciamos el array list
				postings_documento.clear();
				postings_documento.add(qp.getPosting());

				// Caso el mismo docID
			} else {
				postings_documento.add(qp.getPosting());
			}
		}

		// Caso los postings restantes
		if (postings_documento.size() == q) {
			// Calculamos su puuntuacion usando la busqueda proximal
			// E incluimos en el ranking
			proximal_query(postings_documento, docID);
		}

		return ranking;
	}

	/**
	 * Calcula la puntuacion del documento utilizando busqueda proximal y lo incluye
	 * en el ranking
	 * 
	 * @param postings Lista de postings de diferentes terminos en el mismo docID
	 * @param docID    Identificador del documento
	 */
	private void proximal_query(ArrayList<PositionalPosting> postings, int docID) {

		// Longitud de la consulta
		int q = postings.size();

		// Puntuacion de la consulta
		double score = 0.0;

		// Lista con posiciones
		Integer[] posiciones = new Integer[q];

		// Lista con siguientes posiciones
		Integer[] siguientes = new Integer[q];

		// Lista con los iteradores de posiciones
		ArrayList<Iterator<Integer>> iteradores = new ArrayList<Iterator<Integer>>();

		// a,b del algoritmo de busqueda (notacion del psudocodigo)
		int b = -1, a;

		// Inicializacion
		for (int j = 0; j < q; j++) {
			Iterator<Integer> it = postings.get(j).iterator();
			// Obtenemos los iteradores de posiciones
			iteradores.add(it);

			// Fijamos b como el maximo de las primeras posiciones
			posiciones[j] = it.next();
			if (posiciones[j] > b) {
				b = posiciones[j];
			}

			// Vemos el siguiente
			if (it.hasNext()) {
				siguientes[j] = it.next();
			} else {
				siguientes[j] = Integer.MAX_VALUE;
			}
		}

		// Bucle principal: Mientras b sea distinto de infinito
		while (b != Integer.MAX_VALUE) {

			int i = 0;

			// for j=1 to |q| do
			for (int j = 0; j < q; j++) {

				// While posList[j][p[j]+1] <= n then p[j]++
				while (siguientes[j] < b) {
					Iterator<Integer> it = iteradores.get(j);
					posiciones[j] = siguientes[j];

					if (it.hasNext()) {
						siguientes[j] = it.next();
					} else {
						siguientes[j] = Integer.MAX_VALUE;
					}
				}

				// if posList[j][p[j]] < posList[i][p[i]] then i <- j
				if (posiciones[j] < posiciones[i]) {
					i = j;
				}
			}

			// a <- posList[i][p[i]]
			a = posiciones[i];
			// score <- score + 1/ (b - a + |q| - 2)
			score += 1. / (b - a - q + 2);

			// b <- posList[i][++p[i]]
			posiciones[i] = siguientes[i];
			b = posiciones[i];

			Iterator<Integer> it = iteradores.get(i);
			if (it.hasNext()) {
				siguientes[i] = it.next();
			} else {
				siguientes[i] = Integer.MAX_VALUE;
			}
		}

		// Incluimos el doc con su puntuacion correspondiente en el ranking
		this.ranking.add(docID, score);
	}

}

/**
 * @author pablomm Estructura usada en el heap de documentos usado en la
 *         busqueda para ordenar por docID los postings.
 */
class PositionalQueryPosting implements Comparable<PositionalQueryPosting> {

	/**
	 * Posting posicional
	 */
	PositionalPosting posting;

	/**
	 * @param posting Initializa con el posting posicional
	 */
	PositionalQueryPosting(PositionalPosting posting) {
		this.posting = posting;

	}

	/**
	 * @return identificador del documento del posting
	 */
	public int getDocID() {
		return posting.getDocID();
	}

	/**
	 * @return Devuelve el posting posicional
	 */
	public PositionalPosting getPosting() {
		return posting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PositionalQueryPosting qp) {
		return posting.getDocID() - qp.posting.getDocID();
	}
}
