/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;
import openpkm.base.TitleProvider;
import org.netbeans.api.annotations.common.StaticResource;

/**
 *
 * @author Rok Koren
 */
public interface Raindrop extends Source, TitleProvider, PropertiesProvider
{
    @StaticResource()
    String ICON = "openpkm/raindrop/resources/raindrop.png";      
    
    String PROPS_RAINDROP_ID            = "raindrop.id";
    String PROPS_RAINDROP_USER_ID       = "raindrop.user.id";
    String PROPS_RAINDROP_CREATOR_ID    = "raindrop.creator.id";
    String PROPS_RAINDROP_COLLECTION_ID = "raindrop.collection.id";    
    String PROPS_LINK                   = "link";
    String PROPS_EXCERPT                = "excerpt";
    String PROPS_NOTE                   = "note";
    String PROPS_COVER                  = "cover";
    String PROPS_IMPORTANT              = "important";
    String PROPS_REMOVED                = "removed";
    String PROPS_REMINDER               = "reminder";
    String PROPS_LAST_UPDATE            = "last.update";
    String PROPS_HIGHLIGHTS             = "highlights";
    String PROPS_DOMAIN                 = "domain";
    String PROPS_TYPE                   = "type";
    String PROPS_FILE_NAME              = "file.name";
    String PROPS_FILE_SIZE              = "file.size";
    String PROPS_FILE_TYPE              = "file.type";
    
    int getRaindropID();    
    String getLink();
    void setLink(String link);
    String getExcerpt();
    void setExcerpt(String excerpt);
    String getNote();
    void setNote(String note);
    RaindropUser getUser();
    String getCover();
    void setCover(String cover);
    boolean isImportant();
    void setImportant(boolean important);
    boolean isRemoved();
    void setRemoved(boolean removed);
    LocalDateTime getReminder();
    void setReminder(LocalDateTime reminder);
    LocalDateTime getLastUpdate();
    void setLastUpdate(LocalDateTime lastUpdate);
    List<String> getHighlights();
    String getDomain();
    void setDomain(String domain);
    RaindropUser getCreator();
    Type getType();
    RaindropCollection getCollection();
    void setCollection(RaindropCollection collection);  
    
    public enum Type 
    {
        LINK("link"),
        ARTICLE("article"),
        DOCUMENT("document"),
        BOOK("book"),
        VIDEO("video");

        private final String string;

        Type(String string) 
        {
            this.string = string;
        }

        @Override
        public String toString() 
        {
            return string;
        }
        
        public static Optional<Type> get(String string) 
        {
            return Arrays.stream(Type.values())
                    .filter(type -> type.string.equalsIgnoreCase(string))
                    .findFirst();
        }     
    }     
}
