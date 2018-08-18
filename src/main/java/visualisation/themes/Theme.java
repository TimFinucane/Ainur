package visualisation.themes;

/**
 * This interface is used for themeing Ainur's visualisers.
 * Overwrite each of these methods to return a string hexcode.
 * The getCss method should reference a CSS file with the same scheme.
 * See the ThemeTemplate.txt file in resources/style/colours for more info on this.
 */

public interface Theme {
    String COLOUR_DIR = "/style/colours/";

    String getPrimary();

    String getSecondary();

    String getLightBlack();

    String getGreen();

    String getNodeHighlight();

    String getWhite();

    String getTextFill();

    String getDarkBlack();

    String getLighterBlack();

    String getRoot();

    String getCss();
}
