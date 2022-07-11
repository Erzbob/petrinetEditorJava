package petrinet_model;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse bildet das Model des Petrinetzes. Es fungiert als Schnittstelle die mit
 * Controller und Praesentation kommuniziert. ueber ein Objekt von dieser Klasse hat man Zugriff auf alle
 * Elemente, die in direkter Verbindung zu diesem Petrinetz stehen. Also alle Markierungen,
 * Transitonen, Kanten, Stellen, inklusive pEG und allen seinen Elementen. 
 * 
 * @author Richard Herzog, 3208133
 * 
 */
public class Model_Petrinet implements IPetrinet {

    private IReachabilityGraph reachabilityGraph; // der pEG der zu diesem Petrinetz gehoert
    private List<List<Integer>> markings = new ArrayList<List<Integer>>(); // alle Markierunge
    private List<ITransition> transitions = new ArrayList<ITransition>(); // alle Transitionen
    private List<IPlace> places; // alle Stellen
    private List<IPArc> arcs; // alle Kanten
    private List<Integer> initialMarking; // die Anfangsmarkierung
    private List<Integer> markingBeforeSwitching; // wenn eine Transiton geschaltet wird, ist das die vorherige Markierung der Folgemarkierung.
    private List<Integer> markingAfterSwitching; // wenn eine Transiton geschaltet wird, ist das die Folgemarkierung.
    private IPlace highlightedPlace; // die Stelle deren Markenanzahl veraendert werden kann.
    private ITransition lastSwitchedTransition; // die Transition die als letztes geschaltet wurde.
    private boolean bounded; // zur Abfrage der (Un)Beschraenktheit.
    private boolean markingExists; // zur Abfrage ob eine bestimmte Markierung schon besucht wurde.
    private String name; // (Datei)Name den das Petrinetz hat
    /* Die folgenden vier Attribute sind fuer die Ausgabe der unschraenktheit des Petrinetzes, im Textbereich der Anwendung wichtig */
    private int unboundedPathsSize; // Die Pfadlaenge des als unbeschraenkt identifizierten Pfads
    private List<String> transitionIDsOfUnboundedPath = new ArrayList<String>(); // nimmt die IDs der Transitionen auf die fuer den Pfad geschaltet wurden
    private List<Integer> unboundedPathMarkingSmaller; // Die Markierung die dem Knoten m entspricht. Siehe Seite 10 in Aufgabenstellung_2021WS.pdf
    private List<Integer> unboundedPathMarkingGreater; // Die Markierung die dem Knoten m' entspricht. Siehe Seite 10 in Aufgabenstellung_2021WS.pdf

    private int counterForMarkingsDiscoveredWhileAnalysisIsRunning = 0;
    
    @Override
    public void addTransition(ITransition transition) {
        transitions.add(transition);
    }
    
    
    @Override
    public void addPlace(IPlace place) {
        if (places == null) {
            places = new ArrayList<IPlace>();
        }
        places.add(place);
    }

    
    @Override
    public void addArc(IPArc petrinetArc) {
        if (arcs == null) {
            arcs = new ArrayList<IPArc>();
        }
        arcs.add(petrinetArc);
    }

    /**
     * Fuehrt die Beschraenktheitsanalyse fuer ein Petrinetz aus.
     */
    public void boundednessanalysis() {
        /*
         * falls die Anfangsmarkierung geaendert werden soll, muss
         * limited wieder true sein um die Beschraenktheit in einem Petrinetz
         * nachzuweisen
         */
        bounded = true;
        List<List<Integer>> firstPath = new ArrayList<List<Integer>>();
        firstPath.add(initialMarking);
        System.out.println("Analyse gestartet für: " + initialMarking.toString());
        counterForMarkingsDiscoveredWhileAnalysisIsRunning += 1;
        buildPathForBoundednessanalysis(firstPath, transitionIDsOfUnboundedPath);
        System.out.println("Anzahl Knoten gesammt: " + counterForMarkingsDiscoveredWhileAnalysisIsRunning);
    }

    /**
     * Diese Methode sorgt dafuer, das der pEG weiter ausgebaut wird, 
     * sofern zuvor eine aktive Transition geschaltet wurde. 
     */
    public void buildReachabilityGraph() {
        // Wenn der Folge-Knoten bereits im pEG entdeckt wurde. 
        if (markingExists(markingAfterSwitching)) {
            // Wenn zwischen dem bereits entdeckten Folge-Knoten und dem als Parameter uebergebenen Quell-Knoten
            // bereits eine Kante existiert.
            if (reachabilityGraph.arcExists(reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                    reachabilityGraph.getNodeByMarking(markingAfterSwitching))) {
                // Wenn die Entdeckung der bereits vorhandenen Kante durch das Schalten einer
                // anderen Transition ausgeloest wurde als der Transition die als letztes 
                // geschaltet wurde, dann wird eine neue Kante eingefuegt.
                if (reachabilityGraph.getAnArcByLabel("[" + lastSwitchedTransition.getID() + "]" + " " + 
                        lastSwitchedTransition.getName()) == null) {
                    // neue Kante zum pEG hinzufuegen
                    reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                            "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                            reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                            reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
                }
            } 
            // Wenn noch keine Kante zwischen dem bereits entdeckten Folge-Knoten und dem 
            // als Parameter uebergebenen Quell-Knoten existiert.
            else {
                // neue Kante zum pEG hinzufuegen
                reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                        "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                        reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                        reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
            }
        } 
        // Wenn der Folge-Knoten noch nicht entdeckt wurde
        else {
            markings.add(markingAfterSwitching);
            // neuen Knoten zum pEG hinzufuegen
            reachabilityGraph.addNode(new RGNode(markingAfterSwitching));
            // neue Kante zum pEG hinzufuegen
            reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                    "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                    reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                    reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
        }
  }
    
    /**
     * Mit dieser Methode werden bis auf die Anfangsmarkierung,
     * alle Markierungen aus der Liste fuer Markierungen geloescht.
     */
    public void clearMarkingsButInitialMarking() {
        if(markings != null) {
            markings.clear();
            markings.add(initialMarking);
        }
    }
    
    /**
     * Diese Methode loescht die IDs der Transitionen, die waehrend der Beschraenktheitsanalyse
     * geschaltet wurden um einen unbeschraenkten Pfad zu bilden.
     */
    public void clearUnboundedPathAndSetUnboundedPathsSizeToZero() {
        transitionIDsOfUnboundedPath.clear();
        unboundedPathsSize = 0;
    }
    
    /**
     * Diese Methode gibt die IDs der Transitionen zurueck, die waehrend der Beschraenktheitsanalyse
     * geschaltet wurden um einen unbeschraenkten Pfad zu bilden.
     * 
     * @return
     *          IDs von Transitionen
     */
    public List<String> getTransitionIDsOfUnboundedPath() {
        return transitionIDsOfUnboundedPath;
    }

    /**
     * Diese Methode gibt die Markierung zurueck, die waehrend der Beschraenktheitsanalyse als Markierung
     * identifiziert wurde, welcher das Kriterium erfuellt an mindestens einer Stelle eine Markenanzahl zu
     * haben, die kleiner ist als die Markenanzahl der selben Stelle einer Folgemarkierung. Des Weiteren 
     * erfuellt die Markierung das Kriterium an allen anderen Stellen eine kleinere oder maximal gleich 
     * grosse Anzahl an Marken zu haben wie die oben erwaehnte Folgemarkierung.
     * 
     * @return
     *          Die Markierung die kleiner ist als eine ihrer Folgemarkierungen. Entspricht dem Knoten m
     */
    public List<Integer> getUnboundedPathMarkingSmaller() {
        return unboundedPathMarkingSmaller;
    }
 
    /**
     * Diese Methode gibt die Markierung zurueck, die waehrend der Beschraenktheitsanalyse als Markierung
     * identifiziert wurde, welcher das Kriterium erfuellt an mindestens einer Stelle eine Markenanzahl zu
     * haben, die hoeher ist als die Markenanzahl der selben Stelle einer vorhergegangen Markierung. Des Weiteren 
     * erfuellt die Markierung das Kriterium an allen anderen Stellen eine mindestens gleich 
     * grosse Anzahl an Marken zu haben wie die oben erwaehnte vorhergegangen Markierung.
     * 
     * @return
     *          Die Markierung die groesser ist als eine ihrer vorhergegangen Markierungen. Entspricht dem Knoten m'
     */
    public List<Integer> getUnboundedPathMarkingGreater() {
        return unboundedPathMarkingGreater;
    }
    
    /**
     * Diese Methode gibt die Laenge des Pfades zurueck, der bei der Beschraenktheitsanalyse als unbeschraenkt identifiziert wurde.
     * Wurde die Beschraenktheitsanalys noch nicht ausgefuehrt, oder ist das Petrinetz beschraenkt, so wird 0 zurueckgegeben.
     * 
     * @return
     *          Pfadlaenge des, als unbeschraenkt nachgewiesenen Pfads. 0 sonnst.
     */
    public int getUnboundedPathsSize() {
        return unboundedPathsSize;
    }

    /**
     * Diese Methode gibt die Stelle zurueck, bei der im Graph aktuell die Anzahl der Marken veraendert werden kann.
     * Kann bei keiner Stelle die Anzahl der Marken veraendert werden, wird null zurueckgegeben.
     * 
     * @return
     *          Stelle deren Markenanzahl veraendert werden kann oder null.
     */
    public IPlace getHighlightedPlace() {
        return highlightedPlace;
    }
    
    @Override
    public IReachabilityGraph getReachabilityGraphModel() {
        return reachabilityGraph;
    }

    /**
     * Diese Methode liefert den Namen von diesem Petrinetz.
     * 
     * @return
     *          Name von diesem Petrinetz.
     */
    public String getName() {
        return name;
    }

    
    @Override
    public List<ITransition> getTransitions() {
        return transitions;
    }

    
    @Override
    public List<IPlace> getPlaces() {
        return places;
    }

    
    @Override
    public List<IPArc> getArcs() {
        return arcs;
    }

    /**
     * Diese Methode liefert die aktuelle Markierung im Petrinetz anhand der Anzahl der Marken der Stellen.
     * 
     * @return
     *          die aktuelle Markierung im Petrinetz.
     */
    public List<Integer> getCurrentMarkingByCurrentPlaceMarkings() {
        List<Integer> curr = new ArrayList<Integer>();
        for (int i = 0; i < places.size(); i++) {
            curr.add(places.get(i).getMarks());
        }
        return curr;
    }

    /**
     * Diese Methode liefert alle bereits entdeckten Markierungen von diesem Petrinetz.
     * 
     * @return
     *          die bereits entdeckten Markierungen.
     */
    public List<List<Integer>> getMarkings() {
        return markings;
    }
    
    /**
     * Liefert die Transition mit der ID id, sofern sie existiert.
     * 
     * @param id
     *          ID einer Transition
     * @return
     *          eine Transition oder null.
     */
    public ITransition getTransitionByID(String id) {
        for (ITransition transition : transitions) {
            if (transition.getID().equals(id)) {
                return transition;
            }
        }
        System.out.println(
                "[getActivatedTransitionByID, Petrinet] Transition " + id + " existiert nicht");
        return null;
    }
    
    /**
     * Diese Methode liefert die Anfangsmarkierung von diesem Petrinetz.
     * 
     * @return
     *          die Anfangsmarkierung von diesem Petrinetz.
     */
    public List<Integer> getInitialMarking() {
        return initialMarking;
    }
    
    /**
     * Diese Methode liefert true wenn die Beschraenktheitsanalyse bereits stattfand und das Petrinetz beschraenkt ist.
     * Andernfalls liefert die Methode false.
     * 
     * @return
     *          true wenn die Beschraenktheitsanalyse beschraenkt ergibt, false andererseits.
     */     
    public boolean isBounded() {
        return bounded;
    }

    /**
     * Diese Methode tauscht den aktuellen pEG mit einem neuen pEG aus, 
     * der die Anfangsmarkierung von diesem Petrinetz als ersten Knoten enthaelt.
     */
    public void initializeNewReachabilityGraph() {
        reachabilityGraph = new Model_ReachabilityGraph();
        reachabilityGraph.setInitailNode(new RGNode(initialMarking));
        reachabilityGraph.addNode(reachabilityGraph.getInitialNode());
    }

    /**
     * Diese Methode initialisiert das Petrinetz. Das heisst alle Elemente im Petrenetz bekommen
     * die Referenzen die sie brauchen. Auch werden die Transitionen alphabetisch geordnet. 
     * Des Weiteren wird die erste Markierung (die Anfangsmarkierung) fuer das Petrinetz
     * festgelegt. Zuletzt wird der pEG von diesem Petrinetz initialisiert und die Transitionen
     * werden anhand der Anfangsmarkierung aktiviert.
     */
    @Override
    public void initializePetrinet() {
        if(initialMarking == null) {
            connectAllElements();
            setNewInitialMarkingByGivenMarking(getCurrentMarkingByCurrentPlaceMarkings());
            markings.add(initialMarking);
            initializeNewReachabilityGraph();
            setTransitionsActivated();
        } else {
            System.out.println("Methode initializePetrinet, Class Petrinet: Das Petrinetz wurde bereits initialisiert.");
        }
    }
    
    /**
     * Diese Methode setzt die Marken der Stellen anhand der Anfangsmarkierung.
     */
    public void resetPlacesToInitialMarking() {
        int index = 0;
        for (IPlace place : places) {
            place.setMarks(initialMarking.get(index));
            index++;
        }
    }

    /**
     * Diese Methode waehlt eine Stelle aus dem Petrinetz aus, deren Markenanzahl veraendert werden kann.
     * 
     * @param placeID
     *          die ID einer Stelle in diesem Petrinetz
     */         
    private void setHighlightedPlaceByID(String placeID) {
        String id = null;
        for (IPlace place : places) {
            if (place.getID().equals(placeID)) {
                highlightedPlace = place;
                id = place.getID();
            }
        }
        if(id == null) {
            System.out.println("Methode setHighlightedPlaceByID, Class Petrinet: die uebergebene ID existiert nicht");
        }
    }
    
    /**
     * Diese Methode bewirkt, dass bei keiner Stelle die Anzahl der Marken veraendert werden kann.
     */
    private void clearHighlightedPlace() {
        highlightedPlace = null;
    }
    
    /**
     * Anhand der uebergebenen ID wird eine Stelle entweder hervorgehoben, die Hervorhebung geloescht,
     * oder bei einer Stelle die Hervorhebung geloescht und bei einer anderen hinzugefuegt.
     * 
     * @param id
     *          die ID einer Stelle von diesem Petrinetz.
     */
    public void changeStateOfHighlightedPlace(String id) {
        // Eine Stelle ist bereits hervorgehoben
        if(highlightedPlace != null) {
            // auf die hervorgehobene Stelle wurde erneut geklickt
            if(highlightedPlace.getID().equals(id)) {
                clearHighlightedPlace();
            } 
            // auf eine andere Stelle als die hervorgehobene wurde geklickt.
            else {
                setHighlightedPlaceByID(id);
            }
        } 
        // es ist noch keine Stelle hervorgehoben
        else {
            setHighlightedPlaceByID(id);
        }
    }

    /**
     * Diese Methode bewirkt, dass die Marken der Stellen anhand einer uebergebenen Markierung gesetzt werden.
     * Falls die uebergebene Markierung nicht genau so viele Elemente enthaelt wie das Petrinetz Stellen hat, dann
     * geschieht nichts. Ist ein Element der Markierung negativ, so wird die Anzahl der Marken der 
     * entsprechenden Stelle auf Null gesetzt.
     * 
     * @param marking
     *          eine Markierung die diesem Petrinetz entspricht.
     */
    public void setCurrentPlaceMarkingsByGivenMarking(List<Integer> marking) {
        int index = 0;
        if(marking.size() == places.size()) {
            for (IPlace place : places) {
                place.setMarks(marking.get(index));
                index++;
            }
        } else {
            System.err.println("Stellen anahnd einer Markierung setzen fehlgeschlagen: Das Petrinetz hat weniger Stellen, als die uebergebene Markierung");
        }
        
    }
    
    @Override
    public void setName(String name) {
        if(this.name == null) {
            this.name = name;
        } else {
            System.out.println("Methode setName, Class Petrinet: Name " + this.name + " kann nachtraeglich nicht geaendert werden");
        }
    }

    /**
     * Die Anfangsmarkierung wird anhand der uebergebenen Markierung gesetzt. 
     * 
     * @param newInitialMarking
     *          die neue Anfangsmarkierung.
     */
    public void setNewInitialMarkingByGivenMarking(List<Integer> newInitialMarking) {
        if(initialMarking == null) {
            initialMarking = newInitialMarking;
        } else if(newInitialMarking != null && newInitialMarking.size() == initialMarking.size()) {
            initialMarking = newInitialMarking;
        }
    }

    /**
     * Die Transitionen von diesem Petrinetz werden nacheinander aktiviert, sofern die Anzahl der Marken von 
     * jeder Stelle im Vorbereich der jeweiligen Transition groesser als Null ist.
     */
    public void setTransitionsActivated() {
        for (ITransition transition : transitions) {
            transition.setActivated();
        }
    }

    /**
     * Schaltet die uebergeben Transition, sofern sie aktiv ist und zu diesem Petrinetz gehoert.
     * 
     * @param transition
     *          eine Transition aus diesem Petrinetz.
     */
    public void switchActivatedTransition(ITransition transition) {
        if(transitionBelongsToPetrinet(transition)) {
            markingBeforeSwitching = getCurrentMarkingByCurrentPlaceMarkings();
            transition.switchTransition();
            markingAfterSwitching = getCurrentMarkingByCurrentPlaceMarkings();
            lastSwitchedTransition = transition;
        }
    }
    
    /**
     * Die Markierungen im uebergebenen Pfad werden solange gewechselt, bis entweder jede Markierung
     * einmal besucht wurde, oder bis die unbeschraenktheit des Petrinetzes nachgewiesen wurde. <br><br> 
     * 
     * Jedes Mal wenn eine neue Markierung durch Schalten einer Transition der aktuellen Markierung entdeckt wird, 
     * wird aus allen bisher entdeckten Markierungen und der neue entdeckten Markierung ein neuer Pfad gebildet. <br><br>
     * 
     * Die ID der Transition die geschaltet wurde um die neue Markierung zu entdecken, wird zusammen mit den bisherigen 
     * IDs, der geschalteten Transitionen im aktuellen Pfad gespeichert. <br><br>
     * 
     * Die Methode wird anschliessend erneut von sich selbst aufgerufen (Rekursiver Aufruf). Die Parameter fuer den 
     * neuen Aufruf sind der neu entdeckte Pfad und die Liste mit den IDs der Transitionen. Besteht der als Parameter
     * uebergeben Pfad aus mindestens zwei Markierungen, so wird als erstes im rekursiven Aufruf die 
     * Beschraenktheitsanalyse durchgefuehrt.
     * 
     * @param path
     *          der Pfad von dessen Markierungen die Transitionen solange geschaltet werden, 
     *          bis keine Transitionen mehr aktiv sind.
     * @param iDsOfTransitions
     *          die IDs der Transitionen die geschaltet wurden um den Pfad aufzubauen.
     */
    private void buildPathForBoundednessanalysis(List<List<Integer>> path, List<String> iDsOfTransitions) {
        // ueberpruefung ob der uebergebene Pfad unbeschraenkt ist findet erst statt, wenn mehr als eine Markierung im Pfad enthalten ist
        if (path.size() > 1) {
            List<List<Integer>> nodeMAndNodeGreaterM = compareMarkingsForboundednessanalysis(path);
            // der Pfad ist unbeschraenkte falls nodMAndNodeGreaterM nicht leer ist.
            if (nodeMAndNodeGreaterM != null) {
                bounded = false;
                unboundedPathsSize = path.size() - 1;
                transitionIDsOfUnboundedPath = iDsOfTransitions;
                unboundedPathMarkingSmaller = nodeMAndNodeGreaterM.get(0);
                unboundedPathMarkingGreater = nodeMAndNodeGreaterM.get(1);
                ((Model_ReachabilityGraph) reachabilityGraph).setUnlimitedPathsRGArcsByGivenPath(path);
                return;
            }
        }
//        setTransitionsActivated();
        // Speichern der aktiven Transitionen der aktuellen Markierung.
        List<ITransition> activeTransitionsOfCurrentMarking = new ArrayList<ITransition>();
        for (ITransition transition : getTransitions()) {
            if (transition.isActivated()) {
                activeTransitionsOfCurrentMarking.add(transition);
            }
        }
        /*
         * in lastPathfork wird die aktuelle Markierung gespeichert. Damit kann das 
         * Programm wieder zur letzten Weggabelung zurueck kehren um danach einen anderen
         * Weg einzuschlagen. Dies ist wichtig wenn es zur Entdeckung eines Kreises im Pfad kommt.
         */
        List<Integer> lastPathfork = new ArrayList<Integer>();
        lastPathfork = getCurrentMarkingByCurrentPlaceMarkings();
        // Wenn noch mindestens eine Transition der aktuellen Markierung aktiv ist.
        if (activeTransitionsOfCurrentMarking.size() != 0) {
            for (ITransition activeTransition : activeTransitionsOfCurrentMarking) {
                /* im folgenden werden die IDs der Transitionen gesammelt, die zu diesem Pfad gefuehrt haben.
                 * Falls dieser Pfad als unbeschraenkt identifiziert wird, werden diese IDs zur Ausgabe
                 * des Pfads im Textbereich der Anwendung angezeigt. */
                List<String> iDsOfTheActiveTransitionsThatLedToThisPath = new ArrayList<String>();
                for(String transitionID : iDsOfTransitions) {
                    // hinzufuegen aller IDs die bisher geschaltet wurden, damit dieser Pfad aufgebaut wurde
                    iDsOfTheActiveTransitionsThatLedToThisPath.add(transitionID);
                }
                // hinzufuegen der ID der Transition die in diesem Aufruf geschaltet wird
                iDsOfTheActiveTransitionsThatLedToThisPath.add(activeTransition.getID());
                // diese Zuweisung ist wichtig wenn durch entdecken eines Kreises, das Programm
                // zu diesem Aufruf zurueckkommt.
                setCurrentPlaceMarkingsByGivenMarking(lastPathfork); 
                // Transitionen der aktuellen markierung (vor dem Schalten) aktivieren. Diese Zuweisung ist 
                // wichtig wenn durch entdecken eines Kreises, das Programm zu diesem Aufruf zurueckkommt.
                setTransitionsActivated();
                switchActivatedTransition(activeTransition);
                buildReachabilityGraphForBoundednessanalysis();
                // Alle Transitionen muessen nach dem Schalten einer aktiven Transition erneut aktiviert werden.
                setTransitionsActivated();
                // im folgenden wird bereits die Folgemarkierung uebergeben
                List<Integer> followUpMarking = getCurrentMarkingByCurrentPlaceMarkings(); 
                List<List<Integer>> newPath = new ArrayList<List<Integer>>(); 
                // alle bisherigen Markierungen vom uebergebenen Pfad werden einem neuen Pfad hinzugefuegt
                for (List<Integer> marking : path) {
                    newPath.add(marking);
                }
                // der neue Pfad wird um die Folgemarkierung erweitert
                newPath.add(followUpMarking);
                /* nun wird ueberprueft, ob mit der Folgemarkierung ein Kreis im pEG entstanden ist */
                // counter dient zur ueberpruefung ob die Folgemarkierung bereits zuvor entdeckt wurde.
                // Falls die neue Markierung bereits zuvor entdeckt wurde unterscheidet sich counter 
                // von der Anzahl der Markierungen in der Menge der Markierungen.
                int counter = 0;
                for (List<Integer> marking : markings) {
                    if (marking.equals(followUpMarking)) {
                        counter--;
                        markingExists = true;
                    }
                    counter++;
                }
                if (markings.size() == counter) {
                    markings.add(followUpMarking);
                }
                // Wenn die Folgemarkierung nicht in der Menge aller Markierungen zu finden ist,
                // wurde sie noch nicht ueberprueft. Dann findet hier der rekursive Aufruf mit dem neuen Pfad statt
                // in dem ueberprueft wird, ob anhand der Folgemarkierung das Petrinetz als unbeschraenkt identifiziert werden kann. 
                if (markingExists == false) {
                    System.out.println("Neue Markierung gefunden: " + followUpMarking.toString());
                    counterForMarkingsDiscoveredWhileAnalysisIsRunning += 1;
                    buildPathForBoundednessanalysis(newPath, iDsOfTheActiveTransitionsThatLedToThisPath);
                    if (isBounded() == false) {
                        return;
                    }
                }
                // Falls sich herausgestellt hat, dass die Folgemarkierung zur Bildung eines Kreises im pEG fuehrt,
                // gelangt das Programm hier hin. markingExists wird fuer den naechsten durchgang in der urspruenglichen Markierung
                // (bevor die Transition geschaltet wurde die zu einem Kreis im pEG gefuehrt hat), auf false gesetzt.
                markingExists = false;
            }
        }
    }

    /**
     * Diese Methode enthaelt den eigenlichen Beschraenktheits-Algorithmus.
     * Die Methode vergleicht die Knoten eines uebergebenen Pfades immer Paarweise
     * miteinander. Ist der Pfad unbeschraenkt, dann wird ein Pfad zurueckgegeben, der nur aus zwei Markierungen
     * besteh. <br><br>
     * 
     * Erste Markierung: die Markierung die waehrend der Beschraenktheitsanalyse das Kriterium erfuellt an 
     * mindestens einer Stelle eine Anzahl von Marken zu haben, die kleiner ist als die Anzahl von Marken der selben 
     * Stelle einer Folgemarkierung. Ausserdem hat die Markierung an allen anderen Stellen eine maximal gleich 
     * grosse Anzahl von Marken wie die Folgemarkierung. Diese Markierung entspricht der Markierung m.
     * <br><br>
     * 
     * Zweite Markierung: die Markierung die waehrend der Beschraenktheitsanalyse das Kriterium erfuellt an 
     * mindestens einer Stelle eine Anzahl von Marken zu haben, die groesser ist als die Anzahl von Marken der selben 
     * Stelle von Markierung m von Oben. Ausserdem hat die Markierung an allen anderen Stellen eine mindestens gleich 
     * grosse Anzahl von Marken wie die Markierung m von Oben. Diese Markierung entspricht dem Knoten m'.
     * 
     * @param path
     *          ist der Pfad der auf Beschraenktheit ueberprueft werden soll.
     *          
     * @return
     *          Die Knoten m und m' wenn der Pfad unbeschraenkt ist, sonst null.
     */
    private List<List<Integer>> compareMarkingsForboundednessanalysis(List<List<Integer>> path) {
        int positionOfPlaceMarkingInNode = 0;
        int resultUnbounded = 0; // kann den Wert 0 oder 1 annehmen
        int resultBounded = 0; // kann den Wert 0 oder -1 annehmen
        int intermediateResult = 0; // kann den Wert 0, 1 oder -1 annehmen 
        int finalResult = 0; // kann den Wert 0, 1 oder -1 annehmen.
        // Diese for-Schleife wird bei n Markierungen im Pfad n mal ausgefuehrt
        for (int currentLoop = 0; currentLoop < path.size() - 1; currentLoop++) { 
            for (List<Integer> node : path) {
                // Hier werde die Markierungen wie folgt nach den Nummern ihrer Positionen 
                // im Pfad verglichen: 1 bis n mit 0; 2 bis n mit 1, 3 bis n mit 2, ... , n mit n-1
                if (path.indexOf(node) > currentLoop) {
                    // Iterieren über die Stellenmarkierungen eines Knotens der die if-Bedingung erfüllt
                    for (Integer placeMarking : node) {
                        // die Stellenmarkierungen der Stellen mit gleichen Positionen von zwei verschiedenen Knoten 
                        // werden in einem Durchgang miteinander verglichen. Die Zwischenergebnisse werden, sofern sie
                        // erneut die Bedingung erfüllen, in den dafür vorgesehenen Variablen gespeichert.
                        intermediateResult = placeMarking.compareTo(path.get(currentLoop).get(positionOfPlaceMarkingInNode));
                        if (intermediateResult == -1) {
                            resultBounded = -1;
                        } else if (intermediateResult == 1) {
                            resultUnbounded = 1;
                        }
                        positionOfPlaceMarkingInNode++;
                    }
                    // finalResult = 0 oder -1 bedeuted: Pfad ist nicht unbeschraenkt; finalResult = 1 bedeuted: Pfad ist unbeschraenkt
                    // finalResult kann nur genau dann 1 sein, wenn mindestens eine Stelle in der 
                    // Folgemarkierungen eine hoehere Anzahl an Marken hat als die gleiche Stelle in 
                    // einer der vorhergehenden Markierungen und wenn alle Anderen Stellen gleich bleiben.
                    finalResult = resultUnbounded + resultBounded; 
                    // hier werden alle Variablen, auf die Werte ihrer Initialisierung am Anfang der Methode zurueckgesetzt, 
                    // weil diese beim naechsten Durchgang der Schleife wieder so benoetigt werden, falls finalResult die
                    // folgende if-Bedingung nicht bestehen sollte.
                    positionOfPlaceMarkingInNode = 0;
                    intermediateResult = 0;
                    resultUnbounded = 0;
                    resultBounded = 0;
                }
                // true, Wenn der Pfad unbeschraenkt ist. Die Markierungen m und m' werden zurueckgegeben
                if (finalResult == 1) {
                    // der Vergleich von zwei Markierungen hat die Unbeschraenktheit des Petrintzes nachgewiesen
                    List<List<Integer>> nodeMAndNodeGreaterM = new ArrayList<List<Integer>>();
                    nodeMAndNodeGreaterM.add(path.get(currentLoop));
                    nodeMAndNodeGreaterM.add(node);
                    return nodeMAndNodeGreaterM;
                }
            }
        }
        return null;
    }

    /** 
     * Die aequivalente Methode zur oeffentlichen Methode buildReachabilityGraph mit dem Unterschied,
     * dass bei dieser Methode keine Markierung zur Menge der Markierungen hinzugefuegt wird, falls
     * ein neuer Knoten im pEG entdeckt wird. Dies geschieht in der Methode buildPathForBoundednessanalysis
     */
    private void buildReachabilityGraphForBoundednessanalysis() {
        // Wenn der Folge-Knoten bereits im pEG entdeckt wurde. 
        if (markingExists(markingAfterSwitching)) {
            // Wenn zwischen dem bereits entdeckten Folge-Knoten und dem als Parameter uebergebenen Quell-Knoten
            // bereits eine Kante existiert.
            if (reachabilityGraph.arcExists(reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                    reachabilityGraph.getNodeByMarking(markingAfterSwitching))) {
                // Wenn die Entdeckung der bereits vorhandenen Kante durch das Schalten einer
                // anderen Transition ausgeloest wurde als der Transition die als letztes 
                // geschaltet wurde, dann wird eine neue Kante eingefuegt.
                if (reachabilityGraph.getAnArcByLabel("[" + lastSwitchedTransition.getID() + "]" + " " + 
                        lastSwitchedTransition.getName()) == null) {
                    // neue Kante zum pEG hinzufuegen
                    reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                            "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                            reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                            reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
                }
            } 
            // Wenn noch keine Kante zwischen dem bereits entdeckten Folge-Knoten und dem 
            // als Parameter uebergebenen Quell-Knoten existiert.
            else {
                // neue Kante zum pEG hinzufuegen
                reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                        "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                        reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                        reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
            }
        } 
        // Wenn der Folge-Knoten noch nicht entdeckt wurde
        else {
            // neuen Knoten zum pEG hinzufuegen
            reachabilityGraph.addNode(new RGNode(markingAfterSwitching));
            // neue Kante zum pEG hinzufuegen
            reachabilityGraph.addArc(new RGArc("rg" + reachabilityGraph.getNextFreeArcIndex(),
                    "[" + lastSwitchedTransition.getID() + "]" + " " + lastSwitchedTransition.getName(),
                    reachabilityGraph.getNodeByMarking(markingBeforeSwitching),
                    reachabilityGraph.getNodeByMarking(markingAfterSwitching)));
        }
    }
    
    /*
     * Diese Methode bewirkt, dass ein Element im Petrinets alle Referenzen auf die
     * anderen Elemente erhaelt, die es kennen muss. Die Referenzen die eine Kante
     * haben muss, werden bereits bei der Erzeugung der Kante in der Methode newArc
     * der Klasse ExtendedPNMLWopedParser vergeben.
     */
    private void connectAllElements() {
        for (IPlace place : places) {
            for (ITransition transition : transitions) {
                for (IPArc arc : arcs) {
                    // Kante von einer Stelle zu einer Transition
                    if (((PArc) arc).getPlaceAsSource() == place && ((PArc) arc).getTransitionAsTarget() == transition) {
                        // Kante zur Menge der ausgehenden Kanten hinzufuegen.
                        ((Place) place).addArcToTransitionAfter(arc);
                        // Transition zur Menge der Transitionen hinzufuegen, zu 
                        // denen eine Kante von dieser Stelle fuehrt
                        ((Place) place).addTransitionAfter(transition);
                        // Kante zur Menge der eingehenden Kanten hinzufuegen
                        ((Transition) transition).addArcFromPreArea(arc);
                        // Stelle zum Vorbereich hinzufuegen
                        ((Transition) transition).addPlaceToPreArea(place);
                    } 
                    // Kante von einer Transition zu einer Stelle
                    else if (((PArc) arc).getPlaceAsTarget() == place
                            && ((PArc) arc).getTransitionAsSource() == transition) {
                        // Kante zur Menge der eingehenden Kanten hinzufuegen.
                        ((Place) place).addArcFromTransitionBefore(arc);
                        // Transition zur Menge der Transitionen hinzufuegen, von 
                        // denen eine Kante zu dieser Stelle fuehrt
                        ((Place) place).addTransitionBefore(transition);
                        // Kante zur Menge der ausgehenden Kanten hinzufuegen
                        ((Transition) transition).addArcToPostArea(arc);
                        // Stelle zum Vorbereich hinzufuegen
                        ((Transition) transition).addPlaceToPostArea(place);
                    }
                }
            }
        }
    }
    
    /*
     * Testet ob eine uebergebene Markierung in der Menge der Markierungen enthalten
     * ist und gibt das Ergebnis zurueck.
     */
    private boolean markingExists(List<Integer> marking) {
        for (List<Integer> existingMarking : markings) {
            if (existingMarking.equals(marking)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Testet ob eine uebergebene Transition auch wirklich zu diesem Petrinetz gehoert
     * 
     * @param otherTransition
     *          Transition die ueberprueft wird
     * @return
     *          true wenn die Transition zum Petrinetz gehoert, falls sonst.
     */
    private boolean transitionBelongsToPetrinet(ITransition otherTransition) {
        for(ITransition transition : transitions) {
            if(transition == otherTransition) {
                return true;
            }
        }
        return false;
    }
}