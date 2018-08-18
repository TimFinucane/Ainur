package common;

import javafx.scene.Scene;
import javafx.stage.Stage;
import visualisation.themes.Theme;
import visualisation.themes.colour_schemes.GreyBlueTheme;

/**
 * Class fill of MACROS for configuration.
 */
public class Config {
    // Theme
    // Change the theme implementation to change the entire theme of the GUI :)
    private static Theme _theme = new GreyBlueTheme();

    public static final String MAIN_STYLE_SHEET = "/style/Ainur.css";
    public static final String APP_ICON = "/img/icon.png";

    // App Name
    public static final String APP_NAME = "Ainur";

    // Default values
    public static final boolean VISUALISE_DEFAULT = false;
    public static final int CORES_DEFAULT = 1;
    public static final int PROCESSORS_DEFAULT = 1;

    // UI Colours
    public static final String UI_PRIMARY_COLOUR = _theme.getPrimary();
    public static final String UI_SECONDARY_COLOR = _theme.getSecondary();
    public static final String UI_LIGHT_BLACK_COLOUR = _theme.getLightBlack();
    public static final String UI_GREEN_COLOUR = _theme.getGreen();
    public static final String UI_WHITE_COLOUR = _theme.getWhite();
    public static final String UI_NODE_HIGHLIGHT_COLOUR = _theme.getNodeHighlight();
    public static final String UI_DARK_BLACK_COLOUR = _theme.getDarkBlack();
    public static final String UI_TEXT_COLOUR = _theme.getTextFill();
    public static final String UI_LIGHTER_BLACK_COLOUR = _theme.getLighterBlack();
    public static final String UI_ROOT_COLOUR = _theme.getRoot();

    /**
     * Static method used to change the current theme of Ainur.
     * Can be done at run time if you so wish.
     * Currently private as don't want to expose this functionality.
     * If you want to set the default theme change the _theme field.
     *
     * @param stage The stage to alter the theme of.
     * @param theme The new theme to use.
     */
    private static void setTheme(Stage stage, Theme theme) {
        _theme = theme;
        Scene scene = stage.getScene();

        scene.getStylesheets().clear();

        scene.getStylesheets().add(Config.class.getResource(_theme.getCss()).toExternalForm());
        scene.getStylesheets().add(Config.class.getResource(MAIN_STYLE_SHEET).toExternalForm());
    }

    /**
     * Applies the current theme to a stage.
     *
     * @param stage The stage the apply the current theme to.
     */
    public static void setTheme(Stage stage) {
        Config.setTheme(stage, _theme);
    }
}
