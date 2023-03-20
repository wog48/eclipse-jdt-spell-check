package camelcase.jdt.spelling.directory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DictionaryFactoryTest {

  private static List<IDirectory> dir;

  @BeforeAll
  static void classSetup() {
    final DictionaryFactory factory = new DictionaryFactory();
    dir = factory.getDirectories();
  }

  @Test
  void canCreateDirectory() {
    assertNotNull(dir);
  }

  @Test
  void canContainsMultipleDirectories() {
    assertEquals(2, dir.size());
  }

  @Test
  void directoryContainsWord() {
    assertTrue(dir.get(0).contains("time") || dir.get(1).contains("time"));
  }
}
