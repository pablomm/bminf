package es.uam.eps.bmi.recsys.recommender;

import java.util.HashMap;
import java.util.Map.Entry;

import es.uam.eps.bmi.recsys.data.Features;
import es.uam.eps.bmi.recsys.data.FeaturesImpl;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.similarity.FeatureSimilarity;

public class CentroidRecommender<F> extends AbstractRecommender {
	
	
	/**
	 * 
	 */
	private FeatureSimilarity<F> sim;

	
	public CentroidRecommender (Ratings rat, FeatureSimilarity<F> s) {
		// Initialize the recommender
		super(rat);

		// Almacenamos la similitud de Features
		sim=s;
		
		// Obtenemos todas las features por item
		Features<F> yFeatures = s.getFeatures();

		// Centroides de usuario
		Features<F> xFeatures = new FeaturesImpl<F>();
		
		// Mapa con features del usuario
		HashMap<F, Double> featMap = new HashMap<F, Double>();
		
		// Iteramos sobre todos los usuarios, cuyos centroides construiremos
		for (int user : this.ratings.getUsers()) {
			
			
			// Iteramos sobre todos los items que ha puntuado
			for (int item : this.ratings.getItems(user)) {
				// Iteramos sobre las features de cada item y sumamos puntuaciones
				for(F f : yFeatures.getFeatures(item)) {
					Double score = featMap.getOrDefault(f, 0.0);
					featMap.put(f, score + yFeatures.getFeature(item, f));
				}
			}
			
			// Calculamos el modulo del centroide, aunque 
			// con el coseno no es necesario normalizar, con 
			// otras similitudes si
			double mod = 0;
			for(Double score : featMap.values()) {
				mod += score * score;
			}
			mod = Math.sqrt(mod);
			
			// Incluimos el centroide de usuario
			for(Entry<F, Double> entry : featMap.entrySet()) {
				xFeatures.setFeature(user, entry.getKey(), entry.getValue() / mod);
			}
			
			featMap.clear(); // Limpiamos para el siguiente usuario
		}
		
		sim.setXFeatures(xFeatures);
	}
	
	@Override
	public double score(int user, int item) {
		return sim.sim(user, item);
	}
	
	@Override
	public String toString() {
		return "centroid-based (" + sim.toString() + ")";
	}
}
