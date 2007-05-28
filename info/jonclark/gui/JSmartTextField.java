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

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JSmartTextField extends JTextField {
    private static final long serialVersionUID = -5216144780651750753L;

    private boolean modified = false;
    private boolean noticeModification = false;
    private Color unmodifiedColor;
    private Color modifiedColor;
    private String previousValue;

    public JSmartTextField(String initialValue, Color modifiedColor) {
	this.getDocument().addDocumentListener(docListener);
	this.modifiedColor = modifiedColor;
	this.unmodifiedColor = this.getBackground();
	previousValue = initialValue;
	this.setText(initialValue);
	noticeModification = true;
    }

    public void setText(String str, boolean noticeModification) {
	this.noticeModification = noticeModification;
	this.setText(str);
	this.noticeModification = true;
    }

    public boolean isModified() {
	return modified;
    }

    public void clearModified() {
	modified = false;
	this.setBackground(unmodifiedColor);
	previousValue = this.getText();
    }

    protected void handleModification() {
	if (noticeModification) {
	    if (!this.getText().equals(previousValue)) {
		modified = true;
		this.setBackground(modifiedColor);
	    } else {
		modified = true;
		this.setBackground(unmodifiedColor);
	    }
	}
    }

    private DocumentListener docListener = new DocumentListener() {
	public void changedUpdate(DocumentEvent e) {
	    handleModification();
	}

	public void insertUpdate(DocumentEvent e) {
	    handleModification();
	}

	public void removeUpdate(DocumentEvent e) {
	    handleModification();
	}
    };
}
