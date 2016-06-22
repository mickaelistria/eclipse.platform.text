package org.eclipse.ui.texteditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.IEvaluationContext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;

public class ContentAssistProcessorRegistry {

	private static final String EXTENSION_POINT_ID = "org.eclipse.ui.workbench.texteditor.contentAssistProcessors"; //$NON-NLS-1$
	
	private static class ContentAssistProcessorExtension implements IContentAssistProcessor {
		private static final String ACTIVE_WHEN_NODE = "activeWhen"; //$NON-NLS-1$
		private static final String LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
		private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

		private IConfigurationElement extension;
		private Expression activeWhen;
		private String label;
		
		private IContentAssistProcessor delegate;
		
		private ContentAssistProcessorExtension(IConfigurationElement element) throws Exception {
			this.extension = element;
			IConfigurationElement[] activeWhenElements = extension.getChildren(ACTIVE_WHEN_NODE);
			if (activeWhenElements.length != 1) {
				throw new IllegalArgumentException(ACTIVE_WHEN_NODE + " node is mandatory and must be unique in " + element.getContributor().getName() + " extension to " + EXTENSION_POINT_ID); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.activeWhen = ExpressionConverter.getDefault().perform(activeWhenElements[0].getChildren()[0]);
			this.label = element.getAttribute(LABEL_ATTRIBUTE);
		}

		private IContentAssistProcessor getDelegate() {
			if (this.delegate == null) {
				try {
					this.delegate = (IContentAssistProcessor) extension.createExecutableExtension(CLASS_ATTRIBUTE);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			return delegate;
		}

		public boolean isActive(IEvaluationContext context) {
			try {
				return this.activeWhen.evaluate(context).equals(EvaluationResult.TRUE);
			} catch (CoreException ex) {
				TextEditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, TextEditorPlugin.PLUGIN_ID, ex.getMessage(), ex));
				return false;
			}
		}

		private boolean isActive() {
			IEvaluationContext context = PlatformUI.getWorkbench().getService(IHandlerService.class).getCurrentState();
			// TODO: more things in context: current resource name, content type, current editor id..., current partition
			return isActive(context);
		}

		private IContentAssistProcessor createProposal() throws CoreException {
			return (IContentAssistProcessor) this.extension.createExecutableExtension(CLASS_ATTRIBUTE);
		}
		

		@Override
		public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
			if (isActive()) {
				return getDelegate().computeCompletionProposals(viewer, offset);
			}
			return new ICompletionProposal[0];
		}

		@Override
		public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
			if (isActive()) {
				return getDelegate().computeContextInformation(viewer, offset);
			}
			return new IContextInformation[0];
		}

		@Override
		public char[] getCompletionProposalAutoActivationCharacters() {
			if (isActive()) {
				return getDelegate().getCompletionProposalAutoActivationCharacters();
			}
			return null;
		}

		@Override
		public char[] getContextInformationAutoActivationCharacters() {
			if (isActive()) {
				return getDelegate().getContextInformationAutoActivationCharacters();
			}
			return null;
		}

		@Override
		public String getErrorMessage() {
			if (isActive()) {
				return getDelegate().getErrorMessage();
			}
			return null;
		}

		@Override
		public IContextInformationValidator getContextInformationValidator() {
			if (isActive()) {
				return getDelegate().getContextInformationValidator();
			}
			return null;
		}
	}
	
	private Map<IConfigurationElement, ContentAssistProcessorExtension> extensions = new HashMap<>();
	private boolean outOfSync = true;
	
	public ContentAssistProcessorRegistry() {
		Platform.getExtensionRegistry().addRegistryChangeListener(new IRegistryChangeListener() {
			@Override
			public void registryChanged(IRegistryChangeEvent event) {
				outOfSync = true;
			}
		}, EXTENSION_POINT_ID);
	}
	
	public List<IContentAssistProcessor> getContentAssistProcessors(ISourceViewer sourceViewer) {
		if (this.outOfSync) {
			sync();
		}
		List<IContentAssistProcessor> res = new ArrayList<>(this.extensions.values());
		return res;
	}

	private void sync() {
		Set<IConfigurationElement> toRemoveExtensions = new HashSet<>(this.extensions.keySet());
		for (IConfigurationElement extension : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID)) {
			toRemoveExtensions.remove(extension);
			if (!this.extensions.containsKey(extension)) {
				try {
					this.extensions.put(extension, new ContentAssistProcessorExtension(extension));
				} catch (Exception ex) {
					TextEditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, TextEditorPlugin.PLUGIN_ID, ex.getMessage(), ex));
				}
			}
		}
		for (IConfigurationElement toRemove : toRemoveExtensions) {
			this.extensions.remove(toRemove);
		}
	}
}
