package petrinet_model;

import java.util.List;

/**
 * Dieses Interface enthaelt die Methoden die jedes EG-Objekt enthalten sollte. 
 * 
 * @author Richard Herzog, 3208133
 *
 */
public interface IReachabilityGraph {

    /**
     * Fuegt den als Parameter uebergebenen Knoten zu diesem pEG hinzu.
     * 
     * @param node
     *          Knoten der zu diesem pEG hinzugefuegt wird.
     */
    void addNode(IRGNode node);

    /**
     * Fuegt die als Parameter uebergebene Kante zu diesem pEG hinzu.
     * 
     * @param arc
     *          Kante die zu diesem pEG hinzugefuegt wird.
     */
    void addArc(IRGArc arc);

    /**
     * Testet ob ein Kante, zwischen den als Parameter uebergebenen Knoten, in diesem pEG existiert.
     * 
     * @param source
     *          die Quelle der Kante.
     * @param target
     *          das Ziel der Kante.
     * @return
     *          true falls die Kante existiert, false sonst.
     */
    boolean arcExists(IRGNode source, IRGNode target);
    
    /**
     * Zur Abfrage des Knotens, der der uebergebenen Markierung entspricht.
     * 
     * @param marking
     *          eine Markierung aus dem Petrinetz.
     * @return
     *          den Knoten der der Markierung entspricht.
     */
    IRGNode getNodeByMarking(List<Integer> marking);

    /**
     * Zur Abfrage des Knotens, zu dem die uebergebene ID gehoert.
     * 
     * @param id
     *          die ID eines Knotens.
     * @return
     *          den Knoten mit der uebergebenen ID.
     */
    IRGNode getNodeByID(String id);
    
    /**
     * Zur Abfrage der ersten Kante in der Menge der Kanten, zu dem die uebergebene Beschriftung passt. 
     * 
     * @param label
     *          die Beschriftung einer Kante.
     * @return
     *          die erste Kante mit der uebergebenen Beschriftung.
     */
    String getAnArcByLabel(String label);

    /**
     * Zur Abfrage des Anfangsknotens von diesem pEG.
     * 
     * @return
     *          den Anfangsknoten von diesem pEG.
     */
    IRGNode getInitialNode();

    /**
     * Gibt die Menge der Knoten von diesem pEG zurueck.
     * 
     * @return
     *          alle Knoten von diesem pEG.
     */
    List<IRGNode> getNodes();

    /**
     * Gibt die Menge der Kanten von diesem pEG zurueck.
     * 
     * @return
     *          alle Kanten von diesem pEG.
     */
    List<IRGArc> getArcs();

    /**
     * Liefert die naechste freie Position fuer eine Kante in diesem pEG.
     * 
     * @return
     *          naechste freie Position fuer eine Kante in diesem pEG.
     */
    int getNextFreeArcIndex();

    /**
     * zur Abfrage der Anzahl aller Knoten in diesem pEG.
     * 
     * @return
     *          Anzahl aller Knoten in diesem pEG.
     */
    int getNumberOfNodes();
    
    /**
     * zur Abfrage der Anzahl aller Kanten in diesem pEG.
     * 
     * @return
     *          Anzahl aller Kanten in diesem pEG.
     */
    int getNumberOfArcs();

    /**
     * Legt den als Parameter uebergebenen Knoten als neuen Anfangsknoten in diesem pEG fest.
     * 
     * @param initialNode
     *          der neue Anfangsknoten von diesem pEG.
     */
    void setInitailNode(IRGNode initialNode);
}