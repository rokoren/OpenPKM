/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

/**
 *
 * @author Rok Koren
 */
public class CircleIcon implements Icon
{
    private int width;
    private int height;
    private Color color;

    public CircleIcon(int width, int height, Color color) 
    {
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) 
    {
        BufferedImage image = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        int diameter = Math.min(getIconWidth(), getIconHeight());
        g2d.fillOval(0, 0, diameter, diameter);
        g2d.dispose();

        g.drawImage(image, x, y, c);
    }

    @Override
    public int getIconWidth() 
    {
        return width;
    }

    @Override
    public int getIconHeight() 
    {
        return height;
    }      
}
