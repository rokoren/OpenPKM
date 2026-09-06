/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface LiteratureNote extends Note, TitleProvider, SummaryProvider
{
    String PROP_PRIMARY_FILE_NAME = "primary.file.name";
    String PROP_PRIMARY_TITLE     = "primary.title";
    String PROP_AUTHOR_NAME       = "author.name"; 
    String PROP_SUBTITLE          = "subtitle"; 
    String PROP_SOURCE_URL        = "source.url";        
    
    String getPrimaryFileName();
    String getPrimaryTitle();
    String getSubtitle();
    String getAuthorName();
    String getSourceUrl();
}
