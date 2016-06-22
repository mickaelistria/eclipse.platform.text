package org.eclipse.ui.editors.tests;

import java.io.ByteArrayInputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

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
		StyledText widget = (StyledText) editor.getAdapter(Control.class);
		StyleRange style= widget.getStyleRangeAtOffset(0);//get the style of first token
		boolean isRed= style.foreground.getRGB().equals(new RGB(255, 0, 0));//is it Red?
		Assert.assertTrue("Token is not of expected color", isRed);		
	}

}
