/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author rok
 */
public interface RaindropProvider extends SourceProvider<Raindrop>
{
    Map<String, Raindrop> getRaindropsById();
    Collection<Raindrop> getRaindrops();
    FileObject createRaindrop(String link, boolean important, List<String> tags, String note);
    FileObject createRaindrop(RaindropCollection collection, String link, boolean important, List<String> tags, String note);
}
