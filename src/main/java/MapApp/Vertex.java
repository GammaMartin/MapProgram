package MapApp;

import java.lang.Double;

/*A Vertex used in the MapGraph class to distinguish 
 * various points in the map. Implements the Comparable
 * interface so that during the AStar algorithm, the distance from the
 * starting point, which can be logged in an instance of Vertex, 
 * can be compared to other Vertices to access the shortest
 * path
 */
public class Vertex implements Comparable<Vertex> {
		private String name;
		private double x;
		private double y;
		private Vertex previous;
		private double distance= Double.MAX_VALUE;
		
		public Vertex(String name, double x, double y) {
			this.name = name;
			this.x = x;
			this.y = y;;
		}
		
		public double getX() {
			return x;
		}
		
		public double getY() {
			return y;
		}
		
		public String getName() {
			return name;
		}
		
		public void setDistance(double distance) {
			this.distance = distance;
		}
		
		public double getDistance() {
			return distance;
		}
		
		public void setPrevious(Vertex previous) {
			this.previous = previous;
		}
		
		public Vertex getPrevious() {
			return previous;
		}
		
		/*The equals(Object o) and hashCode() methods are overridden to
		 * prevent errors during implementation of the Set data structure.
		 */
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Vertex v = (Vertex) o;
            return this.name.equals(v.getName()) && this.x == v.getX() && this.y == v.getY();	
		}
		
		@Override 
		public int hashCode() {
			int result = 17;
			result = 31*result + name.hashCode();
			Double transformx = new Double(x);
			Double transformy = new Double(y);
			result = 31*result + transformx.hashCode();
			result = 31* result + transformy.hashCode();
			return result;
		}
		
		
		@Override
		public int compareTo(Vertex v) {
			return Double.compare(distance, v.getDistance());
		}
		
		/*Calculates the Manhattan Distance between the current position
		 * on the map and the position of the goal.
		 */
		public double heuristicFunction(double goalX, double goalY) {
			double width = x - goalX;
			double height = y - goalY;
			return Math.sqrt(Math.pow(width,  2.0) + Math.pow(height, 2.0));
		}
	}