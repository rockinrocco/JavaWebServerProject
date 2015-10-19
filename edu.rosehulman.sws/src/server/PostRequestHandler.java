/*
 * GetRequestHandler.java
 * Oct 18, 2015
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
 
package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PostRequestHandler implements IRequestHandler {

	/* (non-Javadoc)
	 * @see server.IRequestHandler#handleRequest(java.lang.String)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request, String rootDirectory) {
//		Map<String, String> header = request.getHeader();
//		String date = header.get("if-modified-since");
//		String hostName = header.get("host");

		String uri = request.getUri();
		File file = new File(rootDirectory + uri);
		// Check if the file exists				// Look for default index.html file in a directory
				if(file.exists()) {
				    FileWriter fw;
					try {
						fw = new FileWriter(rootDirectory + uri,true);
						fw.write(request.getBody());//appends the string to the file
						fw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //the true will append the new data

					// Lets create 200 OK response
					return HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				else {
					Writer writer = null;

					try {
					    writer = new BufferedWriter(new OutputStreamWriter(
					          new FileOutputStream(rootDirectory + uri), "utf-8"));
					    writer.write(request.getBody());
					} catch (IOException ex) {
					  // report
					} finally {
					   try {writer.close();} catch (Exception ex) {/*ignore*/}
					}
					// File does not exist so lets create 404 file not found code
					return HttpResponseFactory.create201CreatedFile(file, Protocol.CLOSE);
				}
	}
}