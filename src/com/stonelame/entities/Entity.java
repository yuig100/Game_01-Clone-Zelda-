package com.stonelame.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.stonelame.main.Game;
import com.stonelame.world.Camera;
import com.stonelame.world.Node;
import com.stonelame.world.Vector2i;
import com.stonelame.world.World;

public class Entity {

	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6 * 16, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7 * 16, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage AMMO_EN = Game.spritesheet.getSprite(6 * 16, 16, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(112, 32, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(112, 16, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(144, 32, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(8 * 16, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(9 * 16, 0, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage DAM_GUN_RIGHT = Game.spritesheet.getSprite(129, 16, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage DAM_GUN_LEFT = Game.spritesheet.getSprite(144, 16, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage BULLET_RIGHT = Game.spritesheet.getSprite(112, 16, World.TILE_SIZE, World.TILE_SIZE);
	public static BufferedImage BULLET_LEFT = Game.spritesheet.getSprite(96, 32, World.TILE_SIZE, World.TILE_SIZE);

	public int depth;
	
	protected int width, height;
	protected double x, y, z;
	protected int mwidth;
	protected int mheight;
	private BufferedImage sprite;

	protected List<Node> path;

	public int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	public double speed = 0.5;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;

		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}

	// Metodos Get

	public int getX() {

		return (int) this.x;

	}

	public int getY() {

		return (int) this.y;

	}

	public int getWidth() {

		return this.width;

	}

	public int getHeight() {

		return this.height;

	}
	// Metodos Get//

	// Metodo Set

	public void setX(int newX) {

		this.x = newX;

	}

	public void setY(int newY) {

		this.y = newY;

	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {

		@Override
		public int compare(Entity n0, Entity n1) {

			if (n1.depth < n0.depth) {

				return +1;

			}
			if (n1.depth > n0.depth) {

				return -1;

			}

			return 0;

		}

	};
	
	// Metodo Set//

	public void setMask(int maskx, int masky, int mwidth, int mheight) {

		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;

	}

	public void tick() {

	}

	public double calculateDistance(int x1, int y1, int x2, int y2) {

		return Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));

	}

	public void followPath(List<Node> path) {

		if (path != null) {

			if (path.size() > 0) {

				Vector2i target = path.get(path.size() - 1).tile;
				// xprev = x;
				// yprev = y;

				if (x < target.x * World.TILE_SIZE && !isColliding(this.getX() + 1, this.getY())) {

					x++;

				} else if (x > target.x * World.TILE_SIZE && !isColliding(this.getX() - 1, this.getY())) {

					x--;

				}

				if (y < target.y * World.TILE_SIZE && !isColliding(this.getX(), this.getY() + 1)) {

					y++;

				} else if (y > target.y * World.TILE_SIZE && !isColliding(this.getX(), this.getY() - 1)) {

					y--;

				}

				if (x == target.x * World.TILE_SIZE && y == target.y * World.TILE_SIZE) {

					path.remove(path.size() - 1);

				}

			}

		}

	}
	
	public boolean isCollidingPerfect(int x1, int y1,int x2,int y2,int[] pixels1,int[] pixels2,BufferedImage sprite1,BufferedImage sprite2) {

		for(int xx1 = 0; xx1 < sprite1.getWidth(); xx1++) {
			
			for(int yy1 = 0; yy1 < sprite1.getHeight(); yy1++) {
				
				for(int xx2 = 0; xx2 < sprite2.getWidth(); xx2++) {
					
					for(int yy2 = 0; yy2 < sprite2.getHeight(); yy2++) {
						
						int PixelAtual1 = pixels1[xx1 + yy1 * sprite1.getWidth()];
						int PixelAtual2 = pixels2[xx1 + yy1 * sprite1.getWidth()];
						
						if(PixelAtual1 == 0x00FFFFFF || PixelAtual2 == 0x00FFFFFF) {
							
							continue;
							
						}
						
						if(xx1+x1 == xx2+x2 && yy1+y1 == yy2+y2) {
							
							return true;
							
						}
						
					}
					
				}
				
			}
			
		}

		return false;
	}
	
	
	
	// Coliz?o Entre inimigos

	public boolean isColliding(int xnext, int ynext) {

		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, mwidth, mheight);

		for (int i = 0; i < Game.enemies.size(); i++) {

			Enemy e = Game.enemies.get(i);

			if (e == this)
				continue;

			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, mwidth, mheight);

			if (enemyCurrent.intersects(targetEnemy)) {

				return true;

			}

		}

		return false;
	}

	// Coliz?o Entre entidades

	public static boolean isColliding(Entity e1, Entity e2) {

		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);

		if (e1Mask.intersects(e2Mask) && e1.z == e2.z) {

			return true;

		}

		return false;

	}

	public void render(Graphics g) {

		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		// g.setColor(Color.red);
		// g.fillRect(this.getX() + maskx - Camera.x,this.getY() + masky - Camera.y,
		// mwidth, mheight);

	}
}
