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

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

/**
 * A class to make a very nice splash screen, very quickly. Note: Don't use
 * transparency and shadow together and expect it to look good.
 */
public class SplashImage extends JWindow {

    /**
     * 
     */
    private static final long serialVersionUID = -3558992835438047994L;

    private final int nShadowPixels = 14;
    private BufferedImage splash = null;
    private final boolean addShadow;
    private final boolean honorTransparency;

    private static final int MS_BETWEEN_REQUEST_FOCUS = 250;

    /**
         * Constructs a new SplashImage object
         * 
         * @param image
         * @param addShadow
         */
    public SplashImage(BufferedImage image, boolean addShadow, boolean honorTransparency) {
	this.addShadow = addShadow;
	this.honorTransparency = honorTransparency;
	createShadowPicture(image);
    }

    /**
         * Show an image on the screen in the style of a splash screen(with no
         * border or decorations) for a given amount of time.
         * 
         * @param imageStream
         *                The image input stream (such as a FileInputStream)
         * @param addShadow
         *                Should we add a shadow to the splash screen?
         * @param honorTransparency
         *                Leave transparency from GIF or PNG file in-tact?
         * @param msToDisplay
         *                The number of milliseconds this image will display
         *                before disappearing
         * @throws IOException
         */
    public static void showSplashImage(InputStream imageStream, boolean addShadow,
	    boolean honorTransparency, int msToDisplay) throws IOException {
	showSplashImage(ImageIO.read(imageStream), addShadow, honorTransparency, msToDisplay);
    }

    /**
         * Show an image on the screen in the style of a splash screen(with no
         * border or decorations) for a given amount of time.
         * 
         * @param image
         *                The buffered image that will be displayed
         * @param addShadow
         *                Should we add a shadow to the splash screen?
         * @param honorTransparency
         *                Leave transparency from GIF or PNG file in-tact?
         * @param msToDisplay
         *                The number of milliseconds this image will display
         *                before disappearing
         * @throws IOException
         */
    public static void showSplashImage(BufferedImage image, boolean addShadow,
	    boolean honorTransparency, final int msToDisplay) throws IOException {
	final SplashImage window = new SplashImage(image, addShadow, honorTransparency);
	window.setVisible(true);

	final SplashAction action = new SplashAction(msToDisplay, window);
	final Timer timer = new Timer(MS_BETWEEN_REQUEST_FOCUS, action);
	action.timer = timer;
	timer.start();
    }

    private static class SplashAction extends AbstractAction {
	private static final long serialVersionUID = -3620673555493386385L;
	private final long killTime;
	private final SplashImage window;
	private Timer timer = null;

	public SplashAction(int msToDisplay, SplashImage window) {
	    this.killTime = System.currentTimeMillis() + msToDisplay;
	    this.window = window;
	}

	public void setTimer(Timer t) {
	    this.timer = t;
	}

	// calling timer in actionPerformed would be risky in that timer
	// could be null... except the only object that can call the
	// method is timer.
	public void actionPerformed(ActionEvent evt) {
	    if (System.currentTimeMillis() < killTime) {
		// make sure the splash stays on top until we're done
		// loading
		window.requestFocus();
		// FIXME: This request currently fails since we don't have a
                // parent
		timer.setRepeats(true);
	    } else {
		window.splash = null;
		window.setVisible(false);
		window.dispose();
		timer.setRepeats(false);
	    }
	}
    }

    /**
         * Paints the image on the screen. This is called internally by the Java
         * framework.
         */
    public void paint(Graphics g) {
	if (splash != null) {
	    g.drawImage(splash, 0, 0, null);
	}
    }

    /**
         * Dynamically create the image buffer, adding shadow if requested.
         * 
         * @param image
         */
    private void createShadowPicture(BufferedImage image) {
	final int imageWidth = image.getWidth();
	final int imageHeight = image.getHeight();

	int windowWidth = imageWidth;
	int windowHeight = imageHeight;

	if (addShadow) {
	    windowWidth += nShadowPixels;
	    windowHeight += nShadowPixels;
	}

	setSize(new Dimension(windowWidth, windowHeight));
	setLocationRelativeTo(null);
	Rectangle windowRect = getBounds();

	splash = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2 = (Graphics2D) splash.getGraphics();
	if (addShadow || honorTransparency) {

	    try {
		Robot robot = new Robot(getGraphicsConfiguration().getDevice());
		BufferedImage capture = robot.createScreenCapture(new Rectangle(windowRect.x,
			windowRect.y, windowRect.width + nShadowPixels, windowRect.height
				+ nShadowPixels));
		g2.drawImage(capture, null, 0, 0);
	    } catch (AWTException e) {
		e.printStackTrace();
	    }

	    if (addShadow) {
		BufferedImage shadow = new BufferedImage(imageWidth + nShadowPixels, imageHeight
			+ nShadowPixels, BufferedImage.TYPE_INT_ARGB);
		Graphics g = shadow.getGraphics();
		g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.3f));
		g.fillRoundRect(6, 6, imageWidth, imageHeight, 12, 12);

		g2.drawImage(shadow, getBlurOp(7), 0, 0);
	    }
	}

	g2.drawImage(image, 0, 0, this);
    }

    /**
         * Some voodoo on the image
         * 
         * @param size
         * @return
         */
    private ConvolveOp getBlurOp(int size) {
	float[] data = new float[size * size];
	float value = 1 / (float) (size * size);
	for (int i = 0; i < data.length; i++) {
	    data[i] = value;
	}
	return new ConvolveOp(new Kernel(size, size, data));
    }

    /**
         * A main method, for demonstration purposes.
         * 
         * @param args
         * @throws Exception
         */
    public static void main(String[] args) throws Exception {
	SplashImage.showSplashImage(new FileInputStream("c:/owned.png"), false, true, 5000);
	System.out.println("Ohhh, pretty.");
    }
}
