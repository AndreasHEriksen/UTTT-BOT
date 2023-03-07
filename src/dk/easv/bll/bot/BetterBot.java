package dk.easv.bll.bot;

import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;

import java.util.List;
import java.util.Random;

public class BetterBot implements IBot{

    private static final String BOTNAME = "BetterBot";
    private Random rand = new Random();

    @Override
    public IMove doMove(IGameState state) {
        List<IMove> moves = state.getField().getAvailableMoves();
        GameSimulator simulator = new GameSimulator(state);


        if (moves.size() > 0) {
            return moves.get(rand.nextInt(moves.size())); /* get random move from available moves */
        }
        return null;
    }

    @Override
    public String getBotName() {
        return BOTNAME;
    }

    private GameSimulator createSimulator(IGameState state) {
        GameSimulator simulator = new GameSimulator(new GameState());
        simulator.setGameOver(ExampleSneakyBot.GameOverState.Active);
        simulator.setCurrentPlayer(state.getMoveNumber() % 2);
        simulator.getCurrentState().setRoundNumber(state.getRoundNumber());
        simulator.getCurrentState().setMoveNumber(state.getMoveNumber());
        simulator.getCurrentState().getField().setBoard(state.getField().getBoard());
        simulator.getCurrentState().getField().setMacroboard(state.getField().getMacroboard());
        return simulator;
    }
    class GameSimulator {
        private final IGameState currentState;
        private int currentPlayer = 0; //player0 == 0 && player1 == 1
        private volatile ExampleSneakyBot.GameOverState gameOver = ExampleSneakyBot.GameOverState.Active;

        GameSimulator(IGameState currentState) {
            this.currentState = currentState;
        }

        public void setGameOver(ExampleSneakyBot.GameOverState state) {
            gameOver = state;
        }

        public ExampleSneakyBot.GameOverState getGameOver() {
            return gameOver;
        }

        public void setCurrentPlayer(int player) {
            currentPlayer = player;
        }


        public IGameState getCurrentState() {
            return currentState;
        }
        }
    }