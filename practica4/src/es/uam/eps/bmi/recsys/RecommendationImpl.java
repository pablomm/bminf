package es.uam.eps.bmi.recsys;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class RecommendationImpl implements Recommendation{

	// Sets are user/Ranking
	HashMap<Integer,Ranking> recommendations=new HashMap<Integer,Ranking>();
	
	public RecommendationImpl() {
		
	}
	
	@Override
	public Set<Integer> getUsers() {
		return recommendations.keySet();
	}

	@Override
	public Ranking getRecommendation(int user) {
		// Nfi yet
		return recommendations.get(user);
	}

	@Override
	public void add(int user, Ranking ranking) {
		recommendations.put(user, ranking);
	}

	@Override
	public void print(PrintStream out) {
		// Print all scores of all users
		for (int user : recommendations.keySet())
			for (RankingElement item : recommendations.get(user))
				out.println(user + "\t" + item.getID() + "\t" + item.getScore());
	}

	@Override
	public void print(PrintStream out, int userCutoff, int itemCutoff) {
		// i and j will check the cutoffs
		int i=0;
		for (int user : recommendations.keySet()) {
			if (i>=userCutoff) break;
			int j=0;
			// Print the n-first rankings of each user 
			for (RankingElement item : recommendations.get(user)) {
				if (j>=itemCutoff) break;
				out.println(user + "\t" + item.getID() + "\t" + item.getScore());
				j++;
			}
			i++;
		}
	}

}
