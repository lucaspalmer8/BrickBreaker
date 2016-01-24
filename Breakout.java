import javax.swing.*;
import java.awt.Color;
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
	
	public static int BRICK_HEIGHT = 20;
	public static int BRICK_WIDTH = 30;
	public static int BALL_RADIUS = 10;
	public static boolean IS_PAUSED = false;

	public Breakout() {
		new Thread(new FrameDrawer()).start();
	}

	public void pauseResume() {
		IS_PAUSED = !IS_PAUSED;
	}

	public class FrameDrawer implements Runnable {
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
			//System.out.println("Break 1");
									break;
								}
								x++;
								y++;
                        	}
						} else {
							for (int i = 0; i < 5; i++) {
                                if (m_brickList.isPointInBricks(x + 1, y - 1)) {
			//System.out.println("Break 2");
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
			//System.out.println("Break 3");
                                    break;
                                }
                                x--;
                                y++;
                            }
                        } else {
                            for (int i = 0; i < 5; i++) {
                                if (m_brickList.isPointInBricks(x - 1, y - 1)) {
			//System.out.println("Break 4");
                                    break;
                                }
                                x--;
                                y--;
                            }
                        }
					}                    

					if (x < 0 || x > getWidth()) {
                        m_ball.reflectX();
                    }
                    if (y < 0 || y > getHeight()) {
                        m_ball.reflectY();
                    }

                    m_ball.setX(x);
                    m_ball.setY(y);
				
					hasItHitAnyBricks();
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
            int m_xPosition;
            int m_yPosition;

            public Point(int x, int y) {
                m_xPosition = x;
                m_yPosition = y;
            }

            public int getX() {
                return m_xPosition;
            }

            public int getY() {
                return m_yPosition;
            }

            public void setX(int x) {
                m_xPosition = x;
            }

            public void setY(int y) {
                m_yPosition = y;
            }
        }


	public class Ball {
		int m_x;
		int m_y;
		int m_speed;
		Point m_direction;

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

		public void draw(Graphics2D g2) {
			if (m_speed == 0) {
				initializePosition();
		 	}

			g2.setColor(Color.RED);
    		g2.fillOval(m_x-10, m_y-10, 20, 20);

            g2.setStroke(new BasicStroke(4));
            g2.setColor(Color.WHITE);
            g2.drawLine(m_x, m_y, m_x, m_y);
        }
	}

	public Ball m_ball = new Ball();

	public class Paddle {
		int xPosition;
		int yPosition;
		int direction;

		public Paddle() {
			xPosition = 10;
			yPosition = 10;
			direction = 0;
		}
		
		public void setDirection(int dir) {
			xPosition += dir*10;
			if (xPosition < 0) {
				xPosition = 0;
			}
			if (xPosition > getWidth()) {
				xPosition = getWidth();
			}
		}

		public void draw(Graphics2D g2) {
            g2.setStroke(new BasicStroke(10));
            g2.setColor(Color.WHITE);
            g2.drawLine(xPosition, getHeight() - 15, xPosition + 30, getHeight() - 15);
        }


	}

	public Paddle m_paddle = new Paddle();

	public class Brick {
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
                if (brickX1 - 10 < ballX && ballX < brickX2 + 10) {  ///Math.abs(ballX - brickX1) <= 10 || Math.abs(ballX - brickX2) <= 10) {
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

		public void draw(Graphics2D g2) {
			g2.setStroke(new BasicStroke(BRICK_HEIGHT));
            g2.setColor(m_color);
            g2.drawLine(getX(), getY(), getX() + BRICK_WIDTH, getY());
			///
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.BLACK);
			g2.drawLine(getX(), getY(), getX(), getY());

			int brickY = m_y;
			int brickX = m_x;
			g2.drawLine(brickX - BRICK_HEIGHT/2, brickY, brickX - BRICK_HEIGHT/2, brickY);
			//g2.drawLine(brickX, brickY =
			//g2.drawLine(brickY - BRICK_HEIGHT/2, brickY + BRICK_HEIGHT/2,brickY - BRICK_HEIGHT/2, brickY + BRICK_HEIGHT/2);
            //g2.drawLine(brickX - BRICK_HEIGHT/2, brickX + BRICK_HEIGHT/2 + BRICK_WIDTH,brickX - BRICK_HEIGHT/2, brickX + BRICK_HEIGHT/2 + BRICK_WIDTH);

		}

		int m_x;
		int m_y;
		Color m_color;
		//boolean leftN;
		//boolean rightN;
		//boolean topN;
		//boolean bottomN;
	}

	public class BrickList {
		public ArrayList<Brick> m_brickList = new ArrayList<Brick>();

		public BrickList() {
			int startingX = 100;
        	int startingY = 80;
        	List<Color> colorList = Arrays.asList(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.ORANGE, Color.WHITE);
        	for (int i = 0; i < 15; i++) {
            	for (int j = 0; j < 6; j++) {
                	m_brickList.add(new Brick(startingX + i * (BRICK_WIDTH + BRICK_HEIGHT + 1), startingY + j * (BRICK_HEIGHT + 1), colorList.get(j)));
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

		public void draw(Graphics2D g2) {
			for (Brick brick : m_brickList) {
            	brick.draw(g2);
        	}
		}
	}

	public BrickList m_brickList = new BrickList();

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
				if (ballY < brickY1 && brickY1 - ballY <= 10 || ballY > brickY2 && ballY - brickY2 <= 10) {       ///Math.abs(ballY - brickY1) <= 10 || Math.abs(ballY - brickY2) <= 10) {
					m_ball.reflectY();
					System.out.println("Here 1");
					m_brickList.remove(i);
				//	incrementPosition();
					break;
					//System.out.println("in the x range");
				}
			} else if (brickY1 <= ballY && ballY <= brickY2) {//in the y range
				//System.out.println("Hehehe");
				if (ballX < brickX1 && brickX1 - ballX <= 10 || ballX > brickX2 && ballX - brickX2 <= 10) {  ///Math.abs(ballX - brickX1) <= 10 || Math.abs(ballX - brickX2) <= 10) {
                    m_ball.reflectX();
					m_brickList.remove(i);
				//	incrementPosition();
					System.out.println("Here 2");
					break;
					//System.out.println("in the y range");
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
						System.out.println("Here 3");
//incrementPosition();

//						System.out.println(j++);
						break;
					}
				} else if (ballX > brickX2 && ballY < brickY1) {//top right corner
					int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY1)*(ballY - brickY1);
					if (distanceSquared <= 100) {
                        if (ballX - brickX2 < brickY1 - ballY) {
                            m_ball.reflectY();
							System.out.println("YYYY");
                        } else {
                            m_ball.reflectX();
                        }
						m_brickList.remove(i);
						System.out.println("Here 4");
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
						System.out.println("Here 5");
						m_brickList.remove(i);
						break;
                    }
				} else if (ballX > brickX2 && ballY > brickY2) {//bottom right corner
                    int distanceSquared = (ballX - brickX2)*(ballX - brickX2) + (ballY - brickY2)*(ballY - brickY2);
                    if (distanceSquared <= 100) {
                        if (ballX - brickX2 < ballY - brickY2) {
							System.out.println("YYY");
                            m_ball.reflectY();
                        } else {
                            m_ball.reflectX();
                        }
						System.out.print("Here 6");
						m_brickList.remove(i);
						break;
                    }
				}
			}
		}
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
        					RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(getHeight()));
		g2.setColor(Color.BLACK);
		g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		/*for (Brick brick : m_brickList) {
			brick.draw(g2);
		}*/
		m_brickList.draw(g2);
		m_paddle.draw(g2);
		m_ball.draw(g2);

		if (IS_PAUSED) {
			g2.setColor(Color.WHITE);
			g2.drawString("PAUSED", getWidth()/2, 2*getHeight()/3);
		}
        //g2.drawLine(0, 0, getWidth(), getHeight());  // draw line 
        //g2.setColor(Color.RED);
        //g2.drawLine(getWidth(), 0, 0, getHeight());  
	}
}
