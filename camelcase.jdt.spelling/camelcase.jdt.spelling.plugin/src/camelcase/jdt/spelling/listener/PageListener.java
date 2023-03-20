package camelcase.jdt.spelling.listener;

import java.util.Optional;

import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.marker.MarkerFactory;

class PageListener implements IPageListener {

  private final PartListener partListener;

  PageListener(final ISpellChecker checker, final MarkerFactory markerFactory) {
    this.partListener = new PartListener(checker, markerFactory);
  }

  @Override
  public void pageActivated(final IWorkbenchPage page) {
    SpellingPlugin.debug("Active editor " + page.getActiveEditor());
    partListener.partActivated(page.getActiveEditor());
  }

  @Override
  public void pageClosed(final IWorkbenchPage page) {
    page.removePartListener(partListener);
  }

  @Override
  public void pageOpened(final IWorkbenchPage page) {
    SpellingPlugin.debug("Active editor " + page.getActiveEditor());
    page.addPartListener(partListener);
  }

  IResource getCurrentResource() {
    return partListener.getCurrentResource();
  }

  void untrack(final IWorkbenchWindow... workbenchWindows) {
    for (final IWorkbenchWindow workbenchWindow : workbenchWindows) {
      removePartListener(workbenchWindow.getPages());
      workbenchWindow.removePageListener(this);
      deactivate(workbenchWindow.getPages());
    }
  }

  private void deactivate(final IWorkbenchPage... pages) {
    for (final IWorkbenchPage page : pages)
      for (final IEditorReference editorReference : page.getEditorReferences())
        Optional.ofNullable(editorReference.getEditor(false))
            .ifPresent(partListener::partClosed);
  }

  void track(final IWorkbenchWindow... workbenchWindows) {
    for (final IWorkbenchWindow workbenchWindow : workbenchWindows) {
      workbenchWindow.addPageListener(this);
      setPartListener(workbenchWindow.getPages());
      activate(workbenchWindow);
    }
  }

  private void setPartListener(final IWorkbenchPage[] pages) {
    for (final IWorkbenchPage page : pages)
      page.addPartListener(partListener);
  }

  private void removePartListener(final IWorkbenchPage[] pages) {
    for (final IWorkbenchPage page : pages)
      page.removePartListener(partListener);
  }

  private void activate(final IWorkbenchWindow workbenchWindow) {
    SpellingPlugin.getInstance().getLog().info("Active page " + workbenchWindow.getActivePage());
    Optional.ofNullable(workbenchWindow.getActivePage())
        .ifPresent(activePage -> {
          pageActivated(activePage);
        });
  }
}
