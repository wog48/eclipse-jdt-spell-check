package camelcase.jdt.spelling.quickfix;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import camelcase.jdt.spelling.SpellingPlugin;
import camelcase.jdt.spelling.directory.DictionaryFactory;

public class AddWordProposal implements IJavaCompletionProposal {
  public static final String IMAGE_ID = "add_correction.gif";
  private static final int RELEVANCE = 7;
  private final String original;
  private final DictionaryFactory factory;

  public AddWordProposal(final String original) {
    super();
    this.original = original;
    this.factory = SpellingPlugin.getInstance().getDictionaryFactory();
  }

  @Override
  public void apply(final IDocument document) {
    factory.addToUserDirectory(original);

  }

  @Override
  public String getAdditionalProposalInfo() {
    return null;
  }

  @Override
  public IContextInformation getContextInformation() {
    return null;
  }

  @Override
  public String getDisplayString() {
    return original;
  }

  @Override
  public Image getImage() {
    return SpellingPlugin.getInstance().getImageRegistry().get(IMAGE_ID);
  }

  @Override
  public Point getSelection(final IDocument document) {
    return null;
  }

  @Override
  public int getRelevance() {
    return RELEVANCE;
  }

}
