package camelcase.jdt.spelling.parser;

import java.util.Locale;
import java.util.Objects;

public class Fragment {
  private String orginalFragment;
  private int fragmentStart;
  private final boolean isSplitter;

  Fragment(final String orginalFragment, final int end) {
    this.orginalFragment = orginalFragment;
    this.fragmentStart = end - orginalFragment.length();
    this.isSplitter = false;
  }

  public Fragment(final char orginalFragment, final int end, final boolean isSplitter) {
    this.orginalFragment = String.valueOf(orginalFragment);
    this.fragmentStart = end - 1;
    this.isSplitter = isSplitter;
  }

  public String getOrginalFragment() {
    return orginalFragment;
  }

  public boolean isSplitter() {
    return isSplitter;
  }

  public int getFragmentStart() {
    return fragmentStart;
  }

  public boolean isAllUpper() {
    return orginalFragment.toUpperCase(Locale.ENGLISH).equals(orginalFragment);
  }

  public boolean firstIsUpper() {
    final Character first = orginalFragment.charAt(0);
    return Character.toUpperCase(first) == first;
  }

  public boolean firstIsLower() {
    final Character first = orginalFragment.charAt(0);
    return Character.toLowerCase(first) == first;
  }

  void merge(final Fragment newToken) {
    if (newToken.getFragmentStart() < fragmentStart) {
      orginalFragment = newToken.getOrginalFragment() + orginalFragment;
      fragmentStart = newToken.getFragmentStart();
    } else
      orginalFragment = orginalFragment + newToken.getOrginalFragment();
  }

  public String getOrginalFragmentLower() {
    return orginalFragment.toLowerCase(Locale.ENGLISH);
  }

  public int getLength() {
    return orginalFragment.length();
  }

  public int getFragmentEnd() {
    return fragmentStart + orginalFragment.length();
  }

  @Override
  public int hashCode() {
    return Objects.hash(orginalFragment, fragmentStart);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Fragment other = (Fragment) obj;
    return Objects.equals(orginalFragment, other.orginalFragment) && fragmentStart == other.fragmentStart;
  }

  @Override
  public String toString() {
    return "Fragment [orginalFragment=" + orginalFragment + ", fragmentStart=" + fragmentStart + ", isSplitter="
        + isSplitter + "]";
  }

}
