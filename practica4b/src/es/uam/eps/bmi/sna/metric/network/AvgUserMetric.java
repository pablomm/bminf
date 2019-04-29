package es.uam.eps.bmi.sna.metric.network;

import es.uam.eps.bmi.sna.metric.GlobalMetric;
import es.uam.eps.bmi.sna.metric.LocalMetric;
import es.uam.eps.bmi.sna.ranking.Ranking;
import es.uam.eps.bmi.sna.ranking.RankingElement;
import es.uam.eps.bmi.sna.structure.UndirectedSocialNetwork;

/**
 * @author Pablo Marcos
 *
 * @param <U> Tipo de usuario
 */
public class AvgUserMetric<U extends Comparable<U>> implements GlobalMetric<U> {

	LocalMetric<U, U> metric;
	
	/**
	 * @param metric Metrica sobre la que calcular la media
	 */
	public AvgUserMetric(LocalMetric<U, U> metric) {
		this.metric = metric;
	}

	@Override
	public double compute(UndirectedSocialNetwork<U> network) {
		
		Ranking<U> ranking = metric.compute(network);
		
		double avg = 0;
		for(RankingElement<U>  element : ranking) {
			avg += element.getScore();
		}
		
		avg /= ranking.size();
		
		return avg;
	}
	
	@Override
	public String toString() {
		return "Avg(" + this.metric.toString() + ")";
	}

}
