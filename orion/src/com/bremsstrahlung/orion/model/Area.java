package com.bremsstrahlung.orion.model;

public class Area {
	Tile[] tiles;
	Rectangle bounds = new Rectangle();
	String tileset;
	
	public Area(Rectangle bounds, Tile[] tiles) {
		this.bounds.width = bounds.width;
		this.bounds.height = bounds.height;
		this.tiles = tiles;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void setTileset(String tileset) {
		this.tileset = tileset;
	}
	
	public String getTileset() {
		return tileset;
	}
	
	public Tile getTileAt(Point pos) {
		return tiles[pos.y * bounds.width + pos.x];
	}
}
