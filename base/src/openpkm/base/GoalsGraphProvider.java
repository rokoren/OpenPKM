/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Rok Koren
 */
public interface GoalsGraphProvider extends GraphProvider  
{
    List<Goal> getGoals();
    List<Goal> getRootGoals();
    List<ChildrenGoal> getChildrenGoals(String parentID);
    void addRootGoal(String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming);
    void removeRootGoal(Goal goal);
    void addChildrenGoal(String parentID, String name, String tag, Goal.Level level, LocalDate startDate, LocalDate endDate, String vision, String accountability, String rewards, String obstacles, String support, String brainstorming, VisibilityProvider.Modifier modifier);   
    void removeChildrenGoal(ChildrenGoal goal);
    List<String> getTags(Goal goal);
    Collection<Goal> getSelectedGoals();
    void clearSelectedGoals();
    boolean isGoal(GoalsProvider provider);
    Set<String> getTags(Collection<Goal> goals);
    void selectGoal(Goal goal);    
}
