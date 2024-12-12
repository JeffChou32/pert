package jxc033200;

import jxc033200.Graph;
import jxc033200.Graph.Vertex;
import jxc033200.Graph.Edge;
import jxc033200.Graph.GraphAlgorithm;
import jxc033200.Graph.Factory;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
    LinkedList<Vertex> finishList;
	
    public static class PERTVertex implements Factory {
	// Add fields to represent attributes of vertices here
	public int earliestStart, earliestFinish, latestStart, latestFinish, slack, duration;
	
	public PERTVertex(Vertex u) {
	}
	public PERTVertex make(Vertex u) { return new PERTVertex(u); }
    }

    // Constructor for PERT is private. Create PERT instances with static method pert().
    private PERT(Graph g) {
	super(g, new PERTVertex(null));
    }

    public void setDuration(Vertex u, int d) {
		PERTVertex v = get(u);
		v.earliestStart = 0; 
		v.earliestFinish = 0; 
		v.latestStart = Integer.MAX_VALUE;
		v.latestFinish = Integer.MAX_VALUE;
		v.slack = Integer.MAX_VALUE;
		v.duration = d;
    }

    // Implement the PERT algorithm. Returns false if the graph g is not a DAG.
    public boolean pert() {
		// Step 1: Get topological order
		LinkedList<Vertex> topOrder = topologicalOrder();
		if (topOrder == null) {
			return false; // Graph is not a DAG
		}

		// Step 2: Forward pass - Calculate earliest start and finish times
		for (Vertex u : topOrder) {
			PERTVertex pu = get(u);

			// Earliest finish time = Earliest start time + duration
			pu.earliestFinish = pu.earliestStart + getDuration(u);

			// Update earliest start time for adjacent vertices
			for (Edge e : g.outEdges(u)) {
				Vertex v = e.toVertex();
				PERTVertex pv = get(v);
				pv.earliestStart = Math.max(pv.earliestStart, pu.earliestFinish);
			}
		}

		// Step 3: Calculate Critical Path Length (CPL)
		int CPL = 0;
		for (Vertex u : g) {
			PERTVertex pu = get(u);
			CPL = Math.max(CPL, pu.earliestFinish);
		}

		// Step 4: Backward pass - Initialize latest finish times
		for (Vertex u : g) {
			PERTVertex pu = get(u);
			pu.latestFinish = CPL; // Initialize latest finish time to CPL
		}

		// Backward pass to calculate latest start and finish times
		for (int i = topOrder.size() - 1; i >= 0; i--) {
			Vertex u = topOrder.get(i);
			PERTVertex pu = get(u);

			// Latest start time = Latest finish time - duration
			pu.latestStart = pu.latestFinish - getDuration(u);

			// Update latest finish time for adjacent vertices
			for (Edge e : g.inEdges(u)) {
				Vertex v = e.fromVertex();
				PERTVertex pv = get(v);
				pv.latestFinish = Math.min(pv.latestFinish, pu.latestStart);
			}
		}

		// Step 5: Calculate slack for all vertices
		for (Vertex u : g) {
			PERTVertex pu = get(u);
			pu.slack = pu.latestFinish - pu.earliestFinish;
		}

		return true; // Successfully computed PERT
    }
	
	private int getDuration(Vertex u) {
		return get(u).duration; 
	}

    // Find a topological order of g using DFS
    LinkedList<Vertex> topologicalOrder() {
		finishList = new LinkedList<>();
    	HashSet<Vertex> visited = new HashSet<>();
		for (Vertex v : g) {
			if (!visited.contains(v)) {
				dfs(v, visited);
			}
		}		
		return finishList;
    }

	void dfs(Vertex v, HashSet<Vertex> visited) {
		visited.add(v);
		for (Edge e : g.outEdges(v)) {
			Vertex k = e.toVertex();
			if (!visited.contains(k)) {
				dfs(k, visited);
			}
		}
		finishList.addFirst(v);
	}



    // The following methods are called after calling pert().

    // Earliest time at which task u can be completed
    public int ec(Vertex u) {
		return get(u).earliestFinish;
    }

    // Latest completion time of u
    public int lc(Vertex u) {
		return get(u).latestFinish;
    }

    // Slack of u
    public int slack(Vertex u) {
		return get(u).latestFinish - get(u).earliestFinish;
    }

    // Length of a critical path (time taken to complete project)
    public int criticalPath() {
		int max = 0;
		for (Vertex u : g) {
			if (critical(u)) {
				max = Math.max(max, ec(u));
			}
		}
		return max;
    }

    // Is u a critical vertex?
    public boolean critical(Vertex u) {
		return slack(u) == 0;
    }

    // Number of critical vertices of g
    public int numCritical() {
		int count = 0;
		for (Vertex u : g) {
			if (critical(u)) {
				count++;
			}
		}
		return count;
    }

    /* Create a PERT instance on g, runs the algorithm.
     * Returns PERT instance if successful. Returns null if G is not a DAG.
     */
    public static PERT pert(Graph g, int[] duration) {
	PERT p = new PERT(g);
	for(Vertex u: g) {
	    p.setDuration(u, duration[u.getIndex()]);
	}
	// Run PERT algorithm.  Returns false if g is not a DAG
	if(p.pert()) {
	    return p;
	} else {
	    return null;
	}
    }
    
    public static void main(String[] args) throws Exception {
	String graph = "10 13   1 2 1   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1";
	Scanner in;
	// If there is a command line argument, use it as file from which
	// input is read, otherwise use input from string.
	in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
	Graph g = Graph.readDirectedGraph(in);
	g.printGraph(false);

	int[] duration = new int[g.size()];
	for(int i=0; i<g.size(); i++) {
	    duration[i] = in.nextInt();
	}
	PERT p = pert(g, duration);
	if(p == null) {
	    System.out.println("Invalid graph: not a DAG");
	} else {
	    System.out.println("Number of critical vertices: " + p.numCritical());
	    System.out.println("u\tEC\tLC\tSlack\tCritical");
	    for(Vertex u: g) {
		System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
	    }
	}
    }
}
