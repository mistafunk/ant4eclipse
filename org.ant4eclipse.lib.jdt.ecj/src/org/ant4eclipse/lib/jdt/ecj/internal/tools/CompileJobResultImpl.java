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
package org.ant4eclipse.lib.jdt.ecj.internal.tools;

import org.ant4eclipse.lib.jdt.ecj.CompileJobResult;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class CompileJobResultImpl implements CompileJobResult {

  private boolean              _succeeded;

  private CategorizedProblem[] _categorizedProblems;

  private Map<String, File>    _compiledclassfiles;

  /**
   * {@inheritDoc}
   */
  public boolean succeeded() {
    return this._succeeded;
  }

  /**
   * {@inheritDoc}
   */
  public CategorizedProblem[] getCategorizedProblems() {
    return this._categorizedProblems == null ? new CategorizedProblem[0] : this._categorizedProblems;
  }

  public void setSucceeded(boolean succeeded) {
    this._succeeded = succeeded;
  }

  public void setCategorizedProblems(CategorizedProblem[] categorizedProblems) {
    this._categorizedProblems = categorizedProblems;
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, File> getCompiledClassFiles() {
    if (this._compiledclassfiles == null) {
      return Collections.EMPTY_MAP;
    } else {
      return this._compiledclassfiles;
    }
  }

  /**
   * Changes the map which contains the compiled class files.
   * 
   * @param compiledclasses
   *          A map for the class files. Maybe <code>null</code>.
   */
  public void setCompiledClassFiles(Map<String, File> compiledclasses) {
    this._compiledclassfiles = compiledclasses;
  }

}
