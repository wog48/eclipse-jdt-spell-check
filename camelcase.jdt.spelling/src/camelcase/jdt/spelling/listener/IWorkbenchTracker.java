package camelcase.jdt.spelling.listener;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWorkbench;

public interface IWorkbenchTracker {

  /**
   * <p>
   * <strong>Must be called on the UI thread</strong>
   * <p>
   * Track will add listeners to workbench to track Editors.
   */
  void untrack(final IWorkbench workbench);

  void track(final IWorkbench workbench);

  IResource getCurrentResource();

}
