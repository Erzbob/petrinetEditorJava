package petrinet_model;

import java.util.List;

/**
* Dieses Interface enthaelt die Methoden die jedes EG-Knoten-Objekt enthalten sollte. 
* 
* @author Richard Herzog, 3208133
*
*/
public interface IRGNode {
    
    /**
     * Zur Abfrage der ID von diesem Knoten.
     * 
     * @return
     *          ID von diesem Knoten.
     */
    String getID();
    
    /**
     * Gibt diesen Knoten in Form einer Markierung fuer ein Petrinetz zurueck.
     * 
     * @return
     *          Markierung fuer ein Petrinetz, die diesem Knoten entspricht.
     */
    List<Integer> getMarking();
}