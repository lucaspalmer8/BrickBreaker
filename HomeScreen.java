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
        /*for (Brick brick : m_brickList) {
            brick.draw(g2);
        }*/
//        m_brickList.draw(g2);
  //      m_paddle.draw(g2);
    //    m_ball.draw(g2);
        //g2.drawLine(0, 0, getWidth(), getHeight());  // draw line 
        //g2.setColor(Color.RED);
        //g2.drawLine(getWidth(), 0, 0, getHeight());  
    }

}
