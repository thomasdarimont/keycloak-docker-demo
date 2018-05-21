/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo.todos.ui;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import demo.JavaFxFrontendApplication;
import demo.todos.Todo;
import demo.todos.TodoService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author Thomas Darimont
 */
@FXMLController
@SuppressWarnings("restriction")
public class TodosController {

	@FXML
	Label currentUsernameLabel;

	@FXML
	Button logoutButton;

	@FXML
	TableView<Todo> todosTable;

	@FXML
	TableColumn<Todo, String> todoNameColumn;
	
	@FXML
	TableColumn<Todo, String> todoDescriptionColumn;
	
	@FXML
	TableColumn<Todo, String> todoCompletedColumn;

	@Autowired
	TodoService todoTrackingService;

	@FXML
	public void initialize() {

		currentUsernameLabel.setText("User: " + JavaFxFrontendApplication.KEYCLOAK.getIdToken().getPreferredUsername());
		logoutButton.setOnAction(this::doLogout);

		configureProjectsTable();

		for (Todo todo : todoTrackingService.findMyTodos()) {
			todosTable.getItems().add(todo);
		}

		todosTable.getSelectionModel().selectFirst();
	}

	private void doLogout(ActionEvent evt) {

		logoutButton.setDisable(true);

		Task<Void> logoutTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				JavaFxFrontendApplication.KEYCLOAK.logout();
				Platform.exit();
				return null;
			}
		};

		new Thread(logoutTask).start();
	}

	private void configureProjectsTable() {

		todoNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		todoDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		todoCompletedColumn.setCellValueFactory(new PropertyValueFactory<>("completed"));
	}
}
