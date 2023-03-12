package camelcase.jdt.spelling;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import camelcase.jdt.spelling.checker.SpellChecker;
import camelcase.jdt.spelling.checker.SpellCheckerFactory;
import camelcase.jdt.spelling.directory.DictionaryFactory;
import camelcase.jdt.spelling.directory.IDirectory;
import camelcase.jdt.spelling.listener.ChangeListener;
import camelcase.jdt.spelling.listener.IWorkbenchTracker;
import camelcase.jdt.spelling.listener.WorkbenchTrackerFactory;
import camelcase.jdt.spelling.marker.MarkerFactory;

public class SpellingPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "camelcase.jdt.spelling";
  private static SpellingPlugin instance = new SpellingPlugin();

  private IWorkbench workbench;
  private IWorkbenchTracker workbenchTracker;
  private IElementChangedListener changeListner;
  private SpellCheckerFactory checkerFactory;
  private SpellChecker checker;
  private IDirectory directory;
  private MarkerFactory markerFactory;

  public static SpellingPlugin getInstance() {
    return instance;
  }

  public SpellingPlugin() {
    super();
  }

  public void initialise(final IWorkbench workbench) {
    this.workbench = workbench;
    debug("Workbench set: " + workbench.toString());
    updateStatus();
  }

  private void updateStatus() {
    workbench.getDisplay().asyncExec(() -> {
      final boolean enabled = true;
      if (enabled)
        enable();
      else
        disable();
    });
  }

  @Override
  public void start(final BundleContext context) throws Exception {
    super.start(context);
    enable();
  }

  public SpellChecker getSpellChecker() {
    return checker;
  }

  private synchronized void enable() {
    if (workbench != null) {
      directory = new DictionaryFactory().getDirectory();
      markerFactory = new MarkerFactory();
      checkerFactory = new SpellCheckerFactory(directory);
      checker = checkerFactory.getSpellChecker();
      workbenchTracker = new WorkbenchTrackerFactory().getWorkbenchTracker(checker, markerFactory);
      workbenchTracker.track(workbench);
      changeListner = new ChangeListener(checker, workbenchTracker, markerFactory);
      JavaCore.addElementChangedListener(changeListner);
    }
  }

  private synchronized void disable() {
    if (workbench != null) {
      workbenchTracker.untrack(workbench);
      JavaCore.removeElementChangedListener(changeListner);
      checkerFactory = null;
    }
  }

  @Override
  public void stop(final BundleContext context) throws Exception {
    try {
      disable();
    } finally {
      if (workbench != null) workbench = null;
      if (Platform.isRunning()) super.stop(context);
    }
  }

  public static void debug(final String string) {
    if (getInstance().isDebugging())
      getInstance()
          .getLog()
          .info(string);

  }

  public IDirectory getDirectory() {
    return directory;
  }

  public static void error(final Exception e) {
    getInstance()
        .getLog()
        .error("An error occured ", e);

  }

}
