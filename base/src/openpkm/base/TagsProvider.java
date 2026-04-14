/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.Set;

/**
 *
 * @author Rok Koren
 */
public interface TagsProvider
{
    String PROP_TAGS = "tags";     
    
    Set<String> getTags();    
}
