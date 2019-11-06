/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package es.uvigo.ei.sing.aibench.pluginmanager.lifecycle;

import org.platonos.pluginengine.PluginLifecycle;

import es.uvigo.ei.aibench.core.Core;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.Workbench;
import es.uvigo.ei.sing.aibench.pluginmanager.PluginManager;

/**
 * @author Hugo Costa
 */
public class Lifecycle extends PluginLifecycle {
	
	
	public void start(){
		try {
			if(PluginManager.getInstance().isStartAutoUpdates() && PluginManager.getInstance().existUpdatesFromRepository(true).size() > 0)
			{
				for (@SuppressWarnings("rawtypes") OperationDefinition def : Core.getInstance().getOperations()){
					if (def.getID().equals("aibench.pluginmanager.updates")){			
						Workbench.getInstance().executeOperation(def);
					}
				}		
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	
}
