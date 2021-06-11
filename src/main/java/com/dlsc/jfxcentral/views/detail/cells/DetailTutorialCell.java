package com.dlsc.jfxcentral.views.detail.cells;

import com.dlsc.gemsfx.DialogPane;
import com.dlsc.jfxcentral.data.DataRepository;
import com.dlsc.jfxcentral.data.ImageManager;
import com.dlsc.jfxcentral.data.model.Tutorial;
import com.dlsc.jfxcentral.util.Util;
import com.dlsc.jfxcentral.views.MarkdownView;
import com.dlsc.jfxcentral.views.RootPane;
import com.dlsc.jfxcentral.views.page.StandardIcons;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class DetailTutorialCell extends DetailCell<Tutorial> {

    private final Label titleLabel = new Label();
    private final MarkdownView descriptionMarkdownView = new MarkdownView();
    private final ImageView thumbnailView = new ImageView();
    private final Button visitButton = new Button("Visit Tutorial");
    private final Label commercialLabel = new Label("$$$");
    private final RootPane rootPane;

    public DetailTutorialCell(RootPane rootPane, boolean largeImage) {
        this.rootPane = rootPane;

        getStyleClass().add("detail-tutorial-cell");

        setPrefWidth(0);

        commercialLabel.getStyleClass().add("commercial-label");

        visitButton.setGraphic(new FontIcon(StandardIcons.TUTORIAL));

        titleLabel.getStyleClass().addAll("header3", "title-label");
        titleLabel.setWrapText(true);
        titleLabel.setMinHeight(Region.USE_PREF_SIZE);
        titleLabel.setGraphic(commercialLabel);

        descriptionMarkdownView.getStyleClass().add("description-label");

        thumbnailView.setFitWidth(largeImage ? 320 : 160);
        thumbnailView.setPreserveRatio(true);

        StackPane thumbnailWrapper = new StackPane(thumbnailView);
        thumbnailWrapper.getStyleClass().add("thumbnail-wrapper");
        thumbnailWrapper.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(thumbnailView, Pos.TOP_LEFT);

        HBox buttonBox = new HBox(10, visitButton);
        buttonBox.setMinHeight(Region.USE_PREF_SIZE);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox vBox = new VBox(titleLabel, descriptionMarkdownView, buttonBox);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setFillWidth(true);
        vBox.getStyleClass().add("vbox");

        HBox.setHgrow(vBox, Priority.ALWAYS);

        HBox hBox = new HBox(vBox, thumbnailWrapper);
        hBox.getStyleClass().add("hbox");
        hBox.setAlignment(Pos.TOP_LEFT);

        setGraphic(hBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        hBox.visibleProperty().bind(itemProperty().isNotNull());

        setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                showLargeImage(getItem());
            }
        });
    }

    private void showLargeImage(Tutorial tutorial) {
        ImageView largeImageView = new ImageView();
        largeImageView.setFitWidth(800);
        largeImageView.setPreserveRatio(true);
        largeImageView.imageProperty().bind(ImageManager.getInstance().tutorialImageLargeProperty(tutorial));
        rootPane.getDialogPane().showNode(DialogPane.Type.BLANK, "Title", largeImageView);
    }

    @Override
    protected void updateItem(Tutorial tutorial, boolean empty) {
        super.updateItem(tutorial, empty);

        if (!empty && tutorial != null) {
            titleLabel.setText(tutorial.getName());

            commercialLabel.setVisible(tutorial.isCommercial());

            descriptionMarkdownView.setBaseURL(DataRepository.BASE_URL + "tutorials/" + tutorial.getId());
            descriptionMarkdownView.mdStringProperty().bind(DataRepository.getInstance().tutorialTextProperty(tutorial));

            thumbnailView.setVisible(true);
            thumbnailView.setManaged(true);
            thumbnailView.imageProperty().bind(ImageManager.getInstance().tutorialImageProperty(tutorial));

            Util.setLink(visitButton, tutorial.getUrl(), tutorial.getName());
        }
    }
}
