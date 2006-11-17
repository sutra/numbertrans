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
package info.jonclark.media;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;

/*******************************************************************************
 * ImageCanvas Class<br>
 * PURPOSE: Displays an image.<br>
 * NOTE: Constructor takes image path RELATIVE TO CODEBASE as argument
 ******************************************************************************/
// TODO: Rewrite this class without the Applet parameter
public class ImageCanvas extends Canvas {
    private static final long serialVersionUID = 6629458079129220667L;
    private Image someImage;

    public ImageCanvas(String path, int width, int height, Applet referrer) {
	// Receieve image name and set up dimensions of canvas
	someImage = referrer.getImage(referrer.getCodeBase(), path);
	setSize(width, height);
    } // end ImageCanvas constructor

    public void paint(Graphics graph) {
	// Stick the image on this canvas
	graph.drawImage(someImage, 0, 0, this);
    } // end paint method

} // end ImageCanvas class
