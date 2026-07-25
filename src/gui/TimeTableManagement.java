package gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import model.MySQL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author daytr
 */
public class TimeTableManagement extends javax.swing.JFrame {

    private final HashMap<String, Integer> gradeMap = new HashMap<>();
    private final HashMap<String, Integer> dayMap = new HashMap<>();
    private final HashMap<String, Integer> subjectMap = new HashMap<>();

    public TimeTableManagement() {
        initComponents();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        loadDays();
        loadGrades();
        loadSubjects();
        loadTimeTable();
    }

    private void loadDays() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `day`");
            DefaultComboBoxModel<String> dcm = (DefaultComboBoxModel<String>) dayComboBox.getModel();
            dcm.removeAllElements();
            dayMap.clear();

            while (rs.next()) {
                String dayName = rs.getString("name");
                dayMap.put(dayName, rs.getInt("id"));
                dcm.addElement(dayName);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadGrades() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `grade`");
            DefaultComboBoxModel<String> dcm = (DefaultComboBoxModel<String>) gradeComboBox.getModel();
            dcm.removeAllElements();
            dcm.addElement("Select");
            gradeMap.clear();

            while (rs.next()) {
                String gradeName = rs.getString("name");
                gradeMap.put(gradeName, rs.getInt("id"));
                dcm.addElement(gradeName);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadSubjects() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `subject`");
            DefaultComboBoxModel<String> dcm = (DefaultComboBoxModel<String>) subjectComboBox.getModel();
            dcm.removeAllElements();
            dcm.addElement("Select");
            subjectMap.clear();

            while (rs.next()) {
                String subjectName = rs.getString("name");
                subjectMap.put(subjectName, rs.getInt("id"));
                dcm.addElement(subjectName);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadTimeTable() {
        try {
            DefaultTableModel dtm = (DefaultTableModel) timeTable.getModel();
            dtm.setRowCount(0);

            String selectedDay = (String) dayComboBox.getSelectedItem();
            if (selectedDay == null) {
                return; // Exit if no day is selected yet
            }
            Integer dayId = dayMap.get(selectedDay);
            if (dayId == null) {
                return; // Exit if day ID not found
            }
            String query = "SELECT ghs.id, g.name AS grade_name, s.name AS subject_name "
                    + "FROM grade_has_subject ghs "
                    + "JOIN grade g ON ghs.grade_id = g.id "
                    + "JOIN subject s ON ghs.subject_id = s.id "
                    + "WHERE ghs.day_id = ? "
                    + "ORDER BY g.priority ASC, s.name ASC";

            ResultSet rs = MySQL.executeSearch(query, dayId);

            while (rs.next()) {
                Vector<Object> v = new Vector<>();
                v.add(rs.getInt("id"));
                v.add(rs.getString("grade_name"));
                v.add(rs.getString("subject_name"));
                dtm.addRow(v);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearFields() {
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnAdd.setEnabled(true);
//        dayComboBox.setSelectedIndex(0);
        gradeComboBox.setSelectedIndex(0);
        subjectComboBox.setSelectedIndex(0);
        timeTable.clearSelection();
        loadGrades();
        dayComboBox.grabFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        gradeComboBox = new javax.swing.JComboBox<>();
        subjectComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        timeTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnDelete1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        dayComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TimeTable Management");

        jLabel1.setFont(new java.awt.Font("Roboto", 1, 30)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TimeTable Management");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Manage Entry", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 0, 12))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel2.setText("Grade: ");

        btnDelete.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        btnDelete.setText("Delete Entry");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        btnUpdate.setText("Update Entry");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        btnAdd.setText("Add Entry");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        gradeComboBox.setFont(new java.awt.Font("Nirmala UI", 0, 14)); // NOI18N
        gradeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        subjectComboBox.setFont(new java.awt.Font("Nirmala UI", 0, 14)); // NOI18N
        subjectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel4.setText("Subject: ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 413, Short.MAX_VALUE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gradeComboBox, 0, 345, Short.MAX_VALUE)
                        .addGap(59, 59, 59)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(subjectComboBox, 0, 345, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnAdd, btnDelete, btnUpdate});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(gradeComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                        .addComponent(subjectComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        timeTable.setFont(new java.awt.Font("Nirmala UI", 0, 14)); // NOI18N
        timeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Grade", "Subject"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        timeTable.setRowHeight(25);
        timeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                timeTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(timeTable);
        if (timeTable.getColumnModel().getColumnCount() > 0) {
            timeTable.getColumnModel().getColumn(0).setResizable(false);
            timeTable.getColumnModel().getColumn(0).setPreferredWidth(10);
            timeTable.getColumnModel().getColumn(1).setResizable(false);
            timeTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            timeTable.getColumnModel().getColumn(2).setResizable(false);
            timeTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 731, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        btnDelete1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        btnDelete1.setText("Clear");
        btnDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelete1ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Day", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Roboto", 0, 12))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel3.setText("Day of the Week:");

        dayComboBox.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        dayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dayComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dayComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dayComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dayComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelete1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete1))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelete1ActionPerformed
        clearFields();
    }//GEN-LAST:event_btnDelete1ActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        String grade = (String) gradeComboBox.getSelectedItem();
        String subject = (String) subjectComboBox.getSelectedItem();
        String day = (String) dayComboBox.getSelectedItem();

        if ("Select".equals(grade) || "Select".equals(subject)) {
            JOptionPane.showMessageDialog(this, "Please select a grade and a subject.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Integer gradeId = gradeMap.get(grade);
            Integer dayId = dayMap.get(day);
            Integer subjectId = subjectMap.get(subject);

            // Check for duplicates
            String checkQuery = "SELECT * FROM `grade_has_subject` WHERE `grade_id`=? AND `subject_id`=? AND `day_id`=?";
            ResultSet rs = MySQL.executeSearch(checkQuery, gradeId, subjectId, dayId);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "This entry already exists for the selected day.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                String insertQuery = "INSERT INTO `grade_has_subject`(`grade_id`, `day_id`, `subject_id`) VALUES(?, ?, ?)";
                MySQL.executeIUD(insertQuery, gradeId, dayId, subjectId);
                loadTimeTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Timetable entry added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        int selectedRow = timeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String grade = (String) gradeComboBox.getSelectedItem();
        String subject = (String) subjectComboBox.getSelectedItem();
        String day = (String) dayComboBox.getSelectedItem();

        if ("Select".equals(grade) || "Select".equals(subject)) {
            JOptionPane.showMessageDialog(this, "Please select a grade and a subject.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer id = (Integer) timeTable.getValueAt(selectedRow, 0);

        try {
            Integer gradeId = gradeMap.get(grade);
            Integer dayId = dayMap.get(day);
            Integer subjectId = subjectMap.get(subject);

            // Check for duplicates before updating
            String checkQuery = "SELECT * FROM `grade_has_subject` WHERE `grade_id`=? AND `subject_id`=? AND `day_id`=? AND `id`!=?";
            ResultSet rs = MySQL.executeSearch(checkQuery, gradeId, subjectId, dayId, id);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "This combination already exists in the timetable.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                String updateQuery = "UPDATE `grade_has_subject` SET `grade_id`=?, `subject_id`=? WHERE `id`=?";
                MySQL.executeIUD(updateQuery, gradeId, subjectId, id);
                loadTimeTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Timetable entry updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selectedRow = timeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entry to delete.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer id = (Integer) timeTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this entry?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                MySQL.executeIUD("DELETE FROM `grade_has_subject` WHERE `id`=?", id);
                loadTimeTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Timetable entry deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(TimeTableManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void timeTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_timeTableMouseClicked
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnAdd.setEnabled(false);
        int selectedRow = timeTable.getSelectedRow();
        if (selectedRow != -1) {
            String gradeName = (String) timeTable.getValueAt(selectedRow, 1);
            String subjectName = (String) timeTable.getValueAt(selectedRow, 2);

            gradeComboBox.setSelectedItem(gradeName);
            subjectComboBox.setSelectedItem(subjectName);
        }
    }//GEN-LAST:event_timeTableMouseClicked

    private void dayComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dayComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            loadTimeTable();
            clearFields();
        }
    }//GEN-LAST:event_dayComboBoxItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacDarkLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TimeTableManagement().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDelete1;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> dayComboBox;
    private javax.swing.JComboBox<String> gradeComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> subjectComboBox;
    private javax.swing.JTable timeTable;
    // End of variables declaration//GEN-END:variables
}
