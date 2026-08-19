package com.nscet.cms.ui;

import com.nscet.cms.db.config.DatabaseConfig;
import com.nscet.cms.ui.navigation.NavigationManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
    "com.nscet.cms.db",
    "com.nscet.cms.core",
    "com.nscet.cms.ui"
})
@Import(DatabaseConfig.class)
class AppSpringConfig {}

public class NscetCmsApp extends Application {

    private static AnnotationConfigApplicationContext applicationContext;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        applicationContext = new AnnotationConfigApplicationContext(AppSpringConfig.class);

        Scene scene = new Scene(new javafx.scene.layout.StackPane(), 1024, 700);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle("NSCET College Management System");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();

        NavigationManager.openLogin();
    }

    public static AnnotationConfigApplicationContext getContext() {
        return applicationContext;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (applicationContext != null) {
            applicationContext.close();
        }
    }
}
