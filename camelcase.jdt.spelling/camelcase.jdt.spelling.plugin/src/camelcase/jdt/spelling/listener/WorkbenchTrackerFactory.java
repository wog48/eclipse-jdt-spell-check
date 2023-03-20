package camelcase.jdt.spelling.listener;

import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.marker.MarkerFactory;

public class WorkbenchTrackerFactory {

  public IWorkbenchTracker getWorkbenchTracker(final ISpellChecker checker, final MarkerFactory markerFactory) {
    return new WindowListener(checker, markerFactory);
  }
}
