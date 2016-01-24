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

public class HomeScreen extends JComponent {
	
	public HomeScreen() {	
	}

	public void paintComponent(Graphics g) {
	Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  // antialiasing look nicer
				RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setStroke(new BasicStroke(getHeight()));
	g2.setColor(Color.BLACK);
	g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		g2.setColor(Color.WHITE);
		g2.drawString("Breakout", 50, 50);
		g2.drawString("Press Enter to start the game!", 50, 100);
		g2.drawString("Instructions:", 50, 120);
		g2.drawString("Press the left/right arrow keys to move the paddle.", 50, 140);
		g2.drawString("Press the Space Bar to pause the game.", 50, 160);
		g2.drawString("Press Enter to start the ball moving.", 50, 180);
	}
}
