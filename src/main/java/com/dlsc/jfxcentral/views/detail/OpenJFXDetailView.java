package com.dlsc.jfxcentral.views.detail;

import com.dlsc.gemsfx.FilterView;
import com.dlsc.jfxcentral.JFXCentralApp;
import com.dlsc.jfxcentral.data.DataRepository;
import com.dlsc.jfxcentral.data.pull.PullRequest;
import com.dlsc.jfxcentral.panels.SectionPane;
import com.dlsc.jfxcentral.panels.SectionPaneWithFilterView;
import com.dlsc.jfxcentral.util.EmptySelectionModel;
import com.dlsc.jfxcentral.util.FilterUtil;
import com.dlsc.jfxcentral.views.AdvancedListView;
import com.dlsc.jfxcentral.views.MarkdownView;
import com.dlsc.jfxcentral.views.RootPane;
import com.dlsc.jfxcentral.views.View;
import com.dlsc.jfxcentral.views.detail.cells.DetailPullRequestCell;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OpenJFXDetailView extends DetailView {

    private final FilterView.FilterGroup<PullRequest> stateGroup = new FilterView.FilterGroup<>("State");
    private final FilterView.FilterGroup<PullRequest> labelGroup = new FilterView.FilterGroup<>("Label");
    private final FilterView.FilterGroup<PullRequest> userGroup = new FilterView.FilterGroup<>("User");
    private final FilterView.FilterGroup<PullRequest> timeGroup = new FilterView.FilterGroup<>("Time");

    private VBox content = new VBox();

    // static, shared across UI instances
    private static final ObservableList<PullRequest> pullRequests = FXCollections.observableArrayList();

    private static ZonedDateTime pullRequestUpdateTime;
    private FilterView<PullRequest> filterView;

    public OpenJFXDetailView(RootPane rootPane) {
        super(rootPane, View.OPENJFX);

        getStyleClass().add("openjfx-detail-view");

        createHeader();
        createPullRequests();

        content.getChildren().add(new Region());

        setContent(content);
        pullRequests.addListener((Observable it) -> updateFilters());

        // using static update time field as this will be for shared clients on the web server
        if (pullRequestUpdateTime == null || Duration.between(pullRequestUpdateTime, ZonedDateTime.now()).toHours() > 3) {
            System.out.println("LOADING");
            Thread thread = new Thread(() -> {
                List<PullRequest> result = DataRepository.getInstance().loadPullRequests();
                Platform.runLater(() -> pullRequests.setAll(result));
            });
            thread.setDaemon(true);
            thread.setName("OpenJFX PR Thread");
            thread.start();

            pullRequestUpdateTime = ZonedDateTime.now();
        }
    }

    private void updateFilters() {
        stateGroup.getFilters().clear();
        labelGroup.getFilters().clear();
        userGroup.getFilters().clear();
        timeGroup.getFilters().clear();

        updateStateGroup();
        updateLabelGroup();
        updateUserGroup();

        timeGroup.getFilters().clear();

        FilterUtil.createFilters(timeGroup, "Date", pr -> DateTimeFormatter.ISO_DATE_TIME.parse(pr.getUpdatedAt() != null ? pr.getUpdatedAt() : pr.getCreatedAt(), ZonedDateTime::from));

        // need to make this call here ... timing issue, otherwise menu items do not show up
        // in menu dropdowns
        filterView.getFilterGroups().setAll(stateGroup, labelGroup, userGroup, timeGroup);
    }

    private void updateUserGroup() {
        List<String> userList = new ArrayList<>();

        pullRequests.forEach(pr -> {
            String id = pr.getUser().getLogin();
            if (!userList.contains(id.trim())) {
                userList.add(id.trim());
            }
        });

        List<FilterView.Filter<PullRequest>> filters = new ArrayList<>();

        userList.forEach(item -> filters.add(new FilterView.Filter<>(item) {
            @Override
            public boolean test(PullRequest pr) {
                return pr.getUser().getLogin().equals(item);
            }
        }));

        filters.sort(Comparator.comparing(x -> x.getName().toLowerCase()));
        userGroup.getFilters().setAll(filters);
    }

    private void updateStateGroup() {
        List<String> stateList = new ArrayList<>();
        stateList.add("open");
        stateList.add("closed");

        List<FilterView.Filter<PullRequest>> filters = new ArrayList<>();

        stateList.forEach(item -> filters.add(new FilterView.Filter<>(item) {
            @Override
            public boolean test(PullRequest pr) {
                return pr.getState().equals(item);
            }
        }));

        stateGroup.getFilters().setAll(filters);
    }

    private void updateLabelGroup() {
        List<String> labels = new ArrayList<>();

        pullRequests.forEach(pr -> {
            pr.getLabels().forEach(label -> {
                if (!labels.contains(label.getName())) {
                    labels.add(label.getName());
                }
            });
        });

        List<FilterView.Filter<PullRequest>> filters = new ArrayList<>();

        labels.forEach(item -> filters.add(new FilterView.Filter<>(item) {
            @Override
            public boolean test(PullRequest pr) {
                return pr.getLabels().stream().anyMatch(label -> label.getName().equals(item));
            }
        }));

        filters.sort(Comparator.comparing(x -> x.getName().toLowerCase()));
        labelGroup.getFilters().setAll(filters);
    }

    private void createHeader() {
        ImageView logo = new ImageView(JFXCentralApp.class.getResource("javafx-logo.png").toExternalForm());
        logo.setFitWidth(300);
        logo.setFitHeight(60);
        logo.setPreserveRatio(true);

        SectionPane sectionPane = new SectionPane();
        sectionPane.setTitle("OpenJFX");
        sectionPane.setSubtitle("The open source project behind JavaFX");
        sectionPane.setExtras(logo);

        MarkdownView markdownView = new MarkdownView();
        markdownView.mdStringProperty().bind(DataRepository.getInstance().openJFXTextProperty());
        sectionPane.getNodes().add(markdownView);

        content.getChildren().add(sectionPane);
    }

    private void createPullRequests() {
        SectionPaneWithFilterView<PullRequest> sectionPane = new SectionPaneWithFilterView();
        sectionPane.setTitle("Pull Requests");

        filterView = sectionPane.getFilterView();
        Bindings.bindContent(filterView.getItems(), pullRequests);

        filterView.setTextFilterProvider(text -> pullRequest -> {
            if (pullRequest.getTitle().toLowerCase().contains(text)) {
                return true;
            }

            if (pullRequest.getBody().toLowerCase().contains(text)) {
                return true;
            }

            if (StringUtils.containsIgnoreCase(pullRequest.getUser().getLogin(), text)) {
                return true;
            }

            return false;
        });

        AdvancedListView<PullRequest> listView = new AdvancedListView<>();
        listView.setPlaceholder(new Label("Loading pull requests from GitHub ..."));
        listView.setPaging(true);
        listView.setVisibleRowCount(8);
        listView.getListView().setSelectionModel(new EmptySelectionModel<>());
        listView.setCellFactory(view -> new DetailPullRequestCell(getRootPane()));
        listView.setItems(filterView.getFilteredItems());

        sectionPane.getNodes().add(listView);

        VBox.setVgrow(listView, Priority.ALWAYS);

        content.getChildren().add(sectionPane);
    }
}
