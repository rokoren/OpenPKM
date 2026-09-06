/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author Rok Koren
 */
public interface LiteratureNoteProvider
{
    String getLiteratureNote(String primaryFileName, String primaryTitle, String title, String subtitle, String authorName, String sourceUrl, String summary);
}
