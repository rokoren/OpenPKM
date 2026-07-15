/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author rok
 */
public interface YouTubeChannelFactory extends SourceFactory<YouTubeChannel>
{
    YouTubeChannel getChannel(Properties props);       
    YouTubeChannel getChannel(String channelID);     
}
