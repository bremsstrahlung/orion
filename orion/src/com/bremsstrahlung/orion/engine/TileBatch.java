package com.bremsstrahlung.orion.engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import com.bremsstrahlung.orion.model.Point;
import com.bremsstrahlung.orion.model.Tile;

public class TileBatch {
	public static final int TILE_WIDTH = 64;
	public static final int TILE_HEIGHT = 32;
	
	private int vVboId;
	private int tVboId;
	private int numVertices;
	private int size;
	private Point bounds = new Point(0, 0);
	
	public TileBatch(Tile[] tiles) {
		this.size = tiles.length;
		
		FloatBuffer vertices = BufferUtils.createFloatBuffer(size * 12);
		FloatBuffer st = BufferUtils.createFloatBuffer(size * 12);
		
		for(int i = 0; i < size; ++i) {
			Point tilePos = tiles[i].getPosition();
			
			if(tilePos.x > bounds.x)
				bounds.x = tilePos.x;
			if(tilePos.y > bounds.y)
				bounds.y = tilePos.y;
			
			float[] tileCoords = generateTileCoords(tilePos);						
			vertices.put(tileCoords);
			
			float[] texCoords = generateTexCoords(0);			
			st.put(texCoords);
		}
		
		vertices.flip();
		st.flip();
		
		numVertices = size * 6;
		
		vVboId = GL15.glGenBuffers();			
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		
		tVboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, st, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
		
		return coords;
	}
	
	public void setTile(Point pos, int tileId) {
		System.out.println(pos.x + "," + pos.y);
		float[] texCoords = generateTexCoords(tileId);
		
		FloatBuffer st = BufferUtils.createFloatBuffer(12);
		
		st.put(texCoords);
		st.flip();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 12 * 4 * ((pos.y * 5) + pos.x), st);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public Point getBounds() {
		return bounds;
	}
	
	public void delete() {
		GL15.glDeleteBuffers(vVboId);
	}
	
	public void render() {
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vVboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, tVboId);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, numVertices);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);		
	}
	
	private float[] generateTileCoords(Point pos) {
		float xOffset = 0.0f;
		
		if(pos.y % 2 == 1)
			xOffset = TILE_WIDTH / 2;
		
		float[] coords = {
				(pos.x * TILE_WIDTH) + (TILE_WIDTH / 2) + xOffset, (pos.y * TILE_HEIGHT) / 2,
				(pos.x * TILE_WIDTH)                    + xOffset, (pos.y * TILE_HEIGHT) / 2 + (TILE_HEIGHT / 2),
				(pos.x * TILE_WIDTH) + (TILE_WIDTH / 2) + xOffset, (pos.y * TILE_HEIGHT) / 2 + (TILE_HEIGHT),
				
				(pos.x * TILE_WIDTH) + (TILE_WIDTH / 2) + xOffset, (pos.y * TILE_HEIGHT) / 2 + (TILE_HEIGHT),
				(pos.x * TILE_WIDTH) + (TILE_WIDTH)     + xOffset, (pos.y * TILE_HEIGHT) / 2 + (TILE_HEIGHT / 2),
				(pos.x * TILE_WIDTH) + (TILE_WIDTH / 2) + xOffset, (pos.y * TILE_HEIGHT) / 2,				
		};
		
		return coords;
	}	
}
