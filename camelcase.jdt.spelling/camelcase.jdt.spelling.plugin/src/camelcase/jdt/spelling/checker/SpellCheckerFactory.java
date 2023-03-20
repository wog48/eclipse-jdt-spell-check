package camelcase.jdt.spelling.checker;

import java.util.List;

import camelcase.jdt.spelling.directory.DictionaryFactory;
import camelcase.jdt.spelling.directory.IDirectory;

public class SpellCheckerFactory {

  private final List<IDirectory> directories;

  public SpellCheckerFactory(final DictionaryFactory dictionaryFactory) {
    super();
    this.directories = dictionaryFactory.getDirectories();
  }

  public ISpellChecker getSpellChecker() {
    return new SpellChecker(directories);
  }

}
