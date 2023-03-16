package camelcase.jdt.spelling;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
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
  public static final String PLUGIN_ID = "camelcase.jdt.spelling.plugin";
  public static final String IMAGE_ID = "correction_rename";
  private static final String JDT_PLUGIN = "org.eclipse.jdt.ui";
  private static SpellingPlugin instance = new SpellingPlugin();

  private IWorkbench workbench;
  private IWorkbenchTracker workbenchTracker;
  private IElementChangedListener changeListener;
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

  public void initialize(final IWorkbench workbench) {
    this.workbench = workbench;
    final Bundle bundle = Platform.getBundle(PLUGIN_ID);
    final IPath path = new Path("icons/correction_rename.png");
    final URL url = FileLocator.find(bundle, path, null);
    final ImageDescriptor desc = ImageDescriptor.createFromURL(url);
    getImageRegistry().put(IMAGE_ID, desc);
    getImageRegistry().get(IMAGE_ID);

    final ISharedImages images = workbench.getSharedImages();
    final Image image = images.getImage("correction_rename");

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
      changeListener = new ChangeListener(checker, workbenchTracker, markerFactory);
      JavaCore.addElementChangedListener(changeListener);
    }
  }

  private synchronized void disable() {
    if (workbench != null) {
      workbenchTracker.untrack(workbench);
      JavaCore.removeElementChangedListener(changeListener);
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
