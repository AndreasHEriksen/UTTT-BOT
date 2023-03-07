package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dk.easv.bll.game.GameManager.isWin;

public class BabyBot implements IBot{
    private boolean firstMove = true;
    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();

         if (moves.size() > 0 && firstMove) {
            for (int[] move : preferredMoves) {
                if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                    for (int[] selectedMove : preferredMoves) {
                        int x = move[0] * 3 + selectedMove[0];
                        int y = move[1] * 3 + selectedMove[1];
                        if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                            firstMove = false;
                            return new Move(x, y);
                        }
                    }
                }
            }
        }
        else if (moves.size() > 0 && !firstMove) {
            for (int[] move : preferredMoves2) {
                if (state.getField().getMacroboard()[move[0]][move[1]].equals(IField.AVAILABLE_FIELD)) {
                    for (int[] selectedMove : preferredMoves2) {
                        int x = move[0] * 3 + selectedMove[0];
                        int y = move[1] * 3 + selectedMove[1];
                        if (state.getField().getBoard()[x][y].equals(IField.EMPTY_FIELD)) {
                            firstMove = true;
                            return new Move(x, y);
                        }
                    }
                }
            }
        }
        return null;
    }
    protected int[][] preferredMoves = {
            {2, 0}, //Center
            {1, 1}, {2, 2}, {0, 2}, {0, 0},  //Corners ordered across
            {0, 1}, {2, 1}, {1, 0}, {1, 2},{1,1}};

    protected int[][] preferredMoves2 = {
            {2, 2}, //Center
            {1, 1}, {2, 0}, {0, 2}, {0, 0},  //Corners ordered across
            {0, 1}, {2, 1}, {1, 0}, {1, 2},{1,1}};







    @Override
    public String getBotName() {
        return "BabyBot";
    }
}
