package gui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import javax.swing.plaf.basic.BasicButtonUI;

public final class UITheme {

    public static final Color PRIMARY = new Color(59, 130, 246);
    public static final Color PRIMARY_DARK = new Color(37, 99, 235);
    public static final Color SECONDARY = new Color(71, 85, 105);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color DANGER = new Color(239, 68, 68);

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font CONTENT_FONT = new Font("Nirmala UI", Font.PLAIN, 14);
    public static final Font CONTENT_BOLD_FONT = new Font("Nirmala UI", Font.BOLD, 14);
    public static final Font TABLE_FONT = new Font("Nirmala UI", Font.PLAIN, 15);

    private static final int CONTROL_ARC = 18;
    private static final int CARD_ARC = 24;
    private static final String ROLE = "rio.theme.role";
    private static final String ROLE_ROOT = "root";
    private static final String ROLE_HEADER = "header";
    private static final String ROLE_SURFACE = "surface";

    private static boolean darkMode = true;

    private UITheme() {
    }

    public static void setupLookAndFeel() {
        installLookAndFeel();
        applyGlobalDefaults();
    }

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static String getToggleButtonText() {
        return darkMode ? "Light Mode" : "Dark Mode";
    }

    public static void toggleTheme() {
        darkMode = !darkMode;
        setupLookAndFeel();
        refreshOpenWindows();
    }

    public static void applyDashboardFrame(JFrame frame, JPanel rootPanel, JPanel headerPanel, JPanel actionPanel, JTable table) {
        rootPanel.putClientProperty(ROLE, ROLE_ROOT);
        headerPanel.putClientProperty(ROLE, ROLE_HEADER);
        actionPanel.putClientProperty(ROLE, ROLE_SURFACE);

        applyFrame(frame);
        styleTable(table);
    }

    public static void applyManagementFrame(JFrame frame, JLabel titleLabel, JTable table, JPanel... panels) {
        for (JPanel panel : panels) {
            panel.putClientProperty(ROLE, ROLE_SURFACE);
        }

        applyFrame(frame);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(text());
        styleTable(table);
    }

    private static void installLookAndFeel() {
        if (darkMode) {
            FlatMacDarkLaf.setup();
        } else {
            FlatMacLightLaf.setup();
        }
    }

    private static void applyGlobalDefaults() {
        UIManager.put("defaultFont", BODY_FONT);
        UIManager.put("Button.arc", CONTROL_ARC);
        UIManager.put("Component.arc", CONTROL_ARC);
        UIManager.put("ComboBox.arc", CONTROL_ARC);
        UIManager.put("TextComponent.arc", CONTROL_ARC);
        UIManager.put("ProgressBar.arc", CONTROL_ARC);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ComboBox.font", CONTENT_FONT);
        UIManager.put("List.font", CONTENT_FONT);
        UIManager.put("TextField.font", CONTENT_FONT);
        UIManager.put("Table.font", TABLE_FONT);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
    }

    private static void refreshOpenWindows() {
        for (Window window : Window.getWindows()) {
            if (window.isDisplayable()) {
                SwingUtilities.updateComponentTreeUI(window);
                if (window instanceof JFrame) {
                    applyFrame((JFrame) window);
                }
                window.invalidate();
                window.validate();
                window.repaint();
            }
        }
        FlatLaf.updateUI();
    }

    private static void applyFrame(JFrame frame) {
        frame.getRootPane().putClientProperty("JRootPane.titleBarBackground", background());
        frame.getRootPane().putClientProperty("JRootPane.titleBarForeground", text());
        frame.getContentPane().setBackground(background());
        styleTree(frame.getContentPane());
    }

    private static void styleTree(Component component) {
        if (component instanceof JComboBox) {
            styleComboBox((JComboBox<?>) component);
            return;
        } else if (component instanceof JSpinner) {
            styleSpinner((JSpinner) component);
            return;
        } else if (component instanceof JTextField) {
            styleTextField((JTextField) component);
            return;
        } else if (component instanceof JPanel) {
            stylePanel((JPanel) component);
        } else if (component instanceof AbstractButton) {
            styleButton((AbstractButton) component);
        } else if (component instanceof JTable) {
            styleTable((JTable) component);
        } else if (component instanceof JScrollPane) {
            styleScrollPane((JScrollPane) component);
        } else if (component instanceof JLabel) {
            styleLabel((JLabel) component);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                styleTree(child);
            }
        }
    }

    private static void stylePanel(JPanel panel) {
        Object role = panel.getClientProperty(ROLE);
        if (ROLE_ROOT.equals(role)) {
            panel.setOpaque(true);
            panel.setBackground(background());
            panel.setBorder(new EmptyBorder(14, 14, 14, 14));
        } else if (ROLE_HEADER.equals(role)) {
            panel.setOpaque(false);
            panel.setBackground(header());
            panel.setBorder(new EmptyBorder(10, 18, 10, 18));
        } else if (ROLE_SURFACE.equals(role) || panel.getBorder() instanceof TitledBorder) {
            panel.setOpaque(false);
            panel.setBackground(surface());
        } else {
            panel.setOpaque(false);
            panel.setBackground(surface());
        }

        Border border = panel.getBorder();
        if (border instanceof TitledBorder) {
            TitledBorder titledBorder = (TitledBorder) border;
            titledBorder.setTitleColor(mutedText());
            titledBorder.setTitleFont(BODY_BOLD_FONT);
            titledBorder.setBorder(new RoundedBorder(borderColor(), CARD_ARC));
        } else if (ROLE_SURFACE.equals(role)) {
            panel.setBorder(new CompoundBorder(new RoundedBorder(borderColor(), CARD_ARC), new EmptyBorder(12, 14, 12, 14)));
        }
    }

    public static void styleActionButton(AbstractButton button) {
        styleButton(button);
    }

    private static void styleButton(AbstractButton button) {
        button.setUI(new RoundedButtonUI());
        button.setFont(BODY_BOLD_FONT);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 17, 9, 17));
        button.putClientProperty("JButton.buttonType", "roundRect");

        String text = button.getText() == null ? "" : button.getText().toLowerCase();
        if (text.contains("delete")) {
            button.setBackground(DANGER);
            button.setForeground(Color.WHITE);
        } else if (text.contains("add") || text.contains("copy") || text.contains("current")) {
            button.setBackground(PRIMARY_DARK);
            button.setForeground(Color.WHITE);
        } else if (text.contains("clear")) {
            button.setBackground(surfaceAlt());
            button.setForeground(text());
        } else {
            button.setBackground(SECONDARY);
            button.setForeground(Color.WHITE);
        }

        button.setPreferredSize(new Dimension(Math.max(button.getPreferredSize().width, 112), 40));
    }

    private static void styleTextField(JTextField textField) {
        textField.setFont(CONTENT_FONT);
        if (isInHeader(textField)) {
            textField.setForeground(mutedText());
            textField.setBackground(header());
            textField.setOpaque(false);
            textField.setBorder(new EmptyBorder(6, 2, 6, 2));
            return;
        }
        textField.setForeground(text());
        textField.setBackground(inputBackground());
        textField.setOpaque(false);
        textField.setCaretColor(text());
        textField.setBorder(controlBorder(7, 11, 7, 11));
    }

    private static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(CONTENT_FONT);
        comboBox.setForeground(text());
        comboBox.setBackground(inputBackground());
        comboBox.setOpaque(false);
        comboBox.setBorder(controlBorder(3, 8, 3, 8));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 40));
        installSinhalaComboRenderer(comboBox);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void installSinhalaComboRenderer(JComboBox<?> comboBox) {
        JComboBox rawComboBox = comboBox;
        rawComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(CONTENT_FONT);
                label.setBorder(new EmptyBorder(7, 10, 7, 10));
                if (!isSelected) {
                    label.setBackground(surface());
                    label.setForeground(text());
                }
                return label;
            }
        });
    }

    private static void styleSpinner(JSpinner spinner) {
        spinner.setFont(CONTENT_FONT);
        spinner.setOpaque(false);
        spinner.setBorder(controlBorder(4, 8, 4, 8));
    }

    private static void styleLabel(JLabel label) {
        Container parent = label.getParent();
        if (isInHeader(label)) {
            label.setForeground(Color.WHITE);
            label.setFont(label.getFont().deriveFont(label == parent.getComponent(0) ? Font.BOLD : Font.PLAIN, 15f));
        } else if (label.getFont().getSize() >= 24) {
            label.setFont(TITLE_FONT);
            label.setForeground(text());
        } else if (label.getFont().isBold()) {
            label.setFont(BODY_BOLD_FONT);
            label.setForeground(text());
        } else {
            label.setFont(BODY_FONT);
            label.setForeground(mutedText());
        }
    }

    private static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setBackground(surface());
        scrollPane.setBorder(new RoundedBorder(borderColor(), CARD_ARC));
    }

    private static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(36);
        table.setGridColor(borderColor());
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(surface());
        table.setForeground(text());
        table.setSelectionBackground(selection());
        table.setSelectionForeground(text());
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(BODY_BOLD_FONT);
            header.setBackground(surfaceAlt());
            header.setForeground(text());
            header.setReorderingAllowed(false);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        }
    }

    private static boolean isInHeader(Component component) {
        Component current = component;
        while (current != null) {
            if (current instanceof JPanel && ROLE_HEADER.equals(((JPanel) current).getClientProperty(ROLE))) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private static Border controlBorder(int top, int left, int bottom, int right) {
        return new CompoundBorder(new RoundedBorder(borderColor(), CONTROL_ARC), new EmptyBorder(top, left, bottom, right));
    }

    private static Color background() {
        return darkMode ? new Color(8, 13, 24) : new Color(245, 247, 251);
    }

    private static Color surface() {
        return darkMode ? new Color(17, 24, 39) : Color.WHITE;
    }

    private static Color surfaceAlt() {
        return darkMode ? new Color(30, 41, 59) : new Color(238, 243, 249);
    }

    private static Color inputBackground() {
        return darkMode ? new Color(15, 23, 42) : new Color(248, 250, 252);
    }

    private static Color text() {
        return darkMode ? new Color(226, 232, 240) : new Color(25, 34, 48);
    }

    private static Color mutedText() {
        return darkMode ? new Color(148, 163, 184) : new Color(88, 101, 120);
    }

    private static Color borderColor() {
        return darkMode ? new Color(51, 65, 85) : new Color(216, 224, 234);
    }

    private static Color header() {
        return darkMode ? new Color(2, 6, 23) : new Color(15, 23, 42);
    }

    private static Color selection() {
        return darkMode ? new Color(37, 99, 235) : new Color(219, 234, 254);
    }


    private static Color hoverColor(Color color) {
        return darkMode ? blend(color, Color.WHITE, 0.10f) : blend(color, Color.BLACK, 0.08f);
    }

    private static Color pressedColor(Color color) {
        return darkMode ? blend(color, Color.BLACK, 0.18f) : blend(color, Color.BLACK, 0.14f);
    }

    private static Color disabledColor(Color color) {
        return blend(color, surface(), 0.45f);
    }

    private static Color blend(Color base, Color overlay, float amount) {
        float inverse = 1.0f - amount;
        int red = Math.round(base.getRed() * inverse + overlay.getRed() * amount);
        int green = Math.round(base.getGreen() * inverse + overlay.getGreen() * amount);
        int blue = Math.round(base.getBlue() * inverse + overlay.getBlue() * amount);
        return new Color(red, green, blue);
    }

    private static final class RoundedButtonUI extends BasicButtonUI {

        @Override
        public void paint(Graphics graphics, javax.swing.JComponent component) {
            AbstractButton button = (AbstractButton) component;
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color color = button.getBackground();
            ButtonModel model = button.getModel();
            if (!button.isEnabled()) {
                color = disabledColor(color);
            } else if (model.isPressed()) {
                color = pressedColor(color);
            } else if (model.isRollover()) {
                color = hoverColor(color);
            }

            graphics2D.setColor(color);
            graphics2D.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), CONTROL_ARC, CONTROL_ARC);
            graphics2D.dispose();
            super.paint(graphics, component);
        }
    }
    private static final class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int arc;

        private RoundedBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            insets.set(1, 1, 1, 1);
            return insets;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(color);
            graphics2D.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            graphics2D.dispose();
        }
    }
}