/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author Rok Koren
 */
public interface GroupProvider 
{
    // BOOKS
    DisplayNameProvider DISPLAY_NAME_PROVIDER_BOOK = new DisplayNameProviderImpl("Books");
    IconProvider ICON_PROVIDER_BOOK = new IconProviderImpl(IconsProvider.ICON.BOOKS);    
    ActionsProvider ACTIONS_PROVIDER_BOOK = new ActionsProviderImpl("Actions/OpenPKM/Book");
    
    // ARTICLES
    DisplayNameProvider DISPLAY_NAME_PROVIDER_ARTICLE = new DisplayNameProviderImpl("Articles");
    IconProvider ICON_PROVIDER_ARTICLE = new IconProviderImpl(IconsProvider.ICON.ARTICLES);    
    ActionsProvider ACTIONS_PROVIDER_ARTICLE = new ActionsProviderImpl("Actions/OpenPKM/Article");  
    
    // DOCUMENTS
    DisplayNameProvider DISPLAY_NAME_PROVIDER_DOCUMENT = new DisplayNameProviderImpl("Documents");
    IconProvider ICON_PROVIDER_DOCUMENT = new IconProviderImpl(IconsProvider.ICON.DOCUMENTS);    
    ActionsProvider ACTIONS_PROVIDER_DOCUMENT = new ActionsProviderImpl("Actions/OpenPKM/Document");  
    
    // LINKS
    DisplayNameProvider DISPLAY_NAME_PROVIDER_LINK = new DisplayNameProviderImpl("Links");
    IconProvider ICON_PROVIDER_LINK = new IconProviderImpl(IconsProvider.ICON.LINKS);    
    ActionsProvider ACTIONS_PROVIDER_LINK = new ActionsProviderImpl("Actions/OpenPKM/Link");   
    
    // PICTURES
    DisplayNameProvider DISPLAY_NAME_PROVIDER_PICTURE = new DisplayNameProviderImpl("Pictures");
    IconProvider ICON_PROVIDER_PICTURE = new IconProviderImpl(IconsProvider.ICON.PICTURES);    
    ActionsProvider ACTIONS_PROVIDER_PICTURE = new ActionsProviderImpl("Actions/OpenPKM/Picture");  
    
    // VIDEOS
    DisplayNameProvider DISPLAY_NAME_PROVIDER_VIDEO = new DisplayNameProviderImpl("Videos");
    IconProvider ICON_PROVIDER_VIDEO = new IconProviderImpl(IconsProvider.ICON.VIDEOS);    
    ActionsProvider ACTIONS_PROVIDER_VIDEO = new ActionsProviderImpl("Actions/OpenPKM/Video");     
    
    String getName();  
    Lookup.Provider getLookupProvider();     
    DisplayNameProvider getDisplayNameProvider();
    IconProvider getIconProvider();
    ActionsProvider getActionsProvider();
    Integer getPosition();
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
    
    public static Comparator<GroupProvider> positionComparator() 
    {
        return new Comparator<GroupProvider>() 
        {
            @Override
            public int compare(GroupProvider group1, GroupProvider group2) 
            {
                return group1.getPosition().compareTo(group2.getPosition());
            }
        };
    } 

    public static class DisplayNameProviderImpl implements DisplayNameProvider
    {
        private final String displayName;

        public DisplayNameProviderImpl(String displayName) 
        {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName(TextFormat format) 
        {
            if(format == TextFormat.PLAIN)
            {
                return displayName;
            }
            return null;
        }                
    }
    
    public static class IconProviderImpl implements IconProvider
    {
        private final IconsProvider.ICON icon;

        public IconProviderImpl(IconsProvider.ICON icon) 
        {
            this.icon = icon;
        }

        @Override
        public Image getIcon(int type) 
        {
            IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
            return provider.getImage(icon);
        }               
    }    
    
    public static class ActionsProviderImpl implements ActionsProvider
    {
        private final String path;

        public ActionsProviderImpl(String path) 
        {
            this.path = path;
        }                
        
        @Override
        public List<Action> getActions() 
        {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath(path));         
            return actions;
        }        
    }    
}
