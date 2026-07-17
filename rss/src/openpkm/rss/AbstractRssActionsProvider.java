/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.rss;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import openpkm.base.ActionsProvider;

/**
 *
 * @author rok
 */
public abstract class AbstractRssActionsProvider implements ActionsProvider
{
    public abstract Action addRssChannel();
    
    @Override
    public List<Action> getActions() 
    {
        List<Action> actions = new ArrayList();
        actions.add(addRssChannel());         
        return actions;
    }       
}
