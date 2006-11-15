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

import info.jonclark.util.Timeout;
import info.jonclark.util.TimeoutListener;

import java.awt.*;
import java.text.*;
import java.util.logging.*;

import javax.swing.*;

/**
 * Developed for use with the ProgressServer class. Displays
 * the progress of a single task
 */
class ProgressPanel extends JPanel {
    private JLabel lab;
    private JProgressBar progress = new JProgressBar();
    private JLabel perc;
    private final Color defaultColor;
    private final NumberFormat f = new DecimalFormat("#0.0");
    private final Timeout timeout = new Timeout();
    private String name;
    private final Logger log = Logger.getLogger("info.jonclark.gui.ProgressPanel");
    
    public ProgressPanel(String name, Logger parent) {
        log.setParent(parent);
        this.name = name;
        
        lab = new JLabel(name);
        perc = new JLabel("0.0%");
        
        progress.setName(name);
        progress.setIndeterminate(true);
        progress.setSize(200,20);
        
        this.add(lab);
        this.add(progress);
        this.add(perc);
        this.setSize(250, 100);
        this.defaultColor = progress.getForeground();

        timeout.addTimeoutListener(new TimeoutListener() {
            public void taskStalled() {
                log.info("Stall detected.");
                setStalled(true);
            }
        });
    }
    
    private void setColor(Color c) {
        if(progress.getForeground() != c) {
            progress.setForeground(c);
        }
    }
    
    public void setName(String name) {
        this.name = name;
        lab.setText(name);
        progress.setName(name);
    }
    
    public void setMaxValue(int n) {
        progress.setMaximum(n);
    }
    
    public void setDone(boolean success) {
        timeout.cancel();
        if(success) {
            setColor(Color.GREEN);
            progress.setValue(progress.getMaximum());
            perc.setText("100%");
        } else {
            setColor(Color.RED);
        }
    }
    
    public void setStallTimeout(int nStallTimeoutSec) {
        if(nStallTimeoutSec > 0) {
            timeout.setTimeout(nStallTimeoutSec*1000);
        } else {
            timeout.cancel();
        }
    }
    
    private void setStalled(boolean stalled) {
        if(stalled) {
            setColor(Color.YELLOW);
        } else {
            setColor(defaultColor);
        }
    }
    
    public void setProgress(int n) {
        progress.setIndeterminate(false);
        timeout.refresh();
        this.setStalled(false);
        progress.setValue(n);
        perc.setText(f.format(100*(float)n/(float)progress.getMaximum()) + "%");
    }
}