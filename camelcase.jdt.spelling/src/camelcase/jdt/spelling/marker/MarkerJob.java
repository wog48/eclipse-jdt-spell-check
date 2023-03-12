package camelcase.jdt.spelling.marker;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.IProgressConstants2;

import camelcase.jdt.spelling.SpellingPlugin;

public class MarkerJob extends WorkspaceJob {

  private final MarkerRunnable runnable;

  public MarkerJob(final IResource resource, final MarkerRunnable runnable) {
    super(MarkerFactory.SPELLING_MARKER);
    this.runnable = runnable;
    setRule(ResourcesPlugin.getWorkspace().getRuleFactory().markerRule(resource));
    setUser(false);
    setProperty(IProgressConstants2.SHOW_IN_TASKBAR_ICON_PROPERTY, Boolean.TRUE);
  }

  @Override
  public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
    try {
      runnable.run();
    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("Error runnig marker job", e);
      return e.getStatus();
    }
    return Status.OK_STATUS;
  }

  public interface MarkerRunnable {
    void run() throws CoreException;
  }
}
