package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.io.InputStream;

/**
 * @author Matt and Paul Initial idea from
 *         http://examples.javacodegeeks.com/core
 *         -java/dynamic-class-loading-example/
 * 
 */
public class JavaClassLoader extends ClassLoader {

	/**
	 * @param parentClassLoader
	 */
	public HashSet<String> loadedClasses = new HashSet<String>();

	public JavaClassLoader(ClassLoader parent, HashSet<String> loadedClasses) {
		super(parent);
		this.loadedClasses = loadedClasses;
	}

	public Class<?> loadNewClass(String className, String classBinName)
			throws ClassNotFoundException {
		return super.loadClass(classBinName);
	} // we've loaded the class before, time to change it by hand

	public Class<?> reloadClass(String className, String classBinName) {
		try {
			File file = new File("");
			String url = "file:" +file.getAbsolutePath() + "/bin/pluginImp/" + className
					+ ".class";
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			InputStream input = connection.getInputStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = input.read();
			while (data != -1) {
				buffer.write(data);
				data = input.read();
			}
			input.close();

			byte[] classData = buffer.toByteArray();

			return defineClass(classBinName, classData, 0, classData.length);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
