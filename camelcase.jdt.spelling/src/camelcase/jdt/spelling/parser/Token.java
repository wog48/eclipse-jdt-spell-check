package camelcase.jdt.spelling.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaElement;

public class Token {

  private static char[] splitter = { '.', '_' };
  private static char STOP = '[';

  private final List<Fragment> fragments;
  private final IJavaElement element;

  public Token(final IJavaElement element) {
    this.element = element;
    this.fragments = element == null ? Collections.emptyList() : tokanize(element.getElementName());
  }

  private Token(final IJavaElement element, final List<Fragment> fragments) {
    this.element = element;
    this.fragments = fragments;
  }

  public List<Fragment> getFragments() {
    return fragments;
  }

  public IJavaElement getElement() {
    return element;
  }

  private List<Fragment> tokanize(final String name) {
    final List<Fragment> result = new ArrayList<>();
    Fragment previousFragment = null;
    if (element != null) {
      final char[] token = Arrays.copyOf(name.toCharArray(), name.length() + 1);
      token[token.length - 1] = STOP;
      char[] fragment = new char[token.length];
      int position = -1;
      for (final char next : token) {
        position++;
        if (splitter[0] == next || splitter[1] == next) {
          final Fragment newFragment = new Fragment(String.valueOf(fragment).trim(), position);
          if (previousFragment != null
              && previousFragment.isAllUpper()
              && newFragment.isAllUpper())
            previousFragment.merge(newFragment);
          else if (newFragment.getLength() > 0)
            result.add(newFragment);
          previousFragment = null;
          fragment = new char[token.length];
          result.add(new Fragment(next, position + 1, true));
          continue;
        } else if (Character.isUpperCase(next) || STOP == next) {
          final Fragment newFragment = new Fragment(String.valueOf(fragment).trim(), position);
          if (previousFragment != null
              && previousFragment.isAllUpper()
              && newFragment.isAllUpper())
            previousFragment.merge(newFragment);
          else if (newFragment.getLength() > 0) {
            result.add(newFragment);
            previousFragment = newFragment;
          }
          fragment = new char[token.length];
        }
        fragment[position] = next;
      }
    }
    return Collections.unmodifiableList(result);
  }

  public String getOriginalToken() {
    return fragments.stream()
        .map(Fragment::getOrginalFragment)
        .collect(Collectors.joining());
  }

  public Token replaceFragments(final Map<Fragment, String> replacments) {
    final StringBuilder result = new StringBuilder();
    for (final Fragment f : fragments)
      if (replacments.containsKey(f))
        result.append(adoptReplacement(replacments, f));
      else
        result.append(f.getOrginalFragment());
    return new Token(element, tokanize(result.toString()));
  }

  @Override
  public String toString() {
    return "Term [element=" + element + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(element, fragments);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Token other = (Token) obj;
    return Objects.equals(element, other.element) && Objects.equals(fragments, other.fragments);
  }

  private String adoptReplacement(final Map<Fragment, String> replacments, final Fragment f) {
    final String replacement = replacments.get(f);
    if (f.isAllUpper())
      return replacement.toUpperCase(Locale.ENGLISH);
    if (f.firstIsUpper()) {
      final char[] r = replacement.toCharArray();
      r[0] = Character.toUpperCase(r[0]);
      return String.valueOf(r);
    }

    return replacement;
  }

}
