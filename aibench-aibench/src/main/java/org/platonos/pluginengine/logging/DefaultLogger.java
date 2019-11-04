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

package org.platonos.pluginengine.logging;

/**
 * Simple ILogger implementation that provides rudimentary logging if the application using the PluginEngine does not provide it's
 * own implementation.
 * @author Kevin Duffey (kevinmduffey@yahoo.com)
 * @author Evert
 * @author Nathan Sweet (misc@n4te.com)
 */
public final class DefaultLogger implements ILogger {
	/**
	 * Logs a message to the console.
	 */
	public void log (LoggerLevel level, String message, Throwable thr) {
		if (level == LoggerLevel.SEVERE || level == LoggerLevel.WARNING) {
			System.err.println("[" + level + "] " + message);
			if (thr != null) thr.printStackTrace();
		} else
			System.out.println("[" + level + "] " + message);
	}
}
