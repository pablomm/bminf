package es.uam.eps.bmi.sna.test;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections15.Factory;

public class Test2 {
	public static void main (String a[]) {
		
		int vertices = 300;
		int nEdges = 12;
		Set<Integer> seedV = new HashSet<Integer>();
	
		// Factories for the graph and the values for both the nodes and the edges connecting them
		UndirectedGraphFactory factory = new UndirectedGraphFactory();
		SimpleIntFactory vertFact = new SimpleIntFactory();
		SimpleIntFactory edgesFact = new SimpleIntFactory();
		
		seedV.add(4);
		seedV.add(19);
		seedV.add(30);
		seedV.add(102);
		seedV.add(206);
		seedV.add(214);
		
		//BarabasiAlbertGenerator<Integer,Integer> BAGen = new BarabasiAlbertGenerator<Integer,Integer>(factory, vertFact, edgesFact, vertices, nEdges, seedV);
		
		/*Graph<Integer,Integer> graph = BAGen.create();
		System.out.print(graph);
		System.out.println();
		BAGen.evolveGraph(4);
		System.out.print(graph);
		System.out.println();*/
		
		ErdosRenyiGenerator<Integer,Integer> ERGen = new ErdosRenyiGenerator<Integer,Integer>(factory, vertFact, edgesFact, vertices, .4);
		Graph<Integer,Integer> graph = ERGen.create();
		System.out.print(graph);
		System.out.println();
		/*ERGen.evolveGraph(4);
		System.out.print(graph);
		System.out.println();*/
	}
	
	static class UndirectedGraphFactory implements Factory<UndirectedGraph<Integer,Integer>> {
	    public UndirectedGraph<Integer,Integer> create() {
	        return new UndirectedSparseGraph<Integer,Integer>();
	    }
	}
	
	static class SimpleIntFactory implements Factory<Integer> {
		
		int count=-1;

		@Override
		public Integer create() {
			count++;
			return count;
		}
	}
}
