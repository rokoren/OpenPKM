/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/quickSearch.java to edit this template
 */
package openpkm.core.search;

import java.io.IOException;
import java.util.logging.Logger;
import openpkm.base.Article;
import openpkm.base.DisplayNameProvider;
import openpkm.base.FilterTagsProvider;
import openpkm.base.GoalsGraphProvider;
import openpkm.base.GoalsProvider;
import openpkm.base.Source;
import openpkm.base.SourceProviderWrapper;
import openpkm.base.SourceProviders;
import openpkm.base.TagsProvider;
import openpkm.base.TopicsProvider;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.quicksearch.SearchProvider;
import org.netbeans.spi.quicksearch.SearchRequest;
import org.netbeans.spi.quicksearch.SearchResponse;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Lookup;
import openpkm.base.TopicsGraphProvider;

public class ArticleSearchProvider implements SearchProvider 
{ 
    private static final Logger LOG = Logger.getLogger(ArticleSearchProvider.class.getName()); 
    
    private final FilterTagsProvider filterTags; 
    
    public ArticleSearchProvider() 
    {
        filterTags = Lookup.getDefault().lookup(FilterTagsProvider.class);
    }
        
    @Override
    public void evaluate(SearchRequest request, SearchResponse response) 
    {
        for(Project project : OpenProjects.getDefault().getOpenProjects())
        {            
            SourceProviders providers = project.getLookup().lookup(SourceProviders.class);
            if(providers != null)
            {
                String text = request.getText().toLowerCase();
                
                TopicsGraphProvider topicProvider = project.getLookup().lookup(TopicsGraphProvider.class);
                GoalsGraphProvider goalProvider = project.getLookup().lookup(GoalsGraphProvider.class);
                try
                {
                    for(FileObject file : providers.getDataDirectory().getChildren())
                    {
                        try
                        {
                            DataObject data = DataObject.find(file);
                            
                            SourceProviderWrapper sourceProvider = data.getLookup().lookup(SourceProviderWrapper.class);
                            if(sourceProvider != null)
                            {
                                Source source = sourceProvider.getSource();  
                                if(source instanceof Article)
                                {
                                    DisplayNameProvider provider = source.getLookup().lookup(DisplayNameProvider.class);
                                    if(provider != null)
                                    {                                         
                                        boolean isTag = true;
                                        boolean isTopic = true; 
                                        boolean isGoal = true;

                                        if(filterTags != null)
                                        {
                                            TagsProvider tagsProvider = data.getLookup().lookup(TagsProvider.class);
                                            if(tagsProvider != null)
                                            {
                                                isTag = filterTags.isTag(tagsProvider);
                                            }                            
                                        }

                                        if(topicProvider != null)
                                        {
                                            TopicsProvider topicsProvider = source.getLookup().lookup(TopicsProvider.class);
                                            if(topicsProvider != null)
                                            {
                                                isTopic = topicProvider.isTopic(topicsProvider);
                                            } 
                                        }    

                                        if(goalProvider != null)
                                        {
                                            GoalsProvider goalsProvider = source.getLookup().lookup(GoalsProvider.class);
                                            if(goalsProvider != null)
                                            {
                                                isGoal = goalProvider.isGoal(goalsProvider);
                                            } 
                                        }                                     

                                        if(isTag && isTopic && isGoal)                    
                                        {  
                                            String displayName = provider.getDisplayName(DisplayNameProvider.TextFormat.PLAIN);
                                            if(displayName.toLowerCase().contains(text))
                                            {
                                                if(!response.addResult(new DataSearchResult(data), displayName))
                                                {
                                                    break;
                                                }  
                                            }                                                                                           
                                        }                                     
                                    }                                     
                                }  
                            }                                                                                                                                             
                            
                        }
                        catch(DataObjectNotFoundException e)
                        {
                            LOG.info(e.getMessage());
                        }                        
                    }                    
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
            }
        }
    }
}
