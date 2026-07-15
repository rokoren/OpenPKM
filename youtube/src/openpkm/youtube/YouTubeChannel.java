/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import java.util.List;
import openpkm.base.DescriptionProvider;
import openpkm.base.Source;
import openpkm.base.TitleProvider;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeChannel extends Source, TitleProvider, DescriptionProvider
{  
    String YOUTUBE_CHANNEL_URL = "https://www.youtube.com/channel/";    
    
    String PROP_CHANNEL_ID            = "channel.id";
    String PROP_THUMBNAIL             = "thumbnail";
    String PROP_PUBLISHED_AT          = "published.at";
    String PROP_CUSTOM_URL            = "custom.url";    
    String PROP_COUNTRY               = "country";
    String PROP_LOCALIZED_TITLE       = "localized.title";
    String PROP_LOCALIZED_DESCRIPTION = "localized.description";    
    String PROP_VIEW_COUNT            = "view.count";  
    String PROP_SUBSCRIBER_COUNT      = "subscriber.count"; 
    String PROP_VIDEO_COUNT           = "video.count"; 
    String PROP_COMMENT_COUNT         = "comment.count"; 
    String PROP_TOPIC_CATEGORIES      = "topic.categories";  
    String PROP_PRIVACY_STATUS        = "privacy.status";     
    
    String getChannelID();
    String getThumbnail(); 
    void setThumbnail(String thumbnail);
    DateTime getPublishedAt();
    void setPublishedAt(DateTime publishedAt);
    String getCustomUrl();
    void setCustomUrl(String customerUrl);
    String getCountry();
    void setCountry(String country);
    String getLocalizedTitle();
    void setLocalizedTitle(String localizedTitle);
    String getLocalizedDescription();
    void setLocalizedDescription(String localizedDescription);
    Long getViewCount();
    void setViewCount(Long viewCount);
    Long getSubscriberCount();
    void setSubscriberCount(Long subscriberCount);
    Long getVideoCount();
    void setVideoCount(Long videoCount);
    Long getCommentCount();
    void setCommentCount(Long commentCount);
    List<String> getTopicCategories();
    void setTopicCategories(List<String> topicCategories);
    String getPrivacyStatus();
    void setPrivacyStatus(String privacyStatus);     
}
