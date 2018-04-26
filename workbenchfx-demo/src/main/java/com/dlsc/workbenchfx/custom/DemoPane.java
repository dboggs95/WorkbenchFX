package com.dlsc.workbenchfx.custom;

import static com.dlsc.workbenchfx.WorkbenchFx.STYLE_CLASS_ACTIVE_TAB;
import static impl.org.controlsfx.ReflectionUtils.addUserAgentStylesheet;

import com.dlsc.workbenchfx.WorkbenchFx;
import com.dlsc.workbenchfx.custom.calendar.CalendarModule;
import com.dlsc.workbenchfx.custom.notes.NotesModule;
import com.dlsc.workbenchfx.custom.preferences.PreferencesModule;
import com.dlsc.workbenchfx.module.Module;
import com.dlsc.workbenchfx.view.controls.Dropdown;
import com.dlsc.workbenchfx.view.module.TabControl;
import com.dlsc.workbenchfx.view.module.TileControl;

import java.util.function.BiFunction;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoPane extends StackPane {

  private static final Logger LOGGER = LogManager.getLogger(DemoPane.class.getName());
  public WorkbenchFx workbenchFx;

  BiFunction<WorkbenchFx, Module, Node> tabFactory = (workbench, module) -> {
    TabControl tabControl = new TabControl(module);
    workbench.activeModuleProperty().addListener((observable, oldValue, newValue) -> {
      LOGGER.trace("Tab Factory - Old Module: " + oldValue);
      LOGGER.trace("Tab Factory - New Module: " + oldValue);
      if (module == newValue) {
        tabControl.getStyleClass().add(STYLE_CLASS_ACTIVE_TAB);
        LOGGER.error("STYLE SET");
      }
      if (module == oldValue) {
        // switch from this to other tab
        tabControl.getStyleClass().remove(STYLE_CLASS_ACTIVE_TAB);
      }
    });
    tabControl.setOnClose(e -> workbench.closeModule(module));
    tabControl.setOnActive(e -> workbench.openModule(module));
    tabControl.getStyleClass().add(STYLE_CLASS_ACTIVE_TAB);
    System.out.println("This tab was proudly created by SteffiFX");
    return tabControl;
  };

  BiFunction<WorkbenchFx, Module, Node> tileFactory = (workbench, module) -> {
    TileControl tileControl = new TileControl(module);
    tileControl.setOnActive(e -> workbench.openModule(module));
    System.out.println("This tile was proudly created by SteffiFX");
    return tileControl;
  };

  BiFunction<WorkbenchFx, Integer, Node> pageFactory = (workbench, pageIndex) -> {
    final int COLUMNS_PER_ROW = 2;

    GridPane gridPane = new GridPane();
    gridPane.getStyleClass().add("tilePage");

    int position = pageIndex * workbench.modulesPerPage;
    int count = 0;
    int column = 0;
    int row = 0;

    while (count < workbench.modulesPerPage && position < workbench.getModules().size()) {
      Module module = workbench.getModules().get(position);
      Node tile = workbench.getTile(module);
      gridPane.add(tile, column, row);

      position++;
      count++;
      column++;

      if (column == COLUMNS_PER_ROW) {
        column = 0;
        row++;
      }
    }
    return gridPane;
  };

  public DemoPane() {

    workbenchFx = WorkbenchFx.builder(
        new CalendarModule(),
        new NotesModule(),
        new PreferencesModule()
    )
        .toolBarControls(
            new Button("", new FontAwesomeIconView(FontAwesomeIcon.HOME)),
            Dropdown.of(
                "ImageView",
                new ImageView("com/dlsc/workbenchfx/user_light.png"),
                new CustomMenuItem(new Label("Content 1")),
                new CustomMenuItem(new Label("Content 2"))
            ),
            Dropdown.of(
                "FAIconView",
                new FontAwesomeIconView(FontAwesomeIcon.ADDRESS_BOOK),
                new Menu(
                    "Submenus", new FontAwesomeIconView(FontAwesomeIcon.PLUS),
                    new MenuItem("Submenu 1"),
                    new CustomMenuItem(new Label("CustomMenuItem"), false)
                )
            )
        )
        .modulesPerPage(2)
        .tabFactory(tabFactory)
        .tileFactory(tileFactory)
        .pageFactory(pageFactory)
        .build();

    addUserAgentStylesheet("com/dlsc/workbenchfx/customTheme.css");
    getChildren().add(workbenchFx);
  }
}
