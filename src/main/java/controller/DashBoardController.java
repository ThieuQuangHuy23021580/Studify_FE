package controller;

import backend.controllers.SessionController;
import backend.controllers.StudySessionController;
import backend.models.Background;
import backend.controllers.BackgroundController;
import backend.controllers.QuoteController;
import backend.models.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DashBoardController {

    private String FOCUS_TIME;
    private String BREAK_TIME;
    private int MIN_FOCUS_MINUTES = 10;
    private int MAX_FOCUS_MINUTES = 120;
    private int MIN_BREAK_MINUTES = 5;
    private int MAX_BREAK_MINUTES = 30;
    boolean isFocus = true;
    boolean isPause = false;

    private String soundPath = getClass().getResource("/PICTURES/clock-alarm.mp3").toString();
    private AudioClip alarmSound;

    @FXML
    private Button addGoalBtn;

    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Label breakTimeFixLabel;

    @FXML
    private Button changeBackgroundBtn;

    @FXML
    private Button changeQuoteBtn;

    @FXML
    private Button changeSoundBtn;

    @FXML
    private Button closeBackgroundBtn;

    @FXML
    private Button closePomodoroFixBtn;

    @FXML
    private Button closePomodoroTimerBtn;

    @FXML
    private Button closeQuoteAnchorPaneBtn;

    @FXML
    private Button closeSessionGoalsBtn;

    @FXML
    private Button closeSoundAnchorPaneBtn;

    @FXML
    private Label focusTimeFixLabel;

    @FXML
    private Label focusTimeLabel;

    @FXML
    private Button fullScreenBtn;

    @FXML
    private Label miniFocusTimeLabel;

    @FXML
    private RadioButton loopAutomaticallyRadioBtn;

    @FXML
    private Button minusBreakTimeBtn;

    @FXML
    private Button minusFocusTimeBtn;

    @FXML
    private Button plusFocusTimeBtn;

    @FXML
    private Button plusTimeBreakBtn;

    @FXML
    private AnchorPane pomodoroAnchorPane;

    @FXML
    private AnchorPane pomodoroFixAnchorPane;

    @FXML
    private Button pomodoroFixBtn;

    @FXML
    private Button pomodoroTimerBtn;

    @FXML
    private AnchorPane quoteAnchorPane;

    @FXML
    private Text quoteLabel;

    @FXML
    private Button hideQuoteBtn;

    @FXML
    private Button shuffleQuoteBtn;

    @FXML
    private Button runPomodoroTimerBtn;

    @FXML
    private Button seeAllTaskBtn;

    @FXML
    private AnchorPane sessionGoalAnchorPane;

    @FXML
    private TextField sessionGoalTextField;

    @FXML
    private Button sessionGoalsBtn;

    @FXML
    private AnchorPane soundAnchorPane;

    @FXML
    private Button startTimerBtn;

    @FXML
    private AnchorPane taskAnchorPane;

    @FXML
    private Button pausePomodoroTimerBtn;

    @FXML
    private FlowPane taskListFlowPane;

    @FXML
    private Label openTaskLabel;

    @FXML
    private Label completedTaskLabel;

    @FXML
    private Label miniTaskLabel;

    @FXML
    private StackPane rootStackPane;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Slider lofiSlider;

    @FXML
    private Slider fireplaceSlider;

    @FXML
    private Slider naturalSlider;

    @FXML
    private Slider rainSlider;

    @FXML
    private Slider binauralSlider;

    @FXML
    private Slider pianoSlider;

    @FXML
    private TextField urlTextField;

    @FXML
    private Button animeCateBtn;

    @FXML
    private Button cafeCateBtn;

    @FXML
    private Button libraryCateBtn;

    @FXML
    private Button naturalCateBtn;

    @FXML
    private Button deskCateBtn;

    @FXML
    private Button cityCateBtn;


    @FXML private ImageView image1;
    @FXML private ImageView image2;
    @FXML private ImageView image3;
    @FXML private ImageView image4;
    @FXML private ImageView image5;
    @FXML private ImageView image6;

    private List<AnchorPane> showStudyTool, showAllTool;

    private PomodoroTimerController focusTimeController, minifocusTimeController;

    private SoundController soundController;

    private Boolean isShowQuote;

    private VideoPlayerController currentVideoController = null;

    private Stage currentVideoStage = null;

    private List<ImageView> backgroundImageViews;

    private QuoteController quoteController;
    private BackgroundController backgroundController;
    private StudySessionController studySessionController;
    private backend.controllers.TaskController taskController;
    private User currentUser;

    private static final String[] YOUTUBE_URL_PATTERNS = {
            "https://(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_\\-]+)",
            "https://(?:www\\.)?youtube\\.com/embed/([a-zA-Z0-9_\\-]+)",
            "https://youtu\\.be/([a-zA-Z0-9_\\-]+)",
            "https://(?:www\\.)?youtube\\.com/v/([a-zA-Z0-9_\\-]+)",
    };


    @FXML
    public void initialize() throws SQLException {
        studySessionController = new StudySessionController();
        quoteController = new QuoteController();
        taskController = new backend.controllers.TaskController();
        isShowQuote = true;

        //Background Settings:
        if (backgroundImage != null && rootStackPane != null) {
            backgroundImage.fitWidthProperty().bind(rootStackPane.widthProperty());
            backgroundImage.fitHeightProperty().bind(rootStackPane.heightProperty());
        } else {
            System.err.println("Lỗi binding: rootStackPane hoặc backgroundImage từ FXML là null.");
        }
        backgroundController = new BackgroundController();
        backgroundImageViews = List.of(image1, image2, image3, image4, image5, image6);

        //Media Settings:
        Platform.runLater(() -> {
            Stage stage = (Stage) rootStackPane.getScene().getWindow();
            if (stage != null) {
                stage.setOnCloseRequest(event -> {
                    System.out.println("Cửa sổ đóng, giải phóng âm thanh...");
                    if (soundController != null) {
                        soundController.disposeAll();
                        this.cleanupOnExit();
                    }
                });
            }
        });

        soundController = new SoundController();
        soundController.loadSound("lofi","/PICTURES/lofi-beat.mp3");
        soundController.loadSound("fireplace","/PICTURES/fireplace-sound.mp3");
        soundController.loadSound("natural","/PICTURES/natural-sound.mp3");
        soundController.loadSound("rain","/PICTURES/rain-sound.mp3");
        soundController.loadSound("binaural","/PICTURES/binaural-beat.mp3");
        soundController.loadSound("piano","/PICTURES/piano-sound.mp3");

        setupSliderListener(lofiSlider, "lofi");
        setupSliderListener(fireplaceSlider, "fireplace");
        setupSliderListener(naturalSlider, "natural");
        setupSliderListener(rainSlider, "rain");
        setupSliderListener(binauralSlider, "binaural");
        setupSliderListener(pianoSlider, "piano");

        //Pomodoro Clock Setting:
        FOCUS_TIME = focusTimeFixLabel.getText();
        BREAK_TIME = breakTimeFixLabel.getText();

        alarmSound = new AudioClip(soundPath);
        alarmSound.setCycleCount(AudioClip.INDEFINITE);

        focusTimeController = new PomodoroTimerController(focusTimeLabel, FOCUS_TIME, this::handleTimerFinish);
        minifocusTimeController = new PomodoroTimerController(miniFocusTimeLabel, FOCUS_TIME, null);

        //Pane Settings:
        showStudyTool = List.of(quoteAnchorPane, soundAnchorPane, backgroundAnchorPane);
        showAllTool = List.of(pomodoroAnchorPane, pomodoroFixAnchorPane, sessionGoalAnchorPane, taskAnchorPane,
                quoteAnchorPane, soundAnchorPane, backgroundAnchorPane);
        for (AnchorPane anchorPane : showAllTool) showNode(anchorPane, false);

    }

    public void initData(User user) {
        if(user != null){
           currentUser = user;
            System.out.println("User Dashboard:" + currentUser.getUserId());
            showUserBackground();
            setupBackgroundSelectionClick();
            loadBackgroundsByCategory("anime");
        }
        else System.out.println("Fail to load dashboard user");
    }

    private void loadBackgroundsByCategory(String category) {
        if (backgroundController == null || backgroundImageViews == null) {
            System.err.println("Error: BackgroundController or ImageView list not initialized.");
            return;
        }
        List<Background> backgrounds = backgroundController.getBackgroundsByCategory(category);
        for (int i = 0; i < backgroundImageViews.size(); i++) {
            ImageView imageView = backgroundImageViews.get(i);
            if (imageView != null) {
                if (i < backgrounds.size()) {
                    Background bg = backgrounds.get(i);
                    String imagePath = bg.getImagePath();
                    System.out.println("Attempting to load image from resource path: [" + imagePath + "]");
                    if (imagePath != null && !imagePath.trim().isEmpty()) {
                        try (InputStream imageStream = getClass().getResourceAsStream("/" + imagePath.trim())) {
                            Image image = new Image(imageStream);
                            imageView.setImage(image);
                            imageView.setUserData(bg);
                            imageView.setFitWidth(120);
                            imageView.setFitHeight(56);
                        } catch (Exception e) {
                            e.printStackTrace();
                            imageView.setImage(null);
                            imageView.setUserData(null);
                        }
                    }
                }
            }
        }
    }

    private void setupBackgroundSelectionClick() {
        if (backgroundImageViews == null) return;
        for (ImageView imageView : backgroundImageViews) {
            if (imageView != null) {
                imageView.setOnMouseClicked(event -> {
                    Object userData = imageView.getUserData();
                    if (userData instanceof Background) {
                        handleBackgroundSelection((Background) userData);
                    }
                });
                imageView.setOnMouseEntered(e -> imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(255,255,255,0.7), 10, 0.5, 0, 0);"));
                imageView.setOnMouseExited(e -> imageView.setStyle("-fx-effect: null;"));
                imageView.setPickOnBounds(true);
                imageView.setCursor(javafx.scene.Cursor.HAND);
            }
        }
    }

    private void handleBackgroundSelection(Background selectedBackground) {
        if (selectedBackground == null) return;
        boolean success = backgroundController.setUserBackground(currentUser.getUserId(), selectedBackground.getId());
        if(currentUser != null && success) currentUser.setBackgroundId(selectedBackground.getId());
        setBackgroundImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + selectedBackground.getImagePath()))));
    }

    public void showUserBackground() {
        String imagePath = backgroundController.getUserBackground(currentUser.getUserId()).getImagePath();
        if(backgroundController.getUserBackground(currentUser.getUserId()) != null){
            Platform.runLater(() ->{
                try(InputStream imageStream = getClass().getResourceAsStream("/"+imagePath.trim())){
                    if(imageStream != null){
                        Image image = new Image(imageStream);
                        setBackgroundImage(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void setBackgroundImage(Image backgroundImage) {
        if (backgroundImage == null) return;
        this.backgroundImage.setImage(backgroundImage);
    }

    @FXML
    public void animeCateBtnClicked() {
        loadBackgroundsByCategory("anime");
    }

    @FXML
    public void libraryCateBtnClicked() {
        loadBackgroundsByCategory("library");
    }

    @FXML
    public void naturalCateBtnClicked() {
        loadBackgroundsByCategory("nature");
    }

    @FXML
    public void animalsCateBtnClicked() {
        loadBackgroundsByCategory("animals");
    }

    @FXML
    public void deskCateBtnClicked() {
        loadBackgroundsByCategory("desk");
    }

    @FXML
    public void cafeCateBtnClicked() {
        loadBackgroundsByCategory("cafe");
    }

    @FXML
    public void cityCateBtnClicked() {
        loadBackgroundsByCategory("city");
    }

    private void showNode(Node node, boolean isShow) {
        node.setVisible(isShow);
        node.setManaged(isShow);
    }

    private void showAnchorPane(AnchorPane ap, List<AnchorPane> showStudyTool, boolean isShow) {
        for (AnchorPane anchorPane : showStudyTool) {
            showNode(anchorPane, false);
        }
        showNode(ap, true);
    }

    @FXML
    public void pomodoroTimerBtnClicked() {
        showNode(pomodoroAnchorPane, true);
    }

    @FXML
    public void sessionGoalsBtnClicked() {
        showNode(sessionGoalAnchorPane, true);
    }

    @FXML
    public void pomodoroFixBtnClicked() {
        showNode(pomodoroAnchorPane, false);
        showNode(pomodoroFixAnchorPane, true);
    }

    @FXML
    public void seeAllTaskBtnClicked() {
        showNode(taskAnchorPane, true);
    }

    @FXML
    public void changeBackgroundBtnClicked() {
        showAnchorPane(backgroundAnchorPane, showStudyTool, true);
    }

    @FXML
    public void changeSoundBtnClicked() {
        showAnchorPane(soundAnchorPane, showStudyTool, true);
    }

    @FXML
    public void changeQuoteBtnClicked() {
        showAnchorPane(quoteAnchorPane, showStudyTool, true);
    }

    @FXML
    public void closeSessionGoalsBtnClicked() {
        showNode(sessionGoalAnchorPane, false);
        showNode(taskAnchorPane, false);
    }

    @FXML
    public void closePomodoroTimerBtnClicked() {
        showNode(pomodoroAnchorPane, false);
    }

    @FXML
    public void closePomodoroFixBtnClicked() {
        showNode(pomodoroFixAnchorPane, false);
    }

    @FXML
    public void closeBackgroundBtnClicked() {
        showNode(backgroundAnchorPane, false);
    }

    @FXML
    public void closeSoundAnchorPaneBtnClicked() {
        showNode(soundAnchorPane, false);
    }

    @FXML
    public void closeQuoteAnchorPaneBtnClicked() {
        showNode(quoteAnchorPane, false);
    }

    @FXML
    public void fullScreenBtnClicked() {
        Node sourceNode = fullScreenBtn;
        Scene scene = sourceNode.getScene();
        Window window = scene.getWindow();
        if (window instanceof Stage) {
            Stage stage = (Stage) window;
            boolean currentFullScreenState = stage.isFullScreen();
            stage.setFullScreen(!currentFullScreenState);
        }
    }

    private void setupSliderListener(Slider slider, String soundIdentifier) {
        if (slider != null && soundController != null) {
            double initialSliderValue = slider.getValue();
            double initialVolume = initialSliderValue / slider.getMax();
            soundController.setVolume(soundIdentifier, initialVolume);

            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double volume = newValue.doubleValue() / slider.getMax();
                soundController.setVolume(soundIdentifier, volume);
                if (volume > 0 && !soundController.isPlaying(soundIdentifier)) {
                    soundController.playSound(soundIdentifier, true);
                } else if (volume == 0 && soundController.isPlaying(soundIdentifier)) {
                    soundController.stopSound(soundIdentifier);
                }
            });
            soundController.playSound(soundIdentifier, true);
        }
    }

    @FXML
    public void hideQuoteBtnClicked() {
        isShowQuote = !isShowQuote;
        showNode(quoteLabel,isShowQuote);
    }

    @FXML
    public void shuffleQuoteBtnClicked() {
        quoteLabel.setText(quoteController.getRandomQuote());
    }

    /**
     * Logic xử lí Pomodoro Timer:
     */
    @FXML
    public void startTimerBtnClicked() {
        if (focusTimeController != null) {
            if(focusTimeController.getPassedMinutes() > 0 && !isPause) {
                studySessionController.logStudyTime(currentUser.getUserId(), focusTimeController.getPassedMinutes());
            }
            focusTimeController.stop();
        }
        if (minifocusTimeController != null) minifocusTimeController.stop();

        FOCUS_TIME = focusTimeFixLabel.getText();
        BREAK_TIME = breakTimeFixLabel.getText();

        isFocus = true;
        focusTimeController.reset(FOCUS_TIME);
        minifocusTimeController.reset(FOCUS_TIME);

        isPause = false;
        updateTimerButtons();
        showNode(pomodoroFixAnchorPane, false);
        showNode(pomodoroAnchorPane, true);
    }

    @FXML
    public void runPomodoroTimerBtnClicked() {
        if (focusTimeController != null) focusTimeController.start();
        if (minifocusTimeController != null) minifocusTimeController.start();
        isPause = false;
        updateTimerButtons();
    }

    @FXML
    public void pausePomodoroTimerBtnClicked() {
        if (focusTimeController != null) {
            if(focusTimeController.getPassedMinutes() > 0){
                studySessionController.logStudyTime(currentUser.getUserId(), focusTimeController.getPassedMinutes());
            }
            focusTimeController.pause();
        }
        if (minifocusTimeController != null) minifocusTimeController.pause();
        isPause = true;
        updateTimerButtons();
    }

    private void handleTimerFinish() {
        playAlarm();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pomodoro Clock");
        alert.setHeaderText(null);
        alert.setContentText(isFocus ? "Hết thời gian! Nghỉ ngơi một chút nhé." : "Nghỉ ngơi xong! Tập trung tếp thôi nào.");

        alert.setOnHidden(e -> {
            stopAlarm();
            if (loopAutomaticallyRadioBtn.isSelected()) {
                isFocus = !isFocus;
                String nextTime = isFocus ? FOCUS_TIME : BREAK_TIME;
                if(focusTimeController.getPassedMinutes() > 0){
                    studySessionController.logStudyTime(currentUser.getUserId(),focusTimeController.getPassedMinutes());
                }
                focusTimeController.reset(nextTime);
                minifocusTimeController.reset(nextTime);
                runPomodoroTimerBtnClicked();
            } else {
                isFocus = true;
                if(focusTimeController.getPassedMinutes() > 0){
                    studySessionController.logStudyTime(currentUser.getUserId(),focusTimeController.getPassedMinutes());
                }
                focusTimeController.reset(FOCUS_TIME);
                minifocusTimeController.reset(FOCUS_TIME);
                isPause = false;
                updateTimerButtons();
            }
        });

        alert.show();
    }

    private void updateTimerButtons() {
        if (runPomodoroTimerBtn != null && pausePomodoroTimerBtn != null) {
              showNode(runPomodoroTimerBtn, isPause || !focusTimeController.isRunning());
              showNode(pausePomodoroTimerBtn, !isPause && focusTimeController.isRunning());
        }
    }

    private void playAlarm() {
        if (alarmSound != null) {
            alarmSound.setCycleCount(AudioClip.INDEFINITE);
            alarmSound.play();
        }
    }

    private void stopAlarm() {
        if (alarmSound != null && alarmSound.isPlaying()) {
            alarmSound.stop();
        }
    }

    @FXML
    public void minusFocusTimeBtnClicked() {
        String currentTimeString = focusTimeFixLabel.getText();
        String[] parts = currentTimeString.split(" : ");
        int currentHours = Integer.parseInt(parts[0]);
        int currentMinutes = Integer.parseInt(parts[1]);
        long totalMinutes = TimeUnit.HOURS.toMinutes(currentHours) + currentMinutes;

        long newTotalMinutes = totalMinutes - 5;
        if (newTotalMinutes < MIN_FOCUS_MINUTES) {
            newTotalMinutes = MIN_FOCUS_MINUTES;
        }
        long newHours = TimeUnit.MINUTES.toHours(newTotalMinutes);
        long remainingMinutes = newTotalMinutes % 60;
        String newTimeString = String.format("%02d : %02d : %02d", newHours, remainingMinutes, 0);
        focusTimeFixLabel.setText(newTimeString);
    }

    @FXML
    public void plusFocusTimeBtnClicked() {
        String currentTimeString = focusTimeFixLabel.getText();
        String[] parts = currentTimeString.split(" : ");
        int currentHours = Integer.parseInt(parts[0]);
        int currentMinutes = Integer.parseInt(parts[1]);
        long totalMinutes = TimeUnit.HOURS.toMinutes(currentHours) + currentMinutes;

        long newTotalMinutes = totalMinutes + 5;
        if (newTotalMinutes > MAX_FOCUS_MINUTES) {
            newTotalMinutes = MAX_FOCUS_MINUTES;
        }
        long newHours = TimeUnit.MINUTES.toHours(newTotalMinutes);
        long remainingMinutes = newTotalMinutes % 60;
        String newTimeString = String.format("%02d : %02d : %02d", newHours, remainingMinutes, 0);
        focusTimeFixLabel.setText(newTimeString);
    }

    @FXML
    public void minusBreakTimeBtnClicked() {
        String currentTimeString = breakTimeFixLabel.getText();
        String[] parts = currentTimeString.split(" : ");
        int currentHours = Integer.parseInt(parts[0]);
        int currentMinutes = Integer.parseInt(parts[1]);
        long totalMinutes = TimeUnit.HOURS.toMinutes(currentHours) + currentMinutes;

        long newTotalMinutes = totalMinutes - 5;
        if (newTotalMinutes < MIN_BREAK_MINUTES) {
            newTotalMinutes = MIN_BREAK_MINUTES;
        }
        long newHours = TimeUnit.MINUTES.toHours(newTotalMinutes);
        long remainingMinutes = newTotalMinutes % 60;
        String newTimeString = String.format("%02d : %02d : %02d", newHours, remainingMinutes, 0);
        breakTimeFixLabel.setText(newTimeString);
    }

    @FXML
    public void plusTimeBreakBtnClicked() {
        String currentTimeString = breakTimeFixLabel.getText();
        String[] parts = currentTimeString.split(" : ");
        int currentHours = Integer.parseInt(parts[0]);
        int currentMinutes = Integer.parseInt(parts[1]);
        long totalMinutes = TimeUnit.HOURS.toMinutes(currentHours) + currentMinutes;

        long newTotalMinutes = totalMinutes + 5;
        if (newTotalMinutes > MAX_BREAK_MINUTES) {
            newTotalMinutes = MAX_BREAK_MINUTES;
        }
        long newHours = TimeUnit.MINUTES.toHours(newTotalMinutes);
        long remainingMinutes = newTotalMinutes % 60;
        String newTimeString = String.format("%02d : %02d : %02d", newHours, remainingMinutes, 0);
        breakTimeFixLabel.setText(newTimeString);
    }

    /**
     * Logic xử lí Session Goals:
     */
    @FXML
    public void addGoalBtnClicked() {
        String goalText = sessionGoalTextField.getText();
        if (goalText == null || goalText.isEmpty()) {
            sessionGoalTextField.requestFocus();
            return;
        }
        try{
            taskController.addTask(currentUser.getUserId(), goalText);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/FXML/Task.fxml")); // Đảm bảo đường dẫn đúng
            AnchorPane taskNode = loader.load();
            TaskController taskController = loader.getController();
            if (taskController == null) {
                System.err.println("Lỗi: Không thể lấy TaskController từ FXML.");
                return;
            }
            taskController.setData(goalText.trim(), taskListFlowPane, this);
            taskNode.setUserData(taskController);
            taskListFlowPane.getChildren().add(taskNode);
            updateTaskCounts();
            sessionGoalTextField.clear();

        } catch ( IOException e) {
            System.err.println("Lỗi khi tải Task.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateTaskCounts() {
        int totalTasks = 0;
        int completedTasks = 0;
        for (Node taskNode : taskListFlowPane.getChildren()) {
            totalTasks++;
            Object taskData = taskNode.getUserData();
            if (taskData instanceof TaskController) {
                TaskController controller = (TaskController) taskData;
                if (controller.isCompeleted()) {
                    completedTasks++;
                }
            }
        }
        int openTasks = totalTasks - completedTasks;
        openTaskLabel.setText(String.valueOf(openTasks));
        completedTaskLabel.setText(String.valueOf(completedTasks));
        miniTaskLabel.setText(String.format("%d/%d", completedTasks, openTasks));
    }

    /**
     * Logic gọi URL Youtube:
     */
    @FXML
    private void handleUrlBtnClicked() {
        String url = urlTextField.getText();
        if (url == null || url.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập URL YouTube.");
            return;
        }
        String videoId = extractYouTubeVideoId(url.trim());
        if (videoId != null) {
            openVideoPlayerWindow(videoId);
        } else {
            showAlert(Alert.AlertType.ERROR, "URL không hợp lệ", "Không thể nhận dạng Video ID từ URL.");
        }
    }

    private void openVideoPlayerWindow(String videoId) {
        closeCurrentVideoPlayer();
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/controller/FXML/WebView.fxml")));
            Parent videoPlayerRoot = loader.load();
            VideoPlayerController videoController = loader.getController();
            Stage videoStage = new Stage();
            videoStage.setTitle("YouTube Player - " + videoId);
            Scene videoScene = new Scene(videoPlayerRoot);
            videoStage.setScene(videoScene);
            videoStage.setFullScreen(true);
            videoStage.setOnCloseRequest((WindowEvent event) -> {
                if (videoController != null) {
                    videoController.shutdown();
                }
                currentVideoStage = null;
                currentVideoController = null;
            });
            this.currentVideoStage = videoStage;
            this.currentVideoController = videoController;
            videoController.loadAndPlay(videoId);
            videoStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi giao diện trình phát video", e.getMessage());
            resetVideoPlayerState();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi mở trình phát video", e.getMessage());
            resetVideoPlayerState();
        }
    }

    private void closeCurrentVideoPlayer() {
        if (currentVideoStage != null) {
            if (currentVideoController != null) {
                currentVideoController.shutdown();
            }
            currentVideoStage.close();
        }
        resetVideoPlayerState();
    }

    private void removeCurrentVideoPlayer() {
        if ( currentVideoController != null) {
            currentVideoController.shutdown();
            this.currentVideoController = null;
        }
    }

    private void resetVideoPlayerState() {
        this.currentVideoController = null;
    }

    private String extractYouTubeVideoId(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.trim().isEmpty()) {
            return null;
        }
        for (String patternString : YOUTUBE_URL_PATTERNS) {
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {
                if (matcher.groupCount() >= 1) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void cleanupOnExit() {
        removeCurrentVideoPlayer();
    }


}
