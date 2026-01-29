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
public class UserIcon extends ImageIcon
{
    private String firstName;
    private String lastName;
    
    private int diameter = 16;

    // Constructor
    public UserIcon(String firstName, String lastName, Type type, Color color) 
    {
        this.firstName = firstName;
        this.lastName = lastName;
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
        g.setColor(Color.DARK_GRAY); // Set text color
        g.setFont(new Font("Arial", Font.BOLD, 10));

        // Get the initials
        String initials = (firstName.isEmpty() ? "" : firstName.substring(0, 1).toUpperCase()) +
                          (lastName.isEmpty() ? "" : lastName.substring(0, 1).toUpperCase());

        // Calculate the width and height of the text to center it
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(initials);
        int textHeight = fm.getHeight();

        // Draw the initials in the center of the image
        g.drawString(initials, (diameter - textWidth) / 2, (diameter + textHeight) / 2 - fm.getDescent());

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
        g.setColor(Color.DARK_GRAY); // Set text color
        g.setFont(new Font("Arial", Font.BOLD, 8));

        // Get the initials
        String initials = (firstName.isEmpty() ? "" : firstName.substring(0, 1).toUpperCase()) +
                          (lastName.isEmpty() ? "" : lastName.substring(0, 1).toUpperCase());

        // Calculate the width and height of the text to center it
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(initials);
        int textHeight = fm.getHeight();

        // Draw the initials in the center of the circle
        g.drawString(initials, (diameter - textWidth) / 2, (diameter + textHeight) / 2 - fm.getDescent());

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
