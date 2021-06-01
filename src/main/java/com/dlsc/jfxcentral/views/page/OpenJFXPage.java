package com.dlsc.jfxcentral.views.page;

import com.dlsc.jfxcentral.views.RootPane;
import com.dlsc.jfxcentral.views.View;
import com.dlsc.jfxcentral.views.detail.DetailView;
import com.dlsc.jfxcentral.views.detail.OpenJFXDetailView;

public class OpenJFXPage extends Page<Object> {

    public OpenJFXPage(RootPane rootPane) {
        super(rootPane, View.OPENJFX);
    }

    @Override
    protected DetailView createDetailView() {
        return new OpenJFXDetailView(getRootPane());
    }
}