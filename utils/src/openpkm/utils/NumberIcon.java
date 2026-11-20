/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Rok Koren
 */
public class NumberIcon extends ImageIcon
{
    private String number;
    
    private int diameter;

    // Constructor
    public NumberIcon(String number, Type type, Color color, int diameter) 
    {
        this.number = number;
        this.diameter = diameter;
        setImage(createIconImage(type, color));
    }

    // Create the icon image with initials inside a circle
    private Image createIconImage(Type type, Color color) 
    {
        if(type == Type.CIRCLE)
        {
            return createCircleIconImage(color);
        }
        return createRectangleIconImage(color);
    }    
    
    // Create the icon image with initials
    private Image createRectangleIconImage(Color color) {
        // Create a BufferedImage with 16x16 size
        BufferedImage image = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        // Get the Graphics object to draw on the image
        Graphics2D g = image.createGraphics();

        // Set anti-aliasing for better text quality
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the background with a solid color (optional)
        g.setColor(color); // Set background color
        g.fillRect(0, 0, diameter, diameter);

        // Set text properties
        g.setColor(Color.BLACK); // Set text color
        g.setFont(new Font("Arial", Font.BOLD, 10));

        // Calculate the width and height of the text to center it
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(number);
        int textHeight = fm.getHeight();

        // Draw the initials in the center of the image
        g.drawString(number, (diameter - textWidth) / 2, (diameter + textHeight) / 2 - fm.getDescent());

        // Dispose of the Graphics object
        g.dispose();

        return image;
    }
    
    // Create the icon image with initials inside a circle
    private Image createCircleIconImage(Color color) 
    {
        // Create a BufferedImage with the same width and height for the circle
        BufferedImage image = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

        // Get the Graphics object to draw on the image
        Graphics2D g = image.createGraphics();

        // Set anti-aliasing for better graphics quality
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill the background with a circular color
        g.setColor(color); // Set background color (circle)
        g.fillOval(0, 0, diameter, diameter);

        // Set text properties (color and font)
        g.setColor(Color.BLACK); // Set text color
        g.setFont(new Font("Arial", Font.BOLD, 8));

        // Calculate the width and height of the text to center it
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(number);
        int textHeight = fm.getHeight();

        // Draw the initials in the center of the circle
        g.drawString(number, (diameter - textWidth) / 2, (diameter + textHeight) / 2 - fm.getDescent());

        // Dispose of the Graphics object
        g.dispose();

        return image;
    }  
    
    public enum Type 
    {
        CIRCLE,
        RECTANGLE;    
    }      
}
