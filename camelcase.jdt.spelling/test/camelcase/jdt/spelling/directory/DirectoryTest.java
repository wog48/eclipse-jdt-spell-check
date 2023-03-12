package camelcase.jdt.spelling.directory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DirectoryTest {
  private Directory cut;

  @BeforeEach
  void setup() {
    cut = new Directory();
  }

  @Test
  void canCreateDirectory() {
    assertNotNull(cut);
  }

  @Test
  void containsReturnTrueIfWordInDirectory() {
    cut.add("liebe");
    assertTrue(cut.contains("liebe"));
    assertEquals(5, cut.maxLevels());
  }

  @Test
  void containsReturnFalseIfWordNotInDirectory() {
    cut.add("liebe");
    assertFalse(cut.contains("libe"));
  }

  @Test
  void containsReturnFalseWhenAskingForSubstring() {
    cut.add("liebe");
    assertFalse(cut.contains("lie"));
  }

  @Test
  void canAddTwoWords() {
    cut.add("liebe").add("leib");
    assertEquals(5, cut.maxLevels());
    assertTrue(cut.contains("liebe"));
    assertTrue(cut.contains("leib"));
    assertFalse(cut.contains("libe"));
  }

  @Test
  void canAddTwoWordsSecondLonger() {
    cut.add("liebe").add("wettervorhersage");
    assertEquals(16, cut.maxLevels());
  }

  @ParameterizedTest
  @CsvSource({ "acfh,acf", "abdh,abd", "abeh,abe", "acgh,acg" })
  void getProposalsInsertationAtTheEnd(final String word, final String exp) {
    buildInitialDirectory();
    final List<String> act = cut.getProposals(word);
    assertEquals(1, act.size());
    assertEquals(exp, act.get(0));
  }

  @ParameterizedTest
  @CsvSource({ "adf,acf", "add,abd", "ade,abe", "adg,acg" })
  void getProposalsReplacementInTheMiddle(final String word, final String exp) {
    buildInitialDirectory();
    final List<String> act = cut.getProposals(word);
    assertEquals(1, act.size());
    assertEquals(exp, act.get(0));
  }

  @ParameterizedTest
  @CsvSource({ "acx,acf,acg", "abx,abd,abe" })
  void getProposalsReplacementAtTheEnd(final String word, final String exp1, final String exp2) {
    buildInitialDirectory();
    final List<String> act = cut.getProposals(word);
    assertEquals(2, act.size());
    assertTrue(act.stream().filter(p -> p.equals(exp1)).findFirst().isPresent());
    assertTrue(act.stream().filter(p -> p.equals(exp2)).findFirst().isPresent());
  }

  @ParameterizedTest
  @CsvSource({ "abim,abeim", "aeim,abeim" })
  void getProposalsDeletionInTheMiddle(final String word, final String exp) {
    cut.add("acgko").add("abdhl").add("abeim").add("acfjn");
    final List<String> act = cut.getProposals(word);
    assertEquals(1, act.size());
    assertEquals(exp, act.get(0));
  }

  @Test
  void getProposalsinfinitive() {
    cut.add("acgk").add("acgke").add("acgken").add("ag");
    final List<String> act = cut.getProposals("agk");
    assertEquals(2, act.size());
    assertTrue(act.stream().filter(p -> p.equals("acgk")).findFirst().isPresent());
    assertTrue(act.stream().filter(p -> p.equals("ag")).findFirst().isPresent());
  }

  @Test
  void getProposalsDeletionAtTheEnd() {
    cut.add("abeim").add("acfjn").add("acgko").add("abeip").add("abdhl");
    final List<String> act = cut.getProposals("abei");
    assertEquals(2, act.size());
    assertTrue(act.stream().filter(p -> p.equals("abeim")).findFirst().isPresent());
    assertTrue(act.stream().filter(p -> p.equals("abeip")).findFirst().isPresent());
  }

  @ParameterizedTest
  @CsvSource({ "acffjn,acfjn" })
  void getProposalsOneInsertationInTheMiddle(final String word, final String exp) {
    cut.add("acgko").add("abdhl").add("abeim").add("acfjn");
    final List<String> act = cut.getProposals(word);
    assertEquals(1, act.size());
    assertEquals(exp, act.get(0));
  }

  @ParameterizedTest
  @CsvSource({ "abhdl,abdhl", "acfnj,acfjn", "aebim,abeim" })
  void getProposalsOneTranspositionInTheMiddle(final String word, final String exp) {
    cut.add("abdhl").add("abeim").add("acfjn").add("acgko");
    final List<String> act = cut.getProposals(word);
    assertEquals(1, act.size());
    assertEquals(exp, act.get(0));
  }

  @ParameterizedTest
  @CsvSource({ "ackog" })
  void getProposalsOneDeletionAndOneInsertation(final String word) {
    cut.add("acgko").add("abdhl").add("abeim").add("acfjn");
    final List<String> act = cut.getProposals(word);
    assertTrue(act.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({ "aokcg" })
  void getProposalsTranspositionOverTwo(final String word) {
    cut.add("acgko").add("abdhl").add("abeim").add("acfjn");
    final List<String> act = cut.getProposals(word);
    assertTrue(act.isEmpty());
  }

  @ParameterizedTest
  @CsvSource({ "aciko,acgko,acfko", "abeim,abeim,abexm" })
  void getProposalsOneReplacementTwoResults(final String word, final String exp1, final String exp2) {
    cut.add("acgko").add("acfko").add("abexm").add("abeim").add("acfjn");
    final List<String> act = cut.getProposals(word);
    assertEquals(2, act.size());
    assertTrue(act.stream().filter(p -> p.equals(exp1)).findFirst().isPresent());
    assertTrue(act.stream().filter(p -> p.equals(exp2)).findFirst().isPresent());
  }

  private void buildInitialDirectory() {
    cut.add("abd").add("abe").add("acf").add("acg");
  }

}
