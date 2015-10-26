package server;

public class JarClassLoader extends MultiClassLoader{
    private JarResources jarResources;
    
    public JarClassLoader(String jarName)
    {
    	jarResources = new JarResources(jarName);
    }
	@Override
	protected byte[] loadClassBytes(String className) {
		// TODO Auto-generated method stub
	    className = formatClassName (className);
	    // Attempt to get the class data from the JarResource.
	    return (jarResources.getResource (className));

	}

}
