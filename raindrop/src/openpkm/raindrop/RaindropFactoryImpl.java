/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.raindrop;

import java.util.Properties;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=RaindropFactory.class)
public class RaindropFactoryImpl implements RaindropFactory
{
    @Override
    public Raindrop getRaindrop(Properties props)
    {
        return AbstractRaindrop.getRaindrop(props);
    }   
}
