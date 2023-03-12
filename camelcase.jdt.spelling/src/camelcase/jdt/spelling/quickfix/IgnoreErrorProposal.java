package camelcase.jdt.spelling.quickfix;

import org.eclipse.jdt.internal.ui.text.correction.AssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

@SuppressWarnings("restriction")
public class IgnoreErrorProposal implements IJavaCompletionProposal {

	private static final int RELEVANCE = 8;

	private final AssistContext context;

	public IgnoreErrorProposal(AssistContext context) {
		this.context = context;
	}

	@Override
	public void apply(IDocument document) {
		System.out.println("Apply");
	}

	@Override
	public Point getSelection(IDocument document) {
		return new Point(context.getOffset(), context.getLength());
	}

	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return "Ignore";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public int getRelevance() {
		return RELEVANCE;
	}

}
