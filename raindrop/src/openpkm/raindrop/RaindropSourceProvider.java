/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.util.Collection;
import java.util.Map;
import openpkm.base.SourceProvider;
import org.openide.filesystems.FileObject;

/**
 *
 * @author rok
 */
public interface RaindropSourceProvider extends SourceProvider<Raindrop>
{
    RaindropProvider getRaindropProvider();
    Map<String, Raindrop> getRaindropsById();
    Collection<Raindrop> getRaindrops();
    FileObject createRaindrop(String link);
}
