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
 * PluginInformationComponent.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 07/04/2009
 */
package es.uvigo.ei.sing.aibench.pluginmanager.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import es.uvigo.ei.aibench.repository.NotInitializedException;
import es.uvigo.ei.sing.aibench.pluginmanager.PluginManager;
import es.uvigo.ei.sing.aibench.pluginmanager.gui2.NeedsRestartListener;
import es.uvigo.ei.sing.aibench.pluginmanager.gui2.PluginManagerGUI;

/**
 * @author Miguel Reboiro Jato
 *
 */
public class PluginInformationComponent extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	final PluginInformationPane informationPane = new PluginInformationPane();
	
	
	public PluginInformationComponent(final PluginManagerGUI pluginManagerGUI) {
		super(new BorderLayout());
		
		final JLabel lblRepository = new JLabel("Repository: " + PluginManager.getInstance().getHost());
		final JButton btnChangeRepository = new JButton("Change Repository");
		final JButton btnViewRepository = new JButton("View Repository");
		
		btnViewRepository.setVisible(false);
		JPanel panelRepository = new JPanel();
		btnViewRepository.setEnabled(PluginManager.getInstance().isDownloaderActive());
		panelRepository.add(lblRepository);
		panelRepository.add(btnChangeRepository);
		panelRepository.add(btnViewRepository);

//		final PluginInformationPane informationPane = new PluginInformationPane();
		
		this.add(panelRepository, BorderLayout.NORTH);
		this.add(informationPane, BorderLayout.CENTER);
		
		btnChangeRepository.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				String newRepository = JOptionPane.showInputDialog(
					"Introduce the URL of the repository:", 
					PluginManager.getInstance().getHost()
				);
				if (newRepository != null) {
					try {
						PluginManager.getInstance().setPluginRepository(newRepository);
						lblRepository.setText("Repository: " + newRepository);
						PluginActionProvider.pluginDownloaderChanged();
						pluginManagerGUI.addNewPluginsPane();
						informationPane.repaint();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(
							null, 
							"Error changing repository host: " + e1.getMessage(), 
							"Error", 
							JOptionPane.ERROR_MESSAGE
						);
					}
				}
				btnViewRepository.setEnabled(PluginManager.getInstance().isDownloaderActive());
			}
		});
		
		btnViewRepository.addMouseListener(new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					RepositoryInformationPane pane = new RepositoryInformationPane();
					JFrame frmRepository = new JFrame("Repository: " + PluginManager.getInstance().getHost());
					frmRepository.getContentPane().add(pane);
					frmRepository.pack();
					pane.packColumns();
					frmRepository.setLocationRelativeTo(null);
					frmRepository.setVisible(true);
				} catch (NotInitializedException nie) {
					JOptionPane.showMessageDialog(null, "Repository not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * @param i
	 */
	public void setHideColumn(int i) {
		informationPane.setHideColumns(i);
		
	}
	
	public void addNeedToRestartListener(NeedsRestartListener l){
		informationPane.addNeedToRestartListener(l);	
	}
	
	public void removeNeedToRestartListener(NeedsRestartListener l){
		if(informationPane!=null)
			informationPane.removeNeedToRestartListener(l);	
	}
	public void hideIDPluginsColumn(){
		informationPane.hideIDPluginsColumn();
	}
}
