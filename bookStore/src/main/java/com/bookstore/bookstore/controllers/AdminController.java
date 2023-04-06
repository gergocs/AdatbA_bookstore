package com.bookstore.bookstore.controllers;

import com.bookstore.bookstore.MainApplication;
import com.bookstore.bookstore.daos.DAO;
import com.bookstore.bookstore.models.Product;
import com.bookstore.bookstore.models.Purchase;
import com.bookstore.bookstore.models.Store;
import com.bookstore.bookstore.models.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class AdminController {
    @FXML
    private AnchorPane content;
    @FXML
    private Button addProduct;
    private List<Node> items;
    private TableColumn[] columns;

    @FXML
    protected void onAddProduct() throws IOException {
        if (items != null) {
            content.getChildren().removeAll(items);
        }

        items = new ArrayList<>();

        //TODO: labels

        TextField name = new TextField();
        name.setLayoutY(150);
        TextField genre = new TextField();
        genre.setLayoutY(200);
        TextField type = new TextField();
        type.setLayoutY(250);
        TextField production = new TextField();
        production.setLayoutY(300);
        Button done = new Button();
        done.setText("done");
        done.setLayoutY(350);
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String nameString = name.getText();
                String genreString = genre.getText();
                String typeString = type.getText();
                String productionString = production.getText();

                if (nameString.isEmpty() || genreString.isEmpty() || typeString.isEmpty() || productionString.isEmpty()) {
                    // TODO Error
                    return;
                }

                Product product = new Product();
                product.setName(nameString);
                product.setGenre(genreString);
                product.setType(typeString);
                product.setProduction(productionString);
                product.setReview(0);

                DAO.instance().addData(product);

                content.getChildren().removeAll(items);
                items = null;
            }
        });

        items.add(name);
        items.add(genre);
        items.add(type);
        items.add(production);
        items.add(done);

        content.getChildren().addAll(items);
    }

    @FXML
    protected void onAddStore() throws IOException {
        if (items != null) {
            content.getChildren().removeAll(items);
        }

        items = new ArrayList<>();
        //TODO: labels

        TextField place = new TextField();
        place.setLayoutY(150);
        TextField capacity = new TextField();
        capacity.setLayoutY(200);
        Button done = new Button();
        done.setText("done");
        done.setLayoutY(250);
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String placeString = place.getText();
                int capacityInt = Integer.parseInt(capacity.getText());

                if (placeString.isEmpty()) {
                    // TODO Error
                    return;
                }

                Store store = new Store();
                store.setPlace(placeString);
                store.setCapacity(capacityInt);
                store.setType("fizikai");

                DAO.instance().addData(store);

                content.getChildren().removeAll(items);
                items = null;
            }
        });

        items.add(place);
        items.add(capacity);
        items.add(done);

        content.getChildren().addAll(items);
    }

    @FXML
    protected void onModifyProduct() throws IOException {
        String result = getDataFromDialog();

        if (result.isEmpty()) {
            // TODO: error
            return;
        }

        Product product = DAO.instance().getDataByID(Product.class, Integer.parseInt(result));

        if (items != null) {
            content.getChildren().removeAll(items);
        }

        items = new ArrayList<>();

        //TODO: labels
        TextField name = new TextField();
        name.setText(product.getName());
        name.setLayoutY(150);
        TextField genre = new TextField();
        genre.setText(product.getGenre());
        genre.setLayoutY(200);
        TextField type = new TextField();
        type.setText(product.getType());
        type.setLayoutY(250);
        TextField production = new TextField();
        production.setText(product.getProduction());
        production.setLayoutY(300);
        Button done = new Button();
        done.setText("done");
        done.setLayoutY(350);
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String nameString = name.getText();
                String genreString = genre.getText();
                String typeString = type.getText();
                String productionString = production.getText();

                if (nameString.isEmpty() || genreString.isEmpty() || typeString.isEmpty() || productionString.isEmpty()) {
                    // TODO Error
                    return;
                }

                product.setName(nameString);
                product.setGenre(genreString);
                product.setType(typeString);
                product.setProduction(productionString);

                DAO.instance().updateData(product);

                content.getChildren().removeAll(items);
                items = null;
            }
        });

        items.add(name);
        items.add(genre);
        items.add(type);
        items.add(production);
        items.add(done);

        content.getChildren().addAll(items);
    }

    @FXML
    protected void onModifyStore() throws IOException {
        String result = getDataFromDialog();

        if (result.isEmpty()) {
            // TODO: error
            return;
        }

        Store store = DAO.instance().getDataByID(Store.class, Integer.parseInt(result));

        if (items != null) {
            content.getChildren().removeAll(items);
        }

        items = new ArrayList<>();

        //TODO: labels
        TextField place = new TextField();
        place.setText(store.getPlace());
        place.setLayoutY(150);
        TextField capacity = new TextField();
        capacity.setText(String.valueOf(store.getCapacity()));
        capacity.setLayoutY(200);
        Button done = new Button();
        done.setText("done");
        done.setLayoutY(250);
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String placeString = place.getText();
                int capacityInt = Integer.parseInt(capacity.getText());

                if (placeString.isEmpty()) {
                    // TODO Error
                    return;
                }

                store.setPlace(placeString);
                store.setCapacity(capacityInt);

                DAO.instance().updateData(store);

                content.getChildren().removeAll(items);
                items = null;
            }
        });

        items.add(place);
        items.add(capacity);
        items.add(done);

        content.getChildren().addAll(items);
    }

    private String getDataFromDialog() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("get ID");
        dialog.setHeaderText("Please enter the id");
        dialog.setContentText("ID:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("");
    }

    public void onDeleteProduct() {
        String idString = getDataFromDialog();

        if (idString.isEmpty()) {
            //TODO error
            return;
        }

        DAO.instance().deleteData(DAO.instance().getDataByID(Product.class, Integer.parseInt(idString)));
    }

    public void onDeleteStore() {
        String idString = getDataFromDialog();

        if (idString.isEmpty()) {
            //TODO error
            return;
        }

        DAO.instance().deleteData(DAO.instance().getDataByID(Store.class, Integer.parseInt(idString)));
    }

    public <T> void generateTable(ArrayList<T> data) {
        if (items != null) {
            content.getChildren().removeAll(items);
        }

        items = new ArrayList<>();
        TableView<T> dataTable = new TableView<T>();
        dataTable.setLayoutY(150);
        dataTable.setPrefWidth(content.getPrefWidth());
        dataTable.setPrefHeight(200);

        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        List<Field> properties = Arrays.asList(data.get(0).getClass().getDeclaredFields());
        columns = new TableColumn[properties.size()];

        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumn<T, String>(properties.get(i).getName());
            columns[i].setCellValueFactory(new PropertyValueFactory<>(properties.get(i).getName()));
            dataTable.getColumns().add(columns[i]);
        }

        for (var item : data) {
            dataTable.getItems().add(item);
        }

        items.add(dataTable);

        content.getChildren().addAll(items);
    }

    public void onUsersButtonClick(ActionEvent actionEvent) {
        ArrayList<User> data = DAO.instance().runCustomQuery(User.class, "SELECT * FROM BOOK_STORE_USERS");
        this.generateTable(data);
    }

    public void onProductsButtonClick(ActionEvent actionEvent) {
        ArrayList<Product> data = DAO.instance().runCustomQuery(Product.class, "SELECT * FROM BOOK_STORE_PRODUCT");
        this.generateTable(data);
    }

    public void onPurchasesButtonClick(ActionEvent actionEvent) {
        ArrayList<Purchase> data = DAO.instance().runCustomQuery(Purchase.class, "SELECT * FROM BOOK_STORE_PURCHASE");
        this.generateTable(data);
    }

    public void onStoresButtonClick(ActionEvent actionEvent) {
        ArrayList<Store> data = DAO.instance().runCustomQuery(Store.class, "SELECT * FROM BOOK_STORE_STORE");
        this.generateTable(data);
    }

    public void onLogOut() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("login-view.fxml")));
        Stage window = (Stage) addProduct.getScene().getWindow();
        window.setScene(new Scene(root, 700, 500));
    }
}
