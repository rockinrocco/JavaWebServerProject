 
package servlet;

import protocol.HttpRequest;
import protocol.HttpResponse;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public abstract class IServlet {
	String requestType;
	protected String rootDirectory;
	/*
	 * Create that constructor that gives the type to the super type
	 */
	public IServlet(String type,String root){
		requestType = type;
		rootDirectory = root;
	}
	
	public abstract HttpResponse handleRequest(HttpRequest request);
	
	public abstract String getName();
	
	public String getHttpRequestType(){
		return requestType;
	}
	
}
