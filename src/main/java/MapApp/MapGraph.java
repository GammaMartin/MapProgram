package MapApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;
import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.HashSet;

/*A graph-type data structure that contains information on the
 * coordinates of sites (Vertex) and the paths (DefaultWeightedEdge) connecting said sites. 
 */
public class MapGraph {
	private Graph<Vertex, DefaultWeightedEdge> graph; 
	
	/*Constructor for a MapGraph that generates a SimpleWeightedGraph
	 * from the JGraph passage based off the contents of the user's
	 * text file, which details the coordinates of each site and the
	 * lengths of the paths connecting them
	 */
	public MapGraph(File text) {
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		boolean vertexcheck = false;
		boolean edgecheck = false;
		try (Scanner scanner = new Scanner(text)) {
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (vertexcheck == true) {
					if (line.equals("")) {
						vertexcheck = false;
					}
					else {
						String delims = "[;]+";
						String[] tokens = line.split(delims);
						Vertex v = new Vertex(tokens[0], Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
						graph.addVertex(v);	
					}
				}
				if (edgecheck == true) {
					if (!line.equals("")) {
						String delims = "[;]+";
						String[] tokens = line.split(delims);
						Set<Vertex> set = graph.vertexSet();
						Vertex start = null;
						Vertex end = null;
						for (Vertex v: set) {
							if (v.getName().equals(tokens[0])) {
								start = v;
							}
							else if (v.getName().equals(tokens[1])) {
								end = v;
							}
						}
						DefaultWeightedEdge e; 
						if (start != null && end != null) {
							e = graph.addEdge(start, end);
							graph.setEdgeWeight(e, Double.parseDouble(tokens[2]));
						}
					}
				}
				if (line.equals("VERTICES")) {
					vertexcheck = true;
				}
				else if (line.equals("EDGES")) {
					edgecheck = true;
				}		
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Set<Vertex> getVertices() {
		return graph.vertexSet();
	}
	
	public Set<DefaultWeightedEdge> getEdges() {
		return graph.edgeSet();
	}
	
	public Set<DefaultWeightedEdge> getEdgesOf(Vertex v) {
		return graph.edgesOf(v);

	}
	
	public double getWeight(DefaultWeightedEdge e) {
		return graph.getEdgeWeight(e);
	}
	
	public Vertex getTarget(DefaultWeightedEdge e) {
		return graph.getEdgeTarget(e);
	}
	
	public Vertex getSource(DefaultWeightedEdge e) {
		return graph.getEdgeSource(e);
	}
	
	public void setEdgeWeight(DefaultWeightedEdge e, double value) {
		graph.setEdgeWeight(e, value);
	}	
	
	/*Retrieves the edge connecting the two vertices*/
	public DefaultWeightedEdge getEdge(Vertex sourceinput, Vertex targetinput) {
		Set<Vertex> vertices = getVertices();	
		Vertex source = sourceinput;
		Vertex target = targetinput;
		for (Vertex vertex: vertices) {
			if (sourceinput.equals(vertex)) {
				source = vertex;
			}
			else if (targetinput.equals(vertex)) {
				target = vertex;
			}
		}
		DefaultWeightedEdge edge = graph.getEdge(source, target);
		return edge;
	}
	
	/*Retrieves all neighbors of the passed Vertex */
	public Set<Vertex> getNeighbors(Vertex v) {
		Set<Vertex> neighbors = new HashSet<Vertex>();
		Set<DefaultWeightedEdge> edges = getEdgesOf(v);
		for (DefaultWeightedEdge edge: edges) {
			Vertex source = graph.getEdgeSource(edge);
			Vertex target = graph.getEdgeTarget(edge);
			if (v.equals(source)) {
				neighbors.add(target);
			}
			else if (v.equals(target)){
				neighbors.add(source);
			}
		}
		return neighbors;
	}
	
}