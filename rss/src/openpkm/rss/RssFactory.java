/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.rss;

import java.util.Properties;

/**
 *
 * @author rok
 */
public interface RssFactory 
{
    RssChannel getRssChannel(Properties props);
}
