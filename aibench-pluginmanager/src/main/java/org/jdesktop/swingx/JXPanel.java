/*
 * #%L
 * The AIBench Plugin Manager Plugin
 * %%
 * Copyright (C) 2006 - 2017 Daniel Glez-Peña and Florentino Fdez-Riverola
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
/*
 * $Id: JXPanel.java,v 1.1 2009-04-13 22:17:51 mrjato Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Scrollable;
import javax.swing.plaf.UIResource;

import org.jdesktop.swingx.painter.Painter;

/**
 * A simple JPanel extension that adds translucency support.
 * This component and all of its content will be displayed with the specified
 * &quot;alpha&quot; transluscency property value. It also supports the
 * Painters using the backgroundPainter property.  For example, to change the background of the
 * panel to a checkeboard do something like this:
 * 
 * 
 * <PRE> <CODE>JXPanel panel = new JXPanel();
 * panel.setBackgroundPainter(new CheckerboardPainter());</CODE></PRE>
 * @author rbair
 */
public class JXPanel extends JPanel implements Scrollable {
    private boolean scrollableTracksViewportHeight = true;
    private boolean scrollableTracksViewportWidth = true;
    
    /**
     * The alpha level for this component.
     */
    private float alpha = 1.0f;
    /**
     * If the old alpha value was 1.0, I keep track of the opaque setting because
     * a translucent component is not opaque, but I want to be able to restore
     * opacity to its default setting if the alpha is 1.0. Honestly, I don't know
     * if this is necessary or not, but it sounded good on paper :)
     * <p>TODO: Check whether this variable is necessary or not</p>
     */
    private boolean oldOpaque;
    /**
     * Indicates whether this component should inherit its parent alpha value
     */
    private boolean inheritAlpha = true;
    /**
     * Specifies the Painter to use for painting the background of this panel.
     * If no painter is specified, the normal painting routine for JPanel
     * is called. Old behavior is also honored for the time being if no
     * backgroundPainter is specified
     */
    private Painter backgroundPainter;
    
    /**
     * Creates a new instance of JXPanel
     */
    public JXPanel() {
    }
    
    /**
     * @param isDoubleBuffered
     */
    public JXPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    
    /**
     * @param layout
     */
    public JXPanel(LayoutManager layout) {
        super(layout);
    }
    
    /**
     * @param layout
     * @param isDoubleBuffered
     */
    public JXPanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    
    /**
     * Set the alpha transparency level for this component. This automatically
     * causes a repaint of the component.
     *
     * <p>TODO add support for animated changes in translucency</p>
     *
     * @param alpha must be a value between 0 and 1 inclusive.
     */
    public void setAlpha(float alpha) {
        if (this.alpha != alpha) {
            assert alpha >= 0 && alpha <= 1.0;
            float oldAlpha = this.alpha;
            this.alpha = alpha;
            if (alpha > 0f && alpha < 1f) {
                if (oldAlpha == 1) {
                    //it used to be 1, but now is not. Save the oldOpaque
                    oldOpaque = isOpaque();
                    setOpaque(false);
                }
                
                RepaintManager manager = RepaintManager.currentManager(this);
                RepaintManager trm = SwingXUtilities.getTranslucentRepaintManager(manager);
                RepaintManager.setCurrentManager(trm);
            } else if (alpha == 1) {
                //restore the oldOpaque if it was true (since opaque is false now)
                if (oldOpaque) {
                    setOpaque(true);
                }
            }
            firePropertyChange("alpha", oldAlpha, alpha);
            repaint();
        }
    }
    
    /**
     * @return the alpha translucency level for this component. This will be
     * a value between 0 and 1, inclusive.
     */
    public float getAlpha() {
        return alpha;
    }
    
    /**
     * Unlike other properties, alpha can be set on a component, or on one of
     * its parents. If the alpha of a parent component is .4, and the alpha on
     * this component is .5, effectively the alpha for this component is .4
     * because the lowest alpha in the heirarchy &quot;wins&quot;
     */
    public float getEffectiveAlpha() {
        if (inheritAlpha) {
            float a = alpha;
            Component c = this;
            while ((c = c.getParent()) != null) {
                if (c instanceof JXPanel) {
                    a = Math.min(((JXPanel)c).getAlpha(), a);
                }
            }
            return a;
        } else {
            return alpha;
        }
    }
    
    public boolean isInheritAlpha() {
        return inheritAlpha;
    }
    
    public void setInheritAlpha(boolean val) {
        if (inheritAlpha != val) {
            inheritAlpha = val;
            firePropertyChange("inheritAlpha", !inheritAlpha, inheritAlpha);
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight() {
        return scrollableTracksViewportHeight;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth() {
        return scrollableTracksViewportWidth;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    /**
     * @param scrollableTracksViewportHeight The scrollableTracksViewportHeight to set.
     */
    public void setScrollableTracksViewportHeight(boolean scrollableTracksViewportHeight) {
        this.scrollableTracksViewportHeight = scrollableTracksViewportHeight;
    }
    /**
     * @param scrollableTracksViewportWidth The scrollableTracksViewportWidth to set.
     */
    public void setScrollableTracksViewportWidth(boolean scrollableTracksViewportWidth) {
        this.scrollableTracksViewportWidth = scrollableTracksViewportWidth;
    }

    /**
     * Sets the background color for this component by
     * 
     * @param bg
     *            the desired background <code>Color</code>
     * @see java.swing.JComponent#getBackground
     * @see #setOpaque
     * 
    * @beaninfo
    *    preferred: true
    *        bound: true
    *    attribute: visualUpdate true
    *  description: The background color of the component.
     */
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        
        //TODO problem with SwingX #964.  Had to undo changes.
        //Change causes background to be painter when using setOpaque=false hack
//        if (canInstallBackgroundUIResourceAsPainter(bg)) {
//            setBackgroundPainter(new PainterUIResource(new MattePainter<JXPanel>(bg)));
//        } else {
//            setBackgroundPainter(new MattePainter<JXPanel>(bg));
//        }
    }

    private boolean canInstallBackgroundUIResourceAsPainter(Color bg) {
        Painter<?> p = getBackgroundPainter();
        
        return bg instanceof UIResource && (p == null || p instanceof UIResource);
    }
    
    /**
     * Sets a Painter to use to paint the background of this JXPanel.
     * 
     * @param p the new painter
     * @see #getBackgroundPainter()
     */
    public void setBackgroundPainter(Painter p) {
        Painter old = getBackgroundPainter();
        backgroundPainter = p;
        firePropertyChange("backgroundPainter", old, getBackgroundPainter());
        repaint();
    }
    
    /**
     * Returns the current background painter. The default value of this property 
     * is a painter which draws the normal JPanel background according to the current look and feel.
     * @return the current painter
     * @see #setBackgroundPainter(Painter)
     * @see #isPaintBorderInsets()
     */
    public Painter getBackgroundPainter() {
        return backgroundPainter;
    }
    
    
    private boolean paintBorderInsets = true;
    
    /**
     * Returns true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is 
     * true by default. This property affects the width, height,
     * and intial transform passed to the background painter.
     */
    public boolean isPaintBorderInsets() {
        return paintBorderInsets;
    }
    
    /**
     * Sets the paintBorderInsets property.
     * Set to true if the background painter should paint where the border is
     * or false if it should only paint inside the border. This property is true by default.
     * This property affects the width, height,
     * and intial transform passed to the background painter.
     * 
     * This is a bound property.
     */
    public void setPaintBorderInsets(boolean paintBorderInsets) {
        boolean old = this.isPaintBorderInsets();
        this.paintBorderInsets = paintBorderInsets;
        firePropertyChange("paintBorderInsets", old, isPaintBorderInsets());
    }
    
    /**
     * Overriden paint method to take into account the alpha setting
     * @param g 
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Composite oldComp = g2d.getComposite();
        float alpha = getEffectiveAlpha();
        Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComp);
        super.paint(g2d);
        g2d.setComposite(oldComp);
    }
    
    /**
     * Overridden to provide Painter support. It will call backgroundPainter.paint()
     * if it is not null, else it will call super.paintComponent().
     */
    @Override
    protected void paintComponent(Graphics g) {
        if(backgroundPainter != null) {
            if (isOpaque()) super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D)g.create();
            
            try {
                // account for the insets
                if(isPaintBorderInsets()) {
                    backgroundPainter.paint(g2, this, this.getWidth(), this.getHeight());
                } else {
                    Insets ins = this.getInsets();
                    g2.translate(ins.left, ins.top);            
                    backgroundPainter.paint(g2, this,
                                            this.getWidth() - ins.left - ins.right,
                                            this.getHeight() - ins.top - ins.bottom);
                }
            } finally {
                g2.dispose();
            }
        } else {
            super.paintComponent(g);
        }
    }
    
}
