package petrinet_controller;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.ui.view.*;

import petrinet_gui.*;
import petrinet_listener.ClickListenerGraph;
import petrinet_model.*;
import petrinet_parser.ExtendedPNMLWopedParser;
import petrinet_view.*;

/**
 * Diese Klasse beschreibt eine moegliche Umsetzung der Steuerung, welche auf
 * die Ereignisse, die an der gesamten Anwendung auftreten, reagiert. <br>
 * <br>
 * 
 * Ein Controller-Objekt erhaelt bei seiner Konstruktion Referenzen auf ein
 * {@link PetrinetFrame}-Objekt, ein {@link PetrinetMenuBar}-Objekt und ein
 * {@link PetrinetToolBar}-Objekt. Ein Petrinet Controller-Objekt reagiert auf
 * alle Ereignisse die an den als Parameter uebergebenen Komponenten (und allen
 * Komponenten die in diesen Komponeneten enthalten sind) auftreten.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class Controller {

    /**
     * Erzeugt und initialisiert ein neues Controller-Objekt.
     * 
     * @param frame   das Fenster das von diesem Controller gesteuert wird.
     * @param menuBar die Menue-Bar die von diesem Controller gesteuert wird.
     * @param toolBar die Tool-Bar die von diesem Controller gesteuert wird.
     */
    public Controller(PetrinetFrame frame, PetrinetMenuBar menuBar, PetrinetToolBar toolBar) {

        // oeffnen-Menue-Eintrag
        menuBar.getMenu(0).getItem(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                /*
                 * die folgende Fallunterscheidung stellt sicher, das beim erstmaligen oeffnen
                 * (beim Programmstart) in das Verzeichnis mit den pnml-Dateien (also
                 * Beispiele), navigiert wird, beim erneuten oeffnen aber in das Verzeichnis,
                 * aus dem heraus zuletzt eine .pnml-Datei geoeffnet wurde.
                 */
                if (frame.getCurrentDirectory() == null) {
                    chooser = new JFileChooser("../ProPra-WS21-Basis/Beispiele/");
                } else {
                    chooser = new JFileChooser(frame.getCurrentDirectory());
                }
                // im FileChooser werden nur Dateien mit Endung .pnml und .PNML angezeigt.
                FileNameExtensionFilter pnmlFilter = new FileNameExtensionFilter(".pnml Datei", "pnml", "PNML");
                chooser.setFileFilter(pnmlFilter);
                int returnVal = chooser.showOpenDialog(null);
                // File geoeffnet da Dialog vorher nicht geschlossen
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    // Die FileListe wird bei jedem Oeffnen einer Datei ueber den FileChooser neu
                    // gesetzt.
                    // Somit wird automatisch, wenn das Datei-Verzeichnis mit den pnml-Dateien
                    // geaendert wurde,
                    // sofort auch der Inhalt aus diesem Verzeichnis gespeichert.
                    // Dies ist deshalb von Bedeutung, weil fuer die Buttons naechstes Petrinetz und
                    // letztes Petrinetz
                    // die Liste mit den Files herangezogen werden muss und diese natuerlich mit den
                    // Dateien im
                    // aktuellen Verzeichnis identisch sein muss.
                    List<File> files = null;
                    files = new ArrayList<File>();
                    for (File file : chooser.getCurrentDirectory().listFiles()) {
                        if (file.getName().endsWith(".pnml") || file.getName().endsWith(".PNML")) {
                            files.add(file);
                        }
                    }
                    // Dem Frame eine Referenz der Liste mit allen .pnml- oder .PNML-Dateien aus dem
                    // Verzeichnis uebergeben
                    frame.setFileList(files);
                    // Dem Frame eine Referenz auf das aktuelle Verzeichnis uebergeben
                    frame.setCurrentDirectory(chooser.getCurrentDirectory());
                    // Dem Frame eine Referenz auf die aktuelle Datei geben
                    frame.setCurrentFile(chooser.getSelectedFile());
                    // String aus der, im FileChooser ausgewaehlten Datei erzeugen
                    String fileString = chooser.getCurrentDirectory() + "/" + chooser.getSelectedFile().getName();
                    // Das Petrinetz aus der ausgewaehlten Datei erzeugen und im Frame anzeigen.
                    displayPetrinetFromFile(frame, fileString);
                    // Die Buttons der Toolbar klickbar machen.
                    toolBar.getComponent(0).setEnabled(true);
                    toolBar.getComponent(1).setEnabled(true);
                    toolBar.getComponent(2).setEnabled(true);
                    toolBar.getComponent(3).setEnabled(true);
                    toolBar.getComponent(4).setEnabled(true);
                    toolBar.getComponent(5).setEnabled(true);
                    toolBar.getComponent(6).setEnabled(true);
                }
            }
        });

        // neu_laden-Menue-Eintrag
        menuBar.getMenu(0).getItem(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Wenn es eine Datei gibt, die neu geladen werden kann
                if (frame.getCurrentFile() != null) {
                    // String aus der, im FileChooser ausgewaehlten Datei erzeugen
                    String fileString = frame.getCurrentDirectory() + "/" + frame.getCurrentFile().getName();
                    // Das Petrinetz aus der ausgewaehlten Datei erzeugen und im Frame anzeigen.
                    displayPetrinetFromFile(frame, fileString);
                }
            }
        });

        // analyse_mehrerer_Dateien-Menue-Eintrag
        menuBar.getMenu(0).getItem(2).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int informationReturnVal = JOptionPane.showConfirmDialog(frame,
                        "Hinweis: Wählen Sie STRG + Maustaste_links um mehrere Dateien auszuwählen",
                        "Auswahl mehrerer Dateien", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                // Wenn der Informations-Dialog nach Klicken des Menue-Eintrages mit OK
                // beantwortet wird
                if (informationReturnVal == JOptionPane.OK_OPTION) {
                    JFileChooser chooser = new JFileChooser();
                    /*
                     * die folgende Fallunterscheidung stellt sicher, das beim erstmaligen oeffnen
                     * (beim Programmstart) in das Verzeichnis mit den pnml-Dateien (also
                     * Beispiele), navigiert wird, beim erneuten oeffnen aber in das Verzeichnis,
                     * aus dem heraus zuletzt eine .pnml-Datei geoeffnet wurde.
                     */
                    if (frame.getCurrentDirectory() == null) {
                        chooser = new JFileChooser("../ProPra-WS21-Basis/Beispiele/");
                    } else {
                        chooser = new JFileChooser(frame.getCurrentDirectory());
                    }
                    // Multi-Datei-Auswahl fuer den FileChooser aktivieren.
                    chooser.setMultiSelectionEnabled(true);
                    // Dateiendungen .pnml und .PNML sind erlaubt
                    FileNameExtensionFilter pnmlFilter = new FileNameExtensionFilter(".pnml Datei", "pnml", "PNML");
                    chooser.setFileFilter(pnmlFilter);
                    int fileChooserReturnVal = chooser.showOpenDialog(null);
                    // wenn bereits ein Graph angezeigt wird
                    if (fileChooserReturnVal == JFileChooser.APPROVE_OPTION && frame.getPetrinetModel() != null) {
                        // Warnung das der angezeigte Graph geloescht wird.
                        int disposePetrinetReturnVal = JOptionPane.showConfirmDialog(frame,
                                "Achtung: Wenn Sie die Analyse starten, wird das aktuelle Petrinetz gelöscht!",
                                "aktuelles Petrinetz löschen?", JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        // Starten der Analyse
                        if (disposePetrinetReturnVal == JOptionPane.OK_OPTION) {
                            analyseFuerMehrereDateienUndTextausgabeImTextbereich(frame, toolBar, chooser);
                        }
                    }
                    // Wenn kein Graph angezeigt wird.
                    else if (fileChooserReturnVal == JFileChooser.APPROVE_OPTION) {
                        // Starten der Analyse
                        analyseFuerMehrereDateienUndTextausgabeImTextbereich(frame, toolBar, chooser);
                    }
                    // Systemproperties im Statuslabel anzeigen
                    changeStatusLabelBecauseNewPetrinetWasLoaded(frame);
                }
            }
        });

        // beenden-Menue-Eintrag
        menuBar.getMenu(0).getItem(3).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = JOptionPane.showConfirmDialog(frame, "Möchten Sie das Programm beenden?", "beenden",
                        JOptionPane.YES_NO_OPTION);
                if (returnVal == JOptionPane.YES_NO_OPTION) {
                    System.exit(0);
                }
            }
        });

        // info-Menue-Eintrag
        menuBar.getMenu(1).getItem(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        frame, "Aktuelles Arbeitsverzeichnis: " + System.getProperty("user.dir") + "\n"
                                + "java.version = " + System.getProperty("java.version"),
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // icons_source-Menue-Eintrag
        menuBar.getMenu(1).getItem(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                 * hiermit wird ein Dialog erzeugt, der die Quellen der Bilder anzeigt, welche
                 * fuer die Buttons der Toolbar verwendet werden.
                 */
                List<JEditorPane> editorPanes = new ArrayList<JEditorPane>();
                editorPanes.add(new JEditorPane("text/html",
                        "Die Symbole der Toolbar stammen von folgenden Quellen, abgerufen am 25.11.2021:"));
                editorPanes.add(new JEditorPane("text/html", "https://icons8.com/icon/79625/mechanistic-analysis"));
                editorPanes.add(new JEditorPane("text/html", "https://icons8.com/icon/91638/previous"));
                editorPanes.add(new JEditorPane("text/html", "https://icons8.com/icon/91637/next"));
                editorPanes.add(new JEditorPane("text/html", "https://icons8.com/icon/68064/trash"));
                editorPanes.add(new JEditorPane("text/html", "https://icons8.com/icon/61117/reset"));
                JPanel jp = new JPanel();
                jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
                for (JEditorPane ep : editorPanes) {
                    ep.setEditable(false);
                    ep.setBackground(jp.getBackground());
                    jp.add(ep);
                }
                JOptionPane.showMessageDialog(frame, jp, "Quellen der verwendeten Symbole",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Menue-Eintrag zum Umschalten des AutoLayouts vom (p)EG.
        menuBar.getMenu(2).getItem(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frame.getReachabilityGraphPanel().usesAutoLayout() == true) {
                    frame.getReachabilityGraphPanel().disableAutoLayout();
                    frame.appendTextOnTextArea("Erreichbarkeits-Graph - autoLayout = deaktiviert\n");
                } else {
                    frame.getReachabilityGraphPanel().enableAutoLayout();
                    frame.appendTextOnTextArea("Erreichbarkeits-Graph - autoLayout = aktiviert\n");
                }
            }
        });

        // der Button in der Tool-Bar, welcher fuer die Analyse von einem Petrinetz ist.
        JButton analyse = (JButton) toolBar.getComponent(0);
        analyse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int disposePetrinetOption = JOptionPane.showConfirmDialog(frame,
                        "Achtung: Wenn Sie die Analyse starten, wird das aktuelle Petrinetz zurückgesetzt!",
                        "aktuelles Petrinetz zurücksetzen?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                // das angezeigte Petrinetz wird auf die Anfangsmarkierung zurueckgesetzt, der
                // (p)EG wird geloescht.
                if (disposePetrinetOption == JOptionPane.OK_OPTION) {
                    int confVal = JOptionPane.showConfirmDialog(frame, "Analyse jetzt starten?", "Analyse starten?",
                            JOptionPane.YES_NO_OPTION);
                    // Analyse wird gestartet
                    if (confVal == JOptionPane.YES_OPTION) {
                        View_ReachabilityGraph rgView = frame.getReachabilityGraphView();
                        View_PetrinetGraph pgView = frame.getPetrinetGraphView();
                        Model_Petrinet p = frame.getPetrinetModel();
                        View_GraphPanel newReachabilityGraphPanel = new View_GraphPanel(rgView);
                        // Tauschen des Panels, welches den Erreichbarkeitsgraph anzeigt.
                        frame.removeReachabilityGraphPanel();
                        frame.changeReachabilityGraphPanel(newReachabilityGraphPanel);
                        // Listener fuer das Erreichbarkeits-Graph-Panel hinzufuegen
                        addListenersToViewerForReachabilityGraph(frame);
                        // Model und View zuruecksetzen auf Anfangsmarkierung
                        setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraphExceptiInitalNode(rgView, p);
                        // Beschraenktheitsanalyse durchfuehren
                        p.boundednessanalysis();
                        // Die Graphen anhand des Models neu zeichnen
                        rgView.highlightInitialNode();
                        repaintPetrinetGraph(pgView);
                        rgView.buildNodes();
                        rgView.buildEdges();
                        // den (p)EG Knoten hervorheben, von dem die Markierung am Ende der Analyse im
                        // Petrinetz angezeigt wird.
                        rgView.changeToCurrentNode();
                        // Wenn das Petrinetz unbeschraenkt ist wird hier der unbeschraenkte Pfad anhand
                        // der
                        // Vorgabe aus der Aufgabenstellung gezeichnet
                        if (p.isBounded() == false) {
                            // die ID vom Knoten m der hervorgehoben wird
                            String idOfUnlimitedNodeM = (p.getReachabilityGraphModel()
                                    .getNodeByID(p.getUnboundedPathMarkingSmaller().toString())).getID();
                            // die ID vom Knoten m' der hervorgehoben wird
                            String idOfUnlimitedNodeGreaterM = (p.getReachabilityGraphModel()
                                    .getNodeByID(p.getUnboundedPathMarkingGreater().toString())).getID();
                            Integer i = Integer.parseInt(rgView.getNumberOfPlacesAsString());
                            // Falls die Anzahl der Stellen im Petrinetz groesser als zehn ist, werden die
                            // pEG Knoten anders dargestellt.
                            if (i > 10) {
                                rgView.setNodeAttributeByNodeID(idOfUnlimitedNodeM, "ui.class",
                                        View_ReachabilityGraph.stylesheetEntryForRGNodeM);
                                rgView.setNodeAttributeByNodeID(idOfUnlimitedNodeGreaterM, "ui.class",
                                        View_ReachabilityGraph.stylesheetEntryForRGNodeGreaterM);
                            }
                            // Die Knoten werden "normal" dargestellt
                            else {
                                rgView.setNodeAttributeByNodeID(idOfUnlimitedNodeM, "ui.class",
                                        View_ReachabilityGraph.stylesheetEntryForRGNodeM
                                                + rgView.getNumberOfPlacesAsString());
                                rgView.setNodeAttributeByNodeID(idOfUnlimitedNodeGreaterM, "ui.class",
                                        View_ReachabilityGraph.stylesheetEntryForRGNodeGreaterM
                                                + rgView.getNumberOfPlacesAsString());
                            }
                            // Die Kanten die zum unbeschraenkten Pfad gehoeren, werden hervorgehoben
                            rgView.setAttributesForUnlimitedPathsArcs("ui.class",
                                    View_ReachabilityGraph.stylesheetEntryForUnlimitedPath);
                        }
                        /*
                         * Im folgenden wird die Ausgabe fuer den Textausgabe-Bereich erstellt und
                         * formatiert. Nach der Textausgabe wird ein Dialog zur Information ueber die
                         * (Un)Beschraenktheit des Petrinetzes angezeigt.
                         */
                        String ueberschriftDateiname = "Dateiname";
                        String ueberschriftBeschraenkt = "beschränkt";
                        String petrinetName = p.getName();
                        String lineBreak = "\n";
                        // Formatieren der Text-Laenge
                        if (p.getName().length() > ueberschriftDateiname.length()) {
                            ueberschriftDateiname = String.format("%1$" + -p.getName().length() + "s",
                                    ueberschriftDateiname);
                        } else if (p.getName().length() < ueberschriftDateiname.length()) {
                            petrinetName = String.format("%1$" + -ueberschriftDateiname.length() + "s", petrinetName);
                        }
                        // bereits angezeigten Text aus der Textausgabe loeschen
                        frame.removeTextFromTextArea();
                        // Ueberschriften im Textausgabe-Bereich anzeigen
                        frame.appendTextOnTextArea(ueberschriftDateiname + " | " + ueberschriftBeschraenkt + " | ");
                        // Spezifische Ueberschrift fuer ein Petrinetz (je nach
                        // beschraenkt/unbeschraenkt) im Textausgabe-Bereich anzeigen.
                        if (p.isBounded() == true) {
                            String ja = String.format("%1$" + -ueberschriftBeschraenkt.length() + "s", "ja");
                            frame.appendTextOnTextArea("Knoten / Kanten" + lineBreak);
                            frame.appendTextOnTextArea(
                                    petrinetName + " | " + ja + " | " + p.getReachabilityGraphModel().getNumberOfNodes()
                                            + " / " + p.getReachabilityGraphModel().getNumberOfArcs() + lineBreak);
                            // Dialog ueber die Beschraenktheit des Petrinetzes anzeigen
                            JOptionPane.showMessageDialog(frame, "Das Petrinetz " + p.getName() + " ist beschränkt",
                                    "Analyse-Ergebnis", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String nein = String.format("%1$" + -ueberschriftBeschraenkt.length() + "s", "nein");
                            String unlimitedPath = createTextualRepraesentationForUnlimitedPath(p);
                            String unlimitedNodeM = createTextualRepraesentationForNodeM(p);
                            String unlimitedNodeGreaterM = createTextualRepraesentationForNodeGreaterM(p);
                            frame.appendTextOnTextArea("Pfadlänge:Pfad; m, m'" + lineBreak);
                            frame.appendTextOnTextArea(petrinetName + " | " + nein + " | " + p.getUnboundedPathsSize()
                                    + ":" + unlimitedPath + "; " + unlimitedNodeM + ", " + unlimitedNodeGreaterM
                                    + lineBreak);
                            // Dialog ueber die Unbeschraenktheit des Petrinetzes anzeigen
                            JOptionPane.showMessageDialog(frame, "Das Petrinetz " + p.getName() + " ist unbeschränkt",
                                    "Analyse-Ergebnis", JOptionPane.INFORMATION_MESSAGE);
                        }
                        // fuer eine erneute Analyse, muessen die unlimited-Merkmale
                        // geloescht werden um in der Textausgabe nicht mehrfach vorzukommen
                        if (p.getTransitionIDsOfUnboundedPath() != null) {
                            p.clearUnboundedPathAndSetUnboundedPathsSizeToZero();
                        }
                        // Ausgabe, dass das auto-Layout vom (p)EG aktiviert ist
                        frame.appendTextOnTextArea("Erreichbarkeits-Graph - autoLayout = aktiviert\n");
                    }
                }
            }

            /**
             * Foramtieren der Repraesentation fuer den Knoten m' wie in der Aufgabestellung
             * gefordert.
             * 
             * @param petrinet das als unbeschraenkt identifizierte Petrinetz.
             * @return String der den Knoten m' fuer die Textausgabe repraesentiert.
             */
            private String createTextualRepraesentationForNodeGreaterM(Model_Petrinet petrinet) {
                int position = 1;
                String ungm = "(";
                for (Integer i : petrinet.getUnboundedPathMarkingGreater()) {
                    if (position == petrinet.getUnboundedPathMarkingGreater().size()) {
                        ungm = ungm.concat(i + ")");
                    } else {
                        ungm = ungm.concat(i + "|");
                    }
                    position++;
                }
                return ungm;
            }

            /**
             * Foramtieren der Repraesentation fuer den Knoten m wie in der Aufgabestellung
             * gefordert.
             * 
             * @param petrinet das als unbeschraenkt identifizierte Petrinetz.
             * @return String der den Knoten m fuer die Textausgabe repraesentiert.
             */
            private String createTextualRepraesentationForNodeM(Model_Petrinet petrinet) {
                int position = 1;
                String unm = "(";
                for (Integer i : petrinet.getUnboundedPathMarkingSmaller()) {
                    if (position == petrinet.getUnboundedPathMarkingSmaller().size()) {
                        unm = unm.concat(i + ")");
                    } else {
                        unm = unm.concat(i + "|");
                    }
                    position++;
                }
                return unm;
            }

            /**
             * Foramtieren der Repraesentation fuer den unbeschraenkten Pfad wie in der
             * Aufgabestellung gefordert.
             * 
             * @param petrinet das als unbeschraenkt identifizierte Petrinetz.
             * @return String der den unbeschraenkten Pfad fuer die Textausgabe
             *         repraesentiert.
             */
            private String createTextualRepraesentationForUnlimitedPath(Model_Petrinet petrinet) {
                int position = 1;
                String up = "(";
                for (String s : petrinet.getTransitionIDsOfUnboundedPath()) {
                    if (position == petrinet.getUnboundedPathsSize()) {
                        up = up.concat(s + ")");
                    } else {
                        up = up.concat(s + ",");
                    }
                    position++;
                }
                return up;
            }
        });

        // der Button in der Tool-Bar, welcher fuer das Laden des naechst kleineren
        // Petrinetzes im Verzeichnis ist.
        JButton letztesPetrinetz = (JButton) toolBar.getComponent(1);
        letztesPetrinetz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // itterieren ueber die Dateien aus dem aktuellen Verzeichnis
                for (File file : frame.getFileList()) {
                    // Suchen der Position der aktuellen Datei
                    if (file.equals(frame.getCurrentFile())) {
                        // die aktuelle Datei hat keinen lexikographischen Vorgaenger im Verzeichnis
                        if (frame.getFileList().indexOf(file) == 0) {
                            // Anzahl der Dateien im Verzeichnis
                            int size = frame.getFileList().size();
                            // die Datei die als naechstes geladen wird, ist die lexikographisch groesste im
                            // Verzeichnis
                            frame.setCurrentFile(frame.getFileList().get(size - 1));
                        }
                        // die aktuelle Datei hat einen lexikographischen Vorgaenger im Verzeichnis
                        else {
                            // die Datei die als naechstes geladen wird, ist die naechst kleinere Datei als
                            // die aktuelle Datei
                            frame.setCurrentFile(frame.getFileList().get(frame.getFileList().indexOf(file) - 1));
                        }
                        // String-Repraesentation fuer den absoluten Dateipfad zur naechsten Datei
                        // erstellen
                        String fileString = frame.getCurrentDirectory() + "/" + frame.getCurrentFile().getName();
                        // Das Petrinetz aus der ausgewaehlten Datei erzeugen und im Frame anzeigen.
                        displayPetrinetFromFile(frame, fileString);
                        // Um ein weiteres Itterieren ueber die Dateien im Verzeichnis zu unterbinden
                        return;
                    }
                }
            }
        });

        // der Button in der Tool-Bar, welcher fuer das Laden des naechst groesseren
        // Petrinetzes im Verzeichnis ist.
        JButton naechstesPetrinetz = (JButton) toolBar.getComponent(2);
        naechstesPetrinetz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // itterieren ueber die Dateien aus dem aktuellen Verzeichnis
                for (File file : frame.getFileList()) {
                    // Suchen der Position der aktuellen Datei
                    if (file.equals(frame.getCurrentFile())) {
                        // die aktuelle Datei hat keinen lexikographischen Nachfolger im Verzeichnis
                        if (frame.getFileList().indexOf(file) == frame.getFileList().size() - 1) {
                            // die Datei die als naechstes geladen wird, ist die lexikographisch kleinste im
                            // Verzeichnis
                            frame.setCurrentFile(frame.getFileList().get(0));
                        }
                        // die aktuelle Datei hat einen lexikographischen Nachfolger im Verzeichnis
                        else {
                            // die Datei die als naechstes geladen wird, ist die naechst groessere Datei als
                            // die aktuelle Datei
                            frame.setCurrentFile(frame.getFileList().get(frame.getFileList().indexOf(file) + 1));
                        }
                        // String-Repraesentation fuer den absoluten Dateipfad zur naechsten Datei
                        // erstellen
                        String fileString = frame.getCurrentDirectory() + "/" + frame.getCurrentFile().getName();
                        // Das Petrinetz aus der ausgewaehlten Datei erzeugen und im Frame anzeigen.
                        displayPetrinetFromFile(frame, fileString);
                        // Um ein weiteres Itterieren ueber die Dateien im Verzeichnis zu unterbinden
                        return;
                    }
                }
            }
        });

        JButton loescheEG = (JButton) toolBar.getComponent(3);
        loescheEG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                View_ReachabilityGraph rgView = frame.getReachabilityGraphView();
                View_PetrinetGraph pgView = frame.getPetrinetGraphView();
                Model_Petrinet p = frame.getPetrinetModel();
                View_GraphPanel newReachabilityGraphPanel = new View_GraphPanel(rgView);
                // Tauschen des Panels, welches den Erreichbarkeitsgraph anzeigt.
                frame.removeReachabilityGraphPanel();
                frame.changeReachabilityGraphPanel(newReachabilityGraphPanel);
                // Listener fuer das Erreichbarkeits-Graph-Panel hinzufuegen
                addListenersToViewerForReachabilityGraph(frame);
                // Model und View zuruecksetzen auf Anfangsmarkierung
                setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraphExceptiInitalNode(rgView, p);
                // Die Graphen anhand des Models neu zeichnen
                rgView.highlightInitialNode();
                repaintPetrinetGraph(pgView);
            }
        });

        JButton markeMinus = (JButton) toolBar.getComponent(4);
        markeMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model_Petrinet p = frame.getPetrinetModel();
                IPlace place = p.getHighlightedPlace();
                // Wenn keine Stelle markiert ist
                if (place == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Wählen Sie eine Stelle (Kreis) aus, bei der " + "Sie die Markierung verringern möchten",
                            "Keine Stelle ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
                }
                // eine Stelle ist markiert
                else {
                    // Die Anzahl der Marken dieser Stelle ist groesser als null und kann somit
                    // verringert werden.
                    if (place.getMarks() != 0) {
                        changeStatusLabelBecauseInitailMarkingChanged(frame);
                        // die ausgewaehlte Stelle um Eins verringern
                        place.decrementMarks();
                        View_ReachabilityGraph rgView = frame.getReachabilityGraphView();
                        View_PetrinetGraph pgView = frame.getPetrinetGraphView();
                        View_GraphPanel newReachabilityGraphPanel = new View_GraphPanel(
                                frame.getReachabilityGraphView());
                        // Tauschen des Panels, welches den Erreichbarkeitsgraph anzeigt.
                        frame.removeReachabilityGraphPanel();
                        frame.changeReachabilityGraphPanel(newReachabilityGraphPanel);
                        // Listener fuer das Erreichbarkeits-Graph-Panel hinzufuegen
                        addListenersToViewerForReachabilityGraph(frame);
                        // Die Markierung nach Aendern einer Stelle wird zur neuen Anfangsmarkierung
                        p.setNewInitialMarkingByGivenMarking(p.getCurrentMarkingByCurrentPlaceMarkings());
                        setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraph(p, rgView);
                        // Die Graphen anhand des Models neu zeichnen
                        rgView.buildNodes();
                        rgView.highlightInitialNode();
                        repaintPetrinetGraph(pgView);
                    } else {
                        // falls die Anzahl der Marken dieser Stelle bereits null ist, muss nichts getan
                        // werden
                    }
                }
            }
        });

        JButton markePlus = (JButton) toolBar.getComponent(5);
        markePlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model_Petrinet p = frame.getPetrinetModel();
                IPlace place = p.getHighlightedPlace();
                // Wenn keine Stelle markiert ist
                if (place == null) {
                    JOptionPane.showMessageDialog(frame,
                            "Wählen Sie eine Stelle (Kreis) aus, bei der " + "Sie die Markierung verringern möchten",
                            "Keine Stelle ausgewählt!", JOptionPane.INFORMATION_MESSAGE);
                }
                // eine Stelle ist markiert
                else {
                    // die ausgewaehlte Stelle um Eins erhoehen
                    place.incrementMarks();
                    View_ReachabilityGraph rgView = frame.getReachabilityGraphView();
                    View_PetrinetGraph pgView = frame.getPetrinetGraphView();
                    View_GraphPanel newReachabilityGraphPanel = new View_GraphPanel(frame.getReachabilityGraphView());
                    // Tauschen des Panels, welches den Erreichbarkeitsgraph anzeigt.
                    frame.removeReachabilityGraphPanel();
                    frame.changeReachabilityGraphPanel(newReachabilityGraphPanel);
                    // Listener fuer das Erreichbarkeits-Graph-Panel hinzufuegen
                    addListenersToViewerForReachabilityGraph(frame);
                    // Die Markierung nach Aendern einer Stelle wird zur neuen Anfangsmarkierung
                    p.setNewInitialMarkingByGivenMarking(p.getCurrentMarkingByCurrentPlaceMarkings());
                    // Model und View zuruecksetzen auf Anfangsmarkierung
                    setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraph(p, rgView);
                    // Die Graphen anhand des Models neu zeichnen
                    rgView.buildNodes();
                    rgView.highlightInitialNode();
                    repaintPetrinetGraph(pgView);
                    // Statuslabel aendern
                    changeStatusLabelBecauseInitailMarkingChanged(frame);
                }
            }
        });

        JButton reset = (JButton) toolBar.getComponent(6);
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Model_Petrinet p = frame.getPetrinetModel();
                View_PetrinetGraph pgView = frame.getPetrinetGraphView();
                resetToInitialMarkingWithEnabledTransitions(p);
                repaintPetrinetGraph(pgView);
                frame.getReachabilityGraphView().changeToCurrentNode();
            }
        });
    }

    /**
     * Fuegt dem Viewer vom Petrinetz-Graph einen {@link ClickListenerGraph}, einen
     * {@link MouseListener} und einen {@link MouseWheelListener} hinzu. <br>
     * <br>
     * 
     * Der ClickListener reagiert auf Mausklicks die am Graph geschehen. Dazu werden
     * in dieser Methode die Methoden buttonPushed und buttonReleased benutzt. <br>
     * 
     * Die MouseListener reagieren ebenfalls auf Mausklicks und noch zusaetzlich auf
     * das Mausrad. Hier wird auf Ereignisse der Sorte MouseEvent und
     * MouseWheelMoved durch Aufruf der Methoden mousePressed und mouseReleased,
     * sowie durch Aufruf der Methode mouseWheelMoved reagiert.
     * 
     * @param frame das Fenster in dem sich der Petrinetz-Graph befindet.
     */
    private void addListenersToViewerForPetrinetGraph(PetrinetFrame frame) {
        ViewerPipe viewerPipePetrinetGraphPanel = frame.getPetrinetGraphPanel().getViewerPipe();
        viewerPipePetrinetGraphPanel.addViewerListener(new ClickListenerGraph() {
            @Override
            public void buttonPushed(String id) {
                if (id.substring(0, 1).equals("t")) {
                    ITransition transition = frame.getPetrinetModel().getTransitionByID(id);
                    if (transition != null && transition.isActivated()) {
                        frame.getPetrinetGraphView().setNodeAttributeByNodeID(id, "ui.class",
                                "transitionHighlightPressed");
                    } else if (transition != null) {
                        frame.getPetrinetGraphView().setNodeAttributeByNodeID(id, "ui.class", "transitionPressed");
                    }
                } else if (id.substring(0, 1).equals("p")) {
                    if (frame.getPetrinetModel().getHighlightedPlace() != null
                            && frame.getPetrinetModel().getHighlightedPlace().getID().equals(id)) {
                        frame.getPetrinetGraphView().setNodeAttributeByNodeID(id, "ui.class", "placeHighlightPressed");
                    } else {
                        frame.getPetrinetGraphView().setNodeAttributeByNodeID(id, "ui.class", "pressed");
                    }
                }
            }

            @Override
            public void buttonReleased(String id) {
                if (id.substring(0, 1).equals("t")) {
                    ITransition transition = frame.getPetrinetModel().getTransitionByID(id);
                    if (transition != null && transition.isActivated()) {
                        frame.getPetrinetModel().switchActivatedTransition(transition);
                        frame.getPetrinetModel().buildReachabilityGraph();
                        frame.getPetrinetModel().setTransitionsActivated();
                        frame.getPetrinetGraphView().repaintPetrinetGraph(id);
                        frame.getReachabilityGraphView().buildNodes();
                        frame.getReachabilityGraphView().buildEdges();
                    } else if (transition != null) {
                        frame.getPetrinetGraphView().repaintPetrinetGraph(id);
                    }
                } else if (id.substring(0, 1).equals("p")) {
                    frame.getPetrinetGraphView().repaintPetrinetGraph(id);
                    frame.getPetrinetModel().changeStateOfHighlightedPlace(id);
                }
            }
        });
        // Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
        // Maus-Button gedrueckt oder losgelassen wird, dann wird die Methode
        // viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
        // ViewerListener zu informieren (hier ueber die ClickListenerGraph-Instanz,
        // die im Controller verwendet wird).
        frame.getPetrinetGraphPanel().getViewPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                frame.getPetrinetGraphPanel().getViewerPipe().pump();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                frame.getPetrinetGraphPanel().getViewerPipe().pump();
            }
        });
        // Zoom per Mausrad ermoeglichen
        frame.getPetrinetGraphPanel().getViewPanel().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomLevel = frame.getPetrinetGraphPanel().getViewPanel().getCamera().getViewPercent();
                if (e.getWheelRotation() == -1) {
                    zoomLevel -= 0.1;
                    if (zoomLevel < 0.1) {
                        zoomLevel = 0.1;
                    }
                }
                if (e.getWheelRotation() == 1) {
                    zoomLevel += 0.1;
                }
                frame.getPetrinetGraphPanel().getViewPanel().getCamera().setViewPercent(zoomLevel);
            }
        });
    }

    /**
     * Fuegt dem Viewer vom Erreichbarkeits-Graph einen {@link ClickListenerGraph},
     * einen {@link MouseListener} und einen {@link MouseWheelListener} hinzu. <br>
     * <br>
     * 
     * Der ClickListener reagiert auf Mausklicks die am Graph geschehen. Dazu werden
     * in dieser Methode die Methoden buttonPushed und buttonReleased benutzt. <br>
     * 
     * Die MouseListener reagieren ebenfalls auf Mausklicks und noch zusaetzlich auf
     * das Mausrad. Hier wird auf Ereignisse der Sorte MouseEvent und
     * MouseWheelMoved durch Aufruf der Methoden mousePressed und mouseReleased,
     * sowie durch Aufruf der Methode mouseWheelMoved reagiert.
     * 
     * @param frame das Fenster in dem sich der Erreichbarkeits-Graph befindet.
     */
    private void addListenersToViewerForReachabilityGraph(PetrinetFrame frame) {
        ViewerPipe viewerPipeReachabilityGraphPanel = frame.getReachabilityGraphPanel().getViewerPipe();
        viewerPipeReachabilityGraphPanel.addViewerListener(new ClickListenerGraph() {
            @Override
            public void buttonPushed(String id) {
            }

            @Override
            public void buttonReleased(String id) {
                IRGNode node = frame.getPetrinetModel().getReachabilityGraphModel().getNodeByID(id);
                for (List<Integer> marking : frame.getPetrinetModel().getMarkings()) {
                    if (marking.toString().equals(node.getID())) {
                        frame.getPetrinetModel().setCurrentPlaceMarkingsByGivenMarking(marking);
                        frame.getPetrinetModel().setTransitionsActivated();
                        frame.getPetrinetGraphView().repaintPlaces();
                        frame.getPetrinetGraphView().repaintTransitions();
                        frame.getReachabilityGraphView().changeToCurrentNode();
                    }
                }
            }
        });
        // Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
        // Maus-Button gedrueckt oder losgelassen wird, dann wird die Methode
        // viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
        // ViewerListener zu informieren (hier ueber die ClickListenerGraph-Instanz,
        // die im Controller verwendet wird).
        frame.getReachabilityGraphPanel().getViewPanel().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                frame.getReachabilityGraphPanel().getViewerPipe().pump();
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                frame.getReachabilityGraphPanel().getViewerPipe().pump();
            }
        });
        // Zoom per Mausrad ermoeglichen
        frame.getReachabilityGraphPanel().getViewPanel().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomLevel = frame.getReachabilityGraphPanel().getViewPanel().getCamera().getViewPercent();
                if (e.getWheelRotation() == -1) {
                    zoomLevel -= 0.1;
                    if (zoomLevel < 0.1) {
                        zoomLevel = 0.1;
                    }
                }
                if (e.getWheelRotation() == 1) {
                    zoomLevel += 0.1;
                }
                frame.getReachabilityGraphPanel().getViewPanel().getCamera().setViewPercent(zoomLevel);
            }
        });
    }

    /**
     * In dieser Methode wird fuer die Dateien die zuvor im FileChooser ausgewaehlt
     * wurden, die Beschraenktheitsanalyse durchgefuehrt. Im Anschluss wird die
     * Textausgabe erzeugt und im Fenster angezeigt.
     * 
     * @param frame   Das Fenster in dem das Ergebnis der Analyse ausgegeben wird.
     * @param toolBar Die Toolbar deren Buttons deaktiviert werden, nachdem die
     *                Analyse gestartet wurde.
     * @param chooser Der JFileChooser aus dem heraus die Dateien fuer diese Analyse
     *                stammen.
     */
    private void analyseFuerMehrereDateienUndTextausgabeImTextbereich(PetrinetFrame frame, PetrinetToolBar toolBar,
            JFileChooser chooser) {
        int confVal = JOptionPane.showConfirmDialog(frame, "Analyse jetzt starten?", "Analyse starten?",
                JOptionPane.YES_NO_OPTION);
        // Die Analyse wird gestartet
        if (confVal == JOptionPane.YES_OPTION) {
            // die Petrinetz Instanzvariable vom Frame wird in jedem Fall genullt, auch wenn
            // sie schon null ist.
            frame.setPetrinet(null);
            // setCurrentFile(null) um nicht nach einer Analyse
            // durch den Menueeintrag neu Laden, das zuletzt angezeigte
            // Petrinetz neu laden zu koennen.
            frame.setCurrentFile(null);
            // das Panel mit ggf. enthaltenen Graphen wir durch ein leeres Panel
            // ausgetauscht;
            frame.removeBothGraphPanel();
            frame.changeBothGraphPanel(frame.newAndEmptyBothGraphPanel());
            // Text der ggf. schon in der Textausgabe steht wird geloescht.
            frame.removeTextFromTextArea();
            // die Toolbar wird inaktiv geschaltet und erst nach dem laden einer
            // neuen Datei wieder aktiviert
            toolBar.getComponent(0).setEnabled(false);
            toolBar.getComponent(1).setEnabled(false);
            toolBar.getComponent(2).setEnabled(false);
            toolBar.getComponent(3).setEnabled(false);
            toolBar.getComponent(4).setEnabled(false);
            toolBar.getComponent(5).setEnabled(false);
            toolBar.getComponent(6).setEnabled(false);
            /* Hier beginnt der Code fuer die Textausgabe */
            // die folgenden Listen sind fuer die, in der Laenge unformatierten Strings, der
            // Textausgabe.
            List<String> dateinamen = new ArrayList<String>();
            List<String> beschraenkt = new ArrayList<String>();
            List<String> alleKnoten = new ArrayList<String>();
            List<String> alleKanten = new ArrayList<String>();
            List<String> pfadlaengen = new ArrayList<String>();
            List<String> pfade = new ArrayList<String>();
            List<String> alleKnotenM = new ArrayList<String>();
            List<String> alleKnotenGroesserM = new ArrayList<String>();
            // die folgenden int Variablen sind zur Bestimmung der Laenge des jeweils
            // laengsten Strings in einer der Listen von Oberhalb und werden zur
            // Formatierung einer einheitlichen String-Laenge herangezogen
            int lengthOfLongestNamestringInAList = 0;
            int lengthOfLongestNodestringInAList = 0;
            int lengthOfLongestEdgestringInAList = 0;
            int lengthOfLongestPathsSizeStringInAList = 0;
            int lengthOfLongestPathStringInAList = 0;
            int lengthOfLongestNodeMStringInAList = 0;
            int lengthOfLongestNodeGreaterMStringInAList = 0;
            // die folgenden Listen sind fuer die Strings, die im Zuge der
            // Beschraenktheitsanalyse
            // in der Text-Area, Zeilenweise ausgegeben werden und die bereits alle in einer
            // einheitlichen Laenge formatiert wurden
            List<String> dateinamenFormatierteLaenge = new ArrayList<String>();
            List<String> beschraenktFormatierteLaenge = new ArrayList<String>();
            List<String> alleKnotenFormatierteLaenge = new ArrayList<String>();
            List<String> alleKantenFormatierteLaenge = new ArrayList<String>();
            List<String> pfadlaengenFormatierteLaenge = new ArrayList<String>();
            List<String> pfadeFormatierteLaenge = new ArrayList<String>();
            List<String> alleKnotenMFormatierteLaenge = new ArrayList<String>();
            List<String> alleKnotenGroesserMFormatierteLaenge = new ArrayList<String>();
            // die folgenden Strings sind fuer die erste Zeile bei der Ausgabe im
            // Textbereich
            String ueberschriftDateiname = "Dateiname";
            String ueberschriftBeschraenkt = "beschränkt";
            String ueberschriftElemente = "Knoten / Kanten bzw. Pfadlänge:Pfad; m, m'";
            // der folgende String ist fuer die Zeilenumbrueche bei der erzeugung der
            // Textausgabe
            String lineBreak = "\n";
            // der folgende int-Wert ist fuer die Menge der Zeichen der laengsten Zeile der
            // Textausgabe vorgesehen.
            // Er wird benoetigt um die Anzahl der Bindestriche zu zaehlen, die in der
            // Textausgabe
            // die ueberschrift vom Rest der Daten trennt
            int longestRow;
            // der folgende String ist fuer die Trennung zwischen Ueberschrift und uebrige
            // Daten
            String stringOfMinusesToDivideHeadlineFromNormalData = "";
            // In der For-Schleife wird ueber die Files die im Chooser ausgewaehlt wurden
            // nacheinander iteriert.
            // Die Beschraenktheitsanalyse wird ausgefuehrt und die Strings fuer die
            // Ausgaben werden vom
            // Petrinetz-Model geholt und in den dafuer vorgesehenen Listen gespeichert.
            for (File file : chooser.getSelectedFiles()) {
                String fileString = chooser.getCurrentDirectory() + "/" + file.getName();
                Model_Petrinet p = (Model_Petrinet) ExtendedPNMLWopedParser.getParserForFilepath(fileString)
                        .getPetrinet();
                frame.appendTextOnTextArea(p.getName() + " wird analysiert...\n");
                // Beschraenktheitsanalyse fuer das aktuelle Petrinetz
                p.boundednessanalysis();
                /*
                 * In der If-else-Bedingung wird zwischen beschraenktem und unbeschraenktem
                 * Petrinetz unterschieden, da sich hier auch die Textausgabe ein wenig
                 * unterscheidet.
                 */
                // das Petrinetz ist beschraenkt
                if (p.isBounded()) {
                    // Hier werden die Textausgabe-Elemente in den dafuer vorgesehenen Listen
                    // gespeichert.
                    // die Laenge der Elemente wird kontrolliert und falls ein neues Maximum
                    // festgestellt wird, so wird die dafuer vorgesehene int-variable
                    // neu belegt. Hinweis: die Textausgabe-Elemente sind danach noch nicht
                    // einheitlich formatiert.
                    if (p.getName().length() > lengthOfLongestNamestringInAList) {
                        lengthOfLongestNamestringInAList = p.getName().length();
                    }
                    // Name vom Petrinetz hinzufuegen
                    dateinamen.add(p.getName());
                    // ja fuer beschraenkt hinzufuegen
                    beschraenkt.add("ja"); // Achtung bei Aenderungen! der Wert wird weiter unten (beim "Befuellen" der
                                           // Textausgabe) verglichen.
                    Integer knotenZahl = (p.getReachabilityGraphModel().getNumberOfNodes());
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (knotenZahl.toString().length() > lengthOfLongestNodestringInAList) {
                        lengthOfLongestNodestringInAList = knotenZahl.toString().length();
                    }
                    // Anzahl der Knoten von diesem Petrinetz hinzufuegen.
                    alleKnoten.add(knotenZahl.toString());
                    Integer kantenZahl = (p.getReachabilityGraphModel().getNumberOfArcs());
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (kantenZahl.toString().length() > lengthOfLongestEdgestringInAList) {
                        lengthOfLongestEdgestringInAList = kantenZahl.toString().length();
                    }
                    // Anzahl der Kanten von diesem Petrinetz hinzufuegen.
                    alleKanten.add(kantenZahl.toString());
                }
                // das Petrinetz ist unbeschraenkt
                else {
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (p.getName().length() > lengthOfLongestNamestringInAList) {
                        lengthOfLongestNamestringInAList = p.getName().length();
                    }
                    // Name vom Petrinetz hinzufuegen
                    dateinamen.add(p.getName());
                    // nein fuer unbeschraenkt zur Liste hinzufuegen
                    beschraenkt.add("nein");
                    Integer pfadlaenge = p.getUnboundedPathsSize();
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (pfadlaenge.toString().length() > lengthOfLongestPathsSizeStringInAList) {
                        lengthOfLongestPathsSizeStringInAList = pfadlaenge.toString().length();
                    }
                    // Hinzufuegen der Pfadlaenge vom unbeschraenkten Pfad
                    pfadlaengen.add(pfadlaenge.toString());
                    int laufvariable = 0;
                    // Die Praesentation des unbeschraenkten Pfads in der Textausgabe, anhand
                    // der Vorgaben in der Aufgabenstellung formatieren.
                    String pfadAusTransitionIDs = "(";
                    for (String string : p.getTransitionIDsOfUnboundedPath()) {
                        if (p.getTransitionIDsOfUnboundedPath().size() == laufvariable + 1) {
                            pfadAusTransitionIDs = pfadAusTransitionIDs.concat(string + ");");
                        } else {
                            pfadAusTransitionIDs = pfadAusTransitionIDs.concat(string + ",");
                        }
                        laufvariable++;
                    }
                    laufvariable = 0;
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (pfadAusTransitionIDs.length() > lengthOfLongestPathStringInAList) {
                        lengthOfLongestPathStringInAList = pfadAusTransitionIDs.length();
                    }
                    // Textuelle Repraesentation des unbeschraenkten Pfades hinzufuegen
                    pfade.add(pfadAusTransitionIDs);
                    // Die Praesentation der Markierung m in der Textausgabe, anhand
                    // der Vorgaben in der Aufgabenstellung formatieren.
                    String markierungM = "(";
                    for (Integer integer : p.getUnboundedPathMarkingSmaller()) {
                        if (p.getUnboundedPathMarkingSmaller().size() == laufvariable + 1) {
                            markierungM = markierungM.concat(integer.toString() + "),");
                        } else {
                            markierungM = markierungM.concat(integer.toString() + "|");
                        }
                        laufvariable++;
                    }
                    laufvariable = 0;
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (markierungM.length() > lengthOfLongestNodeMStringInAList) {
                        lengthOfLongestNodeMStringInAList = markierungM.length();
                    }
                    // Textuelle Repraesentation der Markierung m hinzufuegen
                    alleKnotenM.add(markierungM);
                    // Die Praesentation der Markierung m' in der Textausgabe, anhand
                    // der Vorgaben in der Aufgabenstellung formatieren.
                    String markierungGroesserM = "(";
                    for (Integer integer : p.getUnboundedPathMarkingGreater()) {
                        if (p.getUnboundedPathMarkingGreater().size() == laufvariable + 1) {
                            markierungGroesserM = markierungGroesserM.concat(integer.toString() + ")");
                        } else {
                            markierungGroesserM = markierungGroesserM.concat(integer.toString() + "|");
                        }
                        laufvariable++;
                    }
                    // hier wird wieder die Laenge des laengsten Elements festgestellt.
                    if (markierungGroesserM.length() > lengthOfLongestNodeGreaterMStringInAList) {
                        lengthOfLongestNodeGreaterMStringInAList = markierungGroesserM.length();
                    }
                    // Textuelle Repraesentation der Markierung m' hinzufuegen
                    alleKnotenGroesserM.add(markierungGroesserM);
                }
            }
            // Falls keiner der Dateinamen die Laenge seiner Ueberschrift uebertrifft,
            // wird die Mindestlaenge aller Dateinamen, die Laenge der Ueberschrift.
            // Andernfalls wird die Laenge der Ueberschrift an die Laenge des laengsten
            // Dateinamens angepasst
            if (lengthOfLongestNamestringInAList < ueberschriftDateiname.length()) {
                lengthOfLongestNamestringInAList = ueberschriftDateiname.length();
            } else {
                ueberschriftDateiname = String.format("%1$" + -lengthOfLongestNamestringInAList + "s",
                        ueberschriftDateiname);
            }
            /*
             * Die laengste Zeile in der Textausgabe wird hier ermittelt und dient
             * anschliessen als Referenzlaenge fuer alle Zeilen. Dazu wird die laengste
             * Zeile mit Daten, mit der Zeile der Ueberschrften verglichen.
             */
            if (lengthOfLongestPathsSizeStringInAList + lengthOfLongestPathStringInAList
                    + lengthOfLongestNodeMStringInAList
                    + lengthOfLongestNodeGreaterMStringInAList < ueberschriftElemente.length()) {
                int laengeDerLaengstenElementeDerUnbeschraenktenKnoten = lengthOfLongestPathsSizeStringInAList
                        + lengthOfLongestPathStringInAList + lengthOfLongestNodeMStringInAList
                        + lengthOfLongestNodeGreaterMStringInAList;
                int differenz = ueberschriftElemente.length() - laengeDerLaengstenElementeDerUnbeschraenktenKnoten;
                lengthOfLongestNodeGreaterMStringInAList = lengthOfLongestNodeGreaterMStringInAList + differenz;
            } else {
                ueberschriftElemente = String.format(
                        "%1$" + -(lengthOfLongestPathsSizeStringInAList + lengthOfLongestPathStringInAList
                                + lengthOfLongestNodeMStringInAList + lengthOfLongestNodeGreaterMStringInAList) + "s",
                        ueberschriftElemente);
            }
            /*
             * Formatieren aller Textausgabe Elemente. Ein Minus als Vorzeichen bei den
             * Laengen bedeuted, dass vor dem jeweiligen String, ein Leerzeichen-String als
             * Suffix angehaengt wird. Ein Plus bedeuted , dass nach dem jeweiligen String,
             * ein Leerzeichen-String als Praefix angehaengt wird.
             */
            // Formatieren der Laengen der Dateinamen
            for (String string : dateinamen) {
                string = String.format("%1$" + -lengthOfLongestNamestringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                dateinamenFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der der Strings fuer beschraenkt: ja/nein
            for (String string : beschraenkt) {
                string = String.format("%1$" + -ueberschriftBeschraenkt.length() + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                beschraenktFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Knotenanzahl
            for (String string : alleKnoten) {
                string = String.format("%1$" + lengthOfLongestNodestringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                alleKnotenFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Kantenanzahl
            for (String string : alleKanten) {
                string = String.format("%1$" + lengthOfLongestEdgestringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                alleKantenFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Pfadlaengen
            for (String string : pfadlaengen) {
                string = String.format("%1$" + lengthOfLongestPathsSizeStringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                pfadlaengenFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Pfade
            for (String string : pfade) {
                string = String.format("%1$" + -lengthOfLongestPathStringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                pfadeFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Knoten M
            for (String string : alleKnotenM) {
                string = String.format("%1$" + -lengthOfLongestNodeMStringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                alleKnotenMFormatierteLaenge.add(string);
            }
            // Formatieren der Laengen der Knoten M-String
            for (String string : alleKnotenGroesserM) {
                string = String.format("%1$" + -lengthOfLongestNodeGreaterMStringInAList + "s", string);
                // Einfuegen in die Liste mit den bereits formatierten Elementen
                alleKnotenGroesserMFormatierteLaenge.add(string);
            }
            // Konsolenausgabe
            // longestRow ist die Menge der Bindestriche, die zwischen ueberschrift und
            // Daten ausgegeben werden
            longestRow = lengthOfLongestNamestringInAList + ueberschriftBeschraenkt.length()
                    + lengthOfLongestPathsSizeStringInAList + lengthOfLongestPathStringInAList
                    + lengthOfLongestNodeMStringInAList + lengthOfLongestNodeGreaterMStringInAList;
            // Erstellen der Trennzeile
            for (int i = 0; i <= longestRow; i++) {
                if (i == lengthOfLongestNamestringInAList
                        || i == lengthOfLongestNamestringInAList + ueberschriftBeschraenkt.length() + 1) {
                    stringOfMinusesToDivideHeadlineFromNormalData = stringOfMinusesToDivideHeadlineFromNormalData
                            .concat(" | ");
                } else {
                    stringOfMinusesToDivideHeadlineFromNormalData = stringOfMinusesToDivideHeadlineFromNormalData
                            .concat("-");
                }
            }
            frame.appendTextOnTextArea("-----------------------------------\n");
            // Ausgabe der Zeile mit den Ueberschriften
            frame.appendTextOnTextArea(
                    ueberschriftDateiname + " | " + ueberschriftBeschraenkt + " | " + ueberschriftElemente + lineBreak);
            // Ausgabe der Trennzeile zwischen Ueberschrift und Daten
            frame.appendTextOnTextArea(stringOfMinusesToDivideHeadlineFromNormalData + lineBreak);
            /*
             * Fuer die Ausgabe im Textbereich wird nun auf die verschiedenen Listen mit
             * Textausgabe-Elementen zugegriffen. Damit keine Fehlzugriffe beim hohlen von
             * Elementen in den jeweiligen Listen entstehen, werden hier verschiedene
             * Laufvariablen definiert. Eine fuer die Menge der unbeschraenkten Petrinetze,
             * eine fuer die Menge der beschraenkten Petrinetze und eine fuer die
             * Vereinigung der beiden Mengen. Die letzte Laufvariable wird definiert, da es
             * Textausgabe-Elemente gibt, die fuer beide Mengen benoetigt werde.
             */
            int laufvariableAllgemein = 0;
            int laufvariableBeschraenkt = 0;
            int laufvariableUnbeschraenkt = 0;
            for (int i = 0; i < dateinamenFormatierteLaenge.size(); i++) {
                frame.appendTextOnTextArea(dateinamenFormatierteLaenge.get(laufvariableAllgemein) + " | "
                        + beschraenktFormatierteLaenge.get(laufvariableAllgemein) + " | ");
                // Um immer die zusammengehoerenden Strings in einer Zeile auszugeben,
                // benoetigt man wieder eine Fallunterscheidung, die anhand der
                // beschraenkt-Strings erfolgt.
                if (beschraenktFormatierteLaenge.get(laufvariableAllgemein).startsWith("ja")) {
                    frame.appendTextOnTextArea(alleKnotenFormatierteLaenge.get(laufvariableBeschraenkt) + "/"
                            + alleKantenFormatierteLaenge.get(laufvariableBeschraenkt) + lineBreak);
                    laufvariableBeschraenkt++;
                } else {
                    frame.appendTextOnTextArea(pfadlaengenFormatierteLaenge.get(laufvariableUnbeschraenkt) + ":"
                            + pfadeFormatierteLaenge.get(laufvariableUnbeschraenkt) + " "
                            + alleKnotenMFormatierteLaenge.get(laufvariableUnbeschraenkt) + " "
                            + alleKnotenGroesserMFormatierteLaenge.get(laufvariableUnbeschraenkt) + lineBreak);
                    laufvariableUnbeschraenkt++;
                }
                laufvariableAllgemein++;
            }
        }
    }

    /**
     * Veraendert den Text im Statuslabel anhand der Tatsache, dass das angezeigt
     * Petrinetz veraendert wurde.
     * 
     * @param frame das Fenster in dem das veraenderte Petrinetz angezeigt wird.
     */
    private void changeStatusLabelBecauseInitailMarkingChanged(PetrinetFrame frame) {
        JLabel newLabel = new JLabel(
                "Die Anfangsmarkierung von: " + frame.getPetrinetModel().getName() + " wurde geaendert");
        frame.changeStatusLabel(newLabel);
        // setSize um den frame zu aktualisieren
        frame.setSize(frame.getSize().width + 1, frame.getSize().height);
        frame.setSize(frame.getSize().width - 1, frame.getSize().height);
    }

    /**
     * Veraendert den Text im Statuslabel anhand der Tatsache, dass ein Petrinetz
     * (neu-)geladen wurde.
     * 
     * @param frame das Fenster in dem das (neu-)geladene Petrinetz angezeigt wird.
     */
    private void changeStatusLabelBecauseNewPetrinetWasLoaded(PetrinetFrame frame) {
        JLabel newLabel = new JLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = "
                + System.getProperty("user.dir"));
        frame.changeStatusLabel(newLabel);
        // setSize um den frame zu aktualisieren
        frame.setSize(frame.getSize().width + 1, frame.getSize().height);
        frame.setSize(frame.getSize().width - 1, frame.getSize().height);
    }

    /**
     * Anzeigen des Petrinetzes aus der Datei die als Parameter uebergeben wird.
     * 
     * @param frame      das Fenster in dem das Petrinetz dargestellt wird.
     * @param fileString der String, der den absoluten Dateipfad zu der Datei
     *                   darstellt, die das Petrinetz enthaelt.
     */
    private void displayPetrinetFromFile(PetrinetFrame frame, String fileString) {
        // Petrinetz aus dem fileString erzeugen.
        Model_Petrinet p = (Model_Petrinet) ExtendedPNMLWopedParser.getParserForFilepath(fileString).getPetrinet();
        // Dem Frame eine Referenz auf das Petrinetz uebergeben
        frame.setPetrinet(p);
        // Die Graphen und deren Panels erzeugen
        View_PetrinetGraph newPGraph = new View_PetrinetGraph("pgraph " + p.getName(), p);
        View_ReachabilityGraph newRGraph = new View_ReachabilityGraph("rgraph " + p.getName(), p);
        View_GraphPanel newPPanel = new View_GraphPanel(newPGraph);
        View_GraphPanel newRPanel = new View_GraphPanel(newRGraph);
        // Dem Frame die Referenzen auf die Graphen und deren Panels uebergeben
        frame.setPetrinetGraph(newPGraph);
        frame.setReachabilityGraph(newRGraph);
        frame.setPetrinetGraphPanel(newPPanel);
        frame.setReachabilityGraphPanel(newRPanel);
        // Das Panel in dem die beiden Panel fuer die Graphen eingebetted sind, erzeugen
        JSplitPane bothGraphPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, newPPanel, newRPanel);
        // Anfangsbreite fuer diePanels der Graphen festlegen
        bothGraphPanel.setDividerLocation(frame.getScreenSize().width / 2);
        // Dem Frame eine Referenz auf das Panel in dem die beiden Panel fuer die
        // Graphen eingebetted sind, uebergeben
        frame.removeBothGraphPanel();
        frame.changeBothGraphPanel(bothGraphPanel);
        // Spezifischen Text im Textausgabebereich anzeigen
        String newLine = "\n";
        frame.removeTextFromTextArea();
        // Ausgabe welches Petrinetz geladen wurde
        frame.appendTextOnTextArea(p.getName() + " geladen." + newLine);
        // Ausgabe, dass das auto-Layout vom (p)EG aktiviert ist
        frame.appendTextOnTextArea("Erreichbarkeits-Graph - autoLayout = aktiviert\n");
        // Listener zum Petrinetz-Graph hinzufuegen
        addListenersToViewerForPetrinetGraph(frame);
        // Listener zum Erreichbarkeits-Graph hinzufuegen
        addListenersToViewerForReachabilityGraph(frame);
        // Systemproperties im Statuslabel anzeigen
        changeStatusLabelBecauseNewPetrinetWasLoaded(frame);
    }

    /**
     * Den Petrinetz-Graph auf die Anfangsmarkierung zuruecksetzen und den (p)EG,
     * bis auf den Anfangsknoten loeschen.
     * 
     * @param rgView   der Erreichbarkeits-Graph der geloescht wird.
     * @param petrinet das Petrinetz das von den Graphen dargestellt wird.
     */
    private void setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraphExceptiInitalNode(
            View_ReachabilityGraph rgView, Model_Petrinet petrinet) {
        petrinet.initializeNewReachabilityGraph();
        resetToInitialMarkingWithEnabledTransitions(petrinet);
        petrinet.clearMarkingsButInitialMarking();
        rgView.removeEdgesAndSprites();
        rgView.removeNodesExceptInitialNode();
    }

    /**
     * Den Petrinetz-Graph auf die Anfangsmarkierung zuruecksetzen und den (p)EG
     * loeschen.
     * 
     * @param rgView   der Erreichbarkeits-Graph der geloescht wird.
     * @param petrinet das Petrinetz das von den Graphen dargestellt wird.
     */
    private void setPetrinetGraphToInitialMarkingAndRemoveReachabilityGraph(Model_Petrinet petrinet,
            View_ReachabilityGraph rgView) {
        petrinet.initializeNewReachabilityGraph();
        resetToInitialMarkingWithEnabledTransitions(petrinet);
        petrinet.clearMarkingsButInitialMarking();
        rgView.removeEdgesAndSprites();
        rgView.removeNodes();
    }

    /**
     * Anfangsmarkierung mit aktivierten Transitionen anzeigen.
     * 
     * @param petrinet das Petrinetz das dargestellt wird.
     */
    private void resetToInitialMarkingWithEnabledTransitions(Model_Petrinet petrinet) {
        petrinet.resetPlacesToInitialMarking();
        petrinet.setTransitionsActivated();
    }

    /**
     * Neuzeichnen vom Petrinetz-Graph.
     * 
     * @param pgView der Graph der neu gezeichnet wird.
     */
    private void repaintPetrinetGraph(View_PetrinetGraph pgView) {
        pgView.repaintPlaces();
        pgView.repaintTransitions();
    }

}
