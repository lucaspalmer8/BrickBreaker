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

public class Game {

	public static boolean m_gameStarted = false;

	public static Breakout m_breakout = new Breakout();

	public static HomeScreen m_homeScreen = new HomeScreen();

	public static JFrame m_frame = new JFrame("Breakout");
	
	public static void main(String[] args) {
        
        m_frame.addKeyListener(new GameKeyListener());
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setResizable(true);
        m_frame.setSize(1000, 500);
        m_frame.setContentPane(m_homeScreen);
        m_frame.setVisible(true);
    }

	public static class GameKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					if (!m_gameStarted) {
						return;
					}
//                  System.out.println("Right key typed");
                    m_breakout.m_paddle.setDirection(1);
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
  //                System.out.println("Left key typed");
					if (!m_gameStarted) {
                        return;
                    }
                    m_breakout.m_paddle.setDirection(-1);
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					System.out.println("Enter pressed");
					if (!m_gameStarted) {
						m_gameStarted = true;
						System.out.println("Setting the content");
//						m_frame.remove(m_homeScreen);
						m_frame.setContentPane(m_breakout);
						m_frame.getContentPane().revalidate();
						//m_frame.repaint();
                        return;
                    }
                    if (m_breakout.m_ball.getSpeed() == 0) {
                        m_breakout.m_ball.setDirection(-1, -1);
                        m_breakout.m_ball.setSpeed(10);
                        //new Thread(canvas.new BallDrawer()).start();
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    //                System.out.println("Right key typed");
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
      //              System.out.println("Left key typed");
                }
            }
	}
}
