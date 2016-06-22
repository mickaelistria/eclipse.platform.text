package org.eclipse.ui.editors.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * @since 3.11
 */
public class CompositeContentAssistProcessor implements IContentAssistProcessor {

	private List<IContentAssistProcessor> fContentAssistProcessors;

	public CompositeContentAssistProcessor(List<IContentAssistProcessor> contentAssistProcessors) {
		fContentAssistProcessors= contentAssistProcessors;
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		List<ICompletionProposal> res = new ArrayList<>();
		for (IContentAssistProcessor processor : this.fContentAssistProcessors) {
			res.addAll(Arrays.asList(processor.computeCompletionProposals(viewer, offset)));
		}
		return res.toArray(new ICompletionProposal[res.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		List<IContextInformation> res = new ArrayList<>();
		for (IContentAssistProcessor processor : this.fContentAssistProcessors) {
			res.addAll(Arrays.asList(processor.computeContextInformation(viewer, offset)));
		}
		return res.toArray(new IContextInformation[res.size()]);
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		StringBuilder res = new StringBuilder();
		for (IContentAssistProcessor processor : this.fContentAssistProcessors) {
			String errorMessage = processor.getErrorMessage();
			if (errorMessage != null) {
				res.append(errorMessage);
				res.append('\n');
			}
		}
		if (res.length() == 0) {
			return null;
		} else {
			return res.toString();
		}
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
