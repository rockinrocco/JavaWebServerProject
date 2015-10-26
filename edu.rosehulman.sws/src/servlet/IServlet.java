 
package servlet;

import protocol.HttpRequest;
import protocol.HttpResponse;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public abstract class IServlet {
	String requestType;
	/*
	 * Create that constructor that gives the type to the super type
	 */
	public IServlet(String type){
		requestType = type;
	}
	
	public abstract HttpResponse handleRequest(HttpRequest request);
	
	public abstract String getName();
	
	public String getHttpRequestType(){
		return requestType;
	}
	
}
