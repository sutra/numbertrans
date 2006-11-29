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

import javax.sound.sampled.*;

import java.io.*;

/**
 * @author Jonathan
 */
public class SoundUtils {

    /**
         * Asynconously plays a sound file, given a path. However, if the
         * program exits before the sound has finished playing, the sound will
         * stop at program termination.
         * 
         * @throws IOException
         *                 If there is a problem when attempting to open or read
         *                 the file at <code>path</code>.
         * @throws UnsupportedAudioFileException
         *                 If the format of the specified sound file is not
         *                 recognized.
         * @throws LineUnavailableException
         *                 If there is no output line available to play the
         *                 sound.
         */
    public static void playSound(String path) throws UnsupportedAudioFileException, IOException,
	    LineUnavailableException {
	AudioInputStream in = AudioSystem.getAudioInputStream(new File(path));
	Clip clip = AudioSystem.getClip();
	clip.open(in);
	clip.loop(0);
    }

    /**
         * Synconously plays a sound file, given a path.
         * 
         * @throws IOException
         *                 If there is a problem when attempting to open or read
         *                 the file at <code>path</code>.
         * @throws UnsupportedAudioFileException
         *                 If the format of the specified sound file is not
         *                 recognized.
         * @throws LineUnavailableException
         *                 If there is no output line available to play the
         *                 sound.
         */
    public static void playSoundSync(String path) throws UnsupportedAudioFileException,
	    IOException, LineUnavailableException {
	AudioInputStream in = AudioSystem.getAudioInputStream(new File(path));
	Clip clip = AudioSystem.getClip();
	clip.open(in);
	clip.loop(0);
	clip.drain();
    }

    /**
         * Asynconously plays a sound file, given a path, but guarantees that
         * the sound will finish playing. However, if an error occurs in playing
         * the sound, it will fail silently.
         */
    public static void playSoundAsyncFinish(final String path) {
	Thread thread = new Thread() {
	    public void run() {
		try {
		    playSoundSync(path);
		} catch (Exception e) {
		    ;
		}
	    }
	};
	thread.setDaemon(false);
	thread.start();
    }

    /**
         * Main method FOR TESTING
         * 
         * @param args
         */
    public static void main(String[] args) throws Exception {
	SoundUtils.playSound("c:/apollo13.wav");
	// this line is necessary since the sound is played async
	Thread.sleep(3000);
    }
}