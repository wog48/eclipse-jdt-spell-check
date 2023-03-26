package camelcase.jdt.spelling.checker;

import java.util.Objects;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;

import camelcase.jdt.spelling.parser.Fragment;
import camelcase.jdt.spelling.parser.Token;

public class SpellingEvent {

  private final Fragment fragment;
  private final Token token;
  private final IJavaElement element;
  private IResource resource;

  SpellingEvent(final Fragment fragment, final Token token) {
    this.fragment = fragment;
    this.token = token;
    this.element = token.getElement();
  }

  public Fragment getFragment() {
    return fragment;
  }

  public Token getToken() {
    return token;
  }

  public IJavaElement getElement() {
    return element;
  }

  public IResource getResource() {
    return resource;
  }

  public void setResource(final IResource resource) {
    this.resource = resource;
  }

  public ISourceRange getSourceRange() throws JavaModelException {
    final IJavaElement javaElement = element;

    ISourceRange range = null;
    if (javaElement instanceof ISourceReference) {
      final ISourceReference sourceReference = (ISourceReference) javaElement;
      range = sourceReference.getNameRange();
    }

    int start = range == null ? 0 : range.getOffset();
    start += fragment.getFragmentStart();

    return new SourceRange(start, fragment.getLength());
  }

  @Override
  public String toString() {
    return "SpellingEvent [token=" + fragment + ", term=" + token + ", elementType=" + element.getElementType() + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(element, token, fragment);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final SpellingEvent other = (SpellingEvent) obj;
    return Objects.equals(element, other.element) && Objects.equals(token, other.token) && Objects.equals(fragment,
        other.fragment);
  }

}
