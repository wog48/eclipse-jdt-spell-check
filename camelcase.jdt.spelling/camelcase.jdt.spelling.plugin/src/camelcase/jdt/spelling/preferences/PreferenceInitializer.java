package camelcase.jdt.spelling.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
   */
  @Override
  public void initializeDefaultPreferences() {
    final IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferencePage.PREFERENCE_PAGE_ID);
    store.setDefault(PreferenceConstants.SPELL_CHECK_ENABLED, true);
    store.setDefault(PreferenceConstants.IGNORE_SINGLE_CHARACTER, false);
    store.setDefault(PreferenceConstants.USER_DIRECTORY_PATH, "UserDirectory");
  }

}
