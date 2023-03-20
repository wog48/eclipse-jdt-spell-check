package camelcase.jdt.spelling.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.directory.IDirectory;
import camelcase.jdt.spelling.parser.Token;

class SpellChecker implements ISpellChecker {

  private final List<IDirectory> directories;
  private boolean ignoreSingleCharacter;

  SpellChecker(final List<IDirectory> directories) {
    this.directories = directories;
  }

  @Override
  public List<SpellingEvent> checkElement(final ICompilationUnit cu) {
    SpellingPlugin.debug("Check element " + cu);
    if (cu.getOwner() != null)
      return handleParentSourceReference(cu.getPrimary());
    else
      return handleParentSourceReference(cu);
  }

  @Override
  public void complete(final IResource resource) {
    // TODO Auto-generated method stub
  }

  @Override
  public List<SpellingEvent> checkElement(final Token token) {
    List<SpellingEvent> result = null;
    for (final IDirectory directory : directories) {
      final List<SpellingEvent> intermediateResult = executeCheck(token, directory);
      if (intermediateResult.isEmpty())
        return intermediateResult;
      if (result == null
          || intermediateResult.size() < result.size())
        result = intermediateResult;
    }
    return result;
  }

  @Override
  public List<SpellingEvent> checkElement(final IJavaElement element) {
    return checkElement(new Token(element));
  }

  private List<SpellingEvent> executeCheck(final Token token, final IDirectory directory) {

    final List<SpellingEvent> result = new ArrayList<>();
    if (IJavaElement.COMPILATION_UNIT != token.getElement().getElementType()) {
      SpellingPlugin.debug("Check element " + token.getElement());
      token.getFragments().stream()
          .forEach(fragment -> {
            if (!fragment.isSplitter()
                && !directory.contains(fragment.getOriginalFragmentLower())
                && !ignore(fragment.getOriginalFragment()))
              result.add(new SpellingEvent(fragment, token));
          });
    }
    return result;
  }

  private boolean ignore(final String fragment) {
    return ignoreSingleCharacter && fragment.length() <= 1;
  }

  private ASTNode createASTParser(final ICompilationUnit cu) {
    final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(cu);
    parser.setResolveBindings(true);
    return parser.createAST(null);
  }

  private List<SpellingEvent> handleParentSourceReference(final ICompilationUnit cu) {
    SpellingPlugin.getInstance().getLog().info("Check compilation unit " + cu);
    if (cu instanceof ISourceReference) {
      final SpellCheckVisitor visitor = new SpellCheckVisitor(this);
      createASTParser(cu).accept(visitor);
      return visitor.getSpellEvents();
    }
    return Collections.emptyList();
  }

  @Override
  public void ignoreSingleCharacter() {
    ignoreSingleCharacter = true;
  }

  @Override
  public void respectSingleCharacter() {
    ignoreSingleCharacter = false;
  }

  @Override
  public List<SpellingEvent> checkResource(final IResource resource) {
    final IJavaElement element = JavaCore.create(resource);
    final ICompilationUnit cu = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
    if (cu != null)
      return checkElement(cu);
    return Collections.emptyList();
  }
}
