/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Arrays;
import java.util.Optional;
import openpkm.base.ActionsProvider;
import openpkm.base.NodePositionProvider;
import openpkm.base.PreferredActionProvider;
import openpkm.base.PropertiesProvider;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckListItem extends PropertiesProvider, NodePositionProvider, ActionsProvider, PreferredActionProvider
{
    String getCheckListItemID();
    String getCheckListItemName();
    void setCheckListItemName(String name);
    Integer getCheckListItemPosition(); 
    State getCheckListItemState();
    void setCheckListItemState(State state);
    ChangeSupport getChangeSupport();    
    
    public enum State 
    {
        INCOMPLETE("incomplete"),
        COMPLETE("complete");

        private String string;

        State(String string) 
        {
            this.string = string;
        }

        @Override
        public String toString()
        {
            return string;
        }
        
        public static Optional<State> get(String string)
        {
            return Arrays.stream(State.values())
                    .filter(state -> state.string.equalsIgnoreCase(string))
                    .findFirst();
        }     
    }      
}
