///*
//Copyright 2007 Daniel Gonzalez Peña, Florentino Fernandez Riverola
//
//
//This file is part of the AIBench Project. 
//
//AIBench Project is free software: you can redistribute it and/or modify
//it under the terms of the GNU Lesser Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//AIBench Project is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser Public License for more details.
//
//You should have received a copy of the GNU Lesser Public License
//along with AIBench Project.  If not, see <http://www.gnu.org/licenses/>.
//*/
//
///*  
// * AIBenchProxy.java
// *
// * Created inside the SING research group (http://sing.ei.uvigo.es)
// * University of Vigo
// *
// * Created on 15 de Abr de 2014
// */
//package es.uvigo.ei.aibench.settings;
//
//import java.io.IOException;
//import java.net.Proxy;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import com.silicolife.proxy.ds.ProxyConfiguration;
//import com.silicolife.proxy.utils.ProxyUtils;
//
//import es.uvigo.ei.aibench.Paths;
//import es.uvigo.ei.aibench.workbench.Workbench;
//
//
//public class AIBenchProxy {
//	
//	private static AIBenchProxy _instance;
//	private final String urltest= "http://www.google.com:80";
//	private Proxy proxy;
//	private ProxyConfiguration proxyConfiguration;
//	
//	private AIBenchProxy()
//	{
//		ProxyConfiguration proxyConf;
//		try {
//			proxyConf = ProxyUtils.readFromFile(Paths.getInstance().getProxyConfigurationPath());
//		} catch (IOException e) {
//			proxyConf = new ProxyConfiguration();
//		}
//		setIntenalProxy(proxyConf);	
//	}
//	
//	/** 
//	 *  Access to instance
//	 *  
//	 * @return
//	 */
//	public static AIBenchProxy getInstance() {
//		if (_instance == null) {
//			//			_instance = new Workbench();
//			AIBenchProxy.createInstance();
//		}
//		return _instance;
//	}
//	
//	/**
//	 * Creates the singleton instance.
//	 */
//	private static synchronized void createInstance() {
//		if (_instance == null) {
//			_instance = new AIBenchProxy();
//		}		
//	}
//	
//	public synchronized Proxy getProxy()
//	{
//		return proxy;
//	}
//	
//	public synchronized ProxyConfiguration getProxyConfiguration()
//	{
//		return proxyConfiguration;
//	}
//	
//	public void setProxy(ProxyConfiguration conf)
//	{
//		setIntenalProxy(conf);
//		try {
//			ProxyUtils.writeToFile(conf, Paths.getInstance().getProxyConfigurationPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	private void setIntenalProxy(ProxyConfiguration conf) {
//		if(conf==null)
//		{
//			conf = new ProxyConfiguration();
//		}
//		ProxyUtils.setProxy(conf);
//		try {
//			proxy = ProxyUtils.getProxyWithoutValidation(new URI(urltest));
//		} catch (URISyntaxException e) {
//			proxy = null;
//		}
//		proxyConfiguration = conf;
//	}
//	
////	private Proxy getProxyByConfiguration(ProxyConfiguration conf)
////	{
////		if(conf.getType().equals(ProxyType.NONE))
////		{
////			return new Proxy(Type.DIRECT,null);
////		}		
////		else if(conf.getType().equals(ProxyType.SYSTEM))
////		{
////			return null;
////		}
////		else
////		{
////			if(!conf.getHttpHost().isEmpty() && conf.getHttpPort()>0 && conf.getHttpPort() < 65535)
////			{
////				SocketAddress sa = new InetSocketAddress(conf.getHttpHost(),conf.getHttpPort() );
////				return new Proxy(Type.HTTP, sa);
////			}
////			else if(!conf.getSocksHost().isEmpty() && conf.getSocksPort() > 0 && conf.getSocksPort() < 65535)
////			{
////				SocketAddress sa = new InetSocketAddress(conf.getSocksHost(),conf.getSocksPort() );
////				return new Proxy(Type.SOCKS, sa);
////			}
////			else
////			{
////				return null;
////			}
////		}
////	}
//	
//	public boolean testInternetConnection() {
//		
//		boolean t = false;
//		try {
//			t = ProxyUtils.validProxy(proxy, new URI(urltest));
//		} catch (URISyntaxException e) {
//		}
//		return t; 
//	}
//	
//	public boolean testUrlAccess(String urlTest) {
//		
//		boolean t = false;
//		try {
//			t = ProxyUtils.validProxy(proxy, new URI(urlTest));
//		} catch (URISyntaxException e) {
//		}
//		return t; 
//	}
//
//}
