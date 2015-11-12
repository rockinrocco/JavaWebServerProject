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

import java.io.File;
import java.io.IOException;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Paul Bliudzius
 */
public class Waiter extends IServlet{
	private String[] error;
	public int seconds;
	public Waiter(String root) {
		super(root);
		this.seconds = 6;
	}

	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		try {
			System.out.println("Wait");
		    Thread.sleep(1000*this.seconds);        //1000 milliseconds is one second.
		    System.out.println("Done waiting");
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		return HttpResponseFactory.create200OK("Try",Protocol.CLOSE);
	}
}
