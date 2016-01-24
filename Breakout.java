import javax.swing.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Thread;
import java.lang.Math;

public class Breakout extends JComponent {

	private static int SCORE = 0;
	private static int LEVEL = 0;
	private static int LIVES = 3;

	private static int BRICK_SCORE = 300;
	
	private static int BRICK_HEIGHT = 20;
	private static int BRICK_WIDTH = 30;
	private static int BALL_RADIUS = 10;
	private static boolean IS_PAUSED = false;
	private static int PADDLE_LENGTH = 50;
	private static int PADDLE_HEIGHT = 10;

	public Breakout() {
		new Thread(new FrameDrawer()).start();
	}

	public void pauseResume() {
		IS_PAUSED = !IS_PAUSED;
	}

	public interface DrawableObject {
		void draw(Graphics2D g2);
	}

	private class FrameDrawer implements Runnable {

		@Override
		public void run() {
			while(true) {
				repaint();
				if (IS_PAUSED) {
					try {
						Thread.sleep(1000/40);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					continue;
				}
				if(m_ball.getSpeed() > 0) {
					int x = m_ball.getX();
					int y = m_ball.getY();
					if (m_ball.getDirection().getX() > 0) {
						if (m_ball.getDirection().getY() > 0) {
							for (int i = 0; i < 5; i++) {
								if (m_brickList.isPointInBricks(x + 1, y + 1)) {
									break;
								}
								x++;
								y++;
							}
						} else {
							for (int i = 0; i < 5; i++) {
								if (m_brickList.isPointInBricks(x + 1, y - 1)) {
									break;
								}
								x++;
								y--;
							}
						}
					} else {
						if (m_ball.getDirection().getY() > 0) {
							for (int i = 0; i < 5; i++) {
								if (m_brickList.isPointInBricks(x - 1, y + 1)) {
									break;
								}
								x--;
								y++;
							}
						} else {
							for (int i = 0; i < 5; i++) {
								if (m_brickList.isPointInBricks(x - 1, y - 1)) {
									break;
								}
								x--;
								y--;
							}
						}
					}					 

					if (x <= BALL_RADIUS || x >= getWidth() - BALL_RADIUS) {
						m_ball.reflectX();
					}
					if (y <= BALL_RADIUS) {
						m_ball.reflectY();
					}
					if (y >= getHeight() + BALL_RADIUS) {
						m_ball.setSpeed(0);
						LIVES--;
					}

					m_ball.setX(x);
					m_ball.setY(y);
				
					hasItHitAnyBricks();
					hasItHitThePaddle();
				}

				if (m_paddle.getDirection() != 0) {
					m_paddle.setX(m_paddle.getDirection()*10 + m_paddle.getX());
					if (m_paddle.getX1() < 0) {
						m_paddle.setX1(0);
					}
					if (m_paddle.getX2() > getWidth()) {
						m_paddle.setX2(getWidth());
					}			
				}

				try {
					Thread.sleep(1000/40);			   
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public class Point {
		private int m_x;
		private int m_y;

		public Point(int x, int y) {
			m_x = x;
			m_y = y;
		}

		public int getX() {
			return m_x;
		}

		public int getY() {
			return m_y;
		}

		public void setX(int x) {
			m_x = x;
		}

		public void setY(int y) {
			m_y = y;
		}
	}

	public class Ball implements DrawableObject {
		private int m_x;
		private int m_y;
		private int m_speed;
		private Point m_direction;

		public Ball() {
			m_x = -1;
			m_y = -1;
			m_speed = 0;
			m_direction = new Point(0, 0);
		}

		public int getX() {
			return m_x;
		}

		public int getY() {
			return m_y;
		}

		public void setX(int x) {
			m_x = x;
		}
	
		public void setY(int y) {
			m_y = y;
		}

		public void initializePosition() {
			m_x = getWidth() - 20;
			m_y = getHeight() - 200;
		}
	
		public void setSpeed(int speed) {
			m_speed = speed;
		}

		public int getSpeed() {
			return m_speed;
		}

		public void setDirection(int x, int y) {
			m_direction.setX(x);
			m_direction.setY(y);
		}

		public Point getDirection() {
			return m_direction;
		}

		public void reflectX() {
			m_direction.setX(-1 * m_direction.getX());
		}

		public void reflectY() {
			m_direction.setY(-1 * m_direction.getY());
		}

		@Override
		public void draw(Graphics2D g2) {
			if (m_speed == 0) {
				initializePosition();
			}

			g2.setColor(Color.RED);
			g2.fillOval(m_x - BALL_RADIUS, m_y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		}
	}

	private Ball m_ball = new Ball();

	public Ball getBall() {
		return m_ball;
	}

	public class Paddle implements DrawableObject {
		private int m_xPos;
		private int m_direction;

		public Paddle() {
			m_xPos = 0;
			m_direction = 0;
		}
		
		public void setDirection(int dir) {
			m_direction = dir;
		}

		public int getDirection() {
			return m_direction;
		}

		public void setX(int x) {
			m_xPos = x;
		}

		public int getX() {
			return m_xPos;
		}

		public int getX1() {
			return m_xPos - PADDLE_HEIGHT/2;
		}

		public void setX1(int x1) {
			m_xPos = x1 + PADDLE_HEIGHT/2;
		}
	
		public int getX2() {
			return m_xPos + PADDLE_HEIGHT/2 + PADDLE_LENGTH;
		}

		public void setX2(int x2) {
			m_xPos = x2 - PADDLE_HEIGHT/2 - PADDLE_LENGTH;	
		}

		public int getY() {
			return getHeight() - 15;
		}

		public int getY1() {
			return getY() - PADDLE_HEIGHT/2;
		}
		
		public int getY2() {
			return getY() + PADDLE_HEIGHT/2;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setStroke(new BasicStroke(PADDLE_HEIGHT));
			g2.setColor(Color.WHITE);
			g2.drawLine(m_xPos, getHeight() - 15, m_xPos + PADDLE_LENGTH, getHeight() - 15);
		}


	}

	private Paddle m_paddle = new Paddle();
	
	public Paddle getPaddle() {
		return m_paddle;
	}

	public class Brick implements DrawableObject {
		int m_x;
		int m_y;
		Color m_color;

		Brick(int x, int y, Color color) {
			m_x = x;
			m_y = y;
			m_color = color;
		}
		
		public int getX() {
			return m_x;
		}

		public int getY() {
			return m_y;
		}

		public int getX1() {
			return m_x - BRICK_HEIGHT/2;
		}

		public int getX2() {
			return m_x + BRICK_HEIGHT/2 + BRICK_WIDTH;
		}

		public int getY1() {
			return m_y - BRICK_HEIGHT/2;
		}

		public int getY2() {
			return m_y + BRICK_HEIGHT/2;
		}

		public Color getColor() {
			return m_color;
		}

		public boolean isPointInBrick(int x, int y) {
			return getX1() <= x && x <= getX2() && getY1() <= y && y <= getY2();	
		}

		public boolean isPointInBrickArea(int x, int y) {
			int brickX = getX();
			int brickY = getY();

			int ballX = m_ball.getX();
			int ballY = m_ball.getY();

			int brickX1 = getX1();
			int brickX2 = getX2();
			int brickY1 = getY1();
			int brickY2 = getY2();
	
			if (brickX1 <= ballX && ballX <= brickX2) {//in the x range
				if (brickY1 - 10 < ballY && ballY < brickY2 + 10) {
					return true;
				}
			} else if (brickY1 <= ballY && ballY <= brickY2) {//in the y range
				if (brickX1 - 10 < ballX && ballX < brickX2 + 10) {
					return true;
				}
			} else {
				if (ballX < brickX1 && ballY < brickY1) {//top left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared < 100) {
						return true;
					}
				} else if (ballX > brickX2 && ballY < brickY1) {//top right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared < 100) {
						return true;
					}
				} else if (ballX < brickX1 && ballY > brickY2) {//bottom left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared < 100) {
						return true;
					}
				} else if (ballX > brickX2 && ballY > brickY2) {//bottom right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared < 100) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setStroke(new BasicStroke(BRICK_HEIGHT));
			g2.setColor(m_color);
			g2.drawLine(getX(), getY(), getX() + BRICK_WIDTH, getY());
		}
	}

	public class BrickList implements DrawableObject {
		private ArrayList<Brick> m_brickList = new ArrayList<Brick>();

		public BrickList() {
			int startingX = 100;
			int startingY = 80;
			List<Color> colorList = Arrays.asList(Color.BLUE, Color.GREEN, Color.RED, 
												Color.YELLOW, Color.ORANGE, Color.WHITE);
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 6; j++) {
					m_brickList.add(new Brick(startingX + i * (BRICK_WIDTH + BRICK_HEIGHT + 1), 
										startingY + j * (BRICK_HEIGHT + 1), colorList.get(j)));
				}
			}
		}

		public int size() {
			return m_brickList.size();
		}

		public Brick get(int i) {
			return m_brickList.get(i);
		}

		public void remove(int i) {
			SCORE += BRICK_SCORE;
			m_brickList.remove(i);
		}	

		public boolean isPointInBricks(int x, int y) {
			for (Brick brick : m_brickList) {
				if (brick.isPointInBrick(x, y)) {
					return true;
				}
			}
			return false;
		}

		public boolean isPointInBricksArea(int x, int y) {
			for (Brick brick : m_brickList) {
				if (brick.isPointInBrickArea(x, y)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void draw(Graphics2D g2) {
			for (Brick brick : m_brickList) {
				brick.draw(g2);
			}
		}
	}

	private BrickList m_brickList = new BrickList();

	public BrickList getBrickList() {
		return m_brickList;
	}

	public void hasItHitAnyBricks() {
		int j = 0;
		for (int i = 0; i < m_brickList.size(); i++) {

			Brick brick = m_brickList.get(i);

			int brickX = brick.getX();
			int brickY = brick.getY();
			
			int ballX = m_ball.getX();
			int ballY = m_ball.getY();

			int brickX1 = brick.getX1();
			int brickX2 = brick.getX2();
			int brickY1 = brick.getY1();
			int brickY2 = brick.getY2();

			if (brickX1 <= ballX && ballX <= brickX2) {//in the x range
				if (ballY < brickY1 && brickY1 - ballY <= 10 || ballY > brickY2 && ballY - brickY2 <= 10) { 
					m_ball.reflectY();
					m_brickList.remove(i);
					break;
				}
			} else if (brickY1 <= ballY && ballY <= brickY2) {//in the y range
				if (ballX < brickX1 && brickX1 - ballX <= 10 || ballX > brickX2 && ballX - brickX2 <= 10) {	 
					m_ball.reflectX();
					m_brickList.remove(i);
					break;
				}
			} else {
				if (ballX < brickX1 && ballY < brickY1) {//top left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared <= 100) {
						if (brickX1 - ballX < brickY1 - ballY) {
							m_ball.reflectY();
						} else {
							m_ball.reflectX();
						}
						m_brickList.remove(i);
						break;
					}
				} else if (ballX > brickX2 && ballY < brickY1) {//top right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared <= 100) {
						if (ballX - brickX2 < brickY1 - ballY) {
							m_ball.reflectY();
						} else {
							m_ball.reflectX();
						}
						m_brickList.remove(i);
						break;
					}
				} else if (ballX < brickX1 && ballY > brickY2) {//bottom left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared <= 100) {
						if (brickX1 - ballX < ballY - brickY2) {
							m_ball.reflectY();
						} else {
							m_ball.reflectX();
						}
						m_brickList.remove(i);
						break;
					}
				} else if (ballX > brickX2 && ballY > brickY2) {//bottom right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared <= 100) {
						if (ballX - brickX2 < ballY - brickY2) {
							m_ball.reflectY();
						} else {
							m_ball.reflectX();
						}
						m_brickList.remove(i);
						break;
					}
				}
			}
		}
	}

	public void hasItHitThePaddle() {
		int x1 = m_paddle.getX1();
		int x2 = m_paddle.getX2();
		int y1 = m_paddle.getY1();
		int ballX = m_ball.getX();
		int ballY = m_ball.getY();

		if (x1 <= ballX && ballX <= x2) {
			if (y1 > ballY && y1 - ballY <= BALL_RADIUS) {
				m_ball.reflectY();
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
							RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.BLACK);
		g2.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 22));
		g2.drawString("Score: " + SCORE, 10, 20);
		g2.drawString("Level: " + LEVEL, 210, 20);
		g2.drawString("Lives: " + LIVES, 410, 20);
		m_brickList.draw(g2);
		m_paddle.draw(g2);
		m_ball.draw(g2);

		if (IS_PAUSED) {
			g2.setColor(Color.WHITE);
			g2.drawString("PAUSED", getWidth()/2, 2*getHeight()/3);
		}
	}
}
