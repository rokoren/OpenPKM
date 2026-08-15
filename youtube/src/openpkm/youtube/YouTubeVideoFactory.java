/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.youtube;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.VideoListResponse;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import openpkm.base.SourceFactory;
import openpkm.base.VisibilityProvider;
import openpkm.base.WorkflowProvider;

/**
 *
 * @author Rok Koren
 */
public interface YouTubeVideoFactory extends SourceFactory<YouTubeVideo> 
{
    YouTubeVideo getVideoBase(Properties props);       
    YouTubeVideo getVideo(Properties props);       
    YouTubeVideo getVideo(GooglePasswordProvider provider, String videoID);  
    
    public static Properties getProperties(VideoListResponse response)
    {
        Properties props = new Properties();

        props.setProperty(WorkflowProvider.PROP_WORKFLOW, WorkflowProvider.Workflow.WATCH_LATER.toString());
        props.setProperty(VisibilityProvider.PROP_VISIBILITY_MODIFIER, VisibilityProvider.Modifier.PROTECTED.toString());            

        String videoID = response.getItems().get(0).getId();
        props.setProperty(YouTubeVideo.PROP_VIDEO_ID, videoID); 
        String videoTitle = response.getItems().get(0).getSnippet().getTitle();
        props.setProperty(YouTubeVideo.PROP_VIDEO_TITLE, videoTitle); 
        String channelID = response.getItems().get(0).getSnippet().getChannelId();
        props.setProperty(YouTubeVideo.PROP_CHANNEL_ID, channelID);  
        String channelTitle = response.getItems().get(0).getSnippet().getChannelTitle();
        props.setProperty(YouTubeVideo.PROP_CHANNEL_TITLE, channelTitle);            
        DateTime publishedAt = response.getItems().get(0).getSnippet().getPublishedAt();
        props.setProperty(YouTubeVideo.PROP_PUBLISHED_AT, publishedAt.toStringRfc3339()); 
        String duration = response.getItems().get(0).getContentDetails().getDuration();  
        props.setProperty(YouTubeVideo.PROP_DURATION, duration); 
        String caption = response.getItems().get(0).getContentDetails().getCaption();  
        props.setProperty(YouTubeVideo.PROP_CAPTION, caption);             
        BigInteger views = response.getItems().get(0).getStatistics().getViewCount();  
        if(views != null)
        {
            props.setProperty(YouTubeVideo.PROP_VIEW_COUNT, views.toString());                 
        }
        List<String> youTubeTags = response.getItems().get(0).getSnippet().getTags();
        if(youTubeTags != null)
        {
            StringJoiner joiner = new StringJoiner(",");
            for(String tag : youTubeTags)
            {
                joiner.add(tag);
            }
            props.setProperty(YouTubeVideo.PROP_YOUTUBE_TAGS, joiner.toString());
        }   
        if(response.getItems().get(0).getTopicDetails() != null)
        {
            List<String> topicCategories = response.getItems().get(0).getTopicDetails().getTopicCategories();                
            if(topicCategories != null)
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String topic : topicCategories)
                {
                    joiner.add(topic);
                }
                props.setProperty(YouTubeVideo.PROP_TOPIC_CATEGORIES, joiner.toString());
            }                                                
        }             
        String defaultLanguage = response.getItems().get(0).getSnippet().getDefaultLanguage();
        props.setProperty(YouTubeVideo.PROP_DEFAULT_LANGUAGE, defaultLanguage);
        String defaultAudioLanguage = response.getItems().get(0).getSnippet().getDefaultAudioLanguage();               
        props.setProperty(YouTubeVideo.PROP_DEFAULT_AUDIO_LANGUAGE, defaultAudioLanguage);
        String description = response.getItems().get(0).getSnippet().getDescription();
        props.setProperty(YouTubeVideo.PROP_DESCRIPTION, description); 
        String liveBroadcastContent = response.getItems().get(0).getSnippet().getLiveBroadcastContent();
        props.setProperty(YouTubeVideo.PROP_LIVEBROADCAST_CONTENT, liveBroadcastContent); 

        String thumbnailDefault = response.getItems().get(0).getSnippet().getThumbnails().getDefault().getUrl();
        props.setProperty(YouTubeVideo.PROP_THUMBNAIL_DEFAULT, thumbnailDefault); 
        String thumbnailMedium = response.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
        props.setProperty(YouTubeVideo.PROP_THUMBNAIL_MEDIUM, thumbnailMedium); 
        String thumbnailHigh = response.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
        props.setProperty(YouTubeVideo.PROP_THUMBNAIL_HIGH, thumbnailHigh); 
        if(response.getItems().get(0).getSnippet().getThumbnails().getStandard() != null)
        {
            String thumbnailStandard = response.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();  
            props.setProperty(YouTubeVideo.PROP_THUMBNAIL_STANDARD, thumbnailStandard);                
        }             

        return props;
    }     
}
