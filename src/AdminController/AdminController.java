package AdminController;
 
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
 
public class AdminController implements Initializable {
 
    @FXML
    private Label homelabel;
   
    @FXML
    private Button users;
   
    @FXML
    private Button categories;
   
    @FXML
    private Button products;
   
    @FXML
    private Button orderhistory;
   
    @FXML
    private Button paymenthistory;
   
    @FXML
    private Button shippinghistory;
   
    @FXML
    private Button cart;
 
    @FXML
    private BorderPane borderpane;
 
    @FXML
    private AnchorPane admin;
 
    @FXML
    private TextField usernameTextfield;
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminuser.fxml"));
            borderpane.setCenter(view);
            // Set users button as active initially
            setActiveButton(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    // Method to set active button style
    private void setActiveButton(Button activeButton) {
        // Reset all buttons to normal style
        Button[] allButtons = {users, categories, products, orderhistory, paymenthistory, shippinghistory, cart};
       
        for (Button button : allButtons) {
            button.setStyle("-fx-background-color: #376485; -fx-font-family: 'Montserrat'; -fx-font-weight: normal;");
        }
       
        // Set active button to bold
        activeButton.setStyle("-fx-background-color: #376485; -fx-font-family: 'Montserrat'; -fx-font-weight: bold;");
    }
 
    @FXML
    private void users(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminuser.fxml"));
        borderpane.setCenter(view);
        setActiveButton(users);
    }
 
    @FXML
    private void categories(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/admincategory.fxml"));
        borderpane.setCenter(view);
        setActiveButton(categories);
    }
 
    @FXML
    private void products(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminproduct.fxml"));
        borderpane.setCenter(view);
        setActiveButton(products);
    }
 
    @FXML
    private void orderhistory(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminorder.fxml"));
        borderpane.setCenter(view);
        setActiveButton(orderhistory);
    }
 
    @FXML
    private void paymenthistory(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminpayment.fxml"));
        borderpane.setCenter(view);
        setActiveButton(paymenthistory);
    }
 
    @FXML
    private void shippinghistory(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/adminshipping.fxml"));
        borderpane.setCenter(view);
        setActiveButton(shippinghistory);
    }
 
    @FXML
    private void cart(ActionEvent event) throws IOException{
        AnchorPane view = FXMLLoader.load(getClass().getResource("/AdminUI/admincart.fxml"));
        borderpane.setCenter(view);
        setActiveButton(cart);
    }
 
    @FXML
    public void back(ActionEvent event) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Log Out");
    alert.setHeaderText(null);
    alert.setContentText("Do you want to log out?");
 
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        Parent root = FXMLLoader.load(getClass().getResource("/AdminUI/adminlogin.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
 
 
}