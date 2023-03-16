package camelcase.jdt.spelling.marker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.SpellingEvent;

public class MarkerFactory {
  final static String SPELLING_MARKER = "camelcase.jdt.spelling.marker";
  final static String SPELLING_MARKER_WORD = "camelcase.jdt.spelling.marker.word";

  private final Set<SpellingEvent> marker = new HashSet<>();

  public synchronized void process(final IJavaElement element, final List<SpellingEvent> toBeMarked) {
    final IResource resource = element.getResource();

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

  private boolean makerChanged(final List<SpellingEvent> toBeMarked) {
    final Set<SpellingEvent> found = new HashSet<>();
    for (final SpellingEvent m : toBeMarked)
      if (!marker.contains(m))
        return true;
      else
        found.add(m);
    return found.size() != marker.size();
  }

  public void create(final SpellingEvent event) {
    try {
      final IJavaElement javaElement = event.getElement();
      final IResource resource = javaElement.getResource();
      final ISourceRange sourceRange = event.getSourceRange();

      scheduleWorkspaceJob(resource, sourceRange.getOffset(),
          sourceRange.getOffset() + sourceRange.getLength(), event.getFragment().getOriginalFragment());

    } catch (final CoreException e) {
      SpellingPlugin.getInstance().getLog().error("", e);
    } finally {
      marker.add(event);
    }
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
}
