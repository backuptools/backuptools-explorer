/******************************************************************************
 * Copyright (c) 2013,2014. Florian Mahon <florian@faivre-et-mahon.ch>        *
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

package org.fetm.backuptools.explorer.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fetm.backuptools.common.BackupAgentFactory;
import org.fetm.backuptools.common.IBackupAgent;
import org.fetm.backuptools.common.VaultConfigPersistance;
import org.fetm.backuptools.common.VaultConfiguration;
import org.fetm.backuptools.common.model.Backup;
import org.fetm.backuptools.explorer.GUI.VaultEditorController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by florian on 27.05.14.
 */
public class App {

    private Stage primaryStage;
    private ObservableList<VaultConfiguration> vaultConfigurations = FXCollections.observableArrayList();
    private List<IBackupAgent> backupAgentList;
    private ObservableList<List<Backup>> agentObservableList = FXCollections.observableArrayList();




    private String configuration_location;

    public App(){
        configuration_location = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + ".backuptools";

        readConfigurationFiles(configuration_location);
    }

    private void readConfigurationFiles(String configuration_location) {
        try {
            if(!Files.exists(Paths.get(configuration_location))){
                Files.createDirectory(Paths.get(configuration_location));
            }
            vaultConfigurations.clear();
            for(Path file : Files.newDirectoryStream(Paths.get(configuration_location))){
                Properties properties = new Properties();
                properties.load(new FileInputStream(file.toFile()));

                VaultConfigPersistance persistance = new VaultConfigPersistance(properties);
                vaultConfigurations.add(persistance.read());
            }
        } catch (IOException e) {

        }
    }

    private void writeConfigurationFiles(String configuration_location) {
        try {
            if(!Files.exists(Paths.get(configuration_location))){
                Files.createDirectory(Paths.get(configuration_location));
            }

            for(Path file : Files.newDirectoryStream(Paths.get(configuration_location))){
                Files.deleteIfExists(file);
            }
            int cpt = 0;

            for(VaultConfiguration configuration : getVaultConfigurations()){
                Properties properties = new Properties();
                VaultConfigPersistance persistance = new VaultConfigPersistance(properties);
                persistance.write(configuration);
                properties.store(new FileOutputStream(configuration_location+FileSystems.getDefault().getSeparator()+"vault"+cpt+".properties"),"autogenerate data");
                cpt++;
            }
        } catch (IOException e) {

        }
    }

    public ObservableList<VaultConfiguration> getVaultConfigurations() {
        return vaultConfigurations;
    }


    public boolean showVaultEditor( VaultConfiguration vaultConfiguration) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("VaultEditor.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Vault");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        VaultEditorController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setVaultConfiguration(vaultConfiguration);

        backupAgentList = new ArrayList<IBackupAgent>();
        createBackupAgentList();


        dialogStage.showAndWait();

        return controller.isOkClicked();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void addNewVault() {
        VaultConfiguration vaultConfiguration = new VaultConfiguration();
        try {
            if(showVaultEditor(vaultConfiguration)){
                getVaultConfigurations().add(vaultConfiguration);
                writeConfigurationFiles(configuration_location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void editVault(Integer index) {

        try {
            if(showVaultEditor(getVaultConfigurations().get(index))){
                writeConfigurationFiles(configuration_location);
                readConfigurationFiles(configuration_location);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createBackupAgentList(){
        backupAgentList.clear();
        for(VaultConfiguration vaultconfig : vaultConfigurations){
            backupAgentList.add(BackupAgentFactory.buildBackupAgent(vaultconfig));
        }
    }


}
