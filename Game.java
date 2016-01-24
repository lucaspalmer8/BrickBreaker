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
		
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (!m_gameStarted) {
					return;
				}
				m_breakout.getPaddle().setDirection(1);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (!m_gameStarted) {
					return;
				}
				m_breakout.getPaddle().setDirection(-1);
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!m_gameStarted) {
					m_gameStarted = true;
					m_frame.setContentPane(m_breakout);
					m_frame.getContentPane().revalidate();
					return;
				}
				if (m_breakout.getBall().getSpeed() == 0) {
					m_breakout.getBall().setDirection(-1, -1);
					m_breakout.getBall().setSpeed(10);
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				if (m_gameStarted) {
					m_breakout.pauseResume();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (m_breakout.getPaddle().getDirection() > 0) {
					m_breakout.getPaddle().setDirection(0);
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (m_breakout.getPaddle().getDirection() < 0) {
					m_breakout.getPaddle().setDirection(0);
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
