package com.stonelame.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.stonelame.main.Game;
import com.stonelame.world.Camera;
import com.stonelame.world.World;

public class Bullet extends Entity {

	private double dx, dy;
	private double speed = 4;
	private int life = 50, curLife = 0;

	private BufferedImage[] sprites;

	public Bullet(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);

		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, World.TILE_SIZE, World.TILE_SIZE); // direita
		sprites[1] = Game.spritesheet.getSprite(96, 32, World.TILE_SIZE, World.TILE_SIZE); // esquerda

		this.dx = dx;
		this.dy = dy;

	}

	public void tick() {
		
		if(World.isFreeDynamic((int)(x + (dx * speed)),(int) (y + (dy * speed)), 3, 3)) {
			
			x += dx * speed;
			y += dy * speed;
			
		} else {
			
			Game.bullets.remove(this);
			World.generateParticles(100, (int)x, (int)y);
			return;
			
		}
		
		curLife++;

		if (curLife == life) {

			Game.bullets.remove(this);
			return;

		}

	}

	public void render(Graphics g) {

		if (Game.player.right_dir == Game.player.dir) {

			g.drawImage(sprites[0], this.getX() - Camera.x, this.getY() - Camera.y, null);

		} else if (Game.player.left_dir == Game.player.dir) {

			g.drawImage(sprites[1], this.getX() - Camera.x, this.getY() - Camera.y, null);

		}

	}
}
