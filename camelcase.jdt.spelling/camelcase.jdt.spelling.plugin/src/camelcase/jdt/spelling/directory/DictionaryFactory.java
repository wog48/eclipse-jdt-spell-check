package camelcase.jdt.spelling.directory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.framework.Bundle;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.preferences.PreferencePage;

public class DictionaryFactory {

  private static final String DICTIONARY_LOCATION = "dictionaries";
  private static final String DICTIONARY_TYPE = ".dictionary";
  private static final String CODE_WORDS_TYPE = ".code_words";
  private static final Locale EN_US = Locale.US;
  private static final String HOME_DIRECTORY = System.getProperty("user.home");

  private final Directory directory;
  private Directory userDirectory;
  private final List<IDirectory> directories;
  private List<String> userDirectoryEntries;

  public DictionaryFactory() {
    directories = new ArrayList<>(2);
    directory = new Directory();
    userDirectory = new Directory();
    try {
      buildStandardDirectory();
      buildUserDirectory();
    } catch (final IOException e) {
      SpellingPlugin.getInstance().getLog().error("Can not read directories ", e);
    }
    directories.add(directory);
    directories.add(userDirectory);
  }

  private void buildUserDirectory() throws IOException {
    userDirectoryEntries = readUserDirectory();
    userDirectoryEntries.stream()
        .filter(this::isAlpha)
        .forEach(word -> {
          userDirectory.add(word.toLowerCase(EN_US));
        });
  }

  private List<String> readUserDirectory() throws IOException {
    final Path userDirectoryPath = Paths.get(PreferencePage.getUserDirectoryPathPreference());
    try {
      return Files.readAllLines(userDirectoryPath);
    } catch (final NoSuchFileException e) {
      Files.createFile(userDirectoryPath);
      return new ArrayList<>();
    }
  }

  private void saveUserDirectory() {
    try {
      final Path userDirectoryPath = Paths.get(PreferencePage.getUserDirectoryPathPreference());
      Files.write(userDirectoryPath, userDirectoryEntries);
    } catch (final IOException e) {
      SpellingPlugin.getInstance().getLog().error("Can not save user directory ", e);
    }
  }

  private void buildStandardDirectory() throws IOException, MalformedURLException {
    final URL folder = getDictionaryLocation();
    readDirectory(DICTIONARY_TYPE, folder);
    readDirectory(CODE_WORDS_TYPE, folder);
  }

  public List<IDirectory> getDirectories() {
    return directories;
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

  public void addToUserDirectory(final String word) {
    userDirectory.add(word.toLowerCase(EN_US));
    userDirectoryEntries.add(word);
    saveUserDirectory();
  }

  public void changeUserDirectory(final String newValue) {
    final int index = directories.indexOf(userDirectory);
    userDirectory = new Directory();
    directories.set(index, userDirectory);
    try {
      buildUserDirectory();
    } catch (final IOException e) {
      SpellingPlugin.getInstance().getLog().error("Can not switch user directory ", e);
    }
  }

}
