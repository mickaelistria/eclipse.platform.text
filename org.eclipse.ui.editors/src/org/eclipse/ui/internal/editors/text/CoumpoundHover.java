package org.eclipse.ui.internal.editors.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;

public class CoumpoundHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {

	private List<ITextHover> members;
	
	public CoumpoundHover(List<ITextHover> members) {
		this.members = Collections.unmodifiableList(members);
	}
	
	@Override
	@Deprecated
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return String.valueOf(getHoverInfo(textViewer, hoverRegion));
	}
	
	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		List<IInformationControlCreator> children = new ArrayList<>(members.size());
		for (ITextHover member : this.members) {
			IInformationControlCreator creator = null;
			if (member instanceof ITextHoverExtension) {
				creator = ((ITextHoverExtension)member).getHoverControlCreator();
			}
			if (creator == null) {
				creator =  new IInformationControlCreator() {
					@Override
					public IInformationControl createInformationControl(Shell shell) {
						return new DefaultInformationControl(shell, true);
					}
				};
			}
			children.add(creator);
		}
		return new CoumpundInformationControlCreator(children);
	}

}
