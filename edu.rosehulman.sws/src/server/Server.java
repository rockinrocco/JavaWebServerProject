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
import jarLoader.JarClassLoader;

import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

import AbstractPlugin.AbstractPlugin;

/**
 * This represents a welcoming server for the incoming TCP request from a HTTP
 * client such as a web browser.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class Server implements Runnable {
	private String rootDirectory;
	private int port;
	private boolean stop;
	private ServerSocket welcomeSocket;
	private PluginWatcher pluginWatcher;

	private long connections;
	private long serviceTime;

	private WebServer window;
	private HashMap<String, AbstractPlugin> plugins;
	private HashMap<String, Integer> attempts;
	private HashSet<String> blacklisted;
	private final int BAN_THRESHOLD = 50;
	
    public ClassLoader parentClassLoader = JavaClassLoader.class.getClassLoader();
    public JavaClassLoader classLoader = new JavaClassLoader(parentClassLoader,new HashSet<String>());
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
		this.plugins = new HashMap<String,AbstractPlugin>();
		
		 File dir = new File("web/plugins");
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File file : directoryListing) {
		      uploadPlugin(Paths.get(file.getPath()));
		    }
		  } else {
		  }
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
			PluginWatcher watcher = new PluginWatcher(this,"web/plugins");
			new Thread(watcher).start();

			this.welcomeSocket = new ServerSocket(port);
			// Now keep welcoming new connections until stop flag is set to true
			while (true) {
				// Listen for incoming socket connection
				// This method block until somebody makes a request
				Socket connectionSocket = this.welcomeSocket.accept();
				String sockID = connectionSocket.getRemoteSocketAddress().toString();
				System.out.println("SOCK" +sockID);
				if(blacklisted.contains(sockID)){
					continue;
				}
				int attCount = 1;
				if(attempts.containsKey(sockID)){
					attCount = attempts.get(sockID)+1;
					attempts.put(sockID, attCount);
				}else{
					attempts.put(sockID, attCount);
				}
				if(attCount>=BAN_THRESHOLD){
					blacklisted.add(sockID);
					continue;
				}
				System.out.println(attempts.get(sockID)+"");
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
//		String file = filename.getFileName().toString();
//		int split = file.indexOf('.');
//		String name = file.substring(0,split);
//		if(!plugins.containsKey(name)){
//			plugins.remove(name);
//		}
	}

	/**
	 * @param filename
	 * This function get called when a new file is created or updated
	 */
	public void uploadPlugin(Path filename) {
			//File file = new File(filename.toString());
//		this.classLoader = new JavaClassLoader(parentClassLoader, this.classLoader.loadedClasses);
//		String fileString = filename.getFileName().toString();
//		int split = fileString.indexOf('.');
//		String name = fileString.substring(0,split);
//		String className = "pluginImp." + name;
//		Class clazz;
//		if(this.plugins.containsKey(name)){
//			clazz=this.classLoader.reloadClass(name, className);
//		} else {
//		 clazz = this.classLoader.loadNewClass(name, className);
//		}
//		AbstractPlugin plugin = (AbstractPlugin) clazz.newInstance();
//		plugin.init(this.rootDirectory);
//		System.out.println("Loaded "+ name);
//		this.plugins.put(name, plugin);
		try{
		JarClassLoader jarLoader = new JarClassLoader(filename.toString());
		String file = filename.getFileName().toString();
		int split = file.indexOf('.');
		String name = file.substring(0,split);
		if(!plugins.containsKey(name)){
		Class c = jarLoader.loadClass(name, true);
        Object o;
		o = c.newInstance();
        AbstractPlugin plugin = (AbstractPlugin) c.newInstance();
		System.out.println(plugin.getName());
        plugin.init(this.getRootDirectory());
        plugins.put(name,plugin);
		}
		} catch(Exception e){
			System.out.println("Error adding plugin");
		}
//        writeFilePathToText(filename.toString());
		//}
	
}
	/**
	 * @return
	 */
	public HashMap<String, AbstractPlugin> getPlugins() {
		// TODO Auto-generated method stub
		return this.plugins;
	}
}
