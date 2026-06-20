package app.views;

import java.util.function.BiConsumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {

    private final BiConsumer<String, String> loginHandler;

    public LoginView(BiConsumer<String, String> loginHandler) {
        this.loginHandler = loginHandler;
    }

    public VBox crear() {
        Label marca = new Label("eBarrio");
        marca.getStyleClass().add("login-brand");

        Label titulo = new Label("Iniciar sesion");
        titulo.getStyleClass().add("login-title");

        TextField email = new TextField();
        email.setPromptText("Email");
        email.setPrefWidth(360);

        PasswordField password = new PasswordField();
        password.setPromptText("Clave");
        password.getStyleClass().add("text-field");
        password.setPrefWidth(360);

        Label ayuda = new Label("Ingresa con tu email y clave para continuar");
        ayuda.getStyleClass().add("login-help");

        Button ingresar = new Button("Ingresar");
        ingresar.getStyleClass().add("primary-button");
        ingresar.setMaxWidth(Double.MAX_VALUE);
        ingresar.setOnAction(e -> loginHandler.accept(email.getText(), password.getText()));

        VBox form = new VBox(14, marca, titulo, email, password, ingresar, ayuda);
        form.getStyleClass().add("login-card");
        form.setAlignment(Pos.CENTER_LEFT);
        form.setMaxWidth(430);

        VBox wrapper = new VBox(form);
        wrapper.getStyleClass().add("login-page");
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }
}
