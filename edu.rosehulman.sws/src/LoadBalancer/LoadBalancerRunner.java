/*
 * LoadBalancerRunner.java
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

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class LoadBalancerRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		System.out.println("Load balancer will run on 8080");
		ArrayList<Integer> servers = new ArrayList<Integer>();
		int num;
		Scanner in = new Scanner(System.in);
		int i= 0;
		System.out.println("Enter the ports for your servers. Type 0 to quit");
		while(true){
			i++;
			System.out.print("Server " + i +": ");
			num = in.nextInt();
			if(num == 0){
				break;
			}
			servers.add(num);
		}
		LoadBalancer balancer = new LoadBalancer(servers,8080);
		
		new Thread(balancer).start();
		
		

	}
}
