/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.domain;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Logger;
import openpkm.domain.Domain;
import openpkm.domain.DomainProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author rok
 */
@ServiceProvider(service=DomainProvider.class)
public class DomainProviderImpl implements DomainProvider
{
    private static final Logger LOG = Logger.getLogger(DomainProvider.class.getName());      

    @Override
    public Domain getDomain(Properties props) 
    {
        return new DomainImpl(props);
    }
    
    private static final class DomainImpl implements Domain
    { 
        private final Properties props; 
        private final PropertyChangeSupport propertyChangeSupport;

        private Lookup lkp;  
        private SourceState state;        
        
        public DomainImpl(Properties props)
        {
            this.props = props;
            propertyChangeSupport = new PropertyChangeSupport(this);
        } 
        
        @Override
        public String getSourceID()
        {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }  
        
        @Override
        public String getAppID() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LocalDateTime getTimeCreated() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void save(OutputStream os, String comments) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void markModified() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void notifyDeleted() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public SourceState getState() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Lookup getLookup() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }        
        
        /*
        @Override
        public FileObject getFile() throws IOException
        {          
            return getFile(AbstractFilesProvider.ARTICLES);
        }  
        
        @Override
        public void setFile(FileObject file) throws IOException
        {
            setFile(file, AbstractFilesProvider.ARTICLES);
        }  
        
        @Override
        public Integer getPageNumber()
        {
            String string = props.getProperty(PROP_PAGE_NUMBER);
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
        public void setPageNumber(Integer page)
        {
            if(page == null)
            {
                Object oldValue = props.remove(PROP_PAGE_NUMBER);
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                
            }
            else
            {
                Object oldValue = props.setProperty(PROP_PAGE_NUMBER, page.toString());
                if(oldValue != null)
                {
                    oldValue = Integer.parseInt(oldValue.toString());
                }
                propertyChangeSupport.firePropertyChange(PROP_PAGE_NUMBER, oldValue, page);                 
            }  
            markModified();
        }          

        @Override
        public String getPublisher() 
        {
            return props.getProperty(PROP_PUBLISHER);
        }

        @Override
        public void setPublisher(String publisher) 
        {
            if(publisher == null)
            {
                props.remove(PROP_PUBLISHER);
            }
            else
            {
                props.setProperty(PROP_PUBLISHER, publisher);
            }
        }

        @Override
        public String getLanguage() 
        {
            return props.getProperty(PROP_LANGUAGE);
        }

        @Override
        public void setLanguage(String lang)
        {
            if(lang == null)
            {
                props.remove(PROP_LANGUAGE);
            }
            else
            {
                props.setProperty(PROP_LANGUAGE, lang);
            }
        } 
        
        @Override
        public String preferredID() 
        {
            return "article";
        }        
        
        @Override
        public MultiViewElement createElement() 
        {
            return new PdfMultiViewElement(this);
        }  

        @Override
        public HelpCtx getHelpCtx() 
        {
            return HelpCtx.DEFAULT_HELP;
        }
        
        @Override
        public String getDisplayName() 
        {
            return "Article";
        }   
        
        @Override
        public int getPersistenceType() 
        {
            return TopComponent.PERSISTENCE_NEVER;
        }  
        
        @Override
        public Image getIcon()
        {
            IconProvider provider = getLookup().lookup(IconProvider.class);
            return provider.getIcon(BeanInfo.ICON_COLOR_16x16);
        }  
        */
    }     
}
