package visualisation.themes.colour_schemes;

import visualisation.themes.Theme;

public class PurpleOrangeTheme implements Theme {
    @Override
    public String getPrimary() {
        return "#b8a2ff";
    }

    @Override
    public String getSecondary() {
        return "#ffdba2";
    }

    @Override
    public String getLightBlack() {
        return "#484848";
    }

    @Override
    public String getGreen() {
        return "#a2ffcd";
    }

    @Override
    public String getNodeHighlight() {
        return this.getSecondary();
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
        return this.getWhite();
    }

    @Override
    public String getCss() {
        return COLOUR_DIR + "PurpleOrangeTheme.css";
    }
}
