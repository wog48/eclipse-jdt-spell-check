package camelcase.jdt.spelling.parser;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.junit.jupiter.api.Test;

class TokenTest {

  private Token cut;

  @Test
  void createTermElementNullEmptyTerm() {
    cut = new Token(null);
    final List<Fragment> act = cut.getFragments();
    assertNull(cut.getElement());
    assertTrue(act.isEmpty());
  }

  @Test
  void createTermOfClass() {
    final IJavaElement element = mock(IJavaElement.class);
    final String exp = "camelcase.jdt.spelling.parser";
    when(element.getElementName()).thenReturn(exp);
    when(element.getElementType()).thenReturn(IJavaElement.PACKAGE_DECLARATION);
    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(element, cut.getElement());
    assertEquals(7, act.size());
    assertStart(act, 0, 9, 10, 13, 14, 22, 23);
    assertToken(act, "camelcase", ".", "jdt", ".", "spelling", ".", "parser");
    assertEquals(exp, cut.getOriginalToken());
  }

  @Test
  void createTermOfType() {
    final IJavaElement element = mock(IJavaElement.class);
    when(element.getElementName()).thenReturn("TestClass");
    when(element.getElementType()).thenReturn(IJavaElement.TYPE);

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(2, act.size());
    assertStart(act, 0, 4);
    assertToken(act, "Test", "Class");
  }

  @Test
  void createTermOfMethod() {
    final IJavaElement element = mock(IJavaElement.class);
    when(element.getElementName()).thenReturn("somthingSpecialDone");
    when(element.getElementType()).thenReturn(IJavaElement.METHOD);

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 8, 15);
    assertToken(act, "somthing", "Special", "Done");
  }

  @Test
  void createTermOfMethodWithAbriviation() {
    final IJavaElement element = mock(IJavaElement.class);
    when(element.getElementName()).thenReturn("somthingDONESpecial");
    when(element.getElementType()).thenReturn(IJavaElement.METHOD);

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 8, 12);
    assertToken(act, "somthing", "DONE", "Special");
  }

  @Test
  void createTermOfVariable() {
    final IJavaElement element = mock(IJavaElement.class);
    final String exp = "sometingToBeDon";
    when(element.getElementName()).thenReturn(exp);
    when(element.getElementType()).thenReturn(IJavaElement.LOCAL_VARIABLE);
    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(4, act.size());
    assertStart(act, 0, 8, 10, 12);
    assertToken(act, "someting", "To", "Be", "Don");
    assertEquals(exp, cut.getOriginalToken());
  }

  @Test
  void createTermOfConstant() {
    final IJavaElement element = mock(IJavaElement.class);
    when(element.getElementName()).thenReturn("TEST_SNAKE");
    when(element.getElementType()).thenReturn(IJavaElement.FIELD);
    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 4, 5);
    assertToken(act, "TEST", "_", "SNAKE");
  }

  @Test
  void createTermOfParameterStarting() {
    final IJavaElement element = mock(IJavaElement.class);
    when(element.getElementName()).thenReturn("_test");
    when(element.getElementType()).thenReturn(IJavaElement.FIELD);
    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(2, act.size());
    assertStart(act, 0, 1);
    assertToken(act, "_", "test");
  }

  @Test
  void replaceFragments() {
    final IJavaElement element = mock(IJavaElement.class);
    final String exp = "sometingToBeDon";
    when(element.getElementName()).thenReturn(exp);
    when(element.getElementType()).thenReturn(IJavaElement.LOCAL_VARIABLE);
    cut = new Token(element);
    final List<Fragment> fragments = cut.getFragments();
    final Map<Fragment, String> replacments = new HashMap<>();
    replacments.put(fragments.get(0), "something");
    replacments.put(fragments.get(3), "done");

    final Token act = cut.replaceFragments(replacments);

    assertEquals("somethingToBeDone", act.getOriginalToken());

  }

  private void assertToken(final List<Fragment> act, final String... exp) {
    for (int i = 0; i < act.size(); i++)
      assertEquals(exp[i], act.get(i).getOrginalFragment());
  }

  private void assertStart(final List<Fragment> act, final int... exp) {
    for (int i = 0; i < act.size(); i++)
      assertEquals(exp[i], act.get(i).getFragmentStart());
  }

}
