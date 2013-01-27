package com.bremsstrahlung.orion.editor;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.bremsstrahlung.orion.engine.TileBatch;

public class Selection {
	private int vVboId;
	private int tVboId;
	private float posX, posY;
	private int brush;
	
	public Selection() {
		posX = 0f;
		posY = 0f;
				
		float[] coords = {
				TileBatch.TILE_WIDTH / 2, 0,
				0.0f, TileBatch.TILE_HEIGHT / 2,
				TileBatch.TILE_WIDTH / 2, TileBatch.TILE_HEIGHT,
				
				TileBatch.TILE_WIDTH / 2, TileBatch.TILE_HEIGHT,
				TileBatch.TILE_WIDTH, TileBatch.TILE_HEIGHT / 2,
				TileBatch.TILE_WIDTH / 2, 0,
		};
		
		FloatBuffer verts = BufferUtils.createFloatBuffer(coords.length);
		verts.put(coords);
		verts.flip();
		
		float[] texCoords = generateTexCoords(1);
		
		FloatBuffer st = BufferUtils.createFloatBuffer(texCoords.length);
		st.put(texCoords);
		st.flip();
		
		vVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW);
		
		tVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, st, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,  0);
	}
	
	private float[] generateTexCoords(int tileId) {
		float tileWidth =  ((TileBatch.TILE_WIDTH * 2.0f) / 640.0f);
		float tileHeight = ((TileBatch.TILE_HEIGHT * 2.0f) / 512.0f);
		
		float[] coords = {
				tileWidth * tileId + tileWidth / 2.0f, 0.0f,
				tileWidth * tileId, tileHeight / 2.0f,
				tileWidth * tileId + tileWidth / 2.0f, tileHeight,

				tileWidth * tileId + tileWidth / 2.0f, tileHeight,
				tileWidth * tileId + tileWidth, tileHeight / 2.0f,
				tileWidth * tileId + tileWidth / 2.0f, 0.0f,
				
		};
//				0.9f, 0.0f,
//				0.8f, 0.0675f,
//				0.9f, 0.125f,
//				
//				0.9f, 0.0f,
//				0.9f, 0.125f,
//				1.0f, 0.0675f,					
		
		return coords;
	}
	
	public void setBrush(int brush) {
		this.brush = brush;
		
		System.out.println("updating texCoords");
		float[] texCoords = generateTexCoords(brush);
		
		FloatBuffer st = BufferUtils.createFloatBuffer(texCoords.length);
		st.put(texCoords);
		st.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, st, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,  0);
	}
	
	public void setPosition(float x, float y) {
		posX = x;
		posY = y;
		
		System.out.println("[" + posX + "," + posY + "]");
	}
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vVboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glTranslatef(posX * TileBatch.TILE_WIDTH, posY * TileBatch.TILE_HEIGHT / 2, 0.0f);

		if(posY % 2 == 1)
			GL11.glTranslatef(TileBatch.TILE_WIDTH / 2, 0f, 0f);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
		GL11.glPopMatrix();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}
}
