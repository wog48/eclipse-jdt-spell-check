package camelcase.jdt.spelling.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.checker.SpellingEvent;
import camelcase.jdt.spelling.marker.MarkerFactory;

@SuppressWarnings("restriction")
class PartListener implements IPartListener {

  private static final Collection<String> JAVA_EXTENSIONS =
      new HashSet<String>(Arrays.asList(JavaCore.getJavaLikeExtensions()));

  private final ISpellChecker spellChecker;
  private IResource currentResource;
  private final MarkerFactory markerFactory;

  PartListener(final ISpellChecker checker, final MarkerFactory markerFactory) {
    super();
    this.spellChecker = checker;
    this.markerFactory = markerFactory;
  }

  @Override
  public void partActivated(final IWorkbenchPart part) {
    if (part instanceof IEditorPart)
      editorActivated((IEditorPart) part);
  }

  @Override
  public void partBroughtToTop(final IWorkbenchPart part) {
    // All changes of a workbench part end up in the event partActivated, handle spell check there
  }

  @Override
  public void partClosed(final IWorkbenchPart part) {
    if (part instanceof IEditorPart)
      editorDeactivated((IEditorPart) part);
  }

  @Override
  public void partDeactivated(final IWorkbenchPart part) {}

  @Override
  public void partOpened(final IWorkbenchPart part) {}

  IResource getCurrentResource() {
    SpellingPlugin.debug("Current resource " + currentResource);
    return currentResource;
  }

  private void editorDeactivated(final IEditorPart part) {
    clearEditor(part);
    final IResource resource = getResource(part);
    if (resource != null && resource.equals(currentResource))
      setCurrentResource(null);
  }

  private void clearEditor(final IEditorPart editor) {
    final IResource resource = getResource(editor);
    if (shouldProcess(resource) && spellChecker != null)
      spellChecker.complete(resource);
  }

  private IResource getResource(final IEditorPart editor) {
    return Optional.ofNullable(editor)
        .map(IEditorPart::getEditorInput)
        .map(i -> i.getAdapter(IFile.class))
        .orElse(null);
  }

  private void setCurrentResource(final IResource resource) {
    SpellingPlugin.debug("Set current resource " + resource);
    currentResource = resource;
  }

  private void editorActivated(final IEditorPart part) {
    currentResource = getResource(part);
    checkResource(currentResource);
  }

  private boolean shouldProcess(final IResource resource) {
    return resource != null
        && resource.exists()
        && IResource.FILE == resource.getType()
        && JAVA_EXTENSIONS.contains(resource.getFileExtension());
  }

  private void checkResource(final IResource resource) {
    if (shouldProcess(resource)) {
      final List<SpellingEvent> spellEvents = spellChecker.checkResource(resource);
      markerFactory.process(resource, spellEvents);
    }
  }

}
