/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import javax.swing.event.ChangeListener;

/**
 *
 * @author rok
 */
public interface SourceProviderWrapper extends TagsProvider, ActionsProvider
{
    Source getSource();
    void deleteSource() throws IOException;
    SourceProvider getProvider();
    void addListener(ChangeListener listener);
    void removeListener(ChangeListener listener);
}
