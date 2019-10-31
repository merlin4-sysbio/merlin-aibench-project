package es.uvigo.ei.sing.aibench.pluginmanager.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;


public class WaitingGUI extends JDialog {
	
	protected JProgressBar progressBar;
	
	protected String labelText;
	
	public WaitingGUI() {
		this(null, "Connecting to server...");
	}
	
	public WaitingGUI(Window parent) {
		this(parent, "Connecting to server...");
	}
	
	public WaitingGUI(String labelText) {
		this(null, labelText);
	}
	
	public WaitingGUI(Window parent, String labelText) {
		super(parent);
		this.labelText = labelText;
		initDialog();
	}
	
	protected void initDialog(){
		
		GridBagLayout layout = new GridBagLayout();
		layout.rowHeights = new int[] {0, 0};
		layout.rowWeights = new double[] {0.0, 0.0};
		layout.columnWidths = new int[] {0};
		layout.columnWeights = new double[] {0.0};
		
		this.setLayout(layout);
		
		JLabel label = new JLabel(labelText);
		this.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		this.add(progressBar, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		
		
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		this.setTitle("Please wait");
		
		this.setModal(true);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(this.getParent());
		
		System.out.println("Construct");
	}
	
	public static void main(String[] args) {
		WaitingGUI wait = new WaitingGUI();
		wait.setVisible(true);
	}

}
