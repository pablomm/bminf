package es.uam.eps.bmi.search.index;

import java.io.IOException;
import java.util.List;

import es.uam.eps.bmi.search.index.freq.FreqVector;

public interface Index {

	/**
	 * Obtiene el vocabulario del indice.
	 * 
	 * @return Lista con todos los terminos del indice.
	 * @throws IOException
	 */
	public List<String> getAllTerms() throws IOException;

	/**
	 * Calcula la frecuencia total de un termino
	 * 
	 * @param word
	 * @return Frecuencia del termino
	 */
	public long getTotalFreq(String word) throws IOException;

	/**
	 * Calcula la frecuencia de un termino en un documento
	 * 
	 * @param word
	 * @param      docID: Id del documento
	 * @return Frecuencia del termino
	 * @throws IOException
	 */
	public long getTermFreq(String word, int docID) throws IOException;

	/**
	 * Calcula el vector de frecuencias de un documento
	 * 
	 * @param docID: Id del documento
	 * @return Vector de frecuencias
	 * @throws IOException
	 */
	public FreqVector getDocVector(int docID) throws IOException;

	/**
	 * Devuelve el path de un documento
	 * 
	 * @param docID: Id del documento
	 * @return Path del documento
	 * @throws IOException
	 */
	public String getDocPath(int docID) throws IOException;

	/**
	 * Calcula el numero de documentos donde aparece la palabra
	 * 
	 * @param word
	 * @return frecuencia de documentos
	 * @throws IOException
	 */
	public int getDocFreq(String word) throws IOException;

	/**
	 * Cierra el indice
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;

}
