package camelcase.jdt.spelling.directory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.osgi.framework.Bundle;

import camelcase.jdt.spelling.SpellingPlugin;

public class DictionaryFactory {

  private static final String DICTIONARY_LOCATION = "dictionaries";
  private static final String DICTIONARY_TYPE = ".dictionary";
  private static final String CODE_WORDS_TYPE = ".code_words";
  private static final Locale EN_US = Locale.US;

  private final Directory directory;

  public DictionaryFactory() {

    directory = new Directory();
    try {
      final URL folder = getDictionaryLocation();
      readDirectory(DICTIONARY_TYPE, folder);
      readDirectory(CODE_WORDS_TYPE, folder);
    } catch (final IOException e) {
      SpellingPlugin.getInstance().getLog().error("Can not read directories ", e);
    }
  }

  public IDirectory getDirectory() {
    return directory;
  }

  private void readDirectory(final String codeWordsType, final URL folder) throws MalformedURLException {
    final URL localeUrl = new URL(folder.toString() + "/" + EN_US.toString() + codeWordsType);

    InputStream stream = null;
    try {
      stream = localeUrl.openStream();
      String word = null;
      try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
        boolean doRead = true;
        while (doRead) {
          word = reader.readLine();
          doRead = word != null;
          if (doRead
              && isAlpha(word))
            directory.add(word.toLowerCase(EN_US));
        }
      }
    } catch (final IOException ex) {

    }
  }

  private URL getDictionaryLocation() throws IOException {
    final Bundle bundle = SpellingPlugin.getInstance().getBundle();
    if (bundle != null) {
      SpellingPlugin.getInstance().getLog().info("Bundle found");
      final URL url = bundle.getResource("/" + DICTIONARY_LOCATION);
      if (url != null) {
        SpellingPlugin.getInstance().getLog().info("URL found");
        return bundle.getResource("/" + DICTIONARY_LOCATION);
      }
    }
    return this.getClass().getClassLoader().getResource(DICTIONARY_LOCATION);
  }

  public boolean isAlpha(final String name) {
    return name.matches("[a-zA-Z]+");
  }

}
