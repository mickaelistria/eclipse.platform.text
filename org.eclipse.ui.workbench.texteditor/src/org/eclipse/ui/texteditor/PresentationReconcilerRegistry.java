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
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.internal.texteditor.TextEditorPlugin;

/**
 * @since 3.11
 */
public class PresentationReconcilerRegistry {
	
	private static final String EXTENSION_POINT_ID = "org.eclipse.ui.workbench.texteditor.presentationReconcilers"; //$NON-NLS-1$
	
	private static class PresentationReconcilerExtension implements IPresentationReconciler {
		private static final String ACTIVE_WHEN_NODE = "activeWhen"; //$NON-NLS-1$
		private static final String LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
		private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
		
		private IConfigurationElement extension;
		private Expression activeWhen;
		private String label;
		
		private IPresentationReconciler delegate;
		
		private PresentationReconcilerExtension(IConfigurationElement element) throws Exception {
			this.extension = element;
			IConfigurationElement[] activeWhenElements = extension.getChildren(ACTIVE_WHEN_NODE);
			if (activeWhenElements.length != 1) {
				throw new IllegalArgumentException(ACTIVE_WHEN_NODE + " node is mandatory and must be unique in " + element.getContributor().getName() + " extension to " + EXTENSION_POINT_ID); //$NON-NLS-1$ //$NON-NLS-2$
			}
			this.activeWhen = ExpressionConverter.getDefault().perform(activeWhenElements[0].getChildren()[0]);
			this.label = element.getAttribute(LABEL_ATTRIBUTE);
		}
		
		private IPresentationReconciler getDelegate() {
			if (this.delegate == null) {
				try {
					this.delegate = (PresentationReconciler) extension.createExecutableExtension(CLASS_ATTRIBUTE);
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

		@Override
		public void install(ITextViewer viewer) {
			getDelegate().install(viewer);
			
		}

		@Override
		public void uninstall() {
			getDelegate().uninstall();
		}

		@Override
		public IPresentationDamager getDamager(String contentType) {
			return getDelegate().getDamager(contentType);
			
		}

		@Override
		public IPresentationRepairer getRepairer(String contentType) {
			return getDelegate().getRepairer(contentType);
		}

	}
	private Map<IConfigurationElement, PresentationReconcilerExtension> extensions = new HashMap<>();
	private boolean outOfSync = true;
	
	public PresentationReconcilerRegistry() {
		Platform.getExtensionRegistry().addRegistryChangeListener(new IRegistryChangeListener() {
			@Override
			public void registryChanged(IRegistryChangeEvent event) {
				outOfSync = true;
			}
		}, EXTENSION_POINT_ID);
	}
	
	public List<IPresentationReconciler> getPresentationReconcilers(ISourceViewer sourceViewer) {
		if (this.outOfSync) {
			sync();
		}
		List<IPresentationReconciler> res = new ArrayList<IPresentationReconciler>(this.extensions.values());
		return res;
	}

	private void sync() {
		Set<IConfigurationElement> toRemoveExtensions = new HashSet<>(this.extensions.keySet());
		for (IConfigurationElement extension : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID)) {
			toRemoveExtensions.remove(extension);
			if (!this.extensions.containsKey(extension)) {
				try {
					this.extensions.put(extension, new PresentationReconcilerExtension(extension));
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
