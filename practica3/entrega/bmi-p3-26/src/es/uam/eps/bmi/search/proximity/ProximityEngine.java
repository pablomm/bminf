package es.uam.eps.bmi.search.proximity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.positional.PositionalPosting;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

/**
 * @author Pablo Marcos Manchon
 * Implementacion de la busqueda proximal.
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
	public ProximityEngine(Index index) {
		super(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		
		boolean literal = false;
		// Comprobamos si es una consulta literal
		if(query.startsWith("\"") && query.endsWith("\"")) {
			// Elimina de la consulta las comillas
			query = query.replace("\"", "");
			literal = true;
		}
		
		// Parseamos la query
		String[] terms_parsed = this.parse(query);

		ArrayList<String> terms;
		
		if (!literal) {
			// Eliminamos duplicados de la consulta para no afectar a la busqueda posicional
			// En caso de no ser busqueda literal
			terms = new ArrayList<String>(new HashSet<String>(Arrays.asList(terms_parsed)));
		} else {
			terms = new ArrayList<String>(Arrays.asList(terms_parsed));
		}

		// Longitud de la query |q|
		int q = terms.size();

		// Inicializamos el ranking
		ranking = new RankingImpl(this.index, cutoff);

		// Heap para iterar sobre los postings de todos los documentos
		PriorityQueue<PositionalQueryPosting> postingHeap = new PriorityQueue<PositionalQueryPosting>();

		// Insertamos todos los postings ordenados por docID
		for (int j=0; j <terms.size(); j++) {
			PostingsList lista_postings =  index.getPostings(terms.get(j));
			for (Posting p : lista_postings) {

				// !! Hacemos casting para tener los postings como postings posicionales
				postingHeap.add(new PositionalQueryPosting((PositionalPosting) p, j));
			}
		}

		// docID actual de la iteracion
		int docID = postingHeap.peek().getDocID();

		// Lista donde incluiremos los postings con un mismo docID
		ArrayList<PositionalQueryPosting> postings_documento = new ArrayList<PositionalQueryPosting>();

		// Iteramos hasta vaciar el heap de postings
		while (!postingHeap.isEmpty()) {

			PositionalQueryPosting qp = postingHeap.poll();

			// Caso un nuevo docID
			if (qp.getDocID() != docID) {

				// Si han aparecido todas las palabras en el docID
				if (postings_documento.size() == q) {
					// Calculamos su puuntuacion usando la busqueda proximal
					// O literal e incluimos en el ranking
					
					if(literal) {
						literal_query(postings_documento, docID);
					} else {
						proximal_query(postings_documento, docID);
					}

				}

				// Actualizamos nuevo docID
				docID = qp.getDocID();
				// Vaciamos el array list
				postings_documento.clear();
				postings_documento.add(qp);

				// Caso el mismo docID
			} else {
				postings_documento.add(qp);
			}
		}

		// Caso los postings restantes
		if (postings_documento.size() == q) {
			// Calculamos su puuntuacion usando la busqueda proximal
			// E incluimos en el ranking
			if(literal) {
				literal_query(postings_documento, docID);
			} else {
				proximal_query(postings_documento, docID);
			}
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
	private void proximal_query(ArrayList<PositionalQueryPosting> postings, int docID) {

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
			Iterator<Integer> it = postings.get(j).getPosting().iterator();
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
	
	private void literal_query(ArrayList<PositionalQueryPosting> postings, int docID) {
		
		// Longitud de la consulta
		int q = postings.size();

		// Puntuacion de la consulta, sera el numero de apariciones 
		double score = 0.0;
		
		// Ordenamos la lista por su aparicion en la consulta (podrian haberse
		// descolocado al incluirse en el heap de documentos)
		Collections.sort(postings, new Comparator<PositionalQueryPosting>() {
		    @Override
		    public int compare(PositionalQueryPosting o1, PositionalQueryPosting o2) {
		        return Integer.compare(o1.getPosicionQuery(), o2.getPosicionQuery());
		    }
		});
		
		int b = -1;
		
		// Lista con los iteradores de posiciones
		ArrayList<Iterator<Integer>> iteradores = new ArrayList<Iterator<Integer>>();
		
		// Lista con posiciones
		Integer[] posiciones = new Integer[q];

		// Inicializacion
		for (int j = 0; j < q; j++) {
			Iterator<Integer> it = postings.get(j).getPosting().iterator();
			iteradores.add(it);
			posiciones[j] = -1;
		}
		boolean end = false, consecutivas;
		
		while(iteradores.get(0).hasNext()) {
			
			// Almacenamos si las palabras aparecidas son consecutivas
			consecutivas = true;
				
			// Obtenemos indice de la primera palabra
			b = iteradores.get(0).next();
			
			// Iteramos sobre las siguientes palabras
			for(int j=1; j<q; j++) {
				Iterator<Integer> it = iteradores.get(j);
				
				// Menor o igual para permitir repeticion
				// de palabras
				while(posiciones[j] <= b && !end) {
					if(it.hasNext()) {
						posiciones[j] = it.next();
					} else {
						// Una de las posiciones no tiene mas palabras
						end = true;
					}
				}
				
				// Condicion de parada, 
				if(end == true) {
					break;
				}
				
				// Si no son palabras consecutivas vuelve a avanzar la primera palabra
				if(posiciones[j] - b != 1) {
					consecutivas = false;
					break;
				} else {
					// Actualiza b como la posicion de la palabra anterior
					b = posiciones[j];
				}
			}
			
			// Caso algoritmo finalizado
			if(end) {
				break;
			}
			
			// Si han sido consecutivas se incrementa en uno la frecuencia
			if(consecutivas) {
				score++;
			}
			
		}
		
		if(score != 0.0) {
			// Incluimos el doc con su puntuacion correspondiente en el ranking
			this.ranking.add(docID, score);
		}
	}

}

/**
 * @author Pablo Marcos Manchon 
 * Estructura usada en el heap de documentos usado en la
 *         busqueda para ordenar por docID los postings.
 */
class PositionalQueryPosting implements Comparable<PositionalQueryPosting> {

	/**
	 * Posting posicional
	 */
	PositionalPosting posting;
	int posicionQuery;

	/**
	 * @param posting Initializa con el posting posicional
	 */
	PositionalQueryPosting(PositionalPosting posting, int posicionQuery) {
		this.posting = posting;
		this.posicionQuery = posicionQuery;

	}

	/**
	 * @return identificador del documento del posting
	 */
	public int getDocID() {
		return posting.getDocID();
	}
	
	public int getPosicionQuery() {
		return this.posicionQuery;
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
