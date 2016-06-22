/*******************************************************************************
 * Copyright (c) 2007, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.texteditor;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;



/**
 * Text hover registry that manages the detectors
 * contributed by the <code>org.eclipse.ui.workbench.texteditor.hoverProvder</code> extension point for
 * targets contributed by the <code>org.eclipse.ui.workbench.texteditor.hyperlinkDetectorTargets</code> extension point.
 *
 * @since 3.11
 */
public final class TextHoverRegistry {


	public TextHoverRegistry(IPreferenceStore preferenceStore) {
	}

	public List<ITextHover> getAvailableHover(ISourceViewer sourceViewer) {
		return Collections.emptyList();
	}

}
