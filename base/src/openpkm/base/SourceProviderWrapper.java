/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import javax.swing.event.ChangeListener;
import org.openide.loaders.DataObject;

/**
 *
 * @author rok
 */
public interface SourceProviderWrapper extends TagsProvider, ThoughtsProvider, BacklinksProvider, ActionsProvider
{
    Source getSource();
    void deleteSource() throws IOException;
    void addBacklink(String link);
    void removeBacklink(String link);    
    SourceProvider getProvider();
    LiteratureNoteFactory getLiteratureNoteFactory(DataObject data);
    void addListener(ChangeListener listener);
    void removeListener(ChangeListener listener);
}
