package me.nov.cafebabe.utils.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import me.nov.cafebabe.Cafebabe;

public class Images {
	public static ImageIcon combine(ImageIcon icon1, ImageIcon icon2) {
		Image img1 = icon1.getImage();
		Image img2 = icon2.getImage();

		int w = icon1.getIconWidth();
		int h = icon1.getIconHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.drawImage(img1, 0, 0, null);
		g2.drawImage(img2, 0, 0, null);
		g2.dispose();

		return new ImageIcon(image);
	}

	public static BufferedImage watermark(BufferedImage old) {
		BufferedImage copy = broadenImage(old);
		Graphics2D g2d = copy.createGraphics();
		g2d.setPaint(Color.black);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		FontMetrics fm = g2d.getFontMetrics();
		String watermark = Cafebabe.gui.getTitle();
		int x = copy.getWidth() - fm.stringWidth(watermark) - 5;
		int y = fm.getHeight();
		g2d.drawString(watermark, x, y);
		g2d.dispose();
		return copy;
	}

	private static BufferedImage broadenImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth() + 60, source.getHeight() + 60, source.getType());
		Graphics g = b.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, source.getWidth() + 60, source.getHeight() + 60);
		g.setColor(Color.BLACK);
		g.drawImage(source, 30, 30, null);
		g.dispose();
		return b;
	}
}
