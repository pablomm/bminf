package es.uam.eps.bmi.search.proximity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.uam.eps.bmi.search.AbstractEngine;
import es.uam.eps.bmi.search.SearchEngine;
import es.uam.eps.bmi.search.index.AbstractIndex;
import es.uam.eps.bmi.search.index.DocumentMap;
import es.uam.eps.bmi.search.index.lucene.LucenePositionalIndex;
import es.uam.eps.bmi.search.ranking.SearchRanking;
import es.uam.eps.bmi.search.ranking.impl.RankingImpl;

public class ProximityEngine extends AbstractEngine {
	
	public ProximityEngine(AbstractIndex index) {
		super(index);
	}
	
	/*public ProximityEngine(PositionalIndex index) {
		this.index=index;
	}*/

	@Override
	public SearchRanking search(String query, int cutoff) throws IOException {
		RankingImpl ranking = new RankingImpl(index, cutoff);
		double score=0.0;
        for (int doc = 0; doc < index.numDocs(); doc++) {
            //ranking.add(doc, index.getValue(doc));
        	if (query.charAt(0)=='"' && query.charAt(query.length()-1)=='"')
        		score=M_d_q_literal(doc, query.replaceAll("\"", ""));
        	else score=M_d_q(doc, query.split(" "));
        	
        	if (score>0.0) ranking.add(doc, score);
        }
        return ranking;
	}
	
	
	public double M_d_q (int doc, String[] query) throws IOException {
		
		// The terms in the query must be unique
		String path = index.getDocPath(doc);
		int position=0;
		double score=0;
		
		// Queue for the terms we'll be inserting in order to calculate the scores
		int[] positions = new int[query.length];
		Arrays.fill(positions, -1);
		
		// Create a stream with the entire file
		byte[] stream = Files.readAllBytes(Paths.get(path));
		
		// We transform the input in a list with all the words, spliting by punctuation marks and whitespaces
		ArrayList<String> str = new ArrayList<String>(Arrays
				.asList((new String(stream, Charset.defaultCharset()))
				.replaceAll("\\s+(?=\\p{Punct})", "").toLowerCase().split("[\\p{Punct}\\s]+")) );
		
		
		// Search all next appearance of all the terms in the query and keep trace of their positions
		while (position < str.size()) {
			// The indexes of the first, second and last terms
			int first=-1, second=-1, last=0;
			
			int i=0;
			// We iterate through all elements until we get an occurrence
			for (i=0; i<query.length; i++) {
				if (positions[i]==-1) 
					positions[i]=search(query[i], position, str);
					
				// Check if we didn't find the term
				if (positions[i]==-1) break;
					
				// We keep track of the first, second and last element found
				if (first==-1 || positions[i]<positions[first]) {
					second=first;
					first=i;
				}
				
				else if (second==-1 || positions[i]<positions[second]) second=i;
				
				if (positions[i]>positions[last]) last=i;
			}
			
			// Check if we found or not an occurrence
			if (i<query.length) break;
			
			int position_aux;
			// We search for optimal solutions, which means closer entries of every term to the last one
			while ((position_aux=search(query[first], positions[first]+1, str))<positions[last]) {
				// If there are no more occurrences we just break the loop
				if (position_aux==-1) break;
				// Check if we went too far and position_aux makes a larger distance with the second position
				if (position_aux>positions[last] && position_aux-positions[second]<positions[last]-positions[first]) break;
				
				// Reassign the respective position, we later reuse position_aux by storing there the new position in case it's going to be the last one
				positions[first]=position_aux;
				position_aux=first;
				
				// Reassign the first and second positions
				for (i=0; i<query.length; i++)
					if (positions[i]<positions[first]) {
						second=first;
						first=i;
					}
					else if (i!=first && positions[i]<positions[second])second=i;
				
				if (positions[position_aux]>positions[last]) last=position_aux;
			}
			score+=1.0/(positions[last]-positions[first]-Math.sqrt(query.length)+2);
			positions[first]=-1;
			first=second;
			position=positions[first];
			second=-1;
			// Since me moved the second position to the first place, we ought reassign it
			/*for (i=0; i<query.length; i++)
				if (positions[i]!=-1 && (second==-1 || (i!=first && positions[i]<positions[second])))second=i;*/
		}
		return score;
	}
	
	public double M_d_q_literal (int doc, String query) throws IOException {
		
		String path = index.getDocPath(doc);
		double score=0;
		
		// Create a stream with the entire file
		byte[] stream = Files.readAllBytes(Paths.get(path));
		
		// We transform the input in a list with all the words, spliting by punctuation marks
		String str = (new String(stream, Charset.defaultCharset())).replaceAll("\\s+(?=\\p{Punct})", "");
		
		int last = 0;

		while((last = str.indexOf(query,last)) != -1){
	        last += query.length();
	        score+=1.0/query.length();
		}
		
		return score;
	}
	
	private int search (String term, int start, List<String> stream) {
		for (int i=start; i < stream.size(); i++)
			if (stream.get(i).equals(term)) return i;
		
		return -1;
	}

	/*@Override
	public DocumentMap getDocindex() {
		return index;
	}*/
}
