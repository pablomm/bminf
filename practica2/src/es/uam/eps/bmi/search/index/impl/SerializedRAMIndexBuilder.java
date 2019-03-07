package es.uam.eps.bmi.search.index.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.Index;
import es.uam.eps.bmi.search.index.AbstractIndexBuilder;
import es.uam.eps.bmi.search.index.structure.Posting;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

public class SerializedRAMIndexBuilder extends AbstractIndexBuilder {

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

		// Guardamos el hashmap con Postings
		FileOutputStream fos = new FileOutputStream(indexPath + "/" + Config.INDEX_FILE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(postings);
		oos.close();

		// Guardamos la lista de paths
		fos = new FileOutputStream(indexPath + "/" + Config.PATHS_FILE);
		oos = new ObjectOutputStream(fos);
		oos.writeObject(paths);
		oos.close();

		// Guardamos las normas
		saveDocNorms(indexPath);

	}

	@Override
	protected void indexText(String text, String path) throws IOException {

		// Parseamos las palabras del documento igual que en el engine
		String[] words = text.toLowerCase().split("\\P{Alpha}+");

		// Hash Map (term, freq)
		HashMap<String, Long> freqs = new HashMap<String, Long>();

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
		return new SerializedRAMIndex(indexPath, paths, postings, false);
	}

}
