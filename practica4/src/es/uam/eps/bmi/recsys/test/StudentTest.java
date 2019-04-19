package es.uam.eps.bmi.recsys.test;

import es.uam.eps.bmi.recsys.Recommendation;
import es.uam.eps.bmi.recsys.data.Ratings;
import es.uam.eps.bmi.recsys.recommender.Recommender;
import es.uam.eps.bmi.recsys.recommender.UserCenteredKNNRecommender;
import es.uam.eps.bmi.recsys.data.RatingsImpl;
import es.uam.eps.bmi.recsys.metric.Metric;
import es.uam.eps.bmi.recsys.metric.Precision;
import es.uam.eps.bmi.recsys.metric.Recall;
import es.uam.eps.bmi.recsys.metric.Rmse;
import es.uam.eps.bmi.recsys.recommender.UserKNNRecommender;
import es.uam.eps.bmi.recsys.recommender.similarity.CosineUserSimilarity;
import es.uam.eps.bmi.recsys.recommender.similarity.PearsonUserSimilarity;
import es.uam.eps.bmi.recsys.recommender.similarity.Similarity;
import es.uam.eps.bmi.recsys.util.Timer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 *
 * @author Pablo Marcos, Miguel Laseca
 */
public class StudentTest {
	public static void main(String a[]) throws FileNotFoundException {
		System.out.println("=========================");
		System.out.println("Student test");
		System.out.println("=========================");
		System.out.println("Toy test");
		toyTest("data/toy", "\t");
		System.out.println("=========================");
		System.out.println("Testing toy dataset");
		testDataset("data/toy-ratings.dat", "\t", 1, 2);
		System.out.println("=========================");
		System.out.println("Testing MovieLens \"latest-small\" dataset");
		// testDataset("data/ratings.csv", ",", 35, 1176);
	}

	static <F> void toyTest(String dataset, String separator) throws FileNotFoundException {
		Ratings train = new RatingsImpl(dataset + "-train.dat", separator);
		Ratings test = new RatingsImpl(dataset + "-test.dat", separator);
		Metric metrics[] = new Metric[] { new Rmse(test), new Precision(test, 3, 5), new Recall(test, 3, 5), };

		evaluateRecommenders(train, 5, 100, metrics);
	}

	static <F> void testDataset(String ratingsFile, String separator, int user, int item) throws FileNotFoundException {
		int n = 100;
		int cutoff = 10;
		int k = 50;
		double threshold = 3;

		Ratings ratings = new RatingsImpl(ratingsFile, separator);

		testRecommenders(ratings, k, n, 3, 4);

		Ratings folds[] = ratings.randomSplit(0.8);
		Ratings train = folds[0];
		Ratings test = folds[1];

		Metric metrics[] = new Metric[] { new Rmse(test), new Precision(test, threshold, cutoff),
				new Recall(test, threshold, cutoff), };

		evaluateRecommenders(train, k, n, metrics);
	}

	static <F> void testRecommenders(Ratings ratings, int k, int n, int nUsers, int nItems)
			throws FileNotFoundException {

		Timer.reset();
		Similarity sim = new PearsonUserSimilarity(ratings);
		testRecommender(new UserKNNRecommender(ratings, sim, k), n, nUsers, nItems);

		Timer.reset();
		testRecommender(new UserCenteredKNNRecommender(ratings, sim, k), n, nUsers, nItems); // Ahorramos un poco de
																								// tiempo reutilizando
																								// la similitud.

		Timer.reset();
		sim = new CosineUserSimilarity(ratings);
		testRecommender(new UserCenteredKNNRecommender(ratings, sim, k), n, nUsers, nItems);
		Timer.reset();

	}

	static <U extends Comparable<U>, I extends Comparable<I>, F> void evaluateRecommenders(Ratings ratings, int k,
			int n, Metric metrics[]) {

		Timer.reset();
		Similarity sim = new PearsonUserSimilarity(ratings);
		evaluateRecommender(new UserKNNRecommender(ratings, sim, k), n, metrics);
		Timer.reset();
		evaluateRecommender(new UserCenteredKNNRecommender(ratings, sim, k), n, metrics); // Ahorramos un poco de tiempo
																							// reutilizando la
																							// similitud.
		Timer.reset();
		sim = new CosineUserSimilarity(ratings);
		evaluateRecommender(new UserCenteredKNNRecommender(ratings, sim, k), n, metrics);

	}

	static <U, I extends Comparable<I>> void testRecommender(Recommender recommender, int n, int nUsers, int nItems)
			throws FileNotFoundException {
		System.out.println("-------------------------");
		System.out.println("Testing " + recommender + " recommender");
		Recommendation rec = recommender.recommend(n);
		rec.print(System.out, nUsers, nItems);

		File outFolder = new File("recommendations");
		if (!outFolder.exists())
			outFolder.mkdir();
		else
			for (File f : outFolder.listFiles())
				if (f.isFile())
					f.delete();

		rec.print(new PrintStream("recommendations/" + recommender + ".dat"));
		Timer.time("--> ");
	}

	static <U, I extends Comparable<I>> void evaluateRecommender(Recommender recommender, int n, Metric metrics[]) {
		System.out.println("-------------------------");
		System.out.println("Evaluating " + recommender + " recommender");
		Recommendation rec = recommender.recommend(n);
		for (Metric metric : metrics)
			System.out.println("   " + metric + " = " + metric.compute(rec));
		Timer.time("--> ");
	}
}
