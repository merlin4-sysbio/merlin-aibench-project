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
 * BooleanCellEditor.java
 *
 * Created inside the SING research group (http://sing.ei.uvigo.es)
 * University of Vigo
 *
 * Created on 31/03/2009
 */
package es.uvigo.ei.sing.aibench.pluginmanager.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;

/**
 * @author Miguel Reboiro Jato
 *
 */
final class BooleanCellEditor extends DefaultCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JCheckBox checkBox;
	public BooleanCellEditor() {
		this(new JCheckBox());
	}
	
	public BooleanCellEditor(JCheckBox checkBox) {
		super(checkBox);
		this.checkBox = checkBox;
		this.checkBox.setHorizontalAlignment(JCheckBox.CENTER);
		this.checkBox.setOpaque(true);
	}
}
