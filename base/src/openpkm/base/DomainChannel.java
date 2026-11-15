/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

/**
 *
 * @author rok
 */
public interface DomainChannel 
{
    String getChannelID();
    String getDomainID();
    void setDomainID(String domainID);
    String getName();
    void setName(String name);       
    Type getType(); 
    Properties getProperties();    

    public enum Type 
    {
        HOME_PAGE("Home Page", IconsProvider.ICON.HOME_PAGE),
        BLOG("Blog", IconsProvider.ICON.BLOG),
        GIT_HUB("GitHub", IconsProvider.ICON.GITHUB),
        FACEBOOK("Facebook", IconsProvider.ICON.FACEBOOK),
        TWITTER("Twitter", IconsProvider.ICON.TWITTER),
        YOUTUBE_CHANNEL("YouTube", IconsProvider.ICON.YOUTUBE_CHANNEL);

        private final String name;
        private final IconsProvider.ICON icon;

        Type(String name, IconsProvider.ICON icon) 
        {
            this.name = name;
            this.icon = icon;
        }
        
        public IconsProvider.ICON getIcon()
        {
            return icon;
        }

        @Override
        public String toString() 
        {
            return name;
        }
        
        public static Optional<Type> get(String name) {
            return Arrays.stream(Type.values())
                    .filter(modifier -> modifier.name.equalsIgnoreCase(name))
                    .findFirst();
        }     
    } 
}
