package camelcase.jdt.spelling.checker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
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
  private CompilationUnit compilationUnit;

  public SpellCheckVisitor(final ISpellChecker checker) {
    this.checker = checker;
    this.spellEvents = new ArrayList<>();
  }

  @Override
  public boolean visit(final CompilationUnit node) {
    this.compilationUnit = node;
    return super.visit(node);
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
    checkNode(node);
    return false;
  }

  @Override
  public boolean visit(final LambdaExpression node) {
    for (final Object parameter : node.parameters())
      checkNode((VariableDeclarationFragment) parameter);
    return false;
  }

  @Override
  public boolean visit(final EnumConstantDeclaration node) {
    final IVariableBinding binding = node.resolveVariable();
    final int lineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    triggerSpellCheck(binding, lineNumber);
    return false;
  }

  @Override
  public boolean visit(final EnumDeclaration node) {
    final int lineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    triggerSpellCheck(node.resolveBinding(), lineNumber);
    return true;
  }

  public List<SpellingEvent> getSpellEvents() {
    return spellEvents;
  }

  private void checkNode(final TypeDeclaration node) {
    final int lineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    triggerSpellCheck(node.resolveBinding(), lineNumber);
  }

  private void checkNode(final MethodDeclaration node) {
    final int lineNumber = compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    final IJavaElement element = triggerSpellCheck(node.resolveBinding(), lineNumber);
    if (element != null)
      this.methodResource = element.getResource();
  }

  private void checkNode(final VariableDeclaration declaration) {
    final int lineNumber = compilationUnit.getLineNumber(declaration.getStartPosition()) - 1;
    triggerSpellCheck(declaration.resolveBinding(), lineNumber);
  }

  private IJavaElement triggerSpellCheck(final IBinding binding, final int lineNumber) {
    if (binding != null && binding.getJavaElement() != null) {
      final IJavaElement element = binding.getJavaElement();
      final List<SpellingEvent> events = checker.checkElement(element);
      events.stream().forEach(event -> event.setResource(methodResource));
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
