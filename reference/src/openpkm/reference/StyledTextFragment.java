/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.reference;

import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author Rok Koren
 */
public class StyledTextFragment 
{
    public final String text;
    public final TextPosition position;

    public StyledTextFragment(String text, TextPosition position)
    {
        this.text = text;
        this.position = position;
    }     
}
