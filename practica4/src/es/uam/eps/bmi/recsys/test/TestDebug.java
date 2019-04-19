package es.uam.eps.bmi.recsys.test;

import java.io.FileNotFoundException;

import es.uam.eps.bmi.recsys.data.RatingsImpl;

public class TestDebug {
	 public static void main (String a[]) throws FileNotFoundException {
		 RatingsImpl ratings = new RatingsImpl("data/toy-ratings.dat", "\t");
		 
		 System.out.println(ratings.nRatings());
		 
		 
	 }
}
