package camelcase.jdt.spelling.checker;

import camelcase.jdt.spelling.directory.IDirectory;

public class SpellCheckerFactory {

  private final IDirectory directory;

  public SpellCheckerFactory(final IDirectory directory) {
    super();
    this.directory = directory;
  }

  public SpellChecker getSpellChecker() {
    return new Checker(directory);
  }

}
