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
 * PluginConfiguration.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 20/03/2009
 */
package org.platonos.pluginengine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import org.platonos.pluginengine.logging.DefaultLogger;
import org.platonos.pluginengine.logging.ILogger;
import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * @author Miguel Reboiro Jato
 *
 */
public class PluginFileConfiguration implements IPluginConfiguration {
	private static final ILogger logger = new DefaultLogger();
	/**
	 * 
	 */
	private static final String DEFAULT_OPTION_ENABLED = "enabled";
	
	/**
	 * 
	 */
	private static final String DEFAULT_OPTION_LOAD = "load";
	
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1L;
	
	private final Properties properties;
	private final URL configurationFile;
	private final String enabledSuffix, loadSuffix;
	private final boolean enabledByDefault;
	private final boolean loadByDefault;
	
	public PluginFileConfiguration(URL configurationFile)
	throws IOException {
		this(configurationFile, PluginFileConfiguration.DEFAULT_OPTION_LOAD, PluginFileConfiguration.DEFAULT_OPTION_ENABLED);
	}
	
	public PluginFileConfiguration(URL configurationFile, String loadSuffix, String enabledSuffix)
	throws IOException {
		this.properties = new Properties();
		this.configurationFile = configurationFile;
		this.loadSuffix = loadSuffix;
		this.enabledSuffix = enabledSuffix;
		try {
			InputStream fis = configurationFile.openStream();
			this.properties.load(fis);
			fis.close();
			
			this.enabledByDefault = Boolean.parseBoolean(this.properties.getProperty("default.enabled", "true"));
			this.loadByDefault = Boolean.parseBoolean(this.properties.getProperty("default.load", "true"));
		} catch (IOException e) {
			throw e;
		}
	}
	
	public PluginFileConfiguration(File configurationFile)
	throws IOException {
		this(configurationFile.toURI().toURL());
	}
	
	/**
	 * Constructs a new PluginConfiguration loading the configuration from the <code>conf/plugins.conf</code> file. 
	 * @throws IOException 
	 */
	public PluginFileConfiguration(File configurationFile, String loadSuffix, String enabledSuffix)
	throws IOException {
		this(configurationFile.toURI().toURL(), loadSuffix, enabledSuffix);
	}
	
	private void storeProperties() {
		try {
			String res = URLDecoder.decode(this.configurationFile.getPath(), "utf-8");
			File file = new File(res);
			FileOutputStream os = new FileOutputStream(file);
			this.properties.store(os, null);
			os.flush();
			os.close();
		} catch (IOException e) {
			PluginFileConfiguration.logger.log(LoggerLevel.WARNING, "Plugin configuration file couldn't be modified.", e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isLoadPlugin(org.platonos.pluginengine.Plugin)
	 */
	public boolean isLoadPlugin(Plugin plugin) {
		if (plugin == null) return false;
		else return this.isLoadPlugin(plugin.getUID());
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isLoadPlugin(java.lang.String)
	 */
	public boolean isLoadPlugin(String pluginUID) {
		return Boolean.parseBoolean(this.properties.getProperty(String.format("%s.%s", pluginUID, this.loadSuffix), Boolean.toString(this.loadByDefault)));
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isEnabledPlugin(org.platonos.pluginengine.Plugin)
	 */
	public boolean isEnabledPlugin(Plugin plugin) {
		if (plugin == null) return false;
		else return this.isEnabledPlugin(plugin.getUID());
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isEnabledPlugin(java.lang.String)
	 */
	public boolean isEnabledPlugin(String pluginUID) {
		return Boolean.parseBoolean(this.properties.getProperty(String.format("%s.%s", pluginUID, this.enabledSuffix), Boolean.toString(this.enabledByDefault)));
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setLoadPlugin(org.platonos.pluginengine.Plugin, boolean)
	 */
	public void setLoadPlugin(Plugin plugin, boolean value) {
		this.setLoadPlugin(plugin.getUID(), value);
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setLoadPlugin(java.lang.String, boolean)
	 */
	public void setLoadPlugin(String pluginUID, boolean value) {
		this.properties.setProperty(String.format("%s.%s", pluginUID, this.loadSuffix), Boolean.toString(value));
		this.storeProperties();
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setEnabledPlugin(org.platonos.pluginengine.Plugin, boolean)
	 */
	public void setEnabledPlugin(Plugin plugin, boolean value) {
		this.setEnabledPlugin(plugin.getUID(), value);
	}
	
	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setEnabledPlugin(java.lang.String, boolean)
	 */
	public void setEnabledPlugin(String pluginUID, boolean value) {
		this.properties.setProperty(String.format("%s.%s", pluginUID, this.enabledSuffix), Boolean.toString(value));
		this.storeProperties();
	}
}
