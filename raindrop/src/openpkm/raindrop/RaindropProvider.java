/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package openpkm.raindrop;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import openpkm.base.Domain;
import openpkm.base.SourceProvider;
import openpkm.base.Topic;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Rok Koren
 */
public interface RaindropProvider extends SourceProvider<Raindrop>
{    
    Map<String, Raindrop> getRaindropsById();
    Collection<Raindrop> getRaindrops();
    FileObject createRaindrop(String link, boolean important, String note, List<String> tags, Set<Domain> domains, Set<Topic> topics);
    FileObject createRaindrop(RaindropCollection collection, String link, boolean important, String note, List<String> tags, Set<Domain> domains, Set<Topic> topics);
}
