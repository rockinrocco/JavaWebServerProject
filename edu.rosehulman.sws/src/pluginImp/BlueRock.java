/*
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
 
package pluginImp;

import java.util.HashMap;
import java.util.HashSet;

import AbstractPlugin.AbstractPlugin;
import servlet.FileDeleteGood;
import servlet.FileGetGood;
import servlet.FilePostGood;
import servlet.GetServlet3Page;
import servlet.HttpWorks;
import servlet.LoadHtmlPage;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public class BlueRock extends AbstractPlugin{

	/**
	 * @param root
	 */
	public BlueRock() {
	}

	@Override
	public void init(String root) {
		super.init(root);
		addServlet("Servlet1","GET", new LoadHtmlPage(rootDirectory, "blueRock.html"));

		addServlet("Servlet2","GET", new LoadHtmlPage(rootDirectory, "blueRock.html"));

	}

	@Override
	public void run() {

	}

	@Override
	public String getName() {
		return "BlueRock";
	}

	/**
	 * @return
	 */
	public char[] getClassPath() {
		// TODO Auto-generated method stub
		return null;
	}

}
