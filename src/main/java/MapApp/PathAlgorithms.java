 package MapApp;

import java.util.Vector;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.Random;
import org.jgrapht.graph.*;

public class PathAlgorithms {
	
	/*instance variables and constants*/
	private Vertex start;
	private Vertex end;
	private MapGraph graph;
	private Random generator = new Random();
	private static final int generations = 50;
	private int populationSize;
	private PriorityQueue<Chromosome> population;
	
	/*Constructor*/
	
	public PathAlgorithms(Vertex start, Vertex end, MapGraph graph) {
		this.start = start;
		this.end = end;
		this.graph = graph;
	}
	
	/* Implements the AStar Algorithm to find the
	 * shortest path between two points on a graph;
	 * the heuristic function is the straight distance 
	 * between the two points (known as the "Manhattan Distance")
	 */

	public Stack<Vertex> AStar() {
		/* Priority Queue of unvisited vertices; highest priority is shortest 
		 * recorded path to that vertex
		 */
		Queue<Vertex> unsearched = new PriorityQueue<Vertex>();
		/*Set of previously visited vertices*/
		Set<Vertex> searched = new HashSet<Vertex>();
		start.setDistance(0);
		unsearched.add(start);
		/* While the goal is not yet reached or
		 * otherwise there are still neighbors 
		 * yet unvisited, searches for the 
		 * shortest path between the start and goal
		 */
		while (!unsearched.isEmpty()) {
			Vertex current = unsearched.poll();
			searched.add(current);
			if (current.equals(end)) {
					break;
			}
			Set<DefaultWeightedEdge> currentEdges = graph.getEdgesOf(current);
			for (DefaultWeightedEdge edge: currentEdges) {
				Vertex neighbor;
				Vertex source = graph.getSource(edge);
				Vertex target = graph.getTarget(edge);
				if (source.equals(current)) {
					neighbor = target;
				}
				else {
					neighbor = source;
				}
				if (!searched.contains(neighbor)) {
						double tentativeDistance = current.getDistance() + graph.getWeight(edge) + neighbor.heuristicFunction(end.getX(), end.getY());
						if (tentativeDistance < neighbor.getDistance()) {
							neighbor.setPrevious(current);
							neighbor.setDistance(tentativeDistance);
							unsearched.add(neighbor);
						}
					}
				}
		}
		return reconstructPath(end);			
	}
	
	/*returns the path found by the AStar algorithm
	 * as a Stack data structure, which can be
	 * processed and displayed by the GUI
	 */
	public Stack<Vertex> reconstructPath(Vertex end) {
		Vertex current = end;
		Stack<Vertex> path = new Stack<Vertex>();
		path.push(end);
		while (current.getPrevious() != null) {
			path.push(current.getPrevious());
			current = current.getPrevious();
		}
		return path;
	}

	/*Randomly generates a population of paths (of type Chromosome)
	 * ranked by their distance from the start
	 * point to the endpoint, then evolves them for
	 * 50 generations. Every 9 generations, a random
	 * event happens, meaning that one of the edges in the
	 * graph will have its weight altered; this will change
	 * the fitness of the population. At the end, the best
	 * path (i.e. the shortest) is selected and returned as a
	 * Stack data structure
	 */
	
	public Stack<Vertex> geneticAlgorithm() {
		generatePopulation();
		for (int i = 0; i<generations; i++) {
			if (i % 9 == 0 && i>0) {
				randomEvent();
			}
			evolve();
		}
		Chromosome winner = population.poll();
		Stack<Vertex> path1 = new Stack<Vertex>();
		Vector<Vertex> path2 = winner.getPath();
		for (int i = path2.size() - 1; i >= 0; i--) {
			path1.add(path2.remove(i));
		}
		path1.add(start);
		return path1;
	}
	
	/* Generates a number of Chromosomes
	 * based off the number of vertices contained
	 * in the graph
	 */
	public void generatePopulation() {
		population = new PriorityQueue<Chromosome>();
		Set<Vertex> vertices = graph.getVertices();
		populationSize = (4*vertices.size())/5;
		while (population.size() < populationSize) {
			Chromosome individual = new Chromosome(start, end, graph);
			population.add(individual);
		}	
	}
	
	/*Picks the two shortest paths in the current population
	 * as well as two random paths from the leftovers and allows
	 * them to crossover in an attempt to produce a shortest path 
	 */
	public void evolve() {
		Stack<Chromosome> repeatedwinners = new Stack<Chromosome>();
		Vector<Chromosome> leftovers = new Vector<Chromosome>();
		Chromosome currentfirstplace;
		Chromosome currentsecondplace;
		Chromosome firstrandompick;
		Chromosome secondrandompick;
		while (repeatedwinners.size() < 1) {
			currentfirstplace = population.poll();
			currentsecondplace = population.poll();
			if (repeatedwinners.size() > 0) {
				if (repeatedwinners.peek() != currentfirstplace) {
					repeatedwinners.clear();
				}
			}
			repeatedwinners.add(currentfirstplace);
			while (!population.isEmpty()) {
				leftovers.add(population.poll());
			}
			int leftoversSize = leftovers.size();
			int[] ints = generator.ints(0,leftoversSize).distinct().limit(2).toArray();
			firstrandompick = leftovers.remove(ints[0]);
			if (ints[0] < ints[1]) {
				secondrandompick = leftovers.remove(ints[1] - 1);
			}
			else {
				secondrandompick = leftovers.remove(ints[1]);
			}
			crossover(leftovers, currentfirstplace, currentsecondplace, firstrandompick, secondrandompick);
			mutatePopulation();
			regeneratePopulation();
		}
	}
	
	/*Sets one of the weights of one of the edges
	 * in the map to a random number. Updates the 
	 * population of Chromosomes as well
	 */
	public void randomEvent() {
		Set<DefaultWeightedEdge> edges = graph.getEdges();
		int numberOfEdges = edges.size();
		int randomEdge = generator.nextInt(numberOfEdges);
		double max = 100;
		double randomweight = generator.nextDouble() * max;
		int i = 0;
		for (DefaultWeightedEdge edge: edges) {
			if (i == randomEdge) {
				double currentweight = graph.getWeight(edge);
				graph.setEdgeWeight(edge, randomweight);
				updatePopulation(edge, randomweight - currentweight);
			}
			i++;
		}
	}
	
	public void updatePopulation(DefaultWeightedEdge edge, double weight) {
		Vector<Chromosome> leftovers = new Vector<Chromosome>();
		while (!population.isEmpty()) {
			Chromosome c = population.poll();
			leftovers.add(c);
			if (c.containsEdge(graph, edge)) {
				c.updateDistance(weight);
			}
		}
		population.addAll(leftovers);
	}

	
	/*Finds a common vertex that the two
	 * parent Chromosomes have in common between
	 * their paths. Then, switches the parts before this
	 * vertex with the parts before that same vertex in the other
	 * path, creating two new children.
	 */
	private class Offspring {
		Chromosome firstchild; 
		Chromosome secondchild; 
		
		public Offspring (Chromosome parent1, Chromosome parent2) {
			Vector<Vertex> mother = parent1.getPath();
			Vector<Vertex> father = parent2.getPath();
			Vector<Vertex> commonvertices = new Vector<Vertex>();
			for (int i = 1; i<mother.size() - 1; i++) {
				for (int j = 1; j < father.size() -1; j++) {
					Vertex current = mother.get(i);
					if (current.equals(father.get(j)))  {
						commonvertices.add(current);
					}
				}
			}
			int common = commonvertices.size();
			if (common != 0 ) {
				firstchild = new Chromosome(new Vector<Vertex>());
				secondchild  = new Chromosome(new Vector<Vertex>());
				double total1 = 0;
				double total2 = 0;
				int pivotvalue = generator.nextInt(common);
				int motherpivot = mother.indexOf(commonvertices.get(pivotvalue));
				int fatherpivot = father.indexOf(commonvertices.get(pivotvalue));
				for (int i = 0; i < motherpivot; i++) {
					Vertex current = mother.get(i);
					firstchild.setPath(current);
					if (i > 0) {
						DefaultWeightedEdge edge = graph.getEdge(mother.get(i-1), current);
						total1 = total1 + graph.getWeight(edge);
					}
				}
				for (int j = 0; j < fatherpivot; j++) {
					Vertex current = father.get(j);
					secondchild.setPath(current);
					if (j > 0) {
						DefaultWeightedEdge edge = graph.getEdge(father.get(j-1), current);
						total2 = total2 + graph.getWeight(edge);
					}
				}
				for (int i = motherpivot; i < mother.size(); i++) {
					Vertex current = mother.get(i);
					Vertex vertexbefore = secondchild.getPath().lastElement();
					DefaultWeightedEdge edge = graph.getEdge(current, vertexbefore);
					if (edge == null) {
						total2 = total2 + 1000;
					}
					else {
						total2 = total2 + graph.getWeight(edge);
					}
					secondchild.setPath(current);
				}
				for (int j = fatherpivot; j < father.size(); j++) {
					Vertex current = father.get(j);
					Vertex vertexbefore = firstchild.getPath().lastElement();
					DefaultWeightedEdge edge = graph.getEdge(current, vertexbefore);
					if (edge == null) {
						total1 = total1 + 1000;
					}
					else {
						total1 = total1 + graph.getWeight(edge);
					}
					firstchild.setPath(current);
				}
				firstchild.setDistance(total1);
				secondchild.setDistance(total2);
			}
			else if (common == 0) {
				firstchild = parent1;
				secondchild  = parent2;
			}
		}
		
		public Chromosome getFirstChild() {
			return firstchild;
		}
		
		public Chromosome getSecondChild() {
			return secondchild;
		}
	}
	
	/*Creates four new children and evaluates their
	 * fitness with respect to the other members
	 * of the population
	 */
	public void crossover(Vector<Chromosome> leftovers, Chromosome currentfirstplace, Chromosome currentsecondplace,
			Chromosome firstrandompick, Chromosome secondrandompick) {
		Offspring bestkids =  new Offspring(currentfirstplace, currentsecondplace);
		Offspring randomkids = new Offspring(firstrandompick, secondrandompick);
		Queue<Chromosome> newkids = new PriorityQueue<Chromosome>();
		newkids.add(bestkids.getFirstChild());
		newkids.add(bestkids.getSecondChild());
		newkids.add(randomkids.getFirstChild());
		newkids.add(randomkids.getSecondChild());
		for (int i = 0; i<4; i++) {
			newkids.add(leftovers.get(i));
		}
		for (int i = 0; i<4; i++) {
			population.add(newkids.poll());
		}
		population.add(currentfirstplace);
		population.add(currentsecondplace);
		population.add(firstrandompick);
		population.add(secondrandompick);
		population.addAll(leftovers);
	}
	
	/* Randomly selects some of the
	 * population to be mutated
	 */
	public void mutatePopulation() {
		Vector<Chromosome> leftovers = new Vector<Chromosome>();
		for (int i = 0; i<populationSize; i++) {
			leftovers.add(population.poll());
		}
		population.add(leftovers.firstElement());
		Chromosome current;
		for (int j = 0; j < (populationSize-1)/10; j++) {
			current = leftovers.remove(generator.nextInt(leftovers.size()));
			double change =  current.mutate(graph);
			if (change != 0) {
				current.updateDistance(change);
			}
			population.add(current);
		}
		population.addAll(leftovers);
	}
	
	/*Keeps a certain amount of Chromosomes
	 * that were recognized as the best in the 
	 * last generation (the shortest paths) and 
	 * generates new Chromosomes to replace the rest
	 */
	public void regeneratePopulation() {
		int elitenumber = 1*population.size()/10;
		Chromosome[] elite = new Chromosome[elitenumber];
		for (int i = 0; i< elitenumber; i++) {
			elite[i] = population.poll();
		}
		population.clear();
		for (int i = 0; i< elitenumber; i++) {
			population.add(elite[i]);
		}
		for (int j = 0; j<populationSize - elitenumber; j++) {
			Chromosome individual = new Chromosome(start, end, graph);
			population.add(individual);
		}
	}
}