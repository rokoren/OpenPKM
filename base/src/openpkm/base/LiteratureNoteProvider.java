/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;

/**
 *
 * @author rok
 */
public interface LiteratureNoteProvider
{
    String getLiteratureNote(String primaryFileName, String title, String subtitle, String authorName, String sourceUrl, String summary, List<Quote> quotes);
}
