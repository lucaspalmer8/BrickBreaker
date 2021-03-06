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
	private static int LEVEL = 1;
	private static int LIVES = 3;

	private static int BRICK_SCORE = 300;
	
	private static int FRAME_RATE = 40;
	private static int SPEED = 200;

	private static int PADDLE_SPEED = 400;

	private static final int BRICK_HEIGHT = 20;
	private static final int BRICK_WIDTH = 50;
	private static final int BRICK_GAP = 2;
	private static final int BALL_RADIUS = 7;
	private static final int MINIMUM_RADIUS = 5;
	private static final int PADDLE_LENGTH = 50;
	private static final int PADDLE_HEIGHT = 10;

	public static final int X = 1000;
	public static final int Y = 500;

	public static int PREVIOUS_WIDTH = 1000;
	public static int PREVIOUS_HEIGHT = 500;

	private GameState m_gameState = GameState.GAME_STOPPED;

	public static enum GameState {
		GAME_STARTED,
		GAME_STOPPED,
		GAME_OVER,
		GAME_PAUSED
	};

	public GameState getGameState() {
		return m_gameState;
	}
	
	public void setGameState(GameState gameState) {
		m_gameState = gameState;
	}
	
	public void setRates(int frameRate, int speed) {
		FRAME_RATE = frameRate;
		SPEED = speed; 
	}

	public void pauseResume() {
		if (m_gameState == GameState.GAME_PAUSED) {
			m_gameState = GameState.GAME_STARTED;
		} else if (m_gameState == GameState.GAME_STARTED) {
			m_gameState = GameState.GAME_PAUSED;
		}
	}

	public void startNewGame() {
		LIVES = 3;
		LEVEL = 1;
		SCORE = 0;
		m_gameState = GameState.GAME_STOPPED;
		m_brickList = new BrickList();
	}

	public Breakout() {
		new Thread(new FrameDrawer()).start();
	}

	public interface DrawableObject {
		void draw(Graphics2D g2);
	}

	private class FrameDrawer implements Runnable {
		@Override
		public void run() {
			while(true) {
				repaint();
				if (m_brickList.isEmpty()) {
					m_ballList = new BallList();;
					LIVES = 3;
					LEVEL++;
					SCORE+= 1000*LEVEL;
					m_gameState = GameState.GAME_STOPPED;
					m_brickList = new BrickList();
				}
				if (getWidth() != 0 && PREVIOUS_WIDTH != getWidth()) {
					m_paddle.setX(m_paddle.getFloatX()*getWidth()/PREVIOUS_WIDTH);
					for (Ball m_ball : m_ballList.getList()) {
						if (m_ball != null) {
							m_ball.setX(m_ball.getFloatX()*getWidth()/PREVIOUS_WIDTH);
						}
					}
					PREVIOUS_WIDTH = getWidth();
				}
				if (getHeight() != 0 && PREVIOUS_HEIGHT != getHeight()) {
					for (Ball m_ball : m_ballList.getList()) {
						if (m_ball != null) {
							m_ball.setY(m_ball.getFloatY()*getHeight()/PREVIOUS_HEIGHT);
						}
					}
					PREVIOUS_HEIGHT = getHeight();
				}
				if (m_gameState == GameState.GAME_OVER || m_gameState == GameState.GAME_PAUSED) {
					try {
						Thread.sleep(1000/FRAME_RATE);
					} catch(InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
					continue;
				}
				for (int i = 0; i < m_ballList.getList().size(); i++) {
					Ball m_ball = m_ballList.getList().get(i);
					if (m_ball == null) continue;
					boolean removedBall = false;
					if (m_ball.getSpeed() > 0) {
						int distance = SPEED/FRAME_RATE;
						float s = SPEED;
						float f = FRAME_RATE;
						float d = s/f;
						float leftover = d - (int)d;
						for (int j = 0; j < distance; j++) {
							if (m_ball.getDirection().getX() > 0) {
								m_ball.incrementX(1);
							}
							if (m_ball.getDirection().getX() < 0) {
								m_ball.incrementX(-1);
							}
							if (m_ball.getDirection().getY() > 0) {
								m_ball.incrementY(1);
							}
							if (m_ball.getDirection().getY() < 0) {
								m_ball.incrementY(-1);
							}
							hasItHitAnyBricks(m_ball);
							hasItHitThePaddle(m_ball);

							int RADIUS = m_ball.getRadius();
							int totalX = m_ball.getX();
							int totalY = m_ball.getY();
							if (totalX <= RADIUS) {
								m_ball.posateX();
							}
							if (totalX >= getWidth() - RADIUS) {
								m_ball.negateX();
							}
							if (totalY <= RADIUS) {
								m_ball.posateY();
							}

							if (totalY >= getHeight() + RADIUS) {
								int numBalls = m_ballList.size();
								if (numBalls > 1) {
									m_ballList.getList().set(i, null);
									removedBall = true;
									break;
								} else if (numBalls == 1) {
									m_ball.setSpeed(0);
									LIVES--;
									if (LIVES == 0) {
										m_gameState = GameState.GAME_OVER;
										m_ballList = new BallList();
									} else {
										m_gameState = GameState.GAME_STOPPED;
									}
									break;
								}
							}
						}

						if (removedBall) continue;

						hasItHitAnyBricks(m_ball);
						hasItHitThePaddle(m_ball);

						int RADIUS = m_ball.getRadius();
						int totalX = m_ball.getX();
						int totalY = m_ball.getY();
						if (totalX <= RADIUS) {
							m_ball.posateX();
						}
						if (totalX >= getWidth() - RADIUS) {
							m_ball.negateX();
						}
						if (totalY <= RADIUS) {
							m_ball.posateY();
						}

						if (m_ball.getSpeed() > 0 && totalY >= getHeight() + RADIUS) {
							int numBalls = m_ballList.size();
							if (numBalls > 1) {
								m_ballList.getList().set(i, null);
								continue;
							} else if (numBalls == 1) {
								m_ball.setSpeed(0);
								LIVES--;
								if (LIVES == 0) {
									m_gameState = GameState.GAME_OVER;
								} else {
									m_gameState = GameState.GAME_STOPPED;
								}
								break;
							}
						}						

						if (m_ball.getDirection().getX() > 0) {
							m_ball.incrementX(leftover);
						}
						if (m_ball.getDirection().getX() < 0) {
							m_ball.incrementX(-1*leftover);
						}
						if (m_ball.getDirection().getY() > 0) {
							m_ball.incrementY(leftover);
						}
						if (m_ball.getDirection().getY() < 0) {
							m_ball.incrementY(-1*leftover);
						}
					}
				}

				if (m_paddle.getDirection() != 0) {
					int distance = PADDLE_SPEED/FRAME_RATE;

					for (int i = 0; i < distance; i++) {
						if (m_paddle.getDirection() > 0) {
							m_paddle.incrementX(1);
						}
						if (m_paddle.getDirection() < 0) {
							m_paddle.incrementX(-1);
						}
					}

					if (m_paddle.getX1() < 0) {
						m_paddle.incrementX(-m_paddle.getX1());
					}
					if (m_paddle.getX2() > getWidth()) {
						m_paddle.incrementX(getWidth() - m_paddle.getX2());
					}			
				}

				try {
					Thread.sleep(1000/FRAME_RATE);			   
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
		private float m_x;
		private float m_y;
		private int m_speed;
		private Point m_direction;

		public Ball() {
			m_x = -1f;
			m_y = -1f;
			m_speed = 0;
			m_direction = new Point(0, 0);
		}

		public Ball(int speed) {
			initializePosition();
			m_speed = speed;
			m_direction = new Point(-1, -1);
		}

		public int getX() {
			return (int)m_x;
		}

		public int getY() {
			return (int)m_y;
		}

		public float getFloatX() {
			return m_x;
		}
	
		public float getFloatY() {
			return m_y;
		}

		public void setX(float x) {
			m_x = x;
		}

		public void setY(float y) {
			m_y = y;
		}

		public void incrementX(int x) {
			m_x += x;
		}

		public void incrementY(int y) {
			m_y += y;
		}

		public void incrementX(float x) {
			m_x += x;
		}
	
		public void incrementY(float y) {
			m_y += y;
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

		public void posateX() {
			if (m_direction.getX() < 0) {
				m_direction.setX(-1*m_direction.getX());
				SCORE += 50;
			}
		}

		public void posateY() {
			if (m_direction.getY() < 0) {
				m_direction.setY(-1*m_direction.getY());
				SCORE += 50;
			}
		}

		public void negateX() {
			if (m_direction.getX() > 0) {
				m_direction.setX(-1*m_direction.getX());
				SCORE += 50;
			}
		}

		public void negateY() {
			if (m_direction.getY() > 0) {
				m_direction.setY(-1*m_direction.getY());
				SCORE += 50;
			}
		}

		public int getRadius() {
			return MINIMUM_RADIUS;
		}

		@Override
		public void draw(Graphics2D g2) {
			if (m_speed == 0) {
				initializePosition();
			}
			g2.setColor(Color.RED);
			g2.fillOval(getX() - getRadius(), getY() - getRadius(), getRadius() * 2, getRadius() * 2);
		}
	}

	public class BallList implements DrawableObject {
		private ArrayList<Ball> m_ballyList = new ArrayList<Ball>();

		public ArrayList<Ball> getList() {
			return m_ballyList;
		}		

		public BallList() {
			m_ballyList.add(new Ball());
			for (int i = 0; i < 5; i++) {
				m_ballyList.add(null);
			}
		}
	
		public void addNewBall() {
			for (int i = 0; i < m_ballyList.size(); i++) {
				Ball ball = m_ballyList.get(i);
				if (ball == null) {
					m_ballyList.set(i, new Ball(10));
					return;
				}
			}
		}

		public int size() {
			int counter = 0;
			for (Ball ball : m_ballyList) {
				if (ball != null) {
					counter++;
				}
			}
			return counter;
		}

		public Ball getBall() {
			for (Ball ball : m_ballyList) {
				if (ball != null) {
					return ball;
				}
			}
			return null;
		}

		public int getRadius() {
			return MINIMUM_RADIUS;
		}
	
		@Override
		public void draw(Graphics2D g2) {
			for (Ball ball : m_ballyList) {
			if (ball != null) {
				ball.draw(g2);
			}
			}
		}
	}

	private BallList m_ballList = new BallList();

	public BallList getBallList() {
		return m_ballList;
	}

	public class Paddle implements DrawableObject {
		private float m_xPos;
		private int m_direction;

		public Paddle() {
			m_xPos = 0f;
			m_direction = 0;
		}
		
		public void setDirection(int dir) {
			m_direction = dir;
		}

		public void setX(float x) {
			m_xPos = x;
		}

		public int getDirection() {
			return m_direction;
		}

		public int getX() {
			return (int)m_xPos;
		}

		public float getFloatX() {
			return m_xPos;
		}

		public void incrementX(int x) {
			m_xPos += x;
		}

		public int getX1() {
			return (int)getX() - PADDLE_HEIGHT/2;
		}
	
		public int getX2() {
			return (int)getX() + PADDLE_HEIGHT/2 + PADDLE_LENGTH;
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

		public boolean isPointInPaddle(int x, int y) {
			return getX1() <= x && x <= getX2() && getY1() <= y && y <= getY2();	
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setStroke(new BasicStroke(PADDLE_HEIGHT));
			g2.setColor(Color.WHITE);
			g2.drawLine(getX(), getHeight() - 15, getX() + PADDLE_LENGTH, getHeight() - 15);
		}
	}

	private Paddle m_paddle = new Paddle();
	
	public Paddle getPaddle() {
		return m_paddle;
	}

	public class Brick implements DrawableObject {
		float m_x;
		float m_y;
		Color m_color;
		boolean m_newBall;

		public boolean createNewBall() {
			return m_newBall;
		}

		Brick(float x, float y, Color color, boolean newBall) {
			m_x = x;
			m_y = y;
			m_color = color;
			m_newBall = newBall;
		}
		
		public float getX() {
			return m_x*getWidth()/X;
		}

		public float getY() {
			return m_y*getHeight()/Y;
		}

		public float getX1() {
			return getX();
		}

		public float getX2() {
			return getX1() + BRICK_WIDTH*getWidth()/X - BRICK_GAP;
		}

		public float getY1() {
			return getY();
		}

		public float getY2() {
			return getY1() + BRICK_HEIGHT*getHeight()/Y - BRICK_GAP;
		}

		public Color getColor() {
			return m_color;
		}

		public boolean isPointInBrick(int x, int y) {
			return (int)getX1() <= x && x <= (int)getX2() && (int)getY1() <= y && y <= (int)getY2();	
		}

		public boolean isPointInBrickArea(int x, int y) {

			int ballX = x;
			int ballY = y;

			int brickX1 = (int)getX1();
			int brickX2 = (int)getX2();
			int brickY1 = (int)getY1();
			int brickY2 = (int)getY2();
	
			int RADIUS = m_ballList.getRadius();

			if (brickX1 <= ballX && ballX <= brickX2) {//in the x range
				if (brickY1 - RADIUS < ballY && ballY < brickY2 + RADIUS) {
					return true;
				}
			} else if (brickY1 <= ballY && ballY <= brickY2) {//in the y range
				if (brickX1 - RADIUS < ballX && ballX < brickX2 + RADIUS) {
					return true;
				}
			} else {
				if (ballX < brickX1 && ballY < brickY1) {//top left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared < RADIUS*RADIUS) {
						return true;
					}
				} else if (ballX > brickX2 && ballY < brickY1) {//top right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared < RADIUS*RADIUS) {
						return true;
					}
				} else if (ballX < brickX1 && ballY > brickY2) {//bottom left corner
					int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared < RADIUS*RADIUS) {
						return true;
					}
				} else if (ballX > brickX2 && ballY > brickY2) {//bottom right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY2)*(ballY - brickY2);
					if (distanceSquared < RADIUS*RADIUS) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setColor(m_color);
			g2.fill(new Rectangle2D.Double(getX(), getY(), BRICK_WIDTH*getWidth()/X - BRICK_GAP, BRICK_HEIGHT*getHeight()/Y - BRICK_GAP));
			if (m_newBall) {
				g2.setColor(Color.BLACK);
				float centreX = getX() + (BRICK_WIDTH*getWidth()/X - BRICK_GAP)/2;
				float centreY = getY() + (BRICK_HEIGHT*getHeight()/Y - BRICK_GAP)/2;
				float radius1 = (BRICK_HEIGHT*getHeight()/Y - BRICK_GAP)/3;
				float radius2 = (BRICK_WIDTH*getWidth()/X - BRICK_GAP)/3;
				float radius = radius1;
				if (radius2 < radius1) {
					radius = radius2;
				}
				g2.fillOval((int)(centreX - radius), (int)(centreY - radius), (int)(radius * 2), (int)(radius * 2));
			}
		}
	}

	public class BrickList implements DrawableObject {
		private ArrayList<Brick> m_brickList = new ArrayList<Brick>();

		public BrickList() {
			int startingX = (X - BRICK_WIDTH*15)/2;
			int startingY = Y/6;
			List<Color> colorList = Arrays.asList(Color.BLUE, Color.GREEN, Color.MAGENTA, 
												Color.YELLOW, Color.ORANGE, Color.WHITE);
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 6; j++) {
					boolean circle = false;
					if ((j == 1 || j == 3) && i%2 == 0) circle = true;
					m_brickList.add(new Brick(startingX + i*BRICK_WIDTH, startingY + j*BRICK_HEIGHT, colorList.get(j), circle));
				}
			}
		}

		public boolean isEmpty() {
			for (Brick brick : m_brickList) {
				if (brick != null) {
					return false;
				}
			}
			return true;
		}

		public int size() {
			return m_brickList.size();
		}

		public Brick get(int i) {
			return m_brickList.get(i);
		}

		public void remove(int i) {
			SCORE += BRICK_SCORE;
			Brick brick = m_brickList.get(i);
			if (brick.createNewBall()) {
				m_ballList.addNewBall();
			}
			m_brickList.set(i, null);
		}	

		public boolean isPointInBricks(int x, int y) {
			for (Brick brick : m_brickList) {
				if (brick != null && brick.isPointInBrick(x, y)) {
					return true;
				}
			}
			return false;
		}

		public boolean isPointInBricksArea(int x, int y) {
			for (Brick brick : m_brickList) {
				if (brick != null && brick.isPointInBrickArea(x, y)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void draw(Graphics2D g2) {
			for (Brick brick : m_brickList) {
				if (brick != null) {
					brick.draw(g2);
				}
			}
		}
	}

	private BrickList m_brickList = new BrickList();

	public BrickList getBrickList() {
		return m_brickList;
	}

	public void hasItHitAnyBricks(Ball m_ball) {
		int j = 0;
		for (int i = 0; i < m_brickList.size(); i++) {

			Brick brick = m_brickList.get(i);
			if (brick == null) {
				continue;
			}

			int brickX = (int)brick.getX();
			int brickY = (int)brick.getY();
			
			int ballX = m_ball.getX();
			int ballY = m_ball.getY();

			int brickX1 = (int)brick.getX1();
			int brickX2 = (int)brick.getX2();
			int brickY1 = (int)brick.getY1();
			int brickY2 = (int)brick.getY2();

			int RADIUS = m_ball.getRadius();

			if (brickX1 <= ballX && ballX <= brickX2) {//in the x range
				if (ballY < brickY1 && brickY1 - ballY <= RADIUS) {//above brick 
					m_ball.negateY();
					m_brickList.remove(i);
					return;
				} else if (ballY > brickY2 && ballY - brickY2 <= RADIUS) {//below brick
					m_ball.posateY();
					m_brickList.remove(i);
					return;
				}
			} else if (brickY1 <= ballY && ballY <= brickY2) {//in the y range	
				if (ballX < brickX1 && brickX1 - ballX <= RADIUS) {//left of brick	 
					m_ball.negateX();
					m_brickList.remove(i);
					return;
				} else if (ballX > brickX2 && ballX - brickX2 <= RADIUS) {//right of brick
					m_ball.posateX();
					m_brickList.remove(i);
					return;
				}
			}
		}

		for (int i = 0; i < m_brickList.size(); i++) {
	
			Brick brick = m_brickList.get(i);
			if (brick == null) {
				continue;
			}

			int ballX = m_ball.getX();
			int ballY = m_ball.getY();

			int brickX1 = (int)brick.getX1();
			int brickX2 = (int)brick.getX2();
			int brickY1 = (int)brick.getY1();
			int brickY2 = (int)brick.getY2();

			int RADIUS = m_ball.getRadius();

			Point direction = m_ball.getDirection();

			if (ballX < brickX1 && ballY < brickY1) {//top left corner
				int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY1)*(ballY - brickY1);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() > 0 && direction.getY() > 0) {
						m_ball.negateX();
						m_ball.negateY();
					} else if (direction.getX() > 0 && direction.getY() < 0) {
						m_ball.negateX();
					} else if (direction.getX() < 0 && direction.getY() > 0) {
						m_ball.negateY();
					}
					m_brickList.remove(i);
					break;
				}
			} else if (ballX > brickX2 && ballY < brickY1) {//top right corner
				int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY1)*(ballY - brickY1);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() < 0 && direction.getY() > 0) {
						m_ball.posateX();
						m_ball.negateY();
					} else if (direction.getX() > 0 && direction.getY() > 0) {
						m_ball.negateY();
					} else if (direction.getX() < 0 && direction.getY() < 0) {
						m_ball.posateX();
					}
					m_brickList.remove(i);
					break;
				}
			} else if (ballX < brickX1 && ballY > brickY2) {//bottom left corner
				int distanceSquared = (ballX - brickX1)*(ballX - brickX1) + (ballY - brickY2)*(ballY - brickY2);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() > 0 && direction.getY() < 0) {
						m_ball.negateX();
						m_ball.posateY();
					} else if (direction.getX() < 0 && direction.getY() < 0) {
						m_ball.posateY();
					} else if (direction.getX() > 0 && direction.getY() > 0) {
						m_ball.negateX();
					}
					m_brickList.remove(i);
					break;
				}
			} else if (ballX > brickX2 && ballY > brickY2) {//bottom right corner
				int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY2)*(ballY - brickY2);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() < 0 && direction.getY() < 0) {
						m_ball.posateX();
						m_ball.posateY();
					} else if (direction.getX() > 0 && direction.getY() < 0) {
						m_ball.posateY();
					} else if (direction.getX() < 0 && direction.getY() > 0) {
						m_ball.posateX();
					}
					m_brickList.remove(i);
					break;
				}
			}	
		}
	}

	public void hasItHitThePaddle(Ball m_ball) {
		int x1 = m_paddle.getX1();
		int x2 = m_paddle.getX2();
		int y1 = m_paddle.getY1();
		int y2 = m_paddle.getY2();
		int ballX = m_ball.getX();
		int ballY = m_ball.getY();
		int RADIUS = m_ball.getRadius();
		Point direction = m_ball.getDirection();

		if (x1 <= ballX && ballX <= x2) { //in horizontal range
			if (y1 > ballY && y1 - ballY <= RADIUS) {
				if (direction.getY() > 0) {
					m_ball.negateY();
				}
			}
		} else if (y1 <= ballY && ballY <= y2) { //in vertical range
			if (ballX < x1 && x1 - ballX <= RADIUS) {
				if (direction.getX() > 0) {
					m_ball.negateX();
				}
			}
			if (ballX > x2 && ballX - x2 <= RADIUS) {
				if (direction.getX() < 0) {
					m_ball.posateX();
				}
			}
		} else {
			if (ballX < x1 && ballY < y1) {//top left corner
				int distanceSquared = (ballX - x1)*(ballX - x1) + (ballY - y1)*(ballY - y1);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() > 0 && direction.getY() > 0) {
						m_ball.negateX();
						m_ball.negateY();
					} else if (direction.getX() > 0 && direction.getY() < 0) {
						m_ball.negateX();
					} else if (direction.getX() < 0 && direction.getY() > 0) {
						m_ball.negateY();
					}
				}
			} else if (ballX > x2 && ballY < y1) {//top right corner
				int distanceSquared = (ballX - x2)*(ballX - x2) + (ballY - y1)*(ballY - y1);
				if (distanceSquared <= RADIUS*RADIUS) {
					if (direction.getX() < 0 && direction.getY() > 0) {
						m_ball.posateX();
						m_ball.negateY();
					} else if (direction.getX() > 0 && direction.getY() > 0) {
						m_ball.negateY();
					} else if (direction.getX() < 0 && direction.getY() < 0) {
						m_ball.posateX();
					}
				}
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
		g2.setFont(new Font("Arial", Font.BOLD, 11 + 11*getWidth()/X));
		String score = "Score: " + SCORE;
		int scoreWidth = g2.getFontMetrics().stringWidth(score);
		String level = "Level: " + LEVEL;
		int levelWidth = g2.getFontMetrics().stringWidth(level);
		String lives = "Lives: " + LIVES;
		int livesWidth = g2.getFontMetrics().stringWidth(lives);
		g2.drawString(score, getWidth()/7 - scoreWidth/2, 20);
		g2.drawString(level, getWidth()/2 - levelWidth/2, 20);
		g2.drawString(lives, getWidth()*6/7 - livesWidth/2, 20);
		m_brickList.draw(g2);
		m_paddle.draw(g2);
		m_ballList.draw(g2);

		if (m_gameState == GameState.GAME_PAUSED) {
			g2.setFont(new Font("Arial", Font.BOLD, 11 + 11*getWidth()/X));
			g2.setColor(Color.WHITE);
			String paused = "PAUSED";
			int width = g2.getFontMetrics().stringWidth(paused);
			g2.drawString(paused, getWidth()/2 - width/2, 2*getHeight()/3);
			g2.setFont(new Font("Arial", Font.PLAIN, 8 + 8*getWidth()/X));
			String resume = "Press space bar to resume.";
			int w = g2.getFontMetrics().stringWidth(resume);
			g2.drawString(resume, getWidth()/2 - w/2, 2*getHeight()/3 + 18);
		}
	
		if (m_gameState == GameState.GAME_STOPPED) {
			g2.setFont(new Font("Arial", Font.PLAIN, 8 + 8*getWidth()/X));
			g2.setColor(Color.WHITE);
			String play = "Press enter to start ball movement.";
			int w = g2.getFontMetrics().stringWidth(play);
			g2.drawString(play, getWidth()/2 - w/2, 2*getHeight()/3);
		}

		if (m_gameState == GameState.GAME_OVER) {
			g2.setFont(new Font("Arial", Font.BOLD, 11 + 11*getWidth()/X));
			g2.setColor(Color.WHITE);
			String over = "GAME OVER";
			int width = g2.getFontMetrics().stringWidth(over);
			g2.drawString(over, getWidth()/2 - width/2, 2*getHeight()/3);
			g2.setFont(new Font("Arial", Font.PLAIN, 8 + 8*getWidth()/X));
			String start = "Press enter to start new game.";
			int w = g2.getFontMetrics().stringWidth(start);
			g2.drawString(start, getWidth()/2 - w/2, 2*getHeight()/3 + 18);
		}
	}
}
