package es.uam.eps.bmi.recsys.recommender;

import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;

/**
 * @author Miguel Laseca y Pablo Marcos
 *
 */
public class NormUserKNNRecommender extends UserKNNRecommender {

	/**
	 * Minimo numero de vecinos para recomendar
	 */
	private int min;

	/**
	 * @param ratings Estructura con ratings
	 * @param sim     Similitud
	 * @param k       Numero de vecinos
	 * @param min     Minimo numero de vecinos para recomendar
	 */
	public NormUserKNNRecommender(Ratings ratings, Similarity sim, int k, int min) {

		super(ratings, sim, k);
		this.min = min;

	}

	@Override
	public double score(int user, int item) {

		double score = 0;
		double normalizacion = 0;
		int n_vecinos = 0;

		// Iteramos sobre la vecindad
		for (RankingElement rank : neighborhood.get(user)) {

			// Obtenemos el rating del vecino
			Double rating = ratings.getRating(rank.getID(), item);

			if (rating != null) {
				Double similitud = rank.getScore();
				n_vecinos++;
				normalizacion += similitud;

				// score += r(u,i)*sim(u,v)
				score += similitud * rating;
			}
		}

		// Devolvemos puntuacion positiva solo si hay un minimo de vecinos que
		// recomendar
		if (n_vecinos >= this.min) {

			// Normalizamos la puntuacion
			score /= normalizacion;

			return score;
		}

		// No recomendamos cosas sin datos
		return Double.NEGATIVE_INFINITY;


	}

	@Override
	public String toString() {
		return "normalized " + super.toString();
	}
}
