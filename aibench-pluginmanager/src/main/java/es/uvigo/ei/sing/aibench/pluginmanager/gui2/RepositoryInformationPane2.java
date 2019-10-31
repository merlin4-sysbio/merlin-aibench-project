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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
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

import es.uvigo.ei.aibench.repository.NotInitializedException;
import es.uvigo.ei.aibench.repository.info.DependencyInfo;
import es.uvigo.ei.aibench.repository.info.PluginInfo;
import es.uvigo.ei.sing.aibench.pluginmanager.PluginManager;
import es.uvigo.ei.sing.aibench.pluginmanager.gui.PluginActionProvider;

/**
 * @author Miguel Reboiro Jato
 *
 */
public class RepositoryInformationPane2 extends JScrollPane {
	private final static Logger logger = Logger.getLogger(RepositoryInformationPane2.class);
	
	/**
	 * 
	 */
	private static final int DOWNLOAD_INDEX = 1;

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ROOT_LABEL = "Repository";
	
	private static final List<String> COLUMN_NAMES = new ArrayList<String>();
	
	static {
		COLUMN_NAMES.add("Plugin/Dependencies");
		COLUMN_NAMES.add("Download");
	}
	
	private final JXTreeTable pluginsTreeTable = new JXTreeTable();
	private PluginInformationTreeTableModel treeTableModel;
	
	public RepositoryInformationPane2() throws NotInitializedException {
		super();
		this.setViewportView(this.pluginsTreeTable);
		this.updateModel();
		this.pluginsTreeTable.getColumnExt(RepositoryInformationPane2.DOWNLOAD_INDEX).setCellRenderer(
			new DefaultTableRenderer(new PluginActionProvider())
		);
		
		this.pluginsTreeTable.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.pluginsTreeTable.addHighlighter(new RepositoryHighlighter());
		
	}
	
	public void packColumns() {
		this.pluginsTreeTable.packAll();
	}
	
	private synchronized void updateModel() throws NotInitializedException {
		DefaultMutableTreeTableNode root = new InformationTreeTableNode(RepositoryInformationPane2.ROOT_LABEL);
		DefaultMutableTreeTableNode node, leaf;
		
		for (PluginInfo plugin:PluginManager.getInstance().getRepositoryInfo()) {
			node = new PluginTreeTableNode(plugin);
			for (DependencyInfo dependency:plugin.getListNeeds()) {
				leaf = new DependencyTreeTableNode(dependency);
				node.add(leaf);
			}
			
			root.add(node);
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
				Color installable = new Color(100, 100, 200);
				Color updatable = new Color(100, 200, 100);
				Color depInstallable = new Color(150, 150, 220);
				Color depUpdatable = new Color(150, 220, 150);
				Color depLost = new Color(200, 125, 125);
				
				Object row = RepositoryInformationPane2.this.pluginsTreeTable.getPathForRow(adapter.row).getLastPathComponent();
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
							RepositoryInformationPane2.logger.warn("Error getting PluginInfo version", pee);
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
								RepositoryInformationPane2.logger.warn("Error getting DependencyInfo version", pee);
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
									RepositoryInformationPane2.logger.warn("Error getting DependencyInfo version", pee);
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
			super(root, RepositoryInformationPane2.COLUMN_NAMES);
		}
	}
	
	private class InformationTreeTableNode extends DefaultMutableTreeTableNode {
		/**
		 * 
		 */
		public InformationTreeTableNode() {
			super();
		}

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
			return RepositoryInformationPane2.COLUMN_NAMES.size();
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
				return this.plugin.getUID();
			
			case 2:
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
				return this.dependency.getUid();
			case 1:
				return dependency.getUid();
			}
			return super.getValueAt(column);
		}
	}
}

