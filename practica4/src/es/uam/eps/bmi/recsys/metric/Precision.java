package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

/**
 * @author Pablo Marcos, Miguel Laseca
 *
 */
public class Precision implements Metric {

	Ratings test;
	Integer at;
	Double threshold;

	/**
	 * Precision
	 * 
	 * @param test    Ratings de test para comprobar la precisión
	 * @param minRate Minima puntuacion de un item relevante
	 * @param at      Corte de la recomendacion
	 */
	public Precision(Ratings test, double threshold, int at) {
		this.test = test;
		this.threshold = threshold;
		this.at = at;
	}

	@Override
	public double compute(Recommendation rec) {

		double prec = 0;

		for (int user : rec.getUsers()) {
			int i = 0;
			double prec_user = 0;
			for (RankingElement recomendacion : rec.getRecommendation(user)) {

				// Solo miramos las primeras recomendaciones
				if (i >= at)
					break;

				// Vemos si el usuario lo ha puntuado y es relevante para el
				Double rating = test.getRating(user, recomendacion.getID());

				if (rating != null) {
					if (rating >= threshold) {
						prec_user += 1;
					}

				}
				// Actualizacion de la recomendiacion mirada
				i++;
			}
			
			if (i > 0) {
				prec += prec_user / i;
			}
			
			
		}

		// Devolvemos la precision media
		return prec / (rec.getUsers().size());
	}

	@Override
	public String toString() {
		return "Precision@" + at.toString();
	}

}
