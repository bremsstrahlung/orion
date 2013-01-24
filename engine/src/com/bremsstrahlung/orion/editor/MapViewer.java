package com.bremsstrahlung.orion.editor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import com.bremsstrahlung.orion.engine.TileBatch;
import com.bremsstrahlung.orion.model.Point;
import com.bremsstrahlung.orion.model.Tile;

public class MapViewer extends Composite {
	private GLCanvas canvas;
	private TileBatch tileBatch;
	private int tileSetId;
	private Rectangle bounds;
	private Matrix4f projection = new Matrix4f();
	private Matrix4f modelview = new Matrix4f();
	
	public MapViewer(Composite parent) {
		super(parent, SWT.NONE);
		
		setLayout(new FillLayout());
		
		GLData data = new GLData();
		data.doubleBuffer = true;
		
		canvas = new GLCanvas(this, SWT.NONE, data);
		canvas.setCurrent();
		
		try {
			GLContext.useContext(canvas);
		}
		catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		canvas.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				ortho(-5f, 5f, 10f, -10f, -1f, 1f);				
			}				
		});
		
		canvas.addListener(SWT.MouseMove, new Listener() {
			public void handleEvent(Event event) {
				Vector2f pos = screenToWorld(new Point(event.x, event.y));
				
				event.x = Math.round(pos.x);
				event.y = Math.round(pos.y);
				
				MapViewer.this.notifyListeners(SWT.MouseMove, event);
			}
		});
		
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0.0f);
	}
	
	private Vector2f screenToWorld(Point screen) {
		Vector2f world = new Vector2f();
		
		float[] invertedMatrix, transformMatrix, normalizedIn, worldOut;
		
		Vector4f normalized = new Vector4f(
				(float)screen.x * 2f / bounds.width - 1f,
				(float)screen.y * 2f / bounds.height - 1f,
				-1f,
				1f);
		
		Matrix4f transform = new Matrix4f();
		Matrix4f.mul(projection, modelview, transform);
		
		Matrix4f inverse = new Matrix4f();
		Matrix4f.invert(transform, inverse);
		
		Vector4f out = new Vector4f();
		Matrix4f.transform(inverse, normalized, out);
		
		world.x = out.x;
		world.y = out.y;
		
		return world;
	}
	
	private void ortho(float left, float right, float bottom, float top, float near, float far) {
		bounds = getClientArea();
		
		canvas.setCurrent();
		try {
			GLContext.useContext(canvas);
		}
		catch(LWJGLException e) {
			e.printStackTrace();
		}
		
		FloatBuffer projection = BufferUtils.createFloatBuffer(16);
		FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
		
		Matrix4f projMatrix = new Matrix4f();
		projMatrix.m00 = 2 / (right - left);
		projMatrix.m11 = 2 / (top - bottom);
		projMatrix.m22 = -2 / (far - near);
		projMatrix.m32 = (far + near) / (far -near);
		projMatrix.m30 = (right + left) / (right - left);
		projMatrix.m31 = (top + bottom) / (top - bottom);
		
		projMatrix.store(projection);
		projection.rewind();
		
		Matrix4f modelViewMatrix = new Matrix4f();
		modelViewMatrix.setIdentity();
		
		modelViewMatrix.store(modelView);
		modelView.rewind();
		
		GL11.glViewport(0, 0, bounds.width, bounds.height);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrix(projection);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadMatrix(modelView);
		
		this.projection = projMatrix;
		this.modelview = modelViewMatrix;
	}
	
	private void setTileSet(int tileSetId, int width, int numTiles, int tileWidth) {
		this.tileSetId = tileSetId;
	}
	
	public GLCanvas getCanvas() {
		return canvas;
	}
	
	public void createArea(Tile[] tiles) {
		if(tileBatch != null)
			tileBatch.delete();
		
		tileBatch = new TileBatch(tiles);
	}
	
	public void render() {
		if(tileBatch == null)
			return;
		
		float xOffset = -tileBatch.getBounds().x / 2f;
		float yOffset = -tileBatch.getBounds().y / 2f;
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(xOffset, yOffset, 0.0f);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		tileBatch.render();
		
		GL11.glPopMatrix();
	}
}
