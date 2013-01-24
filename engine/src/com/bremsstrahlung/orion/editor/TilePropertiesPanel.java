package com.bremsstrahlung.orion.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class TilePropertiesPanel extends Composite {
	public TilePropertiesPanel(Composite parent) {
		super(parent, SWT.NONE);
		
		setLayout(new FillLayout());
		
		final List list = new List(this, SWT.SINGLE | SWT.V_SCROLL);
		
		list.add("Tile 01");
		list.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				System.err.println(list.getSelectionIndex());
			}
			
			public void widgetDefaultSelected(SelectionEvent event) {
				System.err.println(list.getSelectionIndex());
			}
		});
	}
}
