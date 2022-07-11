package petrinet_model;

import java.util.List;

/**
 * Diese Klasse repraesentiert ein moegliches Model eines Knotens, der zu einem Erreichbarkeitsgraph gehoert.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class RGNode implements IRGNode {

    private String id;
    private List<Integer> marking; // die Markierung die diesem Knoten entspricht.
    
    /**
     * Erzeugen und initialisieren eines neuen RGNode-Objekts. Die als formaler Parameter uebergebene Markierung 
     * wird (als String interpretiert) zur ID des RGNode-Objekts, da jede Markierung im Petrinetz eindeutig ist.
     * 
     * @param marking
     *          eine Markierung aus dem Petrinetz zu dem der pEG gehoert, zu dem dieser Knoten gehoert.
     */
    public RGNode(List<Integer> marking) {
        this.id = marking.toString();
        this.marking = marking;
    }
    
    @Override
    public String getID() {
        return id;
    }
    
    @Override
    public List<Integer> getMarking() {
        return marking;
    }
}