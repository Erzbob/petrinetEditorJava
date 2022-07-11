package petrinet_main;

import java.awt.Toolkit;

import petrinet_gui.PetrinetFrame;

/**
 * Die Klasse die die Main-Methode zur Ausfuehrung des Petrinetz-Programms enthaelt.
 * 
 * @author Richard Herzog, 3208133
 *
 */
public class Petrinetz_Editor_Main {
    
    /**
     * In der main-Methode wird die grafische Benutzeroberflaeche des Programms
     * gestartet.
     * 
     * @param args
     *            wird nicht genutzt
     */
    public static void main(String[] args) {
        // festlegen, dass der Swing Viewer verwendet werden soll 
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("sun.java2d.uiScale", "1.0");
        // System Properties ausgegeben
        System.out.println("Aktuelles Arbeitsverzeichnis: " + System.getProperty("user.dir"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("ScreenSize = " + Toolkit.getDefaultToolkit().getScreenSize());
        System.out.println("ScreenResolution = " + Toolkit.getDefaultToolkit().getScreenResolution());
        // Frame erzeugen
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PetrinetFrame("Petrinetz_Editor");
            }
        });
    }
}
    
    
