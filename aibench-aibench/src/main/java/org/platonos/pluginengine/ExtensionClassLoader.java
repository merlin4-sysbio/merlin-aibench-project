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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.platonos.pluginengine.logging.LoggerLevel;

/**
 * Loads only the extension class for an Extension. This classloader is needed to allow a Plugin with one or more ExtensionPoints
 * to unload/reload without the need to unload other Plugins that have attached Extensions. By using this classloader, either the
 * Extension's Plugin or the ExtensionPoint's Plugin can be completely unloaded without unloading both Plugins.
 * @see Extension#getExtensionInstance()
 * @see Plugin#setReloadable(boolean)
 * @author Evert jr
 * @author Nathan Sweet (misc@n4te.com)
 * @author Kevin Duffey (kevinmduffey@yahoo.com)
 */
final class ExtensionClassLoader extends ClassLoader {

	PluginClassLoader pluginClassLoader;

	Extension extension;

	ExtensionClassLoader (Extension extension) {
		this.extension = extension;
		pluginClassLoader = extension.getPlugin().pluginClassloader;
	}

	/**
	 * Delegates to the PluginClassLoader of this ExtensionClassLoader to find classes that are used by the extension class.
	 * @throws ClassNotFoundException when a class couldn't be found.
	 */
	public synchronized Class<?> loadClass (String className) throws ClassNotFoundException {
		if (isExtensionClass(className)) {
			Class<?> clazz = loadExtensionClass(className);
			if (clazz == null) throw new ClassNotFoundException(className);
			return clazz;
		} else {
			return pluginClassLoader.loadClass(className);
		}
	}

	/**
	 * Returns true if the specified class should be loaded through this ExtensionClassLoader.
	 */
	boolean isExtensionClass (String className) {
		int dollarIndex = className.indexOf('$');
		if (dollarIndex == -1) {
			return className.equals(extension.getExtensionClassName());
		} else {
			return className.substring(0, dollarIndex).equals(extension.getExtensionClassName());
		}
	}

	/**
	 * Returns the extension Class. No other classes are loaded by this method.
	 */
	synchronized Class<?> loadExtensionClass (String className) {
		synchronized (pluginClassLoader) {
			Class<?> clazz = findLoadedClass(className);
			if (clazz != null) {
				// Make class access start the plugin.
				if (!extension.getPlugin().start()) {
					extension.getPluginEngine().getLogger().log(
						LoggerLevel.SEVERE,
						"Unable to start Plugin \"" + extension.getPlugin() + "\". Extension class cannot be acquired: "
							+ className, null);
					return null;
				}
				return clazz;
			}

			// Use code from PluginClassLoader to load the bytecodes. getResource will cause the Plugin to start.
			URL classResource = pluginClassLoader.getResource(className.replace('.', '/') + ".class");
			if (classResource == null) {
				extension.getPluginEngine().getLogger().log(LoggerLevel.SEVERE,
					"Extension class not found in Plugin \"" + extension.getPlugin() + "\": " + className, null);
				return null;
			}

			byte[] data;
			try {
				InputStream input = classResource.openStream();
				data = new byte[input.available()];
				int off = 0, len = data.length, read;
				while ((read = input.read(data, off, len)) > 0) {
					off += read;
					len -= read;
				}
			} catch (IOException ex) {
				extension.getPluginEngine().getLogger().log(LoggerLevel.SEVERE, "Error loading extension class: " + className,
					ex);
				return null;
			}

			return defineClass(className, data, 0, data.length);
		}
	}

	/*
	 * Delegates to the PluginClassLoader of this ExtensionClassLoader to find a native library.
	 */
	protected String findLibrary (String libName) {
		return pluginClassLoader.findLibrary(libName);
	}

	/**
	 * Delegates to the PluginClassLoader of this ExtensionClassLoader to find a resource.
	 */
	public URL getResource (String name) {
		return pluginClassLoader.getResource(name);
	}
}