package camelcase.jdt.spelling;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class Startup implements IStartup {

  @Override
  public void earlyStartup() {
    final IWorkbench workbench = PlatformUI.getWorkbench();

    workbench.getDisplay().asyncExec(() -> {
      final ILog logger = SpellingPlugin.getInstance().getLog();
      logger.info("Spelling startup called");
      SpellingPlugin.getInstance().initialize(workbench);
    });
  }

}
