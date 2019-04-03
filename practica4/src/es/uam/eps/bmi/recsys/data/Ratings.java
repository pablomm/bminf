package es.uam.eps.bmi.recsys.data;

import java.util.Set;

/**
 *
 * @author pablo
 */
public interface Ratings {
    public void rate (int user, int item, Double rating);
    public Double getRating (int user, int item);
    public Set<Integer> getUsers(int item);
    public Set<Integer> getItems(int user);
    public Set<Integer> getUsers();
    public Set<Integer> getItems();
    public int nRatings();
    public Ratings[] randomSplit(double ratio);
}
