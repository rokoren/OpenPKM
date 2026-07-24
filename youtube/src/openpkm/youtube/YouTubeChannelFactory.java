/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.SourceFactory;
import org.openide.util.RequestProcessor;

/**
 *
 * @author rok
 */
public interface YouTubeChannelFactory extends SourceFactory<YouTubeChannel>
{
    Logger LOG = Logger.getLogger(YouTubeChannelFactory.class.getName());  
    RequestProcessor RP = new RequestProcessor(YouTubeChannelFactory.class);  
    
    YouTubeChannel getChannel(Properties props);       
    YouTubeChannel getChannel(String channelID);     
}
