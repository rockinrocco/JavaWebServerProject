/*
 * TestServlet.java
 * Oct 22, 2015
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
 
package servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Paul Bliudzius
 */
public class FilePostGood extends IServlet{

	public FilePostGood(String root) {
		super(root);
	}
	
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		File file = new File(rootDirectory + "/good.txt");
		if(file.exists()) {
		    FileWriter fw;
			try {
				fw = new FileWriter(rootDirectory + "/good.txt",true);
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
			          new FileOutputStream(rootDirectory + "/good.txt"), "utf-8"));
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
