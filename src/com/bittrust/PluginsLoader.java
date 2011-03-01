/**
 * @author William Speirs <bill.speirs@gmail.com>
 */
package com.bittrust;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @class PluginsLoader
 */
public class PluginsLoader extends URLClassLoader {
	
	private String pluginDirectory = "";
	
	/**
	 * Loads JAR files from the plugins directory.
	 */
	public PluginsLoader(File pluginsDirectory) {
		super(new URL[] { }, ClassLoader.getSystemClassLoader());
		
		// first load in this JAR
		try {
			String thisJARPath = PluginsLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			addURL(new URL("file:" + thisJARPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// if we don't have a plugin directory, then just return
		if(pluginsDirectory == null)
			return;

		this.pluginDirectory = pluginsDirectory.getAbsolutePath();
		
		// get all of the JAR files in the directory
		File[] jarFiles = pluginsDirectory.listFiles(new JarFileNameFilter());
		
		for(File jarFile:jarFiles) {
			try { addURL(new URL("jar:file:/" + jarFile.getAbsolutePath() + "!/")); }
			catch(MalformedURLException e) { e.printStackTrace(); }
		}
	}
	
	public String getPluginDirectory() {
		return pluginDirectory;
	}
	
	private class JarFileNameFilter implements FilenameFilter {
		public boolean accept(File file, String name) {
			return name.endsWith(".jar");
		}
	}

}
