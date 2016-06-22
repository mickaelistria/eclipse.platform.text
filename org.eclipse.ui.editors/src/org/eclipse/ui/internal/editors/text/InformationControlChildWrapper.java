package org.eclipse.ui.internal.editors.text;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IInformationControlExtension4;
import org.eclipse.jface.text.IInformationControlExtension5;

public class InformationControlChildWrapper implements IInformationControl, IInformationControlExtension, IInformationControlExtension2, IInformationControlExtension3, IInformationControlExtension4, IInformationControlExtension5 {

	private IInformationControl fCreateInformationControl;
	private int fI;

	public InformationControlChildWrapper(IInformationControl createInformationControl, int i) {
		fCreateInformationControl= createInformationControl;
		fI= i;
	}

	@Override
	public void setInformation(String information) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// TODO Auto-generated method stub

	}

	@Override
	public Point computeSizeHint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocation(Point location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDisposeListener(DisposeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDisposeListener(DisposeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setForegroundColor(Color foreground) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBackgroundColor(Color background) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFocusControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFocusListener(FocusListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFocusListener(FocusListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsControl(Control control) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Point computeSizeConstraints(int widthInChars, int heightInChars) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStatusText(String statusFieldText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle computeTrim() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean restoresSize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean restoresLocation() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setInput(Object input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasContents() {
		// TODO Auto-generated method stub
		return false;
	}

}
