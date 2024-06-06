package com.casino.Controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class SceneManager {
    public static final ArrayList<String> scenes = new ArrayList<String>() {{
        add("BlackJackController");
        add("RouletteController");
        add("LoginPage");
        add("HomePage");
        add("ChatPage");
        add("GameHistory");
        add("ConnectWindow");
    }};

    public static final String startingScene = "LoginPage";

    private HashMap<String, Parent> screenMap = new HashMap<>();
    private HashMap<String, IController> controllerMap = new HashMap<>();
    private HashMap<String, Stage> openWindows = new HashMap<>();
    private Stage primaryStage;

    private static SceneManager instance = null;
    public static SceneManager getInstance() {
        if (instance == null)
            instance = new SceneManager();
        return instance;
    }

    public void Initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        openWindows.put("primaryStage", primaryStage);
        primaryStage.setResizable(false);
        //this.primaryStage.getIcons().add(new Image("Images/icon.png"));
    }

    public void addScreen(String name, Parent root){
         screenMap.put(name, root);
    }

    public void addController(String name, IController controller){
        controllerMap.put(name, controller);
    }

    public void removeScreen(String name){
        screenMap.remove(name);
    }

    public void removeController(String name){
        controllerMap.remove(name);
    }

    public void activate(String name){
        Parent root = screenMap.get(name);
        IController controller = controllerMap.get(name);
        if (root == null) {
            System.out.println("Scene "+name+" not found");
            return;
        }

        primaryStage.setScene(new Scene(root));

        
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);


        controller.onActivate();
    }

    public void openPopupWindow(String windowName, String rootName, String title, boolean alwaysOnTop, boolean resizable) {
        if (isWindowShown(windowName))
            return;
        if (isWindowLoaded(windowName))
            closePopupWindow(windowName); // make sure its gone
        Parent root = screenMap.get(rootName);
        IController controller = controllerMap.get(rootName);
        if (root == null) {
            System.out.println("Scene "+rootName+" not found");
            return;
        }
        Stage s = new Stage();
        openWindows.put(windowName, s);
        //s.getIcons().add(new Image("Images/icon.png"));
        s.setScene(new Scene(root));
        s.setAlwaysOnTop(alwaysOnTop);
        s.setResizable(resizable);
        s.setTitle(title);
        controller.onActivate();
        s.show();
    }

    public boolean isWindowShown(String windowName) {
        Stage s = openWindows.get(windowName);
        return s != null && s.isShowing();
    }

    public boolean isWindowLoaded(String windowName) {
        return openWindows.get(windowName) != null;
    }

    public void closePopupWindow(String windowName) {
        Stage s = openWindows.get(windowName);
        if (s != null){
            s.close();
            Parent p = new FlowPane(); // create a temporary root
            s.getScene().setRoot(p);
            s.setScene(null);
        }
        openWindows.remove(windowName);
    }

    public void closePopupWindows() {
        List<String> windows = new ArrayList<>(openWindows.keySet());
        for (String windowName : windows) {
            closePopupWindow(windowName);
        }
    }

    public <ControllerType> ControllerType getController(String name) {
        IController controller = controllerMap.get(name);
        if (controller != null && controller.getClass().isInstance(controller))
            return (ControllerType) controller;
        return null;
    }

    public void LoadScenes() {
        screenMap.clear();
        for (String sceneName : scenes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Scenes/"+sceneName+".fxml"));
                Parent root = loader.load();
                IController controller = loader.getController();
                addScreen(sceneName, root);
                addController(sceneName, controller);
            } catch (Exception e) {
                System.out.println("Error loading scene "+sceneName);
                e.printStackTrace();
            }
        }

        activate(startingScene);
    }
}
