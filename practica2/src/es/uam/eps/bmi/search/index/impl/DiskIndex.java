package es.uam.eps.bmi.search.index.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Stream;

import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.Config;
import es.uam.eps.bmi.search.index.NoIndexException;
import es.uam.eps.bmi.search.index.structure.PostingsList;
import es.uam.eps.bmi.search.index.structure.impl.PostingsListImpl;

public class DiskIndex extends AbstractIndex {

	/**
	 * Lista de paths de los documentos indexados por ID
	 */
	//private ArrayList<String> paths;

	/**
	 * Diccionario con listas de postings indexadas por termino
	 */
	// private HashMap<String, PostingsListImpl> postings;
	
	private HashMap<String, Long> positions = null;
	
	// private FileInputStream access;
	
	/**
	 * Path del archivo con las postinglists
	 */
	private String postingsPath;
	
	/**
	 * Path del archivo con las postinglists
	 */
	private String pathsPath;
	
	/**
	 * Número de paths en el fichero
	 */
	private final int nPaths;
	
	/**
	 * Lector estático para no tener que reabrir streams constantemente. Lo usaremos para leer los postings
	 */
	private static ObjectInputStream in = null;
	
	/**
	 * FileChannel asociado a in. Sin él no podríamos averiguar la posición actual en el fichero
	 */
	private static FileChannel channel = null;

	/**
	 * Version para cargar el indice sin leer de disco por eficiencia
	 * 
	 * @param paths    Lista de paths de los documentos indexados por ID
	 * @param postings Diccionario con listas de postings indexadas por termino
	 * @throws FileNotFoundException
	 */
	public DiskIndex(String indexPath, HashMap<String, Long> positions, boolean load_norms_flag)
			throws FileNotFoundException {
		this.positions = positions;
		postingsPath = indexPath + "/" + Config.POSTINGS_FILE;
		// access = new FileInputStream(postingsPath);
		try {
			if (in!=null) in.close();
		
			FileInputStream stream = new FileInputStream(postingsPath);
			in = new ObjectInputStream(stream);
			channel = stream.getChannel();
			
			BufferedReader reader = new BufferedReader(new FileReader(indexPath + "/" + Config.PATHS_FILE));
			
			nPaths = Integer.parseInt(reader.readLine());
			
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new FileNotFoundException();
		}

		if(load_norms_flag)
			loadNorms(indexPath);
	}

	//@SuppressWarnings("unchecked")
	public DiskIndex(String indexPath) throws NoIndexException {

		// Leemos el diccionario con los postings
		// FileInputStream file;
		try {
			/*file = new FileInputStream(indexPath + "/" + Config.POSTINGS_FILE);

			ObjectInputStream in = new ObjectInputStream(file);

			// Leemos el diccionario de postings
			this.postings = (HashMap<String, PostingsListImpl>) in.readObject();

			in.close();
			file.close();*/
			// access = new RandomAccessFile(new File(indexPath + "/" + Config.POSTINGS_FILE), "r");
			
			
			postingsPath = indexPath + "/" + Config.POSTINGS_FILE;
			pathsPath = indexPath + "/" + Config.PATHS_FILE;
			// access = new FileInputStream(postingsPath);
			
			if (DiskIndex.in!=null) in.close();
			in=new ObjectInputStream(new FileInputStream(postingsPath));
			
			BufferedReader reader = new BufferedReader(new FileReader(indexPath + "/" + Config.PATHS_FILE));
			
			nPaths = Integer.parseInt(reader.readLine());
			
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new NoIndexException(indexPath);
		}
	}

	@Override
	public int numDocs() {
		return nPaths;
	}

	@Override
	public PostingsList getPostings(String term) throws IOException {
		long offset = positions.get(term);
		
		try {
			
			// Abrimos el fichero y saltamos al byte
			//FileInputStream access = new FileInputStream(postingsPath);
			/*if (access.getChannel().position() > offset) {
				access.close();
				access = new FileInputStream(postingsPath);
			} else offset -= access.getChannel().position();*/
			// Caso particular, el primer elemento posee el offset de la CABECERA de los datos, y no leerá bien
			//if (offset!=4)
			//	access.skip(offset);
			
			// Si estamos leyendo más allá de nuestra posición actual reiniciamos in y channel
			if (channel.position() > offset) {
				in.close();
				FileInputStream stream = new FileInputStream(postingsPath);
				in = new ObjectInputStream(stream);
				channel = stream.getChannel();
				// En caso contrario desplazamos el buffer para que saltemos a la posición deseada
			} else offset -= channel.position();
			
			// Leemos la PostingsList en la posición y la retornamos
			//ObjectInputStream in = new ObjectInputStream(access);
			in.skip(offset);
			
			PostingsListImpl list = (PostingsListImpl) in.readObject();
			
			//in.close();

			return list;
		} catch (StreamCorruptedException | ClassNotFoundException e) {
			System.out.println(term + " : " + String.valueOf(offset));
			e.printStackTrace();
			throw new IOException("PostingsList corrupted or bad formatted");
		}
	}

	@Override
	public Collection<String> getAllTerms() throws IOException {
		return positions.keySet();
	}

	@Override
	public long getTotalFreq(String term) throws IOException {
		
		long offset = positions.get(term);
		
		try {
			
			// Abrimos el fichero y saltamos al byte
			//FileInputStream access = new FileInputStream(postingsPath);
			/*if (access.getChannel().position() > offset) {
				access.close();
				access = new FileInputStream(postingsPath);
			} else offset -= access.getChannel().position();*/
			// Caso particular, el primer elemento posee el offset de la CABECERA de los datos, y no leerá bien
			//if (offset!=4)
			//	access.skip(offset);
			
			// Si estamos leyendo más allá de nuestra posición actual reiniciamos in y channel
			if (channel.position() > offset) {
				in.close();
				FileInputStream stream = new FileInputStream(postingsPath);
				in = new ObjectInputStream(stream);
				channel = stream.getChannel();
				// En caso contrario desplazamos el buffer para que saltemos a la posición deseada
			} else offset -= channel.position();
			
			// Leemos la PostingsList en la posición y la retornamos
			//ObjectInputStream in = new ObjectInputStream(access);
			in.skip(offset);
			
			PostingsListImpl list = (PostingsListImpl) in.readObject();
			
			//in.close();

			return list.getTotalFreq();
		} catch (StreamCorruptedException | ClassNotFoundException e) {
			System.out.println(term + " : " + String.valueOf(offset));
			e.printStackTrace();
			throw new IOException("PostingsList corrupted or bad formatted");
		}
	}

	@Override
	public long getDocFreq(String term) throws IOException {
		
		long offset = positions.get(term);
		
		try {
			
			// Abrimos el fichero y saltamos al byte
			//FileInputStream access = new FileInputStream(postingsPath);
			/*if (access.getChannel().position() > offset) {
				access.close();
				access = new FileInputStream(postingsPath);
			} else offset -= access.getChannel().position();*/
			// Caso particular, el primer elemento posee el offset de la CABECERA de los datos, y no leerá bien
			//if (offset!=4)
			//	access.skip(offset);
			
			// Si estamos leyendo más allá de nuestra posición actual reiniciamos in y channel
			if (channel.position() > offset) {
				in.close();
				FileInputStream stream = new FileInputStream(postingsPath);
				in = new ObjectInputStream(stream);
				channel = stream.getChannel();
				// En caso contrario desplazamos el buffer para que saltemos a la posición deseada
			} else offset -= channel.position();
			
			// Leemos la PostingsList en la posición y la retornamos
			//ObjectInputStream in = new ObjectInputStream(access);
			in.skip(offset);
			
			PostingsListImpl list = (PostingsListImpl) in.readObject();
			
			//in.close();

			// La frecuencia de documentos es la longitud de la lista de postings
			return list.size();
		} catch (StreamCorruptedException | ClassNotFoundException e) {
			System.out.println(term + " : " + String.valueOf(offset));
			e.printStackTrace();
			throw new IOException("PostingsList corrupted or bad formatted");
		}
	}

	@Override
	public String getDocPath(int docID) throws IOException {
		
		String line = null;
		
		try (Stream<String> lines = Files.lines(Paths.get(pathsPath))) {
		    line = lines.skip(docID+1).findFirst().get();
		}
		
		return line;
	}
}
