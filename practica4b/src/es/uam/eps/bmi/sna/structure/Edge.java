package es.uam.eps.bmi.sna.structure;

/**
 *
 * @author pablo
 */
public class Edge<U extends Comparable<U>> implements Comparable<Edge<U>>{
    U first, second;
    public Edge (U u, U v) {
        first = u;
        second = v;
    }

    public U getFirst() {
        return first;
    }
    
    public U getSecond() {
        return second;
    }
    
    // Definimos como orden natural de los arcos ordenar por el primer usuario, y si es el mismo, por el segundo.
    public int compareTo(Edge<U> e) {
        int f = first.compareTo(e.first);
        return f == 0? second.compareTo(e.second) : f;
    }

    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
