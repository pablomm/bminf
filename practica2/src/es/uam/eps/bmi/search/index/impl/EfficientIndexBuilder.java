package es.uam.eps.bmi.search.index.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

/**
 * Constructor del indice en RAM, almacena la lista de paths en el fichero
 * indexPath/Config.PATHS_FILE y el diccionario con postings en el fichero
 * indexPath/Config.INDEX_PATH.
 */
public class EfficientIndexBuilder extends AbstractIndexBuilder {

	private String indexPath;

	/**
	 * Diccionario con postings almacenados en (termino, postingList)
	 */
	private HashMap<String, PostingsListImpl> postings = new HashMap<String, PostingsListImpl>();

	/**
	 * Contador de docID
	 */
	private int docId = 0;

	/**
	 * Mapa utilizado para el parse de frecuencias en los documentos
	 */
	private HashMap<String, Long> freqs = new HashMap<String, Long>();

	/**
	 * Max number of documents in RAM
	 */
	private long limit;

	/**
	 * Numero de indices auxiliares cargados
	 */
	private int num_index = 0;

	/**
	 * Numero de documentos cargados
	 */
	private long numDocsLoaded = 0;

	private PrintWriter pathWriter;

	public EfficientIndexBuilder() {
		this(500);
	}

	public EfficientIndexBuilder(long limit) {
		this.limit = limit;
	}

	@Override
	public void build(String collectionPath, String indexPath) throws IOException {

		// Guardamos el indexPath para la creacion del indexCore
		this.indexPath = indexPath;

		// Limpiamos el directorio donde se creara el indice
		clear(indexPath);

		// Guardamos los paths en disco
		this.pathWriter = new PrintWriter(new File(indexPath + "/" + Config.PATHS_FILE));

		// Leemos la coleccion y construimos los postings
		File f = new File(collectionPath);
		if (f.isDirectory()) {
			indexFolder(f);
		} else if (f.getName().endsWith(".zip")) {
			indexZip(f);
		} else {
			indexURLs(f);
		}

		// Si quedan palabra en ram las volcamos en otro indice parcial
		// (Lo cual es lo mas probable)
		if (numDocsLoaded > 0) {
			writeAuxIndex();
		}

		// Cerramos el escritor del fichero de disco
		this.pathWriter.close();

		// Mergeamos los indices
		mergeAuxIndex();

		// Guardamos las normas
		saveDocNorms(indexPath);

	}

	/**
	 * Vuelca un indice parcial en memoria
	 * 
	 * @throws IOException
	 */
	protected void writeAuxIndex() throws IOException {

		// Escritor en el fichero de postings auxiliar
		RandomAccessFile access = new RandomAccessFile(new File(indexPath + "/postings_auxiliar_" + num_index + ".dat"),
				"rw");

		// Ahora guardamos en disco (termino, offset)
		PrintWriter writer = new PrintWriter(new File(indexPath + "/diccionario_auxiliar_" + num_index + ".dat"));

		// Por compatibilidad con version anterior de DiskIndex y no cambiarlo
		access.writeInt(0);

		// Iteramos alfabeticamente
		SortedSet<String> keys = new TreeSet<>(postings.keySet());
		for (String term : keys) {

			PostingsListImpl post_list = postings.get(term);

			// Almacenamos el offset asociado a la palabra si es necesario

			writer.println(term);

			// Volcamos el posting
			writePostList(post_list, access);

		}

		access.close();
		writer.close();

		// Actualizamos el numero de indice y ponemos a cero el contador de documentos
		this.num_index++;
		this.numDocsLoaded = 0;

		// Vaciamos los postings actuales
		this.postings.clear();
	}

	/**
	 * Escribe en disco una lista de postings
	 * 
	 * @param post_list
	 * @param access
	 * @throws IOException
	 */
	private void writePostList(PostingsListImpl post_list, RandomAccessFile access) throws IOException {
		// Escribimos el numero de postings
		access.writeInt(post_list.size());

		// Iteramos sobre los posintgs de la palabra
		for (Posting post : post_list) {
			// Escribimos el docId y la freq
			access.writeInt(post.getDocID());
			access.writeLong(post.getFreq());
		}
	}

	/**
	 * Mezcla los indices parciales
	 * 
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private void mergeAuxIndex() throws NumberFormatException, IOException {

		// Escritor en el fichero de postings auxiliar
		RandomAccessFile access = new RandomAccessFile(new File(indexPath + "/" + Config.POSTINGS_FILE), "rw");

		// Ahora guardamos en disco (termino, offset)
		PrintWriter writer = new PrintWriter(new File(indexPath + "/" + Config.DICTIONARY_FILE));

		access.writeInt(0);

		PriorityQueue<IndexEHeap> heap = new PriorityQueue<IndexEHeap>(this.num_index);

		// Creamos las estructuras para hacer el merge
		for (int i = 0; i < this.num_index; i++) {
			heap.add(new IndexEHeap(this.indexPath, i));
		}

		IndexEHeap element = heap.remove();

		// Caso base del algoritmo
		String currentTerm = element.getTerm();
		PostingsListImpl currentList = element.getNext();
		if (element.hasNext()) {
			heap.add(element);
		} else {
			element.close();
		}

		long offset;

		// Mientras no este vacio
		while (!heap.isEmpty()) {
			element = heap.remove();

			String newTerm = element.getTerm();

			if (newTerm == currentTerm) {
				// Hacemos el merge de las listas de postings
				currentList.merge(element.getNext());
			} else {
				// Si hay nuevo termino guardamos en disco el anterior
				offset = access.getFilePointer();
				writer.println(currentTerm + " " + offset);
				writePostList(currentList, access);
				currentTerm = newTerm;
				currentList = element.getNext();
			}

			// Volvemos a incluir en el heap el elemento
			if (element.hasNext()) {
				heap.add(element);
			} else {
				element.close();
			}
		}

		// Ultima palabra
		offset = access.getFilePointer();
		writer.println(currentTerm + " " + offset);
		writePostList(currentList, access);
		writer.close();
		access.close();
	}

	@Override
	protected void indexText(String text, String path) throws IOException {

		// Escribimos la ruta del docID
		this.pathWriter.println(path);

		// Parseamos las palabras del documento igual que en el engine
		String[] words = text.toLowerCase().split("\\P{Alpha}+");

		freqs.clear();

		// Iteramos sobre los terminos del documento y contamos sus frecuencias
		for (String word : words) {
			long freq = freqs.getOrDefault(word, 0L);
			freqs.put(word, freq + 1);
		}

		// Creamos postings con las frecuencias observadas
		for (Map.Entry<String, Long> entry : freqs.entrySet()) {

			Posting posting = new Posting(docId, entry.getValue());
			PostingsListImpl postingList = postings.get(entry.getKey());

			// Si no obtenemos posting creamos uno vacio
			if (postingList == null) {
				postingList = new PostingsListImpl();
				postings.put(entry.getKey(), postingList);
			}

			// Incluimos posting en la lista de postings
			postingList.add(posting);

		}

		// Actualizamos el numero de documento
		docId++;
		numDocsLoaded++;

		// Si hemos llegado al limite volcamos el indice parcial
		if (this.numDocsLoaded >= this.limit) {
			writeAuxIndex();
		}
	}

	@Override
	protected Index getCoreIndex() throws IOException {
		// Creamos el indice con la version en RAM ya cargada,
		// Sin tener que leer del disco nuevamente
		return new DiskIndex(indexPath, false);
	}
}

/**
 * Estructura interna para realizar la iteracion entre los indices para hacer el
 * merge usando un Heap
 */
class IndexEHeap implements Comparable<IndexEHeap> {

	/**
	 * Termino actual del indice parcial
	 */
	private String term;

	/**
	 * Lector del fichero de terminos
	 */
	private BufferedReader termReader;

	/**
	 * Lector de postings en binario
	 */
	private RandomAccessFile access;

	/**
	 * Siguiente lista de postings
	 */
	private PostingsListImpl next;

	/**
	 * @param postings
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public IndexEHeap(String indexPath, int numIndex) throws NumberFormatException, IOException {

		// Lector de la lista de offsets
		this.termReader = new BufferedReader(new FileReader(indexPath + "/diccionario_auxiliar_" + numIndex + ".dat"));
		this.access = new RandomAccessFile(new File(indexPath + "/postings_auxiliar_" + numIndex + ".dat"), "r");
		this.access.readInt();
		this.updateNext();

	}

	public String getTerm() {
		return this.term;
	}

	public boolean hasNext() {
		return term != null;
	}

	/**
	 * @throws IOException
	 * 
	 */
	private void updateNext() throws IOException {

		this.term = this.termReader.readLine();

		if (term != null) {

			PostingsListImpl lista = new PostingsListImpl();

			// Leemos el numero de postings de la lista
			int numero_postings = this.access.readInt();

			for (int i = 0; i < numero_postings; i++) {

				lista.add(this.access.readInt(), this.access.readLong());

			}

			this.next = lista;

		} else {
			this.term = null;
			this.next = null;
		}

	}

	/**
	 * Obtiene el la siguiente lista de postings
	 * 
	 * @return Siguiente posting
	 * @throws IOException
	 */
	public PostingsListImpl getNext() throws IOException {
		PostingsListImpl next_aux = this.next;
		this.updateNext();
		return next_aux;
	}

	public void close() throws IOException {
		this.access.close();
		this.termReader.close();
	}

	@Override
	public int compareTo(IndexEHeap o) {
		return this.term.compareTo(o.getTerm());
	}

}
