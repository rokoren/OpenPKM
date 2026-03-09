/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rokor
 */
public interface OpenIconProvider 
{
    Image getOpenedIcon(int type);
    void addChangeListener(ChangeListener listener);  
    void removeChangeListener(ChangeListener listener);      
}
