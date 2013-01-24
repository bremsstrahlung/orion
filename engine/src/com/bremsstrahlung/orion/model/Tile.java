package com.bremsstrahlung.orion.model;

public class Tile {
	private Point position;
	private int tileId;
	
	public Tile() {
	}
	
	public Tile(Point position) {
		setPosition(position);
	}
	
	public Tile(Point position, int tileId) {
		setPosition(position);
		setTileId(tileId);
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public void setTileId(int tileId) {
		this.tileId = tileId;
	}
	
	public int getTileId() {
		return tileId;
	}
}
