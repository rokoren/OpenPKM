/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;
import openpkm.base.StateSupport;

/**
 *
 * @author Rok Koren
 */
public interface TrelloLabel extends StateSupport, PropertiesProvider, NodeProvider
{    
    String getLabelID();
    String getLabelName();
    void setLabelName(String name);
    String getLabelColor();   
    void setLabelColor(String color);
}
