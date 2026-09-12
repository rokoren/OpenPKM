/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Set;

/**
 *
 * @author rok
 */
public interface ThoughtProvider extends ChangeSupportProvider
{
    Thought getThought();
    ThoughtsGraphProvider getProvider();
    Thought addChildrenThought(String text, Thought.Type type, Set<String> tags, Set<Topic> topics, Set<Goal> goals);
    boolean isSelected();    
    void setSelected(boolean selected);
}
