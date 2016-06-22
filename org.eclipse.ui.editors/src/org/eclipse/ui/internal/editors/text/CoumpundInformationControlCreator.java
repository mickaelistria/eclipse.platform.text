package org.eclipse.ui.internal.editors.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;

public class CoumpundInformationControlCreator implements IInformationControlCreator {

	private List<IInformationControlCreator> fChildren;

	public CoumpundInformationControlCreator(List<IInformationControlCreator> children) {
		fChildren= Collections.unmodifiableList(children);
	}

	@Override
	public IInformationControl createInformationControl(Shell parent) {
		List<InformationControlChildWrapper> controls = new ArrayList<>(fChildren.size());
		for (int i = 0; i < fChildren.size(); i++) {
			controls.add(new InformationControlChildWrapper(fChildren.get(i).createInformationControl(parent), i));
		}
		return null;
		//return new CoumpoundInformationControl(controls);
	}

}
