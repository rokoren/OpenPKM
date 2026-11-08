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
 * @author Rok Koren
 */
public interface Domain
{ 
    String getDomainID();
    Properties getProperties();
    void setProperties(Properties props);        
    Type getType();
    void setType(Type type);     
    
    public enum Type 
    {
        HOME_PAGE("Home Page", IconsProvider.ICON.HOME_PAGE),
        BLOG("Blog", IconsProvider.ICON.BLOG),
        GIT_HUB("GitHub", IconsProvider.ICON.GITHUB),
        YOUTUBE_CHANNEL("YouTube Channel", IconsProvider.ICON.YOUTUBE_CHANNEL);

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
