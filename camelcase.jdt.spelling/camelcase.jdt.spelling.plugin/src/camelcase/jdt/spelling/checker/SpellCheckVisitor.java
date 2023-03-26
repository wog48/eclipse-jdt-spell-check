package camelcase.jdt.spelling.checker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

class SpellCheckVisitor extends ASTVisitor {

  private final ISpellChecker checker;
  private final List<SpellingEvent> spellEvents;
  private IResource methodResource;

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

  @Override
  public boolean visit(final ForStatement node) {
    for (final Object initializer : node.initializers())
      checkNode((VariableDeclarationExpression) initializer);
    return super.visit(node);
  }

  @Override
  public boolean visit(final VariableDeclarationFragment node) {
    node.getLocationInParent();
    node.getStartPosition();
    checkNode(node);
    return false;
  }

  @Override
  public boolean visit(final LambdaExpression node) {
    for (final Object parameter : node.parameters())
      checkNode((VariableDeclarationFragment) parameter);
    return false;
  }

  public List<SpellingEvent> getSpellEvents() {
    return spellEvents;
  }

  private void checkNode(final TypeDeclaration node) {
    triggerSpellCheck(node.resolveBinding());
  }

  private void checkNode(final MethodDeclaration node) {
    final IJavaElement element = triggerSpellCheck(node.resolveBinding());
    if (element != null)
      this.methodResource = element.getResource();
  }

  private void checkNode(final VariableDeclaration declaration) {
    triggerSpellCheck(declaration.resolveBinding());
  }

  private IJavaElement triggerSpellCheck(final IBinding binding) {
    if (binding != null) {
      final IJavaElement element = binding.getJavaElement();
      final List<SpellingEvent> events = checker.checkElement(element);
      events.stream().forEach(e -> e.setResource(methodResource));
      spellEvents.addAll(events);
      return element;
    }
    return null;
  }

  private void checkNode(final VariableDeclarationExpression initializer) {
    for (final Object fragment : initializer.fragments())
      checkNode((VariableDeclaration) fragment);
  }

}
