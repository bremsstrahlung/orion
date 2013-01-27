package com.bremsstrahlung.orion.editor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import com.bremsstrahlung.orion.model.Point;
import com.bremsstrahlung.orion.model.Tile;

public class Oreo implements Runnable {
	private Display display = null;
	private AreaEditor editor = null;
	private NewDialog newDialog = null;
	private TileSelector tileSelector = null;
	private Text textArea = null;
 
	public static void main(String[] args) {
		Oreo o = new Oreo();
		
		o.init();
	}
	
	private void buildFileMenu(Menu menu) {
		MenuItem fileMenuNewItem = new MenuItem(menu, SWT.PUSH);
		fileMenuNewItem.setText("&New...");
		
		MenuItem fileMenuOpenItem = new MenuItem(menu, SWT.PUSH);
		fileMenuOpenItem.setText("&Open...");
		
		MenuItem fileMenuSaveItem = new MenuItem(menu, SWT.PUSH);
		fileMenuSaveItem.setText("&Save");
		
		MenuItem fileMenuSaveAsItem = new MenuItem(menu, SWT.PUSH);
		fileMenuSaveAsItem.setText("Save As...");
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem fileMenuQuitItem = new MenuItem(menu, SWT.PUSH);
		fileMenuQuitItem.setText("&Quit");		
	}
	
	private void buildEditMenu(Menu menu) {
		MenuItem undoItem = new MenuItem(menu, SWT.PUSH);
		undoItem.setText("Undo");
		
		MenuItem redoItem = new MenuItem(menu, SWT.PUSH);
		redoItem.setText("&Redo");
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem cutItem = new MenuItem(menu, SWT.PUSH);
		cutItem.setText("Cut");
		
		MenuItem copyItem = new MenuItem(menu, SWT.PUSH);
		copyItem.setText("&Copy");
		
		MenuItem pasteItem = new MenuItem(menu, SWT.PUSH);
		pasteItem.setText("Paste");
	}
	
	private void init() {
		display = new Display();
		final Shell shell = new Shell(display);
		GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 2;
		shell.setLayout(shellLayout);
		
		newDialog = new NewDialog(shell);
		
		/* Menu */
		Menu menuBar = new Menu(shell, SWT.BAR);
		
		/* Menu: File */
		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		
		MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		fileMenuHeader.setText("&File");
		fileMenuHeader.setMenu(fileMenu);
		
		buildFileMenu(fileMenu);
		
		/* Menu: Edit */
		Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
		
		MenuItem editMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
		editMenuHeader.setText("&Edit");
		editMenuHeader.setMenu(editMenu);
		
		buildEditMenu(editMenu);
		
		shell.setMenuBar(menuBar);
		
		/* ToolBar */
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		GridData toolBarLayoutData = new GridData();
		toolBarLayoutData.horizontalSpan = 2;
		toolBar.setLayoutData(toolBarLayoutData);
		
		ToolItem toolBarNewItem = new ToolItem(toolBar, SWT.PUSH);
		toolBarNewItem.setText("New");
		
		ToolItem toolBarTileSetItem = new ToolItem(toolBar, SWT.PUSH);
		toolBarTileSetItem.setText("Load tileset");
		
		toolBarNewItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				Point areaSize = newDialog.open();
				
				if(areaSize != null) {
					Tile[] tiles = new Tile[areaSize.x * areaSize.y];
					for(int y = 0; y < areaSize.y; ++y) {
						for(int x = 0; x < areaSize.x; ++x) {
							tiles[y * areaSize.x + x] = new Tile(new Point(x, y));
						}
					}
					editor.createArea(tiles);
					System.err.println("Area: " + areaSize.x + "x" + areaSize.y);
				}
			}
		});
		
		toolBarTileSetItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open tileset");
				
				String selected = fd.open();
				
				try {
					loadTileSet(selected);
					tileSelector.loadTileSet(selected);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		toolBar.pack();
		
		/* Map and properties */
		SashForm form = new SashForm(shell, SWT.HORIZONTAL);
		GridData formLayoutData = new GridData(GridData.FILL, GridData.FILL, true, true);
		form.setLayoutData(formLayoutData);
		
		
		editor = new AreaEditor(form);
		editor.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editor.setTile(new Point(event.x, event.y), editor.getBrush());
			}
		});
		
		tileSelector = new TileSelector(form, SWT.NONE);
		tileSelector.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				editor.setBrush(event.index);
			}
		});
		
		form.setWeights(new int[] {8, 2});
		
		/* Status text */
		textArea = new Text(shell, SWT.MULTI);
		textArea.setText("Hello world");
		textArea.setEditable(false);
		GridData textLayoutData = new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1);
		textArea.setLayoutData(textLayoutData);
		
		shell.setText("Oreo map editor");
		shell.open();
		
		display.asyncExec(this);
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();		
	}
	
	private void loadTileSet(String filename) throws IOException {
		InputStream in = new FileInputStream(filename);
		
		ImageData raw = new ImageData(in);
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(raw.width * raw.height * raw.depth / 8);
		buffer.put(raw.data);
		buffer.rewind();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		int texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, raw.depth / 8, raw.width, raw.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);	
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);	
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);			
	}
	
	public void run() {
		GLCanvas canvas = editor.getCanvas();
		
		if(!canvas.isDisposed()) {
			canvas.setCurrent();
			
			try {
				GLContext.useContext(canvas);
			}
			catch(LWJGLException e) {
				e.printStackTrace();
			}
			
			editor.render();
			
			canvas.swapBuffers();
			org.lwjgl.opengl.Display.sync(60);
			display.asyncExec(this);
		}
	}
}
