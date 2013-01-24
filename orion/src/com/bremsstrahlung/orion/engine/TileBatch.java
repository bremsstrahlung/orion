package com.bremsstrahlung.orion.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.bremsstrahlung.orion.model.Point;
import com.bremsstrahlung.orion.model.Tile;

public class TileBatch {
	private static final int TILE_WIDTH = 1;
	private static final int TILE_HEIGHT = 1;
	
	private int vboId;
	private int numVertices;
	private int size;
	private Point bounds = new Point(0, 0);
	
	public TileBatch(Tile[] tiles) {
		this.size = tiles.length;
		
		FloatBuffer vertices = BufferUtils.createFloatBuffer(size * 12);
		
		for(int i = 0; i < size; ++i) {
			Point tilePos = tiles[i].getPosition();
			
			System.out.println("tile[" + tilePos.x + "," + tilePos.y + "]");
			if(tilePos.x > bounds.x)
				bounds.x = tilePos.x;
			if(tilePos.y > bounds.y)
				bounds.y = tilePos.y;
			
			float[] tileCoords = generateTileCoords(tilePos);
			
			vertices.put(tileCoords);
		}
		
		vertices.flip();
		
		numVertices = size * 6;
		
		vboId = GL15.glGenBuffers();			
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public Point getBounds() {
		return bounds;
	}
	
	public void delete() {
		GL15.glDeleteBuffers(vboId);
	}
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numVertices);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		
	}
	
	private float[] generateTileCoords(Point pos) {
		float xOffset = 0.0f;
		
		if(pos.y % 2 == 1)
			xOffset = 0.5f;
		
		float[] coords = {
				pos.x + 0.5f + xOffset,	pos.y,
				pos.x +        xOffset,	pos.y + 0.5f,
				pos.x + 0.5f + xOffset, pos.y + 1.0f,
				
				pos.x + 0.5f + xOffset, pos.y + 1.0f,
				pos.x + 1.0f + xOffset,	pos.y + 0.5f,
				pos.x + 0.5f + xOffset,	pos.y,				
		};
		
		return coords;
	}	
}
