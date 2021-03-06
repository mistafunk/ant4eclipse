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
package org.ant4eclipse.ant.jdt;

import org.ant4eclipse.platform.test.AbstractWorkspaceBasedBuildFileTest;

import org.ant4eclipse.testframework.JdtProjectBuilder;

public class HasNatureTest extends AbstractWorkspaceBasedBuildFileTest {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    setupBuildFile("hasNature.xml");
    JdtProjectBuilder builder = new JdtProjectBuilder("jdtproject");
    builder.createIn(getTestWorkspaceDirectory());
  }

  public void testJdtNatureLong() {
    expectLog("testJdtNatureLong", "OK");
  }

  public void testJdtNatureShort() {
    expectLog("testJdtNatureShort", "OK");
  }

}
