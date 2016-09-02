/*******************************************************************************
 * Copyright (c) 2016 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * - Mickael Istria (Red Hat Inc.)
 *******************************************************************************/
package org.eclipse.ui.genericeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

/**
 * @since 3.11
 */
public class ContentAssistProcessorRegistry {

	private static final String EXTENSION_POINT_ID = GenericEditorPlugin.BUNDLE_ID + ".contentAssistProcessors"; //$NON-NLS-1$
	private static class ContentAssistProcessorExtension implements IContentAssistProcessor {
		private static final String CONTENT_TYPE_ATTRIBUTE = "contentType"; //$NON-NLS-1$
		private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

		private IConfigurationElement extension;
		private IContentType targetContentType;

		private IContentAssistProcessor delegate;

		private ContentAssistProcessorExtension(IConfigurationElement element) throws Exception {
			this.extension = element;
			this.targetContentType = Platform.getContentTypeManager().getContentType(element.getAttribute(CONTENT_TYPE_ATTRIBUTE));
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

		public boolean isActive() {
			IEditorInput input = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			IContentTypeManager contentTypeManager= Platform.getContentTypeManager();
			for (IContentType currentContentType : contentTypeManager.findContentTypesFor(input.getName())) {
				if (currentContentType.isKindOf(targetContentType)) {
					return true;
				}
			}
			return false;
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

	public List<IContentAssistProcessor> getContentAssistProcessors(ISourceViewer sourceViewer, Set<IContentType> contentTypes) {
		if (this.outOfSync) {
			sync();
		}
		List<IContentAssistProcessor> res = new ArrayList<>();
		for (ContentAssistProcessorExtension ext : this.extensions.values()) {
			if (contentTypes.contains(ext.targetContentType)) {
				res.add(ext);
			}
		}
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
					GenericEditorPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, GenericEditorPlugin.BUNDLE_ID, ex.getMessage(), ex));
				}
			}
		}
		for (IConfigurationElement toRemove : toRemoveExtensions) {
			this.extensions.remove(toRemove);
		}
		this.outOfSync = false;
	}
}
