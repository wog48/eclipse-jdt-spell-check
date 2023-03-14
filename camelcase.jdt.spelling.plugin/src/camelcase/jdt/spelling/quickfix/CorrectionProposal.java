package camelcase.jdt.spelling.quickfix;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;
import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchWindow;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.parser.Token;

@SuppressWarnings("restriction")
public class CorrectionProposal implements IJavaCompletionProposal {

  private static final int RELEVANCE = 8;

  private final Token proposal;
  private final AssistContext context;
  private final SimpleName term;

  public CorrectionProposal(final AssistContext context, final Token newToken, final SimpleName term) {
    this.proposal = newToken;
    this.context = context;
    this.term = term;
  }

  @Override
  public void apply(final IDocument document) {
    try {
      final IJavaElement javaElement = ((Name) context.getCoveringNode()).resolveBinding().getJavaElement();
      final RenameJavaElementDescriptor descriptor = createRenameDescriptor(javaElement, proposal.getOriginalToken());
      final RenameSupport renameSupport = RenameSupport.create(descriptor);
      final IWorkbenchWindow workbenchWindow = context.getEditor().getSite().getWorkbenchWindow();

      renameSupport.perform(workbenchWindow.getShell(), workbenchWindow);

//      if (IJavaRefactorings.RENAME_TYPE.equals(descriptor.getID()))
//        Plugin.getDefault().getSpellEngine().partActivated(
//            context.getEditor());

    } catch (InterruptedException | InvocationTargetException | CoreException e) {
      SpellingPlugin.error(e);
    }
  }

  @Override
  public Point getSelection(final IDocument document) {
    return null;
  }

  @Override
  public String getAdditionalProposalInfo() {
    return null;
  }

  @Override
  public String getDisplayString() {
    return proposal.getOriginalToken();
  }

  @Override
  public Image getImage() {
    return SpellingPlugin.getInstance().getImageRegistry().get(SpellingPlugin.IMAGE_ID);
  }

  @Override
  public IContextInformation getContextInformation() {
    return null;
  }

  @Override
  public int getRelevance() {
    return RELEVANCE;
  }

  private String determineRefactoringType(final IJavaElement javaElement) throws JavaModelException {
    switch (javaElement.getElementType()) {
    case IJavaElement.JAVA_PROJECT:
      return IJavaRefactorings.RENAME_JAVA_PROJECT;
    case IJavaElement.PACKAGE_DECLARATION:
      return IJavaRefactorings.RENAME_PACKAGE;
    case IJavaElement.COMPILATION_UNIT:
      return IJavaRefactorings.RENAME_COMPILATION_UNIT;
    case IJavaElement.TYPE:
      return IJavaRefactorings.RENAME_TYPE;
    case IJavaElement.METHOD:
      return IJavaRefactorings.RENAME_METHOD;
    case IJavaElement.FIELD:
      final IField field = (IField) javaElement;
      if (field.isEnumConstant())
        return IJavaRefactorings.RENAME_ENUM_CONSTANT;
      else
        return IJavaRefactorings.RENAME_FIELD;
    case IJavaElement.TYPE_PARAMETER:
      return IJavaRefactorings.RENAME_TYPE_PARAMETER;
    case IJavaElement.LOCAL_VARIABLE:
      return IJavaRefactorings.RENAME_LOCAL_VARIABLE;
    default:
      return null;
    }
  }

  private RenameJavaElementDescriptor createRenameDescriptor(final IJavaElement javaElement, final String proposal)
      throws JavaModelException {
    final String contributionId = determineRefactoringType(javaElement);
    IJavaElement actualElement = javaElement;

    if (javaElement.getElementType() == IJavaElement.PACKAGE_DECLARATION)
      actualElement = javaElement.getAncestor(IJavaElement.PACKAGE_FRAGMENT);

    if (javaElement.getElementType() == IJavaElement.METHOD) {
      final IMethod method = (IMethod) javaElement;
      if (method.isConstructor())
        return createRenameDescriptor(method.getDeclaringType(), proposal);
    }

    return buildDescriptor(javaElement, proposal, contributionId, actualElement);
  }

  private RenameJavaElementDescriptor buildDescriptor(final IJavaElement javaElement, final String proposal,
      final String contributionId, final IJavaElement actualElement) {

    final RenameJavaElementDescriptor descriptor = new RenameJavaElementDescriptor(contributionId);
    descriptor.setProject(javaElement.getJavaProject().getElementName());
    descriptor.setJavaElement(actualElement);
    descriptor.setNewName(proposal);

    final int elementType = javaElement.getElementType();

    if (elementType != IJavaElement.PACKAGE_FRAGMENT_ROOT)
      descriptor.setUpdateReferences(true);

    switch (elementType) {
    case IJavaElement.PACKAGE_FRAGMENT:
      descriptor.setUpdateHierarchy(true);
    }

    switch (elementType) {
    case IJavaElement.PACKAGE_FRAGMENT:
    case IJavaElement.TYPE:
    case IJavaElement.FIELD:
      descriptor.setUpdateTextualOccurrences(true);
      break;
    }

    switch (elementType) {
    case IJavaElement.FIELD:
      descriptor.setRenameGetters(true);
      descriptor.setRenameSetters(true);
    }
    return descriptor;
  }

}
