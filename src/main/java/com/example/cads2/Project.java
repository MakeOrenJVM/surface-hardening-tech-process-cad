package com.example.cads2;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Project {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty author;
    private final SimpleStringProperty name;
    private final SimpleStringProperty dateCreated;

    public Project(int id, String author, String name, String dateCreated) {
        this.id = new SimpleIntegerProperty(id);
        this.author = new SimpleStringProperty(author);
        this.name = new SimpleStringProperty(name);
        this.dateCreated = new SimpleStringProperty(dateCreated);
    }

    public int getId() {
        return id.get();
    }

    public String getAuthor() {
        return author.get();
    }

    public String getName() {
        return name.get();
    }

    public String getDateCreated() {
        return dateCreated.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }
    public SimpleStringProperty authorProperty() {
        return author;
    }
    public SimpleStringProperty projectNameProperty() {
        return name;
    }
    public SimpleStringProperty dateProperty() {
        return dateCreated;
    }
}
