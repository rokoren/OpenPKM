/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface Note
{
    String PROP_LANGUAGE  = "note.language";  
    String PROP_FILE_NAME = "file.name";     
    
    String getFileName(); 
    String getLanguage();
    void setLanguage(String lang);      
}
