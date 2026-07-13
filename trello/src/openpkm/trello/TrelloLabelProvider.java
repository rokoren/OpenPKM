/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.util.Collection;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rokor
 */
public interface TrelloLabelProvider
{
    Collection<TrelloLabel> getLabels();
    TrelloLabel getLabel(String labelID);
    void addLabel(TrelloLabel label);
    void removeLabel(TrelloLabel label);
    void addChangeListener(ChangeListener listener);
    void removeChangeListener(ChangeListener listener);
}
