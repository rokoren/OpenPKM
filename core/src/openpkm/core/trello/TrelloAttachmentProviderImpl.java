/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Attachment;
import java.awt.Image;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloAttachment;
import openpkm.trello.TrelloAttachmentProvider;
import openpkm.youtube.YouTubeUtils;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloAttachmentProvider.class)
public class TrelloAttachmentProviderImpl implements TrelloAttachmentProvider
{
    private static final String MIME_TYPE_PDF  = "application/pdf";
    private static final String MIME_TYPE_PNG  = "image/png";
    private static final String MIME_TYPE_JPEG = "image/jpeg";        
    
    private static final Logger LOG = Logger.getLogger(TrelloAttachmentProvider.class.getName());    

    @Override
    public TrelloAttachment getAttachment(Properties props) 
    {
        return new TrelloAttachmentImpl(props);
    }
    
    @Override
    public TrelloAttachment createAttachment(Attachment attachment) 
    {
        Properties props = new Properties();               
        props.setProperty(PROP_ATTACHMENT_ID, attachment.getId());
        props.setProperty(PROP_ATTACHMENT_URL, attachment.getUrl());
        props.setProperty(PROP_ATTACHMENT_NAME, attachment.getName()); 
        if(attachment.getMimeType() != null)
        {
            props.setProperty(PROP_ATTACHMENT_MIME_TYPE, attachment.getMimeType());             
        }
        return getAttachment(props);
    } 
    
    private static final class TrelloAttachmentImpl implements TrelloAttachment
    { 
        @StaticResource()
        private static final String ICON = "openpkm/core/resources/attach.png";  
        
        private final Properties props;     
        
        public TrelloAttachmentImpl(Properties props)
        {
            this.props = props;              
        }     

// TODO TrelloAttachment        
        
        @Override
        public String getAttachmentID() 
        {
            return props.getProperty(PROP_ATTACHMENT_ID);
        } 
        
        @Override
        public String getAttachmentUrl() 
        {
            return props.getProperty(PROP_ATTACHMENT_URL);
        }         
                
        @Override
        public String getAttachmentName() 
        {
            return props.getProperty(PROP_ATTACHMENT_NAME);
        }
        
        @Override
        public String getAttachmentMimeType() 
        {
            return props.getProperty(PROP_ATTACHMENT_MIME_TYPE);
        }        
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        } 
        
        @Override
        public void merge(PropertiesProvider provider)
        {
            props.putAll(provider.getProperties());
        }        

// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getAttachmentID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getAttachmentName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            String mimeType = getAttachmentMimeType();
            if(mimeType == null || mimeType.isEmpty())
            {
                String url = getAttachmentUrl();
                if(url != null)
                {
                    if(YouTubeUtils.isYouTube(url))
                    {
                        return provider.getImage(IconsProvider.ICON.YOUTUBE_VIDEO);
                    }
                    return provider.getImage(IconsProvider.ICON.WEB_PAGE);
                }                
            }
            if(mimeType.equals(MIME_TYPE_PDF))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PDF);
            }
            else if(mimeType.equals(MIME_TYPE_PNG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_PNG);
            }   
            else if(mimeType.equals(MIME_TYPE_JPEG))
            {
                return provider.getImage(IconsProvider.ICON.FILE_JPG);
            }             
            return ImageUtilities.loadImage(ICON);
        } 
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }        
    }      
}
