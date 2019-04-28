package com.nsu.fit.leonova.model;

import com.nsu.fit.leonova.controller.WorldController;
import com.nsu.fit.leonova.globals.Globals;
import com.nsu.fit.leonova.model.bspline.SplineParameters;
import com.nsu.fit.leonova.observer.BSplineObservable;
import com.nsu.fit.leonova.observer.BSplineObserver;
import com.nsu.fit.leonova.observer.WorldObservable;
import com.nsu.fit.leonova.observer.WorldObserver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class World3DImpl implements World3D, WorldObservable, BSplineObservable {
    private List<WorldObserver> worldObservers = new ArrayList<>();
    private List<BSplineObserver> bSplineObservers = new ArrayList<>();
    private List<Figure> figures = new ArrayList<>();
    private Figure currentWorkingFigure;

    @Override
    public void showSpline3D() {
        BufferedImage image = new BufferedImage(Globals.IMAGE_WIDTH, Globals.IMAGE_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = image.createGraphics();

        for(Figure figure : figures){
            Point[][] splinePoints2D = figure.getSplinePoints2D();
            graphics.setPaint(figure.getParameters().getColor());

            for(int i = 1; i < figure.getPointsToRotateCount(); ++i){
                for(int j = 0; j < figure.getParameters().getM(); j++){
                    try{
                        graphics.drawLine(splinePoints2D[i - 1][j].x, splinePoints2D[i - 1][j].y, splinePoints2D[i][j].x, splinePoints2D[i][j].y);
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("!!!");
                        throw e;
                    }
                }
            }

            for(int i = 0; i < figure.getPointsToRotateCount(); i += figure.getParameters().getK()){
                for(int j = 1; j < figure.getParameters().getM(); j++){
                    graphics.drawLine(splinePoints2D[i][j - 1].x, splinePoints2D[i][j - 1].y, splinePoints2D[i][j].x, splinePoints2D[i][j].y);
                }
            }
        }

        for(WorldObserver worldObserver : worldObservers){
            worldObserver.setMainImage(image);
        }
    }

    @Override
    public void rotationForOX(int shift) {
        double d = Math.toRadians(shift * 0.5);
        currentWorkingFigure.rotateForOX(d);
        showSpline3D();
    }

    @Override
    public void rotationForOY(int shift) {
        double d = Math.toRadians(shift * 0.5);
        currentWorkingFigure.rotateForOY(d);
        showSpline3D();
    }

    @Override
    public void addPointToCurrentBSpline(Point point) {
        currentWorkingFigure.addPoint(point);
    }

    @Override
    public void removePointFromCurrentBSpline(Point point) {
        currentWorkingFigure.removePoint(point);
    }

    @Override
    public void pressedPointOnCurrentBSpline(Point point) {
        currentWorkingFigure.pressedPoint(point);
    }

    @Override
    public void draggedPointOnCurrentBSpline(Point point) {
        currentWorkingFigure.draggedPoint(point);
    }

    @Override
    public void showBSplineInfo(int index) {
        if (figures.size() <= index){
            throw new IllegalArgumentException("Impossible! Size: " + figures.size() + " Index: " + index);
        }
        currentWorkingFigure = figures.get(index);
        figures.get(index).showBspline();
    }

    @Override
    public void addSpline() {
        Figure figure = new Figure(bSplineObservers, figures.size());
        figures.add(figure);
        figure.showBspline();
        currentWorkingFigure = figure;
        for(BSplineObserver obs : bSplineObservers){
            obs.addSpline(figure.getParameters().getSplineName());
        }
        for(WorldObserver obs : worldObservers){
            obs.addFigure(figure.getParameters().getSplineName());
        }
    }

    @Override
    public void removeSpline(int index) {
        figures.remove(index);
        for(BSplineObserver obs : bSplineObservers){
            obs.removeSpline(index);
        }
        for(WorldObserver obs : worldObservers){
            obs.removeFigure(index);
        }
        showBSplineInfo(figures.size() - 1);
    }

    @Override
    public void setSplineParameters(SplineParameters parameters) {
        currentWorkingFigure.setParameters(parameters);
        for(BSplineObserver obs : bSplineObservers){
            obs.changeFigureName(parameters.getSplineName(), figures.indexOf(currentWorkingFigure));
        }
        for(WorldObserver obs : worldObservers){
            obs.renameFigure(parameters.getSplineName(), figures.indexOf(currentWorkingFigure));
        }
    }

    @Override
    public void setFigureCenter(Point3D figureCenter) {
        currentWorkingFigure.shift(figureCenter);
        showSpline3D();
    }

    @Override
    public void setSelectedFigure(int index) {
        currentWorkingFigure = figures.get(index);
        Point3D center = new Point3D(currentWorkingFigure.getShiftMatrix().get(0, 3),
                currentWorkingFigure.getShiftMatrix().get(1, 3),
                currentWorkingFigure.getShiftMatrix().get(2, 3));
        for(WorldObserver obs : worldObservers){
            obs.setInfo(center);
        }
    }

    @Override
    public void addObserver(WorldObserver obs) {
        worldObservers.add(obs);
    }

    @Override
    public void removeObserver(WorldObserver obs) {
        worldObservers.remove(obs);
    }

    @Override
    public void addObserver(BSplineObserver obs) {
        bSplineObservers.add(obs);
        for(Figure figure : figures){
            figure.addObserver(obs);
        }
    }

    @Override
    public void removeObserver(BSplineObserver obs) {
        bSplineObservers.remove(obs);
        for(Figure figure : figures){
            figure.removeObserver(obs);
        }
    }
}
