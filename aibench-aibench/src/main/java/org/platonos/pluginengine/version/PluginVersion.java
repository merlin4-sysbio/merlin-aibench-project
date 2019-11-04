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

package org.platonos.pluginengine.version;

import java.util.regex.Pattern;

import org.platonos.pluginengine.PluginEngineException;


/**
 * Represents the version of a Plugin and allows versions to be compared. This class is immutable.
 * @author Nathan Sweet (misc@n4te.com)
 */
public abstract class PluginVersion implements Comparable<PluginVersion> {
	public final static String TEXT_VERSION_PATTERN = 
		"\\p{Digit}+(\\.\\p{Digit}+(\\.\\p{Digit}+)?)?";
	public final static String TEXT_INSTANCE_VERSION_PATTERN = 
		"\\p{Digit}+(\\.\\p{Digit}+(\\.\\p{Digit}+(\\p{Space}+\\p{Digit}+)?)?)?";
	public final static String TEXT_INTERVAL_VERSION_PATTERN = 
		String.format("%s,%s", PluginVersion.TEXT_VERSION_PATTERN, PluginVersion.TEXT_VERSION_PATTERN);
	public final static String TEXT_MIN_VERSION_PATTERN = 
		"\\p{Digit}+(\\.\\p{Digit}+(\\.\\p{Digit}+-?|-)?|-)?";
	public final static String TEXT_MAX_VERSION_PATTERN = 
		"\\p{Digit}+(\\.\\p{Digit}+(\\.\\p{Digit}+|\\+?||\\+)?|\\+)?";

	public final static Pattern VERSION_PATTERN = 
		Pattern.compile(String.format("^%s$", PluginVersion.TEXT_VERSION_PATTERN));
	public final static Pattern INSTANCE_VERSION_PATTERN = 
		Pattern.compile(String.format("^%s$", PluginVersion.TEXT_INSTANCE_VERSION_PATTERN));
	public final static Pattern INTERVAL_VERSION_PATTERN = 
		Pattern.compile(String.format("^%s$", PluginVersion.TEXT_INTERVAL_VERSION_PATTERN));
	public final static Pattern MIN_VERSION_PATTERN = 
		Pattern.compile(String.format("^%s$", PluginVersion.TEXT_MIN_VERSION_PATTERN));
	public final static Pattern MAX_VERSION_PATTERN = 
		Pattern.compile(String.format("^%s$", PluginVersion.TEXT_MAX_VERSION_PATTERN));
	
	public final static boolean isValidInstanceVersion(String version) {
		return PluginVersion.INSTANCE_VERSION_PATTERN.matcher(version).matches();
	}
	
	public final static boolean isValidDependencyVersion(String version) {
		return PluginVersion.VERSION_PATTERN.matcher(version).matches()
			|| PluginVersion.INTERVAL_VERSION_PATTERN.matcher(version).matches()
			|| PluginVersion.MIN_VERSION_PATTERN.matcher(version).matches()
			|| PluginVersion.MAX_VERSION_PATTERN.matcher(version).matches();
	}
	
	public static final PluginVersion createDependencyVersion(String version) 
	throws PluginEngineException {
		if (PluginVersion.VERSION_PATTERN.matcher(version).matches()) {
			String[] versions = version.split("\\.");
			
			try {
				switch(versions.length) {
				case 1:
					return new PluginDependencySingleVersion(
						Integer.parseInt(versions[0])
					);
				case 2:
					return new PluginDependencySingleVersion(
						Integer.parseInt(versions[0]),
						Integer.parseInt(versions[1])
					);
				case 3:
					return new PluginDependencySingleVersion(
						Integer.parseInt(versions[0]),
						Integer.parseInt(versions[1]),
						Integer.parseInt(versions[2])
					);
				}
			} catch (NumberFormatException nfe) {}
		} else if (PluginVersion.INTERVAL_VERSION_PATTERN.matcher(version).matches()) {
			String[] versions = version.split(",");
			if (versions.length == 2) {
				PluginVersion minVersion = PluginVersion.createDependencyVersion(versions[0]);
				PluginVersion maxVersion = PluginVersion.createDependencyVersion(versions[1]);
				if (minVersion instanceof PluginDependencySingleVersion
					&& maxVersion instanceof PluginDependencySingleVersion) {
					return new PluginDependencyMinMaxVersion(
						(PluginDependencySingleVersion) minVersion,
						(PluginDependencySingleVersion) maxVersion
					);
				}
			}
		} else if (PluginVersion.MIN_VERSION_PATTERN.matcher(version).matches()) {
			String cleanVersion = version.substring(0, version.length()-1);
			PluginVersion minVersion = PluginVersion.createDependencyVersion(cleanVersion);
			if (minVersion instanceof PluginDependencySingleVersion) {
				return new PluginDependencyMinMaxVersion(
					(PluginDependencySingleVersion) minVersion,
					PluginDependencyMinMaxVersion.Type.MIN
				);
			}
		} else if (PluginVersion.MAX_VERSION_PATTERN.matcher(version).matches()) {
			String cleanVersion = version.substring(0, version.length()-1);
			PluginVersion maxVersion = PluginVersion.createDependencyVersion(cleanVersion);
			if (maxVersion instanceof PluginDependencySingleVersion) {
				return new PluginDependencyMinMaxVersion(
						(PluginDependencySingleVersion) maxVersion,
						PluginDependencyMinMaxVersion.Type.MAX
				);
			}
		}
		throw new PluginEngineException("Invalid instance version: " + version);
	}
	
	public static final PluginVersion createInstanceVersion(String version) 
	throws PluginEngineException {
		if (PluginVersion.isValidInstanceVersion(version)) {
			String cleanVersion = version.replaceAll("\\p{Space}+", ".");
			String[] versions = cleanVersion.split("\\.");
			
			try {
				switch(versions.length) {
				case 1:
					return new PluginInstanceVersion(Integer.parseInt(versions[0]));
				case 2:
					return new PluginInstanceVersion(
						Integer.parseInt(versions[0]),
						Integer.parseInt(versions[1])
					);
				case 3:
					return new PluginInstanceVersion(
						Integer.parseInt(versions[0]),
						Integer.parseInt(versions[1]),
						Integer.parseInt(versions[2])
					);
				case 4:
					return new PluginInstanceVersion(
						Integer.parseInt(versions[0]),
						Integer.parseInt(versions[1]),
						Integer.parseInt(versions[2]),
						versions[3]
					);
				default:
					throw new PluginEngineException("Invalid instance version: " + version);
				}
			} catch (NumberFormatException nfe) {
				throw new PluginEngineException("Invalid instance version: " + version);
			}
		} else {
			throw new PluginEngineException("Invalid instance version: " + version);
		}
		
	}

	/**
	 * Returns a String representation of this version, excluding the build version.
	 */
	public abstract String getFullVersion ();

	/**
	 * Compares this PluginVersion with the specified PluginVersion.
	 * @return A negative int, zero, or a positive int if this PluginVersion is less than, equal to, or greater than the specified
	 *         PluginVersion.
	 */
	public final int compareTo(PluginVersion requiredVersion) {
		if (requiredVersion instanceof PluginInstanceVersion) {
			return this.compareTo((PluginInstanceVersion) requiredVersion);
		} else if (requiredVersion instanceof PluginDependencySingleVersion) {
			return this.compareTo((PluginDependencySingleVersion) requiredVersion);
		} else if (requiredVersion instanceof PluginDependencyMinMaxVersion) {
			return this.compareTo((PluginDependencyMinMaxVersion) requiredVersion);
		} else {
			return 0;
		}
	}

	
	public abstract int compareTo(PluginDependencyMinMaxVersion version);
	
	public abstract int compareTo(PluginDependencySingleVersion version);
	
	public abstract int compareTo(PluginInstanceVersion version);
}
