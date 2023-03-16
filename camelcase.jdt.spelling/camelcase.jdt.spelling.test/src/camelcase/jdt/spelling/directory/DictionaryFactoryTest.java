package camelcase.jdt.spelling.directory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DictionaryFactoryTest {

  private static IDirectory dir;

  @BeforeAll
  static void classSetup() {
    final DictionaryFactory factory = new DictionaryFactory();
    dir = factory.getDirectory();
  }

  @Test
  void canCreateDirectory() {
    assertNotNull(dir);
  }

  @Test
  void directoryContainsWord() {
    assertTrue(dir.contains("time"));
  }
}
