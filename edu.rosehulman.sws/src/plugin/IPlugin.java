package plugin;

import java.util.HashMap;

import servlet.IServlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public abstract class IPlugin implements Runnable{
	public HashMap<String,IServlet> servlets;
	
	public IPlugin(){
		servlets = new HashMap<String,IServlet>();
	}
	public abstract void init();
	public boolean addServlet(IServlet servlet){
		String type = servlet.getHttpRequestType();
		if(type==null){
			return false;
		}
		String servClass = servlet.getClass().toString();
		String key=type.toUpperCase()+":"+servClass;
		if(servlets.containsKey(key)){
			return false;
		}else{
			servlets.put(key, servlet);
			return true;
		}
	}
	public boolean removeServlet(IServlet servlet){
		String type = servlet.getHttpRequestType();
		if(type==null){
			return false;
		}
		String servClass = servlet.getClass().toString();
		String key=type.toUpperCase()+":"+servClass;
		if(servlets.containsKey(key)){
			return false;
		}else{
			servlets.remove(key);
			return true;
		}
	}
	
	public abstract void run();

	public HttpResponse handleRequest(HttpRequest request, String uri, String rootDirectory){
		String key=request.getMethod().toUpperCase()+":"+uri;
		if(servlets.containsKey(key)){
			return servlets.get(key).handleRequest(request);
		}else{
			return HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
	}
	
}
