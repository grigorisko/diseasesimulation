CS351 Disease Simulation Project
Authors: Vasileios Grigorios Kourakos and Jaden Johnson

The 4 features implemented from the required features are the following:
1) History of the simulation: Displays when an agent got sick, recovered or died
2) Plot of the different agent states over time
3) Rerun simulation feature: reinitializes simulation with same configuration
4) Initial immunity: select how many agents are immune at the start

To run the application, add the name of the config text file in the command line arguments
Example: java -jar simulation.jar config.txt
If no file is specified, the simulation will run with the default values from
the project description.

Config considerations:
The width and height are measured in pixels, anything over 700x700 will overlap other elements
as they are not scaled.
Additionally, the grid option ignores the width/height options and creates a grid of r rows
and c columns with each cell being size exposuredistance*exposuredistance.
Too many rows/columns or too high of an exposuredistance will not work with the GUI as
the simulation will extend too far.
The incubation/sickness times are interpreted as days, and each day is 1 second (1000ms) in the simulation.

If an option is not specified, the application will use the default value as specified in the
project description. If additional options that are not supported are in the config file,
they will be ignored.

The random config option randomly places n agents within the width/height specified.

The grid config option creates a grid of r rows and c columns and places one agent
in each grid cell, with their distance being equal to the exposure distance.

The randomgrid config option creates a grid of r rows and c columns and places agents
randomly in the cells. We thought this option only made sense if the number of agents n is
less than or equal to the total number of cells. If n is higher than the number of cells,
the simulation will not begin. This way some random cells may be empty
at the start. I'm not sure if this is what the project description meant
but otherwise it seems no different than the random or grid options.
The exposure distance should be at least width/columns or height/rows otherwise no agents will be neighbors

If more than one of the above configurations is specified, the last option in the config file will be used.