package de.unihildesheim.iis.jadedemo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.StaleProxyException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.unihildesheim.iis.jadedemo.graph.Graph;

/**
 * Jade Agent template
 *
 * @author Viktor Eisenstadt
 */
public class AgentOne extends Agent {
  private static final long serialVersionUID = 1L;

  protected void setup() {

    // Define the behaviour
    CyclicBehaviour loop = new CyclicBehaviour(this) {
      private static final long serialVersionUID = 1L;
      String filename = "le450_15b.col";
      @Override
      public void action() {
        // Receive the incoming message
        ACLMessage aclMsg = receive();
        // Interpret the message
        //if (aclMsg != null) {
        	ACLMessage newMsg = new ACLMessage(ACLMessage.INFORM);
            newMsg.addReceiver(new AID("AgentThree", AID.ISLOCALNAME));
            try {
            	Graph graph = new Graph(filename);
                System.out.println("DOHA : "+graph);
				newMsg.setContentObject(graph);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            send(newMsg);
       // }       
        block(); // Stop the behaviour until next message is received
      }
    };
    addBehaviour(loop);
  }
}
