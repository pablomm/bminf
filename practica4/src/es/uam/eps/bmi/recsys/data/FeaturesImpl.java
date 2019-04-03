package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FeaturesImpl<T> implements Features<T> {
	
	HashMap<Integer, HashMap<T,Double>> values=null;
	
	public FeaturesImpl(String path, String delimiter, Parser<T> parser) throws FileNotFoundException {
		
		// Initialize the elements
		values=new HashMap<Integer,HashMap<T,Double>> ();
		Scanner scanner = new Scanner (new File (path));
		
		while (scanner.hasNextLine()) {
			// Get the values line by line
			String[] line = scanner.nextLine().split(delimiter);
			
			// The first one is the item
			int item = Integer.parseInt(line[0]);
			
			// Next comes the feature followed by the value
			T feature = parser.parse(line[1]);
			Double value = Double.parseDouble(line[2]);
			
			// We search for an entry of the item, if not found we create it
			if (!values.containsKey(item))
				values.put(item, new HashMap<T,Double>());
			
			// Add the feature
			values.get(item).put(feature,value);
		}
		scanner.close();
	}

	@Override
	public Set<T> getFeatures(int id) {
		return values.get(id).keySet();
	}

	@Override
	public Double getFeature(int id, T feature) {
		return values.get(id).get(feature);
	}

	@Override
	public void setFeature(int id, T feature, double value) {
		if (!values.containsKey(id))
			values.put(id, new HashMap<T,Double>());
		
		// Add the feature
		values.get(id).put(feature,value);
	}

	@Override
	public Set<Integer> getIDs() {
		return values.keySet();
	}

}
