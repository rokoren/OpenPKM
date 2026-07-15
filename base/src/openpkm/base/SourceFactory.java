/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author rok
 */
public interface SourceFactory<T extends Source> 
{
    void save(T source, OutputStream os, String comments) throws IOException;      
}
