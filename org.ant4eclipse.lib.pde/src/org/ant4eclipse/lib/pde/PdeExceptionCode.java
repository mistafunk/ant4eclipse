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
package org.ant4eclipse.lib.pde;

import org.ant4eclipse.lib.core.exception.ExceptionCode;
import org.ant4eclipse.lib.core.nls.NLS;
import org.ant4eclipse.lib.core.nls.NLSMessage;

/**
 * <p>
 * ExceptionCodes for JDT tools.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class PdeExceptionCode extends ExceptionCode {

  /** - */
  @NLSMessage("The launcher bundle '%s' does not exist in the target platform.")
  public static ExceptionCode    LAUNCHER_BUNDLE_DOES_NOT_EXIST = null;

  /** - */
  @NLSMessage("Unknown platform configuration '%s'.")
  public static ExceptionCode    UKNOWN_PLATFORM_CONFIGURATION  = null;

  /** - */
  @NLSMessage("\n%s")
  public static ExceptionCode    BUNDLE_NOT_RESOLVED_EXCEPTION;

  /** - */
  @NLSMessage("Invalid version '%s'.")
  public static ExceptionCode    INVALID_VERSION;

  /** - */
  @NLSMessage("You have to specify attribute '%s' if  attribute '%s' is set.")
  public static ExceptionCode    ANT_ATTRIBUTE_X_WITHOUT_ATTRIBUTE_Y;

  /** - */
  @NLSMessage("You have to specify either attribute '%s' or the nested '%s'. You can't specify both.")
  public static ExceptionCode    ANT_ATTRIBUTE_X_OR_ELEMENT_Y;

  /** - */
  @NLSMessage("The specified bundle with the symbolic name '%s' and the version '%s' doesn't exist.")
  public static ExceptionCode    SPECIFIED_BUNDLE_NOT_FOUND;

  /** - */
  @NLSMessage("Referenced target platform definition with id '%s' doesn't exist.")
  public static ExceptionCode    NOT_TARGET_PLATFORM_DEFINITION;

  /** - */
  @NLSMessage("Library '%s' doesn't exist in project '%s'.")
  public static PdeExceptionCode LIBRARY_NAME_DOES_NOT_EXIST;

  /** - */
  @NLSMessage("File '%s' doesn't contain a bundle manifest file and will be ignored.")
  public static PdeExceptionCode WARNING_FILE_DOES_NOT_CONTAIN_BUNDLE_MANIFEST_FILE;

  /** - */
  @NLSMessage("File '%s' doesn't contain a feature manifest file and will be ignored.")
  public static PdeExceptionCode WARNING_FILE_DOES_NOT_CONTAIN_FEATURE_MANIFEST_FILE;

  /** the ant attribute is not set */
  @NLSMessage("Attribute '%s' is not set.")
  public static PdeExceptionCode ANT_ATTRIBUTE_NOT_SET;

  /** - */
  @NLSMessage("You have to specify either attribute '%s' or attribute '%s'.")
  public static PdeExceptionCode ANT_ATTRIBUTE_X_OR_Y;

  /** - */
  @NLSMessage("Could not find the feature manifest file for project '%s'.")
  public static PdeExceptionCode FEATURE_MANIFEST_FILE_NOT_FOUND;

  /** - */
  @NLSMessage("No target platform has been set. Please specify a target platform by using the <jdtclasspathcontainerargument>-subelement with 'target.platform' as key")
  public static PdeExceptionCode NO_TARGET_PLATFORM_SET;

  /** - */
  @NLSMessage("The product definition '%s' is invalid. Reason: %s")
  public static PdeExceptionCode INVALID_PRODUCT_DEFINITION;

  /** - */
  @NLSMessage("The value '%s' for %s is not allowed.")
  public static PdeExceptionCode INVALID_CONFIGURATION_VALUE;

  static {
    NLS.initialize(PdeExceptionCode.class);
  }

  /**
   * <p>
   * Creates a new instance of type {@link PdeExceptionCode}.
   * </p>
   * 
   * @param message
   *          the message of the {@link PdeExceptionCode}
   */
  private PdeExceptionCode(String message) {
    super(message);
  }
}
