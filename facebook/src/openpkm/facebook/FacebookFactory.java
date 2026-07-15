/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.facebook;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author rok
 */
public interface FacebookFactory extends SourceFactory<FacebookPage>
{
    FacebookPage getFacebookPage(Properties props); 
}
