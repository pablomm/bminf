package es.uam.eps.bmi.search.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	/**
	 * 
	 */
	private IndexBuilder builder;
	
	/**
	 * 
	 */
	private int maxDocs;
	
	private int numDocs = 0;
	
	private int maxURLS;
	
	private HashSet<String> indexed = new HashSet<String>();
	
	/**
	 * 
	 */
	private LinkedList<String> fifo = new LinkedList<String>();
	
	/**
	 * @param builder
	 */
	public WebCrawler(IndexBuilder builder) {
		this(builder, 1000, 1000);
	}
	/**
	 * @param builder
	 * @param maxDocs
	 * @param maxURLS
	 */
	public WebCrawler(IndexBuilder builder, int maxDocs, int maxURLS) {
		this.builder = builder;
		this.maxDocs = maxDocs;
		this.maxURLS = maxURLS;
	}
	
	public int getNumDocs() {
		return this.numDocs;
	}
	
	/**
	 * @param collectionPath
	 * @param indexPath
	 * @throws IOException
	 */
	public void build(String collectionPath, String indexPath) throws IOException {
		this.builder.init(indexPath);
		
		// Archivo con las urls iniciales
		File file = new File(collectionPath);
		Scanner scan = new Scanner(file);
		
		// Incluimos todas en en la cola inicial (normalizadas)
		while(scan.hasNextLine()) {
			String url = scan.nextLine();
			url = normalize(url);
			if(url != null)
			this.fifo.add(url);
		}
		scan.close();
		
		//Archivo donde se almacenara el grafo
		file = new File(indexPath + "/" + Config.GRAPH_FILE);
		PrintWriter writer = new PrintWriter(file);
		
		// Indexamos hasta agotar urls o llegar al maximo
		while(!this.fifo.isEmpty() && this.numDocs < this.maxDocs) {
			index(fifo.poll(), writer);
		}
		
		// Cerramos los archivos correspondientes
		writer.close();
		this.builder.close(indexPath);
	}
	
	private String normalize(String url) {
		
		try
        {
            url = new URI(url).normalize().toString();
            System.out.println(url);
        }
        catch (URISyntaxException e) {
            System.err.println("URL descartada: " + e);
            return null;
        }

		
		return url;
	}
	
	/**
	 * @param collectionPath
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void index(String url, PrintWriter writer) throws IOException {
		
		Document doc;
		
		// Si ya ha sido indexada la ignoramos
		if(this.indexed.contains(url)) {
			return;
		}
		
		this.indexed.add(url);
		
		
		try {
			doc = Jsoup.connect(url).get();
		} catch (Exception e) {
			System.err.println("Error indexando " + url + ": " + e);
			return;
		}
		
		 Elements links = doc.select("a[href]");
		 int i=0;
		 for (Element link : links) {
			 if (i > this.maxURLS) break;
			 
			 // Obtenemos la lista de enlaces
			 String enlace = link.attr("abs:href").toString();
			 enlace = normalize(enlace);
			 
			 if(enlace != null) {
				 // Incluimos en el grafo
				 writer.println(url + "\t" + enlace);
				 
				 if(!this.indexed.contains(enlace)) {
					 this.fifo.add(enlace);
					 i++;
				 }
			 }
			 
			 
		 }
		 // Indexamos el documento
		 this.builder.indexHTML(doc.text(), url);
		 
		 // Incrementamos el numero de documentos del crawler
		 this.numDocs++;
	}

}
