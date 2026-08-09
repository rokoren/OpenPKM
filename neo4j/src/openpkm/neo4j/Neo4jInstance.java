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
    String PROP_NEO4J_TYPE     = "neo4j.type";     
    String PROP_NEO4J_URI      = "neo4j.uri";
    String PROP_NEO4J_USERNAME = "neo4j.username";   
    String PROP_NEO4J_PASSWORD = "neo4j.password";               
    String PROP_NEO4J_DATABASE = "neo4j.database";
    String PROP_INSTANCE_ID    = "instance.id";     
    String PROP_INSTANCE_NAME  = "instance.name";   
    
    String getInstanceID();
    String getNeo4jUri();
    void setNeo4jUri(String uri);
    String getNeo4jUsername();
    void setNeo4jUsername(String username);
    String getNeo4jPassword();
    void setNeo4jPassword(String password);    
    String getNeo4jDatabase();
    void setNeo4jDatabase(String database);        
    String getInstanceName();
    void setInstanceName(String name);
    Type getNeo4jType();
    void setNeo4jType(Type type);
    Preferences getPreferences();
    
    List<Topic> getRootTopics(String topicID);
    List<ChildrenTopic> getChildrenTopics(String parentID);
    Topic addRootTopic(String projectID, String name, String tag);
    Topic addRootTopic(String projectID, String topicID, String name, String tag);
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
                String name1 = instance1.getInstanceName();
                String name2 = instance2.getInstanceName();
                return name1.compareTo(name2);
            }
        };
    }     
}
