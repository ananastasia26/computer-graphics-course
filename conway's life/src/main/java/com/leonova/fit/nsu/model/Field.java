package com.leonova.fit.nsu.model;

import com.leonova.fit.nsu.observer.Observable;
import com.leonova.fit.nsu.observer.Observer;

import java.util.ArrayList;
import java.util.HashSet;

public class Field implements FieldModel, Observable {

    private int cellsInRow;
    private int cellsInColumn;
    private ArrayList<Observer> observers = new ArrayList<>();
    private Cell[][] field;
    private GameOptions gameOptions;
    private Position[] shiftsForFirstLevelImpactCells = {new Position(-1, -1), new Position(-1, 0),
            new Position(0, -1), new Position(0, 1), new Position(1, -1),new Position(1, 0)};
    private Position[] shiftsForSecondLevelImpactCells = {new Position(-2, 0),new Position(-1, -2),
            new Position(-1, 1), new Position(1, 2),new Position(1, 1),new Position(2, 0)};

    public Field(int cellsInRow, int cellsInColumn, GameOptions gameOptions, ArrayList<Position> aliveCells) {
        this.cellsInRow = cellsInRow;
        this.cellsInColumn = cellsInColumn;
        this.gameOptions = gameOptions;

        field = new Cell[cellsInRow][cellsInColumn];
        for(int i = 0; i < cellsInRow; ++i){
            field[i] = new Cell[cellsInColumn];
            for(int j = 0; j < cellsInColumn - (i % 2 == 1 ? -1 : 0); ++j){
                field[i][j] = new Cell();
            }
        }
        for(Position aliveCell : aliveCells){
            field[aliveCell.getX()][aliveCell.getY()].setAlive(true);
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

    private HashSet<Cell> countLife() {
        HashSet<Cell> changedCells = new HashSet<>();
        for (int i = 0; i < cellsInRow; ++i) {
            for (int j = 0; j < cellsInColumn - (i % 2 == 1 ? -1 : 0); ++j) {
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
        HashSet<Cell> changedCells = new HashSet<>();
        for(int i = 0; i < cellsInRow; ++i){
            for(int j = 0; j < cellsInColumn - (i % 2 == 1 ? -1 : 0); ++j){
                int firstCount = 0;
                int secondCount = 0;
                for(int k = 0; k < shiftsForFirstLevelImpactCells.length; ++k){
                    Position neighbour = new Position(i, j).plus(shiftsForFirstLevelImpactCells[k]);
                    if(isInside(neighbour) && field[neighbour.getX()][neighbour.getY()].isAlive()){
                        ++firstCount;
                    }
                }
                for(int k = 0; k < shiftsForSecondLevelImpactCells.length; ++k){
                    Position neighbour = new Position(i, j).plus(shiftsForSecondLevelImpactCells[k]);
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
    }

    private boolean isInside(Position position){
        if (position.getX() < 0 || position.getY() < 0 || position.getY() >= cellsInColumn){
            return false;
        }
        if((position.getX() % 2 == 1 && position.getX() >= cellsInRow - 1) || position.getX() >= cellsInRow){
            return false;
        }
        return true;
    }

}
