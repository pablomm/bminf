package es.uam.eps.bmi.search.index.lucene;

import es.uam.eps.bmi.search.index.IndexBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
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

	public void build(String collectionPath, String indexPath) throws IOException {
		this.build(collectionPath, indexPath, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uam.eps.bmi.search.index.IndexBuilder#build(java.lang.String,
	 * java.lang.String)
	 */
	public void build(String collectionPath, String indexPath, String modulesPath) throws IOException {

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

		if (modulesPath != null)
			computeDocsModules(indexPath, modulesPath);

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

				content = this.removeHttp(content);
				reader.close();

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

				// Leemos el contenido del archivo comprimido
				InputStream stream = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				String content = reader.lines().collect(Collectors.joining());
				reader.close();

				content = this.removeHttp(content);

				// Almacenamos el contenido del documento, parseando con JSoup
				String text = Jsoup.parse(content).text();

				doc.add(new Field("content", text, type));

				// Incluimos el documento creado en el indice
				builder.addDocument(doc);

			}
		}

	}

	/**
	 * Elimina la cabecera Http no eliminado por JSoup al no tratarse de codigo html
	 * 
	 * @param content: Pagina html con cabecera http
	 * @return Contenido html sin la cabecera
	 */
	private String removeHttp(String content) {
		// Filtramos la cabecera http manualmente
		// Eliminando todo lo anterior a la etiqueta <html>
		if (content.startsWith("HTTP/1.1")) {

			int pos = content.indexOf("<html");
			if (pos == -1)
				pos = content.indexOf("<HTML");
			if (pos != -1) {
				content = content.substring(pos);
			}
		}

		return content;

	}

	/**
	 * Calcula y almacena los modulos de los documentos
	 * 
	 * @param modulesPath Fichero donde almacenar los modulos
	 * @throws IOException
	 */
	private void computeDocsModules(String indexPath, String modulesPath) throws IOException {

		// Abrimos el indice creado
		LuceneIndex index = new LuceneIndex(indexPath);

		// Escritor en el fichero de modulos
		PrintWriter writer = new PrintWriter(modulesPath);

		// Obtenemos todos los terminos del indice
		List<String> terms = index.getAllTerms();

		int nDocs = index.getIndex().numDocs();

		double log2 = Math.log(2);
		double smoothNDocs = nDocs + 1.0;

		// Iteramos sobre los ids de documentos
		for (int docID = 0; docID < nDocs; docID++) {

			double module = 0.0;
			// Iteramos sobre el vocabulario, calculamos |doc|^2
			for (String word : terms) {

				// Frecuencia de la palabra en el documento
				long freq = index.getTermFreq(word, docID);

				if (freq != 0) { // Si no aparece no aporta nada al documento

					// Calculamos su tf
					double tf = 1 + Math.log(freq) / log2;

					// idf suavizado (|D| + 1) / (|Dt| + 0.5)
					double idf = Math.log(smoothNDocs / (0.5 + index.getDocFreq(word)));

					// Sumamos componente al cuadrado
					module += Math.pow(tf * idf, 2);

				}

			}

			// Raiz cuadrada, para obtener el modulo del documento
			module = Math.sqrt(module);

			// Escribimos en el fichero de modulos,
			// Guardados en orden, por tanto no es necesario ningun tipo
			// de identificacion mas
			writer.println(module);

		}

		writer.close();
	}

}
