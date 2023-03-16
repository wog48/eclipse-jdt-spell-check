package camelcase.jdt.spelling.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaModelException;
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
    final String exp = "camelcase.jdt.spelling.parser";
    final IJavaElement element = new JavaElement(exp);

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
    final IJavaElement element = new JavaElement("TestClass");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(2, act.size());
    assertStart(act, 0, 4);
    assertToken(act, "Test", "Class");
  }

  @Test
  void createTermOfMethod() {
    final IJavaElement element = new JavaElement("somthingSpecialDone");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 8, 15);
    assertToken(act, "somthing", "Special", "Done");
  }

  @Test
  void createTermOfMethodWithAbriviation() {
    final IJavaElement element = new JavaElement("somthingDONESpecial");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 8, 12);
    assertToken(act, "somthing", "DONE", "Special");
  }

  @Test
  void createTermOfVariable() {
    final String exp = "sometingToBeDon";
    final IJavaElement element = new JavaElement(exp);

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(4, act.size());
    assertStart(act, 0, 8, 10, 12);
    assertToken(act, "someting", "To", "Be", "Don");
    assertEquals(exp, cut.getOriginalToken());
  }

  @Test
  void createTermOfConstant() {
    final IJavaElement element = new JavaElement("TEST_SNAKE");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(3, act.size());
    assertStart(act, 0, 4, 5);
    assertToken(act, "TEST", "_", "SNAKE");
  }

  @Test
  void createTermOfParameterStarting() {
    final IJavaElement element = new JavaElement("_test");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(2, act.size());
    assertStart(act, 0, 1);
    assertToken(act, "_", "test");
  }

  @Test
  void replaceFragments() {
    final String exp = "sometingToBeDon";
    final IJavaElement element = new JavaElement(exp);

    cut = new Token(element);
    final List<Fragment> fragments = cut.getFragments();
    final Map<Fragment, String> replacments = new HashMap<>();
    replacments.put(fragments.get(0), "something");
    replacments.put(fragments.get(3), "done");

    final Token act = cut.replaceFragments(replacments);

    assertEquals("somethingToBeDone", act.getOriginalToken());

  }

  @Test
  void createTermOfParameterWithNumberEnd() {
    final IJavaElement element = new JavaElement("test1");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(2, act.size());
    assertToken(act, "test", "1");
    assertEquals("test1", cut.getOriginalToken());
  }

  @Test
  void createTermOfParameterWithNumberMiddle() {
    final IJavaElement element = new JavaElement("test09Done");

    cut = new Token(element);
    final List<Fragment> act = cut.getFragments();
    assertEquals(4, act.size());
    assertToken(act, "test", "0", "9", "Done");
    assertEquals("test09Done", cut.getOriginalToken());
  }

  private void assertToken(final List<Fragment> act, final String... exp) {
    for (int i = 0; i < act.size(); i++)
      assertEquals(exp[i], act.get(i).getOriginalFragment());
  }

  private void assertStart(final List<Fragment> act, final int... exp) {
    for (int i = 0; i < act.size(); i++)
      assertEquals(exp[i], act.get(i).getFragmentStart());
  }

  // Failed to use mockito: therefore dummy implementation
  private static class JavaElement implements IJavaElement {

    private final String name;

    public JavaElement(final String name) {
      this.name = name;
    }

    @Override
    public <T> T getAdapter(final Class<T> clazz) {
      return null;
    }

    @Override
    public boolean exists() {
      return false;
    }

    @Override
    public IJavaElement getAncestor(final int id) {
      return null;
    }

    @Override
    public String getAttachedJavadoc(final IProgressMonitor monitor) throws JavaModelException {
      return null;
    }

    @Override
    public IResource getCorrespondingResource() throws JavaModelException {
      return null;
    }

    @Override
    public String getElementName() {
      return name;
    }

    @Override
    public int getElementType() {
      return 0;
    }

    @Override
    public String getHandleIdentifier() {
      return null;
    }

    @Override
    public IJavaModel getJavaModel() {
      return null;
    }

    @Override
    public IJavaProject getJavaProject() {
      return null;
    }

    @Override
    public IOpenable getOpenable() {
      return null;
    }

    @Override
    public IJavaElement getParent() {
      return null;
    }

    @Override
    public IPath getPath() {
      return null;
    }

    @Override
    public IJavaElement getPrimaryElement() {
      return null;
    }

    @Override
    public IResource getResource() {
      return null;
    }

    @Override
    public ISchedulingRule getSchedulingRule() {
      return null;
    }

    @Override
    public IResource getUnderlyingResource() throws JavaModelException {
      return null;
    }

    @Override
    public boolean isReadOnly() {
      return false;
    }

    @Override
    public boolean isStructureKnown() throws JavaModelException {
      return false;
    }

  }
}
