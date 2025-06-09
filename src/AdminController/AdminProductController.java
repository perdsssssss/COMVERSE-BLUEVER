package AdminController;
 
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import Class.Product;
import Class.ProductTransfer;
import Main.DatabaseHandler;
 
import java.io.File;
import java.util.HashMap;
import java.util.Map;
 
public class AdminProductController {
 
    @FXML private ComboBox<String> categorycombobox;
    @FXML private TextField pURLTextFIeld;
    @FXML private TextField productNameTextField;
    @FXML private TextField amountTextFIeld;
    @FXML private TableView<Product> mytable;
    @FXML private TableColumn<Product, Integer> productcol;
    @FXML private TableColumn<Product, String> categorycol;
    @FXML private TableColumn<Product, String> pURLcol;
    @FXML private TableColumn<Product, String> productnamecol;
    @FXML private TableColumn<Product, Double> amountcol;
    @FXML private TableColumn<Product, Integer> quantitycol;
    @FXML private Spinner<Integer> quantityspinner;
    @FXML private Button addproductbtn;
    @FXML private Button updateProductBtn;
    @FXML private Button deleteProductBtn;
    @FXML private TextField searchField;
 
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final FilteredList<Product> filteredProducts = new FilteredList<>(productList, p -> true);
   
    private final Map<String, Image> imageCache = new HashMap<>();
    private final int MAX_CACHE_SIZE = 50;
 
    @FXML
    public void initialize() {
        // Initialize UI components first
        initializeUIComponents();
       
        // Setup table columns
        setupTableColumns();
       
        // Setup image cell factory with caching
        setupImageColumn();
       
        // Setup event handlers
        setupEventHandlers();
       
        // Load data asynchronously
        loadDataAsync();
    }
 
    private void initializeUIComponents() {
        // Use single list creation instead of multiple calls
        categorycombobox.setItems(FXCollections.observableArrayList(
                "C001 - SHIRT", "C002 - CAP", "C003 - BAG", "C004 - JACKET", "C005 - LACE"));
 
        quantityspinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        mytable.setItems(filteredProducts);
    }
 
    private void setupTableColumns() {
        // Batch setup ng table columns
        productcol.setCellValueFactory(new PropertyValueFactory<>("productId"));
        categorycol.setCellValueFactory(new PropertyValueFactory<>("category"));
        productnamecol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        amountcol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        quantitycol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        pURLcol.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));
    }
 
    private void setupImageColumn() {
        pURLcol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
           
            {
                // Pre-configure ImageView properties
                imageView.setFitWidth(80);
                imageView.setFitHeight(80);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
            }
 
            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null || imageUrl.isEmpty()) {
                    setGraphic(null);
                    return;
                }
 
                // Check cache first
                Image cachedImage = imageCache.get(imageUrl);
                if (cachedImage != null) {
                    imageView.setImage(cachedImage);
                    setGraphic(imageView);
                    return;
                }
 
                // Load image asynchronously
                loadImageAsync(imageUrl);
            }
           
            private void loadImageAsync(String imageUrl) {
                Task<Image> imageTask = new Task<>() {
                    @Override
                    protected Image call() {
                        try {
                            return new Image(imageUrl, 80, 80, true, true, true); // background loading
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };
               
                imageTask.setOnSucceeded(e -> {
                    Image image = imageTask.getValue();
                    if (image != null) {
                        // Cache management
                        if (imageCache.size() >= MAX_CACHE_SIZE) {
                            imageCache.clear(); // Simple cache cleanup
                        }
                        imageCache.put(imageUrl, image);
                       
                        Platform.runLater(() -> {
                            imageView.setImage(image);
                            setGraphic(imageView);
                        });
                    } else {
                        Platform.runLater(() -> setGraphic(null));
                    }
                });
               
                imageTask.setOnFailed(e -> Platform.runLater(() -> setGraphic(null)));
               
                // Run sa background thread
                Thread imageThread = new Thread(imageTask);
                imageThread.setDaemon(true);
                imageThread.start();
            }
        });
    }
 
    private void setupEventHandlers() {
        // Batch setup ng event handlers
        addproductbtn.setOnAction(e -> addProduct());
        updateProductBtn.setOnAction(e -> updateProduct());
        deleteProductBtn.setOnAction(e -> deleteProduct());
 
        // Optimized table selection
        mytable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
 
        // Debounced search filter
        setupSearchFilter();
    }
 
    private void setupSearchFilter() {
        // Simple search without complex listeners
        searchField.setOnKeyReleased(e -> {
            String filter = searchField.getText();
            if (filter == null || filter.isBlank()) {
                filteredProducts.setPredicate(p -> true);
                return;
            }
           
            String lowerCaseFilter = filter.toLowerCase();
            filteredProducts.setPredicate(product ->
                product.getProductName().toLowerCase().contains(lowerCaseFilter) ||
                product.getCategory().toLowerCase().contains(lowerCaseFilter) ||
                String.valueOf(product.getAmount()).contains(lowerCaseFilter)
            );
        });
    }
 
    private void loadDataAsync() {
        // Load table data sa background
        Task<ObservableList<Product>> loadTask = new Task<>() {
            @Override
            protected ObservableList<Product> call() {
                return FXCollections.observableArrayList(DatabaseHandler.getInstance().getAllProducts());
            }
        };
       
        loadTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                productList.setAll(loadTask.getValue());
            });
        });
       
        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
 
    private void populateFields(Product product) {
        categorycombobox.setValue(product.getCategory());
        productNameTextField.setText(product.getProductName());
        pURLTextFIeld.setText(product.getImageUrl());
        amountTextFIeld.setText(String.valueOf(product.getAmount()));
        quantityspinner.getValueFactory().setValue(product.getQuantity());
    }
 
    private void addProduct() {
        if (!validateInputs()) return;
 
        Product product = createProductFromInputs();
        if (product == null) return;
 
        // Async database operation
        performDatabaseOperation(() -> DatabaseHandler.getInstance().addProduct(product),
                                "Product added successfully.",
                                "Failed to add product to the database.",
                                () -> {
                                    clearFields();
                                    refreshTable();
                                    updateProductTransfer(product);
                                });
    }
 
    private void updateProduct() {
        Product selected = mytable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to update.");
            return;
        }
 
        if (!validateInputs()) return;
 
        Product updatedProduct = createProductFromInputs();
        if (updatedProduct == null) return;
 
        if (isNoChanges(selected, updatedProduct)) {
            showAlert(Alert.AlertType.INFORMATION, "No Changes Detected", "No changes were made to update.");
            return;
        }
 
        // Store original category before updating
        String originalCategory = selected.getCategory();
       
        // Copy updated values to selected product
        copyProductData(updatedProduct, selected);
 
        performDatabaseOperation(() -> DatabaseHandler.getInstance().updateProduct(selected),
                                "Product updated successfully.",
                                "Failed to update product.",
                                () -> {
                                    refreshTable();
                                    // Update ProductTransfer lists if category changed
                                    if (!originalCategory.equals(selected.getCategory())) {
                                        removeFromProductTransfer(selected, originalCategory);
                                        updateProductTransfer(selected);
                                    }
                                });
    }
 
    private void deleteProduct() {
        Product selected = mytable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a product to delete.");
            return;
        }
 
        if (!confirmDialog("Delete Product", "Are you sure you want to delete this product?")) return;
 
        performDatabaseOperation(() -> DatabaseHandler.getInstance().deleteProduct(selected.getProductId()),
                                "Product deleted successfully.",
                                "Failed to delete product.",
                                () -> {
                                    refreshTable();
                                    // Remove from ProductTransfer lists
                                    removeFromProductTransfer(selected, selected.getCategory());
                                });
    }
 
    private boolean validateInputs() {
        String category = categorycombobox.getValue();
        String productName = productNameTextField.getText().trim();
        String imageUrl = pURLTextFIeld.getText().trim();
        String amountText = amountTextFIeld.getText().trim();
        Integer quantity = quantityspinner.getValue();
 
        if (category == null || category.isEmpty() || productName.isEmpty() ||
            imageUrl.isEmpty() || amountText.isEmpty() || quantity == null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields including quantity.");
            return false;
        }
 
        try {
            Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Amount must be a valid number.");
            return false;
        }
 
        return true;
    }
 
    private Product createProductFromInputs() {
        String category = categorycombobox.getValue();
        String productName = productNameTextField.getText().trim();
        String imageUrl = pURLTextFIeld.getText().trim();
        String amountText = amountTextFIeld.getText().trim();
        Integer quantity = quantityspinner.getValue();
 
        double amount = Double.parseDouble(amountText);
 
        // Improved image URL validation
        String finalImageUrl = validateAndProcessImageUrl(imageUrl);
        if (finalImageUrl == null) {
            return null;
        }
 
        Product product = new Product();
        product.setCategory(category);
        product.setProductName(productName);
        product.setImageUrl(finalImageUrl);
        product.setAmount(amount);
        product.setQuantity(quantity);
 
        return product;
    }
 
    private String validateAndProcessImageUrl(String imageUrl) {
        // Check if it's a web URL
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl; // Return as is for web URLs
        }
       
        // Check if it's a file URI
        if (imageUrl.startsWith("file:/")) {
            return imageUrl; // Return as is for file URIs
        }
       
        // Treat as local file path
        File file = new File(imageUrl);
        if (file.exists() && file.isFile()) {
            return file.toURI().toString();
        }
       
        // Check common image extensions and provide helpful error
        String lowerUrl = imageUrl.toLowerCase();
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") ||
            lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") ||
            lowerUrl.endsWith(".bmp")) {
            showAlert(Alert.AlertType.ERROR, "File Error",
                     "Local image file does not exist: " + imageUrl +
                     "\n\nPlease check if:\n• File path is correct\n• File exists in the specified location");
        } else {
            showAlert(Alert.AlertType.ERROR, "URL Error",
                     "Invalid image URL or file path.\n\nSupported formats:\n• Web URLs (http:// or https://)\n• Local file paths (.jpg, .png, .gif, etc.)");
        }
       
        return null;
    }
 
    private boolean isNoChanges(Product original, Product updated) {
        return original.getCategory().equals(updated.getCategory()) &&
               original.getProductName().equals(updated.getProductName()) &&
               original.getImageUrl().equals(updated.getImageUrl()) &&
               original.getAmount() == updated.getAmount() &&
               original.getQuantity() == updated.getQuantity();
    }
 
    private void copyProductData(Product source, Product target) {
        target.setCategory(source.getCategory());
        target.setProductName(source.getProductName());
        target.setImageUrl(source.getImageUrl());
        target.setAmount(source.getAmount());
        target.setQuantity(source.getQuantity());
    }
 
    private void performDatabaseOperation(DatabaseOperation operation, String successMsg,
                                        String errorMsg, Runnable onSuccess) {
        Task<Boolean> dbTask = new Task<>() {
            @Override
            protected Boolean call() {
                return operation.execute();
            }
        };
 
        dbTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (dbTask.getValue()) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", successMsg);
                    if (onSuccess != null) onSuccess.run();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Database Error", errorMsg);
                }
            });
        });
 
        Thread dbThread = new Thread(dbTask);
        dbThread.setDaemon(true);
        dbThread.start();
    }
 
    private void updateProductTransfer(Product product) {
        String category = product.getCategory();
        switch (category) {
            case "C001 - SHIRT":
                ProductTransfer.getShirtProductList().add(product);
                break;
            case "C002 - CAP":
                ProductTransfer.getCapProductList().add(product);
                break;
            case "C003 - BAG":
                ProductTransfer.getBagProductList().add(product);
                break;
            case "C004 - JACKET":
                ProductTransfer.getJacketProductList().add(product);
                break;
            case "C005 - LACE":
                ProductTransfer.getLaceProductList().add(product);
                break;
        }
    }
 
    private void removeFromProductTransfer(Product product, String category) {
        switch (category) {
            case "C001 - SHIRT":
                ProductTransfer.getShirtProductList().removeIf(p ->
                    p.getProductId() == product.getProductId());
                break;
            case "C002 - CAP":
                ProductTransfer.getCapProductList().removeIf(p ->
                    p.getProductId() == product.getProductId());
                break;
            case "C003 - BAG":
                ProductTransfer.getBagProductList().removeIf(p ->
                    p.getProductId() == product.getProductId());
                break;
            case "C004 - JACKET":
                ProductTransfer.getJacketProductList().removeIf(p ->
                    p.getProductId() == product.getProductId());
                break;
            case "C005 - LACE":
                ProductTransfer.getLaceProductList().removeIf(p ->
                    p.getProductId() == product.getProductId());
                break;
        }
    }
 
    private void clearFields() {
        categorycombobox.setValue(null);
        productNameTextField.clear();
        pURLTextFIeld.clear();
        amountTextFIeld.clear();
        quantityspinner.getValueFactory().setValue(1);
    }
 
    private void refreshTable() {
        loadDataAsync();
    }
 
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 
    private boolean confirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
 
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yes, no);
 
        return alert.showAndWait().orElse(no) == yes;
    }
 
    @FunctionalInterface
    private interface DatabaseOperation {
        boolean execute();
    }
}