package com.example.cads2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;

public class ProjectsListController {
    @FXML
    private TableColumn<Project, String> authorColumn;

    @FXML
    private TableColumn<Project, String> dateColumn;

    @FXML
    private Spinner<?> diameterSpinner;

    @FXML
    private TableColumn<Project, Integer> idColumn;

    @FXML
    private Spinner<?> lengthSpinner;

    @FXML
    private TableColumn<Project, String> nameProjectColumn;

    @FXML
    private Button onBackButton;

    @FXML
    private ComboBox<?> partNameComboBox;

    @FXML
    private TableView<Project> projectsTable;

    @FXML
    private ComboBox<?> quenchDepthsComboBox;

    @FXML
    private ComboBox<?> steelGradeComboBox;

    @FXML
    private Spinner<?> timeSpinner;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAndDisplayProjects();
    }

    private ObservableList<Project> loadProjectsFromDatabase() {
        ObservableList<Project> projects = FXCollections.observableArrayList();

        String query = "SELECT id, case_author, project_name, case_created_at FROM heat_treatment_cases";

        try (Connection conn = Connector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Integer id = rs.getInt("id");
                String author = rs.getString("case_author");
                String name = rs.getString("project_name");
                String dateTime = rs.getString("case_created_at");
                String dateOnly = dateTime.split(" ")[0];

                projects.add(new Project(id,author, name, dateOnly));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }

    // Метод для настройки колонок таблицы
    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        nameProjectColumn.setCellValueFactory(cellData -> cellData.getValue().projectNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    }

    // Метод для загрузки данных из БД и отображения в таблице
    private void loadAndDisplayProjects() {
        ObservableList<Project> projects = loadProjectsFromDatabase();
        projectsTable.setItems(projects);
    }
}