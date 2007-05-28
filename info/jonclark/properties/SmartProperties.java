/*
 * Copyright (c) 2006, Jonathan Clark <jon_DOT_h_DOT_clark_AT_gmail_DOT_com> 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *     * Neither the name of my affiliates nor the names of thier contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIEDWARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package info.jonclark.properties;

import info.jonclark.io.FileListener;
import info.jonclark.io.FileMonitor;
import info.jonclark.log.LogUtils;
import info.jonclark.util.NetUtils;
import info.jonclark.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class SmartProperties {
    private FileMonitor monitor;
    private Properties props;
    private ArrayList<WeakReference<PropertiesListener>> vListeners = new ArrayList<WeakReference<PropertiesListener>>(
	    1);
    private Logger log = LogUtils.getLogger();

    private static final long POLLING_INTERVAL = 5000;

    public SmartProperties(String file) throws FileNotFoundException, IOException,
	    PropertiesException {
	this(new File(file), true);
    }

    public SmartProperties(String file, boolean monitorForChanges) throws FileNotFoundException,
	    IOException, PropertiesException {
	this(new File(file), monitorForChanges);
    }

    public SmartProperties(File file) throws FileNotFoundException, IOException,
	    PropertiesException {
	this(file, true);
    }

    public SmartProperties(File baseFile, boolean monitorForChanges) throws FileNotFoundException,
	    IOException, PropertiesException {
	this.props = PropertyUtils.getProperties(baseFile);

	if (monitorForChanges) {
	    // initialize the monitor
	    monitor = new FileMonitor(POLLING_INTERVAL);
	    monitor.addListener(fileListener);

	    // monitor all files included in this property file
	    ArrayList<File> importedFiles = PropertyUtils.listImports(baseFile);
	    for (final File importedFile : importedFiles) {
		monitor.addFile(importedFile);
	    }
	}
    }

    public boolean hasProperty(String name) {
	return props.containsKey(name);
    }

    public Set<Object> getAllPropertyKeys() {
	return props.keySet();
    }

    public int getPropertyInt(String name) {
	String str = props.getProperty(name);
	if (str != null)
	    return Integer.parseInt(str);
	else
	    throw new RuntimeException("Property not defined: " + name);
    }

    public void setPropertyInt(String name, int value) {
	props.setProperty(name, value + "");
    }

    public float getPropertyFloat(String name) {
	String str = props.getProperty(name);
	if (str != null)
	    return Float.parseFloat(str);
	else
	    throw new RuntimeException("Property not defined: " + name);
    }

    public void setPropertyFloat(String name, float value) {
	props.setProperty(name, value + "");
    }

    public String getPropertyString(String name) {
	String str = props.getProperty(name);
	if (str != null)
	    return str;
	else
	    throw new RuntimeException("Property not defined: " + name);
    }

    public void setPropertyString(String name, String value) {
	props.setProperty(name, value);
    }

    public InetSocketAddress getPropertyInetSocketAddress(String name, String requiredProtocol) {
	String str = props.getProperty(name);
	if (str != null) {
	    return NetUtils.toInetSocketAddress(str, requiredProtocol);
	} else {
	    throw new RuntimeException("Property not defined: " + name);
	}
    }

    public void setPropertyInetSocketAddress(String name, InetSocketAddress address, String protocol) {
	String value = NetUtils.formatAddress(address, protocol);
	props.setProperty(name, value);
    }

    public int[] getPropertyIntArray(String name) {
	String str = props.getProperty(name);
	if (str != null) {
	    String[] tokens = StringUtils.tokenize(str, ", ");
	    return StringUtils.toIntArray(tokens);
	} else {
	    throw new RuntimeException("Property not defined: " + name);
	}
    }

    public void setPropertyIntArray(String name, int[] values) {
	String value = StringUtils.untokenize(values, ",");
	props.setProperty(name, value);
    }

    public float[] getPropertyFloatArray(String name) {
	String str = props.getProperty(name);
	if (str != null) {
	    String[] tokens = StringUtils.tokenize(str, ", ");
	    return StringUtils.toFloatArray(tokens);
	} else {
	    throw new RuntimeException("Property not defined: " + name);
	}
    }

    public void setPropertyFloatArray(String name, float[] values) {
	String value = StringUtils.untokenize(values, ",");
	props.setProperty(name, value);
    }

    public String[] getPropertyStringArray(String name) {
	String str = props.getProperty(name);
	if (str != null) {
	    String[] tokens = StringUtils.tokenize(str, ", ");
	    return tokens;
	} else {
	    throw new RuntimeException("Property not defined: " + name);
	}
    }

    public void setPropertyStringArray(String name, String[] values) {
	String value = StringUtils.untokenize(values, ",");
	props.setProperty(name, value);
    }

    public InetSocketAddress[] getPropertyInetSocketAddressArray(String name,
	    String requiredProtocol) {
	String str = props.getProperty(name);
	if (str != null) {
	    String[] tokens = StringUtils.tokenize(str, ", ");
	    return NetUtils.toInetSocketAddressArray(tokens, requiredProtocol);
	} else {
	    throw new RuntimeException("Property not defined: " + name);
	}
    }

    public void setPropertyInetSocketAddressArray(String name, String protocol, InetSocketAddress[] values) {
	String value = NetUtils.formatAddressArray(values, protocol);
	props.setProperty(name, value);
    }

    /**
         * Save the properties while preserving any comments.
         */
    // public void save() {
    // monitor.removeFile(file);
    //	
    // // save file while preserving comments
    //	
    //
    // monitor.addFile(file);
    //	
    // throw new RuntimeException("Unimplemented.");
    // }
    public void addPropertiesListener(PropertiesListener lis) {
	vListeners.add(new WeakReference<PropertiesListener>(lis));
    }

    protected void firePropertyChanged(String name) {
	for (final WeakReference<PropertiesListener> ref : vListeners) {
	    PropertiesListener lis = ref.get();
	    if (ref != null)
		lis.propertyChanged(this, name);
	}
    }

    private FileListener fileListener = new FileListener() {
	public void fileChanged(File file) {

	    try {
		Properties changedProps = PropertyUtils.getProperties(file);
		if (vListeners.size() > 0) {
		    for (final Object obj : changedProps.keySet()) {
			final String key = (String) obj;
			final String changedEntry = changedProps.getProperty(key);
			final String originalEntry = props.getProperty(key);

			if (originalEntry == null || !changedEntry.equals(originalEntry)) {
			    firePropertyChanged(key);
			}
		    }
		}
		props = changedProps;
	    } catch (FileNotFoundException e) {
		log.warning(StringUtils.getStackTrace(e));
	    } catch (IOException e) {
		log.warning(StringUtils.getStackTrace(e));
	    } catch (PropertiesException e) {
		log.warning(StringUtils.getStackTrace(e));
	    }

	}
    };
}
