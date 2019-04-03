package es.uam.eps.bmi.recsys;

import es.uam.eps.bmi.recsys.ranking.Ranking;
import java.io.PrintStream;
import java.util.Set;

/**
 *
 * @author pablo
 */
public interface Recommendation {
    public Set<Integer> getUsers();
    public Ranking getRecommendation(int user);
    public void add (int user, Ranking ranking);
    public void print(PrintStream out);
    public void print(PrintStream out, int userCutoff, int itemCutoff);
}
