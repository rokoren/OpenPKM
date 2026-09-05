/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author rok
 */
public interface LiteratureNoteFactory 
{
    void createLiteratureNote(List<WizardDescriptor.Panel<WizardDescriptor>> panels);
}
