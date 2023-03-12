package camelcase.jdt.spelling.listener;

import camelcase.jdt.spelling.checker.SpellChecker;
import camelcase.jdt.spelling.marker.MarkerFactory;

public class WorkbenchTrackerFactory {

  public IWorkbenchTracker getWorkbenchTracker(final SpellChecker checker, final MarkerFactory markerFactory) {
    return new WindowListener(checker, markerFactory);
  }
}
