package es.uam.eps.bmi.recsys.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class RatingsImpl implements Ratings {
	
	// Table containing the ratings made by all users
	HashMap<Integer, HashMap<Integer,Double>> ratings=
			new HashMap<Integer,HashMap<Integer,Double>> ();
	
	// Reverse table for optimization of the getUsers method
	HashMap<Integer, HashMap<Integer,Double>> inverseRatings=
			new HashMap<Integer,HashMap<Integer,Double>> ();
	
	// Number of ratings in total
	int nRatings=0;
	
	private RatingsImpl () {
		// Empty builder
	}

	public RatingsImpl(String path, String delimiter) throws FileNotFoundException {
		
		Scanner scanner = new Scanner (new File (path));
		
		while (scanner.hasNextLine()) {
			// Get the values line by line
			String[] line = scanner.nextLine().split(delimiter);
			
			// The first one is the user ID
			int user = Integer.parseInt(line[0]);
			
			// Next comes the item followed by the rating
			int item = Integer.parseInt(line[1]);
			Double rating = Double.parseDouble(line[2]);
			
			// Add the new rating
			rate(user, item, rating);
		}
		scanner.close();
	}
	
	@Override
	public void rate(int user, int item, Double rating) {
		// We search for an entry of the user, if not found we create it
		if (!ratings.containsKey(user))
			ratings.put(user, new HashMap<Integer,Double>());
		if (!inverseRatings.containsKey(item))
			inverseRatings.put(item, new HashMap<Integer,Double>());
		
		// Add the rating
		ratings.get(user).put(item, rating);
		inverseRatings.get(item).put(user, rating);
		
		nRatings++;
	}

	@Override
	public Double getRating(int user, int item) {
		return ratings.get(user).get(item);
	}

	@Override
	public Set<Integer> getUsers(int item) {
		return inverseRatings.get(item).keySet();
	}

	@Override
	public Set<Integer> getItems(int user) {
		return ratings.get(user).keySet();
	}

	@Override
	public Set<Integer> getUsers() {
		return ratings.keySet();
	}

	@Override
	public Set<Integer> getItems() {
		return inverseRatings.keySet();
	}

	@Override
	public int nRatings() {
		return nRatings;
	}

	@Override
	public Ratings[] randomSplit(double ratio) {
		// Create two empty ratings set
		Ratings[] split = { new RatingsImpl(), new RatingsImpl() };
		// Iterate through all entries
		for (int user : ratings.keySet())
			for (int item : ratings.get(user).keySet())
				// Choose a set randomly given the ratio and copy the entry there
				split[(Math.random()<ratio)? 0:1]
						.rate(user, item, ratings.get(user).get(item));
		
		return split;
	}

}
