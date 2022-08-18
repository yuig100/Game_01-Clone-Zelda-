package com.stonelame.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.stonelame.main.Game;

public class Tile {

	public static BufferedImage TILE_FLOOR = Game.spritesheet.getSprite(0, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage TILE_WALL = Game.spritesheet.getSprite(0, 16, World.TILE_SIZE, World.TILE_SIZE);

	public boolean show = false;
	
	private BufferedImage sprite;
	private int x, y;

	public Tile(int x, int y, BufferedImage sprite) {

		this.x = x;
		this.y = y;
		this.sprite = sprite;

	}

	public void render(Graphics g) {

		if(show) {
			
			g.drawImage(sprite, x - Camera.x, y - Camera.y, null);
			
		}

	}
}
