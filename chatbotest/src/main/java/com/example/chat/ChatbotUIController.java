package com.example.chat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.binding.Bindings;

public class ChatbotUIController {
    @FXML private VBox mainContainer;
    @FXML private VBox chatContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField inputField;
    @FXML private Button sendButton;
    @FXML private Button attachButton;
    @FXML private Button settingsButton;
    
    private GeminiClient geminiClient;
    private static final Color USER_BUBBLE_COLOR = Color.web("#10b981");
    private static final Color AI_BUBBLE_COLOR = Color.web("#27272a");
    private static final Color TEXT_COLOR = Color.web("#e2e8f0");
    private static final Color HEADING_COLOR = Color.web("#10b981");
    private static final Color CODE_BLOCK_BG = Color.web("#1e1e1e");
    private static final Color INLINE_CODE_BG = Color.web("#2d2d2d");
    
    // Font definitions
    private static final Font NORMAL_FONT = Font.font("Segoe UI", FontWeight.NORMAL, 14);
    private static final Font BOLD_FONT = Font.font("Segoe UI", FontWeight.BOLD, 14);
    private static final Font HEADING_FONT = Font.font("Segoe UI", FontWeight.BOLD, 18);
    private static final Font CODE_FONT = Font.font("JetBrains Mono", 13);
    private static final Font TIME_FONT = Font.font("Segoe UI", FontWeight.LIGHT, 11);
    
    // Size constraints
    private static final double BUBBLE_MIN_WIDTH = 100;
    private static final double BUBBLE_MAX_WIDTH = 600;
    private static final double IMAGE_MAX_WIDTH = 300;
    private static final double IMAGE_MAX_HEIGHT = 300;
    
    public void initialize() {
        geminiClient = new GeminiClient();
        addInitialMessage();
        
        // Set up scroll pane to auto-scroll to bottom
        chatContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });
        
        // Handle Enter key in input field
        inputField.setOnAction(event -> handleSend());
        
        // Update message bubbles when window is resized
        chatScrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            for (javafx.scene.Node node : chatContainer.getChildren()) {
                if (node instanceof VBox bubbleContainer) {
                    bubbleContainer.maxWidthProperty().bind(chatScrollPane.widthProperty().multiply(0.85));
                    for (javafx.scene.Node child : bubbleContainer.getChildren()) {
                        if (child instanceof TextFlow textFlow) {
                            textFlow.setMaxWidth(newValue.doubleValue() * 0.7);
                        }
                    }
                }
            }
        });
    }
    
    @FXML
    private void handleSend() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;
        
        addMessage("You", message);
        inputField.clear();
        
        // Get AI response in a separate thread
        new Thread(() -> {
            try {
                String response = geminiClient.chat(message);
                javafx.application.Platform.runLater(() -> addMessage("AI", response));
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> 
                    addMessage("System", "Error getting response from AI: " + e.getMessage()));
            }
        }).start();
    }
    
    @FXML
    private void handleAttachment() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File to Attach");
        
        // Add file filters
        FileChooser.ExtensionFilter imageFilter = 
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp");
        FileChooser.ExtensionFilter textFilter = 
            new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.md", "*.rtf");
        FileChooser.ExtensionFilter pdfFilter = 
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
        FileChooser.ExtensionFilter docFilter = 
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx");
        
        fileChooser.getExtensionFilters().addAll(imageFilter, textFilter, pdfFilter, docFilter);
        
        // Show file chooser dialog
        File file = fileChooser.showOpenDialog(mainContainer.getScene().getWindow());
        
        if (file != null) {
            try {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || 
                    fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || 
                    fileName.endsWith(".bmp")) {
                    handleImageFile(file);
                } else if (fileName.endsWith(".txt") || fileName.endsWith(".md") || 
                         fileName.endsWith(".rtf")) {
                    handleTextFile(file);
                } else if (fileName.endsWith(".pdf") || fileName.endsWith(".doc") || 
                         fileName.endsWith(".docx")) {
                    addMessage("System", "Sorry, " + file.getName() + " format is not yet supported for content analysis.");
                }
            } catch (Exception e) {
                addMessage("System", "Error processing file: " + e.getMessage());
            }
        }
    }
    
    private void handleImageFile(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            
            // Calculate image dimensions
            double width = image.getWidth();
            double height = image.getHeight();
            
            if (width > IMAGE_MAX_WIDTH) {
                double ratio = IMAGE_MAX_WIDTH / width;
                width = IMAGE_MAX_WIDTH;
                height = height * ratio;
            }
            
            if (height > IMAGE_MAX_HEIGHT) {
                double ratio = IMAGE_MAX_HEIGHT / height;
                height = IMAGE_MAX_HEIGHT;
                width = width * ratio;
            }
            
            // Set calculated dimensions
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);
            
            // Create text flow with image
            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().addAll(
                new Text("📎 " + file.getName() + "\n\n"),
                imageView
            );
            textFlow.setMinWidth(BUBBLE_MIN_WIDTH);
            textFlow.setMaxWidth(BUBBLE_MAX_WIDTH);
            textFlow.setPadding(new Insets(10));
            
            addCustomMessage("You", textFlow);
            
            // Get AI response
            new Thread(() -> {
                try {
                    String response = geminiClient.chat("I've received an image file named " + 
                        file.getName() + ". Please acknowledge the image receipt and ask if you can help analyze it.");
                    javafx.application.Platform.runLater(() -> addMessage("AI", response));
                } catch (IOException e) {
                    javafx.application.Platform.runLater(() -> 
                        addMessage("System", "Error processing image: " + e.getMessage()));
                }
            }).start();
        } catch (Exception e) {
            addMessage("System", "Error loading image: " + e.getMessage());
        }
    }
    
    private void handleTextFile(File file) {
        try {
            String content = Files.readString(file.toPath());
            addMessage("You", "📎 " + file.getName() + "\n\n" + content);
            
            // Get AI response
            new Thread(() -> {
                try {
                    String response = geminiClient.chat("Here's the content of the file " + 
                        file.getName() + ": \n\n" + content + 
                        "\n\nPlease analyze this content and provide your insights.");
                    javafx.application.Platform.runLater(() -> addMessage("AI", response));
                } catch (IOException e) {
                    javafx.application.Platform.runLater(() -> 
                        addMessage("System", "Error processing file: " + e.getMessage()));
                }
            }).start();
        } catch (IOException e) {
            addMessage("System", "Error reading file: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSettings() {
        // TODO: Implement settings dialog
        System.out.println("Settings not implemented yet");
    }
    
    @FXML
    private void handleQuickAction(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        String prompt = getButtonPrompt(buttonText);
        inputField.setText(prompt);
        inputField.requestFocus();
        inputField.positionCaret(inputField.getText().length());
    }
    
    private void addInitialMessage() {
        addMessage("AI", "Hello! I'm your AI study assistant powered by ChatGPT. How can I help you today?");
    }
    
    private void addMessage(String sender, String message) {
        if (sender.equals("AI")) {
            addFormattedMessage(sender, message);
        } else {
            // Keep the original simple formatting for user messages
            addFormattedMessage(sender, message);
        }
    }
    
    private String getButtonPrompt(String buttonText) {
        return switch (buttonText) {
            case "Explain this concept" -> "Could you explain this topic in detail: ";
            case "Generate study questions" -> "Generate practice questions about: ";
            case "Summarize my notes" -> "Please summarize this text: ";
            case "Help me focus" -> "Give me focus techniques for: ";
            default -> "";
        };
    }
    
    private void addCustomMessage(String sender, TextFlow content) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // Create time label
        Text timeText = new Text(time);
        timeText.setFont(TIME_FONT);
        timeText.setFill(Color.GRAY);
        
        // Set content constraints
        content.setMinWidth(BUBBLE_MIN_WIDTH);
        content.setMaxWidth(BUBBLE_MAX_WIDTH);
        
        // Create bubble container
        VBox bubbleContainer = new VBox(5);
        bubbleContainer.setPadding(new Insets(5));
        bubbleContainer.setAlignment(sender.equals("You") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        // Add time and content to bubble
        bubbleContainer.getChildren().addAll(timeText, content);
        
        // Style the bubble
        content.setStyle("-fx-background-color: " + 
            (sender.equals("You") ? USER_BUBBLE_COLOR.toString().replace("0x", "#") : 
                                  AI_BUBBLE_COLOR.toString().replace("0x", "#")) + 
            "; -fx-background-radius: 15;");
        
        // Add responsive width binding
        content.prefWidthProperty().bind(
            Bindings.createDoubleBinding(
                () -> Math.min(
                    Math.max(
                        BUBBLE_MIN_WIDTH,
                        BUBBLE_MAX_WIDTH
                    ),
                    chatScrollPane.getWidth() * 0.75
                ),
                chatScrollPane.widthProperty()
            )
        );
        
        // Add to chat container
        chatContainer.getChildren().add(bubbleContainer);
    }
    
    private void addFormattedMessage(String sender, String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        
        // Create time label with HBox for proper alignment
        Text timeText = new Text(time);
        timeText.setFont(TIME_FONT);
        timeText.setFill(Color.GRAY);
        HBox timeBox = new HBox(timeText);
        timeBox.setAlignment(sender.equals("You") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        // Create text flow for message
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(12));
        textFlow.setMinWidth(BUBBLE_MIN_WIDTH);
        textFlow.setMaxWidth(BUBBLE_MAX_WIDTH);
        
        // Format message with Markdown-style parsing
        formatMessage(message, textFlow);
        
        // Create bubble container
        VBox bubbleContainer = new VBox(3);
        bubbleContainer.setPadding(new Insets(2));
        bubbleContainer.setAlignment(sender.equals("You") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        
        // Add time and message to bubble
        bubbleContainer.getChildren().addAll(timeBox, textFlow);
        
        // Style the bubble with modern look
        String bubbleStyle = String.format("""
            -fx-background-color: %s;
            -fx-background-radius: 18;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);
            -fx-background-insets: 0;
            """,
            sender.equals("You") ? USER_BUBBLE_COLOR.toString().replace("0x", "#") : 
                                 AI_BUBBLE_COLOR.toString().replace("0x", "#")
        );
        textFlow.setStyle(bubbleStyle);
        
        // Add responsive width binding
        textFlow.prefWidthProperty().bind(
            Bindings.createDoubleBinding(
                () -> Math.min(
                    Math.max(
                        BUBBLE_MIN_WIDTH,
                        BUBBLE_MAX_WIDTH
                    ),
                    chatScrollPane.getWidth() * 0.75
                ),
                chatScrollPane.widthProperty()
            )
        );
        
        // Add to chat container
        chatContainer.getChildren().add(bubbleContainer);
    }
    
    private void formatMessage(String message, TextFlow textFlow) {
        String[] lines = message.split("\n");
        boolean inCodeBlock = false;
        StringBuilder codeBlock = new StringBuilder();
        
        for (String line : lines) {
            if (line.startsWith("```")) {
                if (inCodeBlock) {
                    // End code block
                    addCodeBlock(textFlow, codeBlock.toString());
                    codeBlock.setLength(0);
                    inCodeBlock = false;
                } else {
                    // Start code block
                    inCodeBlock = true;
                }
                continue;
            }
            
            if (inCodeBlock) {
                codeBlock.append(line).append("\n");
                continue;
            }
            
            // Process regular line
            if (line.isEmpty()) {
                textFlow.getChildren().add(new Text("\n"));
                continue;
            }
            
            // Handle headings
            if (line.startsWith("# ")) {
                Text heading = new Text(line.substring(2) + "\n");
                heading.setFont(HEADING_FONT);
                heading.setFill(HEADING_COLOR);
                textFlow.getChildren().add(heading);
                continue;
            }
            
            // Handle inline formatting
            processInlineFormatting(line, textFlow);
            textFlow.getChildren().add(new Text("\n"));
        }
    }
    
    private void processInlineFormatting(String line, TextFlow textFlow) {
        int pos = 0;
        StringBuilder currentText = new StringBuilder();
        
        while (pos < line.length()) {
            if (line.startsWith("**", pos)) {
                // Bold text
                if (currentText.length() > 0) {
                    addNormalText(textFlow, currentText.toString());
                    currentText.setLength(0);
                }
                int endPos = line.indexOf("**", pos + 2);
                if (endPos != -1) {
                    Text boldText = new Text(line.substring(pos + 2, endPos));
                    boldText.setFont(BOLD_FONT);
                    boldText.setFill(TEXT_COLOR);
                    textFlow.getChildren().add(boldText);
                    pos = endPos + 2;
                    continue;
                }
            }
            
            if (line.startsWith("`", pos)) {
                // Inline code
                if (currentText.length() > 0) {
                    addNormalText(textFlow, currentText.toString());
                    currentText.setLength(0);
                }
                int endPos = line.indexOf("`", pos + 1);
                if (endPos != -1) {
                    Text codeText = new Text(line.substring(pos + 1, endPos));
                    codeText.setFont(CODE_FONT);
                    codeText.setFill(TEXT_COLOR);
                    
                    // Create inline code container
                    HBox codeContainer = new HBox(codeText);
                    codeContainer.setStyle("""
                        -fx-background-color: #2d2d2d;
                        -fx-background-radius: 4;
                        -fx-padding: 2 6;
                        -fx-spacing: 0;
                        """);
                    
                    textFlow.getChildren().add(codeContainer);
                    pos = endPos + 1;
                    continue;
                }
            }
            
            currentText.append(line.charAt(pos));
            pos++;
        }
        
        if (currentText.length() > 0) {
            addNormalText(textFlow, currentText.toString());
        }
    }
    
    private void addCodeBlock(TextFlow parentFlow, String code) {
        VBox codeContainer = new VBox();
        Text codeText = new Text(code);
        codeText.setFont(CODE_FONT);
        codeText.setFill(TEXT_COLOR);
        
        codeContainer.getChildren().add(codeText);
        codeContainer.setStyle("""
            -fx-background-color: #1e1e1e;
            -fx-background-radius: 8;
            -fx-padding: 12;
            -fx-spacing: 0;
            """);
        
        parentFlow.getChildren().addAll(new Text("\n"), codeContainer, new Text("\n"));
    }
    
    private void addNormalText(TextFlow flow, String text) {
        Text normalText = new Text(text);
        normalText.setFont(NORMAL_FONT);
        normalText.setFill(TEXT_COLOR);
        flow.getChildren().add(normalText);
    }
} 