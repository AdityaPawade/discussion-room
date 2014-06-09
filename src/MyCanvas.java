import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MyCanvas extends JPanel {

	public MyCanvas() {
		super();
	}

	public void paintComponent(Graphics g) {
		// g.setColor(Color.RED);
		// g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void clearCanvas() {
		Graphics g = getGraphics();
		g.clearRect(0, 0, g.getClipBounds(getVisibleRect()).width,
				g.getClipBounds(getVisibleRect()).height);
		setbkg(Color.RED);
	}

	public void setbkg(Color color) {
		Graphics g = getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void DrawLine(int x1, int y1, int x2, int y2) {
		Graphics g = getGraphics();
		g.drawLine(x1, y1, x2, y2);
	}
}
