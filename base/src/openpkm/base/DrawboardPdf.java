/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

/**
 *
 * @author rok
 */
public interface DrawboardPdf extends Source, StateSupport, PropertiesProvider
{
    String PROP_LINK = "drawboard.pdf.link";
    
    String getLink();
}
