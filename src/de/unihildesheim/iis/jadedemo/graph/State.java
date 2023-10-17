package de.unihildesheim.iis.jadedemo.graph;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;

import de.unihildesheim.iis.jadedemo.graph.Graph.Vertex;
import de.unihildesheim.iis.jadedemo.graph.utils.Rand;


/**
 * This class represents a graph state. it stores the previous state, its children states and the vertex that was changed.
 * @author Romain Fournier
 */
public class State {
    private State parentState = null;
    private Graph graph;
    private int[] values;
    private int[] violations;

    public ArrayList<Vertex> sortedVertexByViolation = new ArrayList<Vertex>(); //stores the vertex ids that violates the most, descending
    private int changedVertex = -1;
    private LinkedList<State> children = new LinkedList<State>();

    class SortVertexByViolations implements Comparator<Vertex> {
        public int compare(Vertex a, Vertex b) {
            return violations[b.ID()] - violations[a.ID()];
        }
    }

    /**
     * Root state
     */
    public State (Graph g, int max_col) {
        graph = g;
        values = new int[g.GetVertexCount()];
        violations = new int[g.GetVertexCount()];
        initialisation_random(max_col);
        
        //We compute the violation map (after initialisation)
        for (int i = 0; i < violations.length; i++) {
            violations[i] = -1;
            Violations(i);
            sortedVertexByViolation.add(graph.Get(i));
        }
        sortedVertexByViolation.sort(new SortVertexByViolations());
    }

    /**
     * Creates a new state, should be called by GenerateNeighboringState
     * @param g the graph attached to this state
     * @param cv the vertex ID that changed from the previous state 
     * @param s the new values (only s[cv] should be altered)
     * @param ps the parent state
     */
    private State (Graph g, int cv, int[] s, State ps) {
        graph = g;
        assert (s.length != g.GetVertexCount()) : "invalid state, vertex count does not match";
        values = s;
        changedVertex = cv;
        parentState = ps;

        //violation map
        violations = parentState.violations.clone();
        //The violation map changed, we compute the change in violation on the neighborhood of the changed vertex
        for (Vertex neighbor : graph.Get(changedVertex).Getneighborhood()) {
            int neighborID = neighbor.ID();
            if (parentState.Violation(changedVertex, neighborID) && !Violation(changedVertex, neighborID))
            violations[neighborID]--;
            else if (!parentState.Violation(changedVertex, neighborID) && Violation(changedVertex, neighborID))
            violations[neighborID]++;
        }
        //And we compute the violations on the changed vertex
        violations[changedVertex] = -1;
        Violations(changedVertex);

        sortedVertexByViolation = new ArrayList<Vertex>(parentState.sortedVertexByViolation);
        sortedVertexByViolation.sort(new SortVertexByViolations());
    }

    /**
     * generates a neighboring state, only 1 value from 'values' should be altered
     * @return a freshly made state
     */
    public State GenerateNeighboringState (int maxCol) {
        int[] newValues = values.clone();
        int newChangedVertex = -1;

        newChangedVertex = Rand.r.nextInt(graph.GetVertexCount());
        newValues[newChangedVertex] = Rand.r.nextInt(maxCol);

        State s = new State(graph, newChangedVertex, newValues, this);
        children.add(s);

        return s;
    }

    /**
     * @return the change in violations this state made
     */
    public int DeltaViolations () {
        if (parentState == null) return 0;
        int parentViolations = parentState.Violations(changedVertex);
        int currentViolations = Violations(changedVertex);
        return currentViolations - parentViolations;
    }

    public void initialisation_random(int max_col){
        for (int vertex: values) {
            int value_random = (int)(Math.random() * max_col);
//            System.out.println(value_random);
            values[vertex] = value_random ;

        }
    }

    /**
     * 
     * @param vertexID the vertex to check for violations
     * @return the number of constraints this vertex violates (aka the number of neighbor with the same value)
     */
    public int Violations (int vertexID) {
        if (violations[vertexID] != -1) return violations[vertexID]; //Dynamic programming, here we try to compute only once this result

        int ans = 0;
        Vertex current = graph.Get(vertexID);
        for(Vertex neighbor : current.Getneighborhood()) {
            if (values[current.ID()] == values[neighbor.ID()]) ans++;
        }

        violations[vertexID] = ans;
        return ans;
    }

    /**
     * @return the number of contraints this state violates
     */
    public int Violations () {
        int ans = 0;
        for (int i = 0; i < violations.length; i++) {
            ans += Violations(i);
        }
        return ans;
    }

    public int HardCheckViolations () {
        int ans = 0;
        for (int i = 0; i < violations.length; i++) {
            violations[i] = -1; //We reset the map so we have to recompute the violations
            ans += Violations(i);
        }
        return ans;
    }

    /**
     * Check if there is a violation bewteen two vertices
     * @param vertexIDA the first vertice
     * @param vertexIDB the second vertice
     * @return true if there is a violation between those two vertices
     */
    public boolean Violation (int vertexIDA, int vertexIDB) {
        return (values[vertexIDA] == values[vertexIDB] && graph.Get(vertexIDA).Getneighborhood().contains(graph.Get(vertexIDB)));
    }

    public int Value (int vertexID) {
        return values[vertexID];
    }
}
