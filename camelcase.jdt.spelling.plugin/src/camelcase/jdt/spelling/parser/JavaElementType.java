package camelcase.jdt.spelling.parser;

import java.util.regex.Pattern;

import org.eclipse.jdt.core.IJavaElement;

enum JavaElementType {

  PACKAGE("\\.", 1),
  DEFAULT("(?<!^)((?<=[$_])|(?=[$_]))|(?<!^)(?=[A-Z0-9][a-z]*)", 0);

  public static JavaElementType convert(final IJavaElement javaElement) {
    switch (javaElement.getElementType()) {
    case IJavaElement.PACKAGE_DECLARATION:
      return JavaElementType.PACKAGE;
    }
    return JavaElementType.DEFAULT;
  }

  private final Pattern pattern;
  private final int delimiterLength;

  private JavaElementType(final String regex, final int length) {
    this.pattern = Pattern.compile(regex);
    this.delimiterLength = length;
  }

  Pattern getPattern() {
    return pattern;
  }

  public int getDelimiterLength() {
    return delimiterLength;
  }

}
