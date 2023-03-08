package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

import java.util.List;
import java.util.Random;

public class BetterBot implements IBot {

    private static final String BOTNAME = "BetterBot";
    private Random rand;

    private GameSimulator2 createSimulator(IGameState state) {
        GameSimulator2 simulator = new GameSimulator2(new GameState());
        simulator.setGameOver(GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();
        for (int i = 0; i < 81; i++) {
            IMove bestMove = doSimulation(state);
            if(bestMove!=null){
                return bestMove;
            }
        }
        for (int i = 0; i < 81; i++) {
            IMove bestMove = doSimulation(state);
            if(bestMove!=null){
                return bestMove;
            }
                for (int j = 0; j < 81; j++) {
                    IMove secondBestMove = doSimulation(state);
                    if(bestMove!=null){
                        return secondBestMove;
                    }
                }
        }
        for (int i = 0; i < 81; i++) {
            IMove bestMove = doSimulation(state);
            if(bestMove!=null){
                return bestMove;
            }
                for (int j = 0; j < 81; j++) {
                    IMove secondBestMove = doSimulation(state);
                    if(bestMove!=null){
                        return secondBestMove;
                    }
                }
            for (int j = 0; j < 81; j++) {
                IMove thirdMove = doSimulation(state);
                if(thirdMove !=null){
                    return thirdMove;
                }
            }
            }
        return moves.get(rand.nextInt(moves.size())); /* get random move from available moves */

    }

    private IMove doSimulation(IGameState state){
            List<IMove> moves = state.getField().getAvailableMoves();
            GameSimulator2 simulator = createSimulator(state);
            rand = new Random();
            IGameState gs = simulator.currentState;
            moves = gs.getField().getAvailableMoves();
            IMove randomMove = moves.get(rand.nextInt(moves.size()));
            simulator.updateGame(randomMove);
            if(simulator.getGameOver() == GameOverState.Win) {
                return randomMove;
            }
            else if (simulator.macroWin) {
                return randomMove;
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

        private boolean macroWin = false;
        private volatile GameOverState gameOver = GameOverState.Active;



        public void setGameOver(GameOverState state) {
            gameOver = state;
        }

        public GameOverState getGameOver() {
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

                if (isWin(board, move, "" + currentPlayer)){
                    macroBoard[macroX][macroY] = currentPlayer + "";
                    macroWin = true;
                }
                else if (isTie(board, move))
                    macroBoard[macroX][macroY] = "TIE";

                //Check macro win
                if (isWin(macroBoard, new Move(macroX, macroY), "" + currentPlayer))
                    gameOver = GameOverState.Win;
                else if (isTie(macroBoard, new Move(macroX, macroY)))
                    gameOver = GameOverState.Tie;
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