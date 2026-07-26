/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.utils;

import openpkm.base.Source;
import openpkm.base.SourceEvent;
import openpkm.base.SourceProvider;

/**
 *
 * @author rok
 */
public class SourceEventImpl implements SourceEvent
{
    private final SourceProvider provider;
    private final Source source;

    public SourceEventImpl(SourceProvider provider, Source source) 
    {
        this.provider = provider;
        this.source = source;
    }        
    
    @Override
    public SourceProvider getProvider() 
    {
        return provider;
    }

    @Override
    public Source getSource() 
    {
        return source;
    }    
}
