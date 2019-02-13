package es.uam.eps.bmi.search.index.lucene;

import es.uam.eps.bmi.search.index.IndexBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

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
	
	
	public final static String DirectoryPath = "res/index";
	
	public final static  boolean Rebuild = true;
	

	public LuceneBuilder(String UrlPath) throws IOException {
		
		
        
		
	}
	
	private void buildUrls(IndexWriter builder, String collectionPath) throws MalformedURLException, IOException {
		
		
		// Abrimos archivo con url por linea
	    BufferedReader reader =  new BufferedReader(new FileReader(collectionPath));

	    
	    // Incluimos en el index cada una de las URLs
	    String url = reader.readLine();
	    
        while (url != null) {
            Document doc = new Document();
            doc.add(new TextField("path", url, Field.Store.YES));
            FieldType type = new FieldType();
            type.setIndexOptions (IndexOptions.DOCS_AND_FREQS);
            type.setStoreTermVectors (true);
            String text = Jsoup.parse(new URL(url), 10000).text();
            doc.add(new Field("content", text, type));
            builder.addDocument(doc);
            
            url = reader.readLine();
        }
        
        builder.close();
        reader.close();
		
		
		
		
	}

	public void build (String collectionPath, String indexPath) throws IOException {
		
		// Directorio donde se creara el indice
		Directory directory = FSDirectory.open(Paths.get(indexPath));
		
		// Inicio creación del índice
	    Analyzer analyzer = new StandardAnalyzer();
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    if (Rebuild) config.setOpenMode(OpenMode.CREATE);
	    else config.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    
	    // Creacion del indice
	    IndexWriter builder = new IndexWriter(directory, config);
	    
	    
	    File collection = new File(collectionPath);
	    
	    if (collection.isDirectory()) {
	    	// Caso parsear directorio
	    } else {
	    	// Leemos archivos con URLS
	    	buildUrls(builder, collectionPath);
	    }
	    	
	}
	
	
	
}
