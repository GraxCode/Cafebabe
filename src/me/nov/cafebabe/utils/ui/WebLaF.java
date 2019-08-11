package me.nov.cafebabe.utils.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import com.alee.extended.image.WebImage;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebOverlay;
import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.tooltip.TooltipManager;

public class WebLaF {

	private static ImageIcon info;
	static {
		info = new ImageIcon(Toolkit.getDefaultToolkit().getImage(WebLaF.class.getResource("/resources/overlay/info.png")));
	}

	public static GroupPanel createInfoLabel(JLabel nameLabel, String overlayText) {
		WebOverlay overlayPanel = new WebOverlay();
		overlayPanel.setComponent(nameLabel);
		WebImage overlay = new WebImage(info);
		TooltipManager.setTooltip(overlay, overlayText, TooltipWay.trailing, 0);
		overlayPanel.addOverlay(overlay, SwingConstants.TRAILING, SwingConstants.TOP);
		overlayPanel.setComponentMargin(0, 0, 0, overlay.getPreferredSize().width);
		return new GroupPanel(overlayPanel);
	}

	public static JSeparator createSeparator() {
		JSeparator sep = new JSeparator();
		sep.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		sep.setPreferredSize(new Dimension(5, 2));
		return sep;
	}
	
  public static void fixUnicodeSupport() {
  	
  	// if (!WebLookAndFeel.globalControlFont.canDisplay(c))
    WebLookAndFeel.globalControlFont = fixFont(WebLookAndFeel.globalControlFont);
    WebLookAndFeel.globalTooltipFont = fixFont(WebLookAndFeel.globalTooltipFont);
    WebLookAndFeel.globalAlertFont = fixFont(WebLookAndFeel.globalAlertFont);
    WebLookAndFeel.globalMenuFont = fixFont(WebLookAndFeel.globalMenuFont);
    WebLookAndFeel.globalAcceleratorFont = fixFont(WebLookAndFeel.globalAcceleratorFont);
    WebLookAndFeel.globalTitleFont = fixFont(WebLookAndFeel.globalTitleFont);
    WebLookAndFeel.globalTextFont = fixFont(WebLookAndFeel.globalTextFont);
  }

  private static Font fixFont(Font font) {
    return new Font(Font.SANS_SERIF, font.getStyle(), font.getSize());
  }
}
