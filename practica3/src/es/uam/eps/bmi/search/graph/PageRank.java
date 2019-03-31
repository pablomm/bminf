package es.uam.eps.bmi.search.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import es.uam.eps.bmi.search.index.DocumentFeatureMap;

public class PageRank implements DocumentFeatureMap {
	
	/**
	 * 
	 */
	private String path;
	
	/**
	 * 
	 */
	private double r;
	
	/**
	 * 
	 */
	private int iterations;
	
	/**
	 * 
	 */
	private int nDocs = 0;
	
	/**
	 * 
	 */
	private Map<String, Integer> identificadores= new HashMap<String, Integer>();
	
	/**
	 * 
	 */
	private Map<Integer, String> docPaths= new HashMap<Integer, String>();
	
	/**
	 * 
	 */
	private Map<Integer, List<Integer>> links= new HashMap<Integer, List<Integer>>();
	
	
	/**
	 * 
	 */
	private Double[] values;
	
	/**
	 * @param path
	 * @param r
	 * @param iterations
	 * @throws FileNotFoundException
	 */
	public PageRank(String path, double r, int iterations) throws FileNotFoundException {
		this.path = path;
		this.iterations = iterations;
		this.r = r;
		

		readGraph();
		pageRank();

	}
	
	private void pageRank() {
		
		// Juega el valor de P en el algoritmo
		this.values = new Double[this.nDocs];
		Arrays.fill(this.values, 1./this.nDocs);
		
		// Por rendimiento usamos un array en lugar de acceder al hashmap continuamente
		int[] out = new int[this.nDocs];
		
		for(int i=0; i<this.nDocs; i++) {
			out[i] = this.links.get(i).size();
		}
		
		// P'
		Double[] aux_values = new Double[this.nDocs];
		
		// Iteracion principal
		for(int iter=0; iter<this.iterations; iter++) {
			
			//P'[i]=r/N
			Arrays.fill(aux_values, this.r/this.nDocs);
			
			// Iteramos sobre todos los nodos
			for(int i=0; i<this.nDocs; i++) {
				
				double weight=0;
				
				// Peso que le corresponde a cada enlace saliente
				if(out[i] != 0) {
					weight = this.values[i] * (1 - r) /out[i];
				}
				
				// Repartimos peso entre los enlaces saliente
				for(int j : this.links.get(i)) {
					aux_values[j] += weight;
				}	
			}
			
			// Sumamos constante para arreglar sumideros
			double sink =0;
			
			for(double p : aux_values) {
				sink += p;
			}
			
			sink = (1 - sink) / this.nDocs;
			
			// Ajustamos peso de sumidero
			for(int i=0; i<this.nDocs; i++) {
				this.values[i] = aux_values[i] + sink;
			}	
		}
	}
	
	private int createOrGetNode(String node) {
		
		Integer nodeId = identificadores.get(node);
		
		// Construimos el nodo si no ha sido creado ya
		if(nodeId == null) {
			nodeId = this.nDocs;
			identificadores.put(node, nodeId);
			docPaths.put(nodeId, node);
			links.put(nodeId, new ArrayList<Integer>());
			this.nDocs++;
		}
		return nodeId;
	}
	
	/**
	 * Lee el grafo
	 * @throws FileNotFoundException 
	 */
	private void readGraph() throws FileNotFoundException {
		
		File file = new File(this.path);
		Scanner scanner = new Scanner(file);
		
		while(scanner.hasNextLine()) {
			String line[] = scanner.nextLine().split("\t");
			
			// Enlace a -> b
			int a = createOrGetNode(line[0]);
			int b = createOrGetNode(line[1]);
			this.links.get(a).add(b);
		}
		
		scanner.close();
		
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.search.index.DocumentMap#getDocPath(int)
	 */
	@Override
	public String getDocPath(int docID) throws IOException {
		return this.docPaths.get(docID);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.search.index.DocumentMap#getDocNorm(int)
	 */
	@Override
	public double getDocNorm(int docID) throws IOException {
		// Devolvemos un 1 para no afectar a los motores de busqueda
		return 1;
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.search.index.DocumentMap#numDocs()
	 */
	@Override
	public int numDocs() {
		return this.nDocs;
	}

	/**
	 * @param docId
	 * @return
	 */
	@Override
	public double getValue(int docId) {

		return this.values[docId];
	}



}
