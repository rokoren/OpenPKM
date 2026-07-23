/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import java.util.Properties;
import openpkm.base.SourceFactory;
import org.openide.util.RequestProcessor;

/**
 *
 * @author rok
 */
public interface BlogFactory extends SourceFactory<Blog>
{
    RequestProcessor RP = new RequestProcessor(BlogFactory.class);  
    
    Blog getBlog(Properties props);           
}
