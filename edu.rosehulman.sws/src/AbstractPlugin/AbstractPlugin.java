package AbstractPlugin;

import java.util.HashMap;
import java.util.HashSet;

import servlet.IServlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Matt Rocco and Paul Bliudzius
 */
public abstract class AbstractPlugin implements Runnable{
	protected HashMap<String,IServlet> servlets;
	protected String rootDirectory;
	protected HashMap<String, HashSet<String>> containers;

	
	public AbstractPlugin(){
		servlets = new HashMap<String,IServlet>();
		containers = new HashMap<String, HashSet<String>>();
	}
	
	public abstract String getName();
	
	public void init(String root){
		rootDirectory = root;
		System.out.println("Plugin Added:"+getName());
	}
	public boolean addServlet(String path, String type, IServlet servlet){
		String servClass = path;
		String key=type.toUpperCase()+":"+servClass;
		if(servlets.containsKey(key)){
			return false;
		}else{
			servlets.put(key, servlet);
			return true;
		}
	}
	
	public abstract void run();

	public HttpResponse handleRequest(HttpRequest request, String uri, String rootDirectory){
		String key=request.getMethod().toUpperCase()+":"+uri;
		System.out.println("Uri:"+key);
		if(servlets.containsKey(key)){
			System.out.println("found servlet");
			return servlets.get(key).handleRequest(request);
		}else{
			return HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
	}
	
}
