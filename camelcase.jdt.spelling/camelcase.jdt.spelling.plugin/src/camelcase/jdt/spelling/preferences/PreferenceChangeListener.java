package camelcase.jdt.spelling.preferences;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import camelcase.jdt.spelling.SpellingPlugin;

class PreferenceChangeListener implements IPropertyChangeListener {

  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    SpellingPlugin.getInstance().getLog().info("Got event: " + event);
    final SpellingPlugin plugin = SpellingPlugin.getInstance();
    switch (event.getProperty()) {
    case PreferenceConstants.SPELL_CHECK_ENABLED:
      if (getBooleanValue(event))
        plugin.enable();
      else
        plugin.disable();
      break;

    case PreferenceConstants.IGNORE_SINGLE_CHARACTER:
      if (getBooleanValue(event))
        plugin.getSpellChecker().ignoreSingleCharacter();
      else
        plugin.getSpellChecker().respectSingleCharacter();
      plugin.checkCurrent();
      break;

    case PreferenceConstants.USER_DIRECTORY_PATH:
      plugin.getDictionaryFactory().changeUserDirectory((String) event.getNewValue());
      break;
    }

  }

  private boolean getBooleanValue(final PropertyChangeEvent event) {
    final Object newValue = event.getNewValue();
    if (newValue instanceof String)
      return Boolean.parseBoolean((String) newValue);
    return (boolean) newValue;
  }

}
