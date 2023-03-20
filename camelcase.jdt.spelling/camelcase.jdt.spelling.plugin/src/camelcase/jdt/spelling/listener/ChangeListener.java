package camelcase.jdt.spelling.listener;

import java.util.Optional;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.marker.MarkerFactory;

public class ChangeListener implements IElementChangedListener {
  private final ISpellChecker spellChecker;
  private final IWorkbenchTracker workbenchTracker;
  private final MarkerFactory markerFactory;

  public ChangeListener(final ISpellChecker spellChecker, final IWorkbenchTracker workbenchTracker,
      final MarkerFactory markerFactory) {
    this.spellChecker = spellChecker;
    this.workbenchTracker = workbenchTracker;
    this.markerFactory = markerFactory;
  }

  @Override
  public void elementChanged(final ElementChangedEvent event) {
    SpellingPlugin.debug("Change event received " + event);

    final IJavaElementDelta delta = event.getDelta();
    if (IJavaElementDelta.REMOVED != delta.getKind()) {

      final IJavaElement element = delta.getElement();
      SpellingPlugin.debug("Changed element " + element
          + ", " + "Change: "
          + Integer.toHexString(delta.getFlags())
          + "|"
          + Integer.toBinaryString(delta.getFlags()));
      if (isCurrent(element))
        markerFactory.process(element.getResource(), spellChecker.checkElement((ICompilationUnit) element));
    }
  }

  private boolean isCurrent(final IJavaElement element) {
    return Optional.ofNullable(element.getResource())
        .map(r -> r.equals(workbenchTracker.getCurrentResource()))
        .orElse(false);
  }
}
