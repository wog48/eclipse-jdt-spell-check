package camelcase.jdt.spelling.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage
    extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public static final String PREFERENCE_PAGE_ID = "camelcase.jdt.spelling.preferences.PreferencePage";

  public static String getUserDirectoryPathPreference() {
    return InstanceScope.INSTANCE.getNode(PREFERENCE_PAGE_ID).get(PreferenceConstants.USER_DIRECTORY_PATH,
        "userDirectory.dictionary");
  }

  public static boolean getEnabaledPreference() {
    return InstanceScope.INSTANCE.getNode(PREFERENCE_PAGE_ID).getBoolean(PreferenceConstants.SPELL_CHECK_ENABLED,
        true);
  }

  public static boolean getSingleCharPreference() {
    return InstanceScope.INSTANCE.getNode(PREFERENCE_PAGE_ID).getBoolean(PreferenceConstants.IGNORE_SINGLE_CHARACTER,
        false);
  }

  public PreferencePage() {
    super(GRID);
  }

  /**
   * Creates the field editors. Field editors are abstractions of
   * the common GUI blocks needed to manipulate various types
   * of preferences. Each field editor knows how to save and
   * restore itself.
   */
  @Override
  public void createFieldEditors() {
    addField(new BooleanFieldEditor(
        PreferenceConstants.SPELL_CHECK_ENABLED,
        "&Enabled",
        getFieldEditorParent()));

    addField(new BooleanFieldEditor(
        PreferenceConstants.IGNORE_SINGLE_CHARACTER,
        "&Ignore single character",
        getFieldEditorParent()));

    final FileFieldEditor fileEditor =
        new FileFieldEditor(
            PreferenceConstants.USER_DIRECTORY_PATH,
            "&User directory:",
            getFieldEditorParent());
    fileEditor.setFileExtensions(new String[] { "*.txt", "*.directory" });
    addField(fileEditor);

  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
   */
  @Override
  public void init(final IWorkbench workbench) {
    final ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCE_PAGE_ID);
    store.addPropertyChangeListener(new PreferenceChangeListener());
    setPreferenceStore(store);
    setDescription("Java spell checker preferences");
  }

}