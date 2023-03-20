package camelcase.jdt.spelling.checker;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import camelcase.jdt.spelling.parser.Token;

public interface ISpellChecker {

  void complete(IResource resource);

  List<SpellingEvent> checkElement(ICompilationUnit create);

  List<SpellingEvent> checkElement(Token token);

  List<SpellingEvent> checkElement(IJavaElement create);

  void ignoreSingleCharacter();

  void respectSingleCharacter();

  List<SpellingEvent> checkResource(IResource r);

}
