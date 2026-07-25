/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author rok
 */
public interface FaviconProvider 
{
    BufferedImage getFavicon(String domain, int size) throws IOException;
}
