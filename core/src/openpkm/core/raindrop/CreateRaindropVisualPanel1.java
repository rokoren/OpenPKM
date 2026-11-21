/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/visualPanel.java to edit this template
 */
package openpkm.core.raindrop;

import java.awt.Component;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import openpkm.base.IconProvider;
import openpkm.base.TitleProvider;
import openpkm.raindrop.RaindropAccount;
import openpkm.raindrop.RaindropChildrenCollection;
import openpkm.raindrop.RaindropCollection;
import openpkm.raindrop.RaindropCollectionProvider;
import openpkm.utils.Utils;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.util.ImageUtilities;

public final class CreateRaindropVisualPanel1 extends JPanel 
{
    private static final Logger LOG = Logger.getLogger(CreateRaindropVisualPanel1.class.getName());
    
    private final DefaultComboBoxModel<RaindropProject> projects = new DefaultComboBoxModel<>();  
    private final DefaultComboBoxModel<RaindropChildrenCollection> model = new DefaultComboBoxModel<>();  
    private final ListCellRenderer renderer1 = new ListCellRendererImpl1();
    private final ListCellRenderer renderer2 = new ListCellRendererImpl2();   
    
    /**
     * Creates new form CreateRaindropVisualPanel1
     */
    public CreateRaindropVisualPanel1() 
    {
        setProjects(); 
        setCollections();
        initComponents();                
    }

    @Override
    public String getName() 
    {
        return "Link";
    }
    
    public String getLink()
    {
        return jTextField1.getText().trim();
    }
    
    public void setLink(String link)
    {
        jTextField1.setText(link);
    }
    
    public RaindropProject getSelectedProject()
    {
        return (RaindropProject)projects.getSelectedItem();
    }
    
    public RaindropCollection getSelectedCollection()
    {
        return (RaindropCollection)model.getSelectedItem();
    }    
    
    public boolean isRaindropImportant()
    {
        return jCheckBox2.isSelected();
    }
    
    private void setProjects()
    {
        Project mainProject = OpenProjects.getDefault().getMainProject();
        Project[] openProjects = OpenProjects.getDefault().getOpenProjects();
        SortedSet<RaindropProject> sorted = new TreeSet<RaindropProject>(titleComparator());
        for(Project project : openProjects)
        {
            if(project instanceof RaindropProject raindropProject)
            {
                sorted.add(raindropProject);
            }
        }
        projects.addAll(sorted);
        if(mainProject instanceof RaindropProject raindropProject)
        {
            projects.setSelectedItem(raindropProject);                     
        }
    }
    
    private List<RaindropChildrenCollection> getCollections(RaindropAccount account, RaindropCollection collection) throws IOException
    {
        List<RaindropChildrenCollection> list = new ArrayList();
        List<RaindropChildrenCollection> collections = account.getChildrenCollections(collection.getCollectionID());
        for(RaindropChildrenCollection childrenCollection : collections)
        {
            list.addAll(getCollections(account, childrenCollection));
        }
        list.addAll(collections);
        return list;
    }
    
    private void setCollections()
    {
        model.removeAllElements();
        RaindropCollectionProvider provider = getSelectedProject();
        if(provider != null)
        {
            try
            {
                List<RaindropChildrenCollection> collections = getCollections(provider.getRaindropAccount(), provider.getRaindropCollection());
                Set<RaindropChildrenCollection> sorted = new TreeSet<RaindropChildrenCollection>(RaindropCollection.titleComparator());
                sorted.addAll(collections);
                model.addAll(sorted);                 
            }  
            catch(IOException e)
            {
                LOG.warning(e.getMessage());
            }
        }
    }        
    
    private static class ListCellRendererImpl1 extends JLabel implements ListCellRenderer<RaindropProject>
    {        
        public ListCellRendererImpl1() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
            setIconTextGap(10);
        }        

        @Override
        public Component getListCellRendererComponent(JList list, RaindropProject raindropProject, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(raindropProject == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {  
                TitleProvider titleProvider = raindropProject.getLookup().lookup(TitleProvider.class);
                if(titleProvider != null)
                {
                    setText(titleProvider.getTitle());                     
                }
                
                IconProvider iconProvider = raindropProject.getLookup().lookup(IconProvider.class);
                if(iconProvider != null)
                {
                    setIcon(ImageUtilities.image2Icon(iconProvider.getIcon(BeanInfo.ICON_COLOR_16x16)));                      
                }
            }
            
            return this;
        }
    } 
    
    private static class ListCellRendererImpl2 extends JLabel implements ListCellRenderer<RaindropChildrenCollection>
    {
        public ListCellRendererImpl2() 
        {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.LEADING);
            setVerticalAlignment(CENTER);
        }   

        @Override
        public Component getListCellRendererComponent(JList list, RaindropChildrenCollection collection, int index, boolean isSelected, boolean cellHasFocus) 
        {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if(collection == null)
            {
                setText("");
                setIcon(null);
            }
            else
            {
                try
                {
                    setIcon(ImageUtilities.image2Icon(Utils.resizeImage(collection.getImage(), 16, 16)));                    
                }
                catch(IOException e)
                {
                    LOG.warning(e.getMessage());
                }
                setText(collection.getTitle());                   
            }
            
            return this;
        }
    }      

    private static Comparator<RaindropProject> titleComparator() 
    {
        return new Comparator<RaindropProject>() 
        {
            @Override
            public int compare(RaindropProject project1, RaindropProject project2) 
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jLabel4 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jCheckBox2 = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jPanel1.border.title"))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jLabel2.text_1") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel1.add(jLabel2, gridBagConstraints);

        jComboBox3.setModel(projects);
        jComboBox3.setPreferredSize(new java.awt.Dimension(200, 22));
        jComboBox3.setRenderer(renderer1);
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                project(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jComboBox3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel1.add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jLabel4.text") + ":"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel1.add(jLabel4, gridBagConstraints);

        jComboBox4.setModel(model);
        jComboBox4.setRenderer(renderer2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jComboBox4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        jPanel1.add(filler5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCheckBox2, org.openide.util.NbBundle.getMessage(CreateRaindropVisualPanel1.class, "CreateRaindropVisualPanel1.jCheckBox2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jCheckBox2, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filler3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void project(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_project
        // TODO add your handling code here:
        setCollections();
    }//GEN-LAST:event_project

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox<RaindropProject> jComboBox3;
    private javax.swing.JComboBox<RaindropChildrenCollection> jComboBox4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
