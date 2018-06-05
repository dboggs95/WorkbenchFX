package com.dlsc.workbenchfx.view.controls.module;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.module.Module;
import java.util.LinkedHashSet;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the standard control used to display {@link Page}s with {@link Module}s in the home
 * screen.
 *
 * @author François Martin
 * @author Marco Sanfratello
 */
public class Page extends Control {
  private static final Logger LOGGER = LogManager.getLogger(Page.class.getName());
  private final Workbench workbench;
  private final ObservableList<Module> modules;
  private final IntegerProperty pageIndex;
  private final ObservableSet<Tile> tiles; // TODO: Set

  /**
   * Constructs a new {@link Tab}.
   *
   * @param workbench which created this {@link Tab}
   */
  public Page(Workbench workbench) {
    this.workbench = workbench;
    pageIndex = new SimpleIntegerProperty();
    modules = workbench.getModules();
    tiles = FXCollections.observableSet(new LinkedHashSet<>());
    setupChangeListeners();
    updateTiles();
  }

  private void setupChangeListeners() {
    // update tiles list whenever modules or the pageIndex of this page have changed
    InvalidationListener modulesChangedListener = observable -> updateTiles();
    modules.addListener(modulesChangedListener);
    pageIndex.addListener(modulesChangedListener);
  }

  // TODO: test this method
  private void updateTiles() {
    // remove any preexisting tiles in the list
    tiles.clear();
    int position = getPageIndex() * workbench.getModulesPerPage();
    modules.stream()
        .skip(position) // skip all tiles from previous pages
        .limit(workbench.getModulesPerPage()) // only take as many tiles as there are per page
        .map(workbench::getTile)
        .map(Tile.class::cast)
        .forEachOrdered(tiles::add);
  }

  public int getPageIndex() {
    return pageIndex.get();
  }

  /**
   * Defines the {@code pageIndex} of this {@link Page}.
   *
   * @param pageIndex to be represented by this {@link Page}
   */
  public final void setPageIndex(int pageIndex) {
    this.pageIndex.set(pageIndex);
  }

  public ReadOnlyIntegerProperty pageIndexProperty() {
    return pageIndex;
  }

  public ObservableSet<Tile> getTiles() {
    return FXCollections.unmodifiableObservableSet(tiles);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new PageSkin(this);
  }
}
