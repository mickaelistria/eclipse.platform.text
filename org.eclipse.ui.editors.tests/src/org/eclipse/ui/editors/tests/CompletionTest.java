/*******************************************************************************
 * Copyright (c) 2016 Red Hat Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mickael Istria (Red Hat Inc.)
 *******************************************************************************/
package org.eclipse.ui.editors.tests;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jface.bindings.keys.KeyStroke;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * @since 3.11
 *
 */
public class CompletionTest {

	private static IProject project;
	private static IFile file;
	private AbstractTextEditor editor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		project.create(new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		file = project.getFile("foo.txt");
		file.create(new ByteArrayInputStream("bar".getBytes()), true, new NullProgressMonitor());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		file.delete(true, new NullProgressMonitor());
		project.delete(true, new NullProgressMonitor());
	}

	@Before
	public void setUp() throws Exception {
		IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
		if (intro != null) {
			PlatformUI.getWorkbench().getIntroManager().closeIntro(intro);
		}

		IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
		editor = (AbstractTextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().openEditor(new FileEditorInput(file), desc.getId());
	}

	@After
	public void tearDown() throws Exception {
		editor.close(false);
		editor= null;
	}

	@Test
	public void testCompletion() throws Exception {
		Set<Shell> beforeShell = new HashSet<>(Arrays.asList(Display.getDefault().getShells()));
		editor.selectAndReveal(3, 0);
		ContentAssistAction action = (ContentAssistAction) editor.getAction(ITextEditorActionConstants.CONTENT_ASSIST);
		action.update();
		action.run();
		Set<Shell> afterShell = new HashSet<>(Arrays.asList(Display.getDefault().getShells()));
		afterShell.removeAll(beforeShell);
		Assert.assertEquals("No completion", 1, afterShell.size());
		Shell completionShell= afterShell.iterator().next();
		Table completionProposalList = findCompletionSelectionControl(completionShell);
		Assert.assertEquals(1, completionProposalList.getItemCount());
		TableItem completionProposalItem = completionProposalList.getItem(0);
		Assert.assertEquals("s are good for a beer.", ((ICompletionProposal)completionProposalItem.getData()).getDisplayString());
		// TODO find a way to actually trigger completion and verify result against Editor content
		// Assert.assertEquals("Completion didn't complete", "bars are good for a beer.", ((StyledText)editor.getAdapter(Control.class)).getText());
		completionShell.close();
	}
	
	private Table findCompletionSelectionControl(Widget control) {
		if (control instanceof Table) {
			return (Table)control; 
		} else if (control instanceof Composite) {
			for (Widget child : ((Composite)control).getChildren()) {
				Table res = findCompletionSelectionControl(child);
				if (res != null) {
					return res;
				}
			}
		}
		return null;
	}
	
	private void pressShortcut(TableItem completionWidget, KeyStroke ... keys) {
		Assert.assertNotNull(completionWidget);
		for (KeyStroke c : keys) {
			Event e = new Event();
			e.character = 0;
			e.keyCode = c.getNaturalKey();
			e.type = SWT.KeyDown;
			e.widget = completionWidget;
			e.item = completionWidget;
			completionWidget.getDisplay().post(e);
			completionWidget.getDisplay().wake();
		}
		List<KeyStroke> reversedKeys = new ArrayList<>(Arrays.asList(keys));
		Collections.reverse(reversedKeys);
		for (KeyStroke c : reversedKeys) {
			Event e = new Event();
			e.type = SWT.KeyUp;
			e.character = 0;
			e.keyCode = c.getNaturalKey();
			e.widget = completionWidget;
			e.item = completionWidget;
			completionWidget.getDisplay().post(e);
			completionWidget.getDisplay().wake();
		}
	}


}
