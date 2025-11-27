/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Component;

/**
 *
 * @author Rok Koren
 */
public interface CardPanel 
{
    String getName();
    Component getVisualRepresentation();
    void reset();
    void cardClosed();  
}
