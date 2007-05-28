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
package info.jonclark.log;

import info.jonclark.util.ReflectUtils;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * @author Jonathan
 */
public class LogUtils {
    public static VarLogger getLogger() {
	return VarLogger.getLogger(ReflectUtils.getCallingClassName());
    }

    public static void logNone() {
	Logger.getLogger("").setLevel(Level.OFF);
    }

    public static void logAll() {
	Logger.getLogger("").setLevel(Level.ALL);
    }
    
    public static void logFine() {
	Logger.getLogger("").setLevel(Level.FINE);
    }

    public static void logToFile(String path) throws SecurityException, IOException {
	Logger.getLogger("").addHandler(new FileHandler(path));
    }

//    public static void logToStdOut() {
//	for (final Handler handler : Logger.getLogger("").getHandlers()) {
//	    if(handler instanceof ConsoleHandler) {
//		ConsoleHandler consoleHandler = (ConsoleHandler) handler;
////		consoleHandler.setLevel(Level.WARNING);
//		Logger.getLogger("").removeHandler(consoleHandler);
//	    }
//	}
//	
//	// now add a custom logger that outputs to stdout
//	// it's a hack, but it works
//	Logger.getLogger("").addHandler(new StreamHandler() {
//
//	    private void configure() {
//	        LogManager manager = LogManager.getLogManager();
//		String cname = getClass().getName();
//
//		setLevel(getLevelProperty(cname +".level", Level.INFO));
//		setFilter(getFilterProperty(cname +".filter", null));
//		setFormatter(getFormatterProperty(cname +".formatter", new SimpleFormatter()));
//		try {
//		    setEncoding(getStringProperty(cname +".encoding", null));
//		} catch (Exception ex) {
//		    try {
//		        setEncoding(null);
//		    } catch (Exception ex2) {
//			// doing a setEncoding with null should always work.
//			// assert false;
//		    }
//		}
//	    }
//	    
//	    Level getLevelProperty(String name, Level defaultValue) {
//		LogManager manager = LogManager.getLogManager();
//		String val = manager.getProperty(name);
//		if (val == null) {
//		    return defaultValue;
//		}
//		try {
//		    return Level.parse(val.trim());
//		} catch (Exception ex) {
//		    return defaultValue;
//		}
//	    }
//
//	    {
//		sealed = false;
//		configure();
//		setOutputStream(System.err);
//		sealed = true;
//	    }
//
//	    public void publish(LogRecord record) {
//		super.publish(record);	
//		flush();
//	    }
//
//	    public void close() {
//		flush();
//	    }
//	});
//    }
}
