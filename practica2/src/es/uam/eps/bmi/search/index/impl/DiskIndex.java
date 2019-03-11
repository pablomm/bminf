package es.uam.eps.bmi.search.index.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

public class DiskIndex extends AbstractIndex {

	/**
	 * Lista de paths de los documentos indexados por ID
	 */
	private ArrayList<String> paths = new ArrayList<String>();

	/**
	 * Diccionario con listas de postings indexadas por termino
	 */
	private HashMap<String, Long> positions = new HashMap<String, Long>();

	/**
	 * Puntero de acceso al fichero de postings
	 */
	private RandomAccessFile access;

	private String indexPath;

	/**
	 * Carga un indice en disco a partir del mapa de posiciones, el array de paths
	 * Constructor llamado en getIndexCore por eficiencia para evitar cargar
	 * duplicados
	 * 
	 * @param indexPath
	 * @param positions
	 * @param paths
	 * @param load_norms
	 * @throws NoIndexException
	 */
	public DiskIndex(String indexPath, HashMap<String, Long> positions, ArrayList<String> paths, boolean load_norms)
			throws NoIndexException {

		this.paths = paths;
		this.positions = positions;
		this.indexPath = indexPath;
		try {

			this.access = new RandomAccessFile(new File(indexPath + "/" + Config.POSTINGS_FILE), "r");

			if (load_norms) {
				this.loadNorms(indexPath);
			}

		} catch (FileNotFoundException e) {
			// Excepcion en caso de fallo al leer el indice
			throw new NoIndexException(indexPath);
		}
	}

	/**
	 * Carga un indice en disco
	 * 
	 * @param indexPath
	 * @throws NoIndexException
	 */
	public DiskIndex(String indexPath) throws NoIndexException {
		
		this(indexPath, true);
	}
	/**
	 * Carga un indice en disco, con opcion a cargar o no las normas
	 * 
	 * @param indexPath
	 * @throws NoIndexException
	 */
	public DiskIndex(String indexPath, boolean load_norms) throws NoIndexException {

		this.indexPath = indexPath;

		try {
			// Cargamos los paths
			loadPaths();

			// Cargamos el diccionario de posiciones
			loadPositions();

			// Abrimos el puntero a los postings
			this.access = new RandomAccessFile(new File(indexPath + "/" + Config.POSTINGS_FILE), "r");

			// Cargamos las normas
			this.loadNorms(this.indexPath);

		} catch (IOException e) {
			// Excepcion en caso de fallo al leer el indice
			throw new NoIndexException(indexPath);
		}

	}

	/**
	 * Lee el fichero con los paths
	 * 
	 * @throws IOException
	 */
	private void loadPaths() throws IOException {

		// Ruta del fichero de paths
		String pathFile = this.indexPath + "/" + Config.PATHS_FILE;

		// Leemos linea a linea los paths
		BufferedReader reader = new BufferedReader(new FileReader(pathFile));
		
		// Descartamos la primera linea que contiene el numero de posiciones
		String line = reader.readLine();
		
		// Cada linea contiene un path, ordenados por docID
		while (line != null) {
			this.paths.add(line);
			line = reader.readLine();
		}

		reader.close();
	}

	private void loadPositions() throws IOException {

		String positionsPath = this.indexPath + "/" + Config.DICTIONARY_FILE;

		// Leemos linea a linea las posiciones
		BufferedReader reader = new BufferedReader(new FileReader(positionsPath));
		// Desechamos la primera linea que contiene el numero de palabras
		String line = reader.readLine();
		
		line = reader.readLine();

		// Cada linea contiene termino offset
		while (line != null) {

			String[] spliteado = line.split(" ");

			// Incluimos (termino, offset)
			this.positions.put(spliteado[0], Long.parseLong(spliteado[1]));
			line = reader.readLine();

		}
		reader.close();

	}

	@Override
	public int numDocs() {
		return this.paths.size();
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {

		// Obtenemos offset del termino
		long offset = this.positions.get(term);

		this.access.seek(offset);

		PostingsListImpl lista = new PostingsListImpl();

		// Leemos el numero de postings de la lista
		int numero_postings = this.access.readInt();

		for (int i = 0; i < numero_postings; i++) {

			lista.add(this.access.readInt(), this.access.readLong());

		}

		return lista;

	}

	@Override
	public Collection<String> getAllTerms() throws IOException {
		return this.positions.keySet();
	}

	@Override
	public long getTotalFreq(String term) throws IOException {

		return ((PostingsListImpl) this.getPostings(term)).getTotalFreq();
	}

	@Override
	public long getDocFreq(String term) throws IOException {
		// Obtenemos offset del termino
		long offset = this.positions.get(term);

		this.access.seek(offset);

		// Leemos el numero de postings de la lista
		int numero_postings = this.access.readInt();

		return numero_postings;
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		return this.paths.get(docID);
	}
}
