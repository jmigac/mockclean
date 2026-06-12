package hr.from.juricamigac.mockclean.settings;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.components.JBScrollPane;

public final class AemJsonCleanerConfigurable implements Configurable {

    private JPanel panel;
    private JTextArea excludedVariablesTextArea;

    @Override
    public String getDisplayName() {
        return "MockClean AEM";
    }

    @Override
    public JComponent createComponent() {
        if (this.panel != null) {
            return this.panel;
        }

        this.excludedVariablesTextArea = new JTextArea(14, 44);
        this.excludedVariablesTextArea.setLineWrap(false);

        final JLabel description = new JLabel("Property names to remove from JSON files. Use one property name per line.");
        final JScrollPane scrollPane = new JBScrollPane(this.excludedVariablesTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Removed JSON properties"));

        final JButton defaultsButton = new JButton("Restore Defaults");
        defaultsButton.addActionListener(event -> this.excludedVariablesTextArea.setText(String.join("\n", AemJsonCleanerSettings.DEFAULT_EXCLUDED_VARIABLES)));

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(defaultsButton);

        this.panel = new JPanel(new BorderLayout(0, 8));
        this.panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.panel.add(description, BorderLayout.NORTH);
        this.panel.add(scrollPane, BorderLayout.CENTER);
        this.panel.add(buttonPanel, BorderLayout.SOUTH);

        this.reset();
        return this.panel;
    }

    @Override
    public boolean isModified() {
        if (this.excludedVariablesTextArea == null) {
            return false;
        }
        final List<String> current = AemJsonCleanerSettings.parseVariablesText(this.excludedVariablesTextArea.getText());
        final List<String> stored = AemJsonCleanerSettings.getInstance()
                                                          .getExcludedVariables();
        return !Objects.equals(current, stored);
    }

    @Override
    public void apply() {
        if (this.excludedVariablesTextArea == null) {
            return;
        }
        AemJsonCleanerSettings.getInstance()
                              .setExcludedVariables(AemJsonCleanerSettings.parseVariablesText(this.excludedVariablesTextArea.getText()));
    }

    @Override
    public void reset() {
        if (this.excludedVariablesTextArea != null) {
            this.excludedVariablesTextArea.setText(AemJsonCleanerSettings.getInstance()
                                                                         .getExcludedVariablesAsText());
            this.excludedVariablesTextArea.setCaretPosition(0);
        }
    }

    @Override
    public void disposeUIResources() {
        this.panel = null;
        this.excludedVariablesTextArea = null;
    }

}
