/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.trello;

import openpkm.base.PropertiesProvider;

/**
 *
 * @author Rok Koren
 */
public interface TrelloMember extends PropertiesProvider
{
    String getMemberID(); 
    String getMemberUsername();
    String getMemberBio();
    String getMemberEmail();
    String getMemberFullName();
    String getMemberStatus();
    String getMemberType();      
}
