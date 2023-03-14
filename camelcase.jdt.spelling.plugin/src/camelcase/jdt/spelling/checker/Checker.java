package camelcase.jdt.spelling.checker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.directory.IDirectory;
import camelcase.jdt.spelling.parser.Token;

class Checker implements SpellChecker {

  private final IDirectory directory;

  Checker(final IDirectory directory) {
    this.directory = directory;
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
    return exceuteCheck(token);
  }

  @Override
  public List<SpellingEvent> checkElement(final IJavaElement element) {
    return exceuteCheck(new Token(element));
  }

  private List<SpellingEvent> exceuteCheck(final Token token) {

    final List<SpellingEvent> result = new ArrayList<>();
    if (IJavaElement.COMPILATION_UNIT != token.getElement().getElementType()) {
      SpellingPlugin.debug("Check element " + token.getElement());
      token.getFragments().stream()
          .forEach(fragment -> {
            if (!fragment.isSplitter()
                && !directory.contains(fragment.getOrginalFragmentLower()))
              result.add(new SpellingEvent(fragment, token));
          });
    }
    return result;
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
}
