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

package org.platonos.pluginengine.event;

import org.platonos.pluginengine.PluginEngine;

/**
 * Event fired by a PluginEngine.
 * @see PluginEngine#addPluginEngineListener(IPluginEngineListener)
 * @see PluginEngineEventType
 * @author Nathan Sweet (misc@n4te.com)
 */
public final class PluginEngineEvent {
	private final PluginEngineEventType eventType;
	private final PluginEngine pluginEngine;
	private Object payload;

	public PluginEngineEvent (PluginEngineEventType eventType, PluginEngine pluginEngine) {
		this.eventType = eventType;
		this.pluginEngine = pluginEngine;
	}

	public PluginEngineEventType getEventType () {
		return eventType;
	}

	public PluginEngine getPluginEngine () {
		return pluginEngine;
	}

	/**
	 * Returns any extra data associated to the event.
	 */
	public Object getPayload () {
		return payload;
	}

	/**
	 * Sets any extra data associated to the event.
	 */
	public void setPayload (Object payload) {
		this.payload = payload;
	}
}
