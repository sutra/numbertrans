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

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A very thin wrapper around the Java logger that enables varargs for log
 * messages to improve efficiency while still retaining readable syntax.
 */
public class VarLogger extends Logger {
    protected int levelValue;

    private VarLogger(String name) {
	super(name, null);

	Level level = getLevel();
	if (level == null) {
	    this.levelValue = Level.INFO.intValue();
	} else {
	    this.levelValue = level.intValue();
	}
    }

    public static synchronized VarLogger getLogger(String name) {
	LogManager manager = LogManager.getLogManager();
	Logger result = manager.getLogger(name);

	if (result == null) {
	    result = new VarLogger(name);
	    manager.addLogger(result);
	    result = manager.getLogger(name);
	} else {
	    if (!(result instanceof VarLogger)) {
		throw new RuntimeException("Non-VarLogger has already been registered for " + name);
	    }
	}

	return (VarLogger) result;
    }

    public void severe(String msg, Object... args) {
	if (Level.SEVERE.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.SEVERE, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    public void warning(String msg, Object... args) {
	if (Level.WARNING.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.WARNING, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    public void info(String msg, Object... args) {
	if (Level.INFO.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.INFO, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    public void fine(String msg, Object... args) {
	if (Level.FINE.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.FINE, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    public void finer(String msg, Object... args) {
	if (Level.FINER.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.FINER, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    public void finest(String msg, Object... args) {
	if (Level.FINEST.intValue() < levelValue) {
	    return;
	}
	StackTraceElement trace = ReflectUtils.getCallingStackTraceElement();
	super.logp(Level.FINEST, trace.getClassName(), trace.getMethodName(), msg, args);
    }

    @Override
    public void setLevel(Level newLevel) {
	if(newLevel != null)
	    this.levelValue = newLevel.intValue();
	super.setLevel(newLevel);
    }
}
