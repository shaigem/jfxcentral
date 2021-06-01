package com.dlsc.jfxcentral.views.master;

import com.dlsc.jfxcentral.data.DataRepository;
import com.dlsc.jfxcentral.data.ImageManager;
import com.dlsc.jfxcentral.model.Tool;
import com.dlsc.jfxcentral.views.AdvancedListCell;
import com.dlsc.jfxcentral.views.MarkdownView;
import com.dlsc.jfxcentral.views.RootPane;
import com.dlsc.jfxcentral.views.View;
import javafx.beans.Observable;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class ToolsMasterView extends MasterView<Tool> {

    private ListView<Tool> listView = new ListView<>();

    public ToolsMasterView(RootPane rootPane) {
        super(rootPane, View.TOOLS);

        getStyleClass().add("tools-view");

        listView.setMinWidth(Region.USE_PREF_SIZE);
        listView.setCellFactory(view -> new ToolListCell());
        listView.setItems(createSortedAndFilteredList(DataRepository.getInstance().toolsProperty(),
                Comparator.comparing(Tool::getName),
                tool -> StringUtils.isBlank(getFilterText()) || StringUtils.containsIgnoreCase(tool.getName(), getFilterText()) || StringUtils.containsIgnoreCase(tool.getSummary(), getFilterText())));
        listView.getItems().addListener((Observable it) -> performDefaultSelection());

        bindListViewToSelectedItem(listView);

        setCenter(listView);

        performDefaultSelection();
    }

    private void performDefaultSelection() {
        if (!listView.getItems().isEmpty()) {
            listView.getSelectionModel().select(0);
        } else {
            listView.getSelectionModel().clearSelection();
        }
    }

    class ToolListCell extends AdvancedListCell<Tool> {

        private final Label nameLabel = new Label();
        private final ImageView imageView = new ImageView();
        private final MarkdownView markdownView = new MarkdownView();
        private final GridPane gridPane;

        public ToolListCell() {
            getStyleClass().add("tool-list-cell");

            setPrefWidth(0);

            imageView.visibleProperty().bind(imageView.imageProperty().isNotNull());
            imageView.managedProperty().bind(imageView.imageProperty().isNotNull());
            imageView.setFitHeight(40);
            imageView.setFitWidth(60);
            imageView.setPreserveRatio(true);

            nameLabel.getStyleClass().add("name-label");
            nameLabel.setMinWidth(0);

            gridPane = new GridPane();
            gridPane.getStyleClass().add("grid-pane");
            gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            gridPane.add(nameLabel, 0, 0);
            gridPane.add(markdownView, 0, 1);
            gridPane.add(imageView, 1, 0);

            GridPane.setRowSpan(imageView, 2);

            GridPane.setHgrow(nameLabel, Priority.ALWAYS);
            GridPane.setHgrow(markdownView, Priority.ALWAYS);

            GridPane.setValignment(nameLabel, VPos.TOP);
            GridPane.setValignment(markdownView, VPos.TOP);
            GridPane.setValignment(imageView, VPos.TOP);

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setGraphic(gridPane);

            gridPane.visibleProperty().bind(emptyProperty().not());
        }

        @Override
        protected void updateItem(Tool tool, boolean empty) {
            super.updateItem(tool, empty);

            if (!empty && tool != null) {
                nameLabel.setText(tool.getName());
                imageView.imageProperty().bind(ImageManager.getInstance().toolImageProperty(tool));
                markdownView.setMdString(tool.getSummary());

                this.setMouseTransparent(true);
                setCellLink(gridPane, tool, this.getChildren());
            }
        }
    }
}