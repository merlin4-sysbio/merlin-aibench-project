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
 * $Id: PipelineEvent.java,v 1.1 2009-04-13 22:17:53 mrjato Exp $
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

package org.jdesktop.swingx.decorator;

import java.util.EventObject;


/**
 * Defines an event that encapsulates changes to a pipeline.
 *
 * @author Ramesh Gupta
 */
public class PipelineEvent extends EventObject
{
    /** Identifies one or more changes in the pipeline. */
    public static final int CONTENTS_CHANGED = 0;

    /** Identifies a order change of the interactive sorter. */
    public static final int SORT_ORDER_CHANGED = 1;
    private int type;

    /**
     * Returns the event type. The possible values are:
     * <ul>
     * <li> {@link #CONTENTS_CHANGED}
     * <li> {@link #SORT_ORDER_CHANGED  }
     * </ul>
     *
     * @return an int representing the type value
     */
    public int getType() { return type; }

    /**
     * Constructs a PipelineEvent object.
     *
     * @param source  the source Object (typically <code>this</code>)
     * @param type    an int specifying the event type
     */
    public PipelineEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    /**
     * Returns a string representation of this event. This method
     * is intended to be used only for debugging purposes, and the
     * content and format of the returned string may vary between
     * implementations. The returned string may be empty but may not
     * be <code>null</code>.
     *
     * @return  a string representation of this event.
     */
    @Override
    public String toString() {
        return getClass().getName() + "[type=" + type + "]";
    }
}



