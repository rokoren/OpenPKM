/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;

/**
 *
 * @author Rok Koren
 */
public interface TopicsProvider 
{
    String PROP_TOPICS = "topics"; 
    
    List<String> getTopics();      
}
