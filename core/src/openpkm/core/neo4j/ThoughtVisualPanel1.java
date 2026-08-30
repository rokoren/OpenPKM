/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package openpkm.core.neo4j;

import java.awt.Component;
import java.beans.BeanInfo;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import openpkm.base.IconProvider;
import openpkm.base.IconsProvider;
import openpkm.base.TagsProvider;
import openpkm.base.Thought;
import openpkm.base.ThoughtsGraphProvider;
import openpkm.base.TitleProvider;
import org.controlsfx.control.CheckComboBox;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author rok
 */
public class ThoughtVisualPanel1 extends javax.swing.JPanel 
{
    private final DefaultComboBoxModel<Project> projects = new DefaultComboBoxModel<>(); 
    private final DefaultComboBoxModel<Thought.Type> types = new DefaultComboBoxModel<>();  
    private final ObservableList<String> tags = FXCollections.observableArrayList(); 
    
    private final ListCellRenderer renderer1 = new ListCellRendererImpl1();
    private final ListCellRenderer renderer2 = new ListCellRendererImpl2();
    
    private CheckComboBox<String> comboBox;
    private JFXPanel panel;   
    
    /**
     * Creates new form ThoughtVisualPanel1
     */
    public ThoughtVisualPanel1() 
    {
        setProjects();
        setTypes();
        setTags();        
        
        initComponents();
        
        panel = new JFXPanel();
        jPanel1.add(panel);
        
        Platform.runLater(new Runnable() 
        {
            @Override
            public void run() 
            {                
                comboBox = new CheckComboBox(tags);
                comboBox.setMaxWidth(Double.MAX_VALUE);  
                comboBox.setMaxHeight(Double.MAX_VALUE);   
                Scene scene = new Scene(comboBox);                             
                panel.setScene(scene);                
            }
        });          
    }
    
    @Override
    public String getName() 
    {
        return "General";
    }
    
    public Project getSelectedProject()
    {
        return (Project)projects.getSelectedItem();
    }    
    
    public Thought.Type getSelectedType()
    {
        return (Thought.Type)types.getSelectedItem();
    }  

    public List<String> getSelectedTags()
    {
        return comboBox.getCheckModel().getCheckedItems();
    }  
    
    private void setProjects()
    {
        Project mainProject = OpenProjects.getDefault().getMainProject();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        SortedSet<Project> sorted = new TreeSet<Project>(titleComparator());        
        
        for(Project project : openProjects)
        {
            if(project.getLookup().lookup(ThoughtsGraphProvider.class) != null)
            {
                sorted.add(project);
            }
        }
        
        projects.addAll(sorted);
        
        if(mainProject != null && mainProject.getLookup().lookup(ThoughtsGraphProvider.class) != null)
        {
            projects.setSelectedItem(mainProject);                     
        }
    }  
    
    private void setTypes()
    {
        types.removeAllElements();
        types.addAll(Arrays.asList(Thought.Type.values()));        
    }       
    
    private void setTags(Set<String> projectTags)
    {
        tags.clear();
        tags.addAll(projectTags);
        Collections.sort(tags);        
    }      

    private void setTags()
    {
        Project project = getSelectedProject();
        if(project != null)
        {
            TagsProvider tagsProvider = project.getLookup().lookup(TagsProvider.class);
            if(tagsProvider != null)
            {
                setTags(tagsProvider.getTags());   
                return;
            }             
        } 
        setTags(Collections.EMPTY_SET);
    }     
    
    private static class ListCellRendererImpl1 extends JLabel implements ListCellRenderer<Project>
    {        
        public ListCellRendererImpl1() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
            setIconTextGap(10);
        }        

        @Override
        public Component getListCellRendererComponent(JList list, Project project, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(project == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {  
                TitleProvider titleProvider = project.getLookup().lookup(TitleProvider.class);
                if(titleProvider != null)
                {
                    setText(titleProvider.getTitle());                     
                }
                
                IconProvider iconProvider = project.getLookup().lookup(IconProvider.class);
                if(iconProvider != null)
                {
                    setIcon(ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16)));                      
                }
            }
            
            return this;
        }
    }  
    
    private static class ListCellRendererImpl2 extends JLabel implements ListCellRenderer<Thought.Type>
    {
        public ListCellRendererImpl2() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
        }   

        @Override
        public Component getListCellRendererComponent(JList list, Thought.Type type, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(type == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {
                IconsProvider provider = Lookup.getDefault().lookup(IconsProvider.class);
                setIcon(provider.getIcon(type.getIcon()));    
                setText(type.toString());                   
            }
            
            return this;
        }
    }     
    
    private static Comparator<Project> titleComparator() 
    {
        return new Comparator<Project>() 
        {
            @Override
            public int compare(Project project1, Project project2) 
            {
                TitleProvider titleProvider1 = project1.getLookup().lookup(TitleProvider.class);
                TitleProvider titleProvider2 = project2.getLookup().lookup(TitleProvider.class);
                if(titleProvider1 != null && titleProvider2 != null)
                {
                    return titleProvider1.getTitle().compareTo(titleProvider2.getTitle());                     
                }
                return -1;
            }
        };
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
        jComboBox1 = new javax.swing.JComboBox<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ThoughtVisualPanel1.class, "ThoughtVisualPanel1.jLabel1.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel1, gridBagConstraints);

        jComboBox1.setModel(projects);
        jComboBox1.setRenderer(renderer1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ThoughtVisualPanel1.class, "ThoughtVisualPanel1.jLabel2.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel2, gridBagConstraints);

        jComboBox2.setModel(types);
        jComboBox2.setSelectedItem(Thought.Type.STATEMENT);
        jComboBox2.setRenderer(renderer2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jComboBox2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        add(filler2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ThoughtVisualPanel1.class, "ThoughtVisualPanel1.jLabel3.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jLabel3, gridBagConstraints);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JComboBox<Project> jComboBox1;
    private javax.swing.JComboBox<Thought.Type> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
