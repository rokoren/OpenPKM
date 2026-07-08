/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.core.search;

import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;

/**
 *
 * @author rok
 */
public class DataSearchResult implements Runnable
{
    private final DataObject data;

    public DataSearchResult(DataObject data) 
    {
        this.data = data;
    }

    @Override
    public void run() 
    {
        OpenCookie open = data.getCookie(OpenCookie.class);
        open.open();
    }   
}
