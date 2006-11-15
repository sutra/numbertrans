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
package info.jonclark.gui;

import java.awt.Component;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JOptionPane;

/**
 * Directs {@link Logger} messages to model GUI dialog boxes.
 * 
 * @author Jonathan Clark
 */
public class GuiLogHandler extends Handler {

    protected final Component parent;
    protected final String title;

    /**
         * Create a new <code>GuiLogHandler</code> object. The average user
         * will want to set the level of this handler to Level.WARNING so that
         * the user doesn't have to deal with too many informational messages.
         * 
         * @param parent
         *                The parent component that will be used for displaying
         *                model error dialogs.
         * @title The default title to show in dialog boxes generated by log
         *        messages.
         */
    public GuiLogHandler(Component parent, String title) {
	this.parent = parent;
	this.title = title;
    }

    @Override
    public void close() throws SecurityException {
	// done
    }

    @Override
    public void flush() {
	// done
    }

    @Override
    public void publish(LogRecord record) {
	if (record.getLevel().intValue() >= this.getLevel().intValue()) {
	    JOptionPane.showMessageDialog(parent, record.getMessage(), title,
		    JOptionPane.WARNING_MESSAGE);
	}
    }
}
