/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.base;

import java.io.IOException;

/**
 *
 * @author Rok Koren
 */
public interface WebPage extends Source
{        
    org.jsoup.nodes.Document getDocument(String userAgent) throws IOException;
}
