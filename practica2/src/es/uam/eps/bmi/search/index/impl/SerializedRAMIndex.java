package es.uam.eps.bmi.search.index.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

/**
 * Indice Serializado cargado en RAM
 *
 */
public class SerializedRAMIndex extends AbstractIndex {

	/**
	 * Lista de paths de los documentos indexados por ID
	 */
	private ArrayList<String> paths;

	/**
	 * Diccionario con listas de postings indexadas por termino
	 */
	private HashMap<String, PostingsListImpl> postings;

	/**
	 * Version para cargar el indice sin leer de disco por eficiencia
	 * 
	 * @param paths    Lista de paths de los documentos indexados por ID
	 * @param postings Diccionario con listas de postings indexadas por termino
	 * @throws FileNotFoundException
	 */
	public SerializedRAMIndex(String indexPath, ArrayList<String> paths, HashMap<String, PostingsListImpl> postings,
			boolean load_norms_flag) throws FileNotFoundException {
		this.paths = paths;
		this.postings = postings;

		if (load_norms_flag)
			loadNorms(indexPath);
	}

	@SuppressWarnings("unchecked")
	public SerializedRAMIndex(String indexPath) throws NoIndexException {

		// Leemos el diccionario con los postings
		FileInputStream file;
		try {
			file = new FileInputStream(indexPath + "/" + Config.INDEX_FILE);

			ObjectInputStream in = new ObjectInputStream(file);

			// Leemos el diccionario de postings
			this.postings = (HashMap<String, PostingsListImpl>) in.readObject();

			in.close();
			file.close();

			// Leemos el diccionario con los postings
			file = new FileInputStream(indexPath + "/" + Config.PATHS_FILE);
			in = new ObjectInputStream(file);

			// Leemos el diccionario de postings
			this.paths = (ArrayList<String>) in.readObject();

			in.close();
			file.close();

			// Cargamos las normas
			this.loadNorms(indexPath);

		} catch (IOException | ClassNotFoundException e) {
			
			throw new NoIndexException(indexPath);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#numDocs()
	 */
	@Override
	public int numDocs() {

		return paths.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getPostings(java.lang.String)
	 */
	@Override
	public PostingsList getPostings(String term) throws IOException {

		return this.postings.get(term);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getAllTerms()
	 */
	@Override
	public Collection<String> getAllTerms() throws IOException {

		return this.postings.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getTotalFreq(java.lang.String)
	 */
	@Override
	public long getTotalFreq(String term) throws IOException {

		return this.postings.get(term).getTotalFreq();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getDocFreq(java.lang.String)
	 */
	@Override
	public long getDocFreq(String term) throws IOException {

		// La frecuencia de documentos es la longitud de la lista de postings
		return this.postings.get(term).size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.Index#getDocPath(int)
	 */
	@Override
	public String getDocPath(int docID) throws IOException {
		return paths.get(docID);
	}

}
