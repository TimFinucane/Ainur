package visualisation.themes.colour_schemes;

import visualisation.themes.Theme;

/**
 * A Grey Blue Theme.
 */
public class GreyBlueTheme implements Theme {
    @Override
    public String getPrimary() {
        return "#304154";
    }

    @Override
    public String getSecondary() {
        return "#dcf3fb";
    }

    @Override
    public String getLightBlack() {
        return "#1b2734";
    }

    @Override
    public String getGreen() {
        return "#00ff91";
    }

    @Override
    public String getNodeHighlight() {
        return "#FF2400";
    }

    @Override
    public String getWhite() {
        return "#fafafa";
    }

    @Override
    public String getTextFill() {
        return this.getWhite();
    }

    @Override
    public String getDarkBlack() {
        return "#484848";
    }

    @Override
    public String getLighterBlack() {
        return "#6d6d6d";
    }

    @Override
    public String getRoot() {
        return this.getDarkBlack();
    }

    @Override
    public String getCss() {
        return COLOUR_DIR + "GreyBlueTheme.css";
    }
}
