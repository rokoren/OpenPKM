/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.io.OutputStream;

/**
 *
 * @author Rok Koren
 */
public interface RaindropDataProvider 
{
    OutputStream getOutput(Integer raindropID);
}
