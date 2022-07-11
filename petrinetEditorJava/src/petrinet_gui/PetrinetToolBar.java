package petrinet_gui;

import java.net.URL;
import javax.swing.*;

/**
 * Dies ist die Klasse, die die Werkzeug-Leiste, inklusive aller Eintraege beschreibt,
 * die im Zuge der Erstellung des Programms fuer das ProPra benoetigt wird. <br><br> 
 * 
 * Diese Klasse erweitert die Klasse {@link JToolBar}. <br><br> 
 * 
 * Beim Erzeugen eines {@link PetrinetToolBar}-Objekts wird die komplette Werkzeug-Leiste,
 * inklusive aller Eintraege erstellt.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class PetrinetToolBar extends JToolBar {
    
    /**
     * Erzeugen und initialisieren eines {@linkplain PetrinetToolBar}-Objekts, mit 
     * allen Eintraegen.
     */
    public PetrinetToolBar() {
        // analyse = Button 0
        JButton analyse = makeButton("icons8-mechanistic-analysis-30.png", "Beschränktheitsanalyse für das aktuelle Petrinetz", "Analyse");
        analyse.setEnabled(false);
        this.add(analyse);
        // letztesNetz = Button 1
        JButton letztesNetz = makeButton("icons8-previous-30.png", "nächstes Petrinetz im Verzeichnis (alphabetisch absteigend). "
                + "Existiert kein kleineres, wird das grösste im Verzeichnis geladen.", "Nächstes (alphabetisch kleiner");
        letztesNetz.setEnabled(false);
        this.add(letztesNetz);
        // naechstesNetz = Button 2
        JButton naechstesNetz = makeButton("icons8-next-30.png", "nächstes Petrinetz im Verzeichnis (alphabetisch aufsteigend). "
                + "Existiert kein grösseres, wird das kleinste im Verzeichnis geladen.", "Nächstes (alphabetisch grösser");
        naechstesNetz.setEnabled(false);
        this.add(naechstesNetz);
        // loescheEG = Button 3
        JButton loescheEG = makeButton("icons8-trash-can-30.png", "Erreichbarkeitsgraph löschen", "Lösche EG");
        loescheEG.setEnabled(false);
        this.add(loescheEG);
        // markeMinus = Button 4
        JButton markeMinus = makeButton("marke_minus.png", "Marken einer Stelle verringern", "Marke -");
        markeMinus.setEnabled(false);
        this.add(markeMinus);
        // markePlus = Button 5
        JButton markePlus = makeButton("marke_plus.png", "Marken einer Stelle erhöhen", "Marke +");
        markePlus.setEnabled(false);
        this.add(markePlus);
        // reset = Button 6
        JButton reset = makeButton("icons8-reset-30.png", "Zur Anfangsmarkierung wechseln", "reset");
        reset.setEnabled(false);
        this.add(reset);
    }
    
    /**
     * Hinweis: diese Methode orientiert sich stark an der Methode makeNavigationButton aus dem Java Tutorial "How to Use Tool Bars", zu finden unter dem Link: "https://docs.oracle.com/javase/tutorial/uiswing/components/toolbar.html".
     * 
     * Erzeugen und initialisieren eines Eintrags fuer diese Werkzeug-Leiste.
     * 
     * @param fileName
     *          der Dateiname des Bildes inklusive Dateiendung.
     * @param toolTipText
     *          der Text der angezeigt wird, wenn man die Maus ueber den ToolBar-Eintrag zieht.
     * @param altText
     *          der Text der auf dem ToolBar-Eintrag angezeigt wird, falls kein Bild gefunden wird.
     * @return 
     *          ein {@link JButton} der den uebergebenen Parametern entspricht.
     */         
    private JButton makeButton(String fileName, String toolTipText, String altText) {
        // Hinweis: diese Methode orientiert sich stark an der Methode makeNavigationButton aus dem Java Tutorial "How to Use Tool Bars", zu finden unter dem Link: "https://docs.oracle.com/javase/tutorial/uiswing/components/toolbar.html".
        // URL mit dem Speicherort des Bildes erstellen
        String imgLocation = "/images/" + fileName;
        URL imageURL = PetrinetToolBar.class.getResource(imgLocation);
        // Erstellen und initialisieren eines JButton-Objekts
        JButton button = new JButton();
        button.setToolTipText(toolTipText);
        // dem JButton ein Bild hinzufuegen.
        if (imageURL == null) { 
            // Bild nicht gefunden
            button.setText(altText);
            System.err.println("Resource not found: " + imgLocation);
        } else { 
            // Bild gefunden
            button.setIcon(new ImageIcon(imageURL, altText));
        }
        return button;
    }
}