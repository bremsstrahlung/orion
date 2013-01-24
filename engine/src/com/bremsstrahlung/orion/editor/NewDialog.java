package com.bremsstrahlung.orion.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.bremsstrahlung.orion.model.Point;

public class NewDialog extends Dialog {
	Point areaSize;
	private Shell parent;
	
	public NewDialog(Shell parent) {
		super(parent);		
	}
	
	public Point open() {
		final Shell shell = new Shell(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		shell.setText("New area");
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		
		shell.setLayout(layout);
		
		Label areaSizeLabel = new Label(shell, SWT.NONE);
		areaSizeLabel.setText("Area size:");
		
		final Text areaSizeXText = new Text(shell, SWT.SINGLE);
		areaSizeXText.setText("5");
		
		final Text areaSizeYText = new Text(shell, SWT.SINGLE);
		areaSizeYText.setText("10");
		
		final Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		
		final Button okButton = new Button(shell, SWT.NONE);
		okButton.setText("OK");
		
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				if(event.widget == okButton) {
					int x = Integer.parseInt(areaSizeXText.getText());
					int y = Integer.parseInt(areaSizeYText.getText());
					
					areaSize = new Point(x, y);
				}
				else {
					areaSize = null;
				}

				shell.close();
			}			
		};
		
		cancelButton.addListener(SWT.Selection, listener);
		okButton.addListener(SWT.Selection, listener);
		
		shell.setDefaultButton(okButton);
		
		shell.pack();		
		shell.open();
		
		Display display = shell.getDisplay();
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		
		return areaSize;
	}
	
	public Point getAreaSize() {
		return areaSize;
	}
}
