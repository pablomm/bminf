package es.uam.eps.bmi.search.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.IndexBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneBuilder;
import es.uam.eps.bmi.search.index.lucene.LuceneIndex;

public class TermStats {

	/**
	 * Archivo donde almacenar terminos ordenados por frecuencia total
	 */
	public final static String freqFile = "termfreq.txt";

	/**
	 * Archivo donde almacenar terminos ordenados por frecuencia de documento
	 */
	public final static String docFreqFile = "termdocfreq.txt";

	/**
	 * Path a la coleccion a indexar
	 */
	public final static String collectionPath = "urls.txt";

	/**
	 * Path adonde construir el indice
	 */
	public final static String indexPath = "res/index";

	/**
	 * Construye un indice y ordena y vuelca sus terminos por frecuencia total y
	 * frecuencia de documento
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Construccion y apertura del indice
		System.out.println("Construyendo indice en " + indexPath);
		IndexBuilder builder = new LuceneBuilder();
		builder.build(collectionPath, indexPath);
		Index index = new LuceneIndex(indexPath);

		// Obtenemos el vocabulario del indice
		System.out.println("Extrayendo vocabulario del indice");
		List<String> terms = index.getAllTerms();

		// Lista de terminos ordenados por frecuencia total
		// Extraido de TestEngine
		System.out.println("Ordenando por frecuencia total");
		Collections.sort(terms, new Comparator<String>() {
			public int compare(String t1, String t2) {
				try {
					return (int) Math.signum(index.getTotalFreq(t2) - index.getTotalFreq(t1));
				} catch (IOException ex) {
					ex.printStackTrace();
					return 0;
				}
			}
		});

		// Volcado de frecuencias
		System.out.println("Volcando frecuencias totales en " + freqFile);
		PrintWriter writer = new PrintWriter(freqFile, "UTF-8");
		for (String term : terms) {
			writer.println(term + "\t" + index.getTotalFreq(term));
		}

		writer.close();

		// Lista de terminos ordenados por frecuencia de documento
		System.out.println("Ordenando por frecuencia de documento");

		Collections.sort(terms, new Comparator<String>() {
			public int compare(String t1, String t2) {
				try {
					return (int) Math.signum(index.getDocFreq(t2) - index.getDocFreq(t1));
				} catch (IOException ex) {
					ex.printStackTrace();
					return 0;
				}
			}
		});

		// Volcado de frecuencias
		System.out.println("Volcando frecuencias de documento en " + docFreqFile);

		writer = new PrintWriter(docFreqFile, "UTF-8");
		for (String term : terms) {
			writer.println(term + "\t" + index.getDocFreq(term));
		}

		writer.close();

	}

}
