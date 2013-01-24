package com.bremsstrahlung.orion.engine;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import com.bremsstrahlung.orion.model.Tile;

public class Engine {
	private boolean isRunning = false;
	private Tile t;
	
	public Engine() {
		
	}
	
	public void init() {
		try {
			Display.setDisplayMode(new DisplayMode(640, 640));
			Display.create();
			
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(-10, 10, 5, -5, -0.5f, 0.5f);
			
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			
			GL11.glClearColor(0.4f, 0.6f, 0.9f, 0.0f);
		}
		catch(LWJGLException e) {
			System.err.println("LWJGL error: " + e);
		}
		
		t = new Tile();
	}
	
	public void run() {
		isRunning = true;
		
		while(isRunning) {
			if(!Display.isCloseRequested()) {
				render();
				
				Display.sync(60);
				Display.update();
			}
			else {
				isRunning = false;
			}
		}
		
		Display.destroy();
	}
	
	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL11.glPushMatrix();
		GL11.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
		
		GL11.glPopMatrix();
	}
	
	public static void main(String[] args) {
		Engine engine = new Engine();
		
		engine.init();
		engine.run();
	}
}
