package com.stonelame.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.stonelame.entities.Bullet;
import com.stonelame.entities.Enemy;
import com.stonelame.entities.Entity;
import com.stonelame.entities.Npc;
import com.stonelame.entities.Player;
import com.stonelame.graficos.Spritesheet;
import com.stonelame.graficos.UI;
import com.stonelame.world.World;

//import java.awt.FontFormatException;
//import java.io.IOException;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	// Recurso que usamos para dizer ao Java que um objeto serializado é compatível
	// ou não com o .class utilizado para desserializar.
	private static final long serialVersionUID = 1L;

	// Usando para criar uma janela
	public static JFrame frame;

	private Thread thread;
	private boolean isRunning = true;
	public static final int WIDTH = 240, HEIGHT = 160, SCALE = 3;
	private BufferedImage image;
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<Bullet> bullets;
	public static Spritesheet spritesheet;
	
	//public static World world;
	public static World world;

	public static Player player;
	public static Random rand;
	public static UI ui;
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	public Font newfont;

	private boolean restartGame = false;

	private int CUR_LEVEL = 1;
			
	//private int MAX_LEVEL = 3;

	public static String gameState = "MENU";

	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	
	public Menu menu;

	// luz dinamica
	public int[] pixels;
	public BufferedImage lightmap;
	public int[] lightMapPixels;
	// luz dinamica\\

	// MAP
	public static int[] minimapaPixels;
	public static BufferedImage minimapa;

	// MAP//

	public int mx, my;

	public boolean saveGame = false;

	
	
	public Game() {

		//Sound.theme.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//Janela
		//setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
		
		//Janela//
		
		initFrame();

		// Inicializando Objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		// luz dinamica
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {

			e.printStackTrace();
		}

		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		// luz dinamica\\

		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<Bullet>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, World.TILE_SIZE, World.TILE_SIZE));
		entities.add(player);
		
		
		world = new World("/level1.png");
		//world = new WorldNotRandom("/level1.png");
		
		// MAP
		minimapa = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapaPixels = ((DataBufferInt) minimapa.getRaster().getDataBuffer()).getData();
		// MAPA\\
		
		/*
		 * try { newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		 * } catch (FontFormatException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */
		Npc npc = new Npc(32,32,16,16,spritesheet.getSprite(112, 48, World.TILE_SIZE, World.TILE_SIZE));
		entities.add(npc);
		menu = new Menu();

	}

	public void initFrame() {

		frame = new JFrame("Game #1");
		frame.add(this);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();

		// Icone da Janela

		Image imagem = null;
		try {

			imagem = ImageIO.read(getClass().getResource("/icon.png")); //Icone Janela

		} catch (IOException e) {

			e.printStackTrace();

		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/icon.png")); //Icone Mouse
		Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "img");
		frame.setCursor(c);
		frame.setIconImage(imagem);
		frame.setAlwaysOnTop(true);

		// Icone da Janela//

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

	}

	public synchronized void start() {

		thread = new Thread(this);
		isRunning = true;
		thread.start();

	}

	public synchronized void stop() {

		isRunning = false;
		try {

			thread.join();

		} catch (InterruptedException e) {

			e.printStackTrace();

		}

	}

	public static void main(String[] args) {

		Game game = new Game();
		game.start();

	}

	public void tick() {

		if (gameState == "NORMAL") {

			if (this.saveGame) {

				this.saveGame = false;

				String[] opt1 = { "level", "vida" };

				int[] opt2 = { this.CUR_LEVEL, (int) player.life };
				Menu.saveGame(opt1, opt2, 10);

			}

			this.restartGame = false;

			for (int i = 0; i < entities.size(); i++) {

				Entity e = entities.get(i);

				e.tick();

			}

			for (int i = 0; i < bullets.size(); i++) {

				bullets.get(i).tick();

			}
			/*
			 * if (enemies.size() == 0) {
			 * 
			 * CUR_LEVEL++;
			 * 
			 * if (CUR_LEVEL > MAX_LEVEL) {
			 * 
			 * CUR_LEVEL = 1;
			 * 
			 * }
			 * 
			 * String newWorld = "level" + CUR_LEVEL + ".png"; World.restartGame(newWorld);
			 * }
			 */
		} else if (gameState == "GAME_OVER") {

			this.framesGameOver++;
			if (this.framesGameOver == 30) {

				this.framesGameOver = 0;
				if (this.showMessageGameOver) {

					this.showMessageGameOver = false;

				} else {

					this.showMessageGameOver = true;

				}
			}

			if (restartGame) {

				this.restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);

			}

		} else if (gameState == "MENU") {

			menu.tick();

		}

	}

	public void applyLight() {

		for (int xx = 0; xx < Game.WIDTH; xx++) {

			for (int yy = 0; yy < Game.HEIGHT; yy++) {

				if (lightMapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff) {
					
					int pixel = Pixel.getLightBlend(pixels[xx + yy * WIDTH],0x808080,0);
					pixels[xx + (yy * WIDTH)] = pixel;

				}

			}

		}

	}

	public void render() {
		
		
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		// Renderização
		Graphics g = image.getGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		// Graphics2D g2 = (Graphics2D) g;

		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);

		for (int i = 0; i < entities.size(); i++) {

			Entity e = entities.get(i);
			e.render(g);

		}

		for (int i = 0; i < bullets.size(); i++) {

			bullets.get(i).render(g);

		}

		 applyLight();
		ui.render(g);
		g.dispose();
		g = bs.getDrawGraphics();

		g.drawImage(image, 0, 0,Toolkit.getDefaultToolkit().getScreenSize().width ,Toolkit.getDefaultToolkit().getScreenSize().height, null);
		g.setFont(new Font("arial", Font.BOLD, 20));
		g.setColor(Color.white);
		g.drawString("Munição: " + Player.ammo, 600, 470);

		// tentativa de FPS
		// g.drawString("FPS: " + frames,630,30);

		if (gameState == "GAME_OVER") {

			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(250, 0, 0, 120));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 40));
			g.setColor(Color.white);
			g.drawString("Game Over", ((WIDTH * SCALE) / 2) - 90, ((HEIGHT * SCALE) / 2));
			g.setFont(new Font("arial", Font.BOLD, 32));
			
			if (showMessageGameOver) {

				g.drawString("Pressione Z para reiniciar o jogo", ((WIDTH * SCALE) / 2) - 230,
						((HEIGHT * SCALE) / 2) + 60);

			}
		} else if (gameState == "MENU") {
			player.updateCamera();
			menu.render(g);

		}
		/*
		 * Graphics2D g2 = (Graphics2D) g; double angleMouse = Math.atan2(225 - my,225 -
		 * mx); g2.rotate(angleMouse,200+25,200+25); g.setColor(Color.red);
		 * g.fillRect(200, 200, 50, 50);
		 */
		/*
		 * g.setFont(newfont); g.setColor(Color.red); g.drawString("Teste", 20, 20);
		 */

		World.renderMiniMap();
		g.drawImage(minimapa, 620, 250, World.WIDTH * 5, World.HEIGHT * 5, null);
		bs.show();

	}

	public void run() {

		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		
		
		//Focar janela
		requestFocus();
		//Focar janela\
		
		while (isRunning) {

			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {

				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {

				System.out.println("FPS: " + frames);

				frames = 0;
				timer += 1000;

			}
		}
		stop();

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {

			player.right = true;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {

			player.left = true;

		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {

			player.up = true;

			if (gameState == "MENU") {

				menu.up = true;

			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {

			player.down = true;

			if (gameState == "MENU") {

				menu.down = true;

			}

		}

		if (e.getKeyCode() == KeyEvent.VK_Z) {

			player.shoot = true;

			if (gameState == "MENU") {

				menu.enter = true;

			}

		}

		if (e.getKeyCode() == KeyEvent.VK_X) {

			player.jump = true;

		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

			if (gameState == "NORMAL") {

				gameState = "MENU";

				Menu.setPause(true);

			}

		}

		if (e.getKeyCode() == KeyEvent.VK_SPACE) {

			if (gameState == "NORMAL") {

				this.saveGame = true;

			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {

			player.right = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {

			player.left = false;

		}

		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {

			player.up = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {

			player.down = false;

		}

		if (e.getKeyCode() == KeyEvent.VK_Z) {

			if (gameState == "GAME_OVER") {

				this.restartGame = true;

			}

		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {

		player.mouseShoot = true;
		player.mx = (e.getX() / SCALE);
		player.my = (e.getY() / SCALE);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		this.mx = e.getX();
		this.my = e.getY();

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
