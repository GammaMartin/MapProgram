# MapProgram

Note 1: This project requires that the user have Apache Maven installed.

Note 2: Enter program through MapGUI.java

This program allows users to upload a directory containing a formatted text file and image file of a map and find the shortest path between any two locations on the map. This is done by translating the text file to a graph and navigating the graph using pathfinding algorithms. There is an option for random events, which causes the edge weights in the graph to be randomly altered. If this option is unselected, an AStar algorithm is used with a Manhattan Distance heuristic. If the option is selected, then a genetic algorithm is used to adaptively solve the shortest path. A few starter maps are packaged (all images and text files taken from the Stanford Trailblazer assignment). 
