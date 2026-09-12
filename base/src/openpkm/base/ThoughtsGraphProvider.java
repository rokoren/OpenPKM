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
    Thought getThought(String thoughtID);
    List<Thought> getRootThoughts();  
    List<ChildrenThought> getChildrenThoughts(String parentID);
    Thought addRootThought(String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals);
    ChildrenThought addChildrenThought(Thought thought, String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals);
    void selectThought(Thought thought);
    Collection<Thought> getSelectedThoughts();
    void clearSelectedThoughts();
    boolean isThought(ThoughtsProvider provider);
}
