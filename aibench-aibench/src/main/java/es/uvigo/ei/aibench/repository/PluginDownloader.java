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
 * PluginDownloader.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 31/03/2009
 */
package es.uvigo.ei.aibench.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import es.uvigo.ei.aibench.Util;
import es.uvigo.ei.aibench.repository.info.PluginInfo;

/**
 * @author Miguel Reboiro Jato
 * 
 */
public class PluginDownloader {
	private final static Logger logger = Logger.getLogger(PluginDownloader.class);
	
	private final static Map<String, Object> DOWNLOAD_LOCKS = new Hashtable<String, Object>();
	/**
	 * 
	 */
	private static final int BUFFER_SIZE = 8192;

	/**
	 * 
	 */
	private static final String DEFAULT_INFO_FILE = "plugins.dat";
	private static final String PLATFORM = "platform";
	public static final String PLATINFOMSG = "message";
	public static final String PLATHASINFOMSG = "hasmessage";
	public static final String PLATDEPRECATED = "deprecated";
	public static final String PLATDEPRECMSG = "deprecatedmessage";

	private static int DOWNLOADID_COUNTER = 0;
	
	private final String host;
	private final String infoFile;
	private final File installDir;
	private final Map<String, PluginInfo> infoPlugins;
	private final Map<Integer, DownloadThread> downloadThreads;
	
	private final List<PluginDownloadListener> downloadListeners;
	private final ExecutorService notifierThreads;
	
	
	private boolean isDeprecated = false;
	private boolean hasDeprecMsg = false;
	private String deprecatedMsg;
	private boolean hasInfoMsg = false;
	private String infoMsg;
	private int timeout = 5;
	private String backupHost;
	
	private Map<String, String> platformProps;
	
	
	
	public PluginDownloader(String host, String installDir) {
		this(host, PluginDownloader.DEFAULT_INFO_FILE, installDir);
	}
	
	public PluginDownloader(String host, String infoFile, String installDir) {
		this.host = host;
		this.infoFile = infoFile;
		this.installDir = new File(installDir);
		this.infoPlugins = new Hashtable<String, PluginInfo>();
		this.downloadThreads = new Hashtable<Integer, DownloadThread>();
		this.downloadListeners = new Vector<PluginDownloadListener>();
		this.notifierThreads = Executors.newSingleThreadExecutor();
	}
	
	private final static Object getDownloadLock(String directory) {
		if (!PluginDownloader.DOWNLOAD_LOCKS.containsKey(directory)) {
			synchronized (PluginDownloader.DOWNLOAD_LOCKS) {
				if (!PluginDownloader.DOWNLOAD_LOCKS.containsKey(directory)) {
					// Any object may be used as lock, so we use the directory itself as lock.
					PluginDownloader.DOWNLOAD_LOCKS.put(directory, directory); 
				}
			}
		}
		return PluginDownloader.DOWNLOAD_LOCKS.get(directory);
	}
	
	public boolean addDownloadListener(PluginDownloadListener listener) {
		synchronized(this.downloadListeners) {
			if (this.downloadListeners.contains(listener)) {
				return false;
			} else {
				return this.downloadListeners.add(listener);
			}
		}
	}
	
	public boolean removeDownloadListener(PluginDownloadListener listener) {
		synchronized (this.downloadListeners) {
			return this.downloadListeners.remove(listener);
		}
	}
	
	public void notifyDownloadStarted(final PluginDownloadEvent event) {
		synchronized (this.downloadListeners) {
			this.notifierThreads.execute(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					for (PluginDownloadListener listener:PluginDownloader.this.downloadListeners) {
						listener.downloadStarted(event);
					}
				}
			});
		}
	}
	
	public void notifyDownloadStep(final PluginDownloadEvent event) {
		synchronized (this.downloadListeners) {
			this.notifierThreads.execute(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					for (PluginDownloadListener listener:PluginDownloader.this.downloadListeners) {
						listener.downloadStep(event);
					}
				}
			});
		}
	}
	
	public void notifyDownloadError(final PluginDownloadEvent event) {
		synchronized (this.downloadListeners) {
			this.notifierThreads.execute(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					for (PluginDownloadListener listener:PluginDownloader.this.downloadListeners) {
						listener.downloadError(event);
					}
				}
			});
		}
	}
	
	public void notifyDownloadFinished(final PluginDownloadEvent event) {
		synchronized (this.downloadListeners) {
			this.notifierThreads.execute(new Runnable() {
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					for (PluginDownloadListener listener:PluginDownloader.this.downloadListeners) {
						listener.downloadFinished(event);
					}
				}
			});
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	private final static String getPluginFromKey(String key) throws NotInitializedException {
		try{
			return key.substring(0, key.lastIndexOf('.'));
		}catch(Exception e){
			throw new NotInitializedException(e);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	private final static String getPropertyFromKey(String key) {
		return key.substring(key.lastIndexOf('.')+1, key.length());
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private final String getFileURL(String file) {
		return String.format("%s/%s", this.host, file);
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * @return the infoFile
	 */
	public String getInfoFile() {
		return this.infoFile;
	}
	
	public String getInfoFileURL() {
		return this.getFileURL(this.infoFile);
	}

	public synchronized boolean hasInfo() {
		return !this.infoPlugins.isEmpty();
	}
	
	
	public Map<String, String> getPlatformProperties(){
		if(platformProps ==null) platformProps = new HashMap<>();
		return platformProps;
	}
	
	public void setPlatformProperty(String key, String value){
		getPlatformProperties().put(key, value);
	}
	
	public String getPlatformProperty(String key){
		return getPlatformProperties().get(key);
	}
	
	
	/**
	 * Changed for Proxy System
	 * 
	 * @throws IOException
	 */
	public synchronized void downloadInfo(boolean forceDownloadInfo, boolean hasTimeout) throws IOException {
		if(!forceDownloadInfo)
			return;
		long init = System.currentTimeMillis();
		Map<String, PluginInfo> infos = new HashMap<String, PluginInfo>();
		URL url = new URL(this.getInfoFileURL());
		HttpURLConnection urlConnect;
		
		urlConnect = tryHttpURLConnection(url);
		if(urlConnect == null)
			throw new IOException("Could not connect to server!");
		
		if(hasTimeout){
			new Thread(new Util.InterruptThread(urlConnect, getTimeout())).start();
			urlConnect.setConnectTimeout(getTimeout());
			urlConnect.setReadTimeout(getTimeout());
		}
		
		Properties properties = new Properties();
		properties.load(urlConnect.getInputStream());
		String property, value, keyString, keyPlugin;
		for (Object key:properties.keySet()) {
			keyString = key.toString();
			keyPlugin = PluginDownloader.getPluginFromKey(keyString);
			property = PluginDownloader.getPropertyFromKey(keyString);
			
			value = properties.getProperty(keyString);
			
			
			if(keyPlugin.equals(PLATFORM))
			{
				setPlatformProperty(property, value);
				
			}else{
				if (!infos.containsKey(keyPlugin)) {
					infos.put(keyPlugin, new PluginInfo(keyPlugin, this.getHost()));
				}
				infos.get(keyPlugin).setValue(property, value);
			}
		}
		
		this.infoPlugins.clear();
		for (PluginInfo info:infos.values()) {
			if (info.getUID() == null) {
				PluginDownloader.logger.warn("Incomplete Plugin Info: " + info);
			} else {
				this.infoPlugins.put(info.getUID(), info);
			}
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Time to downloadInfo:" + (end-init) + " Max defined: " +getTimeout() + " hasTimeout: " + hasTimeout);
		
	}
	
	protected HttpURLConnection tryHttpURLConnection(URL url) throws IOException{
		
		HttpURLConnection urlConnectToTest;

		try{
			// Open the first and usual connection
			urlConnectToTest = openFirstConnection(url);
		}catch(MalformedURLException mfue){
			urlConnectToTest = null;
		}
		if(urlConnectToTest != null)
			return urlConnectToTest;
		
		try{
			// Open the second connection with redirect
			// This connection is used when the plugins.dat is only access through a redirect link
			urlConnectToTest = openRedirectedConnection(url);
		}catch(MalformedURLException mfue){
			urlConnectToTest = null;
		}
		if(urlConnectToTest != null)
			return urlConnectToTest;
		
		try{
			// Open the final backup connection
			// This connection use the link present in the pluginmanager properties file
			// Property name: backupdarepository.host
			urlConnectToTest = openBackupConnection();
		}catch(MalformedURLException mfue){
			urlConnectToTest = null;
		}
		if(urlConnectToTest != null)
			return urlConnectToTest;
		
		// Should not get here!
		return null;
		
	}
	
	protected HttpURLConnection openFirstConnection(URL url) throws IOException{
		HttpURLConnection urlConnectToRet = openConnectionWithProxy(url);
		
		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		// Swap http and test again
		urlConnectToRet = openConnectionWithProxy(swapHttpURL(url));
		
		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		return null;
	}
	
	protected HttpURLConnection openRedirectedConnection(URL url) throws IOException{
		HttpURLConnection urlConnectToRet = (HttpURLConnection) url.openConnection();
		urlConnectToRet.setInstanceFollowRedirects(true);
		urlConnectToRet.connect();
		String location = urlConnectToRet.getHeaderField("Location");
		URL newurl = new URL(location);
		urlConnectToRet = openConnectionWithProxy(newurl);
		
		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		// Swap http and test again
		urlConnectToRet = openConnectionWithProxy(swapHttpURL(url));
		urlConnectToRet.setInstanceFollowRedirects(true);
		urlConnectToRet.connect();
		location = urlConnectToRet.getHeaderField("Location");
		newurl = new URL(location);
		urlConnectToRet = openConnectionWithProxy(newurl);

		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		return null;
	}
	
	protected HttpURLConnection openBackupConnection() throws IOException{
		if(backupHost == null || backupHost.equals(""))
			return null;
		URL url = new URL(backupHost+File.separator+this.infoFile);
		
		HttpURLConnection urlConnectToRet = openConnectionWithProxy(url);
		
		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		// Swap http and test again
		urlConnectToRet = openConnectionWithProxy(swapHttpURL(url));
		if(testURLConnection(urlConnectToRet))
			return urlConnectToRet;
		
		return null;
	}
	
	/**
	 * Swap URL to Https if Http and vice-versa
	 * @param URL
	 * @return new URL
	 * @throws MalformedURLException 
	 */
	protected URL swapHttpURL(URL url) throws MalformedURLException{
		String urlPath = url.toString();
		
		if(urlPath.startsWith("https"))
			urlPath = urlPath.replace("https", "http");
		else
			urlPath = urlPath.replace("http", "https");
		
		return new URL(urlPath);
	}
	
	protected boolean testURLConnection(HttpURLConnection urlConnectToTest) throws IOException{
		HttpURLConnection toTest = openConnectionWithProxy(urlConnectToTest.getURL());
		Properties properties = new Properties();
		try{
			properties.load(toTest.getInputStream());
			String keyString;
			if(properties.isEmpty()){
				toTest.disconnect();
				return false;
			}
			
			for (Object key:properties.keySet()) {
				keyString = key.toString();
				try{
					PluginDownloader.getPluginFromKey(keyString);
				} catch(NotInitializedException e){
					toTest.disconnect();
					return false;
				}
			}
			toTest.disconnect();
			return true;
		}catch(FileNotFoundException fnfe){
			toTest.disconnect();
			return false;
		}
		
	}
	
	protected HttpURLConnection openConnectionWithProxy(URL url) throws IOException{
		HttpURLConnection urlConnectToRet;
//		if(AIBenchProxy.getInstance().getProxy()!=null && !AIBenchProxy.getInstance().getProxy().type().equals(Type.DIRECT))
//			urlConnectToRet = (HttpURLConnection)url.openConnection(AIBenchProxy.getInstance().getProxy());
//		else
			urlConnectToRet = (HttpURLConnection)url.openConnection();
		
		return urlConnectToRet;
	}
	
	public PluginInfo getPluginInfo(String uid) {
		return this.infoPlugins.get(uid);
	}
	
	public Collection<PluginInfo> getPluginsInfo() {
		return this.infoPlugins.values();
	}
	
	public void downloadPlugin(String uid)
	throws NotInitializedException, IllegalArgumentException {
		if (!this.hasInfo()) {
			throw new NotInitializedException();
		} else {
			PluginInfo info = this.getPluginInfo(uid);
			if (info == null) {
				throw new IllegalArgumentException("There isn't information for the plugin: " + uid);
			} else {
				this.downloadPlugin(info);
			}
		}
	}
	
	public void downloadPlugin(PluginInfo plugin) 
	throws NotInitializedException {
		if (this.hasInfo()) {
			synchronized (this.downloadThreads) {
				DownloadThread thread = new DownloadThread(plugin);
				this.downloadThreads.put(thread.getDownloadId(), thread);
				thread.start();
			}
		} else {
			throw new NotInitializedException();
		}
	}
	
	public void downloadPlugin(String uid, String updatePlugin)
	throws NotInitializedException, IllegalArgumentException {
		if (!this.hasInfo()) {
			throw new NotInitializedException();
		} else {
			PluginInfo info = this.getPluginInfo(uid);
			if (info == null) {
				throw new IllegalArgumentException("There isn't information for the plugin: " + uid);
			} else {
				this.downloadPlugin(info, updatePlugin);
			}
		}
	}
	
	public void downloadPlugin(PluginInfo plugin, String updatePlugin) 
	throws NotInitializedException {
		if (this.hasInfo()) {
			synchronized (this.downloadThreads) {
				DownloadThread thread = new DownloadThread(plugin, updatePlugin);
				this.downloadThreads.put(thread.getDownloadId(), thread);
				thread.start();
			}
		} else {
			throw new NotInitializedException();
		}
	}
	
	public void cancelDownload(int downloadId) {
		synchronized (this.downloadThreads) {
			DownloadThread thread = this.downloadThreads.get(downloadId);
			if (thread != null && thread.isAlive()) {
				thread.stopDownload();
			}			
		}
	}
	
	public void cancelDownloads() {
		synchronized (this.downloadThreads) {
			for (DownloadThread thread:this.downloadThreads.values()) {
				thread.stopDownload();
			}
			this.downloadThreads.clear();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.cancelDownloads();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String toString = "";
		for (String key:this.infoPlugins.keySet()) {
			toString += this.infoPlugins.get(key);
			toString += '\n';
		}
		return toString;
	}
	
	public class DownloadThread extends Thread {
		private final int downloadId;
		private final PluginInfo plugin;
		private final String updatePlugin;
		private final File directory;
		private final Object lock;
		
		private int total, downloaded;
		private boolean stopDownload;
		
		public DownloadThread(PluginInfo plugin) {
			this(plugin, null);
		}
		public DownloadThread(PluginInfo plugin, String updatePlugin) {
			this.downloadId = PluginDownloader.DOWNLOADID_COUNTER++;
			this.plugin = plugin;
			this.updatePlugin = updatePlugin;
			this.directory = new File(PluginDownloader.this.installDir, plugin.getUID());
			
			this.total = 0;
			this.downloaded = 0;
			this.stopDownload = false;
			
			Object lock;
			try {
				lock = PluginDownloader.getDownloadLock(this.directory.getCanonicalPath());
			} catch (IOException ioe) {
				PluginDownloader.logger.warn(
					"Canonical Path couldn't be used. Using Absolute Path: " + this.directory.getAbsolutePath()
				);
				lock = PluginDownloader.getDownloadLock(this.directory.getAbsolutePath());
			}
			this.lock = lock;
		}
		
		public final int getDownloadId() {
			return this.downloadId;
		}
		
		public void stopDownload() {
			this.stopDownload = true;
		}
		
		private void notifyDownloadStarted() {
			PluginDownloader.this.notifyDownloadStarted(new PluginDownloadEvent(this.downloadId, this.plugin, this.total, this.downloaded));
		}
		private void notifyDownloadStep() {
			PluginDownloader.this.notifyDownloadStep(new PluginDownloadEvent(this.downloadId, this.plugin, this.total, this.downloaded));
		}
		private void notifyDownloadFinished() {
			PluginDownloader.this.notifyDownloadFinished(new PluginDownloadEvent(this.downloadId, this.plugin, this.total, this.downloaded));
		}
		private void notifyDownloadError(IOException error) {
			PluginDownloader.this.notifyDownloadError(new PluginDownloadEvent(this.downloadId, this.plugin, this.total, this.downloaded, error));
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		
		private int getTotal(URL[] urls) throws IOException {
			int sum = 0;
			for(URL url : urls){
				HttpURLConnection urlConnect;
//				if(AIBenchProxy.getInstance().getProxy()!=null && !AIBenchProxy.getInstance().getProxy().type().equals(Type.DIRECT))
//					urlConnect = (HttpURLConnection)url.openConnection(AIBenchProxy.getInstance().getProxy());
//				else
					urlConnect = (HttpURLConnection)url.openConnection();
				sum += urlConnect.getContentLength(); // The last one is when the install.info file is stored.
			}
			sum += 1;
			return sum;
		}
		@Override
		public void run() {
			InputStream is = null;
			FileOutputStream fos = null;
			this.downloaded = 0;
			this.total = 0;
			synchronized (this.lock) {
				try {
					if (!this.stopDownload && this.directory.mkdir()) {
						File installFile = new File(this.directory, this.plugin.getFile());
						File[] nameFiles = new File[1+plugin.getDependencies().size()];
						nameFiles[0] = installFile;					
						URL[] urls = new URL[1+plugin.getDependencies().size()];
						URL url = new URL(PluginDownloader.this.getFileURL(this.plugin.getFile()));
						urls[0] = url;
						int i=1;
						for(String dep : plugin.getDependencies()){
							urls[i] = new URL(PluginDownloader.this.getFileURL(dep));
							nameFiles[i] = new File(this.directory, dep);
							i++;
						}
						
						this.total = getTotal(urls);
						
						
						this.notifyDownloadStarted();
						byte[] data = new byte[PluginDownloader.BUFFER_SIZE];
						int len;
						
						i =0;
						for(File file : nameFiles){
							HttpURLConnection urlConnect;
//							if(AIBenchProxy.getInstance().getProxy()!=null && !AIBenchProxy.getInstance().getProxy().type().equals(Type.DIRECT))
//								urlConnect = (HttpURLConnection)urls[i].openConnection(AIBenchProxy.getInstance().getProxy());
//							else
								urlConnect = (HttpURLConnection)urls[i].openConnection();
							is = urlConnect.getInputStream();
							fos = new FileOutputStream(file);				
							while (!this.stopDownload && (len = is.read(data)) != -1) {
								fos.write(data, 0, len);
								this.downloaded += len;
								this.notifyDownloadStep();
							}
							i++;
							is.close();
							fos.close();
						}
						
						if (!this.stopDownload) {
							if (this.updatePlugin == null) {
								this.plugin.getInstallInfo().store(this.directory);
							} else {
								this.plugin.getInstallInfo(this.updatePlugin).store(this.directory);
							}
							this.downloaded++;
						}
						
						
						this.notifyDownloadFinished();
					}
				} catch (IOException ioe) {
					this.stopDownload = true; // To delete de downloaded files.
					this.notifyDownloadError(ioe);
				} finally {
					PluginDownloader.this.downloadThreads.remove(this.downloadId);
					try {
						if (fos != null) {
							fos.flush();
							fos.close();
						}
					} catch (IOException e) {}
					try {
						if (is != null) is.close();
					} catch (IOException e) {}
					if (this.stopDownload) 
						PluginInstaller.deleteFile(this.directory);
				}
			}
		}
		/**
		 * @param urls
		 * @return
		 */
		
	}

	public int getTimeout() {
		return this.timeout*1000;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setBackupHost(String backupHost) {
		this.backupHost = backupHost;
	}

}
