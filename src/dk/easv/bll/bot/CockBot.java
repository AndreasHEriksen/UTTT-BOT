package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CockBot implements IBot {

    private String BOT_NAME = getClass().getSimpleName();
    Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> winMoves = checkWinningMove(state);
        List<IMove> blockMoves = checkBlockMove(state);

        //If a move can win, win.
        if (!winMoves.isEmpty()) {
            return winMoves.get(0);
        }
        //If a move can block, block!
        if (!blockMoves.isEmpty()) {
            return blockMoves.get(0);
        }

        //Do a random move.
        List<IMove> moves = state.getField().getAvailableMoves();
        if (moves.size() > 0) {
            return moves.get(rand.nextInt(moves.size()));
        }
        return null;
    }

    @Override
    public String getBotName() {
        return BOT_NAME;
    }

    private List<IMove> checkBlockMove(IGameState state) {
        String player = "0";
        if (state.getMoveNumber() % 3 == 0) {
            player = "1";
        }
        List<IMove> avail = state.getField().getAvailableMoves();
        String[][] board = state.getField().getBoard();

        List<IMove> winningMoves = new ArrayList<>();
        for (IMove move : avail) {
            // Row check
            boolean isRowWin = true;
            int startX = move.getX() - (move.getX() % 3);
            int endX = startX + 2;
            for (int x = startX; x <= endX; x++) {
                if (x != move.getX()) {
                    if (!board[x][move.getY()].equals(player)) {
                        isRowWin = false;
                    }
                }
            }
            if (isRowWin) {
                winningMoves.add(move);
                break;
            }

            // Column check
            boolean isColumnWin = true;
            int startY = move.getY() - (move.getY() % 3);
            int endY = startY + 2;
            for (int y = startY; y <= endY; y++) {
                if (y != move.getY()) {
                    if (!board[move.getX()][y].equals(player)) {
                        isColumnWin = false;
                    }
                }
            }
            if (isColumnWin) {
                winningMoves.add(move);
                break;
            }


            // Diagonal Check left top to right bottom
            boolean isDiagWin = true;

            if (!(move.getX() == startX && move.getY() == startY)) {
                if (!board[startX][startY].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 1 && move.getY() == startY + 1)) {
                if (!board[startX + 1][startY + 1].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 2 && move.getY() == startY + 2)) {
                if (!board[startX + 2][startY + 2].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (isDiagWin) {
                winningMoves.add(move);
                break;
            }

            // Diagonal Check left bottom to right top
            boolean isOppositDiagWin = true;
            if (!(move.getX() == startX && move.getY() == startY + 2)) {
                if (!board[startX][startY + 2].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 1 && move.getY() == startY + 1)) {
                if (!board[startX + 1][startY + 1].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 2 && move.getY() == startY)) {
                if (!board[startX + 2][startY].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (isOppositDiagWin) {
                winningMoves.add(move);
                break;
            }
        }
        return winningMoves;
    }

    private List<IMove> checkWinningMove(IGameState state) {
        String player = "1";
        if (state.getMoveNumber() % 2 == 0) {
            player = "0";
        }
        List<IMove> avail = state.getField().getAvailableMoves();
        String[][] board = state.getField().getBoard();

        List<IMove> winningMoves = new ArrayList<>();
        for (IMove move : avail) {
            // Row check
            boolean isRowWin = true;
            int startX = move.getX() - (move.getX() % 3);
            int endX = startX + 2;
            for (int x = startX; x <= endX; x++) {
                if (x != move.getX()) {
                    if (!board[x][move.getY()].equals(player)) {
                        isRowWin = false;
                    }
                }
            }
            if (isRowWin) {
                winningMoves.add(move);
                break;
            }

            // Column check
            boolean isColumnWin = true;
            int startY = move.getY() - (move.getY() % 3);
            int endY = startY + 2;
            for (int y = startY; y <= endY; y++) {
                if (y != move.getY()) {
                    if (!board[move.getX()][y].equals(player)) {
                        isColumnWin = false;
                    }
                }
            }
            if (isColumnWin) {
                winningMoves.add(move);
                break;
            }


            // Diagonal Check left top to right bottom
            boolean isDiagWin = true;

            if (!(move.getX() == startX && move.getY() == startY)) {
                if (!board[startX][startY].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 1 && move.getY() == startY + 1)) {
                if (!board[startX + 1][startY + 1].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 2 && move.getY() == startY + 2)) {
                if (!board[startX + 2][startY + 2].equals(player)) {
                    isDiagWin = false;
                }
            }
            if (isDiagWin) {
                winningMoves.add(move);
                break;
            }

            // Diagonal Check left bottom to right top
            boolean isOppositDiagWin = true;
            if (!(move.getX() == startX && move.getY() == startY + 2)) {
                if (!board[startX][startY + 2].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 1 && move.getY() == startY + 1)) {
                if (!board[startX + 1][startY + 1].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (!(move.getX() == startX + 2 && move.getY() == startY)) {
                if (!board[startX + 2][startY].equals(player)) {
                    isOppositDiagWin = false;
                }
            }
            if (isOppositDiagWin) {
                winningMoves.add(move);
                break;
            }
        }
        return winningMoves;
    }
}