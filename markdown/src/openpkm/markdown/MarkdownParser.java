/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package openpkm.markdown;

import com.vladsch.flexmark.util.ast.Document;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.util.ChangeSupport;

/**
 *
 * @author Rok Koren
 */
public class MarkdownParser extends Parser 
{
    private Snapshot snapshot;
    private Document document;
    private MarkdownParserResult lastResult;
    private int extensions;
    private ChangeSupport cs;
    
    private ChangeSupport getChangeSupport()
    {
        if(cs == null)
        {
            cs = new ChangeSupport(this);
        }
        return cs;
    }

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) throws ParseException 
    {
        //String content = AsciiDocUtils.correctExtensionBlocks(text.toString());
        if(lastResult == null || !lastResult.isValid())
        {
            this.snapshot = snapshot;
            CharSequence text = snapshot.getText(); 
            document = MarkdownService.getDeafult().getParser().parse(text.toString());
            //getChangeSupport().fireChange();      
        }
    }

    @Override
    public Result getResult(Task task) throws ParseException 
    {
        lastResult = new MarkdownParserResult(snapshot, document, 0);
        return lastResult;
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) 
    {
        getChangeSupport().addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) 
    {
        getChangeSupport().removeChangeListener(changeListener);
    }     
}
