/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;
import javax.swing.Action;

/**
 *
 * @author Rok Koren
 */
public interface NodeActionsProvider<T extends NodeProvider>
{
    List<Action> getActions(T source);    
}
