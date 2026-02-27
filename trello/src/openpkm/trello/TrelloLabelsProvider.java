/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;

/**
 *
 * @author rokor
 */
public interface TrelloLabelsProvider
{
    Collection<TrelloLabel> getLabels();    
    void addLabel(TrelloLabel label);
    void removeLabel(TrelloLabel label);
}
