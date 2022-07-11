package petrinet_model;

import java.util.List;

/**
 * Dieses Interface enthaelt die Methoden die jedes Petrinetz-Objekt enthalten sollte
 * 
 * @author Richard Herzog, 3208133
 *
 */
public interface IPetrinet {

    /**
     * Fuegt diesem Petrinetz eine Transition hinzu.
     * Wird beim Erzeugen des Petrinetzes in der Klasse ExtendedPNMLWopedParser verwendet.
     * @param transition
     *          Transition die zum Petrinetz hinzugefuegt wird.
     */
    void addTransition(ITransition transition);

    /**
     * Fuegt diesem Petrinetz eine Stelle hinzu.
     * Wird beim Erzeugen des Petrinetzes in der Klasse ExtendedPNMLWopedParser verwendet.
     * @param place
     *          Stelle die zum Petrinetz hinzugefuegt wird.
     */
    void addPlace(IPlace place);

    /**
     * Fuegt diesem Petrinetz eine Kante hinzu.
     * Wird beim Erzeugen des Petrinetzes in der Klasse ExtendedPNMLWopedParser verwendet.
     * @param petrinetArc
     *          Kante die zum Petrinetz hinzugefuegt wird.
     */
    void addArc(IPArc petrinetArc);

    /**
     * Diese Methode liefert den aktuellen Erreichbarkeitsgraph fuer dieses Petrinetz.
     * 
     * @return
     *          Den aktuellen pEG zu diesem Petrinetz.
     */
    IReachabilityGraph getReachabilityGraphModel();

    /**
     * Diese Methode liefert die Transitionen von diesem Petrinetz.
     * 
     * @return
     *          Die Transitionen von diesem Petrinetz.
     */
    List<ITransition> getTransitions();

    /**
     * Diese Methode liefert die Stellen von diesem Petrinetz.
     * 
     * @return
     *          Die Stellen von diesem Petrinetz.
     */
    List<IPlace> getPlaces();

    /**
     * Diese Methode liefert die Kanten von diesem Petrinetz.
     * 
     * @return
     *          Die Kanten von diesem Petrinetz.
     */
    List<IPArc> getArcs();

    /**
     * Diese Methode initialisiert das Petrinetz.
     */  
    void initializePetrinet();
    
    /**
     * Diese Methode uebergibt dem Petrinetz einen Namen, sofern es noch keinen hat.
     * 
     * @param name
     *          der Name von diesem Petrinetz
     */
    void setName(String name);

}