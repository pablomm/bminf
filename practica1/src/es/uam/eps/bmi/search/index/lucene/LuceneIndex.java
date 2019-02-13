package es.uam.eps.bmi.search.index.lucene;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.FileReader;


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

import es.uam.eps.bmi.search.index.Index;




public class LuceneIndex implements Index{
	
	
	public final static String DirectoryPath = "res/index";
	
	private IndexWriter builder;

	public LuceneIndex(String UrlPath) throws IOException {
		
		boolean rebuild = true;
		
		
		
		Directory directory = FSDirectory.open(Paths.get(DirectoryPath));
		
		// Inicio creación del índice
	    Analyzer analyzer = new StandardAnalyzer();
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    if (rebuild) config.setOpenMode(OpenMode.CREATE);
	    else config.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    
	    // Creacion del in
	    builder = new IndexWriter(directory, config);
	    
	    
	    // Abrimos archivo con url por linea
	    BufferedReader reader =  new BufferedReader(new FileReader(UrlPath));

	    
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
        
		
	}
	
	
	

}
