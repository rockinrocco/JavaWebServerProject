/*
 * ConnectionHandler.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import AbstractPlugin.AbstractPlugin;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;
import server.Server;

/**
 * This class is responsible for handling a incoming request by creating a
 * {@link HttpRequest} object and sending the appropriate response be creating a
 * {@link HttpResponse} object. It implements {@link Runnable} to be used in
 * multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Server server;
	private Socket socket;

	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;

	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * The entry point for connection handler. It first parses incoming request
	 * and creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client
	 * (web browser).
	 */
	public void run() {
		try {
			HashMap<String, AbstractPlugin> plugins = this.server.getPlugins();
			socket.setKeepAlive(true);
			InputStream inStream = null;
			OutputStream outStream = null;
			HttpRequest request = null;
			HttpResponse response = null;
			while (true) {
				try {
					inStream = this.socket.getInputStream();
					outStream = this.socket.getOutputStream();
					request = HttpRequest.read(inStream);
					System.out.println(request);
					// Fill in the code to create a response for version
					// mismatch.
					// You may want to use constants such as Protocol.VERSION,
					// Protocol.NOT_SUPPORTED_CODE, and more.
					// You can check if the version matches as follows
					String rootDirectory = server.getRootDirectory();
					String uri = request.getUri();
					String[] paths = uri.split("/");
					System.out.print("PLUGIN " + paths[1]);
					if (paths[1].equals("favicon.ico")) {
						response = HttpResponseFactory
								.create404NotFound(Protocol.CLOSE);
					} else {
						if (!request.getVersion().equalsIgnoreCase(
								Protocol.VERSION)) {
							// Here you checked that the "Protocol.VERSION"
							// string
							// is not equal to the
							// "request.version" string ignoring the case of the
							// letters in both strings
							// TODO: Fill in the rest of the code here
						} else if (plugins.containsKey(paths[1])) {
							String newURI = paths[2];
							System.out.print("  SERVLET  " + newURI);
							if (paths.length > 4) {
								response = HttpResponseFactory
										.create404NotFound(Protocol.CLOSE);
							} else {
								// for(int i=2;i<paths.length;i++){
								// newURI+="/"+paths[i];
								// }
								StringBuilder builder = new StringBuilder();
								java.util.Date date = new java.util.Date();
								builder.append(date.getTime());
								builder.append('\t');
								builder.append(this.socket.toString());
								builder.append('\t');
								builder.append(paths[1] + "/" + paths[2] + "/"
										+ request.getMethod());
								try (PrintWriter out = new PrintWriter(
										new BufferedWriter(new FileWriter(
												"log.txt", true)))) {
									out.println(builder.toString());
									// more code
									// more code
								} catch (IOException e) {
									// exception handling left as an exercise
									// for
									// the reader
								}
								response = plugins.get(paths[1]).handleRequest(
										request, newURI,
										server.getRootDirectory());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					// Write response and we are all done so close the socket
					System.out.println(response);
					response.write(outStream);
				} catch (Exception e) {
					// We will ignore this exception
					e.printStackTrace();
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
// Get the start time
// long start = System.currentTimeMillis();
//
// HashMap<String, AbstractPlugin> plugins = this.server.getPlugins();;
// InputStream inStream = null;
// OutputStream outStream = null;
//
// try {
// inStream = this.socket.getInputStream();
// outStream = this.socket.getOutputStream();
// }
// catch(Exception e) {
// // Cannot do anything if we have exception reading input or output stream
// // May be have text to log this for further analysis?
// e.printStackTrace();
//
// // Increment number of connections by 1
// server.incrementConnections(1);
// // Get the end time
// long end = System.currentTimeMillis();
// this.server.incrementServiceTime(end-start);
// return;
// }
//
// // At this point we have the input and output stream of the socket
// // Now lets create a HttpRequest object
// HttpRequest request = null;
// HttpResponse response = null;
// try {
// request = HttpRequest.read(inStream);
// System.out.println(request);
// }
// catch(ProtocolException pe) {
// // We have some sort of protocol exception. Get its status code and create
// response
// // We know only two kind of exception is possible inside fromInputStream
// // Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
// int status = pe.getStatus();
// if(status == Protocol.BAD_REQUEST_CODE) {
// response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
// }
// // TODO: Handle version not supported code as well
// }
// catch(Exception e) {
// e.printStackTrace();
// // For any other error, we will create bad request response as well
// response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
// }
//
// if(response != null) {
// // Means there was an error, now write the response object to the socket
// try {
// response.write(outStream);
// // System.out.println(response);
// }
// catch(Exception e){
// // We will ignore this exception
// e.printStackTrace();
// }
//
// // Increment number of connections by 1
// server.incrementConnections(1);
// // Get the end time
// long end = System.currentTimeMillis();
// this.server.incrementServiceTime(end-start);
// return;
// }
//
// // We reached here means no error so far, so lets process further
// try {
// // Fill in the code to create a response for version mismatch.
// // You may want to use constants such as Protocol.VERSION,
// Protocol.NOT_SUPPORTED_CODE, and more.
// // You can check if the version matches as follows
// String rootDirectory = server.getRootDirectory();
// String uri = request.getUri();
// String[] paths = uri.split("/");
// System.out.print("PLUGIN " + paths[1]);
// if(!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
// // Here you checked that the "Protocol.VERSION" string is not equal to the
// // "request.version" string ignoring the case of the letters in both strings
// // TODO: Fill in the rest of the code here
// }else if(plugins.containsKey(paths[1])) {
// String newURI = paths[2];
// System.out.print("  SERVLET  " + newURI);
// if(paths.length>4){
// response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
// }else{
// // for(int i=2;i<paths.length;i++){
// // newURI+="/"+paths[i];
// // }
// StringBuilder builder = new StringBuilder();
// java.util.Date date= new java.util.Date();
// builder.append(date.getTime());
// builder.append('\t');
// builder.append(this.socket.toString());
// builder.append('\t');
// builder.append(paths[1] + "/" + paths[2] + "/" + request.getMethod());
// try(PrintWriter out = new PrintWriter(new BufferedWriter(new
// FileWriter("log.txt", true)))) {
// out.println(builder.toString());
// //more code
// //more code
// }catch (IOException e) {
// //exception handling left as an exercise for the reader
// }
// response = plugins.get(paths[1]).handleRequest(request, newURI,
// server.getRootDirectory());
// }
// }
// }
// catch(Exception e) {
// e.printStackTrace();
// response = HttpResponseFactory.create500ServerError(Protocol.CLOSE);
// }
//
// if(response == null) {
// response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
// }
//
// try{
// // Write response and we are all done so close the socket
// response.write(outStream);
// socket.close();
// }
// catch(Exception e){
// // We will ignore this exception
// e.printStackTrace();
// }
//
// // Increment number of connections by 1
// server.incrementConnections(1);
// // Get the end time
// long end = System.currentTimeMillis();
// this.server.incrementServiceTime(end-start);
// }
// }
