package de.unihildesheim.iis.jadedemo;

import java.io.IOException;

import de.unihildesheim.iis.jadedemo.graph.Graph;
import de.unihildesheim.iis.jadedemo.graph.Graph.Vertex;
import de.unihildesheim.iis.jadedemo.graph.State;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.Serializable;

/**
 * @author Viktor Eisenstadt
 */
public class AgentThree extends Agent {
  private static final long serialVersionUID = 1L;
  int maxMoves = 3000;
  int maxTries = 20;
  int maxCol = 15;
  float temperature = 1f;
  float alpha = 0.5f;
  boolean verbose = false;

  protected void setup() {

    // Define the behaviour
    CyclicBehaviour loop = new CyclicBehaviour(this) {
      private static final long serialVersionUID = 1L;

      @Override
      public void action() {

        // Receive the incoming message
        ACLMessage aclMsg = receive();

        // Interpret the message
        if (aclMsg != null) {
		try {

			Graph graph = (Graph) aclMsg.getContentObject();
			System.out.println("doha : "+graph);
		    long start = System.currentTimeMillis();
		    State best = Metaheuristic.MetaHeuristic(graph, maxCol, maxTries, maxMoves, temperature, alpha);
		    long end = System.currentTimeMillis();

		    int violation = best.HardCheckViolations();
		    System.out.println("");
		    if (violation == 0)
		       System.out.println("Solution find found after " + (end - start) + "ms");
		    else{
		              //System.out.println("Approximation found after " + (end - start) + "ms with " + violation + " violations.");
		      System.out.println("Approximation found after " + (end - start) + "ms with " + (violation / 2) + " violations.");
		    }
		    for (Vertex v : best.sortedVertexByViolation)
		       System.out.println(v.ID() + "		" + best.Violations(v.ID()) + "		" + v.GetDegree() + "		" + best.Value(v.ID()));
		} catch (UnreadableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          // TODO Aufgabe 1
        }
        block(); // Stop the behaviour until next message is received
      }
    };
    addBehaviour(loop);
  }
}
