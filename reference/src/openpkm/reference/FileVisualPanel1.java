/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package openpkm.reference;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.DefaultComboBoxModel;
import openpkm.base.FileTypeProvider;
import openpkm.base.KnowledgeGraphProvider;
import openpkm.base.Topic;
import openpkm.base.VisibilityProvider;
import openpkm.utils.TopicNode;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.SearchableComboBox;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Rok Koren
 */
public class FileVisualPanel1 extends javax.swing.JPanel
{
    private final DefaultComboBoxModel<FileTypeProvider> fileTypes = new DefaultComboBoxModel<>(); 
    private final DefaultComboBoxModel<VisibilityProvider.Modifier> modifiers = new DefaultComboBoxModel<>();  
    
    private final ObservableList<ReferenceFile> files = FXCollections.observableArrayList();  
    private final ObservableList<String> tags = FXCollections.observableArrayList();      
    
    private SearchableComboBox comboBox1;  
    private CheckComboBox<String> comboBox2;
    private JFXPanel panel1, panel2;    
    
    /**
     * Creates new form BookWizardPanel1
     */
    public FileVisualPanel1(AbstractFilesProvider provider) throws IOException
    {
        setFileTypes();
        setModifiers();
        setFiles(provider);
        initComponents();
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                comboBox1 = new SearchableComboBox(files);
                comboBox1.setMaxWidth(Double.MAX_VALUE);
                comboBox1.setEditable(true);  
                comboBox1.setDisable(false); 
                //comboBox1.getStylesheets().setAll(Objects.requireNonNull(PdfVisualElement.class.getResource("nord-dark.css")).toExternalForm(), Objects.requireNonNull(PdfVisualElement.class.getResource("pdf-view-atlanta.css")).toExternalForm());                
                Scene scene1 = new Scene(comboBox1);                             
                panel1.setScene(scene1);
                
                comboBox2 = new CheckComboBox(tags);
                comboBox2.setMaxWidth(Double.MAX_VALUE); 
                //comboBox2.getStylesheets().setAll(Objects.requireNonNull(PdfVisualElement.class.getResource("nord-dark.css")).toExternalForm(), Objects.requireNonNull(PdfVisualElement.class.getResource("pdf-view-atlanta.css")).toExternalForm());                
                Scene scene2 = new Scene(comboBox2);                             
                panel2.setScene(scene2);                
            }
        });         
    } 
    
    @Override
    public String getName() 
    {
        return "General";
    }      
    
    private void setFileTypes()
    {
        fileTypes.removeAllElements();
        fileTypes.addAll(FileTypeProvider.getAll());        
    } 

    private void setModifiers()
    {
        modifiers.removeAllElements();
        modifiers.addAll(Arrays.asList(VisibilityProvider.Modifier.values()));        
    }     

    private List<ReferenceFile> getFiles(FileObject root, FileObject dir)
    {
        List<ReferenceFile> files = new ArrayList<>();
        for(FileObject fo : dir.getChildren())
        {
            if(fo.isFolder())
            {
                files.addAll(getFiles(root, fo));
            }
            else
            {
                files.add(new ReferenceFile(root, fo));    
            }
        } 
        return files;
    }  
    
    public void setFiles(AbstractFilesProvider provider) throws IOException
    {
        Comparator<ReferenceFile> comparator = Comparator.comparing(ReferenceFile::toString);
        files.clear();
        files.addAll(getFiles(provider.getDirectory(), provider.getDirectory())); 
        Collections.sort(files, comparator);        
    }  
    
    public void setTags(Set<String> projectTags)
    {
        tags.clear();
        tags.addAll(projectTags);
        Collections.sort(tags);        
    }    
    
    public void setSelectedTags(Set<String> selectedTags)
    {
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {
                comboBox2.getCheckModel().clearChecks();
                for(String tag : selectedTags)
                {
                    int index = comboBox2.getCheckModel().getItemIndex(tag);
                    comboBox2.getCheckModel().checkIndices(index);
                }            
            }
        });                 
    }   
    
    public FileTypeProvider getReferenceFileType()
    {
        return (FileTypeProvider)fileTypes.getSelectedItem();
    }    
    
    public String getReferenceTitle()
    {
        return jTextField2.getText().trim();
    }
    
    public VisibilityProvider.Modifier getReferenceVisibilityModifier()
    {
        return (VisibilityProvider.Modifier)jComboBox2.getSelectedItem();
    }    
     
    public List<String> getReferenceTags()
    {
        return comboBox2.getCheckModel().getCheckedItems();
    }  
    
    public String getReferenceFileRelativePath()
    {
        ReferenceFile file = (ReferenceFile)comboBox1.getValue();
        if(file != null)
        {
            return file.getRelativePath();
        }
        return null;
    }  
    
    public String getReferenceFileName()
    {
        ReferenceFile file = (ReferenceFile)comboBox1.getValue();
        if(file != null)
        {
            return file.getName();
        }
        return null;
    }  
    
    public String getReferenceFileExt()
    {
        ReferenceFile file = (ReferenceFile)comboBox1.getValue();
        if(file != null)
        {
            return file.getExt();
        }
        return null;
    }     
    
    private static final class ReferenceFile
    {
        private final FileObject root;
        private final FileObject file;

        public ReferenceFile(FileObject root, FileObject file) 
        {
            this.root = root;
            this.file = file;
        }
        
        public String getRelativePath()
        {
            return FileUtil.getRelativePath(root, file);
        }
        
        public String getName()
        {
            return file.getName();
        }
        
        public String getExt()
        {
            return file.getExt();
        }        
        
        @Override
        public String toString()
        {
            return getName();
        }
    } 

    static final class TopicsChildren extends Children.Keys<Topic> 
    {
        private final KnowledgeGraphProvider provider;

        public TopicsChildren(KnowledgeGraphProvider provider)
        {
            this.provider = provider;
        }  

        @Override
        protected void addNotify() 
        {
            updateKeys();                             
        }

        private void updateKeys() 
        {
            EventQueue.invokeLater(new Runnable() 
            {
                public void run()
                {                                                
                    SortedSet<Topic> topics = new TreeSet<Topic>(Topic.nameComparator());
                    topics.addAll(provider.getRootTopics());           
                    setKeys(topics);                   
                }
            });
        }        

        @Override
        protected void removeNotify() 
        {                            
            setKeys(Collections.<Topic>emptySet());
        }

        @Override
        protected Node[] createNodes(Topic topic) 
        {
            return new Node[] {new TopicNode(provider, topic)};
        }           
    }     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel6 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();

        setPreferredSize(new java.awt.Dimension(480, 113));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FileVisualPanel1.class, "FileVisualPanel1.jLabel1.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FileVisualPanel1.class, "FileVisualPanel1.jLabel2.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel2, gridBagConstraints);

        jTextField2.setColumns(28);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jTextField2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(FileVisualPanel1.class, "FileVisualPanel1.jLabel3.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel3, gridBagConstraints);

        jPanel1.setPreferredSize(new java.awt.Dimension(150, 23));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        panel1 = new JFXPanel();
        jPanel1.add(panel1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        panel2 = new JFXPanel();
        jPanel2.add(panel2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(FileVisualPanel1.class, "FileVisualPanel1.jLabel4.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel4, gridBagConstraints);

        jComboBox1.setModel(fileTypes);
        jComboBox1.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(FileVisualPanel1.class, "FileVisualPanel1.jLabel6.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel6, gridBagConstraints);

        jComboBox2.setModel(modifiers);
        jComboBox2.setSelectedItem(VisibilityProvider.Modifier.NONE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBox2, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JComboBox<FileTypeProvider> jComboBox1;
    private javax.swing.JComboBox<VisibilityProvider.Modifier> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
