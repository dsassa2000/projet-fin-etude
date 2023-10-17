package de.unihildesheim.iis.jadedemo;

import de.unihildesheim.iis.jadedemo.graph.Graph;
import de.unihildesheim.iis.jadedemo.graph.State;

public class Metaheuristic {
	/**
     * Simulated Annealing (SA) method
     * @param cur current configuration
     * @param maxCol Maximum number of colours
     * @param temperature important parameter of SA, A high temperature T allows the algorithm to escape from local minima, A low temperature makes the algorithm a greedy algorithm
     * @return neighbor configuration
     */
    public static State SA_Move(State cur, int maxCol, float temperature) {
        int bestCost = Integer.MAX_VALUE;
        State x_best = cur;
        boolean accepted = false; //Accepted?(cur, x) : cost(x) ≤ cost(cur) or Random()< exp(−∆/T)
        for (int i = 0; i < maxCol; i++) {
            State x = cur.GenerateNeighboringState(maxCol); //Generate-Neighbor(cur): any neighbor (selected randomly)
            if (x.Violations() <= cur.Violations() || Math.random() < Math.exp(-x.DeltaViolations() / temperature)) {
                accepted = true;
            }
            if (x.Violations() < bestCost) {
                x_best = x;
                bestCost = x.Violations();
            }
        }
        if (accepted) return x_best;
        else return cur;
    }

    /**
     *
     * @param g
     * @param maxCol Maximum number of colours
     * @param maxTries Maximun number of tries
     * @param maxMoves Maximum number of moves
     * @param baseTemp Base temperature
     * @param alpha Multiplicator of temperature
     * @return neighbor
     * configuration
     */
    public static State MetaHeuristic(Graph g, int maxCol, int maxTries, int maxMoves, float baseTemp, float alpha) {
        State best = null;

        for (int i = 0; i < maxTries; i++) {
            State x = new State(g, maxCol);
            State bestWalk = null;
            float temperature = baseTemp;
            for (int j = 0; j < maxMoves; j++) {
                x = SA_Move(x, maxCol, temperature);
                temperature *= alpha;
                if (bestWalk == null || x.Violations() < bestWalk.Violations())
                    bestWalk = x;
            }
            if (best == null || bestWalk.Violations() < best.Violations())
                best = bestWalk;
        }

        return best;
    }

}
