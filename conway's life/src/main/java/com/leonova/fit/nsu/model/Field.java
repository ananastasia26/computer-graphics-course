package com.leonova.fit.nsu.model;

import com.leonova.fit.nsu.observer.Observable;
import com.leonova.fit.nsu.observer.Observer;
import com.leonova.fit.nsu.view.GraphicsOptions;

import java.util.ArrayList;
import java.util.HashSet;

public class Field implements FieldModel, Observable {

    private int rows;
    private int columns;

    private ArrayList<Observer> observers = new ArrayList<>();
    private Cell[][] field;
    private GameOptions gameOptions;
    private Position[] shiftsForFirstLevelImpactCellsOdd = {new Position(-1, 0), new Position(-1, 1),
            new Position(0, -1), new Position(0, 1), new Position(1, 0),new Position(1, 1)};
    private Position[] shiftsForSecondLevelImpactCellsOdd = {new Position(-2, 0),new Position(-1, -1),
            new Position(-1, 2), new Position(1, -1),new Position(1, 2),new Position(2, 0)};


    public Field(int rows, int columns, GameOptions gameOptions){
        this.rows = rows;
        this.columns = columns;
        this.gameOptions = gameOptions;

        field = new Cell[rows][columns];
        for(int i = 0; i < rows; ++i){
            field[i] = new Cell[columns];
            for(int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j){
                field[i][j] = new Cell(new Position(i, j));
            }
        }
    }

    @Override
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    @Override
    public void deleteObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void pressedCell(Position position) {
        Cell cell = field[position.getX()][position.getY()];
        if(gameOptions.isModeXor()){
            cell.setAlive(!cell.isAlive());
        }
        else{
            cell.setAlive(true);
        }
        HashSet<Cell> changedCells = countImpact();
        changedCells.add(cell);
        notifyAboutCellsChange(changedCells);
    }

    @Override
    public void nextStep(){
        HashSet<Cell> lifeChangedCells = countLife();
        HashSet<Cell> impactChangedCells = countImpact();
        impactChangedCells.addAll(lifeChangedCells);
        notifyAboutCellsChange(impactChangedCells);
    }

    @Override
    public void clearField() {
        HashSet<Cell> changedCells = new HashSet<>(columns * rows);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j) {
                Cell cell = field[i][j];
                cell.setAlive(false);
                cell.setImpact(0.0);
                changedCells.add(cell);
            }
        }
        notifyAboutCellsChange(changedCells);
    }

    @Override
    public void impactPressed() {
        HashSet<Cell> cells = getAllCells();
        for(Observer observer : observers){
            observer.displayImpact(cells);
        }
    }

    @Override
    public void newOptions(GameOptions gameOptions, GraphicsOptions graphicsOptions) {
        this.gameOptions = gameOptions;

        int newRows = graphicsOptions.getRows();
        int newColumns = graphicsOptions.getColumns();

        Cell[][] newField = new Cell[newRows][newColumns];
        for(int i = 0; i < newRows; ++i){
            newField[i] = new Cell[newColumns];
            for(int j = 0; j < newColumns - (i % 2 == 1 ? 1 : 0); ++j){
                newField[i][j] = new Cell(new Position(i, j));
                if(i < rows && j < columns - (i % 2 == 1 ? 1 : 0)){
                    newField[i][j].setAlive(field[i][j].isAlive());
                }
            }
        }
        columns = newColumns;
        rows = newRows;

        field = newField;
        countImpact();
        HashSet<Cell> cells = getAllCells();
        for(Observer observer : observers){
           observer.repaintAll(cells, graphicsOptions);
        }
    }

    @Override
    public void newField(GameOptions gameOptions, GraphicsOptions graphicsOptions, ArrayList<Position> aliveCells) {
        this.gameOptions = gameOptions;
        rows = graphicsOptions.getRows();
        columns = graphicsOptions.getColumns();

        field = new Cell[rows][columns];
        for(int i = 0; i < rows; ++i){
            field[i] = new Cell[columns];
            for(int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j){
                field[i][j] = new Cell(new Position(i, j));
            }
        }
        for(Position aliveCell : aliveCells){
            field[aliveCell.getX()][aliveCell.getY()].setAlive(true);
        }
        countImpact();
        for(Observer observer : observers){
            observer.repaintAll(getAllCells(), graphicsOptions);
        }
    }

    @Override
    public HashSet<Cell> getAliveCells() {
        HashSet<Cell> cells = new HashSet<>();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j) {
                if(field[i][j].isAlive()){
                    cells.add(field[i][j]);
                }
            }
        }
        return cells;
    }

    private HashSet<Cell> getAllCells(){
        HashSet<Cell> cells = new HashSet<>();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j) {
                cells.add(field[i][j]);
            }
        }
        return cells;
    }

    private HashSet<Cell> countLife() {
        HashSet<Cell> changedCells = new HashSet<>();
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j) {

                Cell cell = field[i][j];
                double impact = cell.getImpact();
                if(!cell.isAlive() && impact >= gameOptions.getBirthBegin() && impact <= gameOptions.getBirthEnd()){
                    cell.setAlive(true);
                    changedCells.add(cell);
                }
                if(cell.isAlive() && (impact < gameOptions.getLiveBegin() || impact > gameOptions.getLiveEnd())){
                    cell.setAlive(false);
                    changedCells.add(cell);
                }
            }
        }
        return changedCells;
    }

    private void notifyAboutCellsChange(HashSet<Cell> changedCells){
        for(Observer observer : observers){
            observer.updateGraphicField(changedCells);
        }
    }

    private HashSet<Cell> countImpact(){
        try{
            HashSet<Cell> changedCells = new HashSet<>();
            for(int i = 0; i < rows; ++i){
                for(int j = 0; j < columns - (i % 2 == 1 ? 1 : 0); ++j){
                    int firstCount = 0;
                    int secondCount = 0;
                    for(int k = 0; k < shiftsForFirstLevelImpactCellsOdd.length; ++k){

                        int x0 = i + shiftsForFirstLevelImpactCellsOdd[k].getX();
                        int y0 = j + shiftsForFirstLevelImpactCellsOdd[k].getY();
                        if(i % 2 == 0 && shiftsForFirstLevelImpactCellsOdd[k].getX() != 0){
                            y0 -= 1;
                        }
                        Position neighbour = new Position(x0, y0);
                        if(isInside(neighbour) && field[neighbour.getX()][neighbour.getY()].isAlive()){
                            ++firstCount;
                        }
                    }
                    for(int k = 0; k < shiftsForSecondLevelImpactCellsOdd.length; ++k){
                        int x0 = i + shiftsForSecondLevelImpactCellsOdd[k].getX();
                        int y0 = j + shiftsForSecondLevelImpactCellsOdd[k].getY();
                        if(i % 2 == 0 && shiftsForSecondLevelImpactCellsOdd[k].getY() != 0){
                            y0 -= 1;
                        }
                        Position neighbour = new Position(x0, y0);
                        if(isInside(neighbour) && field[neighbour.getX()][neighbour.getY()].isAlive()){
                            ++secondCount;
                        }

                    }
                    Cell cell = field[i][j];
                    double oldImpact = cell.getImpact();
                    double newImpact = gameOptions.getFirstImpact() * firstCount + gameOptions.getSecondImpact() * secondCount;
                    if(oldImpact != newImpact){
                        changedCells.add(cell);
                        cell.setImpact(newImpact);
                    }
                }
            }
            return changedCells;
        } catch (Exception e){
            System.out.println();
            throw e;
        }
    }

    private boolean isInside(Position position){
        if(position.getX() < 0 || position.getY() < 0 || position.getY() >= columns || position.getX() >= rows){
            return false;
        }
        else if((position.getX() % 2 == 1 && position.getY() >= columns - 1)){
            return false;
        }

        return true;
    }

}
