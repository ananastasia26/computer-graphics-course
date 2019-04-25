package com.nsu.fit.leonova.model;

import com.nsu.fit.leonova.globals.Globals;
import com.nsu.fit.leonova.model.bspline.BSpline;
import com.nsu.fit.leonova.model.bspline.SplineParameters;
import com.nsu.fit.leonova.observer.BSplineObservable;
import com.nsu.fit.leonova.observer.BSplineObserver;
import org.ejml.simple.SimpleMatrix;

import java.awt.*;
import java.util.List;

public class Figure implements BSplineObservable {
    private BSpline bSpline;
    private SplineParameters parameters;
    private SimpleMatrix rotationMatrix = MatrixGenerator.identity4();
    //TODO: think about this matrix
    private SimpleMatrix shiftMatrix;

    private Point3D splinePoints3D[][];
    private boolean isActualSplinePoints3D = false;
    private Point splinePoints2D[][];
    private boolean isActualSplinePoints2D = false;

    public Figure(List<BSplineObserver> obs, int index) {
        parameters = Globals.SPLINE_PARAMETERS;
        parameters.setSplineName("Figure #" + index);
        bSpline = new BSpline(this.parameters);
        for(BSplineObserver o : obs){
            bSpline.addObserver(o);
        }
    }

    public Point[][] getSplinePoints2D(){
        if(!isActualSplinePoints2D){
            if(!isActualSplinePoints3D){
                countBSpline3D();
            }
            useTransformation();
        }
        return splinePoints2D;
    }

//    public List<Point> getPointsToRotate() {
//        return bSpline.getPointsToRotate();
//    }

    public void showBspline() {
        bSpline.showBspline();
    }

    public void addPoint(Point point) {
        bSpline.addPoint(point);
        isActualSplinePoints3D = false;
    }

    public void removePoint(Point point) {
        bSpline.removePoint(point);
        isActualSplinePoints3D = false;
    }

    public void pressedPoint(Point point) {
        bSpline.pressedPoint(point);
        isActualSplinePoints3D = false;
    }

    public void draggedPoint(Point point) {
        bSpline.draggedPoint(point);
        isActualSplinePoints3D = false;
    }

    public void setParameters(SplineParameters parameters) {
        this.parameters = parameters;
    }

    public SplineParameters getParameters() {
        return parameters;
    }

    public SimpleMatrix getRotationMatrix() {
        return rotationMatrix;
    }

    public SimpleMatrix getShiftMatrix() {
        return shiftMatrix;
    }

    public int getPointsToRotateCount(){
        return bSpline.getPointsToRotate().size();
    }

    public void rotateForOX(double angle){
        rotationMatrix = MatrixGenerator.rotationMatrix4OX(angle).mult(rotationMatrix);
        isActualSplinePoints2D = false;
    }

    public void rotateForOY(double angle){
        rotationMatrix = MatrixGenerator.rotationMatrix4OY(angle).mult(rotationMatrix);
        isActualSplinePoints2D = false;
    }

    private void countBSpline3D(){
        List<Point> points = bSpline.getPointsToRotate();
        splinePoints3D = new Point3D[points.size()][parameters.getM()];
        double step = 2 * Math.PI / (parameters.getM() - 1);
        for(int k = 0; k < points.size(); ++k){
            for(int v = 0; v < parameters.getM(); v++){
                Point p = points.get(k);
                int z = 0;
                SimpleMatrix coordinates = new SimpleMatrix(new double[][]{{p.x}, {p.y}, {z}});
                SimpleMatrix rotationMtx = MatrixGenerator.rotationMatrix3OX(v * step);
                SimpleMatrix result = rotationMtx.mult(coordinates);
                splinePoints3D[k][v] = new Point3D(result.get(0, 0), result.get(1, 0), result.get(2, 0));
            }
        }
        isActualSplinePoints3D = true;
    }

    private void useTransformation(){
        splinePoints2D = new Point[bSpline.getPointsToRotate().size()][parameters.getM()];
        for(int k = 0; k < bSpline.getPointsToRotate().size(); ++k){
            for(int v = 0; v < parameters.getM(); v++){
                SimpleMatrix coordinates = new SimpleMatrix(new double[][] {{splinePoints3D[k][v].getX()},
                        {splinePoints3D[k][v].getY()},
                        {splinePoints3D[k][v].getZ()},
                        {1}});
                coordinates = rotationMatrix.mult(coordinates);
                coordinates = MatrixGenerator.shiftMatrix(0, 0, 1000).mult(coordinates);
                coordinates = MatrixGenerator.projectionMatrix().mult(coordinates);
                coordinates = coordinates.divide(coordinates.get(3, 0));
                splinePoints2D[k][v] = new Point((int)Math.round(coordinates.get(0, 0) + Globals.IMAGE_WIDTH / 2),
                        (int)Math.round(coordinates.get(1, 0) + Globals.IMAGE_HEIGHT /2));
            }
        }
        isActualSplinePoints2D = true;
    }

    @Override
    public void addObserver(BSplineObserver obs) {
        bSpline.addObserver(obs);
    }

    @Override
    public void removeObserver(BSplineObserver obs) {
        bSpline.removeObserver(obs);
    }
}
