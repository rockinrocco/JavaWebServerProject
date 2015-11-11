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

import java.io.File;
import java.net.ServerSocket;
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

	public LoadBalancer(ArrayList<Integer> serverPorts, int myPort) {
		this.serverPorts = serverPorts;
		this.myPort = myPort;

	}

	@Override
	public void run() {
		try {

			loadBalancerSocket = new ServerSocket(myPort);

		} catch (Exception e) {

		}
	}
}
