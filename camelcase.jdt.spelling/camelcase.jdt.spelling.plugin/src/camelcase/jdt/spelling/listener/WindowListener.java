package camelcase.jdt.spelling.listener;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.marker.MarkerFactory;

class WindowListener implements IWindowListener, IWorkbenchTracker {

  private final PageListener pageListener;

  WindowListener(final ISpellChecker checker, final MarkerFactory markerFactory) {
    pageListener = new PageListener(checker, markerFactory);
  }

  @Override
  public void track(final IWorkbench workbench) {

    SpellingPlugin.getInstance().getLog().info("Track " + workbench + ", #Windows " + workbench
        .getWorkbenchWindows().length);
    workbench.addWindowListener(this);
    pageListener.track(workbench.getWorkbenchWindows());
  }

  /**
   * <p>
   * <strong>Must be called on the UI thread</strong>
   * <p>
   * Track will add listeners to workbench to track Editors.
   */
  @Override
  public void untrack(final IWorkbench workbench) {
    pageListener.untrack(workbench.getWorkbenchWindows());
    workbench.removeWindowListener(this);
  }

  @Override
  public IResource getCurrentResource() {
    return pageListener.getCurrentResource();
  }

  @Override
  public void windowActivated(final IWorkbenchWindow window) {}

  @Override
  public void windowDeactivated(final IWorkbenchWindow window) {}

  @Override
  public void windowClosed(final IWorkbenchWindow window) {
    pageListener.untrack(window);
  }

  @Override
  public void windowOpened(final IWorkbenchWindow window) {
    pageListener.track(window);
  }

}
