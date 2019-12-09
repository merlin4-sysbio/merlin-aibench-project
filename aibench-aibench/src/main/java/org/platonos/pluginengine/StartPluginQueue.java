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

package org.platonos.pluginengine;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.platonos.pluginengine.event.PluginEngineEvent;
import org.platonos.pluginengine.event.PluginEngineEventType;
import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * Queue of Plugins that have thier PluginLifecycle#start() method invoked as soon as possible by multiple threads.
 * @author Nathan Sweet (misc@n4te.com)
 * @author Brian Goetz (brian@quiotix.com)
 */
final class StartPluginQueue {
	private final PluginEngine pluginEngine;
	private final Thread[] threads;
	private final LinkedList<Plugin> pluginQueue = new LinkedList<Plugin>();
	private boolean shutdown = false;
	private int currentMaximumOrder = -1;
	private int startingCounter = 0;
	
	final static Comparator<Plugin> PLUGIN_START_ORDER_COMPARATOR = new Comparator<Plugin>() {
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Plugin o1, Plugin o2) {
			if (o1.getStartOrder() == o2.getStartOrder()) {
				return o1.getUID().compareTo(o2.getUID());
			} else {
				return (o1.getStartOrder() < o2.getStartOrder())?-1:1;
			}
		}
	};

	StartPluginQueue (PluginEngine pluginEngine) {
		this.pluginEngine = pluginEngine;

		threads = new Thread[pluginEngine.startPluginThreadCount];
		for (int i = 0; i < pluginEngine.startPluginThreadCount; i++) {
			threads[i] = new Thread(new PoolWorker(), "PluginStart" + i);
			threads[i].start();
		}
	}
	
	private boolean canExecute(Plugin plugin) {
		synchronized (pluginQueue) {
			return plugin.getStartOrder() == this.currentMaximumOrder;
		}
	}
	
	private void pluginStarting(Plugin plugin) {
		synchronized (pluginQueue) {
			this.startingCounter++;		
		}
	}
	
	private void pluginStarted(Plugin plugin) {
		synchronized (pluginQueue) {
			this.startingCounter--;
			if (this.startingCounter == 0) {
				if (this.pluginQueue.isEmpty()) {
					this.currentMaximumOrder = -1;
				} else {
					this.currentMaximumOrder = this.pluginQueue.getFirst().getStartOrder();
				}
			}
		}
	}

	void add (Plugin plugin) {
		if (threads.length == 0) return;
		synchronized (pluginQueue) {
			pluginQueue.addLast(plugin);
			Collections.sort(this.pluginQueue, StartPluginQueue.PLUGIN_START_ORDER_COMPARATOR); // TODO: Change to add elements directly in its correct position.
			if (this.startingCounter == 0)
				this.currentMaximumOrder = this.pluginQueue.getFirst().getStartOrder();
			pluginQueue.notify();
		}
	}

	void shutdown () {
		shutdown = true;
		synchronized (pluginQueue) {
			pluginQueue.notifyAll();
		}
	}

	public class PoolWorker implements Runnable {
		Plugin plugin;
		public void run () {
			while (true) {
				synchronized (pluginQueue) {
					while (pluginQueue.isEmpty() || !canExecute(pluginQueue.getFirst())) {
						try {
							pluginQueue.wait();
						} catch (InterruptedException ignored) {}
						if (shutdown) return;
					}
					plugin = (Plugin)pluginQueue.removeFirst();
					pluginStarting(plugin);
				}

				try {
					// The Plugin could be stopped after its PluginLifecycle was created but before it got here.
					if (!plugin.isStarted()) return;
					if (plugin.lifecycleInstance != null){
						PluginEngineEvent engineEvent = new PluginEngineEvent(PluginEngineEventType.PLUGIN_STARTING, pluginEngine);
						engineEvent.setPayload(plugin);
						pluginEngine.firePluginEngineEvent(engineEvent);
						plugin.lifecycleInstance.start();
					}

					PluginEngineEvent engineEvent = new PluginEngineEvent(PluginEngineEventType.PLUGIN_STARTED, pluginEngine);
					engineEvent.setPayload(plugin);
					pluginEngine.firePluginEngineEvent(engineEvent);
				} catch (RuntimeException ex) {
					ex.printStackTrace();
					System.out.println("ex cause "+ex.getCause());
					System.out.println("ex message "+ex.getMessage());
					// Must catch RuntimeException or the pool could leak threads.
					pluginEngine.getLogger().log(LoggerLevel.SEVERE, "Uncaught exception during Plugin start: " + plugin, ex);
				}
				
				if (plugin != null) {
					pluginStarted(plugin);
				}				
			}
		}
		/**
		 * @return the plugin
		 */
		public Plugin getPlugin() {
			return this.plugin;
		}
	}
}
