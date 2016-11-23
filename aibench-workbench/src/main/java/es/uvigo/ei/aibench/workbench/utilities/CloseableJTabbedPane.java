/*
 * #%L
 * The AIBench Workbench Plugin
 * %%
 * Copyright (C) 2006 - 2016 Daniel Glez-Peña and Florentino Fdez-Riverola
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package es.uvigo.ei.aibench.workbench.utilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import es.uvigo.ei.aibench.workbench.Workbench;

/**
 * @author Miguel Reboiro-Jato
 *
 */
public class CloseableJTabbedPane extends JTabbedPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final ImageIcon CLOSE_ICON = 
		new ImageIcon(CloseableJTabbedPane.class.getResource("images/tabclose.png"));
	private static final Border CLOSE_PANEL_BORDER = 
		BorderFactory.createEmptyBorder(4, 0, 0, 0);
	private static final Border CLOSE_BUTTON_BORDER = 
		BorderFactory.createEmptyBorder(0, 10, 0, 0);
	
	private final Icon closeIcon;

	public CloseableJTabbedPane() {
		this(CloseableJTabbedPane.CLOSE_ICON);
	}
	
	public CloseableJTabbedPane(int tabPlacement) {
		this(CloseableJTabbedPane.CLOSE_ICON, tabPlacement);
	}
	
	public CloseableJTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		this(CloseableJTabbedPane.CLOSE_ICON, tabPlacement, tabLayoutPolicy);
	}
	
	public CloseableJTabbedPane(Icon closeIcon) {
		super();
		this.closeIcon = closeIcon;
	}

	public CloseableJTabbedPane(Icon closeIcon, int tabPlacement) {
		super(tabPlacement);
		this.closeIcon = closeIcon;
	}
	
	public CloseableJTabbedPane(Icon closeIcon, int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		this.closeIcon = closeIcon;
	}
	
	

	public void addTabCloseListener(TabCloseListener listener) {
		this.listenerList.add(TabCloseListener.class, listener);
	}
	
	public void removeTabCloseListener(TabCloseListener listener) {
		this.listenerList.remove(TabCloseListener.class, listener);
	}
	
	public void fireTabClosingEvent(int index) {
		final TabCloseEvent event = new TabCloseEvent(this, TabCloseEvent.TAB_CLOSING, index);
		for (TabCloseListener listener : this.listenerList.getListeners(TabCloseListener.class)) {
			listener.tabClosing(event);
		}
		
		if (!event.isCancelled()) {
			this.remove(index);
		}
	}
	
	public void fireTabCloseEvent(int index) {
		final TabCloseEvent event = new TabCloseEvent(this, TabCloseEvent.TAB_CLOSED, index);
		for (TabCloseListener listener : this.listenerList.getListeners(TabCloseListener.class)) {
			listener.tabClosed(event);
		}
	}

	public void addCloseableTab(String title, Component component) {
		final Component tabComponent = this.add(title, component);
		final int index = this.indexOfTabComponent(tabComponent);
		
		if (index != -1) {
			this.setTabComponentAt(index, null);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTabbedPane#addTab(java.lang.String, java.awt.Component)
	 */
	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		final int index = this.indexOfComponent(component);
		if (index != -1) {
			this.setTabComponentAt(index, new CloseTab());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTabbedPane#addTab(java.lang.String, javax.swing.Icon, java.awt.Component)
	 */
	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, component);
		final int index = this.indexOfComponent(component);
		if (index != -1) {
			this.setTabComponentAt(index, new CloseTab());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTabbedPane#addTab(java.lang.String, javax.swing.Icon, java.awt.Component, java.lang.String)
	 */
	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		final int index = this.indexOfComponent(component);
		if (index != -1) {
			this.setTabComponentAt(index, new CloseTab());
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JTabbedPane#removeTabAt(int)
	 */
	@Override
	public void removeTabAt(int index) {
		super.removeTabAt(index);
		this.fireTabCloseEvent(index);
	}
	
	protected class CloseTab extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CloseTab() {
			super(new BorderLayout());
			
			this.setBorder(CloseableJTabbedPane.CLOSE_PANEL_BORDER);
			this.setOpaque(false);
			
			final JLabel label = new JLabel() {
				private static final long serialVersionUID = 1L;

				@Override
				public Icon getIcon() {
					final int i = CloseableJTabbedPane.this.indexOfTabComponent(CloseTab.this);
					
					return (i == -1)?null:CloseableJTabbedPane.this.getIconAt(i);
				}
				
				@Override
				public String getText() {
					final int i = CloseableJTabbedPane.this.indexOfTabComponent(CloseTab.this);
					
					return (i == -1)?null:CloseableJTabbedPane.this.getTitleAt(i);
				}
			};
			label.setVerticalTextPosition(SwingConstants.CENTER);
			
			this.add(label, BorderLayout.CENTER);
			this.add(new CloseButton(), BorderLayout.EAST);
		}
		
		private class CloseButton extends JButton {
			private static final long serialVersionUID = 1L;

			public CloseButton() {
				super(CloseableJTabbedPane.this.closeIcon);
				
				this.setToolTipText("Close tab");
				this.setContentAreaFilled(false);
				this.setBorderPainted(false);
				this.setBorder(CloseableJTabbedPane.CLOSE_BUTTON_BORDER);
				this.setCursor(new Cursor(Cursor.HAND_CURSOR));
				
				this.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeCurrentButtonTab();
					}
				});
				if(mustShowPopupMenu())
					this.setComponentPopupMenu(new JPopupMenu());
			}
	
			private boolean mustShowPopupMenu() {
				return Workbench.CONFIG.getProperty("documentviewer.show_close_tabs_menu")!=null 
						&& Workbench.CONFIG.getProperty("documentviewer.show_close_tabs_menu").equals("true");
			}

			public JPopupMenu getComponentPopupMenu() {
				if(mustShowPopupMenu()) {
			    	JPopupMenu toret = new JPopupMenu();
			    	toret.add(new AbstractAction("Close all tabs") {
						private static final long serialVersionUID = 1L;
			
						@Override
						public void actionPerformed(ActionEvent e) {
							closeAllTabs();
						}
					});
			    	toret.add(new AbstractAction("Close this tab") {
			    		private static final long serialVersionUID = 1L;
			    		
			    		@Override
			    		public void actionPerformed(ActionEvent e) {
			    			closeCurrentButtonTab();
			    		}
			    	});
			    	toret.add(new AbstractAction("Close other tabs") {
			    		private static final long serialVersionUID = 1L;
			    		
			    		@Override
			    		public void actionPerformed(ActionEvent e) {
			    			closeOtherTabs();
			    		}
			    		
			    		@Override
			    		public boolean isEnabled() {
			    			return CloseableJTabbedPane.this.getTabCount() > 1;
			    		}
			    	});
					return toret;
				} else {
					return super.getComponentPopupMenu();
				}
			};
			
		}
		
		private void closeAllTabs() {
			for(int toRemove = CloseableJTabbedPane.this.getTabCount()-1; toRemove >= 0; toRemove-- ) {
				CloseableJTabbedPane.this.fireTabClosingEvent(toRemove);
			}
		}
		
		private void closeCurrentButtonTab() {
			final int index = CloseableJTabbedPane.this.indexOfTabComponent(CloseTab.this);
			CloseableJTabbedPane.this.fireTabClosingEvent(index);
		}
		
		private void closeOtherTabs() {
			final int index = CloseableJTabbedPane.this.indexOfTabComponent(CloseTab.this);
			for(int toRemove = CloseableJTabbedPane.this.getTabCount()-1; toRemove >= 0; toRemove-- ) {
				if(toRemove != index) {
					CloseableJTabbedPane.this.fireTabClosingEvent(toRemove);
				}
			}
		}
	}
}
