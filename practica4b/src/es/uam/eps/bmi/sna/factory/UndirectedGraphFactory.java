package es.uam.eps.bmi.sna.factory;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import org.apache.commons.collections4.Factory;

import edu.uci.ics.jung.graph.UndirectedGraph;


class UndirectedGraphFactory implements Factory<UndirectedGraph<Integer,Integer>> {
    public UndirectedGraph<Integer,Integer> create() {
        return new UndirectedSparseGraph<Integer,Integer>();
    }
}