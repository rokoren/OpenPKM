/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeVideoFactory extends SourceFactory<YouTubeVideo> 
{
    YouTubeVideo getVideo(Properties props, Type type);       
    YouTubeVideo getVideo(String videoID, Type type);     
    
    public enum Type 
    {
        BASIC,
        STANDARD,
        WATCH_LATER;    
    }      
}
