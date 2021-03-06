package org.fetm.backuptools.explorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fetm.backuptools.explorer.GUI.MainLayoutController;
import org.fetm.backuptools.explorer.domain.App;

/******************************************************************************
 * Copyright (c) 2014. Richard Breguet <richard.breguet@gmail.com>            *
 *                                                                            *
 * This file is part of backuptools.                                          *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * any later version.                                                         *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * General Public License for more details. You should have received a        *
 * copy of the GNU General Public License along with this program.            *
 * If not, see <http://www.gnu.org/licenses/>.                                *
 ******************************************************************************/

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainLayout.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        primaryStage.setTitle("BackUp Tool");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        App app = new App();
        app.setPrimaryStage(primaryStage);

        MainLayoutController controller = loader.getController();
        controller.setApp(app);
    }

    public static void main(String[] args){
        launch(args);
    }


}
