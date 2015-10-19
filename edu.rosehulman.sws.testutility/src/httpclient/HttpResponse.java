/*
 * HttpRequest.java
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

 
package httpclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a request object for HTTP.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class HttpResponse {
	private String statusLine;
	private Map<String, String> header;
	private char[] body;
	
	private HttpResponse() {
		this.statusLine = "";
		this.header = new HashMap<String, String>();
		this.body = new char[0];
	}
	

	/**
	 * Gets the status line.
	 * @return the version
	 */
	public String getStatusLine() {
		return statusLine;
	}
	
	/**
	 * Gets the body of the response.
	 * 
	 * @return The body of the response.
	 */
	public char[] getBody() {
		return body;
	}

	/**
	 * The key to value mapping in the request header fields.
	 * 
	 * @return the header
	 */
	public Map<String, String> getHeader() {
		// Lets return the unmodifable view of the header map
		return Collections.unmodifiableMap(header);
	}

	/**
	 * Reads raw data from the supplied input stream and constructs a 
	 * <tt>HttpRequest</tt> object out of the raw data.
	 * 
	 * @param inputStream The input stream to read from.
	 * @return A <tt>HttpRequest</tt> object.
	 * @throws Exception Throws either {@link ProtocolException} for bad request or 
	 * {@link IOException} for socket input stream read errors.
	 */
	public static HttpResponse read(InputStream inputStream) throws Exception {
		// We will fill this object with the data from input stream and return it
		HttpResponse response = new HttpResponse();
		
		InputStreamReader inStreamReader = new InputStreamReader(inputStream);
		BufferedReader reader = new BufferedReader(inStreamReader);
		
		//First Response Line: HTTP/1.1 200 OK
		String line = reader.readLine(); // A line ends with either a \r, or a \n, or both
		
		if(line == null) {
			throw new ProtocolException();
		}

		response.statusLine = line;
		
		// Rest of the request is a header that maps keys to values
		// e.g. Host: www.rose-hulman.edu
		// We will convert both the strings to lower case to be able to search later
		line = reader.readLine().trim();
		
		while(!line.equals("")) {
			// THIS IS A PATCH 
			// Instead of a string tokenizer, we are using string split
			// Lets break the line into two part with first space as a separator 
			
			// First lets trim the line to remove escape characters
			line = line.trim();
			
			// Now, get index of the first occurrence of space
			int index = line.indexOf(' ');
			
			if(index > 0 && index < line.length()-1) {
				// Now lets break the string in two parts
				String key = line.substring(0, index); // Get first part, e.g. "Host:"
				String value = line.substring(index+1); // Get the rest, e.g. "www.rose-hulman.edu"
				
				// Lets strip off the white spaces from key if any and change it to lower case
				key = key.trim().toLowerCase();
				
				// Lets also remove ":" from the key
				key = key.substring(0, key.length() - 1);
				
				// Lets strip white spaces if any from value as well
				value = value.trim();
				
				// Now lets put the key=>value mapping to the header map
				response.header.put(key, value);
			}
			
			// Processed one more line, now lets read another header line and loop
			line = reader.readLine().trim();
		}
		
		int contentLength = 0;
		try {
			contentLength = Integer.parseInt(response.header.get("content-length"));
		}
		catch(Exception e){}
		
		if(contentLength > 0) {
			response.body = new char[contentLength];
			reader.read(response.body);
		}
		
		return response;
	}
	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("----------- Header ----------------\n");
		buffer.append(this.statusLine);
		buffer.append("\n");
		
		for(Map.Entry<String, String> entry : this.header.entrySet()) {
			buffer.append(entry.getKey());
			buffer.append(" : ");
			buffer.append(entry.getValue());
			buffer.append("\n");
		}
		buffer.append("------------- Body ---------------\n");
		buffer.append(this.body);
		buffer.append("\n----------------------------------\n");
		return buffer.toString();
	}
}
