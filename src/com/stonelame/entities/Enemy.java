package com.stonelame.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.stonelame.main.Game;
import com.stonelame.main.Sound;
import com.stonelame.world.AStar;
import com.stonelame.world.Camera;
import com.stonelame.world.Vector2i;
import com.stonelame.world.World;

public class Enemy extends Entity {

	private int frames = 0, maxframes = 20, index = 0, maxindex = 1;
	private BufferedImage[] sprites;
	private double life = 10;
	private boolean isDamaged = false;
	private int damageFrame = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);

		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 32, World.TILE_SIZE, World.TILE_SIZE);
		sprites[1] = Game.spritesheet.getSprite(128, 32, World.TILE_SIZE, World.TILE_SIZE);

	}

	public void tick() {
		depth = 0;
		
		
		maskx = 6;
		masky = 6;
		mwidth = 10;
		mheight = 10;
		if (!isCollidingWithPlayer()) {

			if (path == null || path.size() == 0) {

				Vector2i start = new Vector2i((int) (x / World.TILE_SIZE), (int) (y / World.TILE_SIZE));
				Vector2i end = new Vector2i((int) (Game.player.x / World.TILE_SIZE),
						(int) (Game.player.y / World.TILE_SIZE));
				path = AStar.findPath(Game.world, start, end);
			}

		} else {

			if (Game.rand.nextInt(100) < 10) {

				Sound.hurt.play();
				Game.player.life -= Game.rand.nextInt(3);
				Game.player.isDamage = true;

			}

		}

		// maskx=8;masky=8;maskw=10;maskh=10;
		/*
		 * if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(),
		 * Game.player.getY()) < 110) {
		 * 
		 * if (this.isCollidingWithPlayer() == false) {
		 * 
		 * if ((int) x < Game.player.getX() && World.isFree((int) (x + speed),
		 * this.getY(), (int) z) && !isColliding((int) (x + speed), this.getY())) {
		 * 
		 * x += speed;
		 * 
		 * } else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed),
		 * this.getY(), (int) z) && !isColliding((int) (x - speed), this.getY())) {
		 * 
		 * x -= speed;
		 * 
		 * }
		 * 
		 * if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y -
		 * speed), (int) z) && !isColliding(this.getX(), (int) (y - speed))) {
		 * 
		 * y -= speed;
		 * 
		 * } else if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y
		 * + speed), (int) z) && !isColliding(this.getX(), (int) (y + speed))) {
		 * 
		 * y += speed;
		 * 
		 * } } else {
		 * 
		 * if (Game.rand.nextInt(100) < 10) {
		 * 
		 * Sound.hurtEffect.play(); Game.player.life -= Game.rand.nextInt(3);
		 * Game.player.isDamage = true;
		 * 
		 * }
		 * 
		 * } }
		 * 
		 */

		if (path == null || path.size() == 0) {

			Vector2i start = new Vector2i((int) (x / World.TILE_SIZE), (int) (y / World.TILE_SIZE));

			Vector2i end = new Vector2i((int) (Game.player.x / World.TILE_SIZE),
					(int) (Game.player.y / World.TILE_SIZE));

			path = AStar.findPath(Game.world, start, end);

		}

		if (new Random().nextInt() < 90) {

			followPath(path);

		}
		if (new Random().nextInt() < 5) {

			Vector2i start = new Vector2i((int) (x / World.TILE_SIZE), (int) (y / World.TILE_SIZE));

			Vector2i end = new Vector2i((int) (Game.player.x / World.TILE_SIZE),
					(int) (Game.player.y / World.TILE_SIZE));

			path = AStar.findPath(Game.world, start, end);

		}
		frames++;

		if (frames == maxframes) {

			frames = 0;
			index++;

			if (index > maxindex) {

				index = 0;
			}

		}

		collidingBullet();
		if (life <= 0) {

			destroySelf();
		}

		if (isDamaged) {

			this.damageCurrent++;

			if (this.damageCurrent == this.damageFrame) {

				this.damageCurrent = 0;
				this.isDamaged = false;

			}

		}

	}

	public void destroySelf() {

		Game.enemies.remove(this);
		Game.entities.remove(this);
		return;

	}

	public void collidingBullet() {

		for (int i = 0; i < Game.bullets.size(); i++) {

			Entity e = Game.bullets.get(i);

			if (e instanceof Bullet) {

				if (Entity.isColliding(this, e)) {

					isDamaged = true;
					life--;
					Game.bullets.remove(i);

					return;

				}

			}

		}
	}

	public boolean isCollidingWithPlayer() {

		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);

		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);

		return enemyCurrent.intersects(player);
	}

	public void render(Graphics g) {

		if (!isDamaged) {

			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);

		} else {

			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);

		}

		// g.setColor(Color.blue);
		// g.fillRect(this.getX() - Camera.x + maskx, this.getY() + masky - Camera.y,
		// maskw, maskh);

	}

}
