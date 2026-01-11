/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import java.time.LocalDateTime;
import openpkm.base.DescriptionProvider;
import openpkm.base.Link;
import openpkm.base.TitleProvider;
import openpkm.base.WatchLater;

/**
 *
 * @author Rok Koren
 */
public interface Rss extends Link, TitleProvider, DescriptionProvider, WatchLater
{
    String PROP_RSS_ID         = "rss.id";
    String PROP_URI            = "uri";
    String PROP_PUBLISHED_DATE = "published.date";
    
    String getRssID();
    String getUri();
    LocalDateTime getPublishedDate();
}
