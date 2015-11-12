/*
 * RequestHandler.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import com.google.common.io.ByteStreams;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class RequestHandler implements Runnable {

	private Socket connectionSocket;
	private Socket serverSocket;

	/**
	 * @param connectionSocket
	 * @param socket
	 */
	public RequestHandler(Socket connectionSocket, Socket socket) {
		// TODO Auto-generated constructor stub
		this.connectionSocket = connectionSocket;
		this.serverSocket = socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		InputStream clientInStream = null;
		OutputStream clientOutStream = null;
		InputStream ServerInStream = null;
		OutputStream ServerOutStream = null;
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			clientInStream = this.connectionSocket.getInputStream();
			clientOutStream = this.connectionSocket.getOutputStream();
			ServerInStream = this.serverSocket.getInputStream();
			ServerOutStream = this.serverSocket.getOutputStream();

			request = HttpRequest.read(clientInStream);
			ServerOutStream.write(request.toString().getBytes(Charset.forName("UTF-8")));
			System.out.println("\n-------------------REQUEST-------------------\n");
			System.out.println(request);
			// ServerOutStream.write(stringRequest.getBytes());
			ServerOutStream.flush();

			InputStreamReader inStreamReader = new InputStreamReader(ServerInStream);
			BufferedReader reader = new BufferedReader(inStreamReader);
			String line = reader.readLine();
			if(line == null) {
				throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
			}
			StringTokenizer tokenizer = new StringTokenizer(line, " ");
			if(tokenizer.countTokens() != 3) {
				throw new ProtocolException(Protocol.BAD_REQUEST_CODE, Protocol.BAD_REQUEST_TEXT);
			}
			String version = tokenizer.nextToken();
			int status = Integer.parseInt(tokenizer.nextToken());
			String phrase = tokenizer.nextToken();
			
			StringBuilder fullRequest = new StringBuilder(line + Protocol.CRLF);
			System.out.println("\n-------------------RESPONSE-------------------\n");
			boolean hasContent = false;
			while (!line.equals("")) {
				line = reader.readLine();
				fullRequest.append(line + Protocol.CRLF);
				if(line.startsWith("Content-Length")){
					hasContent = true;
				}
			}
			
			if(!hasContent){
			//ok response, no body	
			} else {
			char[] body = new char[10024];
			reader.read(body);
			fullRequest.append(String.valueOf(body) + Protocol.CRLF);
			}
			String stringRequest = fullRequest.toString();
			System.out.print(stringRequest);
			clientOutStream.write(stringRequest.getBytes(Charset
					.forName("UTF-8")));

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
