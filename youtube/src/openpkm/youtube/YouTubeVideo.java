/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import java.util.List;
import java.util.regex.Pattern;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.Video;
import openpkm.base.WatchLater;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeVideo extends Source, Video, PropertiesProvider, WatchLater
{    
    //String YOUTUBE_URL = "https://www.youtube.com/";
    String YOUTUBE_URL = "https://www.youtube-nocookie.com/";
    //String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    
    Pattern PATTERN_TODO = Pattern.compile("TODO");
    Pattern PATTERN_DONE = Pattern.compile("DONE");       
    
    String PROP_VIDEO_ID               = "video.id";
    String PROP_VIDEO_TITLE            = "video.title";  
    String PROP_DESCRIPTION            = "description";    
    String PROP_CHANNEL_ID             = "channel.id";
    String PROP_CHANNEL_TITLE          = "channel.title";    
    String PROP_PUBLISHED_AT           = "published-at";    
    String PROP_LIVEBROADCAST_CONTENT  = "live-broadcast-content";
    String PROP_CATEGORY_ID            = "category.id";
    String PROP_YOUTUBE_TAGS           = "youtube.tags";
    String PROP_DEFAULT_LANGUAGE       = "default-language";
    String PROP_DEFAULT_AUDIO_LANGUAGE = "default-audio-language";
    String PROP_EMBEDABLE              = "embedable";
    // contentDetails
    String PROP_DURATION               = "duration";
    String PROP_DIMENSION              = "dimension";
    String PROP_DEFINITION             = "definition";    
    String PROP_CAPTION                = "caption";
    String PROP_LICENSED_CONTENT       = "licensed-content";      
    // statistics
    String PROP_VIEW_COUNT             = "statistics.count.view";
    String PROP_LIKE_COUNT             = "statistics.count.like";
    String PROP_DISLIKE_COUNT          = "statistics.count.dislike";
    String PROP_COMMENT_COUNT          = "statistics.count.comment";
    String PROP_FAVORITE_COUNT         = "statistics.count.dislike";
    // thumbnails
    String PROP_THUMBNAIL_DEFAULT      = "thumbnail.default";
    String PROP_THUMBNAIL_MEDIUM       = "thumbnail.medium";
    String PROP_THUMBNAIL_HIGH         = "thumbnail.high";
    String PROP_THUMBNAIL_STANDARD     = "thumbnail.standard";
    String PROP_THUMBNAIL_MAXRES       = "thumbnail.maxres";  
    // topicDetails
    String PROP_TOPIC_CATEGORIES       = "topic-categories";     
    
    String getVideoID();
    String getChannelID();    
    String getVideoTitle();
    void setVideoTitle(String title);
    String getChannelTitle();
    void setChannelTitle(String title); 
    String getDescription();
    void setDescription(String desc);
    DateTime getPublishedAt();
    void setPublishedAt(DateTime time);
    String getThumbnailDefault();
    void setThumbnailDefault(String thumbnail);
    String getThumbnailMedium();
    void setThumbnailMedium(String thumbnail); 
    String getThumbnailHigh();
    void setThumbnailHigh(String thumbnail);
    String getThumbnailStadard();
    void setThumbnailStandard(String thumbnail);     
    String getThumbnail(Thumbnail thumbnail);  
    List<String> getYouTubeTags();
    
    public enum Thumbnail 
    {
        DEFAULT,
        MEDIUM,
        HIGH,
        STANDARD,
        MAXRES     
    }     
}
