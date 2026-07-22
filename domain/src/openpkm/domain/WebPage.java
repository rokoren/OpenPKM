/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.domain;

import java.io.IOException;
import openpkm.base.PropertiesProvider;
import openpkm.base.Source;

/**
 *
 * @author Rok Koren
 */
public interface WebPage extends Source, PropertiesProvider
{  
    String getWebPageID();
    org.jsoup.nodes.Document getDocument(String userAgent) throws IOException;
}
