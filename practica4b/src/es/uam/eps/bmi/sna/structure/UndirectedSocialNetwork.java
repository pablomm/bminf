package es.uam.eps.bmi.sna.structure;

import java.util.Set;

/**
 *
 * @author pablo
 */
public interface UndirectedSocialNetwork<U> {
    public Set<U> getUsers();
    public Set<U> getContacts(U user);
    public void addContact(U u, U v);
    public boolean connected(U u, U v);
    public int nEdges();
}
