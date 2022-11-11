package simulation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * This class is the GUI class for the JavaFX Application.
 * It handles all of the GUI setup and updates.
 */
public class Display extends Application{
    private Main main = new Main();
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

    private TextArea events;
    private XYChart.Series sickLine = new XYChart.Series();
    private XYChart.Series deadLine = new XYChart.Series();
    private XYChart.Series vulnerableLine = new XYChart.Series();
    private XYChart.Series immuneLine = new XYChart.Series();
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<Number,Number> lineChart =
            new LineChart<Number,Number>(xAxis,yAxis);

    private GridPane gridPane = new GridPane();
    private Pane pane = new Pane();
    private Button restart;
    private List<Agent> agents = new ArrayList<>();


    @Override
    public void start(Stage stage) throws FileNotFoundException {
        //Send command line arguments to main and get all
        //the config variables
        main.start(this,getParameters().getRaw().toArray(new String[0]));
        width = main.getWidth();
        height = main.getHeight();
        exposureDistance = main.getExposureDistance();
        incubation = main.getIncubation();
        sickness = main.getSickness();
        recover = main.getRecover();
        random = main.isRandom();
        n = main.getN();
        grid = main.isGrid();
        randomGrid = main.isRandomGrid();
        initialSick = main.getInitialSick();
        initialImmune = main.getInitialImmune();
        rows = main.getRows();
        columns = main.getColumns();

        Counters.setVulnerableCounter(n);

        //Event text field
        events = new TextArea();
        events.setTranslateX(850);
        events.setTranslateY(10);
        events.setEditable(false);
        events.setMaxSize(400,200);
        pane.getChildren().add(events);

        //plot
        lineChart.setTitle("Simulation");
        sickLine.setName("Sick");
        deadLine.setName("Dead");
        vulnerableLine.setName("Vulnerable");
        immuneLine.setName("Immune");
        lineChart.setMaxSize(450,450);
        lineChart.getXAxis().setLabel("Days");
        lineChart.getYAxis().setLabel("Agents");
        lineChart.getData().addAll(sickLine, deadLine, vulnerableLine,
                                    immuneLine);
        lineChart.setTranslateX(800);
        lineChart.setTranslateY(200);
        pane.getChildren().add(lineChart);

        //Restart Button
        restart = new Button("Restart");
        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                restartSimulation(stage);
            }
        });
        restart.setTranslateX(900);
        restart.setTranslateY(650);
        pane.getChildren().add(restart);

        stage.setTitle("Simulation");
        Scene scene = new Scene(pane, 1280, 720);
        stage.setScene(scene);
        stage.show();

        //Depending on config, run the correct startup method
        if(random) {
            initializeRandom();
        }
        else if(grid) {
            initializeGrid();
        }
        else if(randomGrid) {
            initializeRandomGrid();
        }

        //Start a 1second loop to update the plots
        //with new data points and increment the days
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(1000), event ->  {
                    updateCounters();
                    Counters.incrementDays();
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * This method restarts the simulation with
     * new random positions for the agents.
     * @param stage the JavaFX stage
     */
    public void restartSimulation(Stage stage) {
        //stop the current agent threads
        for(Agent agent:agents) {
            agent.stop();
        }

        //reset counters
        Counters.setDeadCounter(0);
        Counters.setSickCounter(0);
        Counters.setImmuneCounter(0);
        Counters.setVulnerableCounter(n);
        Counters.setDays(0);

        //reset text field
        events = new TextArea();
        events.setTranslateX(850);
        events.setTranslateY(10);
        events.setEditable(false);
        events.setMaxSize(400,200);

        //reset plot
        sickLine.getData().clear();
        deadLine.getData().clear();
        vulnerableLine.getData().clear();
        immuneLine.getData().clear();

        //reset pane
        pane = new Pane();
        gridPane = new GridPane();
        pane.getChildren().add(lineChart);
        pane.getChildren().add(events);
        pane.getChildren().add(restart);
        stage.setTitle("Simulation");
        Scene scene = new Scene(pane, 1280, 720);
        stage.setScene(scene);
        stage.show();

        //reset agents
        agents = new ArrayList<>();

        //choose correct startup method based on config
        if(random == true) {
            initializeRandom();
        }
        else if(grid==true) {
            initializeGrid();
        }
        else if(randomGrid==true) {
            initializeRandomGrid();
        }


    }

    /**
     * This method is the startup method if the
     * random configuration is chosen in the
     * text file, and also the default configuration
     * if not specified in the text file.
     * Creates the agents on a new Thread and
     * initializes their positions and neighbors.
     */
    public void initializeRandom() {
        //Loop to create n agents
        for(int i = 0;i<n;i++) {
            //generate random position within dimensions
            Random random = new Random();
            int upperBoundX = width-5;
            int upperBoundY = height-5;
            int x = random.nextInt(upperBoundX)+5;
            int y = random.nextInt(upperBoundY)+5;
            Agent agent = new Agent(x,y,this,i);
            //start a new thread
            Thread agentThread = new Thread(agent);
            agentThread.start();
            //get the agent's circle and add it to the pane
            Circle circle = agent.getCircle();
            pane.getChildren().add(circle);
            //add the agent to our agent list
            agents.add(agent);
        }

        //figure out each agent's neighbor based on
        //distance and exposuredistance
        for (Agent agent:agents) {
            for (Agent agent1:agents) {

                double xDistance = agent1.getXPosition()-agent.getXPosition();
                double yDistance = agent1.getYPosition()-agent.getYPosition();
                double distance = Math.sqrt((yDistance*yDistance)+
                                            (xDistance*xDistance));
                if (distance<(double)exposureDistance && !agent.equals(agent1)) {
                    agent.addNeighbor(agent1);
                }
            }

        }
        //set initialImmune immune agents
        //Not randomized since agents are already randomly positioned
        int counter = 0;
        for (Agent agent:agents) {
            if (counter<initialImmune) {
                agent.setImmune();
                counter++;
            }
        }
        //set initialSick sick agents
        //Not randomized since agents are already randomly positioned
        counter=0;
        for (Agent agent:agents) {
            if (counter<initialSick&&!agent.getImmune()) {
                agent.initialSick();
                counter++;
            }
        }
    }

    /**
     * This method is the startup method for when the "grid"
     * option is specified in the config text file.
     * Creates a gridpane of r rows and c columns and
     * places 1 agent in each cell. Distance between adjacent
     * cells is equal to exposuredistance as specified in
     * the project description.
     */
    public void initializeGrid() {
        //set row and column sizes
        for(int i=0;i<rows;i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(exposureDistance);
            gridPane.getRowConstraints().add(rowConstraints);
        }
        for(int i=0;i<rows;i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setMinWidth(exposureDistance);
            gridPane.getColumnConstraints().add(columnConstraints);
        }
        //set gridpane size and position
        gridPane.setMinSize(700,700);
        gridPane.setTranslateX(0);
        gridPane.setTranslateY(0);
        //create rows*columns agents
        Counters.setVulnerableCounter(rows*columns);
        for(int i = 0;i<rows;i++) {
            for(int j=0;j<columns;j++) {
                int x = i*exposureDistance + exposureDistance/2;
                int y = j*exposureDistance + exposureDistance/2;
                Agent agent = new Agent(x, y, this, i*columns+j);
                //start a new thread for each agent
                Thread agentThread = new Thread(agent);
                agentThread.start();
                //get agent's circle and add it to the gridpane
                Circle circle = agent.getCircle();
                gridPane.add(circle,i,j);
                //add the agent to our list of agents
                agents.add(agent);
            }
        }
        //add the gridpane to the pane
        pane.getChildren().add(gridPane);
        //generate initialSick + initialImmune random indeces
        List<Integer> initialAgents = new ArrayList<>();
        for (int i=0;i<initialSick;i++) {
            int x = (int)(Math.random()*n);
            while(initialAgents.contains(x)) {
                x = (int)(Math.random()*n);
            }
            initialAgents.add(x);
        }
        for (int i=0;i<initialImmune;i++) {
            int x = (int)(Math.random()*n);
            while(initialAgents.contains(x)) {
                x = (int)(Math.random()*n);
            }
            initialAgents.add(x);
        }
        //calculate neighbors for each agent
        for (Agent agent:agents) {
            for (Agent agent1:agents) {

                double xDistance = agent1.getXPosition()-agent.getXPosition();
                double yDistance = agent1.getYPosition()-agent.getYPosition();
                double distance = Math.sqrt((yDistance*yDistance)+
                                            (xDistance*xDistance));
                if (distance<=exposureDistance && !agent.equals(agent1)) {
                    agent.addNeighbor(agent1);
                }
            }

        }
        //Set initialSick sick agents at the start
        //of the simulation
        int next = -1;
        if(initialAgents.size()>0) {
            next = initialAgents.remove(0);
        }
        for(int counter = 0;counter<initialSick;counter++) {
            agents.get(next).initialSick();
            if(initialAgents.size()>0) {
                next = initialAgents.remove(0);
            }
        }

        //Set initialImmune immune agents at the start of
        //the simulation
        for (int counter = 0;counter<initialImmune;counter++) {
            if (counter<initialImmune) {
                agents.get(next).setImmune();
                if (initialAgents.size()>0) {
                    next = initialAgents.remove(0);
                }
            }
        }
    }

    /**
     * This method is the startup method when the "randomgrid"
     * option is specified in the config text file.
     * We thought this option is only really different
     * if n is less than rows*columns, so there are
     * some random empty cells in the grid.
     * Otherwise it would be the same as the grid
     * option with overlapping agents
     */
    public void initializeRandomGrid() {

        //set row and column constraints
        for(int i=0;i<rows;i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(height/rows);

            gridPane.getRowConstraints().add(rowConstraints);
        }
        for(int i=0;i<columns;i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setMinWidth(width/columns);

            gridPane.getColumnConstraints().add(columnConstraints);
        }
        //set gridpane size
        gridPane.setMinSize(700,700);
        gridPane.setTranslateX(0);
        gridPane.setTranslateY(0);
        //create n randomly placed agents
        if (n<=rows*columns) {
            //hashset to keep track of generated coordinates
            //and make sure there are no duplicates
            HashSet<List<Integer>> coords = new HashSet<>();
            for (int i = 0; i < n; i++) {
                int row = (int) (Math.random() * rows);
                int col = (int) (Math.random() * columns);
                while (coords.contains(List.of(row,col))) {
                    row = (int) (Math.random() * rows);
                    col = (int) (Math.random() * columns);
                }
                coords.add(List.of(row,col));
                //create agent
                int x = col*(width/columns) + (width/columns)/2;
                int y = row*(height/rows) + (height/rows)/2;
                Agent agent = new Agent(x, y, this, row*columns+col);
                //create new thread
                Thread agentThread = new Thread(agent);
                agentThread.start();
                //get agent's circle and add it to the gridpane
                Circle circle = agent.getCircle();
                gridPane.add(circle,col,row);
                //add agent to our list of agents
                agents.add(agent);
            }
        }
        //add gridpane to pane
        pane.getChildren().add(gridPane);


        //calculate each agent's neighbors
        for (Agent agent:agents) {
            for (Agent agent1:agents) {

                double xDistance = agent1.getXPosition()-agent.getXPosition();
                double yDistance = agent1.getYPosition()-agent.getYPosition();
                double distance = Math.sqrt((yDistance*yDistance)+
                                    (xDistance*xDistance));
                if (distance<=exposureDistance && !agent.equals(agent1)) {
                    agent.addNeighbor(agent1);
                }
            }

        }
        //add initialImmune immune agents at the start of the sim
        //not randomized since placement is already randomized
        int counter = 0;
        for (Agent agent:agents) {
            if (counter<initialImmune) {
                agent.setImmune();
                counter++;
            }
        }
        //add initialSick sick agents at the start of the sim
        //not randomized since placement is already randomized
        counter=0;
        for (Agent agent:agents) {
            if (counter<initialSick&&!agent.getImmune()) {
                agent.initialSick();
                counter++;
            }
        }
    }

    /**
     * This method updates the plot with new data points
     * from the Counters class
     */
    public void updateCounters() {
        sickLine.getData().add(new XYChart.Data
                (Counters.getDays(),Counters.getSickCounter()));
        deadLine.getData().add(new XYChart.Data
                (Counters.getDays(),Counters.getDeadCounter()));
        vulnerableLine.getData().add(new XYChart.Data
                (Counters.getDays(),Counters.getVulnerableCounter()));
        immuneLine.getData().add(new XYChart.Data
                (Counters.getDays(),Counters.getImmuneCounter()));
    }

    /**
     * This method adds an event to the
     * event text field
     * @param string The string to append
     */
    public void addEvent(String string) {
        events.appendText(string+"\n");
    }


    /**
     * This method returns the incubation time variable
     * @return incubation
     */
    public int getIncubation() {
        return incubation;
    }

    /**
     * This method returns the sickness time variable
     * @return sickness
     */
    public int getSickness() {
        return sickness;
    }

    /**
     * This method returns the recover chance variable
     * @return recover
     */
    public double getRecover() {
        return recover;
    }
}
