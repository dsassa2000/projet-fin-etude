package de.unihildesheim.iis.jadedemo.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A simple class that parses .col files into a graph structure
 * @author Romain Fournier 
 */
public class Graph implements Serializable {
	private int vertices = 0;
	private int edges = 0;
	private ArrayList<Vertex> vertexList;
	private ArrayList<Vertex> sortedVertexList; //Sorted list by neighbors
	private boolean directed = false;
	
	/**
	 * A vertex from a graph. This class keeps track of the neighbors of each vertex.
	 * @author Romain Fournier
	 */
	public class Vertex implements Serializable{
		private int id = 0;
		private ArrayList<Vertex> neighborhood = new ArrayList<Vertex>();
		private ArrayList<Vertex> sortedNeighborhood = new ArrayList<Vertex>();

		/**
		 * @param n the name of the vertex
		 */
		public Vertex(int id) {
			this.id = id;
		};
		
		/**
		 * Should only be called by Graph's constructor
		 */
		protected void ComputeSortedNeighborhood () {
			sortedNeighborhood.sort(new SortVertexByDegree());
		}
		
		/**
		 * Should only be called by Graph's constructor
		 */
		protected void AddEdge (Vertex b) {
			if (neighborhood.contains(b)) return;
			neighborhood.add(b);
			sortedNeighborhood.add(b);
		}
		
		/**
		 * @return the neighbor list
		 */
		public ArrayList<Vertex> Getneighborhood () {
			return neighborhood;
		}
		
		/**
		 * @return the sorted neighbor list (by neighbor's neighbor size, ascending)
		 */
		public ArrayList<Vertex> GetSortedNeighborhood () {
			return sortedNeighborhood;
		}
		
		/**
		 * @param i
		 * @return the i-th neighbor of this vertex
		 */
		public Vertex Get (int i) {
			return neighborhood.get(i);
		}
		
		/**
		 * @param i
		 * @return the i-th neighbor of this vertex with the smallest neighborhood
		 */
		public Vertex GetByneighbor (int i) {
			return sortedNeighborhood.get(i);
		} 
		
		/**
		 * @return the degree of this vertex
		 */
		public int GetDegree () {
			return neighborhood.size();
		}
		
		/**
		 * @return the name of the vertex
		 */
		public int ID() {
			return id;
		}
	
		@Override
		public String toString() {
			return "" + (id + 1);
		}
	}
	
	class SortVertexByDegree implements Comparator<Vertex> {
	    public int compare(Vertex a, Vertex b) {
	        return a.GetDegree() - b.GetDegree();
	    }
	}
	
	/**
	 * Creates a new graph from a .col file.
	 * @param path, the path of the .col file
	 * @throws IOException on IO errors and syntax errors.
	 */
	public Graph (String path) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new FileReader(path));
		int lineNb = 0;
		String line = reader.readLine();
		
		//Reads the p line
		while (line != null) {
			if (line.startsWith("p edge")) { //Param
				String[] params = line.split(" ");
				if (params.length == 4) {
					vertices = Integer.parseInt(params[2]);
					edges = Integer.parseInt(params[3]);
					break;
				} else {
					reader.close();
					throw new IOException("syntax error, wrong args count at line " + lineNb);
				}
			} else if (!line.startsWith("c")) {
				reader.close();
				throw new IOException("syntax error, expected 'p' or 'c' at line " + lineNb);
			}
			line = reader.readLine();
			lineNb++;
		}
				
		//Inits the vertices
		vertexList = new ArrayList<Vertex>(vertices);
		for (int i = 0; i < vertices; i++) {
			vertexList.add(new Vertex(i));
		}

		
		
		//Next line...
		line = reader.readLine();
		lineNb++;

		//Reads the e lines
		while (line != null) {
			if (line.startsWith("e")) { 
				String[] params = line.split(" ");
				if (params.length == 3) {
					int a = Integer.parseInt(params[1]) - 1;
					int b = Integer.parseInt(params[2]) - 1;

					vertexList.get(a).AddEdge(vertexList.get(b));
					if (!directed)
						vertexList.get(b).AddEdge(vertexList.get(a));

				} else {
					reader.close();
					throw new IOException("syntax error, wrong args count at line " + lineNb);
				}
			} else if (!line.startsWith("c")) {
				reader.close();
				throw new IOException("syntax error, expected 'e' or 'c' at line " + lineNb);
			}
			line = reader.readLine();
			lineNb++;
		}
		
		//Inits the sorted array
		sortedVertexList = new ArrayList<Vertex>(vertices);
		for (int i = 0; i < vertices; i++) {
			sortedVertexList.add(vertexList.get(i));
		}

		sortedVertexList.sort(new SortVertexByDegree());
		
		//Sorts every vertice's neighbor
		for (int i = 0; i < vertices; i++) {
			vertexList.get(i).ComputeSortedNeighborhood();
		}
		
		reader.close();
	}
	
	/**
	 * Unsorted vertex list
	 */
	public ArrayList<Vertex> GetVertexList () {
		return vertexList;
	}
	
	/**
	 * Sorted vertex list by degree (ascending)
	 */
	public ArrayList<Vertex> GetSortedVertexList () {
		return sortedVertexList;
	}
	
	/**
	 * Returns true if the graph is directed (currently, only undirected graphs are supported)
	 */
	public boolean IsDirected () {
		return directed;
	}
	
	/**
	 * @param i
	 * @return the i-th vertex
	 */
	public Vertex Get (int i) {
		return vertexList.get(i);
	}
	
	/**
	 * 
	 * @param i
	 * @return the i-th vertex with the smallest degree
	 */
	public Vertex GetByDegree (int i) {
		return sortedVertexList.get(i);
	}
	
	/**
	 * 
	 * @return the number of vertices
	 */
	public int GetVertexCount() {
		return vertices;
	}
	
	/**
	 * 
	 * @return the number of edges
	 */
	public int GetEdgeCount() {
		return edges;
	}
}
