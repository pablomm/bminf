package es.uam.eps.bmi.search.index.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
// import java.util.HashMap;
import java.util.HashMap;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

public class DiskIndex extends AbstractIndex {
	
	private String indexPath=null;

	/**
	 * Lista de paths de los documentos indexados por ID
	 */
	private ArrayList<String> paths;
	
	/**
	 * Diccionario con listas de postings indexadas por termino
	 */
	private HashMap<String, Long> offsets = new HashMap<String, Long>();

	/**
	 * Punto de acceso para lectura de los offsets
	 */
	private RandomAccessFile access;
	
	public DiskIndex(String indexPath, ArrayList<String> paths, HashMap<String, Long> offsets, boolean load_norms_flag) throws NoIndexException {

		this.indexPath = indexPath;
		this.paths = paths;
		this.offsets = offsets;

		try {
			this.access = new RandomAccessFile(new File(Config.POSTINGS_FILE), "r");
			
			// Cargamos las normas
			if (load_norms_flag)
				this.loadNorms(indexPath);
				
		} catch (IOException e) {
			throw new NoIndexException(indexPath);
		}
	}


	public DiskIndex(String indexPath) throws NoIndexException {

		this.indexPath = indexPath;
		

		try {
			
			// Cargamos el indice
			loadIndex();
			
			// Cargamos las normas
			this.loadNorms(indexPath);

		} catch (IOException e) {
			throw new NoIndexException(indexPath);
		}
	}
	
	private void loadIndex() throws IOException {
		
		
		FileReader freader = new FileReader(Config.PATHS_FILE);
		
		// Primero leemos los paths
		BufferedReader reader = new BufferedReader(freader);
		
		String line;
		while((line = reader.readLine()) != null) {
			this.paths.add(line);
		}
		reader.close();
		freader.close();
		
		// Leemos el hasmap de offsets
		freader = new FileReader(Config.DICTIONARY_FILE);
		reader = new BufferedReader(freader);
		
		while((line = reader.readLine()) != null) {
			String[] spliteado = line.split(" ");
			offsets.put(spliteado[0], Long.parseLong(spliteado[1]));
		}
		
		reader.close();
		freader.close();
		
		this.access = new RandomAccessFile(new File(Config.POSTINGS_FILE), "r");

		
	}

	@Override
	public int numDocs() {
		return paths.size();
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {

		PostingsListImpl lista = new PostingsListImpl();
		
		try {
			// Obtenemos el offset del termino
			Long pos = this.offsets.get(term);
			
			// Hacemos un seek a la posicion
			this.access.seek(pos);
			
			// Leemos el numero de postings
			int n_postings = this.access.readInt();
			
			// Iteramos sobre los postings de la palabra
			for(int i=0; i<n_postings; i++) {
				
				int docID = access.readInt();
				long frecuencia = access.readLong();
				
				// Incluimos en la lista de postings
				lista.add(new Posting(docID, frecuencia));
			}

			
		} catch (IOException e) {
			throw new NoIndexException(indexPath);
		}

		return lista;
	}

	@Override
	public Collection<String> getAllTerms() throws IOException {
		return offsets.keySet();
	}

	@Override
	public long getTotalFreq(String term) throws IOException {

		return ((PostingsListImpl)this.getPostings(term)).getTotalFreq();
	}

	@Override
	public long getDocFreq(String term) throws IOException {
		int n_postings = 0;
		try {
			// Obtenemos el offset del termino
			Long pos = this.offsets.get(term);
			
			// Hacemos un seek a la posicion
			this.access.seek(pos);
			
			// Leemos el numero de postings
			n_postings = this.access.readInt();
			
			
		} catch (IOException e) {
			throw new NoIndexException(indexPath);
		}

		return n_postings;
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return this.paths.get(docID);
	}
}
