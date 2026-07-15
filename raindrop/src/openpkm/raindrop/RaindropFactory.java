/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author Rok Koren
 */
public interface RaindropFactory extends SourceFactory<Raindrop>
{
    public Raindrop getRaindrop(Properties props);     
}
