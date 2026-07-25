package gui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.ResultSet;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.MySQL;

/**
 *
 * @author daytr
 */
public class Main extends javax.swing.JFrame {

    java.time.LocalDate displayedDate = java.time.LocalDate.now();
    private int daysOffset = 0;
    // Format the date as yyyy-MM-dd
    java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Main() {
        initComponents();
        this.setMinimumSize(new Dimension(500, 521));
        configureDatePicker();
        clock();
        loadSubjects();
        loadContent();
        updateDateAndDay(); // Initialize date and day display
    }

    private void updateDateAndDay() {
        String formattedDate = displayedDate.format(dateFormatter);
        String dayOfWeek = displayedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        dateField.setText(formattedDate);
        jLabel2.setText(dayOfWeek);
    }

    private void adjustDaysOffset(int delta) {
        setDisplayedDate(displayedDate.plusDays(delta));
    }

    private void setDisplayedDate(java.time.LocalDate date) {
        displayedDate = date;
        daysOffset = (int) java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), displayedDate);
        updateDateAndDay();
        loadSubjects();
    }

    private void configureDatePicker() {
        dateField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField.setToolTipText("Click to choose a date");
        dateField.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openDatePicker();
            }
        });
    }

    private void openDatePicker() {
        Date selectedDate = Date.from(displayedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        SpinnerDateModel dateModel = new SpinnerDateModel(selectedDate, null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));

        int option = JOptionPane.showConfirmDialog(this, dateSpinner, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            Date date = (Date) dateSpinner.getValue();
            setDisplayedDate(date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        }
    }

    private void loadSubjects() {
        try {
            String dayOfWeek = displayedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            ResultSet rs = MySQL.executeSearch(
                    "SELECT grade.name AS grade_name, subject.name AS subject_name FROM grade_has_subject "
                    + "JOIN grade ON grade_has_subject.grade_id = grade.id "
                    + "JOIN subject ON grade_has_subject.subject_id = subject.id "
                    + "JOIN day ON grade_has_subject.day_id = day.id "
                    + "WHERE day.name = ? ORDER BY `grade`.`priority` ASC, `subject`.`name` ASC",
                    dayOfWeek
            );

            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> vector = new Vector<>();
                String grade = rs.getString("grade.name");
                String subject = rs.getString("subject.name");
                vector.add(grade + " - " + subject + " (" + displayedDate.format(dateFormatter) + ")");
                vector.add("Copy");
                model.addRow(vector);
            }

            jTable1.getColumn("Action").setCellRenderer(new StatusButtonRenderer());
            jTable1.getColumn("Action").setCellEditor(new StatusButtonEditor(new JCheckBox()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadContent() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `settings`");
            Vector<String> vector = new Vector<>();
            vector.add("Select");

            while (rs.next()) {
                vector.add(rs.getString("content"));
            }

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(vector);
            jComboBox1.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void showCopyFeedback(JButton button) {
        String originalText = button.getText();
        button.setText("Copied");
        button.setEnabled(false);

        Timer timer = new Timer(900, event -> {
            button.setText(originalText);
            button.setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showWindowCopyFeedback() {
        String originalTitle = getTitle();
        setTitle(originalTitle + " - Copied");

        Timer timer = new Timer(900, event -> setTitle(originalTitle));
        timer.setRepeats(false);
        timer.start();
    }

    private void clock() {
        Thread clockThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        // Get the current date and time
                        java.time.LocalTime currentTime = java.time.LocalTime.now().withNano(0);

                        // Format the time as HH:mm:ss
                        java.time.format.DateTimeFormatter timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
                        String formattedTime = currentTime.format(timeFormatter);

                        // Set the formatted time to jLabel3
                        timeLabel.setText(formattedTime);

                        // Sleep for 1 second (1000 milliseconds)
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        clockThread.start(); // Start the thread
    }

    // Custom Renderer
    class StatusButtonRenderer extends JButton implements TableCellRenderer {

        public StatusButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) value;
            setText(status);

            if ("Active".equals(status)) {
                setBackground(Color.GREEN);
                setForeground(Color.BLACK);
            } else {
                setBackground(new Color(74, 105, 132));
//                setBackground(new Color(74, 108, 74));
                setForeground(Color.WHITE);
            }

            return this;
        }
    }

// Custom Editor
    class StatusButtonEditor extends DefaultCellEditor {

        private JButton button;
        private String currentStatus;
        private boolean isPushed;
        private int row;

        public StatusButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> toggleStatus());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.currentStatus = (String) value;
            this.row = row;

            button.setText(currentStatus);
            if ("Active".equals(currentStatus)) {
                button.setBackground(Color.GREEN);
                button.setForeground(Color.BLACK);
            } else {
                setBackground(new Color(74, 105, 132));
                button.setForeground(Color.WHITE);
            }

            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return currentStatus;
        }

        private void toggleStatus() {
            int row = jTable1.getSelectedRow();
            String className = String.valueOf(jTable1.getValueAt(row, 0));
            copyToClipboard(className);
            showCopyFeedback(button);
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

        roundedPanel1 = new gui.RoundedPanel();
        roundedPanel2 = new gui.RoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        timeLabel = new javax.swing.JLabel();
        dateField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        roundedPanel3 = new gui.RoundedPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        nextButton1 = new javax.swing.JButton();
        gradeButton = new javax.swing.JButton();
        subjectButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        timeTableButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        currentButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rio Online School");
        setPreferredSize(new java.awt.Dimension(960, 540));

        roundedPanel1.setBackground(new java.awt.Color(51, 0, 51));

        roundedPanel2.setBackground(new java.awt.Color(11, 25, 44));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Rio Online School Subject Generator");

        timeLabel.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        timeLabel.setForeground(new java.awt.Color(255, 255, 255));
        timeLabel.setText("Time");

        dateField.setEditable(false);
        dateField.setBackground(new java.awt.Color(11, 25, 44));
        dateField.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        dateField.setForeground(new java.awt.Color(255, 255, 255));
        dateField.setBorder(null);

        jLabel2.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Day");

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(timeLabel)
                .addContainerGap())
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(timeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTable1.setBackground(new java.awt.Color(0, 0, 0));
        jTable1.setFont(new java.awt.Font("Nirmala UI", 0, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Subject", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setRowHeight(25);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(580);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(10);
        }

        roundedPanel3.setBackground(new java.awt.Color(0, 0, 0));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel3.setText("Select Option to Copy");

        nextButton1.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        nextButton1.setText("Copy");
        nextButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButton1ActionPerformed(evt);
            }
        });

        gradeButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        gradeButton.setText("Grade Management");
        gradeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeButtonActionPerformed(evt);
            }
        });

        subjectButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        subjectButton.setText("subject Management");
        subjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subjectButtonActionPerformed(evt);
            }
        });

        settingsButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        settingsButton.setText("settings Management");
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        timeTableButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        timeTableButton.setText("TimeTable Management");
        timeTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeTableButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeTableButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nextButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundedPanel3Layout.createSequentialGroup()
                        .addComponent(gradeButton, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                        .addGap(79, 79, 79)
                        .addComponent(subjectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                        .addGap(50, 50, 50)
                        .addComponent(settingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)))
                .addContainerGap())
        );
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gradeButton)
                    .addComponent(subjectButton)
                    .addComponent(settingsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(timeTableButton)
                .addGap(18, 18, 18)
                .addGroup(roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nextButton1))
                .addGap(8, 8, 8))
        );

        nextButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        currentButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        currentButton.setText("Current");
        currentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentButtonActionPerformed(evt);
            }
        });

        previousButton.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        previousButton.setText("Previous");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(roundedPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundedPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(previousButton)
                        .addGap(18, 18, 18)
                        .addComponent(currentButton)
                        .addGap(18, 18, 18)
                        .addComponent(nextButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(13, 13, 13)))
                .addGap(10, 10, 10))
        );

        roundedPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {currentButton, nextButton, previousButton});

        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(roundedPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(roundedPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previousButton)
                    .addComponent(currentButton)
                    .addComponent(nextButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        roundedPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {currentButton, nextButton, previousButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if (evt.getClickCount() == 2 && row != -1) {
            String className = String.valueOf(jTable1.getValueAt(row, 0));
            copyToClipboard(className);
            showWindowCopyFeedback();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        adjustDaysOffset(-1);
    }//GEN-LAST:event_previousButtonActionPerformed

    private void currentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentButtonActionPerformed
        daysOffset = 0;
        adjustDaysOffset(0);
    }//GEN-LAST:event_currentButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        adjustDaysOffset(1);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void timeTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeTableButtonActionPerformed

        TimeTableManagement timeTableManage = new TimeTableManagement();
        timeTableManage.setVisible(true);
    }//GEN-LAST:event_timeTableButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        Settings settings = new Settings();
        settings.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void subjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subjectButtonActionPerformed
        SubjectManagement subManage = new SubjectManagement();
        subManage.setVisible(true);
    }//GEN-LAST:event_subjectButtonActionPerformed

    private void gradeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeButtonActionPerformed
        GradeManagement grademanage = new GradeManagement();
        grademanage.setVisible(true);
    }//GEN-LAST:event_gradeButtonActionPerformed

    private void nextButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButton1ActionPerformed
        String setting = jComboBox1.getSelectedItem().toString();

        if (!setting.equals("Select")) {
            copyToClipboard(setting);
            showCopyFeedback(nextButton1);
        }
    }//GEN-LAST:event_nextButton1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        String content = String.valueOf(jComboBox1.getSelectedItem());
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatMacDarkLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton currentButton;
    private javax.swing.JTextField dateField;
    private javax.swing.JButton gradeButton;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton nextButton1;
    private javax.swing.JButton previousButton;
    private gui.RoundedPanel roundedPanel1;;
    private gui.RoundedPanel roundedPanel2;
    private gui.RoundedPanel roundedPanel3;
    private javax.swing.JButton settingsButton;
    private javax.swing.JButton subjectButton;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JButton timeTableButton;
    // End of variables declaration//GEN-END:variables
}
