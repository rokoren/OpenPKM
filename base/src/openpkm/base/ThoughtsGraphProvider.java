/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public interface ThoughtsGraphProvider
{
    Lookup.Provider getProvider();      
    List<Thought> getRootThoughts();  
    List<Thought> getChildrenThoughts(String parentID);
    Thought addRootThought(String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals);
    Thought addChildrenThought(Thought thought, String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals);
    Collection<Thought> getSelectedThoughts(); 
    void selectThought(Thought thought);
    void clearSelectedThoughts();
}
