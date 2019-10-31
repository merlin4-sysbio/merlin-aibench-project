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
 * PluginInfo.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 31/03/2009
 */
package es.uvigo.ei.aibench.repository.info;

import java.util.ArrayList;
import java.util.List;

import org.platonos.pluginengine.PluginEngineException;
import org.platonos.pluginengine.version.PluginVersion;

/**
 * @author Miguel Reboiro Jato
 * @author Hugo Costa (Adapt)
 *
 */
public class PluginInfo {
	
	private final String pluginID;
	private final String host;
	private String uid;
	private String name;
	private String version;
	private String needs;
	private String file;
	private String description;
	private String url;
	private String md5;
	private ArrayList<String> dependencies;
	private ArrayList<String> dependenciesPaths;
	
	private PluginVersion pluginVersion;
	
//	private final ReadWriteLock pluginVersionLock = new ReentrantReadWriteLock();
	
	/**
	 * 
	 * @param pluginID
	 */
	public PluginInfo(String pluginID, String host) {
		if (pluginID == null)
			throw new NullPointerException("The pluginID is null");
		if (host == null)
			throw new NullPointerException("The host is null");
		this.pluginID = pluginID;
		this.host = host;
		if(dependencies == null){
			dependencies = new ArrayList<String>();
			dependenciesPaths = new ArrayList<String>();
		}
		
	}
	
	/**
	 * @param pluginID
	 * @param uid
	 * @param name
	 * @param version
	 * @param needs
	 * @param file
	 */
	public PluginInfo(String pluginID, String host, String uid, String name, String version,
			String needs,String description,String url, String file, String md5, String dependencies, String dependenciesPath) {
		this(pluginID, host);
		this.uid = uid;
		this.name = name;
		this.version = version;
		this.needs = needs;
		this.description = description;
		this.url = url;
		this.file = file;
		this.md5 = md5;
		setDependencies(dependencies);
		setDepFolders(dependenciesPath);
	}
	
	public void setValue(String property, String value) {
		if (property.equalsIgnoreCase("uid")) {
			this.setUID(value);
		} else if (property.equalsIgnoreCase("name")) {
			this.setName(value);
		} else if (property.equalsIgnoreCase("version")) {
			this.setVersion(value);
		} else if (property.equalsIgnoreCase("needs")) {
			this.setNeeds(value);
		} else if (property.equalsIgnoreCase("file")) {
			this.setFile(value);
		} else if (property.equalsIgnoreCase("md5")) {
			this.setMd5(value);
		}
		else if(property.equalsIgnoreCase("description")){
			this.setDescription(value);
		}else if(property.equalsIgnoreCase("url")){
			this.setUrl(value);
		}else if(property.equalsIgnoreCase("dependencies")){	
			this.setDependencies(value);
		}else if(property.equalsIgnoreCase("depFolders")){
			this.setDepFolders(value);
		}
	}

	private void setDepFolders(String value) {
//		System.out.println(value);
		ArrayList<String> vs = InstallInfo.parseStringDep(value);
		this.dependenciesPaths = vs;
	}

//	private ArrayList<String> parseStringDep(String value) {
//		ArrayList<String> output = new ArrayList<String>();
//		
//		String[] data = value.split(separatedDeps);
//		
//		for(String d : data){
//			output.add(d.trim());
//		}
//		
//		return output;
//	}

	private void setDependencies(String value) {
		ArrayList<String> vs = InstallInfo.parseStringDep(value);
		this.dependencies = vs;
	}
	
	

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the pluginID
	 */
	public final String getPluginID() {
		return this.pluginID;
	}
	
	/**
	 * 
	 * @return the host
	 */
	public final String getHost() {
		return this.host;
	}

	/**
	 * @return the uid
	 */
	public String getUID() {
		return this.uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUID(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
//		try {
//			this.pluginVersionLock.readLock().lock();
			
			return this.version;
//		} finally {
//			this.pluginVersionLock.readLock().unlock();
//		}
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
//		try {
//			this.pluginVersionLock.writeLock().lock();

			this.version = version;
			this.pluginVersion = null;
//		} finally {
//			this.pluginVersionLock.writeLock().unlock();
//		}
	}

	/**
	 * @return the needs
	 */
	public String getNeeds() {
		return (this.needs==null)?"":this.needs;
	}

	/**
	 * @param needs the needs to set
	 */
	public void setNeeds(String needs) {
		this.needs = needs;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<DependencyInfo> getListNeeds() {
		if (this.needs == null || this.needs.trim().length() == 0) 
			return new ArrayList<DependencyInfo>(0);
		
		String[] dependencies = needs.trim().split(";");
		List<DependencyInfo> info = new ArrayList<DependencyInfo>(dependencies.length);
		for (String dependency:dependencies) {
			try {
				info.add(new DependencyInfo(dependency));
			} catch (IllegalArgumentException iae) {
				iae.printStackTrace();
			}
		}
		return info;
	}
	

	/**
	 * @return the file
	 */
	public String getFile() {
		return this.file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @param md5
	 */
	public void setMd5(String md5) {
		this.md5 = md5.trim();
	}
	
	/**
	 * 
	 * @param md5
	 * @return
	 */
	public String getMd5() {
		return (this.md5 == null || this.md5.length() == 0)?null:this.md5;
	}
	
	
	
	public ArrayList<String> getDependenciesPaths() {
		return dependenciesPaths;
	}

	public ArrayList<String> getDependencies() {
		return dependencies;
	}

	/**
	 * 
	 * @return
	 */
	public InstallInfo getInstallInfo() {
		return new InstallInfo(
			this.getFile(),
			null,
			this.version,
			this.getMd5(),
			this.getDependencies(),
			this.getDependenciesPaths()
		);
	}
	
	/**
	 * 
	 * @param updatePlugin
	 * @return
	 */
	public InstallInfo getInstallInfo(String updatePlugin) {
		return new InstallInfo(
			this.getFile(),
			updatePlugin,
			this.version,
			this.getMd5(),
			this.getDependencies(),
			this.getDependenciesPaths()
		);
	}
	
	private void createPluginVersion()
	throws PluginEngineException {
//		try {
//			this.pluginVersionLock.writeLock().lock();
//			
			if (this.pluginVersion == null && this.version != null) {
				this.pluginVersion = PluginVersion.createInstanceVersion(this.getVersion());
			}
//		} finally {
//			this.pluginVersionLock.writeLock().unlock();
//		}
	}
	
	public PluginVersion getPluginVersion()
	throws PluginEngineException {
//		try {
//			this.pluginVersionLock.readLock().lock();
			if (this.pluginVersion == null) {
//				this.pluginVersionLock.readLock().unlock();
				this.createPluginVersion();
//				this.pluginVersionLock.readLock().lock();
			}
			
			return this.pluginVersion;
//		} finally {
//			this.pluginVersionLock.readLock().unlock();
//		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String lineFormat = String.format("%s.%%s=%%s\n", this.pluginID);
		String toret = String.format(lineFormat, "uid", this.getUID());
		toret += String.format(lineFormat, "host", this.getHost());
		toret += String.format(lineFormat, "name", (this.getName()==null)?"":this.getVersion());
		toret += String.format(lineFormat, "description", (this.getDescription()==null)?"":this.getDescription());
		toret += String.format(lineFormat, "version", (this.getVersion()==null)?"":this.getVersion());
		toret += String.format(lineFormat, "url", (this.getUrl()==null)?"":this.getUrl());
		toret += String.format(lineFormat, "needs", (this.getNeeds()==null)?"":this.getNeeds());
		toret += String.format(lineFormat, "file", (this.getFile()==null)?"":this.getFile());
		toret += String.format("%s.%s=%s\n", this.pluginID, "md5", (this.getMd5() == null)?"":this.getMd5());
		return toret;
	}
}
