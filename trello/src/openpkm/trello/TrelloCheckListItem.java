/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Rok Koren
 */
public interface TrelloCheckListItem 
{
    TrelloCheckList getCheckList();
    String getCheckListItemID();
    String getTitle();
    int getPosition(); 
    State getState();
    void setState(State state);
    
    public enum State 
    {
        INCOMPLETE("Incomplete"),
        COMPLETE("Complete"),
        DELETED("Deeleted");

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
