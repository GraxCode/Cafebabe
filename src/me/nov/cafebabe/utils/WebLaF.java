package me.nov.cafebabe.utils;

import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.alee.extended.image.WebImage;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.panel.WebOverlay;
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

}
