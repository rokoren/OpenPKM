/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Component;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Rok Koren
 */
public interface PasswordProvider 
{
    void load();
    void store();
    Component getField();
    String getName();
    String getDisplayName();
    void addListener(ChangeListener listener);
    void removeListener(ChangeListener listener);
}
