package com.bremsstrahlung.orion.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TileSelector extends Composite {
	private Table table;
	private TableColumn column1;
	private Image[] tiles;
	
	public TileSelector(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout());
		
		table = new Table(this, SWT.BORDER);
		
		column1 = new TableColumn(table, SWT.NONE);
		
		column1.pack();
	}

	public void loadTileSet(String filename) {
		if(tiles != null) {
			for(int i = 0; i < tiles.length; ++i)
				tiles[i].dispose();
		}
		
		table.clearAll();
		
		Image tileSet = new Image(getDisplay(), filename);
		
		Rectangle bounds = tileSet.getBounds();
		
		int numTiles = 18; //bounds.width / 32 * bounds.height / 16;
		
		tiles = new Image[numTiles];
		
		for(int i = 0; i < numTiles; ++i) {
			tiles[i] = new Image(getDisplay(), 128, 64);
			
			GC gc = new GC(tiles[i]);
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			
			int x = (i % 5) * 128;
			int y = (i / 5) * 64;
			
			System.out.println(x + "," + y);
			gc.drawImage(tileSet, x, y, 128, 64, 0, 0, 128, 64);
			
			gc.dispose();
			
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(new Integer(i));
			item.setImage(tiles[i]);
		}
				
		column1.pack();
		
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem[] selection = table.getSelection();
				
				event.index = (Integer)selection[0].getData();
				notifyListeners(SWT.Selection, event);
			}
		});		
	}
}
