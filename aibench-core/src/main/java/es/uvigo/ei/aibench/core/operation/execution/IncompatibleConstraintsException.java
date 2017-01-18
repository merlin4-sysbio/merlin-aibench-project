/*
 * #%L
 * The AIBench Core Plugin
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
package es.uvigo.ei.aibench.core.operation.execution;

/**
 * @author Daniel Glez-Peña 11-sep-2006
 *
 */
public class IncompatibleConstraintsException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new IncompatibleConstraintsException.
	 * 
	 * @param message the exception message.
	 */
	public IncompatibleConstraintsException(String message) {
		super(message);
	}

}