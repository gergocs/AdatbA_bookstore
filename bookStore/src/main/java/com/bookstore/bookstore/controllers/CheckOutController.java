package com.bookstore.bookstore.controllers;

import com.bookstore.bookstore.MainApplication;
import com.bookstore.bookstore.daos.DAO;
import com.bookstore.bookstore.models.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class CheckOutController {
    @FXML
    private Button cancel;
    @FXML
    private AnchorPane content;
    @FXML
    private Button doneOnline;
    private List<Node> items;
    ArrayList<Product> cartArray = new ArrayList<>();

    @FXML
    public void initialize() {
        var cart = DAO.getCart();

        for (var key : cart.keySet()) {
            Product product = DAO.instance().getDataByID(Product.class, key);
            for (int i = 0; i < cart.get(key); i++) {
                this.cartArray.add(product);
            }
        }

        this.generateTable(this.cartArray);
    }

    public void onDoneOnline(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog("Address");
        dialog.setTitle("Address for the order");
        dialog.setHeaderText("Write address");
        dialog.setContentText("Address:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(s -> {
            Date date = new Date();
            Purchase purchase = new Purchase();
            purchase.setDateOfPurchase(date);
            purchase.setUser(DAO.getCurrentUser());
            purchase.setPrice(1000);

            TextInputDialog dialog2 = new TextInputDialog("review");
            dialog2.setTitle("review");
            dialog2.setHeaderText("review");
            dialog2.setContentText("review");

            Optional<String> result2 = dialog2.showAndWait();

            if (result2.isPresent()) {
                try {
                    purchase.setReview(Integer.parseInt(result2.get()));
                } catch (NumberFormatException e) {
                    purchase.setReview(0);
                }
            } else {
                purchase.setReview(0);
            }

            DAO.instance().addData(purchase);

            Online online = new Online();

            online.setAddress(s);
            online.setDateTime(date);
            online.setPurchase(purchase);

            DAO.instance().addData(online);
            DAO.cart.clear();

            Parent root = null;

            try {
                root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-view.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Stage window = (Stage) doneOnline.getScene().getWindow();
            window.setScene(new Scene(root, 700, 500));
        });
    }

    public void onDoneOffline(ActionEvent actionEvent) {
        var stores = DAO.instance().runCustomQuery(Store.class, "SELECT * FROM BOOK_STORE_STORE").toArray(new Store[0]);
        ChoiceDialog<Store> d = new ChoiceDialog<>(stores[1], stores);

        Optional<Store> store = d.showAndWait();

        store.ifPresent(s -> {
            Date date = new Date();
            Purchase purchase = new Purchase();
            purchase.setDateOfPurchase(date);
            purchase.setUser(DAO.getCurrentUser());
            purchase.setPrice(1000);

            TextInputDialog dialog2 = new TextInputDialog("review");
            dialog2.setTitle("review");
            dialog2.setHeaderText("review");
            dialog2.setContentText("5");

            Optional<String> result2 = dialog2.showAndWait();

            if (result2.isPresent()) {
                try {
                    purchase.setReview(Integer.parseInt(result2.get()));
                } catch (NumberFormatException e) {
                    purchase.setReview(0);
                }
            } else {
                purchase.setReview(0);
            }

            DAO.instance().addData(purchase);

            Offline offline = new Offline();

            offline.setStore(s);
            offline.setPurchase(purchase);
            offline.setPlace(s.getPlace());

            DAO.instance().addData(offline);
            DAO.cart.clear();

            Parent root = null;

            try {
                root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-view.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Stage window = (Stage) doneOnline.getScene().getWindow();
            window.setScene(new Scene(root, 700, 500));
        });
    }

    public void onCancel(ActionEvent actionEvent) {
        Parent root = null;

        try {
            root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-view.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage window = (Stage) doneOnline.getScene().getWindow();
        window.setScene(new Scene(root, 700, 500));
    }

    public void generateTable(ArrayList<Product> data) {
        if (items != null) {
            content.getChildren().removeAll(items);
        }

        data.sort(Comparator.comparingInt(Product::getId));

        items = new ArrayList<>();
        TableView<Product> dataTable = new TableView<>();
        dataTable.setLayoutY(150);
        dataTable.setPrefWidth(content.getPrefWidth());
        dataTable.setPrefHeight(200);

        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        List<Field> properties = Arrays.asList(data.get(0).getClass().getDeclaredFields());
        TableColumn[] columns = new TableColumn[properties.size()];

        for (int i = 0; i < columns.length; i++) {
            columns[i] = new TableColumn<Product, String>(properties.get(i).getName());
            columns[i].setCellValueFactory(new PropertyValueFactory<>(properties.get(i).getName()));
            dataTable.getColumns().add(columns[i]);
        }

        for (var item : data) {
            dataTable.getItems().add(item);
        }

        TableColumn<Product, Void> moreButton = new TableColumn<>("more");

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> moreCellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("more");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Product data1 = getTableView().getItems().get(getIndex());
                            cartArray.add(data1);
                            generateTable(cartArray);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        moreButton.setCellFactory(moreCellFactory);

        dataTable.getColumns().add(moreButton);

        TableColumn<Product, Void> deleteButton = new TableColumn<>("delete");

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> deleteCellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("delete");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Product data1 = getTableView().getItems().get(getIndex());
                            cartArray.remove(data1);

                            if (cartArray.size() == 0) {
                                Parent root = null;

                                try {
                                    root = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource("main-view.fxml")));
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                Stage window = (Stage) doneOnline.getScene().getWindow();
                                window.setScene(new Scene(root, 700, 500));
                                return;
                            }

                            generateTable(cartArray);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        deleteButton.setCellFactory(deleteCellFactory);

        dataTable.getColumns().add(deleteButton);

        items.add(dataTable);

        content.getChildren().addAll(items);
    }
}
