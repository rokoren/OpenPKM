/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.raindrop;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.raindrop.Raindrop;
import openpkm.raindrop.RaindropFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=RaindropFactory.class)
public class RaindropFactoryImpl implements RaindropFactory
{
    private static final Logger LOG = Logger.getLogger(RaindropFactory.class.getName()); 
    
    @Override
    public Raindrop getRaindrop(Properties props)
    {
        return AbstractRaindrop.getRaindrop(props);
    } 
    
    @Override
    public void save(Raindrop raindrop, OutputStream os, String comments) throws IOException
    {
        raindrop.getProperties().store(os, comments);
        LOG.info("Raindrop saved");      
    }     
}
