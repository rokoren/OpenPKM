/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 *
 * @author Rok Koren
 */
public class StyledTextStripper extends PDFTextStripper
{
    private TextPosition lastText;
    
    private final List<StyledTextFragment> fragments = new ArrayList<>();
    
    private final float paragraphThreshold = 20.0f; // prilagodi glede na velikost pisave    

    public StyledTextStripper() throws IOException 
    {
        super.setSortByPosition(true);
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        if (textPositions.isEmpty()) return;
        
        TextPosition currentText = textPositions.get(0);

        if(lastText != null)
        {
            float diff = Math.abs(currentText.getYDirAdj() - lastText.getYDirAdj());
            if(currentText.getFontSizeInPt() != lastText.getFontSizeInPt() && diff > currentText.getFontSizeInPt())
            {
                fragments.add(new StyledTextFragment("\n", lastText));
            }
            else if(diff > currentText.getFontSizeInPt() * 1.5)
            {
                fragments.add(new StyledTextFragment("\n", lastText));
            }
        }               
        
        for (TextPosition tp : textPositions) {
            fragments.add(new StyledTextFragment(tp.getUnicode(), tp));
        }
        
        lastText = currentText;
    }

    public List<StyledTextFragment> getFragments() 
    {
        return fragments;
    }    
}
