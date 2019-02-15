package es.uam.eps.bmi.search.index.lucene;

import es.uam.eps.bmi.search.index.IndexBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;

public class LuceneBuilder implements IndexBuilder {

	/**
	 * Define el modo de apertura del indice, OpenMode.CREATE crea el indice
	 * completo, OpenMode.CREATE_OR_APPEND utiliza el indice creado anteriormente.
	 */
	private final static OpenMode openMode = OpenMode.CREATE;

	/**
	 * Indica estadisticas a almacenar en el indice, entre DOCS, FREQS, POSITIONS y
	 * OFFSETS.
	 */
	private final static IndexOptions indexOptions = IndexOptions.DOCS_AND_FREQS;

	/**
	 * Charset de los documentos a incluir en el index
	 */
	private final static String charset = StandardCharsets.UTF_8.name();

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.IndexBuilder#build(java.lang.String,
	 * java.lang.String)
	 */
	public void build(String collectionPath, String indexPath) throws IOException {

		// Directorio donde se creara el indice
		Directory directory = FSDirectory.open(Paths.get(indexPath));

		// Inicio creacion del índice
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);

		// Modo de apertura del indice
		config.setOpenMode(openMode);

		// Creacion del indice
		IndexWriter builder = new IndexWriter(directory, config);

		File collection = new File(collectionPath);

		if (collection.isDirectory()) {
			// Creacion a partir de directorio
			fromDirectory(builder, collection);

		} else if (collectionPath.endsWith(".zip")) {
			// Creacion a partir de zip con documentos
			fromZip(builder, collection);

		} else {
			// Fichero de texto con urls
			fromUrls(builder, collection);
		}

		builder.close();

	}

	/**
	 * Construye el indice a partir de un fichero de URLs
	 * 
	 * @param builder
	 * @param collection
	 * @throws IOException
	 */
	private void fromUrls(IndexWriter builder, File collection) throws IOException {

		// Abrimos archivo con url por linea
		Scanner scanner = new Scanner(collection);

		// Parseamos todas las urls
		while (scanner.hasNextLine()) {

			String url = scanner.nextLine();

			// Creacion de documento del indice
			Document doc = new Document();
			doc.add(new TextField("path", url, Field.Store.YES));

			// Almacenamos docs, frecuencias y vectores de terminos
			FieldType type = new FieldType();
			type.setIndexOptions(indexOptions);
			type.setStoreTermVectors(true);

			// Almacenamos el contenido del documento, parseando con JSoup

			try { // Manejamos la exception si una url falla

				String text = Jsoup.parse(new URL(url), 10000).text();
				doc.add(new Field("content", text, type));

				// Incluimos el documento creado en el indice
				builder.addDocument(doc);

			} catch (IOException e) {

				System.err.println("Error obteniendo " + url);
				System.err.println(e);

			}

		}
		scanner.close();

	}

	/**
	 * Construye el indice a partir de un fichero zip con los archivos
	 * 
	 * @param builder
	 * @param collection
	 * @throws IOException
	 */
	private void fromZip(IndexWriter builder, File collection) throws IOException {

		// Apertura del archivo zip
		ZipFile zipFile = new ZipFile(collection);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		String zipPath = collection.getPath();

		// Iteramos sobre los documentos del zip
		while (entries.hasMoreElements()) {

			ZipEntry entry = entries.nextElement();

			// Ignoramos los subdirectorios
			if (!entry.isDirectory()) {

				// Creacion de documento del indice
				Document doc = new Document();

				// Path del documento
				String filePath = zipPath + "/" + entry.getName();

				// Almacenamos el path
				doc.add(new TextField("path", filePath, Field.Store.YES));

				// Almacenamos docs, frecuencias y vectores de terminos
				FieldType type = new FieldType();
				type.setIndexOptions(indexOptions);
				type.setStoreTermVectors(true);

				// Leemos el contenido del archivo comprimido
				InputStream stream = zipFile.getInputStream(entry);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String content = reader.lines().collect(Collectors.joining());

				// Almacenamos el contenido del documento, parseando con JSoup
				String text = Jsoup.parse(content).text();

				doc.add(new Field("content", text, type));

				// Incluimos el documento creado en el indice
				builder.addDocument(doc);
			}
		}

		zipFile.close();

	}

	/**
	 * Construye el indice a partir de un directorio con ficheros
	 * 
	 * @param builder
	 * @param collection
	 * @throws IOException
	 */
	private void fromDirectory(IndexWriter builder, File collection) throws IOException {

		// Abrimos archivo con url por linea
		File[] directoryListing = collection.listFiles();

		if (directoryListing == null) {
			System.err.println("Directorio vacio.");
			return;
		}

		// Iteramos sobre los documentos del directorio
		for (File file : directoryListing) {

			// Solo procesamos los archivos
			if (!file.isDirectory()) {

				// Creacion de documento del indice
				Document doc = new Document();

				// Almacenamos el path
				doc.add(new TextField("path", file.getPath(), Field.Store.YES));

				// Almacenamos docs, frecuencias y vectores de terminos
				FieldType type = new FieldType();
				type.setIndexOptions(indexOptions);
				type.setStoreTermVectors(true);

				// Almacenamos el contenido del documento, parseando con JSoup
				String text = Jsoup.parse(file, charset).text();

				doc.add(new Field("content", text, type));

				// Incluimos el documento creado en el indice
				builder.addDocument(doc);

			}
		}

	}

}
