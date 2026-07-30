/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 *
 * @author rok
 */
public class NotificationUtils 
{
    public static JComponent createDetails(String text, ActionListener action, Icon icon) 
    {
        if (null == action) 
        {
            return new JLabel(text);
        }   

        JButton btn = new JButton(Utils.convertStringToHtml(text, 50));
        btn.setFocusable(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFont(btn.getFont().deriveFont(btn.getFont().getSize() + 2));
        btn.addActionListener(action);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color c = UIManager.getColor("nb.html.link.foreground"); //NOI18N
        if (c != null) {
            btn.setForeground(c);
        }
        btn.setIcon(icon);
        btn.setIconTextGap(10);        
        btn.setVerticalTextPosition(SwingConstants.TOP);
        btn.setHorizontalTextPosition(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }      
}
