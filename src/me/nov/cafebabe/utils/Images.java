package me.nov.cafebabe.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

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
}
