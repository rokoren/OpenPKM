/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.neo4j;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;
import openpkm.base.ChildrenGoal;
import openpkm.base.ChildrenTopic;
import openpkm.base.Goal;
import openpkm.base.Topic;
import openpkm.base.VisibilityProvider;

/**
 *
 * @author Rok Koren
 */
public interface Neo4jInstance
{
    String PROP_ID       = "instance.id";     
    String PROP_NAME     = "instance.name";   
    String PROP_URI      = "connection.uri";
    String PROP_USERNAME = "db.username";   
    String PROP_PASSWORD = "db.password";   
    String PROP_TYPE     = "db.type"; 
    
    String getInstanceID();
    String getUri();
    void setUri(String uri);
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    String getName();
    void setName(String name);
    Type getType();
    void setType(Type type);
    Preferences getPreferences();
    
    List<Topic> getRootTopics(String topicID);
    List<ChildrenTopic> getChildrenTopics(String parentID);
    Topic addRootTopic(String topic, String name, String tag);
    void removeRootTopic(String topicID);
    ChildrenTopic addChildrenTopic(String parentID, String name, String tag, VisibilityProvider.Modifier modifier);
    void removeChildrenTopic(ChildrenTopic topic);
   
    List<Goal> getRootGoals(String topicID);    
    List<ChildrenGoal> getChildrenGoals(String parentID);    
    Goal addRootGoal(String topic, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming);    
    void removeRootGoal(String goalID);    
    ChildrenGoal addChildrenGoal(String parentID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming, VisibilityProvider.Modifier modifier);    
    void removeChildrenGoal(ChildrenGoal goal);    
    
    public enum Type 
    {
        NEO4J_DESKTOP("Neo4j Desktop"),
        AURADB_FREE("AuraDB Free"),
        AURADB_PROFESSIONAL("AuraDB Professional");

        private String string;

        Type(String string) 
        {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
        
        public static Optional<Type> get(String string) {
            return Arrays.stream(Type.values())
                    .filter(level -> level.string.equalsIgnoreCase(string))
                    .findFirst();
        }     
    }      
    
    public static Comparator<Neo4jInstance> nameComparator() 
    {
        return new Comparator<Neo4jInstance>() 
        {
            @Override
            public int compare(Neo4jInstance instance1, Neo4jInstance instance2) 
            {
                String name1 = instance1.getName();
                String name2 = instance2.getName();
                return name1.compareTo(name2);
            }
        };
    }     
}
