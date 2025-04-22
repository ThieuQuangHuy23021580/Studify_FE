package controller;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class VideoPlayerController {

    @FXML
    private WebView webView;

    private WebEngine webEngine;
    private boolean playerReady = false;
    private String currentVideoId = null;

    private Runnable closeRequestHandler;

    @FXML
    private void initialize() {
        System.out.println("VideoPlayerController initializing...");
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webView.setContextMenuEnabled(false);
        setupJsBridge();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("VideoPlayer WebView loaded initial HTML successfully.");
            } else if (newState == Worker.State.FAILED) {
                System.err.println("VideoPlayer WebView failed to load initial HTML.");
                if (webEngine.getLoadWorker().getException() != null) {
                    webEngine.getLoadWorker().getException().printStackTrace();
                    webEngine.loadContent("Lỗi tải trình phát: "
                            + webEngine.getLoadWorker().getException().getMessage());
                } else {
                    webEngine.loadContent("Lỗi không xác định khi tải trình phát.");
                }
            }
        });
        webEngine.loadContent(createHtmlContent(""));

    }

    public void loadAndPlay(String videoId) {
        this.currentVideoId = videoId;
        if (videoId == null || videoId.trim().isEmpty() || !videoId.matches("[a-zA-Z0-9_\\-]+")) {
            Platform.runLater(() ->
                    webEngine.loadContent("Lỗi: Video ID không hợp lệ:" + videoId )
            );
            return;
        }
        if (playerReady) {
            executeScript("loadVideo('" + videoId + "');");
        }
    }

    public void setPlayerReady(boolean ready) {
        Platform.runLater(() -> {
            this.playerReady = ready;
            if (ready && this.currentVideoId != null && !this.currentVideoId.trim().isEmpty()) {
                executeScript("loadVideo('" + this.currentVideoId + "');");
            }
        });
    }


    /**
     * Tạo chuỗi HTML nhúng YouTube IFrame Player API.
     *
     * @param initialVideoId Video ID ban đầu.
     * @return Chuỗi HTML.
     */
    private String createHtmlContent(String initialVideoId) {
        String safeInitialVideoId = (initialVideoId != null && initialVideoId.matches("[a-zA-Z0-9_\\-]+")) ? initialVideoId : "";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("  <head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <style>\n");
        html.append("      body { margin: 0; padding: 0; overflow: hidden; background-color: black; }\n");
        html.append("      html, body, #player { width: 100%; height: 100%; }\n");
        html.append("    </style>\n");
        html.append("  </head>\n");
        html.append("  <body>\n");
        html.append("    <div id=\"player\"></div>\n");
        html.append("    <script>\n");

        html.append("      function logToJava(level, message) {\n");
        html.append("        if (typeof javaConnector !== 'undefined' && javaConnector) {\n");
        html.append("          if (level === 'error' && typeof javaConnector.logError === 'function') {\n");
        html.append("            javaConnector.logError('JS: ' + message);\n");
        html.append("          } else if (level !== 'debug' && typeof javaConnector.log === 'function') {\n");
        html.append("            javaConnector.log('JS: ' + message);\n");
        html.append("          }\n");
        html.append("        } else {\n");
        html.append("          if (level === 'error') { console.error('JS (no bridge): ' + message); }\n");
        html.append("        }\n");
        html.append("      }\n\n");

        html.append("      var tag = document.createElement('script');\n");
        html.append("      tag.src = \"https://www.youtube.com/iframe_api\";\n");
        html.append("      var firstScriptTag = document.getElementsByTagName('script')[0];\n");
        html.append("      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);\n");
        html.append("      var player;\n\n");

        html.append("      function onYouTubeIframeAPIReady() {\n");
        html.append("        try {\n");
        html.append("          player = new YT.Player('player', {\n");
        html.append("            videoId: '").append(safeInitialVideoId).append("',\n");
        html.append("            playerVars: { 'playsinline': 1, 'autoplay': 1, 'controls': 1, 'fs': 1, 'modestbranding': 1, 'rel': 0 },\n");
        html.append("            events: { 'onReady': onPlayerReady, 'onStateChange': onPlayerStateChange, 'onError': onPlayerError }\n");
        html.append("          });\n");
        html.append("        } catch (e) {\n");
        html.append("          logToJava('error', 'CATCH Error creating YT.Player: ' + e.message);\n");
        html.append("        }\n");
        html.append("      }\n\n");

        html.append("      function onPlayerReady(event) {\n");
        html.append("        logToJava('info', 'onPlayerReady function CALLED!');\n"); // Keep this important one
        html.append("        if(typeof javaConnector !== 'undefined') {\n");
        html.append("          try {\n");
        html.append("              javaConnector.setPlayerReady(true);\n");
        html.append("          } catch (e) {\n");
        html.append("               logToJava('error', 'CATCH Error calling javaConnector.setPlayerReady: ' + e.message);\n");
        html.append("          }\n");
        html.append("        } else {\n");
        html.append("           logToJava('error', 'ERROR: javaConnector is UNDEFINED when onPlayerReady was called!');\n");
        html.append("        }\n");
        html.append("      }\n\n");

        html.append("      function onPlayerStateChange(event) {\n");
        html.append("      }\n\n");

        html.append("      function onPlayerError(event) {\n");
        html.append("        var errorCode = event.data;\n");
        html.append("        logToJava('error', 'YouTube Player Error Occurred. Code: ' + errorCode);\n");
        html.append("        var errorMeaning = 'Unknown Error';\n");
        html.append("        switch(errorCode) {\n");
        html.append("           case 2: errorMeaning = 'Invalid parameter.'; break;\n");
        html.append("           case 5: errorMeaning = 'HTML5 player error.'; break;\n");
        html.append("           case 100: errorMeaning = 'Video not found.'; break;\n");
        html.append("           case 101: case 150: errorMeaning = 'Embedding denied.'; break;\n");
        html.append("        }\n");
        html.append("        logToJava('error', 'Error Meaning: ' + errorMeaning);\n");
        html.append("      }\n\n");

        html.append("      function loadVideo(videoId) {\n");
        html.append("        if (player && typeof player.loadVideoById === 'function') {\n");
        html.append("          try {\n");
        html.append("              player.loadVideoById(videoId);\n");
        html.append("          } catch (e) {\n");
        html.append("               logToJava('error', 'CATCH Error calling loadVideoById: ' + e.message);\n");
        html.append("          }\n");
        html.append("        } else {\n");
        html.append("           logToJava('error', 'Player not ready or loadVideoById not available when trying to load ' + videoId);\n");
        html.append("        }\n");
        html.append("      }\n\n");

        html.append("      window.onerror = function(message, source, lineno, colno, error) {\n");
        html.append("           var errorMsg = 'Global JS Error: ' + message + ' at ' + source + ':' + lineno;\n");
        html.append("           logToJava('error', errorMsg);\n");
        html.append("           return true;\n");
        html.append("      };\n\n");

        html.append("    </script>\n");
        html.append("  </body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private void executeScript(String script) {
        if (webEngine != null && webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            try {
                webEngine.executeScript(script);
            } catch (netscape.javascript.JSException e) {
                System.err.println("VideoPlayerController JSException executing script: " + script + "\n - Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("VideoPlayerController Error executing script: " + script + "\n - Error: " + e.getMessage());
            }
        } else {
            System.err.println("VideoPlayerController WebEngine not ready to execute script (State: "
                    + (webEngine != null ? webEngine.getLoadWorker().getState() : "null") + ")");
        }
    }

    /**
     * Thiết lập cầu nối để JS có thể gọi các phương thức trong đối tượng JavaConnector.
     */
    private void setupJsBridge() {
        if (webEngine == null) return;

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED && !webEngine.getLocation().equals("about:blank")) {
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    Object currentConnector = window.getMember("javaConnector");
                    if (!(currentConnector instanceof VideoPlayerController.JavaConnector)) {
                        window.setMember("javaConnector", new JavaConnector());
                        executeScript("if(typeof javaConnector !== 'undefined') { console.log('JS: JavaConnector confirmed.'); } else { console.error('JS: JavaConnector injection failed verification.'); }");
                    }
                } catch (Exception e) {
                    System.err.println("VideoPlayerController: Failed to inject/verify JavaConnector: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Chứa các phương thức JS có thể gọi thông qua đối tượng javaConnector được inject vào môi trường JS.
     * (Call trong JS code)
     */
    public class JavaConnector {

        /**
         * Được gọi bởi hàm 'onPlayerReady' trong JavaScript.
         * @param ready Luôn là true.
         */
        public void setPlayerReady(boolean ready) {
            VideoPlayerController.this.setPlayerReady(ready);
        }

        public void logError(String errorMessage) {
            System.err.println("Error reported from JS: " + errorMessage);
        }

        public void log(String message) {
            System.out.println("Log from JS: " + message);
        }
    }

    public void setCloseRequestHandler(Runnable handler) {
        this.closeRequestHandler = handler;
    }

    public void shutdown() {
        executeScript("destroyPlayer();");
        if (webEngine != null) {
            webEngine.getLoadWorker().cancel();
            webEngine.load("about:blank");
        }
        playerReady = false;
        currentVideoId = null;
    }
}