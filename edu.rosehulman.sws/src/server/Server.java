/*
 * Server.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
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
 */

package server;

import gui.WebServer;

import java.awt.Container;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import plugin.IPlugin;
import protocol.Protocol;
import servlet.IServlet;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private HashMap<String, IRequestHandler> requestHandlers;
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	private PluginWatcher pluginWatcher;

	private long connections;
	private long serviceTime;

	private WebServer window;
	private HashMap<String, IPlugin> plugins;

	/**
	 * @param rootDirectory
	 * @param port
	 */
	public Server(String rootDirectory, int port, WebServer window) {
		this.rootDirectory = rootDirectory;
			// We obtain the file system of the Path
		this.port = port;
		this.stop = false;
		this.connections = 0;
		this.serviceTime = 0;
		this.window = window;
		this.plugins = new HashMap<String,IPlugin>();
		this.requestHandlers = new HashMap<String, IRequestHandler>();
		this.requestHandlers.put(Protocol.GET, new GetRequestHandler());
		this.requestHandlers.put(Protocol.POST, new PostRequestHandler());
		this.requestHandlers.put(Protocol.PUT, new PutRequestHandler());
		this.requestHandlers.put(Protocol.DELETE, new DeleteRequestHandler());
	}

	/**
	 * Gets the root directory for this web server.
	 * 
	 * @return the rootDirectory
	 */
	public String getRootDirectory() {
		return rootDirectory;
	}

	/**
	 * Gets the port number for this web server.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * 
	 * Get the handlers to use the code
	 * 
	 */
	public HashMap<String, IRequestHandler> getHandlers() {
		return requestHandlers;
	}

	/**
	 * Returns connections serviced per second. Synchronized to be used in
	 * threaded environment.
	 * 
	 * @return
	 */
	public synchronized double getServiceRate() {
		if (this.serviceTime == 0)
			return Long.MIN_VALUE;
		double rate = this.connections / (double) this.serviceTime;
		rate = rate * 1000;
		return rate;
	}

	/**
	 * Increments number of connection by the supplied value. Synchronized to be
	 * used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementConnections(long value) {
		this.connections += value;
	}

	/**
	 * Increments the service time by the supplied value. Synchronized to be
	 * used in threaded environment.
	 * 
	 * @param value
	 */
	public synchronized void incrementServiceTime(long value) {
		this.serviceTime += value;
	}

	/**
	 * The entry method for the main server thread that accepts incoming TCP
	 * connection request and creates a {@link ConnectionHandler} for the
	 * request.
	 */
	public void run() {
		try {
			PluginWatcher watcher = new PluginWatcher(this,rootDirectory);
			new Thread(watcher).start();

			this.welcomeSocket = new ServerSocket(port);
			// Now keep welcoming new connections until stop flag is set to true
			while (true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();

				// Come out of the loop if the stop flag is set
				if (this.stop)
					break;

				// Create a handler for this incoming connection and start the
				// handler in a new thread
				ConnectionHandler handler = new ConnectionHandler(this,
						connectionSocket);
				new Thread(handler).start();
			}
			this.welcomeSocket.close();
		} catch (Exception e) {
			window.showSocketException(e);
		}
	}

	/**
	 * Stops the server from listening further.
	 */
	public synchronized void stop() {
		if (this.stop)
			return;

		// Set the stop flag to be true
		this.stop = true;
		try {
			// This will force welcomeSocket to come out of the blocked accept()
			// method
			// in the main loop of the start() method
			Socket socket = new Socket(InetAddress.getLocalHost(), port);

			// We do not have any other job for this socket so just close it
			socket.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Checks if the server is stopeed or not.
	 * 
	 * @return
	 */
	public boolean isStoped() {
		if (this.welcomeSocket != null)
			return this.welcomeSocket.isClosed();
		return true;
	}

	/**
	 * @param filename
	 * This function get called when a new file is removed
	 */
	public void removePlugin(Path filename) {
		// TODO Auto-generated method stub
		String file = filename.getFileName().toString();
		int split = file.indexOf('.');
		String name = file.substring(0,split);
		plugins.remove(name);
	}

	/**
	 * @param filename
	 * This function get called when a new file is created or updated
	 */
	public void uploadPlugin(Path filename) {
		// TODO Auto-generated method stub
		try {
		JarClassLoader jarLoader = new JarClassLoader(filename.toString());
		String file = filename.getFileName().toString();
		int split = file.indexOf('.');
		String name = file.substring(0,split);
		Class c = jarLoader.loadClass(name, true);
        Object o;
		o = c.newInstance();
        IPlugin plugin = (IPlugin) c.newInstance();
        plugin.init(this.getRootDirectory());
        plugins.put(name,plugin);
       
		} catch (Exception e){
			e.printStackTrace();
		}
}

	/**
	 * @return
	 */
	public HashMap<String, IPlugin> getPlugins() {
		// TODO Auto-generated method stub
		return this.plugins;
	}
}
