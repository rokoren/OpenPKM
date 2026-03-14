/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.trello;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import openpkm.base.ActionsProvider;

/**
 *
 * @author rokor
 */
public abstract class AbstractMemberActionsProvider implements ActionsProvider
{
    public abstract Action addMember();
    
    @Override
    public List<Action> getActions() 
    {
        List<Action> actions = new ArrayList();
        actions.add(addMember());         
        return actions;
    }      
}
