package camelcase.jdt.spelling.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FragmentTest {

  private Fragment cut;

  @ParameterizedTest
  @CsvSource({ "DONE, true", "Done, false", "dONE, false" })
  void checkAllUpperCase(final String token, final String exp) {
    cut = new Fragment(token, 0);
    assertEquals(Boolean.valueOf(exp), cut.isAllUpper());
  }

  @ParameterizedTest
  @CsvSource({ "DONE, true", "Done, true", "dONE, false", "done, false" })
  void checkFirstUpper(final String token, final String exp) {
    cut = new Fragment(token, 0);
    assertEquals(Boolean.valueOf(exp), cut.firstIsUpper());
  }

  @ParameterizedTest
  @CsvSource({ "DONE, false", "Done, false", "dONE, true", "done, true" })
  void checkFirstLower(final String token, final String exp) {
    cut = new Fragment(token, 0);
    assertEquals(Boolean.valueOf(exp), cut.firstIsLower());
  }

  @Test
  void checkMerge1() {
    cut = new Fragment("DO", 2);
    final Fragment newToken = new Fragment("NE", 4);

    cut.merge(newToken);
    assertEquals("DONE", cut.getOriginalFragment());
    assertEquals(0, cut.getFragmentStart());
  }

  @Test
  void checkMerge2() {
    cut = new Fragment("DO", 4);
    final Fragment newToken = new Fragment("NE", 2);

    cut.merge(newToken);
    assertEquals("NEDO", cut.getOriginalFragment());
    assertEquals(0, cut.getFragmentStart());
  }
}
