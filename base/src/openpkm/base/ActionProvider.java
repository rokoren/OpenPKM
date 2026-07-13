/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import javax.swing.Action;

/**
 *
 * @author rok
 */
public interface ActionProvider<T extends SourceProvider>
{
    Action getAction(T provider);
}
