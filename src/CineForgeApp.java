import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class CineForgeApp extends JFrame {
    private static final Color DARK_BG = new Color(20, 25, 35);
    private static final Color CARD_BG = new Color(35, 42, 55);
    private static final Color ACCENT_COLOR = new Color(100, 150, 255);
    private static final Color TEXT_COLOR = new Color(220, 225, 235);
    private static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 14);
    private JComboBox<String> genreCombo;
    private JTextField settingField;
    private JComboBox<String> toneCombo;
    private JTextField keywordsField;
    private JTextField charactersField;
    private JTextArea plotTextArea;
    private JButton generateButton;
    private JButton saveButton;
    private JButton regenerateButton;
    private JButton exportButton;
    private JButton deleteButton;
    private JComboBox<String> sortCombo;
    private JList<Plot> historyList;
    private DefaultListModel<Plot> historyModel;
    private DatabaseManager dbManager;
    private OpenRouterClient aiClient;
    private Plot currentPlot;
    private String currentSortOrder = "date_desc";
    
    private static final Map<String, Map<String, String>> CINEMATIC_TONES = new HashMap<>();
    
    static {
        Map<String, String> actionMap = new HashMap<>();
        actionMap.put("Dark", "Ruthless Action");
        actionMap.put("Light", "Heroic Action");
        actionMap.put("Mysterious", "Covert Action");
        actionMap.put("Comedic", "Parody Action");
        actionMap.put("Dramatic", "Emotional Action");
        actionMap.put("Suspenseful", "Tense Action");
        actionMap.put("Romantic", "Romantic Action");
        actionMap.put("Epic", "Grand-scale Action");
        actionMap.put("Intense", "High-octane Action");
        actionMap.put("Whimsical", "Fantastical Action");
        CINEMATIC_TONES.put("Action", actionMap);
        
        Map<String, String> dramaMap = new HashMap<>();
        dramaMap.put("Dark", "Gritty Drama");
        dramaMap.put("Light", "Heartwarming Drama");
        dramaMap.put("Mysterious", "Enigmatic Drama");
        dramaMap.put("Comedic", "Dramedy");
        dramaMap.put("Dramatic", "Intense Drama");
        dramaMap.put("Suspenseful", "Thriller Drama");
        dramaMap.put("Romantic", "Romance Drama");
        dramaMap.put("Epic", "Historical Drama");
        dramaMap.put("Intense", "Emotional Drama");
        dramaMap.put("Whimsical", "Whimsical Drama");
        CINEMATIC_TONES.put("Drama", dramaMap);
        
        Map<String, String> comedyMap = new HashMap<>();
        comedyMap.put("Dark", "Dark Comedy");
        comedyMap.put("Light", "Lighthearted Comedy");
        comedyMap.put("Mysterious", "Mystery Comedy");
        comedyMap.put("Comedic", "Slapstick Comedy");
        comedyMap.put("Dramatic", "Dramatic Comedy");
        comedyMap.put("Suspenseful", "Suspenseful Comedy");
        comedyMap.put("Romantic", "Romantic Comedy");
        comedyMap.put("Epic", "Epic Comedy");
        comedyMap.put("Intense", "Intense Comedy");
        comedyMap.put("Whimsical", "Whimsical Comedy");
        CINEMATIC_TONES.put("Comedy", comedyMap);
        
        Map<String, String> horrorMap = new HashMap<>();
        horrorMap.put("Dark", "Terrifying Horror");
        horrorMap.put("Light", "Campy Horror");
        horrorMap.put("Mysterious", "Psychological Horror");
        horrorMap.put("Comedic", "Horror Comedy");
        horrorMap.put("Dramatic", "Gothic Horror");
        horrorMap.put("Suspenseful", "Suspense Horror");
        horrorMap.put("Romantic", "Romantic Horror");
        horrorMap.put("Epic", "Apocalyptic Horror");
        horrorMap.put("Intense", "Extreme Horror");
        horrorMap.put("Whimsical", "Fantasy Horror");
        CINEMATIC_TONES.put("Horror", horrorMap);
        
        Map<String, String> thrillerMap = new HashMap<>();
        thrillerMap.put("Dark", "Noir Thriller");
        thrillerMap.put("Light", "Mystery Thriller");
        thrillerMap.put("Mysterious", "Psychological Thriller");
        thrillerMap.put("Comedic", "Comedic Thriller");
        thrillerMap.put("Dramatic", "Dramatic Thriller");
        thrillerMap.put("Suspenseful", "Suspense Thriller");
        thrillerMap.put("Romantic", "Romantic Thriller");
        thrillerMap.put("Epic", "Conspiracy Thriller");
        thrillerMap.put("Intense", "Action Thriller");
        thrillerMap.put("Whimsical", "Unconventional Thriller");
        CINEMATIC_TONES.put("Thriller", thrillerMap);
        
        Map<String, String> sciFiMap = new HashMap<>();
        sciFiMap.put("Dark", "Dystopian Sci-Fi");
        sciFiMap.put("Light", "Optimistic Sci-Fi");
        sciFiMap.put("Mysterious", "Mystery Sci-Fi");
        sciFiMap.put("Comedic", "Comedic Sci-Fi");
        sciFiMap.put("Dramatic", "Dramatic Sci-Fi");
        sciFiMap.put("Suspenseful", "Suspenseful Sci-Fi");
        sciFiMap.put("Romantic", "Romantic Sci-Fi");
        sciFiMap.put("Epic", "Space Opera");
        sciFiMap.put("Intense", "Cyberpunk");
        sciFiMap.put("Whimsical", "Whimsical Sci-Fi");
        CINEMATIC_TONES.put("Sci-Fi", sciFiMap);
        
        Map<String, String> romanceMap = new HashMap<>();
        romanceMap.put("Dark", "Tragic Romance");
        romanceMap.put("Light", "Lighthearted Romance");
        romanceMap.put("Mysterious", "Mysterious Romance");
        romanceMap.put("Comedic", "Romantic Comedy");
        romanceMap.put("Dramatic", "Dramatic Romance");
        romanceMap.put("Suspenseful", "Suspenseful Romance");
        romanceMap.put("Romantic", "Classic Romance");
        romanceMap.put("Epic", "Historical Romance");
        romanceMap.put("Intense", "Passionate Romance");
        romanceMap.put("Whimsical", "Whimsical Romance");
        CINEMATIC_TONES.put("Romance", romanceMap);
        
        Map<String, String> mysteryMap = new HashMap<>();
        mysteryMap.put("Dark", "Noir Mystery");
        mysteryMap.put("Light", "Cozy Mystery");
        mysteryMap.put("Mysterious", "Psychological Mystery");
        mysteryMap.put("Comedic", "Comedic Mystery");
        mysteryMap.put("Dramatic", "Dramatic Mystery");
        mysteryMap.put("Suspenseful", "Suspenseful Mystery");
        mysteryMap.put("Romantic", "Romantic Mystery");
        mysteryMap.put("Epic", "Historical Mystery");
        mysteryMap.put("Intense", "Crime Mystery");
        mysteryMap.put("Whimsical", "Whimsical Mystery");
        CINEMATIC_TONES.put("Mystery", mysteryMap);
        
        Map<String, String> adventureMap = new HashMap<>();
        adventureMap.put("Dark", "Gritty Adventure");
        adventureMap.put("Light", "Family Adventure");
        adventureMap.put("Mysterious", "Mysterious Adventure");
        adventureMap.put("Comedic", "Comedic Adventure");
        adventureMap.put("Dramatic", "Dramatic Adventure");
        adventureMap.put("Suspenseful", "Suspenseful Adventure");
        adventureMap.put("Romantic", "Romantic Adventure");
        adventureMap.put("Epic", "Epic Adventure");
        adventureMap.put("Intense", "Action Adventure");
        adventureMap.put("Whimsical", "Fantasy Adventure");
        CINEMATIC_TONES.put("Adventure", adventureMap);
        
        Map<String, String> fantasyMap = new HashMap<>();
        fantasyMap.put("Dark", "Dark Fantasy");
        fantasyMap.put("Light", "High Fantasy");
        fantasyMap.put("Mysterious", "Mysterious Fantasy");
        fantasyMap.put("Comedic", "Comedic Fantasy");
        fantasyMap.put("Dramatic", "Dramatic Fantasy");
        fantasyMap.put("Suspenseful", "Suspenseful Fantasy");
        fantasyMap.put("Romantic", "Romantic Fantasy");
        fantasyMap.put("Epic", "Epic Fantasy");
        fantasyMap.put("Intense", "Grimdark Fantasy");
        fantasyMap.put("Whimsical", "Whimsical Fantasy");
        CINEMATIC_TONES.put("Fantasy", fantasyMap);
    }
    
    public CineForgeApp() {
        dbManager = new DatabaseManager();
        aiClient = new OpenRouterClient();
        
        initializeUI();
        loadPlotHistory();
        
        setVisible(true);
    }
    
    private void initializeUI() {
        setTitle("CineForge - AI Movie Plot Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        getContentPane().setBackground(DARK_BG);
        
        setLayout(new BorderLayout(10, 10));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("🎬 CineForge");
        titleLabel.setFont(new Font("Algerian", Font.BOLD, 28));
        titleLabel.setForeground(ACCENT_COLOR);
        
        JLabel subtitleLabel = new JLabel("AI-Powered Movie Plot Generator");
        subtitleLabel.setFont(MAIN_FONT);
        subtitleLabel.setForeground(TEXT_COLOR);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DARK_BG);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        JPanel inputPanel = createInputPanel();
        inputPanel.setPreferredSize(new Dimension(350, 0));
        
        JPanel centerPanel = createPlotPanel();
        
        JPanel historyPanel = createHistoryPanel();
        historyPanel.setPreferredSize(new Dimension(300, 0));
        
        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(historyPanel, BorderLayout.EAST);
        
        return mainPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = createStyledPanel("Plot Parameters", 300);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Genre:"), gbc);
        
        String[] genres = {"Action", "Drama", "Comedy", "Horror", "Thriller", 
                          "Sci-Fi", "Romance", "Mystery", "Adventure", "Fantasy"};
        genreCombo = createStyledComboBox(genres);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(genreCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Setting:"), gbc);
        
        settingField = createStyledTextField("e.g., Modern Tokyo, Medieval Castle");
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(settingField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Tone:"), gbc);
        
        String[] tones = {"Dark", "Light", "Mysterious", "Comedic", "Dramatic", 
                         "Suspenseful", "Romantic", "Epic", "Intense", "Whimsical"};
        toneCombo = createStyledComboBox(tones);
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(toneCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Characters:"), gbc);
        
        charactersField = createStyledTextField("e.g., A retired detective, A young hacker");
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(charactersField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Keywords:"), gbc);
        
        keywordsField = createStyledTextField("e.g., betrayal, redemption, time travel");
        gbc.gridx = 0; gbc.gridy = 9; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(keywordsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 10; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Cinematic Tone:"), gbc);
        
        JLabel cinematicToneLabel = new JLabel("Select genre and tone");
        cinematicToneLabel.setForeground(ACCENT_COLOR);
        cinematicToneLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = 11; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(cinematicToneLabel, gbc);
        
        genreCombo.addActionListener(e -> updateCinematicTone(cinematicToneLabel));
        toneCombo.addActionListener(e -> updateCinematicTone(cinematicToneLabel));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBackground(CARD_BG);
        
        generateButton = createStyledButton("🎭 Generate Plot");
        generateButton.addActionListener(e -> generatePlot());
        saveButton = createStyledButton("💾 Save Plot");
        saveButton.addActionListener(e -> savePlot());
        regenerateButton = createStyledButton("🔄 Regenerate");
        regenerateButton.addActionListener(e -> regeneratePlot());
        regenerateButton.setEnabled(false);
        saveButton.setEnabled(false);
        
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(regenerateButton);
        
        gbc.gridx = 0; gbc.gridy = 12; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private void updateCinematicTone(JLabel cinematicToneLabel) {
        String genre = (String) genreCombo.getSelectedItem();
        String tone = (String) toneCombo.getSelectedItem();
        String cinematicTone = getCinematicTone(genre, tone);
        cinematicToneLabel.setText(cinematicTone);
    }
    
    private String getCinematicTone(String genre, String tone) {
        Map<String, String> genreMap = CINEMATIC_TONES.get(genre);
        if (genreMap != null) {
            String cinematicTone = genreMap.get(tone);
            if (cinematicTone != null) {
                return cinematicTone;
            }
        }
        return genre + " " + tone;
    }
    
    private JPanel createPlotPanel() {
        JPanel panel = createStyledPanel("Generated Plot", 0);
        panel.setLayout(new BorderLayout(10, 10));
        
        plotTextArea = new JTextArea();
        plotTextArea.setBackground(DARK_BG);
        plotTextArea.setForeground(TEXT_COLOR);
        plotTextArea.setFont(new Font("Georgia", Font.PLAIN, 14));
        plotTextArea.setLineWrap(true);
        plotTextArea.setWrapStyleWord(true);
        plotTextArea.setEditable(false);
        plotTextArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        plotTextArea.setText("Welcome to CineForge!\n\n" +
                           "Enter your plot parameters on the left and click 'Generate Plot' " +
                           "to create compelling movie stories powered by AI.\n\n" +
                           "Your generated plots will be saved when you click 'Save Plot' and can be " +
                           "accessed from the history panel on the right.");
        
        JScrollPane scrollPane = new JScrollPane(plotTextArea);
        scrollPane.setBorder(new LineBorder(ACCENT_COLOR.darker(), 1));
        scrollPane.getViewport().setBackground(DARK_BG);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        exportButton = createStyledButton("💾 Export to TXT");
        exportButton.addActionListener(e -> exportPlot());
        exportButton.setEnabled(false);
        
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportPanel.setBackground(CARD_BG);
        exportPanel.add(exportButton);
        
        panel.add(exportPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createHistoryPanel() {
        JPanel panel = createStyledPanel("Plot History", 280);
        panel.setLayout(new BorderLayout(5, 5));
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sortPanel.setBackground(CARD_BG);
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(TEXT_COLOR);
        sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        String[] sortOptions = {
            "Latest First", "Oldest First", "Genre A-Z", "Genre Z-A", 
            "Setting A-Z", "Setting Z-A"
        };
        sortCombo = new JComboBox<>(sortOptions);
        sortCombo.setBackground(DARK_BG);
        sortCombo.setForeground(TEXT_COLOR);
        sortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sortCombo.addActionListener(e -> {
            updateSortOrder();
            loadPlotHistory();
        });
        
        sortPanel.add(sortLabel);
        sortPanel.add(sortCombo);
        
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyList.setBackground(DARK_BG);
        historyList.setForeground(TEXT_COLOR);
        historyList.setFont(MAIN_FONT);
        historyList.setSelectionBackground(ACCENT_COLOR);
        historyList.setSelectionForeground(Color.WHITE);
        historyList.setCellRenderer(new PlotListCellRenderer());
        
        historyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        historyList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Plot selectedPlot = historyList.getSelectedValue();
                    if (selectedPlot != null) {
                        loadPlotIntoEditor(selectedPlot);
                    }
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = historyList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        historyList.setSelectedIndex(index);
                        showContextMenu(e.getX(), e.getY());
                    }
                }
            }
        });
        
        historyList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                deleteButton.setEnabled(historyList.getSelectedIndices().length > 0);
            }
        });
        
        JScrollPane historyScroll = new JScrollPane(historyList);
        historyScroll.setBorder(new LineBorder(ACCENT_COLOR.darker(), 1));
        historyScroll.getViewport().setBackground(DARK_BG);
        
        panel.add(sortPanel, BorderLayout.NORTH);
        panel.add(historyScroll, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(CARD_BG);
        
        JLabel historyLabel = new JLabel("<html><center>Double-click to load<br>Right-click for options<br>Ctrl/Cmd+Click for multiple selection</center></html>");
        historyLabel.setForeground(TEXT_COLOR.darker());
        historyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        historyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        deleteButton = createStyledButton("🗑️ Delete Selected");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deleteButton.setBackground(new Color(220, 53, 69)); 
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedPlots());
        
        bottomPanel.add(historyLabel, BorderLayout.CENTER);
        bottomPanel.add(deleteButton, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(DARK_BG);
        statusPanel.setBorder(new EmptyBorder(5, 20, 10, 20));
        
        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setForeground(TEXT_COLOR);
        statusLabel.setFont(MAIN_FONT);
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        return statusPanel;
    }
    
    private JPanel createStyledPanel(String title, int preferredWidth) {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR.darker(), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        if (preferredWidth > 0) {
            panel.setPreferredSize(new Dimension(preferredWidth, 0));
        }
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_COLOR);
        label.setFont(MAIN_FONT);
        return label;
    }
    
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setBackground(DARK_BG);
        field.setForeground(TEXT_COLOR);
        field.setFont(MAIN_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(ACCENT_COLOR.darker(), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setToolTipText(placeholder);
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(DARK_BG);
        combo.setForeground(TEXT_COLOR);
        combo.setFont(MAIN_FONT);
        return combo;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(MAIN_FONT);
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
        });
        
        return button;
    }
    
    private void generatePlot() {
        String genre = (String) genreCombo.getSelectedItem();
        String setting = settingField.getText().trim();
        String tone = (String) toneCombo.getSelectedItem();
        String characters = charactersField.getText().trim();
        String keywords = keywordsField.getText().trim();
        
        if (setting.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a setting for your movie plot.", 
                "Missing Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cinematicTone = getCinematicTone(genre, tone);
        
        generateButton.setEnabled(false);
        plotTextArea.setText("🎬 Generating your movie plot...\n\nPlease wait while our AI crafts a compelling story based on your parameters.");
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return aiClient.generatePlot(genre, setting, tone, cinematicTone, characters, keywords);
            }
            
            @Override
            protected void done() {
                try {
                    String generatedPlot = get();
                    plotTextArea.setText(generatedPlot);
                    
                    currentPlot = new Plot(genre, setting, tone, characters, keywords, generatedPlot);
                    
                    regenerateButton.setEnabled(true);
                    exportButton.setEnabled(true);
                    saveButton.setEnabled(true); 
                } catch (Exception e) {
                    plotTextArea.setText("Error generating plot: " + e.getMessage() + 
                                       "\n\nPlease check your internet connection and try again.");
                    e.printStackTrace();
                } finally {
                    generateButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void savePlot() {
        if (currentPlot == null || currentPlot.getPlotText() == null || currentPlot.getPlotText().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No plot to save. Please generate a plot first.",
                "Nothing to Save",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        saveButton.setEnabled(false);
        plotTextArea.setText("💾 Saving your plot to database...\n\nPlease wait while we save your story.");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return dbManager.savePlot(currentPlot);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        plotTextArea.setText(currentPlot.getPlotText());
                        JOptionPane.showMessageDialog(CineForgeApp.this,
                            "Plot saved successfully!",
                            "Saved",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        loadPlotHistory();
                    } else {
                        plotTextArea.setText("Error: Failed to save plot to database.");
                        JOptionPane.showMessageDialog(CineForgeApp.this,
                            "Failed to save plot. Please try again.",
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    plotTextArea.setText("Error saving plot: " + e.getMessage());
                    JOptionPane.showMessageDialog(CineForgeApp.this,
                        "Error saving plot: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    saveButton.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
    
    private void regeneratePlot() {
        if (currentPlot != null) {
            generateButton.setEnabled(false);
            regenerateButton.setEnabled(false);
            saveButton.setEnabled(false); 
            plotTextArea.setText("🔄 Regenerating plot with fresh creativity...\n\nCreating a new version of your story.");
            
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    String cinematicTone = getCinematicTone(currentPlot.getGenre(), currentPlot.getTone());
                    return aiClient.generatePlot(currentPlot.getGenre(), 
                                               currentPlot.getSetting(), 
                                               currentPlot.getTone(), 
                                               cinematicTone,
                                               currentPlot.getCharacters(),
                                               currentPlot.getKeywords());
                }
                
                @Override
                protected void done() {
                    try {
                        String newPlot = get();
                        plotTextArea.setText(newPlot);
                        
                        Plot newPlotObj = new Plot(
                            currentPlot.getGenre(),
                            currentPlot.getSetting(),
                            currentPlot.getTone(),
                            currentPlot.getCharacters(),
                            currentPlot.getKeywords(),
                            newPlot
                        );
                        newPlotObj.setGeneratedAt(new java.util.Date());
                        currentPlot = newPlotObj;
                        saveButton.setEnabled(true);                        
                    } catch (Exception e) {
                        plotTextArea.setText("Error regenerating plot: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        generateButton.setEnabled(true);
                        regenerateButton.setEnabled(true);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private void loadPlotIntoEditor(Plot plot) {
        genreCombo.setSelectedItem(plot.getGenre());
        settingField.setText(plot.getSetting());
        toneCombo.setSelectedItem(plot.getTone());
        charactersField.setText(plot.getCharacters());
        keywordsField.setText(plot.getKeywords());
        plotTextArea.setText(plot.getPlotText());
        
        currentPlot = plot;
        regenerateButton.setEnabled(true);
        exportButton.setEnabled(true);
        
        saveButton.setEnabled(plot.getId() == 0);
    }
    
    private void exportPlot() {
        if (currentPlot != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Plot to TXT");
            fileChooser.setSelectedFile(new java.io.File(
                "CineForge_Plot_" + currentPlot.getGenre() + "_" + 
                System.currentTimeMillis() + ".txt"));
            
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter writer = new java.io.PrintWriter(
                    new java.io.FileWriter(fileChooser.getSelectedFile()))) {
                    
                    writer.println("=== CineForge Generated Plot ===");
                    writer.println("Genre: " + currentPlot.getGenre());
                    writer.println("Setting: " + currentPlot.getSetting());
                    writer.println("Tone: " + currentPlot.getTone());
                    writer.println("Cinematic Tone: " + getCinematicTone(currentPlot.getGenre(), currentPlot.getTone()));
                    writer.println("Characters: " + currentPlot.getCharacters());
                    writer.println("Keywords: " + currentPlot.getKeywords());
                    writer.println("Generated: " + currentPlot.getGeneratedAt());
                    writer.println();
                    writer.println("=== PLOT ===");
                    writer.println(currentPlot.getPlotText());
                    
                    JOptionPane.showMessageDialog(this, 
                        "Plot exported successfully!", 
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error exporting plot: " + e.getMessage(), 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void updateSortOrder() {
        int selectedIndex = sortCombo.getSelectedIndex();
        switch (selectedIndex) {
            case 0: currentSortOrder = "date_desc"; break;
            case 1: currentSortOrder = "date_asc"; break;
            case 2: currentSortOrder = "genre_asc"; break;
            case 3: currentSortOrder = "genre_desc"; break;
            case 4: currentSortOrder = "setting_asc"; break;
            case 5: currentSortOrder = "setting_desc"; break;
            default: currentSortOrder = "date_desc";
        }
    }
    
    private void showContextMenu(int x, int y) {
        int[] selectedIndices = historyList.getSelectedIndices();
        if (selectedIndices.length == 0) return;
        
        JPopupMenu contextMenu = new JPopupMenu();
        contextMenu.setBackground(CARD_BG);
        contextMenu.setBorder(new LineBorder(ACCENT_COLOR.darker(), 1));
        
        if (selectedIndices.length == 1) {
            Plot selectedPlot = historyList.getSelectedValue();
            JMenuItem loadItem = new JMenuItem("📖 Load Plot");
            loadItem.setBackground(CARD_BG);
            loadItem.setForeground(TEXT_COLOR);
            loadItem.addActionListener(e -> loadPlotIntoEditor(selectedPlot));
            contextMenu.add(loadItem);
            contextMenu.addSeparator();
        }
        
        JMenuItem exportItem = new JMenuItem("💾 Export Plot" + (selectedIndices.length > 1 ? "s" : ""));
        exportItem.setBackground(CARD_BG);
        exportItem.setForeground(TEXT_COLOR);
        exportItem.addActionListener(e -> exportSelectedPlots());
        contextMenu.add(exportItem);
        
        JMenuItem deleteItem = new JMenuItem("🗑️ Delete Plot" + (selectedIndices.length > 1 ? "s" : ""));
        deleteItem.setBackground(CARD_BG);
        deleteItem.setForeground(new Color(220, 53, 69));
        deleteItem.addActionListener(e -> deleteSelectedPlots());
        contextMenu.add(deleteItem);
        
        contextMenu.show(historyList, x, y);
    }
    
    private void exportSelectedPlots() {
        int[] selectedIndices = historyList.getSelectedIndices();
        if (selectedIndices.length == 0) return;
        
        List<Plot> selectedPlots = new ArrayList<>();
        for (int index : selectedIndices) {
            selectedPlots.add(historyModel.get(index));
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Plot" + (selectedPlots.size() > 1 ? "s" : "") + " to TXT");
        
        if (selectedPlots.size() == 1) {
            fileChooser.setSelectedFile(new java.io.File(
                "CineForge_Plot_" + selectedPlots.get(0).getGenre() + "_" + 
                System.currentTimeMillis() + ".txt"));
        } else {
            fileChooser.setSelectedFile(new java.io.File(
                "CineForge_Plots_" + System.currentTimeMillis() + ".txt"));
        }
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(
                new java.io.FileWriter(fileChooser.getSelectedFile()))) {
                
                for (int i = 0; i < selectedPlots.size(); i++) {
                    Plot plot = selectedPlots.get(i);
                    
                    if (selectedPlots.size() > 1) {
                        writer.println("=== PLOT " + (i + 1) + " OF " + selectedPlots.size() + " ===");
                    }
                    
                    writer.println("Genre: " + plot.getGenre());
                    writer.println("Setting: " + plot.getSetting());
                    writer.println("Tone: " + plot.getTone());
                    writer.println("Cinematic Tone: " + getCinematicTone(plot.getGenre(), plot.getTone()));
                    writer.println("Characters: " + plot.getCharacters());
                    writer.println("Keywords: " + plot.getKeywords());
                    writer.println("Generated: " + plot.getGeneratedAt());
                    writer.println();
                    writer.println("=== PLOT ===");
                    writer.println(plot.getPlotText());
                    
                    if (i < selectedPlots.size() - 1) {
                        writer.println();
                        writer.println("=====================================");
                        writer.println();
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    selectedPlots.size() + " plot(s) exported successfully!", 
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting plots: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteSelectedPlots() {
        int[] selectedIndices = historyList.getSelectedIndices();
        if (selectedIndices.length == 0) return;
        
        // Get selected plots
        List<Plot> selectedPlots = new ArrayList<>();
        List<Long> selectedIds = new ArrayList<>();
        for (int index : selectedIndices) {
            Plot plot = historyModel.get(index);
            selectedPlots.add(plot);
            selectedIds.add(plot.getId());
        }
        
        String message;
        if (selectedPlots.size() == 1) {
            Plot plot = selectedPlots.get(0);
            message = "Are you sure you want to delete this plot?\n\n" +
                     "Genre: " + plot.getGenre() + "\n" +
                     "Setting: " + (plot.getSetting().length() > 50 ? 
                         plot.getSetting().substring(0, 50) + "..." : 
                         plot.getSetting()) + "\n\n" +
                     "This action cannot be undone.";
        } else {
            message = "Are you sure you want to delete " + selectedPlots.size() + " plots?\n\n" +
                     "This action cannot be undone.";
        }
        
        int result = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return dbManager.deletePlots(selectedIds);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        
                        for (Plot plot : selectedPlots) {
                            if (currentPlot != null && currentPlot.getId() == plot.getId()) {
                                currentPlot = null;
                                plotTextArea.setText("Plot deleted. Generate a new plot or select one from history.");
                                regenerateButton.setEnabled(false);
                                exportButton.setEnabled(false);
                            }
                        }
                        
                        loadPlotHistory();
                        
                        if (success) {
                            JOptionPane.showMessageDialog(
                                CineForgeApp.this,
                                selectedPlots.size() + " plot(s) deleted successfully.",
                                "Delete Complete",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                CineForgeApp.this,
                                "Failed to delete plots. Please try again.",
                                "Delete Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(
                            CineForgeApp.this,
                            "Error deleting plots: " + e.getMessage(),
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        e.printStackTrace();
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    private void loadPlotHistory() {
        SwingWorker<List<Plot>, Void> worker = new SwingWorker<List<Plot>, Void>() {
            @Override
            protected List<Plot> doInBackground() throws Exception {
                return dbManager.getAllPlots(currentSortOrder);
            }
            
            @Override
            protected void done() {
                try {
                    List<Plot> plots = get();
                    historyModel.clear();
                    for (Plot plot : plots) {
                        historyModel.addElement(plot);
                    }


                    deleteButton.setEnabled(false);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private class PlotListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Plot) {
                Plot plot = (Plot) value;
                setText(String.format("<html><b>%s</b><br><small>%s</small></html>", 
                    plot.getGenre(), 
                    plot.getSetting().length() > 30 ? 
                        plot.getSetting().substring(0, 30) + "..." : 
                        plot.getSetting()));
            }
            
            if (isSelected) {
                setBackground(ACCENT_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(DARK_BG);
                setForeground(TEXT_COLOR);
            }
            
            setBorder(new EmptyBorder(8, 8, 8, 8));
            
            return this;
        }
    }
}