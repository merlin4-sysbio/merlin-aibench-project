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
 * Inflater.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 01/04/2009
 */
package es.uvigo.ei.aibench.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;

/**
 * @author Miguel Reboiro Jato
 *
 */
public class Inflater {
	/**
	 * 
	 */
	private static final int BUFFER_SIZE = 8192;

	public static Set<String> inflate(File source, File output) {
		Set<String> createdFiles = new HashSet<String>();
		if (Inflater.inflatableFile(source)) {
			String sourceName = source.getName();
			try {
				if (sourceName.endsWith("jar")) {
					Inflater.copyFile(source, output);
				} else if (sourceName.endsWith("zip")) {
					createdFiles = Inflater.inflateZip(source, output);
				} else if (sourceName.endsWith("tar.gz")) {
					createdFiles = Inflater.inflateTarGz(source, output);
				} else if (sourceName.endsWith("tar")) {
					createdFiles = Inflater.inflateTar(source, output);
				}
			} catch (IOException e) {
				e.printStackTrace();
				createdFiles = null;
			}
		} else {
			createdFiles = null;
		}
		return createdFiles;
	}
	
	public static boolean inflatableFile(File source) {
		return Inflater.inflatableFile(source.getName());
	}
	
	public static boolean inflatableFile(String fileName) {
		return fileName.endsWith(".jar") ||
			fileName.endsWith(".zip") ||
			fileName.endsWith(".tar.gz") ||
			fileName.endsWith(".tar");
	}

	private static void copyFile(File source, File output)
	throws IOException {
		FileChannel sourceChannel = null, outputChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			outputChannel = new FileOutputStream(output).getChannel();
			
			sourceChannel.transferTo(0, sourceChannel.size(), outputChannel);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				sourceChannel.close();
			} catch (IOException e) {}
			try {
				outputChannel.close();
			} catch (IOException e) {}
		}
	}
	

	private static Set<String> inflateZip(File source, File output)
	throws IOException {
		
		Set<String> createdFiles = new HashSet<String>();
		if (output.exists() || output.mkdir()) {
			ZipInputStream zis = null;
			BufferedOutputStream bos = null;
			try {
				zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(source), Inflater.BUFFER_SIZE));
				ZipEntry entry;
				byte[] data = new byte[Inflater.BUFFER_SIZE];
				int len;
				File file;
				while ((entry = zis.getNextEntry()) != null) {
					file = new File(output, entry.getName());
					if (entry.isDirectory()) {
						file.mkdir();
					} else {
						file.getParentFile().mkdirs();
						createdFiles.add(file.getAbsolutePath());
						bos = new BufferedOutputStream(new FileOutputStream(file), Inflater.BUFFER_SIZE);
						while ((len = zis.read(data)) != -1) {
							bos.write(data, 0, len);
						}
						bos.flush();
						bos.close();
						bos = null;
					}
				}
				
				zis.close();
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				try {
					if (zis != null) zis.close();
				} catch (IOException ioe) {}
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (IOException ioe) {}
			}

			return createdFiles;
		} else {
			return null;
		}
	}

	private static Set<String> inflateTarGz(File source, File output)
	throws IOException {
		
		Set<String> createdFiles = new HashSet<String>();
		if (output.mkdir()) {
			BufferedOutputStream bos = null;
			TarInputStream tis = null;
			try {
				byte[] data = new byte[Inflater.BUFFER_SIZE];
				int len;
				TarEntry entry;
				File file;
				
				tis = new TarInputStream(new GZIPInputStream(new FileInputStream(source), Inflater.BUFFER_SIZE), Inflater.BUFFER_SIZE);
				while ((entry = tis.getNextEntry()) != null) {
					file = new File(output, entry.getName());
					if (entry.isDirectory()) {
						file.mkdir();
					} else {
						createdFiles.add(file.getAbsolutePath());
						bos = new BufferedOutputStream(new FileOutputStream(file));
						while ((len = tis.read(data)) != -1) {
							bos.write(data, 0, len);
						}
						bos.flush();
						bos.close();
						bos = null;
					}
				}
				tis.close();
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (IOException ioe) {}
				try {
					if (tis != null) tis.close();
				} catch (IOException ioe) {}
			}

			return createdFiles;
		} else {
			return null;
		}
	}

	private static Set<String> inflateTar(File source, File output)
	throws IOException {
		Set<String> createdFiles = new HashSet<String>();
		if (output.mkdir()) {
			BufferedOutputStream bos = null;
			TarInputStream tis = null;
			try {
				byte[] data = new byte[Inflater.BUFFER_SIZE];
				int len;
				TarEntry entry;
				File file;
				
				tis = new TarInputStream(new FileInputStream(source), Inflater.BUFFER_SIZE);
				while ((entry = tis.getNextEntry()) != null) {
					file = new File(output, entry.getName());
					if (entry.isDirectory()) {
						file.mkdir();
					} else {
						createdFiles.add(file.getAbsolutePath());
						bos = new BufferedOutputStream(new FileOutputStream(file));
						while ((len = tis.read(data)) != -1) {
							bos.write(data, 0, len);
						}
						bos.flush();
						bos.close();
						bos = null;
					}
				}
				tis.close();
			} catch (IOException ioe) {
				throw ioe;
			} finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (IOException ioe) {}
				try {
					if (tis != null) tis.close();
				} catch (IOException ioe) {}
			}

			return createdFiles;
		} else {
			return null;
		}
	}
}
