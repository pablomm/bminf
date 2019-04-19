package es.uam.eps.bmi.recsys.metric;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.ranking.RankingElement;

public class Recall implements Metric {

	Ratings test;
	Double threshold;
	Integer cutoff;
	
	
	public Recall(Ratings test, double threshold, int cutoff) {
		this.test = test;
		this.threshold = threshold;
		this.cutoff = cutoff;
	}

	@Override
	public double compute(Recommendation rec) {

		double recall = 0;

		for (int user : rec.getUsers()) {
			
			// Contamos cuantos relevantes hay para el usuario (Maximo cutoff)
			int nRelevantes = 0;
			for (int item : test.getItems(user)) {
				if (test.getRating(user, item) > threshold) {
					nRelevantes++;
				}
			}
			

			if (nRelevantes > 0) {
				double devueltos = 0;
				int i = 0;
				for (RankingElement recomendacion : rec.getRecommendation(user)) {
	
					// Solo miramos las primeras recomendaciones
					if (i >= threshold)
						break;
	
					// Vemos si el usuario lo ha puntuado y es relevante para el
					Double rating = test.getRating(user, recomendacion.getID());
	
					if (rating != null) {
						if (rating >= threshold) {
							devueltos += 1;
						}
	
					}
					// Actualizacion de la recomendiacion mirada
					i++;
				}
				
				recall += devueltos / nRelevantes;
			} else {
				// Si no hay relevantes se han devuelto el 100% de ellos
				
				recall += 1;
			}
		}

		// Devolvemos la recall media
		return recall / rec.getUsers().size();
	}

	@Override
	public String toString() {
		return "Recall@" + cutoff.toString();
	}

}
