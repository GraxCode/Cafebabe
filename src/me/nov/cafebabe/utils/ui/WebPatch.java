package me.nov.cafebabe.utils.ui;

import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.alee.utils.ImageFilterUtils;

public class WebPatch {
	public static void patchGreyscaleLag() throws Exception {
		Field f = ImageFilterUtils.class.getDeclaredField("grayscaleColorConvert");
		f.setAccessible(true);
		Field modifiers = Field.class.getDeclaredField("modifiers");
		modifiers.setAccessible(true);
		modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);

		f.set(null, new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), null));
	}
}
