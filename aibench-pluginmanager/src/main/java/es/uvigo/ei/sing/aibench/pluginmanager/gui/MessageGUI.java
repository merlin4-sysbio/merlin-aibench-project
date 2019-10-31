package es.uvigo.ei.sing.aibench.pluginmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import es.uvigo.ei.aibench.workbench.utilities.Utilities;


public class MessageGUI extends JDialog{

	
	private String message;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane jEditorPaneInfo;
	private boolean isWarning;
	private boolean hasRepeatCheck;
	private boolean doNotShowAgain;
	private String repeatMsg;
	
	/**
	 * Show Message GUI
	 * @param message The message that will be shown, support HTML
	 * */
	public MessageGUI(String message){
		this(message, false);
	}
	
	/**
	 * Show Message GUI
	 * @param message The message that will be shown, support HTML
	 * @param isWarning True for warning message. False for informative message
	 * */
	public MessageGUI(String message, boolean isWarning){
		this(message, isWarning, false);
	}
	
	/**
	 * Show Message GUI
	 * @param message The message that will be shown, support HTML
	 * @param isWarning True for warning message. False for informative message
	 * @param hasRepeatCheck True to show checkbox "Do not show this message again." (Default)
	 * */
	public MessageGUI(String message, boolean isWarning, boolean hasRepeatCheck){
		this(message, isWarning, hasRepeatCheck, "Do not show this message again.");
	}
	
	/**
	 * Show Message GUI
	 * @param message The message that will be shown, support HTML
	 * @param isWarning True for warning message. False for informative message
	 * @param hasRepeatCheck True to show checkbox @repeatMsg message
	 * @param repeatMsg Message that will be shown along with the checkbox
	 * */
	public MessageGUI(String message, boolean isWarning, boolean hasRepeatCheck, String repeatMsg){
		super(/*Workbench.getInstance().getMainFrame()*/);
		this.message = message;
		this.isWarning = isWarning;
		this.hasRepeatCheck = hasRepeatCheck;
		this.repeatMsg = repeatMsg;
		initGUI();
		this.setSize(200, 200);
		Utilities.centerOnOwner(this);
	}


	private void initGUI() {
		{
			jEditorPaneInfo = new JEditorPane();
			getContentPane().add(jEditorPaneInfo, BorderLayout.CENTER);
			jEditorPaneInfo.setContentType("text/html");
			
			jEditorPaneInfo.setText(message);
			
			jEditorPaneInfo.setEditable(false);
			jEditorPaneInfo.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent hle) {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
						Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
					        try {
					            desktop.browse(hle.getURL().toURI());
					        } catch (Exception e) {
					        	e.printStackTrace();
					        }
					    }
                    }
                }
            });
			jEditorPaneInfo.setBackground(new Color(0f,0f,0f,0f));	
			
			
			JCheckBox checkbox = new JCheckBox(repeatMsg); 
			
			Object[] params = null;
			if(hasRepeatCheck) 
				params = new Object[]{jEditorPaneInfo, checkbox};
			else
				params = new Object[]{jEditorPaneInfo};
			
			
			if(isWarning)
				JOptionPane.showMessageDialog(null, params, "Warning", JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, params, "Information", JOptionPane.INFORMATION_MESSAGE);
			
			doNotShowAgain = checkbox.isSelected(); 
		}
	}
	
	public boolean doNotShowAgain(){
		return doNotShowAgain;
	}
	
	public static void main(String[] args)
	{
		boolean doNotShowAgain = (new MessageGUI("<HTML><font face='verdana'>A new version of OptFlux has been released! Version <b>3.1.1</b> is available. <p>You can download this version using the link <a href='http://www.optflux.org/'>http://www.optflux.org/</a></p></font></HTML>", false, true)).doNotShowAgain();
		
		System.out.println("doNotShowAgain " + doNotShowAgain);
		
		
		
//		public static void main(String[] args){
//		JFrame frame = new JFrame();
//		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
//		JCheckBox checkbox = new JCheckBox("Do not show this message again.");  
//		String message = "In this new version some workspaces may lose items. \nBe sure to make a backup before load them.";  
//		Object[] params = {message, checkbox};  
//		int n = JOptionPane.showConfirmDialog(frame, params, "OptFlux Information", JOptionPane.DEFAULT_OPTION);  
//		boolean dontShow = checkbox.isSelected(); 
//		System.out.println(dontShow);
//		
//		//frame.add(combo); 
//		frame.setVisible(true);
//		frame.pack();
//	}
	}

}
