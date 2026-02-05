/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloCard;
import openpkm.trello.TrelloCardProvider;
import openpkm.trello.TrelloCardsProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Rok Koren
 */
public class TrelloCardProviderImpl implements TrelloCardProvider
{    
    private static final Logger LOG = Logger.getLogger(TrelloCardProvider.class.getName());     
    
    @Override
    public TrelloCard getCard(FileObject fo) 
    {
        if(fo.isFolder())
        {
            try
            {
                Project project = ProjectManager.getDefault().findProject(fo);
                if(project != null)
                {
                    return project.getLookup().lookup(TrelloCard.class);  
                }                
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }
        else
        {
            try
            {
                Properties props = Utils.getProperties(fo); 
                return new TrelloCardImpl(props);
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }
        return null;
    }

    @Override
    public TrelloCard createCard(Properties props, TrelloCardsProvider provider)
    {
        if(isCardLink(props))
        {
            try
            {
                FileObject file = provider.getRootDirectory().createData(getCardID(props), PropertiesProvider.EXTENSION);
                OutputStream os = file.getOutputStream();
                props.store(os, "OpenPKM Trello Card Link"); 
                os.close();   
                
                return getCard(file);
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }              
        }
        else
        {
            try
            {
                FileObject projectDirectory = FileUtil.createFolder(provider.getRootDirectory(), getCardID(props));           
                FileObject projectFolder = FileUtil.createFolder(projectDirectory, TrelloCardProjectFactory.PROJECT_FOLDER);                   

                OutputStream os = projectFolder.createAndOpen(TrelloCardProjectFactory.PROJECT_FILE);
                props.store(os, "OpenPKM Trello Card Project"); 
                os.close();    

                return getCard(projectDirectory);
            }
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }         
        }
        return null;
    }
    
    public static String getCardID(Properties props)
    {
        return props.getProperty(PROP_CARD_ID);      
    }    
    
    public static boolean isCardLink(Properties props)
    {
        String string = props.getProperty(PROP_CARD_LINK);
        if(string != null)
        {
            return Boolean.parseBoolean(string);  
        }
        return false;        
    }
    
    private static final class TrelloCardImpl implements TrelloCard, PropertiesProvider
    {         
        private final Properties props;     
        
        public TrelloCardImpl(Properties props)
        {
            this.props = props;              
        }     

// TODO TrelloCard        
        
        @Override
        public String getAppID() 
        {
            return props.getProperty(PROP_APP_ID);
        }   

        @Override
        public LocalDateTime getTimeCreated() 
        {
            String string = props.getProperty(PROP_TIME_CREATED);
            if(string != null)
            {
                return LocalDateTime.parse(string, DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        }  
        
        @Override
        public String getSourceID()
        {
            return getCardID();
        }

        @Override
        public String getBoardID() 
        {
            return props.getProperty(PROP_BOARD_ID);
        }    

        @Override
        public String getListID() 
        {
            return props.getProperty(PROP_LIST_ID);
        } 

        @Override
        public String getCardID() 
        {
            return props.getProperty(PROP_CARD_ID);
        }   

        @Override
        public String getCardName() 
        {
            return props.getProperty(PROP_CARD_NAME);
        }     

        @Override
        public Integer getCardPosition() 
        {
            String string = props.getProperty(PROP_CARD_POSITION);
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
        

        @Override
        public boolean isCardLink() 
        {
            return TrelloCardProviderImpl.isCardLink(props);
        }        
        
// TODO PropertiesProvider        
        
        @Override
        public Properties getProperties()
        {
            return props;
        }                       
    }     
}
