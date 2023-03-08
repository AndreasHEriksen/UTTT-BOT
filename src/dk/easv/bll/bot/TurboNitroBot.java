package dk.easv.bll.bot;

import dk.easv.bll.field.IField;
import dk.easv.bll.game.GameManager;
import dk.easv.bll.game.GameState;
import dk.easv.bll.game.IGameState;
import dk.easv.bll.move.IMove;
import dk.easv.bll.move.Move;

public class TurboNitroBot implements IBot {
    private int winPoints = 1000;
    private int losePoints = -1;
    private int minPoints = -1000;
    private int noPoints = -10000;


    private GameState currentState;

    @Override
    public IMove doMove(IGameState state) {
        return null;
    }

    public IMove doSimulatedMove(IMove move){
        if(verifyMoveLegality(move)){
            simulateGame(move);
            return move;
        }
        return null;
    }

    public String[][] simulateGame(IMove move){
        String[][] simulatedBoard = getBoard();
        String[][] simulatedMacroBoard = getMacroBoard();
        for(int i = 0; i < simulatedBoard.length; i++){
            for (int k = 0;k < simulatedMacroBoard.length; k++){

            }
        }
    return null;
    }


    public String[][] getBoard(){
        String[][] board = currentState.getField().getBoard();
        return board;
    }

    public String[][] getMacroBoard(){
        String[][] macroBoard = currentState.getField().getMacroboard();
        return macroBoard;
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

    @Override
    public String getBotName() {
        return "GodBot";
    }
}
