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
package org.ant4eclipse.testframework;

import java.net.URL;

/**
 * Common api used to produce project suites dynamically.
 * 
 * @author Daniel Kasmeroglu (Daniel.Kasmeroglu@Kasisoft.net)
 */
public interface ProjectSuiteApi {

  // external libraries, external folders and runtimes

  int KIND_MULTIPLESOURCEFOLDERSPRIMARY   = 1 << 0;

  int KIND_MULTIPLESOURCEFOLDERSSECONDARY = 1 << 1;

  int KIND_INTERNALLIBRARYPRIMARY         = 1 << 2;

  int KIND_INTERNALLIBRARYSECONDARY       = 1 << 3;

  /**
   * Creates a simple project which is practically empty.
   * 
   * @param script
   *          The location of an ANT build script. Maybe <code>null</code>.
   * @param projectsettings
   *          A list of flags used to control the creation of the project.
   * 
   * @return A description of the created project structures. Not <code>null</code>.
   */
  ProjectDescription createEmptyProject(URL script, int projectsettings);

  /**
   * Creates two projects where project one depends from project two.
   * 
   * @param script
   *          The location of an ANT build script. Maybe <code>null</code>.
   * @param projectsettings
   *          A list of flags used to control the creation of the project.
   * 
   * @return A description of the created project structures. Not <code>null</code>.
   */
  ProjectDescription createComplexProject(URL script, int projectsettings);

  /**
   * Creates two projects where both projects depends on each other.
   * 
   * @param script
   *          The location of an ANT build script. Maybe <code>null</code>.
   * @param projectsettings
   *          A list of flags used to control the creation of the project.
   * 
   * @return A description of the created project structures. Not <code>null</code>.
   */
  ProjectDescription createCyclicProject(URL script, int projectsettings);

} /* ENDINTERFACE */
