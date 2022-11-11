package simulation;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static java.lang.Thread.sleep;

/**
 * This class handles each of the agents and the
 * updates based on exposure
 * Implements runnable and runs on a new thread
 * when created in Display
 */
public class Agent implements Runnable {
    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    private int agentID;
    private boolean alive = true;
    private boolean exposed = false;
    private boolean immune = false;
    private boolean sick = false;
    private Display display;
    private Circle circle = new Circle(10,10,5);
    private List<Agent> neighbors = new ArrayList<>();

    /**
     * Constructor for the agent class
     * @param x the x position of the agent
     * @param y the y position of the agent
     * @param display the application display
     * @param ID the agent ID number
     */
    public Agent(int x,int y,Display display,int ID) {
        this.display=display;
        this.agentID = ID;
        circle.setCenterX(x);
        circle.setCenterY(y);
        //set the color to green for vulnerable
        Platform.runLater(()-> {

            circle.setFill(Color.rgb(0,255,0));
        });
    }

    /**
     * This is the run method for the agent.
     * This method keeps running until the alive boolean
     * is set to false. While the agent is alive,
     * check for messages to the BlockingQueue and
     * parse them accordingly
     */
    @Override
    public void run() {
        while(alive) {
            try {
                //wait for Queue message
                Integer message = queue.take();
                //Message 0 = Agent is initially sick
                if(message==0) {
                    //set color to red
                    Platform.runLater(()-> {
                        circle.setFill(Color.rgb(255,0,0));
                    });
                    //Added a check to prevent events from the previous sim
                    //from being added to the restarted sim
                    if(alive) {
                        //Display event message
                        Platform.runLater(() -> {
                            display.addEvent("Agent " + agentID +
                                                " was sick at the start");
                        });
                        //set variable and adjust counters
                        sick = true;
                        Counters.incrementSick();
                        Counters.decrementVulnerable();
                    }
                }
                //Message 1 = agent was exposed to sick neighbor
                if(message==1) {
                    if (!exposed && !immune && !sick) {
                        exposed = true;
                    }
                }
                //Message 2 = Agent is immune at the start of the sim
                else if(message==2) {
                    //set variables and adjust counters
                    immune = true;
                    Counters.incrementImmune();
                    Counters.decrementVulnerable();
                    //set color to blue for immune
                    Platform.runLater(()-> {
                        circle.setFill(Color.rgb(0,0,255));
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(exposed && !immune) {
                //sleep for the incubation period
                try {
                    sleep(display.getIncubation()*1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //after incubation,set color to red
                Platform.runLater(()-> {
                    circle.setFill(Color.rgb(255,0,0));
                });
                if(alive) {
                    //Add event to the text field
                    Platform.runLater(() -> {
                        display.addEvent("Agent " + agentID +
                                        " got sick on day "
                                        + Counters.getDays());
                    });
                    //set variables and adjust counters
                    sick = true;
                    Counters.incrementSick();
                    Counters.decrementVulnerable();
                }

            }
            if(sick) {
                if(alive) {
                    //Send an exposure message to neighboring agents
                    for (Agent agent : neighbors) {
                        Platform.runLater(() -> {
                            agent.setExposed();
                        });
                    }
                    //sleep for sickness time
                    try {
                        sleep(display.getSickness() * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (alive) {
                        //generate a random number to check
                        //whether agent dies or recovers
                        double x = Math.random();
                        //if x>recover set color to black for dead
                        if (x > display.getRecover()) {
                            Platform.runLater(() -> {
                                circle.setFill(Color.rgb(0, 0, 0));
                            });
                            //add event to the textfield
                            Platform.runLater(() -> {
                                display.addEvent("Agent " + agentID +
                                        "died on day " + Counters.getDays());
                            });
                            //set variables and adjust counters
                            alive = false;
                            sick = false;
                            exposed = false;
                            Counters.decrementSick();
                            Counters.incrementDead();
                        }
                        //if agent recovers
                        else {
                            if (alive) {
                                //set color to blue for immune
                                Platform.runLater(() -> {
                                    circle.setFill(Color.rgb(0, 0, 255));
                                });
                                //add event to text field
                                Platform.runLater(() -> {
                                    display.addEvent("Agent " + agentID +
                                            " recovered on day " +
                                            Counters.getDays());
                                });
                                //set variables and adjust counters
                                Counters.decrementSick();
                                Counters.incrementImmune();
                                sick = false;
                                immune = true;
                                exposed = false;
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * Return the agents Circle
     * @return circle
     */
    public Circle getCircle() {
        return circle;
    }

    /**
     * Add a neighboring agent to this agents list
     * @param agent the neighboring agent
     */
    public void addNeighbor(Agent agent) {
        this.neighbors.add(agent);
    }

    /**
     * Return the x position of the agent
     * @return the x position
     */
    public double getXPosition() {
        return circle.getCenterX();
    }

    /**
     * Return the y position of the agent
     * @return the y position
     */
    public double getYPosition() {
        return circle.getCenterY();
    }

    /**
     * Return the immune status of the agent
     * @return immune
     */
    public boolean getImmune() {
        return  immune;
    }

    /**
     * Send an initialSick message to the
     * agent
     */
    public void initialSick() {
        queue.add(0);
    }

    /**
     * Send an exposure message to the agent
     */
    public void setExposed() {
        queue.add(1);
    }

    /**
     * Send an immunity message to the agent
     */
    public void setImmune() {
        queue.add(2);
        immune=true;
    }

    /**
     * Stop the main loop from running.
     */
    public void stop()
    {
        alive = false;
    }

}
