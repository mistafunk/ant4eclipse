package org.ant4eclipse.jdt.ecj;

import org.ant4eclipse.core.Ant4EclipseConfigurator;
import org.ant4eclipse.core.Assert;
import org.ant4eclipse.core.exception.Ant4EclipseException;
import org.ant4eclipse.core.logging.A4ELogging;
import org.ant4eclipse.core.util.Utilities;

import org.ant4eclipse.jdt.ant.EcjAdditionalCompilerArguments;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Implements a javac compiler adapter for the eclipse compiler for java (ecj). The usage of the ecj has several
 * advantages, e.g. support of access restrictions, multiple source folders.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JDTCompilerAdapter extends DefaultCompilerAdapter {

  private static final String ANT4ECLIPSE_DEFAULT_FILE_ENCODING = "ant4eclipse.default.file.encoding";

  /** - */
  private static final String COMPILE_PROBLEM_MESSAGE           = "----------\n%s. %s in %s (at line %s)\n%s\n%s\n%s\n";

  /** the compiler argument separator */
  private static final String COMPILER_ARGS_SEPARATOR           = "=";

  /** the refid key for the additional compiler arguments */
  private static final String COMPILER_ARGS_REFID_KEY           = "compiler.args.refid";

  /** - */
  private static final String COMPILER_OPTIONS_FILE             = "compiler.options.file";

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings( { "unchecked" })
  public boolean execute() {

    // Step 1: check preconditions
    preconditions();

    // Step 2: Configure ant4eclipse
    Ant4EclipseConfigurator.configureAnt4Eclipse(getProject());

    // Step 3: Fetch compiler arguments
    EcjAdditionalCompilerArguments ecjAdditionalCompilerArguments = fetchEcjAdditionalCompilerArguments();

    // Step 4: Create the EcjAdapter
    final EcjAdapter ejcAdapter = EcjAdapter.Factory.create();

    // Step 5: create CompileJobDescription
    final DefaultCompileJobDescription compileJobDescription = new DefaultCompileJobDescription();
    SourceFile[] sourceFiles = getSourceFilesToCompile(ecjAdditionalCompilerArguments);
    compileJobDescription.setSourceFiles(sourceFiles);
    compileJobDescription.setCompilerOptions(getCompilerOptions());
    compileJobDescription.setClassFileLoader(createClassFileLoader(ecjAdditionalCompilerArguments));

    // Step 6: Compile
    final CompileJobResult compileJobResult = ejcAdapter.compile(compileJobDescription);

    // Step 7: dump result
    CategorizedProblem[] categorizedProblems = compileJobResult.getCategorizedProblems();

    for (int i = 0; i < categorizedProblems.length; i++) {
      CategorizedProblem categorizedProblem = categorizedProblems[i];

      String fileName = new String(categorizedProblem.getOriginatingFileName());
      for (SourceFile sourceFile : sourceFiles) {
        if (fileName.equals(sourceFile.getSourceFileName())) {

          Object[] args = new Object[7];
          args[0] = Integer.valueOf(i + 1);
          args[1] = categorizedProblem.isError() ? "ERROR" : "WARNING";
          args[2] = sourceFile.getSourceFile().getAbsolutePath();
          args[3] = Integer.valueOf(categorizedProblem.getSourceLineNumber());
          String[] problematicLine = readProblematicLine(sourceFile, categorizedProblem);
          args[4] = problematicLine[0];
          args[5] = problematicLine[1];
          args[6] = categorizedProblem.getMessage();
          A4ELogging.error(COMPILE_PROBLEM_MESSAGE, args);
          if (i + 1 == categorizedProblems.length) {
            A4ELogging.error("----------");
          }
        }
      }
    }

    // throw Exception if compilation was not successful
    if (!compileJobResult.succeeded()) {
      throw new Ant4EclipseException(EcjExceptionCodes.COMPILATION_WAS_NOT_SUCCESFUL);
    }

    // Step 8: Return
    return true;
  }

  /**
   * <p>
   * Checks the preconditions of the JDTCompilerAdapter
   * </p>
   * 
   * @throws BuildException
   */
  private void preconditions() throws BuildException {

    // source path is not supported!
    if (getJavac().getSourcepath() != null) {
      // TODO: NLS
      throw new BuildException("getJavac().getSourcepath() != null");
    }
  }

  /**
   * <p>
   * Creates the compiler options for the JDT compiler.
   * </p>
   * <p>
   * The compiler options are defined here:
   * <ul>
   * <li><a href="http://help.eclipse.org/galileo/topic/org.eclipse.jdt.doc.isv/guide/jdt_api_options.htm">JDT Core
   * options</a></li>
   * <li>
   * <a href=
   * "http://help.eclipse.org/galileo/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-compiler.htm"
   * >Java Compiler Preferences </a></li>
   * <li>
   * <a href="http://help.eclipse.org/galileo/topic/org.eclipse.jdt.doc.user/reference/preferences/java/compiler/ref-preferences-errors-warnings.htm"
   * >Java Compiler Errors/Warnings Preferences</a></li>
   * </ul>
   * </p>
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  private Map getCompilerOptions() {

    // Step 1: create result
    CompilerOptions compilerOptions = null;

    // 
    String compilerOptionsFileName = extractJavacCompilerArg(COMPILER_OPTIONS_FILE, null);
    if (compilerOptionsFileName != null) {
      File compilerOptionsFile = new File(compilerOptionsFileName);
      if (compilerOptionsFile.exists() && compilerOptionsFile.isFile()) {
        Map<String, String> compilerOptionsMap = Utilities.readProperties(compilerOptionsFile);
        compilerOptions = new CompilerOptions(compilerOptionsMap);
      }
    }

    // create default
    if (compilerOptions == null) {

      // create compiler options
      compilerOptions = new CompilerOptions();

      // debug
      if (getJavac().getDebug()) {
        compilerOptions.produceDebugAttributes = ClassFileConstants.ATTR_SOURCE | ClassFileConstants.ATTR_LINES
            | ClassFileConstants.ATTR_VARS;
      } else {
        compilerOptions.produceDebugAttributes = 0x0;
      }
      // TODO
      // see: http://help.eclipse.org/galileo/topic/org.eclipse.jdt.doc.isv/guide/jdt_api_options.htm#compatibility

      // get the source option
      compilerOptions.sourceLevel = CompilerOptions.versionToJdkLevel(getJavac().getSource());

      // get the target option
      long targetLevel = CompilerOptions.versionToJdkLevel(getJavac().getTarget());
      compilerOptions.complianceLevel = targetLevel;
      compilerOptions.targetJDK = targetLevel;
    }

    // TODO:
    // A4ELogging.info("Using the following compile options:\n %s", compilerOptions.toString());

    // return the compiler options
    return compilerOptions.getMap();
  }

  /**
   * <p>
   * Returns an array with all the source files to compile.
   * </p>
   * 
   * @param compilerArguments
   *          can be null
   * @return the source files to compile
   */
  private SourceFile[] getSourceFilesToCompile(EcjAdditionalCompilerArguments compilerArguments) {

    // get default destination folder
    File defaultDestinationFolder = getJavac().getDestdir();

    // get the files to compile
    final List<SourceFile> sourceFiles = new LinkedList<SourceFile>();

    // iterate over all the source files and create SourceFile
    for (final File file : getJavac().getFileList()) {

      // get the source folder
      final File sourceFolder = getSourceFolder(file);

      // get the relative source file name
      final String sourceFileName = file.getAbsolutePath().substring(
          sourceFolder.getAbsolutePath().length() + File.separator.length());

      // get the destination folder
      File destinationFolder = compilerArguments != null ? compilerArguments.getOutputFolder(sourceFolder)
          : defaultDestinationFolder;

      // add the new source file
      sourceFiles.add(new SourceFile(sourceFolder, sourceFileName, destinationFolder, getDefaultEncoding()));
    }

    // return the result
    return sourceFiles.toArray(new SourceFile[0]);
  }

  /**
   * <p>
   * Returns the source folder for the given source file.
   * </p>
   * 
   * @param sourceFile
   *          the source file.
   * @return the source folder
   */
  private File getSourceFolder(final File sourceFile) {

    // get the absoult path
    final String absolutePath = sourceFile.getAbsolutePath();

    // get the list of all source directories
    final String[] srcDirs = getJavac().getSrcdir().list();

    // find the 'right' source directory
    for (final String srcDir : srcDirs) {
      if (absolutePath.startsWith(srcDir) && absolutePath.charAt(srcDir.length()) == File.separatorChar) {
        return new File(srcDir);
      }
    }

    // TODO: NLS
    throw new RuntimeException();
  }

  /**
   * <p>
   * Creates class file loader.
   * </p>
   * 
   * @param compilerArguments
   *          the compiler arguments, can be <code>null</code>.
   * @return the class file loader.
   */
  @SuppressWarnings("unchecked")
  private ClassFileLoader createClassFileLoader(final EcjAdditionalCompilerArguments compilerArguments) {

    // Step 1: create class file loader list
    final List<ClassFileLoader> classFileLoaderList = new LinkedList<ClassFileLoader>();

    // Step 2: add boot class loader
    classFileLoaderList.add(createBootClassLoader(compilerArguments));

    // Step 3: add class loader for class path entries
    for (final Iterator iterator = getJavac().getClasspath().iterator(); iterator.hasNext();) {

      // get the file resource
      final FileResource fileResource = (FileResource) iterator.next();

      if (fileResource.getFile().exists()) {

        // TODO: LIBRARY AND PROJECT
        // create class file loader for file resource
        final ClassFileLoader myclassFileLoader = ClassFileLoaderFactory.createClasspathClassFileLoader(fileResource
            .getFile(), EcjAdapter.LIBRARY);

        // create and add FilteringClassFileLoader is necessary
        if (compilerArguments != null && compilerArguments.hasAccessRestrictions(fileResource.getFile())) {
          classFileLoaderList.add(ClassFileLoaderFactory.createFilteringClassFileLoader(myclassFileLoader,
              compilerArguments.getAccessRestrictions(fileResource.getFile())));
        }
        // else add class file loader
        else {
          classFileLoaderList.add(myclassFileLoader);
        }
      }
    }

    // Step 4: return the compound class file loader
    return ClassFileLoaderFactory.createCompoundClassFileLoader(classFileLoaderList.toArray(new ClassFileLoader[0]));
  }

  /**
   * <p>
   * Create a boot class loader.
   * </p>
   * 
   * @param compilerArguments
   *          the compiler arguments , can be <code>null</code>.
   * @return the boot class loader
   */
  @SuppressWarnings("unchecked")
  private ClassFileLoader createBootClassLoader(final EcjAdditionalCompilerArguments compilerArguments) {

    // Step 1: get the boot class path as specified in the javac task
    final Path bootclasspath = getJavac().getBootclasspath();

    // Step 2: create ClassFileLoaders for each entry in the boot class path
    final List<ClassFileLoader> bootClassFileLoaders = new LinkedList<ClassFileLoader>();

    // Step 3: iterate over the boot class path entries as specified in the ant path
    for (final Iterator<FileResource> iterator = bootclasspath.iterator(); iterator.hasNext();) {

      // get the file resource
      final FileResource fileResource = iterator.next();

      // create class file loader
      if (fileResource.getFile().exists()) {
        final ClassFileLoader classFileLoader = ClassFileLoaderFactory.createClasspathClassFileLoader(fileResource
            .getFile(), EcjAdapter.LIBRARY);
        bootClassFileLoaders.add(classFileLoader);
      }
    }

    // Step 4: create compound class file loader
    final ClassFileLoader classFileLoader = ClassFileLoaderFactory.createCompoundClassFileLoader(bootClassFileLoaders
        .toArray(new ClassFileLoader[0]));

    // Step 5: create FilteringClassFileLoader is necessary
    if (compilerArguments != null && compilerArguments.hasBootClassPathAccessRestrictions()) {

      // Step 4: debug
      if (A4ELogging.isDebuggingEnabled()) {
        A4ELogging.debug("Boot class path access restrictions: '%s'", compilerArguments
            .getBootClassPathAccessRestrictions());
      }

      return ClassFileLoaderFactory.createFilteringClassFileLoader(classFileLoader, compilerArguments
          .getBootClassPathAccessRestrictions());
    }
    // else return compound class file loader
    else {
      return classFileLoader;
    }
  }

  /**
   * <p>
   * Helper method that reads the compiler argument with the specified name from the ant's javac task.
   * </p>
   * <p>
   * Compiler arguments can be specified using <code>&lt;compilerarg/&gt;</code> subelement:
   * 
   * <pre>
   * &lt;code&gt; &lt;javac destdir=&quot;${executeJdtProject.default.output.directory}&quot;
   *   debug=&quot;on&quot;
   *   source=&quot;1.5&quot;&gt;
   *   
   *   ...
   * 
   *   &lt;compilerarg value=&quot;compiler.args.refid=executeJdtProject.compiler.args&quot;
   *                compiler=&quot;org.ant4eclipse.jdt.ecj.JDTCompilerAdapter&quot; /&gt;
   * &lt;/javac&gt;
   * &lt;/code&gt;
   * </pre>
   * 
   * </p>
   * 
   * @param argumentName
   * @param defaultValue
   * @return
   */
  private String extractJavacCompilerArg(String argumentName, String defaultValue) {
    Assert.notNull(argumentName);

    // Step 1: Get all compilerArguments
    final String[] currentCompilerArgs = getJavac().getCurrentCompilerArgs();

    // Step 2: Find the 'right' one
    for (final String compilerArg : currentCompilerArgs) {

      // split the argument
      final String[] args = compilerArg.split(COMPILER_ARGS_SEPARATOR);

      // requested one?
      if (args.length > 1 && argumentName.equalsIgnoreCase(args[0])) {

        // return the argument
        return args[1];
      }
    }

    // Step 3: Return defaultValue
    return defaultValue;
  }

  /**
   * <p>
   * Helper method that fetches the {@link EcjAdditionalCompilerArguments} from the underlying ant project. The
   * {@link EcjAdditionalCompilerArguments} are set when a JDT class path is resolved by ant4eclipse.
   * </p>
   * <p>
   * If no {@link EcjAdditionalCompilerArguments} are set, <code>null</code> will be returned.
   * </p>
   * 
   * @return the {@link EcjAdditionalCompilerArguments}
   */
  private EcjAdditionalCompilerArguments fetchEcjAdditionalCompilerArguments() {

    // Step 1: Fetch the CompilerArgument key
    String compilerArgsRefid = extractJavacCompilerArg(COMPILER_ARGS_REFID_KEY, null);

    // Step 2: Return null, if no EcjAdditionalCompilerArguments are set
    if (compilerArgsRefid == null) {
      return null;
    }

    // Step 3: Fetch the compiler arguments
    EcjAdditionalCompilerArguments compilerArguments = (EcjAdditionalCompilerArguments) getProject().getReference(
        compilerArgsRefid);

    // Step 4: Throw exception if null
    if (compilerArguments == null) {
      throw new Ant4EclipseException(EcjExceptionCodes.NO_ECJ_ADDITIONAL_COMPILER_ARGUMENTS_OBJECT, compilerArgsRefid);
    }

    // Step 5: Return the result
    return compilerArguments;
  }

  /**
   * <p>
   * </p>
   * 
   * @param sourceFile
   * @param lineNumber
   * @param sourceStart
   * @param sourceEnd
   * @return
   */
  private String[] readProblematicLine(SourceFile sourceFile, CategorizedProblem categorizedProblem) {
    Assert.notNull(sourceFile);
    Assert.notNull(categorizedProblem);

    int lineNumber = categorizedProblem.getSourceLineNumber();
    int sourceStart = categorizedProblem.getSourceStart();
    int sourceEnd = categorizedProblem.getSourceEnd();

    try {
      // Open the file that is the first
      // command line parameter
      FileInputStream fstream = new FileInputStream(sourceFile.getSourceFile());
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      int lineStart = 0;
      String strLine = "";
      // Read File Line By Line
      for (int i = 0; i < lineNumber; i++) {
        String newLine = br.readLine();

        lineStart = lineStart + strLine.length();
        if (i + 1 != lineNumber) {
          lineStart = lineStart + 1;
        }
        strLine = newLine;
      }
      // Close the input stream
      in.close();
      StringBuilder underscoreLine = new StringBuilder();
      for (int i = lineStart; i < sourceStart; i++) {
        underscoreLine.append(' ');
      }
      for (int i = sourceStart; i <= sourceEnd; i++) {
        underscoreLine.append('^');
      }
      return new String[] { strLine, underscoreLine.toString() };
    } catch (Exception e) {// Catch exception if any
      return new String[] { "", "" };
    }
  }

  /**
   * <p>
   * Helper method. Returns the default encoding of the eclipse workspace.
   * </p>
   * 
   * @return the default encoding
   */
  private String getDefaultEncoding() {

    // Step 1: is the 'ANT4ECLIPSE_DEFAULT_FILE_ENCODING' property set?
    String property = getProject().getProperty(ANT4ECLIPSE_DEFAULT_FILE_ENCODING);
    if (property != null) {
      return property;
    }

    // Step 2: is the encoding set in the javac task?
    String encoding = getJavac().getEncoding();
    if (encoding != null) {
      return encoding;
    }

    // Step 3: try to resolve the os specific eclipse encoding
    if (Os.isFamily(Os.FAMILY_WINDOWS) && Charset.isSupported("Cp1252")) {
      return "Cp1252";
    } else if (Os.isFamily(Os.FAMILY_UNIX) && Charset.isSupported("UTF-8")) {
      return "UTF-8";
    } else if (Os.isFamily(Os.FAMILY_MAC) && Charset.isSupported("MacRoman")) {
      return "MacRoman";
    }

    // Step 4: last resort: return the default file encoding
    return System.getProperty("file.encoding");
  }
}
