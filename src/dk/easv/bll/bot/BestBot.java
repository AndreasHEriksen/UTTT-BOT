package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.List;
import java.util.Random;

public class BestBot implements IBot{
    private static final String BOTNAME = "BestBot";
    private Random rand;
    private boolean firstMove = true;

    private BestBot.GameSimulator2 createSimulator(IGameState state) {
        BestBot.GameSimulator2 simulator = new BestBot.GameSimulator2(new GameState());
        simulator.setGameOver(BestBot.GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }
    protected int[][] preferredMoves = {
            {1, 1}, //Center
            {0, 0}, {0, 1}, {0, 2}, {1, 0},  //Corners ordered across
            {1, 2}, {2, 0}, {2, 1}, {2, 2},{1,1}};

    protected int[][] preferredMoves2 = {
            {2, 2}, //Center
            {1, 1}, {2, 0}, {0, 2}, {0, 0},  //Corners ordered across
            {0, 1}, {2, 1}, {1, 0}, {1, 2},{1,1}};

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();
        for (int i = 0; i < 1000; i++) {
            BestBot.GameSimulator2 simulator = createSimulator(state);
            rand = new Random();
            IGameState gs = simulator.currentState;
            moves = gs.getField().getAvailableMoves();
            IMove randomMove = moves.get(rand.nextInt(moves.size()));
            simulator.updateGame(randomMove);
            if(simulator.getGameOver() == BestBot.GameOverState.Win) {
                int countered = 0;
                countered++;
                System.out.println(countered);
                return randomMove;
            }
        }
        if (moves.size() > 0) {
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
        }
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    public enum GameOverState {
        Active,
        Win,
        Tie
    }

    class GameSimulator2 {
        private final IGameState currentState;
        private int currentPlayer = 0; //player0 == 0 && player1 == 1
        private volatile BestBot.GameOverState gameOver = BestBot.GameOverState.Active;


        public void setGameOver(BestBot.GameOverState state) {
            gameOver = state;
        }

        public BestBot.GameOverState getGameOver() {
            return gameOver;
        }

        public void setCurrentPlayer(int player) {
            currentPlayer = player;
        }

        public IGameState getCurrentState() {
            return currentState;
        }
        GameSimulator2(IGameState currentState) {
            this.currentState = currentState;
        }


        public boolean updateGame(IMove randomMove) {
            if (!verifyMoveLegality(randomMove))
                return false;

            updateBoard(randomMove);
            currentPlayer = (currentPlayer + 1) % 2;

            return true;
        }
        private void updateBoard(IMove move) {
            String[][] board = currentState.getField().getBoard();
            board[move.getX()][move.getY()] = currentPlayer + "";
            currentState.setMoveNumber(currentState.getMoveNumber() + 1);
            if (currentState.getMoveNumber() % 2 == 0) {
                currentState.setRoundNumber(currentState.getRoundNumber() + 1);
            }
            checkAndUpdateIfWin(move);
            updateMacroboard(move);
        }
        private void updateMacroboard(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            for (int i = 0; i < macroBoard.length; i++)
                for (int k = 0; k < macroBoard[i].length; k++) {
                    if (macroBoard[i][k].equals(IField.AVAILABLE_FIELD))
                        macroBoard[i][k] = IField.EMPTY_FIELD;
                }

            int xTrans = move.getX() % 3;
            int yTrans = move.getY() % 3;

            if (macroBoard[xTrans][yTrans].equals(IField.EMPTY_FIELD))
                macroBoard[xTrans][yTrans] = IField.AVAILABLE_FIELD;
            else {
                // Field is already won, set all fields not won to avail.
                for (int i = 0; i < macroBoard.length; i++)
                    for (int k = 0; k < macroBoard[i].length; k++) {
                        if (macroBoard[i][k].equals(IField.EMPTY_FIELD))
                            macroBoard[i][k] = IField.AVAILABLE_FIELD;
                    }
            }
        }

        private void checkAndUpdateIfWin(IMove move) {
            String[][] macroBoard = currentState.getField().getMacroboard();
            int macroX = move.getX() / 3;
            int macroY = move.getY() / 3;

            if (macroBoard[macroX][macroY].equals(IField.EMPTY_FIELD) ||
                    macroBoard[macroX][macroY].equals(IField.AVAILABLE_FIELD)) {

                String[][] board = getCurrentState().getField().getBoard();

                if (isWin(board, move, "" + currentPlayer))
                    macroBoard[macroX][macroY] = currentPlayer + "";
                else if (isTie(board, move))
                    macroBoard[macroX][macroY] = "TIE";

                //Check macro win
                if (isWin(macroBoard, new Move(macroX, macroY), "" + currentPlayer))
                    gameOver = BestBot.GameOverState.Win;
                else if (isTie(macroBoard, new Move(macroX, macroY)))
                    gameOver = BestBot.GameOverState.Tie;
            }
        }

        private Boolean verifyMoveLegality(IMove move) {
            IField field = currentState.getField();
            boolean isValid = field.isInActiveMicroboard(move.getX(), move.getY());

            if (isValid && (move.getX() < 0 || 9 <= move.getX())) isValid = false;
            if (isValid && (move.getY() < 0 || 9 <= move.getY())) isValid = false;

            if (isValid && !field.getBoard()[move.getX()][move.getY()].equals(IField.EMPTY_FIELD))
                isValid = false;

            return isValid;
        }
        private boolean isTie(String[][] board, IMove move) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            for (int i = startX; i < startX + 3; i++) {
                for (int k = startY; k < startY + 3; k++) {
                    if (board[i][k].equals(IField.AVAILABLE_FIELD) ||
                            board[i][k].equals(IField.EMPTY_FIELD))
                        return false;
                }
            }
            return true;
        }

        public boolean isWin(String[][] board, IMove move, String currentPlayer) {
            int localX = move.getX() % 3;
            int localY = move.getY() % 3;
            int startX = move.getX() - (localX);
            int startY = move.getY() - (localY);

            //check col
            for (int i = startY; i < startY + 3; i++) {
                if (!board[move.getX()][i].equals(currentPlayer))
                    break;
                if (i == startY + 3 - 1) return true;
            }

            //check row
            for (int i = startX; i < startX + 3; i++) {
                if (!board[i][move.getY()].equals(currentPlayer))
                    break;
                if (i == startX + 3 - 1) return true;
            }

            //check diagonal
            if (localX == localY) {
                //we're on a diagonal
                int y = startY;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][y++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }

            //check anti diagonal
            if (localX + localY == 3 - 1) {
                int less = 0;
                for (int i = startX; i < startX + 3; i++) {
                    if (!board[i][(startY + 2) - less++].equals(currentPlayer))
                        break;
                    if (i == startX + 3 - 1) return true;
                }
            }
            return false;
        }
    }
}

