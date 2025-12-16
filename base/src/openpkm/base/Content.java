/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface Content extends Source, FileTypeIndependent
{
    String PROP_CONTENT_ID      = "content.id";
    String PROP_CONTENT_CREATOR = "content.creator";
    
    String getCreator();
    void setCreator(String creator);
}
