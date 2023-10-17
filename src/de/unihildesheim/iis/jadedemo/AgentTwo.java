package de.unihildesheim.iis.jadedemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * @author Viktor Eisenstadt
 */
public class AgentTwo extends Agent {
  private static final long serialVersionUID = 1L;

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
          System.out.println(myAgent.getLocalName()
              + "> Received message from: " + aclMsg.getSender());
          System.out.println("Message content: " + aclMsg.getContent());
          // TODO Aufgabe 1
        }
        block(); // Stop the behaviour until next message is received
      }
    };
    addBehaviour(loop);
    
 // Start a server to listen for Python messages
    startServer();
  }
  private void startServer() {
      new Thread(() -> {
          try {
              ServerSocket serverSocket = new ServerSocket(9876);
              System.out.println("Java Server listening on port 9876");

              while (true) {
                  Socket socket = serverSocket.accept();
                  Scanner scanner = new Scanner(socket.getInputStream());

                  while (scanner.hasNextLine()) {
                      String message = scanner.nextLine();
                      ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                      aclMessage.setContent(message);
                      aclMessage.addReceiver(getAID());
                      send(aclMessage);
                  }

                  scanner.close();
                  socket.close();
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }).start();
  }
}
