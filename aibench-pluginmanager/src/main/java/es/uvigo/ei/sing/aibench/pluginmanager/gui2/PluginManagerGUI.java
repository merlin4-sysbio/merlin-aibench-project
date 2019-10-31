/*
Copyright 2007 Daniel Gonzalez Peña, Florentino Fernandez Riverola


This file is part of the AIBench Project. 

AIBench Project is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

AIBench Project is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser Public License for more details.

You should have received a copy of the GNU Lesser Public License
along with AIBench Project.  If not, see <http://www.gnu.org/licenses/>.
 */

/*  
 * PluginManagerGUI.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on Nov 16, 2012
 */
package es.uvigo.ei.sing.aibench.pluginmanager.gui2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import es.uvigo.ei.aibench.Util;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.repository.NotInitializedException;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.aibench.workbench.Workbench;
import es.uvigo.ei.aibench.workbench.utilities.Utilities;
import es.uvigo.ei.sing.aibench.pluginmanager.PluginManager;
import es.uvigo.ei.sing.aibench.pluginmanager.gui.PluginActionProvider;
import es.uvigo.ei.sing.aibench.pluginmanager.gui.PluginInformationComponent;
import es.uvigo.ei.sing.aibench.pluginmanager.gui.WaitingGUI;

/**
 *
 */
public class PluginManagerGUI extends JDialog implements InputGUI, NeedsRestartListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static boolean restart = false;
	private JButton jButtonCancel;
	private JPanel jPanelButtons;
	protected JButton btnRestart;
	
	private JPanel repositoryPlugins;

	private PluginInformationComponent info;

	protected WaitingGUI waitingGUI;
	protected Thread progressThread;
	protected Thread waitingThread;
	
	protected JDialog thisDialog;

	public PluginManagerGUI() {

		super(Workbench.getInstance().getMainFrame());
		initProgressThread();
		thisDialog = this;
		
		progressThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				initGUI();
				try {
					PluginManager.getInstance().getPluginDownloader();
					addNewPluginsPane();
				} catch (Exception e) {
					// if(System.getProperty("aibench.nogui")!=null)return;
					// JOptionPane.showMessageDialog(null, "Can not connect to server",
					// "Error", JOptionPane.ERROR_MESSAGE);

					if (repositoryPlugins != null)
						repositoryPlugins.removeAll();
					addPluginsPaneWithError(repositoryPlugins, "Can not connect to server!");
				}
				setPreferredSize(new Dimension(800, 500));
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

				addWindowListener(getClosingListener());
				pack();
				Utilities.centerOnWindow(thisDialog);
				setModal(true);
				stopProgressThread();
				setVisible(true);
			}
		});
		progressThread.start();
	}

	protected WindowListener getClosingListener() {
		return new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				clean();
				finish();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		};
	}

	protected void initProgressThread() {
		
		waitingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				waitingGUI = new WaitingGUI(Workbench.getInstance().getMainFrame());
				waitingGUI.setVisible(true);
				System.out.println("Tread up");
			}
		});
		waitingThread.start();
		
	}

	protected void stopProgressThread() {
		waitingGUI.dispose();
//		try {
//			waitingThread.join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * @param repositoryPlugins2
	 * @param message
	 */
	private void addPluginsPaneWithError(JPanel panel, String message) {

		JLabel frame = new JLabel();
		frame.setText(message);

		panel.add(frame);
		// updateRestartBotton();

	}

	private void initGUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.5, 0.5, 0.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel instaledPlugins = new JPanel();
		// instaledPlugins.setBorder(new TitledBorder(new LineBorder(new
		// Color(0, 0, 0)), "Instaled Plugins", TitledBorder.LEADING,
		// TitledBorder.BELOW_TOP, null, Color.BLACK));
		GridBagConstraints gbc_instaledPlugins = new GridBagConstraints();
		gbc_instaledPlugins.insets = new Insets(0, 0, 5, 0);
		gbc_instaledPlugins.fill = GridBagConstraints.BOTH;
		gbc_instaledPlugins.gridx = 0;
		gbc_instaledPlugins.gridy = 0;
		getContentPane().add(instaledPlugins, gbc_instaledPlugins);
		instaledPlugins.setLayout(new BorderLayout(0, 0));
		
		info = new PluginInformationComponent(this);
		instaledPlugins.add(info);

		// info.hideIDPluginsColumn();
//		info.setHideColumn(3);
//		info.setHideColumn(3);
//		info.setHideColumn(4);

		repositoryPlugins = new JPanel();
		repositoryPlugins.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Repository Plugins", TitledBorder.LEADING,
				TitledBorder.BELOW_TOP, null, null));
		GridBagConstraints gbc_repositoryPlugins = new GridBagConstraints();
		gbc_repositoryPlugins.insets = new Insets(0, 0, 5, 0);
		gbc_repositoryPlugins.fill = GridBagConstraints.BOTH;
		gbc_repositoryPlugins.gridx = 0;
		gbc_repositoryPlugins.gridy = 1;
		getContentPane().add(repositoryPlugins, gbc_repositoryPlugins);
		repositoryPlugins.setLayout(new BorderLayout(0, 0));

		GridBagConstraints gbc_btnRestart = new GridBagConstraints();
		gbc_btnRestart.anchor = GridBagConstraints.EAST;
		gbc_btnRestart.gridx = 0;
		gbc_btnRestart.gridy = 2;
		{
			jPanelButtons = new JPanel();
			GridBagLayout jPanelButtonsLayout = new GridBagLayout();
			getContentPane().add(jPanelButtons,
					new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			jPanelButtonsLayout.rowWeights = new double[] { 0.1 };
			jPanelButtonsLayout.rowHeights = new int[] { 7 };
			jPanelButtonsLayout.columnWeights = new double[] { 0.1, 0.1 };
			jPanelButtonsLayout.columnWidths = new int[] { 7, 7 };
			jPanelButtons.setLayout(jPanelButtonsLayout);
			{
				btnRestart = new JButton("Restart");
				btnRestart.setEnabled(false);
				jPanelButtons.add(btnRestart, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));
			}
			{
				jButtonCancel = new JButton();
				jPanelButtons.add(jButtonCancel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				jButtonCancel.setText("Exit & Cancel");
				jButtonCancel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						clean();
						finish();
					}

				});
			}
		}

//		info.addNeedToRestartListener(this);
	}

	private void clean() {
		if (PluginManager.getInstance().isDownloaderActive())
			PluginManager.getInstance().getPluginDownloader().cancelDownloads();
		PluginManager.getInstance().deleteAllPluginsToInstall();
		PluginManager.getInstance().removeNeedToRestartListener(this);
//		if (info != null)
//			info.removeNeedToRestartListener(this);
//		PluginActionProvider.pluginDownloaderChanged();
	}

	public void addNewPluginsPane() {
		if (repositoryPlugins == null)
			repositoryPlugins = new JPanel();
		repositoryPlugins.removeAll();
		try {
			PluginManager.getInstance().getPluginDownloader().downloadInfo();
		} catch (NotInitializedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PluginManager.getInstance().addNeedToRestartListener(this);
		AddPluginPane repository = new AddPluginPane();
		repositoryPlugins.add(repository);
		updateRestartBotton();
		repositoryPlugins.updateUI();
	}

	private void updateRestartBotton() {
		// btnRestart.setEnabled(restart);

		btnRestart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
//				Util.restart();
				System.out.println("should restart when pressed!");
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uvigo.ei.aibench.workbench.InputGUI#init(es.uvigo.ei.aibench.workbench
	 * .ParamsReceiver, es.uvigo.ei.aibench.core.operation.OperationDefinition)
	 */
	public void init(ParamsReceiver receiver, OperationDefinition<?> operation) {

		setLocationRelativeTo(Workbench.getInstance().getMainFrame().getContentPane());
//		this.setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.uvigo.ei.aibench.workbench.InputGUI#onValidationError(java.lang.Throwable
	 * )
	 */
	public void onValidationError(Throwable t) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uvigo.ei.aibench.workbench.InputGUI#finish()
	 */
	public void finish() {
		this.setModal(true);
		this.setVisible(false);
		this.dispose();
//		try {
//			progressThread.;
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.uvigo.ei.sing.aibench.pluginmanager.gui2.NeedsRestartListener#
	 * needsToRestart()
	 */
	public void needsToRestart() {
		restart = true;
		btnRestart.setEnabled(restart);
	}
	
}
