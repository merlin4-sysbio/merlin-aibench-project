/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package es.uvigo.ei.sing.aibench.pluginmanager.gui2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.platonos.pluginengine.Plugin;
import org.platonos.pluginengine.PluginEngineException;

import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.repository.NotInitializedException;
import es.uvigo.ei.aibench.repository.info.DependencyInfo;
import es.uvigo.ei.aibench.repository.info.PluginInfo;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.aibench.workbench.Workbench;
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
/**
 *
 */
public class AddPluginPane extends JPanel{
	private final static Logger logger = Logger.getLogger(AddPluginPane.class);
	
	/**
	 * 
	 */
	private static final int DOWNLOAD_INDEX = 4;
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPanePluginsAvailabel;
	private JPanel jPanelNewPluginsToInstall;
	
	private static final String ROOT_LABEL = "New Plugins Available";
	
	
	private static final List<String> COLUMN_NAMES = new ArrayList<String>();
	
	static {
		COLUMN_NAMES.add("New Plugin/Dependencies");
		COLUMN_NAMES.add("Installed Version");
		COLUMN_NAMES.add("Repository Version");
		COLUMN_NAMES.add("Required Version");
		COLUMN_NAMES.add("Download");
	}
	
	private final JXTreeTable pluginsTreeTable = new JXTreeTable();
	private PluginInformationTreeTableModel treeTableModel;
	private JPopupMenu jPopupMenu;
		
	public AddPluginPane() throws NotInitializedException {
		this.updateModel();
		this.createJpopupMenu();
		GridBagLayout thisLayout = new GridBagLayout();
		thisLayout.rowWeights = new double[] {0.1, 0.0};
		thisLayout.rowHeights = new int[] {7, 7};
		thisLayout.columnWeights = new double[] {0.1};
		thisLayout.columnWidths = new int[] {7};
		setLayout(thisLayout);
		jPanelNewPluginsToInstall = new JPanel();
		GridLayout jPanelNewPluginsToInstallLayout = new GridLayout(1, 1);
		jPanelNewPluginsToInstallLayout.setColumns(1);
		jPanelNewPluginsToInstallLayout.setHgap(5);
		jPanelNewPluginsToInstallLayout.setVgap(5);
		jPanelNewPluginsToInstall.setLayout(jPanelNewPluginsToInstallLayout);
		add(jPanelNewPluginsToInstall, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		{
			jScrollPanePluginsAvailabel = new JScrollPane();
			jPanelNewPluginsToInstall.add(jScrollPanePluginsAvailabel);
		}
		jScrollPanePluginsAvailabel.setViewportView(this.pluginsTreeTable);
		this.pluginsTreeTable.getColumnExt(AddPluginPane.DOWNLOAD_INDEX).setCellRenderer(
			new DefaultTableRenderer(new PluginActionProvider())
		);
		
		this.pluginsTreeTable.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.pluginsTreeTable.addHighlighter(new RepositoryHighlighter());
		this.pluginsTreeTable.addMouseListener( new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				switch(e.getModifiers()) {
				case InputEvent.BUTTON1_MASK: {
//					System.out.println("That's the LEFT button");     
					break;
				}
				case InputEvent.BUTTON2_MASK: {
//					System.out.println("That's the MIDDLE button");     
					break;
				}
				case InputEvent.BUTTON3_MASK: {
					JXTreeTable source = (JXTreeTable)e.getSource();
					int row = source.rowAtPoint( e.getPoint() );
					int column = source.columnAtPoint( e.getPoint() );

					if (! source.isRowSelected(row))
						source.changeSelection(row, column, false, false);
					jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
					break;
				}
				}
			}
		});		
		this.setSize(1024, 800);
		this.setVisible(true);
	}

	/**
	 * 
	 */
	private void createJpopupMenu() {
		jPopupMenu = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Description");
		item.setBackground(Color.decode("#F0F0F0"));
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				viewDescription(evt);
			}
		});
		jPopupMenu.add(item);
		
		item = new JMenuItem("View on-line information");
		item.setBackground(Color.decode("#F0F0F0"));
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				viewOnlineInformation(evt);
			}
		});
		jPopupMenu.add(item);
	}

	/**
	 * @param evt
	 */
	protected void viewOnlineInformation(ActionEvent evt) {
		int selectedRow = this.pluginsTreeTable.getSelectedRow();
		if (selectedRow != -1) {
			TreePath path = this.pluginsTreeTable.getPathForRow(selectedRow);
			Object node = path.getLastPathComponent();
			if (node instanceof PluginTreeTableNode) {
				PluginInfo plugin = ((PluginTreeTableNode) node).getUserObject();
				if(plugin.getUrl()!=null)
				{
					internetAccess(plugin.getUrl());
				}
				else
				{
					String  info = "information Not available";
					JOptionPane.showMessageDialog(null, info , "Information", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else
			{
				String  info = "information Not available";
				JOptionPane.showMessageDialog(null, info , "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	public static void internetAccess(String link)
	{
		URL url=null;
		try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HyperlinkEvent hp = new HyperlinkEvent(new Object(),HyperlinkEvent.EventType.ACTIVATED,url);
		if (hp.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
			try {
				Desktop.getDesktop().browse(hp.getURL().toURI());
			} catch(URISyntaxException ioe) {
				ioe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param evt
	 */
	protected void viewDescription(ActionEvent evt) {
		int selectedRow = this.pluginsTreeTable.getSelectedRow();
		if (selectedRow != -1) {
			TreePath path = this.pluginsTreeTable.getPathForRow(selectedRow);
			Object node = path.getLastPathComponent();
			if (node instanceof PluginTreeTableNode) {
				PluginInfo plugin = ((PluginTreeTableNode) node).getUserObject();
				if(plugin.getDescription()!=null)
				{
					JOptionPane.showMessageDialog(null,plugin.getDescription(),"Plug-in info",JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					String  info = "information Not available";
//					JOptionPane.showMessageDialog(null, info , "Information", JOptionPane.INFORMATION_MESSAGE);
					Workbench.getInstance().warn(info);
				}
			}
			else
			{
				String  info = "information Not available";
				JOptionPane.showMessageDialog(null, info , "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void packColumns() {
		this.pluginsTreeTable.packAll();
	}
	
	private synchronized void updateModel() throws NotInitializedException {
		DefaultMutableTreeTableNode root = new InformationTreeTableNode(AddPluginPane.ROOT_LABEL);
		DefaultMutableTreeTableNode node, leaf;		
		for (PluginInfo plugin:PluginManager.getInstance().getRepositoryInfo()) {
			if(!PluginManager.getInstance().pluginExists(plugin.getUID()))
			{
				node = new PluginTreeTableNode(plugin);
				for (DependencyInfo dependency:plugin.getListNeeds()) {
					leaf = new DependencyTreeTableNode(dependency);
					node.add(leaf);
				}
				root.add(node);
			}
		}
		this.treeTableModel = new PluginInformationTreeTableModel(root);
		this.pluginsTreeTable.setTreeTableModel(this.treeTableModel);	
		this.pluginsTreeTable.packAll();
		this.repaint();
	}	
	
	private final class RepositoryHighlighter extends ColorHighlighter {
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.decorator.ColorHighlighter#applyBackground(java.awt.Component, org.jdesktop.swingx.decorator.ComponentAdapter)
		 */
		@Override
		protected void applyBackground(Component renderer, ComponentAdapter adapter) {
			if (!adapter.isSelected()) {				
				Color installable = new Color(0, 128, 128);
				Color updatable = new Color(100, 200, 100);
				Color depInstallable = new Color(0, 128, 128);
				Color depUpdatable = new Color(150, 220, 150);
				Color depLost = new Color(200, 125, 125);
				
				Object row = AddPluginPane.this.pluginsTreeTable.getPathForRow(adapter.row).getLastPathComponent();
				if (row instanceof InformationTreeTableNode) {
					Object value = ((InformationTreeTableNode) row).getUserObject();
					if (value instanceof PluginInfo) {
						PluginInfo info = (PluginInfo) value;
						
						try {
							Plugin plugin = PluginManager.getInstance().getInstalledPlugin(info.getUID());
							if (plugin == null) {
								renderer.setBackground(installable);
							} else if (info.getPluginVersion().compareTo(plugin.getVersion()) > 0) {
								renderer.setBackground(updatable);
							}
						} catch (PluginEngineException pee) {
							AddPluginPane.logger.warn("Error getting PluginInfo version", pee);
						}
					} else if (value instanceof DependencyInfo) {
						DependencyInfo info = (DependencyInfo) value;
						PluginInfo update = PluginManager.getInstance().getDownloadPluginInfo(info.getUid());
						Plugin plugin = PluginManager.getInstance().getInstalledPlugin(info.getUid());
						if (plugin == null && update == null) {
							renderer.setBackground(depLost);
						} else if (plugin == null && update != null) {
							try {
								if (info.getDependencyVersion().compareTo(update.getPluginVersion()) == 0) {
									
									renderer.setBackground(depInstallable);							
								} else {
									renderer.setBackground(depLost);
								}
							} catch (PluginEngineException pee) {
								AddPluginPane.logger.warn("Error getting DependencyInfo version", pee);
							}
						} else if (plugin != null && update == null) {
							if (info.getDependencyVersion().compareTo(plugin.getVersion()) != 0) {
								renderer.setBackground(depLost);
							}
						} else {
							if (info.getDependencyVersion().compareTo(plugin.getVersion()) != 0) {
								try {
									if (info.getDependencyVersion().compareTo(update.getPluginVersion()) == 0) {
										renderer.setBackground(depUpdatable);							
									} else {
										renderer.setBackground(depLost);
									}
								} catch (PluginEngineException pee) {
									AddPluginPane.logger.warn("Error getting DependencyInfo version", pee);
								}
							}
							
						}
					}
				}
			}	
			super.applyBackground(renderer, adapter);
		}
	}
	

	
	private final class PluginInformationTreeTableModel extends DefaultTreeTableModel {
		public PluginInformationTreeTableModel(TreeTableNode root) {
			super(root, AddPluginPane.COLUMN_NAMES);
		}
	}
	
	private class InformationTreeTableNode extends DefaultMutableTreeTableNode {

		/**
		 * @param userObject
		 * @param allowsChildren
		 */
		public InformationTreeTableNode(Object userObject,
				boolean allowsChildren) {
			super(userObject, allowsChildren);
		}

		/**
		 * @param userObject
		 */
		public InformationTreeTableNode(Object userObject) {
			super(userObject);
		}
		
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode#isEditable(int)
		 */
		@Override
		public boolean isEditable(int column) {
			return false;
		}
		
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			return AddPluginPane.COLUMN_NAMES.size();
		}
		
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode#getValueAt(int)
		 */
		@Override
		public Object getValueAt(int column) {
			return (column==0)?this.getUserObject():"";
		}
	}
	
	private class PluginTreeTableNode extends InformationTreeTableNode {
		private final PluginInfo plugin;
		
		public PluginTreeTableNode(PluginInfo plugin) {
			super(plugin, true);
			this.plugin = plugin;
		}
		
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode#getUserObject()
		 */
		@Override
		public PluginInfo getUserObject() {
			return this.plugin;
		}
		
		/* (non-Javadoc)
		 * @see es.uvigo.ei.sing.aibench.pluginmanager.gui.RepositoryInformationPane.InformationTreeTableNode#getValueAt(int)
		 */
		@Override
		public Object getValueAt(int column) {
			Plugin plugin = PluginManager.getInstance().getInstalledPlugin(this.plugin.getUID());
			switch(column) {
			case 0:
				return this.plugin.getName();
			case 1:
				return (plugin == null)?null:plugin.getVersion().toString();
			case 2:
				try {
					return this.plugin.getPluginVersion().toString();
				} catch (PluginEngineException e) {
					return this.plugin.getVersion();
				}
			case 3:
				return null;
			case 4:
				return this.plugin.getUID();
			}
			return super.getValueAt(column);
		}
	}
	
	private class DependencyTreeTableNode extends InformationTreeTableNode {
		private final DependencyInfo dependency;
		public DependencyTreeTableNode(DependencyInfo dependency) {
			super(dependency, true);
			this.dependency = dependency;
		}
		
		/* (non-Javadoc)
		 * @see org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode#getUserObject()
		 */
		@Override
		public DependencyInfo getUserObject() {
			return this.dependency;
		}
		
		/* (non-Javadoc)
		 * @see es.uvigo.ei.sing.aibench.pluginmanager.gui.RepositoryInformationPane.InformationTreeTableNode#getValueAt(int)
		 */
		@Override
		public Object getValueAt(int column) {
			Plugin plugin = PluginManager.getInstance().getInstalledPlugin(this.dependency.getUid());
			PluginInfo infoPlugin = PluginManager.getInstance().getDownloadPluginInfo(this.dependency.getUid());
			switch(column) {
			case 0:
				return infoPlugin.getName();
			case 1:
				return (plugin == null)?null:plugin.getVersion();
			case 2:
				try {
					return (infoPlugin == null)?null:infoPlugin.getPluginVersion();
				} catch (PluginEngineException e) {
					AddPluginPane.logger.warn("Error getting PluginInfo version.", e);
					return null;
				}
			case 3:
				return this.dependency.getDependencyVersion();
			case 4:
				return null;
			}
			return super.getValueAt(column);
		}
	}

	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.workbench.InputGUI#init(es.uvigo.ei.aibench.workbench.ParamsReceiver, es.uvigo.ei.aibench.core.operation.OperationDefinition)
	 */
	public void init(ParamsReceiver receiver, OperationDefinition<?> operation) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.workbench.InputGUI#onValidationError(java.lang.Throwable)
	 */
	public void onValidationError(Throwable t) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see es.uvigo.ei.aibench.workbench.InputGUI#finish()
	 */
	public void finish() {
		this.setVisible(true);
	}
	

}
