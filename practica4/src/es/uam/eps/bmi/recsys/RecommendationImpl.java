package es.uam.eps.bmi.recsys;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.uam.eps.bmi.recsys.ranking.Ranking;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

/**
 * @author Miguel Laseca, Pablo Marcos
 *
 */
public class RecommendationImpl implements Recommendation{

	// Hash map con rakings de recomendaciones indexados por ids
	Map<Integer,Ranking> recommendations = new HashMap<Integer,Ranking>();
	
	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.Recommendation#getUsers()
	 */
	@Override
	public Set<Integer> getUsers() {
		return recommendations.keySet();
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.Recommendation#getRecommendation(int)
	 */
	@Override
	public Ranking getRecommendation(int user) {
		return recommendations.get(user);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.Recommendation#add(int, es.uam.eps.bmi.recsys.ranking.Ranking)
	 */
	@Override
	public void add(int user, Ranking ranking) {
		recommendations.put(user, ranking);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.Recommendation#print(java.io.PrintStream)
	 */
	@Override
	public void print(PrintStream out) {
		this.print(out, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/* (non-Javadoc)
	 * @see es.uam.eps.bmi.recsys.Recommendation#print(java.io.PrintStream, int, int)
	 */
	@Override
	public void print(PrintStream out, int userCutoff, int itemCutoff) {
				
		int i=0;
		for (int user : recommendations.keySet()) {
			
			if (i>=userCutoff) break;
			int j=0;
			
			// Imprime los elementos del ranking
			for (RankingElement item : recommendations.get(user)) {
				if (j>=itemCutoff) break;
				
				out.println(user + "\t" + item.getID() + "\t" + item.getScore());
				j++; // Contador del itemCutoff
			}
			
			i++; // Contador del userCutoff
		}
	}

}
