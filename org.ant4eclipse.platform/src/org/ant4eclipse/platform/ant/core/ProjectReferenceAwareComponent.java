package org.ant4eclipse.platform.ant.core;

import org.ant4eclipse.platform.tools.ReferencedProjectsResolver;

import org.apache.tools.ant.BuildException;

/**
 * <p>
 * Interface for all tasks, conditions and types that have to deal with project references. In ant4eclipse, types of
 * project references are plugable via {@link ReferencedProjectsResolver ReferencedProjectsResolvers}. E.g. a JDT
 * defines a project references if the underlying class path contains an entry that points to that project. This
 * interface allows you to set the reference types a specific task is interested in.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface ProjectReferenceAwareComponent {

  /**
   * <p>
   * Sets the list of reference type (e.g. 'jdt, platform').
   * </p>
   * 
   * @param referenceTypes
   *          a comma separated list of reference type (e.g. 'jdt, platform').
   */
  void setProjectReferenceTypes(String referenceTypes);

  /**
   * <p>
   * Returns the set reference types.
   * </p>
   * 
   * @return the set reference types.
   */
  String[] getProjectReferenceTypes();

  /**
   * <p>
   * Return <code>true</code>, if the project reference types are set.
   * </p>
   * 
   * @return <code>true</code>, if the project reference types are set.
   */
  boolean isProjectReferenceTypesSet();

  /**
   * <p>
   * Throws an {@link BuildException} if the project reference types are not set.
   * </p>
   */
  void requireProjectReferenceTypesSet();
}
