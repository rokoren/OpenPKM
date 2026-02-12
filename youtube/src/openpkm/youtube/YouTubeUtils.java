/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.youtube;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rok Koren
 */
public class YouTubeUtils 
{
    private static final Pattern YT_PATTERN = Pattern.compile("^(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:watch\\?v=|embed/|v/)|youtu\\.be/)([A-Za-z0-9_-]{11}).*");             
    
    public static String getVideoID(String url) 
    {
        Matcher m = YT_PATTERN.matcher(url);
        if (m.matches()) {
            return m.group(1);
        }
        return null; // ni YouTube link ali ni ID-ja
    } 

    public static boolean isYouTube(String url)
    {
        return YT_PATTERN.matcher(url).matches();
    }
}
