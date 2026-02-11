/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.trello;

import com.julienvey.trello.domain.Member;
import java.awt.Color;
import java.awt.Image;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.swing.Action;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.trello.TrelloMember;
import openpkm.trello.TrelloMemberProvider;
import openpkm.utils.UserIcon;
import org.openide.nodes.Children;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Rok Koren
 */
@ServiceProvider(service=TrelloMemberProvider.class)
public class TrelloMemberProviderImpl implements TrelloMemberProvider
{
    private static final String PROP_MEMBER_ID        = "member.id";
    private static final String PROP_MEMBER_USERNAME  = "member.username";    
    private static final String PROP_MEMBER_BIO       = "member.bio";
    private static final String PROP_MEMBER_EMAIL     = "member.email";      
    private static final String PROP_MEMBER_FULL_NAME = "member.full.name";      
    private static final String PROP_MEMBER_STATUS    = "member.status";      
    private static final String PROP_MEMBER_TYPE      = "member.type";     
    
    private static final Logger LOG = Logger.getLogger(TrelloMemberProvider.class.getName());     
    
    @Override
    public TrelloMember getMember(Properties props) 
    {
        return new TrelloMemberImpl(props);
    }
    
    @Override
    public TrelloMember createMember(Member member) 
    {
        Properties props = new Properties();
        props.setProperty(PROP_MEMBER_ID, member.getId());
        props.setProperty(PROP_MEMBER_USERNAME, member.getUsername());
        if(member.getBio() != null)
        {
            props.setProperty(PROP_MEMBER_BIO, member.getBio());            
        }
        if(member.getEmail() != null)
        {
            props.setProperty(PROP_MEMBER_EMAIL, member.getEmail());                      
        }
        props.setProperty(PROP_MEMBER_FULL_NAME, member.getFullName());
        if(member.getStatus() != null)
        {
            props.setProperty(PROP_MEMBER_STATUS, member.getStatus());                               
        }
        if(member.getMemberType() != null)
        {
            props.setProperty(PROP_MEMBER_TYPE, member.getMemberType());            
        }
        return new TrelloMemberImpl(props);
    } 
    
    private static final class TrelloMemberImpl implements TrelloMember, NodeProvider, PropertiesProvider
    {
        private final Properties props;                
        
        public TrelloMemberImpl(Properties props)
        {
            this.props = props;              
        }  

// TODO TrelloMember        
        
        @Override
        public String getMemberID() 
        {
            return props.getProperty(PROP_MEMBER_ID);
        }
        
        @Override
        public String getMemberUsername() 
        {
            return props.getProperty(PROP_MEMBER_USERNAME);
        }        

        @Override
        public String getMemberBio() 
        {
            return props.getProperty(PROP_MEMBER_BIO);
        }

        @Override
        public String getMemberEmail() 
        {
            return props.getProperty(PROP_MEMBER_EMAIL);
        }

        @Override
        public String getMemberFullName() 
        {
            return props.getProperty(PROP_MEMBER_FULL_NAME);
        }

        @Override
        public String getMemberStatus() 
        {
            return props.getProperty(PROP_MEMBER_STATUS);
        }

        @Override
        public String getMemberType() 
        {
            return props.getProperty(PROP_MEMBER_TYPE);
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
            return getMemberID();
        }
        
        @Override
        public String getDisplayName() 
        {
            return getMemberFullName();
        }
        
        @Override
        public Image getIcon(boolean opened) 
        {
            StringTokenizer st = new StringTokenizer(getMemberFullName());
            return new UserIcon(st.nextToken(), st.nextToken(), UserIcon.Type.CIRCLE, Color.ORANGE).getImage();
        }  
        
        @Override
        public List<Action> getActions() 
        {       
            return Collections.EMPTY_LIST;
        }          

        @Override
        public Children getChildren() 
        {
            return Children.LEAF;
        }
    }     
}
