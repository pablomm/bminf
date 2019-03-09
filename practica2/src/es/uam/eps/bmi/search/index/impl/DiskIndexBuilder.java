package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
public class DiskIndexBuilder extends AbstractIndexBuilder {
	
	private String indexPath;

	/**
	 * Lista de paths
	 */
	private ArrayList<String> paths = new ArrayList<String>();

	/**
	 * Diccionario con postings almacenados en (termino, postingList)
	 */
	private HashMap<String, PostingsListImpl> postings = new HashMap<String, PostingsListImpl>();

	/**
	 * Contador de docID
	 */
	private int docId = 0;
	
	// Hash Map (term, freq)
	private HashMap<String, Long> freqs = new HashMap<String, Long>();
	
	private HashMap<String, Long> positions;

	@Override
	public void build(String collectionPath, String indexPath) throws IOException {

		// Guardamos el indexPath para la creacion del indexCore
		this.indexPath = indexPath;
		
		// Limpiamos el directorio donde se creara el indice
		clear(indexPath);

		// Leemos la coleccion y construimos los postings
		File f = new File(collectionPath);
		if (f.isDirectory()) {
			indexFolder(f);
		} else if (f.getName().endsWith(".zip")) {
			indexZip(f);
		} else {
			indexURLs(f);
		}

		// Guardamos en disco el indice
		writeIndex();

		// Guardamos las normas
		saveDocNorms(indexPath);

	}
	
	
	protected void writeIndex() throws IOException {
		
		// HashMap con Terminos y Posicion en el archivo de postings
		positions = new HashMap<String, Long>();
		
		// Escritor en el fichero de postings
		/*RandomAccessFile access = new RandomAccessFile(new File(indexPath + "/" + Config.POSTINGS_FILE), "rw");
		
		// Iteramos sobre el indice en ram
		for(Entry<String, PostingsListImpl> entry : postings.entrySet()) {
			
			String term = entry.getKey();
			PostingsListImpl post_list = entry.getValue();
			
			long offset = access.getFilePointer();
			
			// Almacenamos el offset asociado a la palabra
			positions.put(term, offset);
			
			// Escribimos el numero de postings
			access.writeInt(post_list.size());
			
			// Iteramos sobre los posintgs de la palabra
			for(Posting post : post_list) {
				// Escribimos el docId y la freq
				access.writeInt(post.getDocID());
				access.writeLong(post.getFreq());
			}
		}
		access.close();*/
		// Escritor en el fichero de postings
		FileOutputStream post_access = new FileOutputStream(indexPath + "/" + Config.POSTINGS_FILE);
		ObjectOutputStream post_stream = new ObjectOutputStream(post_access);
		FileChannel post_channel = post_access.getChannel();
		//FileChannel dict_channel = dict_access.getChannel();
		
		// Iteramos sobre el indice en ram
		for(Entry<String, PostingsListImpl> entry : postings.entrySet()) {
			String term = entry.getKey();
			PostingsListImpl post_list = entry.getValue();
			
			long offset = post_channel.position();
			// System.out.println(offset);
			// Almacenamos el offset asociado a la palabra
			positions.put(term, offset);
			
			// Escribimos la postingslist
			post_stream.writeObject(post_list);
		}
		post_stream.close();
		
		// Ahora guardamos en disco (de forma no muy eficiente) (termino, offset)
		PrintWriter writer = new PrintWriter(new File(indexPath + "/" + Config.DICTIONARY_FILE));
		
		// Iteramos por diccionario de strings / offsets
		
		for(Entry<String, Long> entry : positions.entrySet()) {
			String term = entry.getKey();
			Long offset = entry.getValue();
			
			writer.println(term + " " + offset);	
		}
		writer.close();
		
		// Guardamos los paths en disco
		writer = new PrintWriter(new File(indexPath + "/" + Config.PATHS_FILE));
		
		// Primero escribimos el número de paths
		writer.println(paths.size());
		
		// El resto de las filas corresponden a cada path
		for(String path : paths)
			writer.println(path);
		
		writer.close();
	}

	@Override
	protected void indexText(String text, String path) throws IOException {

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

		// Incluimos en la lista de paths el documento
		this.paths.add(path);

		// Actualizamos el numero de documento
		docId++;
	}

	@Override
	protected Index getCoreIndex() throws IOException {
		// Creamos el indice con la version en RAM ya cargada,
		// Sin tener que leer del disco nuevamente
		return new DiskIndex(indexPath, this.positions, true);
	}
}
