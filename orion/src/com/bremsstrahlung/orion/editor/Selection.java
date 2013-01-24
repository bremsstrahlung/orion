package com.bremsstrahlung.orion.editor;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class Selection {
	private int vboId;
	private float posX, posY;
	
	public Selection() {
		posX = 0;
		posY = 0;
		
		float[] coords = {
				 0.0f, -0.5f,
				-0.5f,  0.0f,
				 0.0f,  0.5f,				 
				 0.5f,  0.0f,
		};
		
		FloatBuffer verts = BufferUtils.createFloatBuffer(coords.length);
		verts.put(coords);
		verts.flip();
		
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,  0);
	}
	
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(posX, -posY, 0.0f);
		
//		if(posY % 2 == 0)
//			GL11.glTranslatef(-0.5f, 0.0f, 0.0f);
		
		GL11.glColor3f(0.0f, 0.0f, 0.0f);
		GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, 4);
		
		GL11.glPopMatrix();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
}
