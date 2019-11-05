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
 * Util.java
 * This class is part of the AIBench Project.
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 13/03/2008
 */
package es.uvigo.ei.aibench;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Daniel Gonzalez Peña
 *
 */
public class Util {
	
	static protected int restartSignal = 10;
	
	static protected String pluginsBin;
	static protected Set<String> properties = new HashSet<String>();
	static protected String javaCommand= null;
	
	public static URL getGlobalResourceURL(String resourcePath){
		try{
			URL url = Util.class.getProtectionDomain().getCodeSource().getLocation();
			
			try {
				if (url.getFile().endsWith(".jar")){
					url = new URL(url.toString().substring(0,url.toString().lastIndexOf('/'))+"/../"+resourcePath);
				}else{
					url = new URL(url+"../"+resourcePath);
				}
				
				
				return url;
			} catch (MalformedURLException e1) {
				throw new RuntimeException("Not found a aibench configuration file, searching in url: "+url.getFile());
			}
		}catch(NullPointerException e){
			try {
				return new File(resourcePath).toURL();
			} catch (Exception e1) {
				throw new RuntimeException("Can't find resource in path "+resourcePath+" due to "+e1);
			}
		}
	}
	
	public static void restart(){
//		String classpath = System.getProperty("java.class.path");
//		String fileSep = System.getProperty("file.separator");
//		String working_dir = System.getProperty("user.dir");
//		String javaCommand = System.getProperty("java.home")+fileSep+"bin"+fileSep+"java";
//		
//		
//		if(Util.javaCommand==null){
//			System.out.println(working_dir);
//			String command = javaCommand + " -cp " + classpath + " " +"es.uvigo.ei.aibench.Launcher " + pluginsBin + " > out.txt";
//			System.out.println(command);
//			String[] env = getProperties2();
//		//		JFrame mainFrame = Workbench.getInstance().getMainFrame();
//		//		mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
//			
//			try {
//				
//				Process p = Runtime.getRuntime().exec(command, env, new File(working_dir));
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}else{
//			
//			
//			System.out.println("cd " + working_dir);
//			System.out.println(Util.javaCommand);
//			
//			try {
//				Runtime.getRuntime().exec(Util.javaCommand, new String[]{}, new File(working_dir));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		System.out.println("Exit code 10 restart");
		System.exit(10);
	
	}
	
	public static String getProperties(){
		String ret = "";
		for(Object key : properties){
			
			String value = System.getProperty(key.toString());
			if(value!=null && !value.trim().equals(""))
				ret+="-D"+key+"=\""+value+"\" ";
			
		}
		return ret;
	}
	
	public static String[] getProperties2(){
		String[] ret =  new String[properties.size()];
		int i = 0;
		
//		System.out.println("keys: " + System.getenv().keySet());
		
		for(Object key : System.getProperties().keySet()){
			System.out.println(key + "\t" + System.getProperty(key.toString()));
		}
		
//		System.out.println("keys: " + System.getProperties().keySet());
		
		for(Object key : properties){
			
			String value = System.getProperty(key.toString());
			
//			System.out.println(key + "\t" + value);
			if(value!=null && !value.trim().equals("")){
				ret[i] = key+"=\""+value+"\" ";
//				System.out.println(key + "\t" + value);
				i++;
			}
			
		}
		
		for(; i< ret.length; i++)
			ret[i]="";
		
		return ret;
	}
	
	/**
	 * @param pluginsBin
	 * @param properties
	 */
	public static void registRestarInfo(String pluginsBin, String properties) {
		Util.pluginsBin = pluginsBin;
		
		if(properties!=null){
			String[] data = properties.split(";");
			for(String id : data){
				Util.properties.add(id);
			}
		}
	}
	
	public static void registRestartCommand(String restartCommand) {
		Util.javaCommand =restartCommand;
	}
	
	public static void reloadClassPath(Set<String> jars) {
		
		String classpath = System.getProperty("java.class.path");
		
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Method m;
		try {
			m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
			m.setAccessible(true);    
	        for(String j :jars){
	        	
	        	File jarFile = new File(j);
	        	m.invoke(urlClassLoader, jarFile.toURI().toURL());
	        }
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		System.setProperty("java.class.path", classpath);
		
	}

	public static class InterruptThread implements Runnable {
	    HttpURLConnection con;
	    int timer;
	    public InterruptThread(HttpURLConnection con, int timer) {
	        this.con = con;
	        this.timer = timer;
	    }

	    public void run() {
	        try {
	            Thread.sleep(timer);
	        } catch (InterruptedException e) {

	        }
	        con.disconnect();
	    }
	}

}
