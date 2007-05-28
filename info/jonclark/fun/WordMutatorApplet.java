/*
 * Created on Jan 12, 2007
 */
package info.jonclark.fun;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JApplet;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTextArea;

public class WordMutatorApplet extends JApplet {

    private static final long serialVersionUID = 2179399125367118520L;
    private JPanel jContentPane = null;
    private JScrollPane jScrollPane = null;
    private JScrollPane jScrollPane1 = null;
    private JButton jButton = null;
    private JTextArea jTextArea = null;
    private JTextArea jTextArea1 = null;

    /**
     * This is the xxx default constructor
     */
    public WordMutatorApplet() {
	super();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void init() {
	this.setSize(500, 300);
	this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
	if (jContentPane == null) {
	    jContentPane = new JPanel();
	    jContentPane.setLayout(null);
	    jContentPane.setPreferredSize(new Dimension(0, 0));
	    jContentPane.add(getJScrollPane(), null);
	    jContentPane.add(getJScrollPane1(), null);
	    jContentPane.add(getJButton(), null);
	}
	return jContentPane;
    }

    /**
     * This method initializes jScrollPane	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
    	jScrollPane = new JScrollPane();
    	jScrollPane.setBounds(new Rectangle(37, 23, 403, 100));
    	jScrollPane.setViewportView(getJTextArea());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jScrollPane1	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPane1() {
        if (jScrollPane1 == null) {
    	jScrollPane1 = new JScrollPane();
    	jScrollPane1.setBounds(new Rectangle(38, 180, 402, 86));
    	jScrollPane1.setViewportView(getJTextArea1());
        }
        return jScrollPane1;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton() {
        if (jButton == null) {
    	jButton = new JButton(new AbstractAction() {
	    private static final long serialVersionUID = -2265970273232767906L;
	    public void actionPerformed(ActionEvent e) {
		jTextArea1.setText(WordMutator.mutateSentence(jTextArea.getText()));
	    }
    	});
    	jButton.setBounds(new Rectangle(198, 138, 87, 27));
    	jButton.setText("Mutate");
        }
        return jButton;
    }

    /**
     * This method initializes jTextArea	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJTextArea() {
        if (jTextArea == null) {
    	jTextArea = new JTextArea();
        }
        return jTextArea;
    }

    /**
     * This method initializes jTextArea1	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJTextArea1() {
        if (jTextArea1 == null) {
    	jTextArea1 = new JTextArea();
        }
        return jTextArea1;
    }

}
