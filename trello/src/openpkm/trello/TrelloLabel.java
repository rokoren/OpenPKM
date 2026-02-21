/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import java.awt.Color;
import openpkm.base.NodeProvider;
import openpkm.base.PropertiesProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloLabel extends PropertiesProvider, NodeProvider
{    
    String getLabelID();
    String getLabelName();
    Color getLabelColor();       
}
