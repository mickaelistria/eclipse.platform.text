/*******************************************************************************
 * Copyright (c) 2016 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Sopot Cela, Mickael Istria (Red Hat Inc.) - initial implementation
 *******************************************************************************/
package org.eclipse.ui.genericeditor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public final class ExtensionBasedTextViewerConfiguration extends TextSourceViewerConfiguration {

	private IEditorPart editor;
	private Set<IContentType> contentTypes;

	public ExtensionBasedTextViewerConfiguration(IEditorPart editor, IPreferenceStore preferenceStore) {
		super(preferenceStore);
		this.editor = editor;
	}

	private Set<IContentType> getContentTypes() {
		if (this.contentTypes == null) {
			this.contentTypes = new HashSet<>(Arrays.asList(Platform.getContentTypeManager().findContentTypesFor(editor.getEditorInput().getName())));
		}
		return this.contentTypes;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		TextHoverRegistry registry= GenericEditorPlugin.getDefault().getHoverRegistry();
		return registry.getAvailableHover(sourceViewer, getContentTypes());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistProcessorRegistry registry= GenericEditorPlugin.getDefault().getContentAssistProcessorRegistry();
		IContentAssistProcessor processor = new CompositeContentAssistProcessor(registry.getContentAssistProcessors(sourceViewer, getContentTypes()));
		ContentAssistant res= new ContentAssistant();
		res.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_BELOW);
		res.setProposalPopupOrientation(ContentAssistant.PROPOSAL_REMOVE);
		res.enableColoredLabels(true);
		res.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		res.setInformationControlCreator(new AbstractReusableInformationControlCreator() {
			@Override
			protected IInformationControl doCreateInformationControl(Shell parent) {
				return new DefaultInformationControl(parent);
			}
		});
		return res;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconcilerRegistry registry = GenericEditorPlugin.getDefault().getPresentationReconcilerRegistry();
		List<IPresentationReconciler> reconciliers = registry.getPresentationReconcilers(sourceViewer, getContentTypes());
		if (!reconciliers.isEmpty()) {
			return reconciliers.get(0);
		}
		return null;
	}

}
