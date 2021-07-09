package com.dlsc.jfxcentral.views.detail.cells;

import com.dlsc.jfxcentral.data.model.Tip;
import com.dlsc.jfxcentral.views.RootPane;
import javafx.scene.control.ContentDisplay;

public class DetailTipCell extends DetailCell<Tip> {

    private final RootPane rootPane;
    private final ResponsiveBox responsiveBox;

    public DetailTipCell(RootPane rootPane, boolean largeImage) {
        this.rootPane = rootPane;

        getStyleClass().add("detail-tip-cell");

        setPrefWidth(0);

        responsiveBox = new ResponsiveBox(rootPane.isMobile() ? ResponsiveBox.ImageLocation.BANNER : largeImage ? ResponsiveBox.ImageLocation.LARGE_ON_SIDE : ResponsiveBox.ImageLocation.SMALL_ON_SIDE);
        responsiveBox.visibleProperty().bind(itemProperty().isNotNull());
        responsiveBox.setLargeImageWidth(200);
        responsiveBox.setLargeImageHeight(100);

        setGraphic(responsiveBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    protected void updateItem(Tip tip, boolean empty) {
        super.updateItem(tip, empty);

        if (!empty && tip != null) {
            responsiveBox.setTitle(tip.getName());
            responsiveBox.setDescription(tip.getSummary());
        }
    }
}