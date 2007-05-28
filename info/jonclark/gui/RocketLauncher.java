/*
 * Created on May 2, 2007
 */
package info.jonclark.gui;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JFrame;

public class RocketLauncher extends JFrame {

    private static final long serialVersionUID = -7694815129630290102L;
    private final URLClassLoader loader;

    public RocketLauncher() throws MalformedURLException {
	File file = new File("E:/My Documents/Eclipse Workspace/CrescentSmartHome");
	loader = new URLClassLoader(new URL[] { file.toURL() });
    }

    /**
         * @param packageAndClass
         *                e.g. info.jonclark.gui.RocketLauncher
         */
    public void launchApplication(String packageAndClass) {
	try {
	    Class desiredClass = Class.forName(packageAndClass, true, loader);
	    Method mainMethod = desiredClass.getMethod("main", new Class[] { String[].class });
	    
	    mainMethod.invoke(null, "conf/cacl.properties");
	    
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	try {
	    RocketLauncher launcher = new RocketLauncher();
	    launcher.launchApplication("com.tcudev.smartkitchen.agent.SmartKitchenAgent");
	    launcher.launchApplication("com.tcudev.smartkitchen.agent.NaturalLanguageAgent");
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
