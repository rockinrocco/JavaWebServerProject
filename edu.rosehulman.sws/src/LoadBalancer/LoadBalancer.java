/*
 * LoadBalancer.java
 * Nov 11, 2015
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */

package LoadBalancer;

import gui.WebServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import AbstractPlugin.AbstractPlugin;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class LoadBalancer implements Runnable {

	private ArrayList<Integer> serverPorts;
	private int myPort;
	private ServerSocket loadBalancerSocket;
	private HashMap<String, Integer> attempts;
	private HashSet<String> blacklisted;
	private final int BAN_THRESHOLD = 500;


	public LoadBalancer(ArrayList<Integer> serverPorts, int myPort) {
		this.serverPorts = serverPorts;
		this.myPort = myPort;
		this.attempts = new HashMap<String, Integer>();
		this.blacklisted = new HashSet<String>();

	}

	@Override
	public void run() {
		try {
			ArrayList<Socket> sockets = new ArrayList<Socket>();
			for (int port : serverPorts) {
				System.out.println(port);
				Socket socket = new Socket("localhost", port);
				socket.setKeepAlive(true);
				sockets.add(socket);
			}
			Clock clock = new Clock(this);
			new Thread(clock).start();

			loadBalancerSocket = new ServerSocket(myPort);
			int currentRequest=0;
			while(true){
				System.out.println("Current Servlet1: "+currentRequest);
				Socket connectionSocket = this.loadBalancerSocket.accept();
				System.out.println("Current Servlet2: "+currentRequest);
				String sockID = connectionSocket.getInetAddress().toString();
				System.out.println("Current Servlet3: "+currentRequest);
				System.out.println("SOCK" + sockID);
				if (blacklisted.contains(sockID)) {
					try (PrintWriter out = new PrintWriter(new BufferedWriter(
							new FileWriter("log.txt", true)))) {
						out.write("BLACKLISTED " + sockID
								+ " TOO MANY CONNECTION ATTEMPTS");
					}
					continue;
				}
				int attCount = 1;
				if (attempts.containsKey(sockID)) {
					attCount = attempts.get(sockID) + 1;
					attempts.put(sockID, attCount);
				} else {
					attempts.put(sockID, attCount);
				}
				if (attCount >= BAN_THRESHOLD) {
					blacklisted.add(sockID);
					continue;
				}
				System.out.println(attCount);
				// System.out.println(attempts.get(sockID)+"");
				// Come out of the loop if the stop flag is set
				System.out.println("Current Servlet4: "+currentRequest);
//				RequestHandler handler = new RequestHandler(connectionSocket,sockets.get(currentRequest));
				System.out.println("Current Servlet5: "+currentRequest);
				currentRequest = (currentRequest + 1) % serverPorts.size();
				new Thread(new RequestHandler(connectionSocket,sockets.get(currentRequest))).start();
				System.out.println("done");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the attempts resource
	 */
	public void resetAttempts() {
		this.attempts.clear();
		this.blacklisted.clear();
		System.out.println("I reset the attempts and blacklist");
	}

	
	public class Clock implements Runnable {

		private LoadBalancer serv;

		public Clock(LoadBalancer loadBalancer) {
			this.serv = loadBalancer;
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(1000 * 5 * 60);
					serv.resetAttempts();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
