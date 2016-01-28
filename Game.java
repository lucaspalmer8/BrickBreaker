import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
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
		if (args.length == 2) {
			m_breakout.setRates(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
		m_frame.addKeyListener(new GameKeyListener());
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.setResizable(true);
		m_frame.setSize(Breakout.X, Breakout.Y);
		m_frame.setMinimumSize(new Dimension(200, 300));
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
//				System.out.println("Setting the direction to 1");
				m_breakout.getPaddle().setDirection(1);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (!m_gameStarted) {
					return;
				}
//				System.out.println("setting the direction to -1");
				m_breakout.getPaddle().setDirection(-1);
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!m_gameStarted) {
					m_gameStarted = true;
					m_frame.setContentPane(m_breakout);
					m_frame.getContentPane().revalidate();
//					m_breakout.startThread();
					return;
				}
				if (m_breakout.getGameState() == Breakout.GameState.GAME_OVER) {
					m_breakout.startNewGame();
				} else if (m_breakout.getBall().getSpeed() == 0) {
					m_breakout.getBall().setDirection(-1, 1);
					m_breakout.getBall().setSpeed(10);
					m_breakout.setGameState(Breakout.GameState.GAME_STARTED);
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
//					System.out.println("Setting the direction to 0");
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				if (m_breakout.getPaddle().getDirection() < 0) {
//					System.out.println("Setting the direction to 0");
					m_breakout.getPaddle().setDirection(0);
				}
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
//			System.out.println("Key typed");
		}
	}
}
