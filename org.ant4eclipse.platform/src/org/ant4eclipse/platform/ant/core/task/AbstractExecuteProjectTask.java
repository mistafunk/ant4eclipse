package org.ant4eclipse.platform.ant.core.task;

import org.ant4eclipse.core.Assert;

import org.ant4eclipse.platform.ant.PlatformExecutorValuesProvider;
import org.ant4eclipse.platform.ant.core.MacroExecutionComponent;
import org.ant4eclipse.platform.ant.core.ScopedMacroDefinition;
import org.ant4eclipse.platform.ant.core.delegate.MacroExecutionDelegate;
import org.ant4eclipse.platform.ant.core.delegate.MacroExecutionValuesProvider;

import org.apache.tools.ant.DynamicElement;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroDef.NestedSequential;

import java.util.List;

/**
 * <p>
 * Abstract base class for all tasks that allow to iterate over a JDT (or JDT-based) project. This class can be
 * subclassed to implement a custom executor task for specific project types (e.g. PDE plug-in projects).
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractExecuteProjectTask extends AbstractProjectPathTask implements DynamicElement,
    MacroExecutionComponent<String> {

  /** the macro execution delegate */
  private final MacroExecutionDelegate<String> _macroExecutionDelegate;

  /** the platform executor values provider */
  private final PlatformExecutorValuesProvider _platformExecutorValuesProvider;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractExecuteProjectTask}.
   * </p>
   * 
   * @param prefix
   *          the prefix for all scoped values
   */
  public AbstractExecuteProjectTask(final String prefix) {
    Assert.notNull(prefix);

    this._platformExecutorValuesProvider = new PlatformExecutorValuesProvider(this);

    // create the delegates
    this._macroExecutionDelegate = new MacroExecutionDelegate<String>(this, prefix);
  }

  /**
   * <p>
   * </p>
   * 
   * @return the platformExecutorValuesProvider
   */
  public PlatformExecutorValuesProvider getPlatformExecutorValuesProvider() {
    return this._platformExecutorValuesProvider;
  }

  /**
   * {@inheritDoc}
   */
  public final NestedSequential createScopedMacroDefinition(final String scope) {
    return this._macroExecutionDelegate.createScopedMacroDefinition(scope);
  }

  /**
   * {@inheritDoc}
   */
  public void executeMacroInstance(final MacroDef macroDef, final MacroExecutionValuesProvider provider) {
    this._macroExecutionDelegate.executeMacroInstance(macroDef, provider);
  }

  /**
   * {@inheritDoc}
   */
  public final List<ScopedMacroDefinition<String>> getScopedMacroDefinitions() {
    return this._macroExecutionDelegate.getScopedMacroDefinitions();
  }

  /**
   * {@inheritDoc}
   */
  public final String getPrefix() {
    return this._macroExecutionDelegate.getPrefix();
  }

  /**
   * {@inheritDoc}
   */
  public final void setPrefix(final String prefix) {
    this._macroExecutionDelegate.setPrefix(prefix);
  }
}
