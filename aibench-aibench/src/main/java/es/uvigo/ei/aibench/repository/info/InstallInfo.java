/*
Copyright 2007 Daniel Gonzalez Peña, Florentino Fernandez Riverola


This file is part of the AIBench Project. 

AIBench Project is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

AIBench Project is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser Public License for more details.

You should have received a copy of the GNU Lesser Public License
along with AIBench Project.  If not, see <http://www.gnu.org/licenses/>.
*/

/*  
 * InstallInfo.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 01/04/2009
 */
package es.uvigo.ei.aibench.repository.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

/**
 * 
 * @author Miguel Reboiro Jato
 *
 */
public class InstallInfo {
	
	public static String separatedDeps=";";	/**
	 * 
	 */
	private static final String PROPERTY_VERSION = "version";
	/**
	 * 
	 */
	private static final String INSTALL_INFO_FILE = "install.info";
	/**
	 * 
	 */
	private static final String PROPERTY_FILE = "file";
	/**
	 * 
	 */
	private static final String PROPERTY_UPDATEPLUGIN = "updateplugin";
	/**
	 * 
	 */
	private static final String PROPERTY_MD5 = "md5";
	
	private static final String PROPERTY_DEPENDENCIES = "dependencies";
	
	private static final String PROPERTY_DEPENDENCY_FOLDER = "depFolders";
	
	
	public final String file;
	public final String updatePlugin;
	public final String version;
	public final String md5;
	public final ArrayList<String> dependencies;
	public final ArrayList<String> depFolders;
	
	public InstallInfo(String file, String updatePlugin, String version, String md5, ArrayList<String> deps,  ArrayList<String> depsFolders) {
		this.file = file;
		this.updatePlugin = updatePlugin;
		this.version = version;
		this.md5 = md5;
		this.dependencies = deps;
		this.depFolders = depsFolders;
	}
	
	public InstallInfo(File dir) 
	throws IOException {
		Properties properties = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(dir, InstallInfo.INSTALL_INFO_FILE));
			properties.load(fis);
			
			this.file = properties.getProperty(InstallInfo.PROPERTY_FILE);
			this.updatePlugin = properties.getProperty(InstallInfo.PROPERTY_UPDATEPLUGIN);
			this.version = properties.getProperty(InstallInfo.PROPERTY_VERSION);
			this.md5 = properties.getProperty(InstallInfo.PROPERTY_MD5);
			this.dependencies = parseStringDep((String) properties.get(InstallInfo.PROPERTY_DEPENDENCIES));
			this.depFolders = parseStringDep((String) properties.get(InstallInfo.PROPERTY_DEPENDENCY_FOLDER));
			
			if(dependencies.size() != depFolders.size()){
				throw new IOException("Configuration problems in" +InstallInfo.PROPERTY_DEPENDENCIES + " and " + InstallInfo.PROPERTY_DEPENDENCY_FOLDER);
			}
		} finally {
			try {
				if (fis != null) fis.close();
			} catch(IOException ioe) {}
		}
	}
	
	public void store(File dir) 
	throws IOException {
		Properties properties = new Properties();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(dir, InstallInfo.INSTALL_INFO_FILE));
			
			if (this.file != null)
				properties.setProperty(InstallInfo.PROPERTY_FILE, this.file);
			if (this.updatePlugin != null)
				properties.setProperty(InstallInfo.PROPERTY_UPDATEPLUGIN, this.updatePlugin);
			if (this.version != null)
				properties.setProperty(InstallInfo.PROPERTY_VERSION, this.version);
			if (this.md5 != null)
				properties.setProperty(InstallInfo.PROPERTY_MD5, this.md5);
			if(dependencies != null && dependencies.size()>0){
//				System.out.println(dependencies);
//				System.out.println(depFolders);
				properties.setProperty(InstallInfo.PROPERTY_DEPENDENCIES, toStringDeps(dependencies));
				properties.setProperty(InstallInfo.PROPERTY_DEPENDENCY_FOLDER, toStringDeps(depFolders));
			}
			properties.store(fos, null);
		} finally {
			try {
				if (fos != null) {
					fos.flush();
					fos.close();
				}
			} catch(IOException ioe) {}
		}
	}
	
	public static ArrayList<String> parseStringDep(String value) {
		ArrayList<String> output = new ArrayList<String>();
		
		if(value!=null){
			String[] data = value.split(separatedDeps);
			
			for(String d : data){
				output.add(d.trim());
			}
		}
		
		return output;
	}
	
	public static String toStringDeps(ArrayList<String> values){
		String ret = "";
		
//		if(values!=null){
			Iterator<?> iterator = values.iterator();
			while(iterator.hasNext()) {
				Object o = iterator.next();
				ret += o;
				if(iterator.hasNext()) ret += separatedDeps;
			}
//		}
		return ret;
	}
}