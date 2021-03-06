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
package org.ant4eclipse.lib.jdt.internal.model.jre;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.core.exception.Ant4EclipseException;
import org.ant4eclipse.lib.core.logging.A4ELogging;
import org.ant4eclipse.lib.core.util.ClassLoadingHelper;
import org.ant4eclipse.lib.jdt.JdtExceptionCode;

/**
 * <p>
 * An instance of type {@link JavaExecuter} can be used to execute a java application with a specific java runtime
 * environment. You have to specify the directory that contains the java runtime within the constructor of this class.
 * To set the java class that should be executed you have to call <code>setMainClass(String)</code>. To set class path
 * entries you can use one of the <code>setClasspathEntries()</code>.
 * </p>
 * <p>
 * Example: <code><pre>
 * JavaExecuter javaExecuter = new JavaExecuter(location);
 * javaExecuter.setClasspathEntries(new File(&quot;c:\temp\myjar.jar&quot;));
 * javaExecuter.setMainClass(&quot;com.example.Main&quot;);
 * javaExecuter.execute();
 * </pre></code>
 * </p>
 * <p>
 * It is also possible to create a new instance that is already configured with the ant4eclipse class path.
 * </p>
 * <p>
 * Example: <code><pre>
 * JavaExecuter javaExecuter = JavaExecuter.createWithA4eClasspath(location);
 * javaExecuter.setMainClass(&quot;net.sf.ant4eclipse.model.jdt.jre.internal.support.LibraryDetector&quot;);
 * javaExecuter.execute();
 * </pre></code>
 * </p>
 * 
 * @author Gerd W&uml;therich (gerd@gerd-wuetherich.de)
 */
public class JavaExecuter {

  /** the directory of the java runtime environment */
  private File     _jreDirectory;

  /** the class path entries */
  private File[]   _classpathEntries;

  /** the qualified name of the main class */
  private String   _mainClass;

  /** vm arguments */
  private String[] _vmargs = new String[0];

  /** the program arguments */
  private String[] _args   = new String[0];

  /** the system out result */
  private String[] _systemOut;

  /** the system err result */
  private String[] _systemErr;

  /**
   * <p>
   * Returns a new {@link JavaExecuter} that has the ant4eclipse classes set on the class path.
   * </p>
   * 
   * @param jreLocation
   *          the location of the java runtime
   * 
   * @return a new {@link JavaExecuter}
   */
  public static JavaExecuter createWithA4eClasspath(File jreLocation) {
    Assure.isDirectory("jreLocation", jreLocation);

    // check if the location points to a JDK (instead a JRE)...
    File jreDirectory = new File(jreLocation, "jre");
    if (!jreDirectory.isDirectory()) {
      jreDirectory = jreLocation;
    }

    // create new java launcher
    JavaExecuter javaExecuter = new JavaExecuter(jreDirectory);

    // resolve the class path entries
    String[] classpathentries = ClassLoadingHelper.getClasspathEntriesFor(JavaRuntimeImpl.class);

    // TODO
    // patch for the usage with clover instrumented classes...
    String ant4eclipseCloverPath = System.getProperty("clover.path");
    if (ant4eclipseCloverPath != null) {
      // need more ram
      javaExecuter.setVmargs(new String[] { "-Xmx128m" });
      String[] ant4eclipseCloverPathEntries = ant4eclipseCloverPath.split(File.pathSeparator);
      String[] finalEntries = new String[ant4eclipseCloverPathEntries.length + classpathentries.length];
      System.arraycopy(ant4eclipseCloverPathEntries, 0, finalEntries, 0, ant4eclipseCloverPathEntries.length);
      System.arraycopy(classpathentries, 0, finalEntries, ant4eclipseCloverPathEntries.length, classpathentries.length);
      classpathentries = finalEntries;
    }

    javaExecuter.setClasspathEntries(classpathentries);

    // return result
    return javaExecuter;
  }

  /**
   * <p>
   * Creates a new instance of type {@link JavaExecuter}.
   * </p>
   * 
   * @param jreDirectory
   *          the directory of the java runtime.
   */
  public JavaExecuter(File jreDirectory) {
    Assure.isDirectory("jreDirectory", jreDirectory);
    this._jreDirectory = jreDirectory;
  }

  /**
   * <p>
   * Sets the specified class path entries.
   * </p>
   * 
   * @param classpathEntries
   *          the class path entries
   */
  public void setClasspathEntries(String[] classpathEntries) {
    Assure.notNull("classpathEntries", classpathEntries);

    // create file array
    File[] files = new File[classpathEntries.length];
    for (int i = 0; i < classpathEntries.length; i++) {
      files[i] = new File(classpathEntries[i]);
    }

    // sets the class path entries
    setClasspathEntries(files);
  }

  /**
   * <p>
   * Sets the specified class path entry.
   * </p>
   * 
   * @param classpathEntry
   *          the class path entry
   */
  public void setClasspathEntries(File classpathEntry) {
    Assure.notNull("classpathEntry", classpathEntry);
    setClasspathEntries(new File[] { classpathEntry });
  }

  /**
   * <p>
   * Sets the specified class path entries.
   * </p>
   * 
   * @param classpathEntries
   *          the class path entries
   */
  public void setClasspathEntries(File[] classpathEntries) {
    Assure.notNull("classpathEntries", classpathEntries);
    this._classpathEntries = classpathEntries;
  }

  /**
   * <p>
   * Specifies the main class that should be executed.
   * </p>
   * 
   * @param mainClass
   *          the main class
   */
  public void setMainClass(String mainClass) {
    Assure.notNull("mainClass", mainClass);
    this._mainClass = mainClass;
  }

  /**
   * <p>
   * Specifies the program arguments.
   * </p>
   * 
   * @param args
   *          the program arguments.
   */
  public void setArgs(String[] args) {
    Assure.notNull("args", args);

    this._args = args;
  }

  /**
   * @param vmargs
   *          the vmargs to set
   */
  public void setVmargs(String[] vmargs) {
    Assure.notNull("vmargs", vmargs);
    this._vmargs = vmargs;
  }

  /**
   * @throws IOException
   */
  public void execute() {

    // get runtime
    Runtime runtime = Runtime.getRuntime();

    // create class path
    StringBuffer classpathBuffer = new StringBuffer();
    for (int i = 0; i < this._classpathEntries.length; i++) {
      File file = this._classpathEntries[i];
      classpathBuffer.append(file.getAbsolutePath());
      if (i + 1 < this._classpathEntries.length) {
        classpathBuffer.append(File.pathSeparatorChar);
      }
    }
    String classPath = classpathBuffer.toString();
    boolean classPathContainsBlanks = classPath.contains(" ");

    // create java command
    StringBuffer cmd = new StringBuffer();
    cmd.append(getJavaExecutable().getAbsolutePath());

    // add VM arguments
    for (String vmArg : this._vmargs) {
      cmd.append(" ");
      cmd.append(vmArg);
    }

    // add classpath
    cmd.append(" -cp ");
    if (classPathContainsBlanks) {
      cmd.append("\"");
    }
    cmd.append(classPath);
    if (classPathContainsBlanks) {
      cmd.append("\"");
    }

    // add main class
    cmd.append(" ");
    cmd.append(this._mainClass);

    // add program arguments
    for (String _arg : this._args) {
      cmd.append(" ");
      cmd.append(_arg);
    }

    // execute
    try {
      // debug
      A4ELogging.debug("JavaExecuter.execute(): Executing '%s'.", cmd.toString());

      Process proc = runtime.exec(cmd.toString(), new String[] { "JavaHome=" });

      // wait for result
      proc.waitFor();

      // read out and err stream
      this._systemOut = extractFromInputStream(proc.getInputStream());
      this._systemErr = extractFromInputStream(proc.getErrorStream());

      // debug
      A4ELogging.debug("JavaExecuter.execute(): System.out -> '%s'.", Arrays.asList(this._systemOut));
      A4ELogging.debug("JavaExecuter.execute(): System.err -> '%s'.", Arrays.asList(this._systemErr));

      // log error...
      if (this._systemErr != null && this._systemErr.length > 0) {
        // TODO
        throw new RuntimeException("ERROR JAVAEXCUTOR FAILED: " + Arrays.asList(this._systemErr));
      }

    } catch (IOException e) {
      // throw Ant4EclipseException
      throw new Ant4EclipseException(e, JdtExceptionCode.JAVA_LAUNCHER_EXECUTION_EXCEPTION, cmd.toString());
    } catch (InterruptedException e) {
      // throw Ant4EclipseException
      throw new Ant4EclipseException(e, JdtExceptionCode.JAVA_LAUNCHER_EXECUTION_EXCEPTION, cmd.toString());
    }
  }

  /**
   * <p>
   * Returns the output that was written to system out as a string array .
   * </p>
   * 
   * @return the output that was written to system out as a string array .
   */
  public String[] getSystemOut() {
    return this._systemOut;
  }

  /**
   * <p>
   * Returns the output that was written to system err as a string array .
   * </p>
   * 
   * @return the output that was written to system err as a string array .
   */
  public String[] getSystemErr() {
    return this._systemErr;
  }

  /**
   * <p>
   * Returns the java executable. If the java executable could not be resolved, a {@link Ant4EclipseException} with the
   * ExecptionCode {@link JdtModelExceptionCode#INVALID_JRE_DIRECTORY} is thrown.
   * </p>
   * 
   * @return the java executable
   */
  private File getJavaExecutable() {
    // try 'bin/java'
    File result = new File(this._jreDirectory, "bin/java");

    // try 'bin/java.exe'
    if (!result.exists()) {
      result = new File(this._jreDirectory, "bin/java.exe");
    }

    // try 'bin/j9'
    if (!result.exists()) {
      result = new File(this._jreDirectory, "bin/j9");
    }

    // try 'bin/j9.exe'
    if (!result.exists()) {
      result = new File(this._jreDirectory, "bin/j9.exe");
    }

    // throw Ant4EclipseException
    if (!result.exists()) {
      throw new Ant4EclipseException(JdtExceptionCode.INVALID_JRE_DIRECTORY, this._jreDirectory.getAbsolutePath());
    }

    // return result
    return result;
  }

  /**
   * <p>
   * Returns
   * </p>
   * 
   * @param inputstream
   * @return
   * @throws IOException
   */
  private String[] extractFromInputStream(InputStream inputstream) throws IOException {
    InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

    // read the ls output

    List<String> sysOut = new LinkedList<String>();
    String line;
    while ((line = bufferedreader.readLine()) != null) {
      sysOut.add(line);
    }
    return sysOut.toArray(new String[0]);
  }
}
