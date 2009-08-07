/**********************************************************************
 * Copyright (c) 2005-2008 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.platform.ant.core;

import org.ant4eclipse.platform.model.resource.Workspace;

import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * <p>
 * Interface for ant4eclipse tasks, conditions and types that require a {@link Workspace}.
 * </p>
 * <p>
 * E.g. if an ant task implements this interface, you are able to set a workspace on this task:
 * 
 * <pre>
 * &lt;code&gt;
 * &lt;myTask workspace=&quot;c:/dev/workspace&quot; /&gt;
 * &lt;/code&gt;
 * </pre>
 * 
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface WorkspaceComponent {

  /**
   * <p>
   * Sets the workspace directory.
   * </p>
   * 
   * @param workspace
   *          the workspace directory
   * 
   * @deprecated use {@link WorkspaceComponent#setWorkspaceDirectory(File)} instead. This method is for backward
   *             compatibility only.
   */
  @Deprecated
  void setWorkspace(final File workspace);

  /**
   * <p>
   * Sets the workspace directory.
   * </p>
   * 
   * @param workspace
   *          the workspace directory
   */
  void setWorkspaceDirectory(final File workspaceDirectory);

  /**
   * <p>
   * Returns the workspace directory.
   * </p>
   * 
   * @return the workspace directory.
   */
  File getWorkspaceDirectory();

  /**
   * <p>
   * Returns <code>true</code> if the workspace directory is set, <code>false</code> otherwise.
   * </p>
   * 
   * @return <code>true</code> if the workspace directory is set, <code>false</code> otherwise.
   */
  boolean isWorkspaceDirectorySet();

  /**
   * <p>
   * Throws an {@link BuildException} if the workspace directory has not been set.
   * </p>
   */
  void requireWorkspaceDirectorySet();

  /**
   * <p>
   * Returns the {@link Workspace} instance.
   * </p>
   * 
   * @return the {@link Workspace} instance.
   */
  Workspace getWorkspace();
}