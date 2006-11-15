/* Copyright (c) 2006, Marian Olteanu <marian_DOT_olteanu_AT_gmail_DOT_com>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.
- Neither the name of the University of Texas at Dallas nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package info.jonclark.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;


public class GuiUtils {
    
    /**
     * Sets the LookAndFeel of a program to the
     * system's default (native) look
     *
     */
    public static void setNativeLookAndFeel() {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {
            // this is an exception we can safely ignore
        	e.printStackTrace();
        }
    }
	
    /**
     * Unimplemented
     *
     */
	private void showAboutDialog() {
		final JDialog diaAbout = new JDialog();
		JPanel content = new JPanel();
		diaAbout.setContentPane(content);
		content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
		/*
		JLabel lblHeader = new JLabel(tcuHeader);
		lblHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		content.add( lblHeader );
		content.add(Box.createRigidArea(new Dimension(0,20)));
		
		JLabel lblHeader2 = new JLabel(turninHeader);
		lblHeader2.setAlignmentX(Component.CENTER_ALIGNMENT);
		content.add( lblHeader2 );
		content.add(Box.createRigidArea(new Dimension(0,20)));
		*/
		JLabel lblCredits = new JLabel("<html><center><font size=4>Concept, Design, and Administration:<br>" +
				"Dr. Richard Rinewalt, Ph.D.<br><br>" +
				"<font size=3>TurnIn 5.0 User Interface Redesign:<br>" +
				"Jonathan Clark",
				JLabel.CENTER);
		lblCredits.setAlignmentX(Component.CENTER_ALIGNMENT);
		content.add( lblCredits );
		content.add(Box.createRigidArea(new Dimension(0,20)));
		
		JButton butClose = new JButton( new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				diaAbout.setVisible(false);
			}
		});
		butClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		content.add( butClose );
		
		diaAbout.pack();
		diaAbout.setVisible(true);
	}
	
	/**
	 * Prompts user to open a file
	 * 
	 * @param parent
	 * @return File, if user chose one, null otherwise
	 */
	public static File promptForFile(Component parent) {
		File file = null;
	    JFileChooser fc = new JFileChooser();
	    int returnVal = fc.showOpenDialog(parent);
	    if(returnVal == 0) {
	        file = fc.getSelectedFile();
	    } else {
	        file = null;
	    }
	    return file;
	}

}
