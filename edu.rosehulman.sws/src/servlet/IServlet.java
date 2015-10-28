 
package servlet;

import protocol.HttpRequest;
import protocol.HttpResponse;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public abstract class IServlet {
	protected String rootDirectory;
	/*
	 * Create that constructor that gives the type to the super type
	 */
	public IServlet(String root){
		rootDirectory = root;
	}
	
	public abstract HttpResponse handleRequest(HttpRequest request);
	
}
