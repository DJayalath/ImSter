import javafx.application.Platform;
import javafx.scene.Parent;

/* Class for abstract JavaFX views */
public abstract class View {

    /* Handles fatal exceptions within Application thread */
    protected void handleFatalException(Throwable e) {
        System.out.println("FATAL ERROR: " + e.getMessage());
        System.out.println("\nINIT CAUSE: " + e.getCause().getMessage());
        System.out.println("\n----- STACK TRACE -----");
        e.printStackTrace();
        Platform.exit();
        System.exit(1);
    }

    /* Returns root node of view */
    public abstract Parent getRoot();

}
