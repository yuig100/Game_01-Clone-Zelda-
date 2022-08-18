package com.stonelame.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.stonelame.main.Game;
import com.stonelame.world.Camera;
import com.stonelame.world.World;

public class Player extends Entity {

	public boolean right, up, left, down;
	public double speed = 1.4;
	public int right_dir = 0, left_dir = 1, dir = right_dir;

	// JUMP FAKE
	public boolean jump = false, isJumping = false;
	public int z = 0;
	public int jumpFrames = 50, jumpCur = 0, jumpSpeed = 2;
	public boolean jumpUp = false, jumpDown = false;
	// JUMP FAKE \\

	private int frames = 0, maxframes = 5, index = 0, maxindex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	private BufferedImage[] damagePlayer;

	private boolean gun = false;

	public boolean isDamage = false;
	private int damageframes = 0;

	public boolean shoot = false;
	public boolean mouseShoot = false;

	public static int ammo = 0;

	public double life = 100, maxlife = 100;

	public int mx, my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		damagePlayer = new BufferedImage[2];
		damagePlayer[0] = Game.spritesheet.getSprite(32, 32, World.TILE_SIZE, World.TILE_SIZE);
		damagePlayer[1] = Game.spritesheet.getSprite(48, 32, World.TILE_SIZE, World.TILE_SIZE);

		for (int i = 0; i < 4; i++) {

			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * World.TILE_SIZE), 0, World.TILE_SIZE,
					World.TILE_SIZE);
			leftPlayer[i] = Game.spritesheet.getSprite(80 - (i * World.TILE_SIZE), World.TILE_SIZE, World.TILE_SIZE,
					World.TILE_SIZE);
		}

	}
	
	public void revealMap() {
		
		int xx = (int)(x / World.TILE_SIZE);
		int yy = (int)(y / World.TILE_SIZE);
		
		
		World.tiles[xx + yy * World.WIDTH].show = true;
		World.tiles[xx + (yy+1) * World.WIDTH].show = true;
		World.tiles[xx  + (yy-1) * World.WIDTH].show = true;
		
		World.tiles[(xx - 1) + yy * World.WIDTH].show = true;
		World.tiles[(xx - 1) + (yy-1) * World.WIDTH].show = true;
		World.tiles[(xx - 1) + (yy+1) * World.WIDTH].show = true;		
		
		World.tiles[(xx + 1) + yy * World.WIDTH].show = true;
		World.tiles[(xx + 1) + (yy+1) * World.WIDTH].show = true;
		World.tiles[(xx + 1) + (yy-1) * World.WIDTH].show = true;	
		

	}
	
	public void tick() {
		
		revealMap();
		
		depth = 1;
		
		if (jump) {

			if (isJumping == false) {

				jump = false;
				isJumping = true;
				jumpUp = true;
			}

		}

		if (isJumping == true) {

			if (jumpUp) {

				jumpCur += jumpSpeed;

			} else if (jumpDown) {

				jumpCur -= jumpSpeed;

				if (jumpCur <= 0) {

					isJumping = false;
					jumpDown = false;
					jumpUp = false;
				}

			}

			z = jumpCur;

			if (jumpFrames <= jumpCur) {

				jumpUp = false;
				jumpDown = true;

			}

		}

		moved = false;
		if (right && World.isFree((int) (x + speed), this.getY(), z)) {
			moved = true;
			dir = right_dir;
			x += speed;

		} else if (left && World.isFree((int) (x - speed), this.getY(), z)) {
			moved = true;
			dir = left_dir;
			x -= speed;

		}

		if (up && World.isFree(this.getX(), (int) (y - speed), z)) {
			moved = true;
			y -= speed;

		} else if (down && World.isFree(this.getX(), (int) (y + speed), z)) {
			moved = true;
			y += speed;

		}

		if (moved) {

			frames++;
			if (frames == maxframes) {

				frames = 0;
				index++;

				if (index > maxindex) {

					index = 0;
				}

			}

		}

		this.checkCollisionGun();
		this.checkCollisionLifePack();
		this.checkCollisionAmmo();

		if (isDamage) {

			this.damageframes++;
			if (this.damageframes == 8) {

				this.damageframes = 0;
				isDamage = false;
			}

		}

		if (shoot) {
			shoot = false;

			if (gun && ammo > 0) {

				ammo--;

				int dx = 1;
				int px = 18;
				int py = 1;

				if (dir == right_dir) {

					px = 18;

					dx = 1;

				} else if (dir == left_dir) {

					px = -18;

					dx = -1;

				}
				Bullet bullet = new Bullet(this.getX() + px, this.getY() + py, 16, 16, null, dx, 0);
				Game.bullets.add(bullet);
			}
		}
		if (mouseShoot) {

			mouseShoot = false;

			if (gun && ammo > 0) {

				ammo--;

				int px = 0;
				int py = 0;

				double angle = 0;

				if (dir == right_dir) {

					px = 18;
					py = 0;

					angle = Math.atan2(my - (this.getY() + py - Camera.y) - 8, mx - (this.getX() + px - Camera.x) - 8);

				} else if (dir == left_dir) {

					px = -18;
					py = 0;

					angle = Math.atan2(my - (this.getY() - py - Camera.y), mx - (this.getX() - px - Camera.x));

				}

				double dx = Math.cos(angle);
				double dy = Math.sin(angle);

				Bullet bullet = new Bullet(this.getX() + px, this.getY() + py, 16, 16, null, dx, dy);
				Game.bullets.add(bullet);
			}
		}

		if (life <= 0) {

			Game.gameState = "GAME_OVER";

		}

		updateCamera();
	}

	public void checkCollisionGun() {

		for (int i = 0; i < Game.entities.size(); i++) {

			Entity atual = Game.entities.get(i);
			if (atual instanceof Weapon) {
				if (Entity.isColliding(this, atual)) {

					gun = true;
					ammo += 10;
					if (gun == true) {

						gun = true;

					}

					Game.entities.remove(atual);
				}

			}
		}

	}

	public void checkCollisionAmmo() {

		for (int i = 0; i < Game.entities.size(); i++) {

			Entity atual = Game.entities.get(i);
			if (atual instanceof Ammo) {
				if (Entity.isColliding(this, atual)) {

					ammo += 10;

					Game.entities.remove(atual);
				}

			}
		}

	}

	public void updateCamera() {

		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * World.TILE_SIZE - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * World.TILE_SIZE - Game.HEIGHT);

	}

	public void checkCollisionLifePack() {

		for (int i = 0; i < Game.entities.size(); i++) {

			Entity atual = Game.entities.get(i);
			if (atual instanceof Lifepack) {
				if (Entity.isColliding(this, atual)) {

					life += 20;
					if (life > 100) {

						life = 100;

					}
					Game.entities.remove(atual);
				}

			}
		}

	}

	public void render(Graphics g) {

		if (!isDamage) {

			if (dir == right_dir) {

				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, this.getWidth(),
						this.getHeight(), null);

				if (gun) {

					g.drawImage(Entity.GUN_RIGHT, this.getX() + 9 - Camera.x, this.getY() - Camera.y - z, null);

				}

			} else if (dir == left_dir) {

				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, this.getWidth(),
						this.getHeight(), null);

				if (gun) {

					g.drawImage(Entity.GUN_LEFT, this.getX() - 9 - Camera.x, this.getY() - Camera.y - z, null);

				}

			}

		} else {

			if (dir == right_dir) {

				g.drawImage(damagePlayer[0], this.getX() - Camera.x, this.getY() - Camera.y - z, this.getWidth(),
						this.getHeight(), null);

				if (gun) {

					g.drawImage(Entity.DAM_GUN_RIGHT, this.getX() + 9 - Camera.x, this.getY() - Camera.y - z, null);

				}

			} else if (dir == left_dir) {

				g.drawImage(damagePlayer[1], this.getX() - Camera.x, this.getY() - Camera.y - z, this.getWidth(),
						this.getHeight(), null);

				if (gun) {

					g.drawImage(Entity.DAM_GUN_LEFT, this.getX() - 9 - Camera.x, this.getY() - Camera.y - z, null);

				}

			}

		}

		if (isJumping) {

			g.setColor(Color.BLACK);
			g.fillOval(this.getX() - Camera.x + 5, this.getY() - Camera.y + 12, 8, 8);

		}

	}

}
