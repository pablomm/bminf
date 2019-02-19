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
	private String modulePath = null;
	
	
	private final static String defaultModulesPath = "modules.txt";

	/**
	 * @param index Indice de lucene
	 */
	public VSMEngine(LuceneIndex index) {
		// Por defecto no se utilizan los modulos del documento
		this(index, defaultModulesPath);
	}

	/**
	 * @param index      Indice de Lucene
	 * @param modulePath Path al fichero de modulos
	 */
	public VSMEngine(LuceneIndex index, String modulePath) {
		super(index);
		this.modulePath = modulePath;

	}
	
	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.search.SearchEngine#search(java.lang.String, int)
	 */
	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		return this.search(query, cutoff, modulePath != null);
	}


	
	/**
	 * Permite elegir si realizar la normalizacion o no
	 * @param query: Consulta a buscar en el indice
	 * @param cutoff: Numero maximo de resultados a mostrar
	 * @param normalize: Flag para normalizar o no
	 * @return: Search ranking con el resultado de la consulta
	 * @throws IOException
	 */
	public SearchRanking search(String query, int cutoff, boolean normalize) throws IOException {

		// Separamos la consulta por blancos
		String[] words = query.split(" ");

		// Lista de resultados
		List<RankingDoc> results = new ArrayList<>();

		BufferedReader reader = null;


		// En caso de especificar el fichero con los modulos
		if (normalize) {

			try {
				reader = new BufferedReader(new FileReader(this.modulePath));

			} catch (FileNotFoundException e) {
				normalize = false;
				System.out.println("Error al abrir el fichero con los modulos, "
						+ "se utilizara el producto escalar para realizar el ranking.");
			}
		}

		// Numero de documentos en el indice
		int nDocs = ((LuceneIndex) index).getIndex().numDocs();
		double smoothNDocs = nDocs + 1.0;

		// Log2
		double log2 = Math.log(2);
		
		// Calculamos los idfs de la consulta de antemano
		List<Double> idfs = new ArrayList<Double>();
		
		for(String word: words) {
			double idf = Math.log(smoothNDocs / (0.5 + index.getDocFreq(word))) / log2;
			idfs.add(idf);
		}

		// Iteramos sobre los docID
		for (int docID = 0; docID < nDocs; docID++) {

			double scalar = 0;
			int i = 0;

			// Iteramos sobre las palabras de la consulta y calculamos sus tf-idf
			for (String word : words) {

				// Frecuencia de la palabra en el documento
				long freq = index.getTermFreq(word, docID);

				if (freq != 0) { // Si no aparece no aporta nada al documento

					// Calculamos su tf
					double tf = 1 + Math.log(freq) / log2;

					// idf suavizado (|D| + 1) / (|Dt| + 0.5)
					double idf =idfs.get(i);
					
					scalar += tf * idf;

				}
				
				// Actualizamos el numero de palabra de la query
				i++;

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
