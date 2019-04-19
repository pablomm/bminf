package es.uam.eps.bmi.recsys.recommender.similarity;

import java.util.Set;

import es.uam.eps.bmi.recsys.data.Features;

/**
 * @author Pablo Marcos, Miguel Laseca
 *
 * @param <F> Tipo de feature
 */
public class JaccardFeatureSimilarity<F>  extends FeatureSimilarity<F> {

	/**
	 * Distancia Jaccard entre Features
	 * @param features 
	 */
	public JaccardFeatureSimilarity(Features<F> features) {
		super(features);
		
	}

	@Override
	public double sim(int x, int y) {
		
		// Obtenemos las features de x y de y
		Set<F> xF = this.xFeatures.getFeatures(x);
		Set<F> yF = this.yFeatures.getFeatures(y);
		
		// Jacard(X,Y) = |X^Y|/|XUY|=|X^Y|/(|X| + |Y| - |X^Y|)
		
		// Calculamos |X^Y|
		int intersection_mod = 0;
		for(F feature : xF) {
			if (yF.contains(feature))
				intersection_mod++;
		}
		
		if (intersection_mod == 0) {
			return 0;
		}
		
		return intersection_mod / (xF.size() + yF.size() - intersection_mod);
	}
	
	@Override
	public String toString() {
		return "Jaccard on item features";
	}

}
