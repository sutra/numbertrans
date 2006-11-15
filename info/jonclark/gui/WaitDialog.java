/*
 * Created on Jun 8, 2006
 */
package info.jonclark.gui;

import java.awt.*;

import javax.swing.*;

/**
 * @author Jonathan
 */
public class WaitDialog {
    JDialog diaLoading = new JDialog();
    
	public void showWaitDialog(String strTitle, String strMessage) {
	    final JLabel lblLoadingMessage = new JLabel(strMessage);;
	    
		// Initialize if necessary
		if( diaLoading == null ) {			
			//centerWindow( diaLoading ); // how to center?
			
			JProgressBar progress = new JProgressBar();
			progress.setIndeterminate(true);
			JPanel panLoading = new JPanel();
			panLoading.setLayout( new BoxLayout(panLoading, BoxLayout.Y_AXIS));
			diaLoading.setContentPane(panLoading);
			panLoading.add(progress);
			panLoading.add(Box.createRigidArea(new Dimension(0,20)));
			panLoading.add(lblLoadingMessage);
			diaLoading.setTitle(strTitle);
			diaLoading.setSize(300,100);
			diaLoading.setLocation(300,300);
			diaLoading.setVisible(true);
		} else {
			lblLoadingMessage.setText(strMessage);
			diaLoading.setVisible(true);
		}
	}
	
	public void hideWaitDialog() {
		diaLoading.setVisible(false);
		diaLoading.dispose();
	}
	
	public static void main(String[] args) throws Exception {
	    WaitDialog wait = new WaitDialog();
	    wait.showWaitDialog("Important", "You's gonna have to keep on waiting...");
	    Thread.sleep(10000);
	    wait.hideWaitDialog();
	}
}
