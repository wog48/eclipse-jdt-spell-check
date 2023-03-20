package camelcase.jdt.spelling.checker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

class SpellCheckVisitor extends ASTVisitor {

  private final ISpellChecker checker;
  private final List<SpellingEvent> spellEvents;

  public SpellCheckVisitor(final ISpellChecker checker) {
    this.checker = checker;
    this.spellEvents = new ArrayList<>();
  }

  @Override
  public boolean visit(final TypeDeclaration node) {
    checkNode(node);
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean visit(final MethodDeclaration node) {
    checkNode(node);
    final List<SingleVariableDeclaration> parameters = node.parameters();
    for (final SingleVariableDeclaration declaration : parameters)
      checkNode(declaration);
    return true;
  }

  @Override
  public boolean visit(final VariableDeclarationStatement node) {
    for (final Object fragment : node.fragments())
      checkNode((VariableDeclaration) fragment);
    return false;
  }

  @Override
  public boolean visit(final FieldDeclaration node) {
    for (final Object fragment : node.fragments())
      checkNode((VariableDeclaration) fragment);
    return true;
  }

  public List<SpellingEvent> getSpellEvents() {
    return spellEvents;
  }

  private void checkNode(final TypeDeclaration node) {
    triggerSpellCheck(node.resolveBinding());
  }

  private void checkNode(final MethodDeclaration node) {
    triggerSpellCheck(node.resolveBinding());
  }

  private void checkNode(final VariableDeclaration declaration) {
    triggerSpellCheck(declaration.resolveBinding());
  }

  private void triggerSpellCheck(final IBinding binding) {
    if (binding != null) {
      final IJavaElement element = binding.getJavaElement();
      spellEvents.addAll(checker.checkElement(element));
    }
  }

  private void checkNode(final VariableDeclarationExpression initializer) {
    for (final Object fragment : initializer.fragments())
      checkNode((VariableDeclaration) fragment);
  }

  @Override
  public boolean visit(final ForStatement node) {
    for (final Object initializer : node.initializers())
      checkNode((VariableDeclarationExpression) initializer);
    return super.visit(node);
  }

  @Override
  public boolean visit(final WhileStatement node) {
    return super.visit(node);
  }
}
