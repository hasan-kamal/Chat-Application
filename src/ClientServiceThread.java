/*

@author Hasan Kamal

*/

import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.regex.*;
import java.util.*;

public class ClientServiceThread implements Runnable{

	int clientId_;
	private Server myServer_;
	private String availableCommands;

	public ClientServiceThread(int clientId, Server server){
		myServer_ = server;
		clientId_ = clientId;
		availableCommands = "Available commands:\n\t1. Client X: <message>\n\t2. All: <message>\n\t3. Client X,Y: <message>\n\t4. Server: List All";
	}

	public void run(){
		try{
			//send available commands first
			synchronized(myServer_.getWriteStream(myServer_.getSocket(clientId_))){
				myServer_.getWriteStream(myServer_.getSocket(clientId_)).println(availableCommands);
				myServer_.getWriteStream(myServer_.getSocket(clientId_)).println("You are client " + clientId_);
			}

			//communicate
			String textReceivedFromClient;
			while((textReceivedFromClient = myServer_.getReadStream(myServer_.getSocket(clientId_)).readLine()) != null){
				System.out.println("Received from Client " + clientId_ + ": " + textReceivedFromClient);
				
				if(Pattern.matches("Client ([0-9])+:.*", textReceivedFromClient)){
					//Client X: <message>
					int clientToSend = Integer.parseInt(textReceivedFromClient.substring(7, textReceivedFromClient.indexOf(':')));
					String textToSend = textReceivedFromClient.substring(textReceivedFromClient.indexOf(':') + 1).trim();

					PrintWriter os = myServer_.getWriteStream(myServer_.getSocket(clientToSend));
					if(os == null){
						myServer_.getWriteStream(myServer_.getSocket(clientId_)).println("Error: Client " + clientToSend + " does not exist");
						continue;
					}
					synchronized(os){
						os.println("Client " + clientId_ + " says: " + textToSend);
						System.out.println("Sent message to client " + clientToSend);
					}

				}else if(Pattern.matches("All:.*", textReceivedFromClient)){
					//All: <message>
					String textToSend = textReceivedFromClient.substring(textReceivedFromClient.indexOf(':') + 1).trim();
					Map<Integer, Socket> currentClientsMap = myServer_.getMapSocket();
					for(Map.Entry<Integer, Socket> entry : currentClientsMap.entrySet()){
						PrintWriter os = myServer_.getWriteStream(myServer_.getSocket(entry.getKey()));
						if(os == null){
							continue;
						}
						synchronized(os){
							os.println("Client " + clientId_ + " says: " + textToSend);
						}
					}
				}else if(Pattern.matches("Client ([0-9])+,([0-9])+:.*", textReceivedFromClient)){
					//Client X,Y: <message>
					int clientToSend1 = Integer.parseInt(textReceivedFromClient.substring(7, textReceivedFromClient.indexOf(',')));
					int clientToSend2 = Integer.parseInt(textReceivedFromClient.substring(textReceivedFromClient.indexOf(',') + 1, textReceivedFromClient.indexOf(':')));

					String textToSend = textReceivedFromClient.substring(textReceivedFromClient.indexOf(':') + 1).trim();

					PrintWriter os = myServer_.getWriteStream(myServer_.getSocket(clientToSend1));
					if(os == null){
						myServer_.getWriteStream(myServer_.getSocket(clientId_)).println("Error: Client " + clientToSend1 + " does not exist");
						continue;
					}
					synchronized(os){
						os.println("Client " + clientId_ + " says: " + textToSend);
						System.out.println("Sent message to client " + clientToSend1);
					}

					os = myServer_.getWriteStream(myServer_.getSocket(clientToSend2));
					if(os == null){
						myServer_.getWriteStream(myServer_.getSocket(clientId_)).println("Error: Client " + clientToSend2 + " does not exist");
						continue;
					}
					synchronized(os){
						os.println("Client " + clientId_ + " says: " + textToSend);
						System.out.println("Sent message to client " + clientToSend2);
					}
				}else if(Pattern.matches("Server: List All", textReceivedFromClient)){
					//Server: List All
					Map<Integer, Socket> currentClientsMap = myServer_.getMapSocket();
					String list = "Connected clients:\n";
					for(Map.Entry<Integer, Socket> entry : currentClientsMap.entrySet()){
						list += ("\t" + entry.getKey());
						list += "\n";
					}
					list = list.substring(0, list.length() - 1);
					synchronized(myServer_.getWriteStream(myServer_.getSocket(clientId_))){
						myServer_.getWriteStream(myServer_.getSocket(clientId_)).println(list);
					}
				}else{
					synchronized(myServer_.getWriteStream(myServer_.getSocket(clientId_))){
						myServer_.getWriteStream(myServer_.getSocket(clientId_)).println("Command not recognized");
					}
				}
				
			}
			myServer_.removeClient(clientId_);
			System.out.println("Client " + clientId_ + " disconnected");
		}catch(Exception ex){
			System.out.println("error during communication");
			ex.printStackTrace();
			System.exit(0);
		}
	}

}