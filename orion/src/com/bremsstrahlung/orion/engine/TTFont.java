package com.bremsstrahlung.orion.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TTFont {
	private int textureId;
	private int fontSize;
	
	public TTFont(String fileName, int fontSize) throws IOException, FontFormatException {
		this.fontSize = fontSize;
		
		InputStream is = getClass().getResourceAsStream(fileName);
		
		Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize);
		
		BufferedImage img = new BufferedImage(fontSize * 256, fontSize, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D)img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		g.setColor(Color.WHITE);
		
		g.drawString("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 0, 0);
		
		ByteBuffer data = BufferUtils.createByteBuffer(256 * 4);
		data.flip();
		
		data.put(((DataBufferByte)img.getData().getDataBuffer()).getData());
		
		textureId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);	
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, fontSize * 256, fontSize, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
	}
	
	public void drawString(String string) {
		char[] chars = string.toCharArray();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		
		for(int i = 0; i < chars.length; ++i) {
			
		}
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}
