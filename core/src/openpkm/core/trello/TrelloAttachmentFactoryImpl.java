/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Attachment;
import java.awt.Image;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.DisplayNameProvider;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport.State;
import openpkm.trello.TrelloAttachment;
import openpkm.youtube.YouTubeUtils;
import org.openide.nodes.Children;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;
import openpkm.trello.TrelloAttachmentFactory;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloAttachmentFactory.class)
public class TrelloAttachmentFactoryImpl implements TrelloAttachmentFactory
{    
    private static final Logger LOG = Logger.getLogger(TrelloAttachmentFactory.class.getName());    

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
    
    private static final class TrelloAttachmentImpl implements TrelloAttachment, DisplayNameProvider, IconProvider
    {         
        private final Properties props;    
        
        private Lookup lkp; 
        private State state;         
        
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
        
        @Override
        public Integer getAttachmentPosition() 
        {
            String string = props.getProperty(PROP_ATTACHMENT_POSITION);
            if(string != null)
            {
                try
                {
                    return Integer.parseInt(string);
                }
                catch(NumberFormatException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
            return null;
        }         
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        } 
        
        @Override
        public boolean merge(PropertiesProvider provider)
        {
            if(props.equals(provider.getProperties()))       
            {
                return false;
            }
            props.putAll(provider.getProperties());        
            return true;
        } 
        
        @Override
        public boolean isModified() 
        {
            return state == State.MODIFIED;
        }

        @Override
        public void markModified()
        {
            state = State.MODIFIED;      
        }   

        @Override
        public boolean isDeleted() 
        {
            return state == State.DELETED;
        }                      
        
// TODO DisplayNameProvider        
        
        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return getAttachmentName();
            }
            return null;            
        }        

// TODO IconProvider  
        
        @Override
        public Image getIcon(int type) 
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
            return provider.getImage(IconsProvider.ICON.ATTACHMENT);
        }         
        
// TODO NodeProvider         

        @Override
        public String getName() 
        {
            return getAttachmentID();
        }
        
        @Override
        public Lookup getLookup() 
        {
            if (lkp == null) 
            {
                lkp = Lookups.fixed(this);              
            }
            return lkp;
        }                  
        
        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }  
        
        @Override
        public HelpCtx getHelp()
        {
            return HelpCtx.DEFAULT_HELP;
        }        
        
        @Override
        public int getPosition() 
        {
            Integer position = getAttachmentPosition();
            if(position != null)
            {
                return position.intValue();
            }
            return -1;
        }        
    }     
}
