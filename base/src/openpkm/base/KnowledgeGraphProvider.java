/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rok Koren
 */
public interface KnowledgeGraphProvider extends GraphProvider
{
    List<Topic> getTopics(); 
    List<Topic> getRootTopics();
    String getTreeID(Topic topic);
    Collection<Topic> getSelectedTopics(); 
    void clearSelectedTopics();
    void selectTopic(Topic topic);
    boolean isTopic(TopicsProvider project);
    List<ChildrenTopic> getChildrenTopics(String parentID);
    void addRootTopic(String name, String tag);
    void addRootTopic(String topicID, String name, String tag);    
    void removeRootTopic(Topic topic);
    void addChildrenTopic(String parentID, String name, String tag, VisibilityProvider.Modifier modifier);
    void addChildrenTopic(String parentID, String topicID, String name, String tag, VisibilityProvider.Modifier modifier);
    void removeChildrenTopic(ChildrenTopic topic);   
    List<String> getTags(Topic topic);
    Set<String> getTags(Collection<Topic> topics);    
    
    public static boolean isTag(KnowledgeGraphProvider topicsProvider, TagsProvider tagsProvider)
    {
        if(topicsProvider == null)
        {
            return true;
        }
        return topicsProvider.isTag(tagsProvider);
    }     
}
