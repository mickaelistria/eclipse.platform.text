package org.eclipse.ui.editors.tests;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.ui.texteditor.AbstractTextEditor;

public class StylingTest {
	
	private static IProject project;
	private static IFile file;
	private AbstractTextEditor editor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
		project.create(new NullProgressMonitor());
		project.open(new NullProgressMonitor());
		file = project.getFile("foo.txt");
		file.create(new ByteArrayInputStream("'test'".getBytes()), true, new NullProgressMonitor());
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
	public void testStyle() throws Exception {
		
		editor.selectAndReveal(0, 4);
		Thread.currentThread();
		Thread.sleep(4000);
		ITextSelection selection= (ITextSelection) editor.getSelectionProvider().getSelection();
		//somehow test that it is red
		
	}

}
