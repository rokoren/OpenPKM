/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author rokoren
 */
public interface ChildrenGoal extends Goal
{
    String getParentID();
    void setParentID(String parentID);        
}
