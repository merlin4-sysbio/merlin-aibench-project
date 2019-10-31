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
 * UpdateAllPluginsGUI.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 6 de Nov de 2012
 */
package es.uvigo.ei.sing.aibench.pluginmanager.gui2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.platonos.pluginengine.Plugin;

import es.uvigo.ei.aibench.Util;
import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.repository.NotInitializedException;
import es.uvigo.ei.aibench.repository.PluginDownloadEvent;
import es.uvigo.ei.aibench.repository.PluginDownloadInfoEvent;
import es.uvigo.ei.aibench.repository.PluginDownloadListener;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.aibench.workbench.utilities.Utilities;
import es.uvigo.ei.sing.aibench.pluginmanager.PluginManager;
import es.uvigo.ei.sing.aibench.pluginmanager.gui.PluginActionProvider;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class UpdateAllPluginsExtraGUI extends JDialog implements InputGUI, PluginDownloadListener {

	/**
	 * 
	 */
	
	private static boolean needsRestart = false;
	private static final long serialVersionUID = 1L;
	private JButton jButtonRestart;
	private JButton jButtonInstallAll;
	private JScrollPane scrollPane;
	private JTable table;
	private int numToDownload;
	private int contUpdated;
	private ParamsReceiver rec;
	private JButton jButtonCancel;
	private JPanel jPanelButtonsPane;
	
	//TODO: This class is duplicated - change it!
	public UpdateAllPluginsExtraGUI()
	{
		super(/*Workbench.getInstance().getMainFrame(),true*/);
		try {
			List<Plugin> toUpdate = PluginManager.getInstance().existUpdatesFromRepository(false);
			if(toUpdate == null || toUpdate.size()==0)
			{
				if(!PluginManager.getInstance().isDownloaderActive())
				{
					initGui2("Can not connect to server!");
				}
				else
					initGui2("The software is updated!");
			}
			else
			{
				this.setTitle("Update plugins");
				numToDownload = toUpdate.size();
				initGUI(toUpdate);
				this.setSize(800, 600);
				Utilities.centerOnOwner(this);
				this.setVisible(true);
//				this.setModal(true);
				this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			}
			
		} catch (NotInitializedException e) {
			initGui2("Can not connect to server!");
			finish();
		
		} catch (IOException e) {
			initGui2("Can not connect to server!");
			finish();
		}
//		this.setLocationRelativeTo(Workbench.getInstance().getMainFrame());

	}
	
	
	/**
	 * 
	 */
	private void initGui2(String message) {
		GridBagLayout jPanelInfoPanelLayout = new GridBagLayout();
		jPanelInfoPanelLayout.rowWeights = new double[] {1.0, 0.0};
		jPanelInfoPanelLayout.rowHeights = new int[] {40, 0};
		jPanelInfoPanelLayout.columnWeights = new double[] {1.0};
		jPanelInfoPanelLayout.columnWidths = new int[] {200};
		getContentPane().setLayout(jPanelInfoPanelLayout);
	
		
		JLabel label = new JLabel();
		label.setText(message);
		GridBagConstraints gbc_label= new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		getContentPane().add(label, gbc_label);

		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		Utilities.centerOnWindow(this);
		pack();
		setModal(true);
		setVisible(true);
	}




	/**
	 * @param toUpdate 
	 * 
	 */
	private void initGUI(List<Plugin> toUpdate) {

		GridBagLayout jPanelInfoPanelLayout = new GridBagLayout();
		jPanelInfoPanelLayout.rowWeights = new double[] {1.0, 0.0};
		jPanelInfoPanelLayout.rowHeights = new int[] {0, 30};
		jPanelInfoPanelLayout.columnWeights = new double[] {1.0};
		jPanelInfoPanelLayout.columnWidths = new int[] {7};
//			jPanelInfoPanel.setLayout(jPanelInfoPanelLayout);
		getContentPane().setLayout(jPanelInfoPanelLayout);
//			getContentPane().add(jPanelInfoPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		{
			scrollPane = new JScrollPane();
			GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			getContentPane().add(scrollPane, gbc_scrollPane);
			{
				table = new JTable(updateModel(toUpdate));
				table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableRenderer(new PluginActionProvider()));
//					table.getColumn().setCellRenderer(
//						new DefaultTableRenderer(new PluginActionProvider())
//					);
				
				scrollPane.setViewportView(table);
			}
		}
		{
			jPanelButtonsPane = new JPanel();
			GridBagLayout jPanelButtonsPaneLayout = new GridBagLayout();
			getContentPane().add(jPanelButtonsPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			jPanelButtonsPaneLayout.rowWeights = new double[] {0.1};
			jPanelButtonsPaneLayout.rowHeights = new int[] {7};
			jPanelButtonsPaneLayout.columnWeights = new double[] {0.1, 0.1};
			jPanelButtonsPaneLayout.columnWidths = new int[] {7, 7};
			jPanelButtonsPane.setLayout(jPanelButtonsPaneLayout);
			{
				jButtonInstallAll = new JButton();
				jPanelButtonsPane.add(jButtonInstallAll, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				jButtonInstallAll.setText("Update");
				jButtonInstallAll.setVisible(!needsRestart);
				jButtonInstallAll.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent arg0) {					
						installAllPlugins();
					};
				});
				
			}
			{
				jButtonRestart = new JButton();
				jPanelButtonsPane.add(jButtonRestart, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				jButtonRestart.setText("Restart");
				jButtonRestart.setVisible(needsRestart);
				jButtonRestart.addActionListener(new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						restart();
					};
				});
			}
			{
				jButtonCancel = new JButton();
				jPanelButtonsPane.add(jButtonCancel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				jButtonCancel.setText("Cancel");
				jButtonCancel.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						deletePluginsToIntallAndExit();
					
					}
				});
			}
			
			this.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent ev) {
	            	deletePluginsToIntallAndExit();
	            }
			});
		}

		
		PluginManager.getInstance().getPluginDownloader().addDownloadListener(this);

	}

	/**
	 * 
	 */
	protected void deletePluginsToIntallAndExit() {
		PluginManager.getInstance().deleteAllPluginsToInstall();
		this.finish();
	}


	/**
	 * @param toUpdate 
	 * 
	 */
	private TableModel updateModel(List<Plugin> toUpdate) {
		
		Object[][] data = new Object[toUpdate.size()][2];
		Object[] headers = {"Name","Progress"};
		
		int i =0;
		for(Plugin p : toUpdate){
			data[i][0] = p.getName();
			data[i][1] = p.getUID();
			i++;
		}
		DefaultTableModel model = new DefaultTableModel(data,headers){
			
			@Override
			public boolean isCellEditable(int row,
	                int column) {
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int column){
				switch(column) {
				case 0:
					return String.class;
				case 1:
					return Plugin.class;
				}
				return null;
			}
		};
		return model;
		
	}


	

	/**
	 * 
	 */
	protected void restart() {
//		Util.restart();
		System.out.println("Should restart but i am not working");
	}




	protected void installAllPlugins() {
		PluginManager.getInstance().updateAllPlugins();
		jButtonInstallAll.setVisible(false);
	}




	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.workbench.InputGUI#onValidationError(java.lang.Throwable)
	 */
	public void onValidationError(Throwable t) {
		// TODO Auto-generated method stub
		
	}

	public void termination(){
		rec.paramsIntroduced(new ParamSpec[] {});		
	}
	

	public void finish() {
//		this.setModal(false);
		setVisible(false);
		dispose();
	}

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {
		rec = arg0;
	    
	}




	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.repository.PluginDownloadListener#downloadStarted(es.uvigo.ei.aibench.repository.PluginDownloadEvent)
	 */
	public void downloadStarted(PluginDownloadEvent event) {
		// TODO Auto-generated method stub
		
	}




	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.repository.PluginDownloadListener#downloadStep(es.uvigo.ei.aibench.repository.PluginDownloadEvent)
	 */
	public void downloadStep(PluginDownloadEvent event) {
		// TODO Auto-generated method stub
		
	}




	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.repository.PluginDownloadListener#downloadFinished(es.uvigo.ei.aibench.repository.PluginDownloadEvent)
	 */
	public void downloadFinished(PluginDownloadEvent event) {
		contUpdated++;
		if(contUpdated == numToDownload){
			needsRestart = true;
			jButtonInstallAll.setVisible(!needsRestart);
			jButtonRestart.setVisible(needsRestart);
		}		
	}




	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.repository.PluginDownloadListener#downloadError(es.uvigo.ei.aibench.repository.PluginDownloadEvent)
	 */
	public void downloadError(PluginDownloadEvent event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void downloadInfoStarted(PluginDownloadInfoEvent event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void downloadInfoFinished(PluginDownloadInfoEvent event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void downloadInfoError(PluginDownloadInfoEvent event) {
		// TODO Auto-generated method stub
		
	}
}
