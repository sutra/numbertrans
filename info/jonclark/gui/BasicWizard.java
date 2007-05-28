/*
 * Created on Feb 23, 2007
 */
package info.jonclark.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public abstract class BasicWizard extends JPanel {

    private static final long serialVersionUID = 4719978441000492969L;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel viewPanel = new JPanel();
    private final JPanel navigationPanel = new JPanel();
    private JButton butBack;
    private JButton butNext;
    private JButton butFinishOrCancel;

    private final Component[] steps;
    private final String[] keys;
    private final boolean[] visible;
    private int nCurrentStep = 0;

    /**
         * @param steps
         *                An array of steps that might or might not be shown
         */
    public BasicWizard(final Component[] steps) {
	this.steps = steps;

	// create arrays & init basic values
	this.keys = new String[steps.length];
	this.visible = new boolean[steps.length];

	// initialize arrays
	viewPanel.setLayout(cardLayout);
	for (int i = 0; i < steps.length; i++) {
	    keys[i] = generateKey(steps[i]);
	    visible[i] = true;
	    viewPanel.add(steps[i], keys[i]);
	}

	butBack = new JButton(new AbstractAction() {
	    private static final long serialVersionUID = -4910278131575419276L;

	    public void actionPerformed(ActionEvent e) {
		back();
	    }
	});
	butBack.setText("<< Back");
	navigationPanel.add(butBack);

	butNext = new JButton(new AbstractAction() {
	    private static final long serialVersionUID = -4910278131575419276L;

	    public void actionPerformed(ActionEvent e) {
		next();
	    }
	});
	butNext.setText("Next >>");
	navigationPanel.add(butNext);

	JLabel space = new JLabel("          ");
	navigationPanel.add(space);

	butFinishOrCancel = new JButton(new AbstractAction() {
	    private static final long serialVersionUID = -4910278131575419276L;

	    public void actionPerformed(ActionEvent e) {
		finishOrCancel();
	    }
	});
	navigationPanel.add(butFinishOrCancel);

	this.setLayout(new BorderLayout());
	this.add(viewPanel, BorderLayout.CENTER);
	this.add(navigationPanel, BorderLayout.SOUTH);
	this.setMinimumSize(GuiUtils.caclulateMaxDimensions(steps));

	updateStep();
    }

    /**
         * @param index
         *                The index in the array of components passed to the
         *                constructor
         * @param visible
         *                Whether or not this component should be shown as a
         *                step
         */
    public void setStepVisible(int index, boolean bVisible) {
	visible[index] = bVisible;
	updateButtons();
    }

    private static String generateKey(Component comp) {
	return comp.hashCode() + "";
    }

    private void finishOrCancel() {
	if (butFinishOrCancel.getText().equals("Finish")) {
	    movedAwayFromStep(steps[nCurrentStep]);
	    finish();
	} else {
	    int nResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?",
		    "Confirm Cancel", JOptionPane.YES_NO_OPTION);
	    if (nResult == JOptionPane.YES_OPTION)
		cancel();
	}
    }

    /**
         * @param nStartStep
         * @return -1 if there is no previous visible step
         */
    private int findPreviousVisibleStep(final int nStartStep) {
	int nPrevVisibleStep = nStartStep;
	nPrevVisibleStep--;
	while (nPrevVisibleStep > 0 && !visible[nPrevVisibleStep])
	    nPrevVisibleStep--;

	// make sure there was actually WAS a previous visible step
	if (nPrevVisibleStep != -1)
	    return nPrevVisibleStep;
	else
	    return -1;
    }

    /**
         * @param nStartStep
         * @return -1 if there is no next visible step
         */
    private int findNextVisibleStep(final int nStartStep) {
	int nNextVisibleStep = nStartStep;
	nNextVisibleStep++;
	while (nNextVisibleStep < steps.length && !visible[nNextVisibleStep])
	    nNextVisibleStep++;

	// make sure there was actually WAS a next visible step
	if (nNextVisibleStep != steps.length)
	    return nNextVisibleStep;
	else
	    return -1;
    }

    protected void back() {
	final Component stepCameFrom = steps[nCurrentStep];
	movedAwayFromStep(stepCameFrom);

	nCurrentStep = findPreviousVisibleStep(nCurrentStep);

	final Component currentStep = steps[nCurrentStep];
	updateStep();
	movedToStep(currentStep);
    }

    /**
         * Moves to the next step in this wizard. (This method is called by the
         * "Next" button.
         */
    protected void next() {
	final Component stepCameFrom = steps[nCurrentStep];
	movedAwayFromStep(stepCameFrom);

	nCurrentStep = findNextVisibleStep(nCurrentStep);
	updateStep();

	final Component currentStep = steps[nCurrentStep];
	movedToStep(currentStep);
    }

    private void updateStep() {
	cardLayout.show(viewPanel, keys[nCurrentStep]);
	updateButtons();
    }

    private void updateButtons() {
	final int nNext = findNextVisibleStep(nCurrentStep);
	final int nPrevious = findPreviousVisibleStep(nCurrentStep);
	boolean enableNext = (nNext != -1);
	boolean enableBack = (nPrevious != -1);

	butNext.setEnabled(enableNext);
	butBack.setEnabled(enableBack);

	if (enableNext)
	    butFinishOrCancel.setText("Cancel");
	else
	    butFinishOrCancel.setText("Finish");
    }

    public abstract void movedAwayFromStep(Component stepCameFrom);

    public abstract void movedToStep(Component stepCameFrom);

    public abstract void finish();

    public abstract void cancel();

    public static void main(String... args) {
	JTextArea text = new JTextArea();
	text.setPreferredSize(new Dimension(200, 200));

	JButton but = new JButton("hi");
	but.setPreferredSize(new Dimension(300, 300));

	JList list = new JList(new String[] { "hi", "bye" });
	list.setPreferredSize(new Dimension(400, 400));

	Component[] steps = { text, but, list };
	BasicWizard wizard = new BasicWizard(steps) {

	    /**
	     * 
	     */
	    private static final long serialVersionUID = 522280440849320858L;

	    @Override
	    public void cancel() {
		System.exit(0);
	    }

	    @Override
	    public void finish() {
		System.exit(0);
	    }

	    @Override
	    public void movedAwayFromStep(Component stepCameFrom) {
		System.err.println("Came from: " + stepCameFrom.getClass().getName());
	    }

	    @Override
	    public void movedToStep(Component currentStep) {
		System.err.println("Currently at: " + currentStep.getClass().getName());
	    }

	};

	JFrame testFrame = new JFrame();
	testFrame.setTitle("BasicWizard Example");
	testFrame.add(wizard);
	testFrame.pack();
	testFrame.setVisible(true);
    }
}
