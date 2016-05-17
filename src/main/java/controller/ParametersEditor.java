package controller;

import commands.Command;
import commands.EditModule;
import commands.EditValue;
import commands.ExecuteAll;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Module;
import model.Value;
import utils.CareTaker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felipe on 11/05/16.
 */
public class ParametersEditor extends VBox {

    @FXML
    private ListView<String> listView;

    private Map<String, Module> selectedModules = MainWindow.selectedModules;

    @FXML
    private Label matchesLabel;

    @FXML
    private Label parameterLabel;

    @FXML
    private TextField valueTextField;

    @FXML
    private Button acceptButton;

    @FXML
    private Button cancelButton;

    public ParametersEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/parametersEditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        listView = new ListView<>();
    }


    @FXML
    public void initialize() {
        parameterLabel.setText("");
        Module base = selectedModules.values().stream().findFirst().get();


        Map<String, Value> candidates = new HashMap<>(base.getParameters());
        ArrayList<Value> blackList = new ArrayList<>();


        for (Module module : selectedModules.values()) {
            candidates.keySet().forEach(key -> {
                if (!module.getParameters().keySet().contains(key)) {

                    blackList.add(candidates.get(key));
                } else {
                    if (!module.getParameters().get(key).getType().equals(candidates.get(key).getType())) {

                        blackList.add(candidates.get(key));
                    }
                }

            });
        }

        candidates.put("Host", new Value("#module_host", new String("localhost"), "localhost", true));
        blackList.forEach(value -> {
            candidates.remove(value.getName());
        });


        matchesLabel.setText("" + (candidates.size() + 1));
        ObservableList<String> list = FXCollections.observableArrayList(candidates.keySet());

        listView.setItems(list);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            valueTextField.setText("");
            String valueName = newValue;
            parameterLabel.setText(valueName);
            valueTextField.setPromptText("Insert here a " + candidates.get(newValue).getType() + " value");
        });

        acceptButton.setOnAction(event -> {
            if(valueTextField.getText()!=null&& !parameterLabel.getText().isEmpty()) {
                ArrayList<Command> allEdits = new ArrayList<>();
                if (parameterLabel.getText().equals("Host")) {
                    selectedModules.keySet().forEach(key -> {
                        Command editHost = new EditModule(selectedModules.get(key), EditModule.Type.Host, valueTextField.getText());
                        allEdits.add(editHost);
                    });

                } else {
                    selectedModules.keySet().forEach(key -> {
                        Value current = selectedModules.get(key).getParameters().get(parameterLabel.getText());
                        Command currentEdit = new EditValue(current, valueTextField.getText());
                        allEdits.add(currentEdit);
                    });

                }

                Command executeAll = new ExecuteAll(allEdits);
                executeAll.execute();
                CareTaker.addMemento(executeAll);

                selectedModules.keySet().forEach(s -> {
                    selectedModules.get(s).validate();
                });
                this.getScene().getWindow().hide();
            }
        });


        cancelButton.setOnAction(event -> {
            this.getScene().getWindow().hide();

        });


    }
}
