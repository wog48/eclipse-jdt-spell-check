package camelcase.jdt.spelling.marker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.SpellingEvent;

public class MarkerFactory {
  final static String SPELLING_MARKER = "camelcase.jdt.spelling.marker";
  final static String SPELLING_MARKER_WORD = "camelcase.jdt.spelling.marker.word";

  private final Set<SpellingEvent> marker = new HashSet<>();

  public synchronized void process(final IResource resource, final List<SpellingEvent> toBeMarked) {

    if (makerChanged(toBeMarked)) {
      clear(resource);
      final MarkerJob job = new MarkerJob(resource, () -> {
        toBeMarked.stream().forEach(e -> {
          create(e);
        });
      });
      job.schedule();
    }
  }

  public synchronized void prepare(final IResource resource) {
    final MarkerJob job = new MarkerJob(resource, () -> {
      clear(resource);
    });
    job.schedule();
  }

  private boolean makerChanged(final List<SpellingEvent> toBeMarked) {
    final Set<SpellingEvent> found = new HashSet<>();
    if (marker.size() == toBeMarked.size()) {
      for (final SpellingEvent m : toBeMarked)
        if (!marker.contains(m))
          return true;
        else
          found.add(m);
      return found.size() != marker.size();
    }
    return true;
  }

  public void create(final SpellingEvent event) {
    try {
      final IResource resource = findResource(event);
      final ISourceRange sourceRange = event.getSourceRange();

      scheduleWorkspaceJob(resource, sourceRange.getOffset(),
          sourceRange.getOffset() + sourceRange.getLength(), event.getFragment().getOriginalFragment());

    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("", e);
    } finally {
      marker.add(event);
    }
  }

  private IResource findResource(final SpellingEvent event) {
    final IResource r = event.getElement().getResource();
    if (r == null)
      return event.getResource();
//    final IResource r = event.getResource();
//    if (r == null && event.getParent() != null)
//      return findResource(event.getParent());
    return r;
  }

  private void scheduleWorkspaceJob(final IResource resource, final int start, final int end,
      final String word) throws CoreException {

    final Map<String, Object> attributes = new HashMap<>();
    attributes.put(IMarker.MESSAGE, "Incorrect spelling");
    attributes.put(IMarker.CHAR_START, start);
    attributes.put(IMarker.CHAR_END, end);
    attributes.put(IMarker.SOURCE_ID, SpellingPlugin.PLUGIN_ID);
    attributes.put(SPELLING_MARKER_WORD, word);
    create(resource, attributes);
  }

  private Optional<IMarker> create(final IResource resource, final Map<String, Object> attributes) {
    try {
      if (resource == null)
        SpellingPlugin.getInstance().getLog().error("Resource null");
      if (resource.exists())
        return Optional.of(resource.createMarker(SPELLING_MARKER, attributes));
    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("", e);
    }
    return Optional.empty();
  }

  public IMarker[] find(final IResource target) {
    try {
      return target.findMarkers(SPELLING_MARKER, true, IResource.DEPTH_INFINITE);
    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("Error when trying to find maker of " + target, e);
      return null;
    }
  }

  public void clear(final IResource resource) {
    try {
      resource.deleteMarkers(SPELLING_MARKER, true, IResource.DEPTH_INFINITE);
      marker.clear();
    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("", e);
    }
  }

  public void clear(final IWorkbench workbench) {
    final IWorkbenchPage[] pages =
        workbench
            .getActiveWorkbenchWindow()
            .getPages();
    for (final IWorkbenchPage page : pages) {
      final IEditorReference[] editors = page.getEditorReferences();
      for (final IEditorReference editor : editors)
        Optional.ofNullable(editor.getEditor(false))
            .map(IEditorPart::getEditorInput)
            .map(i -> i.getAdapter(IFile.class))
            .ifPresent(r -> clear(r));
    }
  }
}
