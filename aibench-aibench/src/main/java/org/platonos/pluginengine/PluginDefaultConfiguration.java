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
 * PluginDefaultConfiguration.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 30/03/2009
 */
package org.platonos.pluginengine;

/**
 * @author Daniel Glez-Peña
 *
 */
public class PluginDefaultConfiguration implements IPluginConfiguration {

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isEnabledPlugin(org.platonos.pluginengine.Plugin)
	 */
	public boolean isEnabledPlugin(Plugin plugin) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isEnabledPlugin(java.lang.String)
	 */
	public boolean isEnabledPlugin(String pluginUID) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isLoadPlugin(org.platonos.pluginengine.Plugin)
	 */
	public boolean isLoadPlugin(Plugin plugin) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#isLoadPlugin(java.lang.String)
	 */
	public boolean isLoadPlugin(String pluginUID) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setEnabledPlugin(org.platonos.pluginengine.Plugin, boolean)
	 */
	public void setEnabledPlugin(Plugin plugin, boolean value) {}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setEnabledPlugin(java.lang.String, boolean)
	 */
	public void setEnabledPlugin(String pluginUID, boolean value) {}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setLoadPlugin(org.platonos.pluginengine.Plugin, boolean)
	 */
	public void setLoadPlugin(Plugin plugin, boolean value) {}

	/* (non-Javadoc)
	 * @see org.platonos.pluginengine.IPluginConfiguration#setLoadPlugin(java.lang.String, boolean)
	 */
	public void setLoadPlugin(String pluginUID, boolean value) {}
}
