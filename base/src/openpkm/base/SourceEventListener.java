/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.EventListener;

/**
 *
 * @author rok
 */
public interface SourceEventListener extends EventListener
{
    void sourceDeleted(SourceEvent evt);
    void sourceModified(SourceEvent evt);
    void sourceAdded(SourceEvent evt);
}
