package camelcase.jdt.spelling;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import camelcase.jdt.spelling.checker.ISpellChecker;
import camelcase.jdt.spelling.checker.SpellCheckerFactory;
import camelcase.jdt.spelling.directory.DictionaryFactory;
import camelcase.jdt.spelling.listener.ChangeListener;
import camelcase.jdt.spelling.listener.IWorkbenchTracker;
import camelcase.jdt.spelling.listener.WorkbenchTrackerFactory;
import camelcase.jdt.spelling.marker.MarkerFactory;
import camelcase.jdt.spelling.quickfix.AddWordProposal;
import camelcase.jdt.spelling.quickfix.CorrectionProposal;

public class SpellingPlugin extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "camelcase.jdt.spelling.plugin";
  private static SpellingPlugin instance = new SpellingPlugin();

  private IWorkbench workbench;
  private IWorkbenchTracker workbenchTracker;
  private IElementChangedListener changeListener;
  private SpellCheckerFactory checkerFactory;
  private ISpellChecker checker;
  private MarkerFactory markerFactory;
  private DictionaryFactory dictionaryFactory;

  public static SpellingPlugin getInstance() {
    return instance;
  }

  public SpellingPlugin() {
    super();
  }

  public void initialize(final IWorkbench workbench) {
    this.workbench = workbench;
    final Bundle bundle = Platform.getBundle(PLUGIN_ID);
    addIcon(bundle, CorrectionProposal.IMAGE_ID);
    addIcon(bundle, AddWordProposal.IMAGE_ID);

    debug("Workbench set: " + workbench.toString());
    updateStatus();
  }

  private void addIcon(final Bundle bundle, final String imageId) {
    final String pathString = "icons/" + imageId;
    final IPath path = new Path(pathString);
    final URL url = FileLocator.find(bundle, path, null);
    final ImageDescriptor desc = ImageDescriptor.createFromURL(url);
    getImageRegistry().put(imageId, desc);
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

  public ISpellChecker getSpellChecker() {
    return checker;
  }

  public synchronized void enable() {
    if (workbench != null) {
      dictionaryFactory = new DictionaryFactory();
      markerFactory = new MarkerFactory();
      checkerFactory = new SpellCheckerFactory(dictionaryFactory);
      checker = checkerFactory.getSpellChecker();
      workbenchTracker = new WorkbenchTrackerFactory().getWorkbenchTracker(checker, markerFactory);
      workbenchTracker.track(workbench);
      changeListener = new ChangeListener(checker, workbenchTracker, markerFactory);
      JavaCore.addElementChangedListener(changeListener);
    }
  }

  public synchronized void disable() {
    if (workbench != null) {
      workbenchTracker.untrack(workbench);
      JavaCore.removeElementChangedListener(changeListener);
      checkerFactory = null;
      markerFactory.clear(workbench);
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

  public DictionaryFactory getDictionaryFactory() {
    return dictionaryFactory;
  }

  public static void error(final Exception e) {
    getInstance()
        .getLog()
        .error("An error occured ", e);

  }

  public void checkCurrent() {
    final IEditorPart editor = workbench.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    final IResource r = editor.getEditorInput().getAdapter(IFile.class);
    markerFactory.clear(r);
    checker.checkResource(r);
  }

}
