package petrinet_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repraesentiert ein moegliches Model des Erreichbarkeitsgraphen fuer ein Petrinetz. <br><br>
 * 
 * Ein Model_ReachabilityGraph-Objekt kennt seine Knoten sowie seine Kanten. <br><br>
 * 
 * ueber ein Model_ReachabilityGraph-Objekt kann auf die Knoten und Kanten die zu ihm gehoeren, 
 * zugegriffen werden, sodass keine direkten Referenzen auf Knoten- oder Kanten-Objekte, 
 * ausserhalb des Model_ReachabilityGraph-Objekts noetig sind.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class Model_ReachabilityGraph implements IReachabilityGraph {

    private IRGNode initialNode;
    private List<IRGNode> nodes = new ArrayList<IRGNode>(); // alle Knoten von diesem pEG
    private List<IRGArc> arcs = new ArrayList<IRGArc>(); // alle Kante von diesem pEG
    private List<IRGArc> unlimitedPathsRGArcs; // alle Kanten die zu einem unbeschraenkten Pfad von diesem pEG gehoeren
    private int numberOfArcs; // Anzahl der Kanten von diesem pEG
    private int numberOfNodes; // Anzahl der Knoten von diesem pEG

    @Override
    public void addNode(IRGNode node) {
        // Initialisieren der Liste
        if (nodes == null) {
            nodes = new ArrayList<IRGNode>();
        }
        nodes.add(node);
        numberOfNodes++;
    }

    @Override
    public void addArc(IRGArc arc) {
        // Initialisieren der Liste
        if(arcs == null) {
            arcs = new ArrayList<IRGArc>();
        }
        arcs.add(arc);
        numberOfArcs++;
    }
    
    @Override
    public boolean arcExists(IRGNode source, IRGNode target) {
        if(arcs == null) {
            return false;
        } else {
            for(IRGArc arc : arcs) {
                if(arc.getSource() == source && arc.getTarget() == target) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public int getNumberOfNodes() {
        return this.numberOfNodes;
    }
    
    @Override
    public int getNumberOfArcs() {
        return this.numberOfArcs;
    }
    
    @Override
    public IRGNode getNodeByMarking(List<Integer> marken) {
        for(IRGNode node : nodes) {
            if(node.getMarking().equals(marken)) {
                return node;
            }
        }
        return null;
    }
    
    @Override
    public IRGNode getNodeByID(String id) {
        for(IRGNode node : nodes) {
            if(node.getID().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public IRGNode getInitialNode() {
        return initialNode;
    }
    
    @Override
    public List<IRGNode> getNodes() {
        return nodes;
    }
    
    @Override
    public List<IRGArc> getArcs() {
        return arcs;
    }
    
    @Override
    public int getNextFreeArcIndex() {
        return numberOfArcs;
    }
    
    @Override
    public String getAnArcByLabel(String label) {
        for(IRGArc arc : arcs) {
            if(arc.getLabel().equals(label)) {
                return arc.getLabel();
            }
        }
        return null;
    }
    
    @Override
    public void setInitailNode(IRGNode initialNode) {
        this.initialNode = initialNode;
    }

    /**
     * Diese Methode fuegt die Kanten, die zu dem unbeschraenkten Teil des als Parameter 
     * uebergebenen Pfad gehoeren, zu der dafuer vorgesehenen Menge hinzu.
     * 
     * @param path
     *          der Pfad der einen unbeschraenkten Teil enthaelt.
     */
    public void setUnlimitedPathsRGArcsByGivenPath(List<List<Integer>> path) {
        // die Knoten des Pfads werden in einer Liste gesammelt
        List<IRGNode> pathsNodes = new ArrayList<IRGNode>();
        for(List<Integer> intList : path) {
            pathsNodes.add(getNodeByMarking(intList));
        }
        // alle Kanten zu den Knoten des Pfades werden gesucht und in einer List gesammelt.
        for(IRGArc arc : arcs) {
            IRGNode source = null;
            IRGNode target = null;
            for(IRGNode node : pathsNodes) {
                if(node == arc.getSource()) {
                    source = node;
                } else if(node == arc.getTarget()) {
                    target = node;
                }
            }
            // eine Kante kann nur dann zum Pfad gehoeren, wenn ihre Quelle und ihr Ziel zum Pfad gehoeren.
            if(source != null && target != null) {
                // nun wird ueberprueft ob ein Knoten mehr als einmal die Quelle einer Kante im Pfad darstellt.
                // Falls ja, wird die Kante die bereits zuvor gefunden wurde und deren Quelle ebenfalls 
                // dieser Knoten ist, wieder aus der dafuer vorgesehenen Menge geloescht, da es sich naemlich 
                // nur um eine Kante handeln kann die zur Bildung eines Kreises im Pfad gefuehrt hat.
                // Ein Kreis im Pfad gehoert aber nicht zum unbeschraenkten Teil des Pfades.
                IRGArc arcOfACircle = null;
                if(unlimitedPathsRGArcs != null) {
                    for(IRGArc previouslyFoundArc : unlimitedPathsRGArcs) {
                        if(previouslyFoundArc.getSource() == source) {
                            arcOfACircle = previouslyFoundArc;
                        }
                    }
                    if(arcOfACircle != null) {
                        unlimitedPathsRGArcs.remove(arcOfACircle);
                    }
                }
                // zu guter Letzt wird die Kante zur Menge der Kanten des unbeschraenkten Pfades hinzugefuegt
                if(unlimitedPathsRGArcs == null) {
                    unlimitedPathsRGArcs = new ArrayList<IRGArc>();
                }
                this.unlimitedPathsRGArcs.add(arc);
            }
        }
    }
    
    /**
     * Liefert die Menge der Kanten die zu einem unbeschraenten Pfad von diesem pEG gehoeren.
     * 
     * @return
     *          Kanten eines unbeschraenkten Pfades von diesem pEG.
     */
    public List<IRGArc> getUnlimitedPathsRGArcs() {
        return unlimitedPathsRGArcs;
    }
}    