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
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Paul Bliudzius
 */
public class AlcoholLog extends IServlet{
	public DrinkStats stats;
	

	public AlcoholLog(String root) {
		super(root);
		int count = 0;
	}
	
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		Gson gson = new Gson();
		stats = gson.fromJson(new String(request.getBody()), DrinkStats.class);
		if(request.getMethod().equals(Protocol.POST)){
			System.out.println("Handling Post");
			stats.count = stats.count + 1;
		}
		if(request.getMethod().equals(Protocol.PUT)){
			System.out.println("Handling Put");
		}
		
		if(request.getMethod().equals(Protocol.DELETE)){
			System.out.println("Handling Delete");
			stats.count = 0;
		}
		if(request.getMethod().equals(Protocol.GET)){
			System.out.println("Handling Get");
			String[] dranks = new String[] {"Hamms", "Hamms Special Light", "Straight Vodka","Cement Mixer","You're an alcoholic","Grasshopper","Gin & Tonic"};
			int rando = ThreadLocalRandom.current().nextInt(0, dranks.length);

			String randomDrink = dranks[rando];
			HttpResponse response = HttpResponseFactory.create200OK(gson.toJson(randomDrink), Protocol.CLOSE);
			return response;
		}	
		
		HttpResponse response = HttpResponseFactory.create200OK(gson.toJson(stats),Protocol.CLOSE);

		return response;
	}

}
