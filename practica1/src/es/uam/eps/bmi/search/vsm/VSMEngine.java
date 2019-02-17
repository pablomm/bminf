package es.uam.eps.bmi.search.vsm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.uam.eps.bmi.search.AbstractEngine;

import es.uam.eps.bmi.search.index.lucene.LuceneIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.Ranking;
import es.uam.eps.bmi.search.ranking.impl.RankingDoc;

public class VSMEngine extends AbstractEngine {

	/**
	 * Path al fichero con los modulos de los documentos
	 */
	private String modulePath;

	/**
	 * @param index Indice de lucene
	 */
	public VSMEngine(LuceneIndex index) {
		// Por defecto no se utilizan los modulos del documento
		this(index, null);
	}

	/**
	 * @param index      Indice de Lucene
	 * @param modulePath Path al fichero de modulos
	 */
	public VSMEngine(LuceneIndex index, String modulePath) {
		super(index);
		this.modulePath = modulePath;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {

		// Separamos la consulta por blancos
		String[] words = query.split(" ");

		// Lista de resultados
		List<RankingDoc> results = new ArrayList<>();

		BufferedReader reader = null;
		boolean normalize = false;

		// En caso de especificar el fichero con los modulos
		if (this.modulePath != null) {

			try {
				reader = new BufferedReader(new FileReader(this.modulePath));
				normalize = true;
			} catch (FileNotFoundException e) {

				System.out.println("Error al abrir el fichero con los modulos, "
						+ "se utilizara el producto escalar para realizar el ranking.");
			}
		}

		// Numero de documentos en el indice

		// Hay que arreglar este cast, por alguna razon el constructor
		// de abstract index no hace caso cuando sobrecargo la variable index
		// con tipo LuceneIndex

		int nDocs = ((LuceneIndex) index).getIndex().numDocs();
		double smoothNDocs = nDocs + 1.0;

		// Log2
		double log2 = Math.log(2);

		// Iteramos sobre los docID
		for (int docID = 0; docID < nDocs; docID++) {

			double scalar = 0;

			// Iteramos sobre las palabras de la consulta y calculamos sus tf-idf
			for (String word : words) {

				// Frecuencia de la palabra en el documento
				long freq = index.getTermFreq(word, docID);

				if (freq != 0) { // Si no aparece no aporta nada al documento

					// Calculamos su tf
					double tf = 1 + Math.log(freq) / log2;

					// idf suavizado (|D| + 1) / (|Dt| + 0.5)
					double idf = Math.log(smoothNDocs / (0.5 + index.getDocFreq(word)));
					scalar += tf * idf;

				}

			}

			// Solo dividimos por la norma del documento pues
			// La norma de la query es comun a todos y no afecta al resultado
			if (normalize) {
				String line = reader.readLine();

				if (scalar > 0)
					scalar /= Double.valueOf(line);
			}

			// Si ha coincidido algun termino incluimos en los resultados de la consulta
			if (scalar > 0) {
				results.add(new RankingDoc(scalar, docID, index.getDocPath(docID)));
			}

		}

		// Cierre del fichero de modulos
		if (normalize)
			reader.close();

		// Ordenamos el ranking de mayor puntuacion a menor
		results.sort(Collections.reverseOrder());

		Ranking ranking;

		// Podamos el ranking si es necesario
		if (results.size() > cutoff)
			ranking = new Ranking(results.subList(0, cutoff));
		else
			ranking = new Ranking(results);

		return ranking;

	}

}
