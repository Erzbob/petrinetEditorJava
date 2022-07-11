package petrinet_parser;

import java.io.File;
import java.util.List;

import petrinet_model.*;
import propra.pnml.PNMLWopedParser;

/**
 * Diese Klasse ist eine Erweiterung der Klasse {@link PNMLWopedParser}. <br><br>
 * 
 * Mit Dieser Klasse ist es moeglich, das Datenmodel fuer ein Petrinetz zu erstellen.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class ExtendedPNMLWopedParser extends PNMLWopedParser {

    private IPetrinet petrinet = new Model_Petrinet(); // das Petrinetz von diesem Parser-Objekt
    private static String name; // ein Klassenattribut fuer die Namen von Petrinetzen. 
    
    /**
     * Erzeugen und initialisieren eines neuen ExtendedPNMLWopedParser-Objekts, dessen Petrinetz
     * aus der uebergebenen pnml-Datei erzeugt wird.
     * 
     * @param pnml
     *          Die pnml-Datei aus der das Petrinetz von diesem ExtendedPNMLWopedParser-Objekt erzeugt wird.
     */
    private ExtendedPNMLWopedParser(File pnml) {
        super(pnml);
    }

    /**
     * Diese Methode bekommt als formalen Parameter einen Dateipfad uebergeben und 
     * erzeugt daraus ein neues ExtendedPNMLWopedParser-Objekt. Das Petrinetz des 
     * Parser-Objekts wird anhand des Inhalts der Datei hinter dem Dateipfad erstellt.
     * Das Rueckgabe-Objekt enthaelt bereits das vollstaendig aufgebaute Petrinetz-Modell.
     * 
     * @param filepath
     *          ein String der einen absoluten Dateipfad zu einer pnml-Datei
     *          repraesentiert
     * @return 
     *          ein neues ExtendedPNMLWopedParser-Objekt mit vollstaendig aufgebautem Petrinets-Modell.
     */
    public static ExtendedPNMLWopedParser getParserForFilepath(final String filepath) {
        // Wenn kein leerer String uebergeben wurde
        if (filepath != null) {
            File pnmlDatei = new File(filepath);
            // Wenn sich hinter dem Dateipfad eine Datei verbirgt
            if (pnmlDatei.exists()) {
                // Wenn die Datei hinter dem Dateipfad mit WoPed erzeugt wurde (oder zumindest die richtige Dateiendung hat)
                if (filepath.endsWith(".pnml") || filepath.endsWith(".PNML")) {
                    // der Name fuer das Petrinetz aus deser Datei
                    name = pnmlDatei.getName(); 
                    ExtendedPNMLWopedParser newParser = new ExtendedPNMLWopedParser(pnmlDatei);
                    newParser.initParser();
                    newParser.parse();
                    return newParser;
                } else {
                    System.out.println("Bitte eine Datei mit dem Typ .pnml oder .PNML laden");
                    return null;
                }
            } else {
                System.err.println("Die Datei " + pnmlDatei.getAbsolutePath() + " wurde nicht gefunden!");
                return null;
            }
        } else {
            System.out.println("Bitte eine Datei als Parameter angeben!");
            return null;
        }
    }

    /**
     * Liefert das Petrinetz von diesem ExtendedPNMLWopedParser-Objekt.
     * 
     * @return 
     *      Das Petrinet-Objekt von diesem ExtendedPNMLWopedParser-Objekt.
     */
    public IPetrinet getPetrinet() {
        petrinet.initializePetrinet();
        petrinet.setName(name);
        return petrinet;
    }

    /**
     * Erzeugen und inizialisieren eines neuen Transition-Objekts mit der ID id.
     * Hinzufuegen der Transition zum Petrinetz von diesem Parser.
     * 
     * @param id
     *          die ID der Transition
     */
    @Override
    public void newTransition(String id) {
        ITransition newTransition = new Transition(id);
        petrinet.addTransition(newTransition);
    }

    /**
     * Erzeugen und inizialisieren eines neuen Place-Objekts mit der ID id.
     * Hinzufuegen der Stelle zum Petrinetz von diesem Parser.
     * 
     * @param id
     *          die ID der Stelle
     */
    @Override
    public void newPlace(String id) {
        IPlace newPlace = new Place(id);
        petrinet.addPlace(newPlace);
    }

    /**
     * Erzeugen und initialisieren eines neuen PArc-Objekts mit der ID id, der Quelle
     * source und dem Ziel target.
     * Hinzufuegen der Kante zum Petrinetz von diesem Parser.
     * 
     * @param id
     *          die ID der Kante
     * @param source
     *          die Quelle der Kante
     * @param target
     *          das Ziel der Kante
     */
    @Override
    public void newArc(String id, String source, String target) {
        // aus den bisher im Petrinetz enthaltenen Stellen und Transitionen, werden
        // diese Herausgesucht, die mit den Parametern source und target uebereinstimmen.
        // Bei einer uebereinstimmung wird dieser Kante eine Referenz auf das Element
        // zu dem sie gehoert uebergeben.
        IPArc newPetrinetArc = new PArc(id);
        List<ITransition> transitions = petrinet.getTransitions();
        List<IPlace> places = petrinet.getPlaces();
        // Suchen der Transition die zu dieser Kante gehoert
        for (ITransition transition : transitions) {
            if (transition.getID().equals(source)) {
                ((PArc) newPetrinetArc).setTransitionAsSource(transition);
            } else if (transition.getID().equals(target)) {
                ((PArc) newPetrinetArc).setTransitionAsTarget(transition);
            }
        }
        // Suchen der Stelle die zu dieser Kante gehoert.
        for (IPlace place : places) {
            if (place.getID().equals(source)) {
                ((PArc) newPetrinetArc).setPlaceAsSource(place);
            } else if (place.getID().equals(target)) {
                ((PArc) newPetrinetArc).setPlaceAsTarget(place);
            }
        }
        petrinet.addArc(newPetrinetArc);
    }

    /**
     * Vergibt den uebergebenen Namen an die Stelle oder die Transition mit der uebergebenen ID.
     * Gibt es noch keine Stellen oder Transitionen im Petrinetz, wird ein neues Element erzeugt.
     * 
     * @param id
     *          die ID einer Stelle oder Transition.
     * @param name
     *          der Name der zugewiesen wird.
     */
    @Override
    public void setName(String id, String name) {
        if (id.substring(0, 1).equals("t")) {
            setTransitionName(id, name);
        } else if (id.substring(0, 1).equals("p")) {
            setPlaceName(id, name);
        }
    }

    /**
     * Zuweisen einer Position an die Stelle oder die Transition mit der uebergebenen ID.
     * Gibt es noch kein Stellen oder Transitionen im Petrinetz, wird ein neues Element erzeugt.
     * 
     * @param id
     *         die ID einer Stelle oder Transition. 
     * @param x
     *         die x Koordinate. 
     * @param y
     *         die y Koordinate.
     */
    @Override
    public void setPosition(String id, String x, String y) {
        int xPos = Integer.parseInt(x);
        int yPos = Integer.parseInt(y);
        if (id.substring(0, 1).equals("t")) {
            setTransitionPosition(id, x, y, xPos, yPos);
        } else if (id.substring(0, 1).equals("p")) {
            setPlacePosition(id, x, y, xPos, yPos);
        }
    }

    // Marken werden an eine Stelle vergeben.
    // Falls es zu der ID noch keine Stelle gibt, wird eine neue Stelle erstellt, die die Position und die ID bekommt.
    // Die neue Stelle wird anschliessend zum Petrinetz hinzugefuegt.
    /**
     * Zuweisen einer Menge von Marken an die Stelle mit der uebergebenen ID.
     * Gibt es noch keine Stellen im Petrinetz, wird eine neue Stelle erzeugt.
     * 
     * @param id
     *          die ID einer Stelle.
     * @param tokens
     *          die Marken einer Stelle.
     */
    @Override
    public void setTokens(String id, String tokens) {
        int initialMarking = Integer.parseInt(tokens);
        if (id.substring(0, 1).equals("p")) {
            setPlaceInitialMarking(id, tokens, initialMarking);
        }
    }

    // wird in der Methode setName verwendet
    private void setPlaceName(String id, String name) {
        // Wenn es Stellen im Petrinetz gibt
        if (petrinet.getPlaces() != null) {
            for (IPlace place : petrinet.getPlaces()) {
                // Wenn es eine Stelle im Petrinetz mit der ID id gibt
                if (place.getID().equals(id)) {
                    place.setName(name);
                }
            }
        } 
        // Wenn es noch keine Stellen im Petrinetz gibt
        else {
            newPlace(id);
            setName(id, name);
        }
    }

    // wird in der Methode setName verwendet
    private void setTransitionName(String id, String name) {
        // Wenn es Transitionen im Petrinetz gibt.
        if (petrinet.getTransitions() != null) {
            for (ITransition transition : petrinet.getTransitions()) {
                // Wenn es eine Transition mit der ID id gibt
                if (transition.getID().equals(id)) {
                    transition.setName(name);
                }
            }
        } 
        // Wenn es noch keine Transitionen im Petrinetz gibt
        else {
            newTransition(id);
            setName(id, name);
        }
    }

    // wird in der Methode setPosition verwendet
    private void setPlacePosition(String id, String x, String y, int xPos, int yPos) {
        // Wenn es Stellen im Petrinetz gibt.
        if (petrinet.getPlaces() != null) {
            for (IPlace place : petrinet.getPlaces()) {
                // Wenn es eine Stelle mit der ID id gibt.
                if (place.getID().equals(id)) {
                    place.setPosition(xPos, yPos);
                }
            }
        } 
        // Wenn es noch keine Stellen im Petrinetz gibt.
        else {
            newPlace(id);
            setPosition(id, x, y);
        }
    }

    // wird in der Methode setPosition verwendet
    private void setTransitionPosition(String id, String x, String y, int xPos, int yPos) {
        // Wenn es Transitionen im Petrinetz gibt.
        if (petrinet.getTransitions() != null) {
            for (ITransition transition : petrinet.getTransitions()) {
                // Wenn es eine Transition mit der ID id gibt.
                if (transition.getID().equals(id)) {
                    transition.setPosition(xPos, yPos);
                }
            }
        } 
        // Wenn es noch keine Transitionen gibt.
        else {
            newTransition(id);
            setPosition(id, x, y);
        }
    }

    // wird in der Methode setTokens verwendet
    private void setPlaceInitialMarking(String id, String tokens, int initialMarking) {
        // Wenn es Stellen im Petrinetz gibt.
        if (petrinet.getPlaces() != null) {
            for (IPlace place : petrinet.getPlaces()) {
                // Wenn es eine Stelle mit der ID id gibt.
                if (place.getID().equals(id)) {
                    place.setMarks(initialMarking);
                }
            }
        } 
        // Wenn es noch keine Stellen im Petrinetz gibt.
        else {
            newPlace(id);
            setTokens(id, tokens);
        }
    }
}
