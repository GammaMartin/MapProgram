package MapApp;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;

/*An object containing a path from the starting point
 * in the map to the endpoint as well as the
 * the length of the path. Implements comparable so 
 * that its path length can be compared to the length
 * of other Chromosomes. Can be randomly mutated to
 * provide diversity in paths.
 */

public class Chromosome implements Comparable<Chromosome> {
	private Vector<Vertex> path;
	private Vertex goal;
	private double totaldistance;
	
	/*Constructor that randomly
	 * generates a path between the start point
	 * and the endpoint. If no path can be found, 
	 * adds the endpoint to the end of the path and 
	 * adds 1000000 to the distance, designating it as a
	 * poor path.
	 */
	public Chromosome(Vertex start, Vertex end, MapGraph graph) {
		goal = end;
		path = new Vector<Vertex>();
		Set<Vertex> searched = new HashSet<Vertex>();
		Vertex current;
		current = start;
		totaldistance = 0;
		while (!current.equals(goal)) {
			Set<Vertex> currentNeighbors = graph.getNeighbors(current);
			Set<Vertex> unsearchedNeighbors = new HashSet<Vertex>();
			for (Vertex neighbor: currentNeighbors) {
				if (!searched.contains(neighbor)) {
					unsearchedNeighbors.add(neighbor);
				}
			}
			int neighborSize = unsearchedNeighbors.size();
			if (neighborSize == 0) {
				totaldistance = totaldistance + 1000000;
				current = goal;
				searched.add(current);
				path.add(current);
			}
			else {
				int randomIndex = new Random().nextInt(neighborSize);
				int i = 0;
				for (Vertex neighbor: unsearchedNeighbors) {
					if (i == randomIndex) {
						double weight = graph.getWeight(graph.getEdge(current, neighbor));
						totaldistance = totaldistance + weight;
						current = neighbor;
						searched.add(current);
						path.add(current);
						break;
					}
					i++;
				}
			};
		}
	}
	
	public Chromosome(Vector<Vertex> path) {
		this.path = path;
	}
	
	public Vector<Vertex> getPath() {
		return path;
	}
	
	public void setPath(Vertex vertex) {
		path.add(vertex);
	}
	
	public double getDistance() {
		return totaldistance;
	}
	
	public void setDistance(double d) {
		this.totaldistance = d;
	}
	public boolean containsEdge(MapGraph graph, DefaultWeightedEdge edge) {
		Vertex source = graph.getSource(edge);
		Vertex target = graph.getTarget(edge);
		if (path.contains(source) && path.contains(target)) {
			if (path.indexOf(source) == path.indexOf(target) + 1 || path.indexOf(source) == path.indexOf(target)-1){
				return true;
			}
		}
		return false;
	}
	
	public void updateDistance(double weight) {
		totaldistance = totaldistance + weight;
	}
	
	/*Finds a random Vertex in the path that could be
	 * mutated into another within the specifications of the graph
	 * (i.e. that the Vertices coming before and after this
	 * random Vertex are still connected to the new Vertex that is
	 * produced upon mutation
	 */
	public double mutate(MapGraph graph) {
		double mutationchange = 0;
		int pathsize = path.size();
		if (pathsize > 2) {
			int pathmark = new Random().nextInt(pathsize - 2) + 1;
			Vertex mutated = path.get(pathmark);
			Vertex parent = path.get(pathmark-1);
			Vertex child = path.get(pathmark + 1);
			Set<Vertex> parentneighbors = graph.getNeighbors(parent);
			Set<Vertex> childneighbors = graph.getNeighbors(child);
			for (Vertex neighbor: parentneighbors) {
				if (childneighbors.contains(neighbor)) {
					DefaultWeightedEdge firstedge = graph.getEdge(mutated, parent);
					DefaultWeightedEdge thirdedge = graph.getEdge(neighbor, parent);
					DefaultWeightedEdge fourthedge = graph.getEdge(neighbor, child);
					mutationchange += graph.getWeight(thirdedge) + graph.getWeight(fourthedge) -
										graph.getWeight(firstedge);
					if (!child.equals(goal)) {
						DefaultWeightedEdge secondedge = graph.getEdge(mutated, child);
						mutationchange -= graph.getWeight(secondedge);
					}
					path.set(pathmark, neighbor);
					break;
				}
			}
		}
		return mutationchange;
	}
	
	@Override
	public int compareTo(Chromosome other) {
		double thisfinal = totaldistance;
		double comparisonfinal = other.getDistance();
		return Double.compare(thisfinal, comparisonfinal);
	}
	
	/*Overrides hashcode() and equals(Object o) 
	 * to prevent errors when using the Set
	 * data structure
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chromosome c = (Chromosome) o;
        return this.path.equals(c.getPath());	
	}
	
	@Override 
	public int hashCode() {
		int result = 17;
		result = 31*result + path.hashCode();
		return result;
	}
}