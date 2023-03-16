package camelcase.jdt.spelling.parser;

import java.util.Locale;
import java.util.Objects;

public class Fragment {
  private String originalFragment;
  private int fragmentStart;
  private final boolean isSplitter;

  Fragment(final String originalFragment, final int end) {
    this.originalFragment = originalFragment;
    this.fragmentStart = end - originalFragment.length();
    this.isSplitter = false;
  }

  public Fragment(final char ordinalFragment, final int end, final boolean isSplitter) {
    this.originalFragment = String.valueOf(ordinalFragment);
    this.fragmentStart = end - 1;
    this.isSplitter = isSplitter;
  }

  public String getOriginalFragment() {
    return originalFragment;
  }

  public boolean isSplitter() {
    return isSplitter;
  }

  public int getFragmentStart() {
    return fragmentStart;
  }

  public boolean isAllUpper() {
    return originalFragment.toUpperCase(Locale.ENGLISH).equals(originalFragment);
  }

  public boolean firstIsUpper() {
    final Character first = originalFragment.charAt(0);
    return Character.toUpperCase(first) == first;
  }

  public boolean firstIsLower() {
    final Character first = originalFragment.charAt(0);
    return Character.toLowerCase(first) == first;
  }

  void merge(final Fragment newToken) {
    if (newToken.getFragmentStart() < fragmentStart) {
      originalFragment = newToken.getOriginalFragment() + originalFragment;
      fragmentStart = newToken.getFragmentStart();
    } else
      originalFragment = originalFragment + newToken.getOriginalFragment();
  }

  public String getOriginalFragmentLower() {
    return originalFragment.toLowerCase(Locale.ENGLISH);
  }

  public int getLength() {
    return originalFragment.length();
  }

  public int getFragmentEnd() {
    return fragmentStart + originalFragment.length();
  }

  @Override
  public int hashCode() {
    return Objects.hash(originalFragment, fragmentStart);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Fragment other = (Fragment) obj;
    return Objects.equals(originalFragment, other.originalFragment) && fragmentStart == other.fragmentStart;
  }

  @Override
  public String toString() {
    return "Fragment [originalFragment=" + originalFragment + ", fragmentStart=" + fragmentStart + ", isSplitter="
        + isSplitter + "]";
  }

}
