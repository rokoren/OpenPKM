/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import com.google.api.client.util.DateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import openpkm.base.DomainChannel;
import openpkm.youtube.YouTubeChannel;

/**
 *
 * @author Rok Koren
 */
public abstract class AbstractDomainChannel implements DomainChannel 
{
    private final String channelID;
    protected final Properties props; 
    
    private String domainID, name;         
    
    public AbstractDomainChannel(String channelID, Properties props) 
    {
        this.channelID = channelID;
        this.props = props;
    }  
    
    @Override
    public String getChannelID() 
    {
        return channelID;
    }          

    @Override
    public String getDomainID() 
    {
        return domainID;
    }

    @Override
    public void setDomainID(String domainID)
    {
        this.domainID = domainID;
    } 

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    } 
    
    @Override
    public Properties getProperties()
    {
        return props;
    }    
    
    public static DomainChannel getDomainChannel(String domainID, String channelID, String name, Properties props, DomainChannel.Type type)
    {
        if(type == Type.YOUTUBE_CHANNEL)
        {
            DomainChannel channel = new YouTubeChannelImpl(channelID, props);
            channel.setDomainID(domainID);
            channel.setName(name);            
            return channel;
        }
        return null;
    }
    
    private static final class YouTubeChannelImpl extends AbstractDomainChannel implements YouTubeChannel
    {
        public YouTubeChannelImpl(String channelID, Properties props) 
        {
            super(channelID, props);
        } 
        
        @Override
        public Type getType()
        {
            return Type.YOUTUBE_CHANNEL;
        }         
        
        @Override
        public String getTitle() 
        {
            return props.getProperty(PROP_TITLE);
        }

        @Override
        public void setTitle(String title) 
        {
            if(title == null)
            {
                props.remove(PROP_TITLE);  
            }
            else
            {
                props.setProperty(PROP_TITLE, title);                  
            }
        }  
        
        @Override
        public String getDescription() 
        {
            return props.getProperty(PROP_DESCRIPTION);
        }

        @Override
        public void setDescription(String desc) 
        {
            if(desc == null)
            {
                props.remove(PROP_DESCRIPTION);                 
            }
            else
            {
                props.setProperty(PROP_DESCRIPTION, desc);                                 
            }
        }         

        @Override
        public String getThumbnail() 
        {
            return props.getProperty(PROP_THUMBNAIL);
        }

        @Override
        public void setThumbnail(String thumbnail) 
        {
            if(thumbnail == null)
            {
                props.remove(PROP_THUMBNAIL); 
            }
            else
            {
                props.setProperty(PROP_THUMBNAIL, thumbnail); 
            }
        }

        @Override
        public DateTime getPublishedAt()
        {
            String publishedAt = props.getProperty(PROP_PUBLISHED_AT);
            if(publishedAt != null)
            {
                return new DateTime(publishedAt);
            }
            return null;        
        }

        @Override
        public void setPublishedAt(DateTime time) 
        {
            if(time == null)
            {
                props.remove(PROP_PUBLISHED_AT);
            }
            else
            {
                props.setProperty(PROP_PUBLISHED_AT, time.toStringRfc3339());                   
            }
        } 

        @Override
        public String getCustomUrl() 
        {
            return props.getProperty(PROP_CUSTOM_URL);
        }

        @Override
        public void setCustomUrl(String url) 
        {
            if(url == null)
            {
                props.remove(PROP_CUSTOM_URL);
            }
            else        
            {
                props.setProperty(PROP_CUSTOM_URL, url);  
            } 
        } 

        @Override
        public String getCountry() 
        {
            return props.getProperty(PROP_COUNTRY);
        }

        @Override
        public void setCountry(String country) 
        {
            if(country == null)
            {
                props.remove(PROP_COUNTRY);
            }
            else        
            {
                props.setProperty(PROP_COUNTRY, country);  
            } 
        }  

        @Override
        public String getLocalizedTitle() 
        {
            return props.getProperty(PROP_LOCALIZED_TITLE);
        }

        @Override
        public void setLocalizedTitle(String localizedTitle) 
        {
            if(localizedTitle == null)
            {
                props.remove(PROP_LOCALIZED_TITLE);
            }
            else        
            {
                props.setProperty(PROP_LOCALIZED_TITLE, localizedTitle);  
            } 
        } 

        @Override
        public String getLocalizedDescription() 
        {
            return props.getProperty(PROP_LOCALIZED_DESCRIPTION);
        }

        @Override
        public void setLocalizedDescription(String localizedDescription) 
        {
            if(localizedDescription == null)
            {
                props.remove(PROP_LOCALIZED_DESCRIPTION);
            }
            else        
            {
                props.setProperty(PROP_LOCALIZED_DESCRIPTION, localizedDescription);  
            } 
        } 

        @Override
        public Long getViewCount()
        {
            String viewCount = props.getProperty(PROP_VIDEO_COUNT);
            if(viewCount != null)
            {
                return Long.parseLong(viewCount);
            }
            return null;        
        }

        @Override
        public void setViewCount(Long viewCount) 
        {
            if(viewCount == null)
            {
                props.remove(PROP_VIDEO_COUNT);          
            }
            else
            {
                props.setProperty(PROP_VIDEO_COUNT, viewCount.toString());
            }
        }  

        @Override
        public Long getSubscriberCount()
        {
            String subscriberCount = props.getProperty(PROP_SUBSCRIBER_COUNT);
            if(subscriberCount != null)
            {
                return Long.parseLong(subscriberCount);
            }
            return null;        
        }

        @Override
        public void setSubscriberCount(Long subscriberCount) 
        {
            if(subscriberCount == null)
            {
                props.remove(PROP_SUBSCRIBER_COUNT);          
            }
            else
            {
                props.setProperty(PROP_SUBSCRIBER_COUNT, subscriberCount.toString());
            }
        }  

        @Override
        public Long getVideoCount()
        {
            String videoCount = props.getProperty(PROP_VIDEO_COUNT);
            if(videoCount != null)
            {
                return Long.parseLong(videoCount);
            }
            return null;        
        }

        @Override
        public void setVideoCount(Long videoCount) 
        {
            if(videoCount == null)
            {
                props.remove(PROP_VIDEO_COUNT);        
            }
            else
            {
                props.setProperty(PROP_VIDEO_COUNT, videoCount.toString());
            }
        }  

        @Override
        public Long getCommentCount()
        {
            String commentCount = props.getProperty(PROP_COMMENT_COUNT);
            if(commentCount != null)
            {
                return Long.parseLong(commentCount);
            }
            return null;        
        }

        @Override
        public void setCommentCount(Long commentCount) 
        {
            if(commentCount == null)
            {
                props.remove(PROP_COMMENT_COUNT);           
            }
            else
            {
                props.setProperty(PROP_COMMENT_COUNT, commentCount.toString());
            }
        }  

        @Override
        public List<String> getTopicCategories() 
        {
            String topicCategories = props.getProperty(PROP_TOPIC_CATEGORIES);
            if(topicCategories != null)
            {
                return List.of(topicCategories.split(","));                   
            }                
            return Collections.EMPTY_LIST;
        }  

        @Override
        public void setTopicCategories(List<String> topicCategories)
        {
            if(topicCategories == null)
            {
                props.remove(PROP_TOPIC_CATEGORIES);          
            }
            else
            {
                StringJoiner joiner = new StringJoiner(",");
                for(String topicCategory : topicCategories)
                {
                    joiner.add(topicCategory);
                }
                props.setProperty(PROP_TOPIC_CATEGORIES, joiner.toString());
            }        
        }

        @Override
        public String getPrivacyStatus() 
        {
            return props.getProperty(PROP_PRIVACY_STATUS);
        }

        @Override
        public void setPrivacyStatus(String privacyStatus) 
        {
            if(privacyStatus == null)
            {
                props.remove(PROP_PRIVACY_STATUS);
            }
            else        
            {
                props.setProperty(PROP_PRIVACY_STATUS, privacyStatus);  
            } 
        }

        @Override
        public DateTime getLastUploadTime()
        {
            String string = props.getProperty(PROP_LAST_UPLOAD_TIME);
            if(string != null)
            {
                return new DateTime(string);
            }
            return null;
        }

        @Override
        public void setLastUploadTime(DateTime time)
        {
            if(time == null)
            {
                props.remove(PROP_LAST_UPLOAD_TIME);          
            }
            else
            {
                props.setProperty(PROP_LAST_UPLOAD_TIME, time.toStringRfc3339());        
            }
        }               
        
        @Override
        public String toString()
        {
            return getTitle();
        }        
    }
}
