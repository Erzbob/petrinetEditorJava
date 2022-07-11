package petrinet_model;

/**
 * Diese Klasse repraesentiert ein moegliches Model einer Kante, die zu einem Erreichbarkeitsgraph gehoert.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class RGArc implements IRGArc {

    private IRGNode source, target;
    private String id, label;
    
    /**
     * Erzeugen und initialisieren eines neuen RGArc-Objekts.
     * 
     * @param id
     *          die ID von dieser Kante.
     * @param label
     *          die Beschriftung dieser Kante.
     * @param source
     *          die Quelle dieser Kante.
     * @param target
     *          das Ziel dieser Kante.
     */
    public RGArc(String id, String label, IRGNode source, IRGNode target) {
        this.id = id;
        this.label = label;
        this.source = source;
        this.target = target;
    }
    
    @Override
    public String getID() {
        return id;
    }
    
    @Override
    public IRGNode getSource() {
        return source;
    }
    
    @Override
    public IRGNode getTarget() {
        return target;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
}
