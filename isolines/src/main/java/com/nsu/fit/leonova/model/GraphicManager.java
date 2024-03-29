package com.nsu.fit.leonova.model;

import com.nsu.fit.leonova.model.graphicProvider.GraphicValues;

import java.awt.*;

public interface GraphicManager {
    void setColorsRGB(SafeColor[] colorsRGB);
    void setDefinitionArea(GraphicValues graphicValues);

    void createGraphic(boolean gradient, int width, int height);
    void createLegend(boolean gradient, int width, int height);

    void drawNet();
    void pivotPoints();
    void pixelToCoordinate(Point pixel);
}
