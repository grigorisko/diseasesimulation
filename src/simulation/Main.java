/*************************************************************************
 * Disease Simulation Project
 * Authors: Vasileios Grigorios Kourakos and Jaden Johnson
 ************************************************************************/

package simulation;

import javafx.application.Application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is the main class of the application.
 * launches the JavaFX application from the
 * Display class, gets back the command
 * line arguments and reads and parses
 * the config text file.
 */
public class Main {

    /**
     * Return the width
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the height
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Return the exposure distance
     * @return exposureDistance
     */
    public int getExposureDistance() {
        return exposureDistance;
    }

    /**
     * Return the incubation time
     * @return incubation
     */
    public int getIncubation() {
        return incubation;
    }

    /**
     * Return the sickness time
     * @return sickness
     */
    public int getSickness() {
        return sickness;
    }

    /**
     * Return the recovery chance
     * @return recover
     */
    public double getRecover() {
        return recover;
    }

    /**
     * Return the random boolean
     * @return random
     */
    public boolean isRandom() {
        return random;
    }

    /**
     * Return the number n of agents
     * @return n
     */
    public int getN() {
        return n;
    }

    /**
     * Return the grid boolean
     * @return grid
     */
    public boolean isGrid() {
        return grid;
    }

    /**
     * Return the randomGrid boolean
     * @return randomGrid
     */
    public boolean isRandomGrid() {
        return randomGrid;
    }

    /**
     * Return the initial sick number
     * @return initialSick
     */
    public int getInitialSick() {
        return initialSick;
    }

    /**
     * Return the initial immune number
     * @return initialImmune
     */
    public int getInitialImmune() {
        return initialImmune;
    }

    /**
     * Return the row number
     * @return rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Return the column number
     * @return columns
     */
    public int getColumns() {
        return columns;
    }

    //all the config variables
    private int width = 200;
    private int height = 200;
    private int exposureDistance = 20;
    private int incubation = 5;
    private int sickness = 10;
    private double recover = 0.95;
    private boolean random = true;
    private int n = 100;
    private boolean grid = false;
    private boolean randomGrid = false;
    private int initialSick = 1;
    private int initialImmune = 0;
    private int rows = 0;
    private int columns = 0;

    private String configFile = "";
    private Display display;

    /**
     * main method of the application
     * launch Display
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Display.class, args);
    }


    /**
     * This method reads and parses the config text file
     * @param display the application display
     * @param args command line arguments
     * @throws FileNotFoundException
     */
    public void start(Display display, String[] args)
                        throws FileNotFoundException {
        this.display = display;
        //get the text file name
        for(String s:args) {
            configFile=s;
        }
        String[] words;
        if(!configFile.equals("")) {
            //parse each line of the text file
            try (BufferedReader reader =
                         new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    //split line at spaces
                    words = line.split(" ");
                    //parse line based on first word
                    switch (words[0]) {
                        case "dimensions":
                            width = Integer.parseInt(words[1]);
                            height = Integer.parseInt(words[2]);
                            break;
                        case "exposuredistance":
                            exposureDistance = Integer.parseInt(words[1]);
                            break;
                        case "incubation":
                            incubation = Integer.parseInt(words[1]);
                            break;
                        case "sickness":
                            sickness = Integer.parseInt(words[1]);
                            break;
                        case "recover":
                            recover = Double.parseDouble(words[1]);
                            break;
                        case "grid":
                            grid = true;
                            rows = Integer.parseInt(words[1]);
                            columns = Integer.parseInt(words[2]);
                            random = false;
                            randomGrid = false;
                            break;
                        case "random":
                            n = Integer.parseInt(words[1]);
                            break;
                        case "randomgrid":
                            rows = Integer.parseInt(words[1]);
                            columns = Integer.parseInt(words[2]);
                            n = Integer.parseInt(words[3]);
                            randomGrid = true;
                            grid = false;
                            random = false;
                            break;
                        case "initialimmune":
                            initialImmune = Integer.parseInt(words[1]);
                            break;
                        case "initialsick":
                            initialSick = Integer.parseInt(words[1]);
                            break;

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        this.display = display;
    }
}
