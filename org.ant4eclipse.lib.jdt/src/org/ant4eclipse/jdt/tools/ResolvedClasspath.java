/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.jdt.tools;

import java.io.File;

/**
 * <p>
 * Represents a resolved class path.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface ResolvedClasspath {

  /**
   * <p>
   * Returns the class path entries.
   * </p>
   * 
   * @return the class path entries.
   */
  ResolvedClasspathEntry[] getClasspath();

  /**
   * <p>
   * Convenience method that returns all files contained in the resolved class path entries as a single array.
   * </p>
   * 
   * @return all files contained in the resolved class path entries as a single array.
   */
  File[] getClasspathFiles();

  /**
   * <p>
   * Returns the boot class path entries.
   * </p>
   * 
   * @return the boot class path
   */
  ResolvedClasspathEntry getBootClasspath();

  /**
   * <p>
   * Convenience method that returns all files contained in the resolved boot class path entries as a single array.
   * </p>
   * 
   * @return all files contained in the resolved boot class path entries as a single array.
   */
  File[] getBootClasspathFiles();
}