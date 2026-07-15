/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import java.util.Properties;
import openpkm.base.SourceFactory;

/**
 *
 * @author rok
 */
public interface BlogFactory extends SourceFactory<Blog>
{
    Blog getBlog(Properties props);           
}
