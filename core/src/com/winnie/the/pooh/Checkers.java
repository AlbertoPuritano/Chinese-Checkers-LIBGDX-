package com.winnie.the.pooh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;

public class Checkers {
    final private SpriteBatch batch;
    public Board board;
    public int numPlayers;
    public int playerTurn;
    public Integer selectedPiece;
    public ArrayList<Integer> possibleMoves;
    public ArrayList<Integer> jumpMoves;
    public Circle circle;
    public Integer onlyJump;
    public Integer beforeJump;
    public Stage stage;
    public ShapeRenderer shapeRenderer;
    public Boolean[] AI;
    public Audio audio;
    public Checkers(int numPlayers, Stage stage, Boolean[] AI, final Audio audio)
    {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        this.stage=stage;
        beforeJump=-1;
        final int finalNumPlayers = numPlayers; //non capisco ma funziona solo così
        shapeRenderer= new ShapeRenderer();
        this.numPlayers=numPlayers;
        playerTurn=1;
        batch = new SpriteBatch();
        board = new Board(stage,audio);
        possibleMoves= new ArrayList<Integer>();
        jumpMoves=new ArrayList<Integer>();
        onlyJump=-1;
        board.addPlayer(1);
        board.addPlayer(2);
        numPlayers-=2;
        if (numPlayers>0)
        {
            int i=2;
            while (numPlayers>0)
            {
                i++;
                board.addPlayer(i);
                numPlayers--;
            }
        }
        //END TURN BUTTON
        stage.getActors().get(0).addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor endTurn) {
                audio.playClick();
                if (onlyJump>-1 && beforeJump != selectedPiece)
                {
                    onlyJump = -1;
                    playerTurn++;
                    if (playerTurn== finalNumPlayers +1)
                        playerTurn=1;
                    board.turnChanged(playerTurn);
                    possibleMoves.clear();
                    beforeJump=-1;
                    endTurn.setVisible(false);
                    audio.playNext();
                }
            }
        });
        this.AI=AI;
        this.audio = audio;
        audio.playNewGame();
        board.turnChanged(playerTurn);
    }
    public void draw()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK))
            stage.getActors().get(stage.getActors().size-1).setVisible(!stage.getActors().get(stage.getActors().size-1).isVisible());
        stage.getCamera().update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Game.input.convertInput();
        board.draw(batch,shapeRenderer,possibleMoves);
        stage.act();
        stage.draw();
    }
    public Boolean makeMove()
    {
        if (AI[playerTurn])
            return AIMove();
        else
            return humanMove();
    }
    public Boolean winCond(int playerTurn)
    {
        boolean full=true;
        boolean atLeast1=false;
        switch(playerTurn)
        {
            case 1:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==2 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==2 && p.getPlayer()==1)
                        atLeast1=true;
                }
                break;
            case 2:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==1 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==1 && p.getPlayer()==2)
                        atLeast1=true;
                }
                break;
            case 3:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==4 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==4 && p.getPlayer()==3)
                        atLeast1=true;
                }
                break;
            case 4:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==3 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==3 && p.getPlayer()==4)
                        atLeast1=true;
                }
                break;
            case 5:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==4 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==4 && p.getPlayer()==5)
                        atLeast1=true;
                }
                break;
            case 6:
                for (Piece p: board.pieces)
                {
                    if (p.getOriginPlayer()==5 && p.getPlayer()==0)
                    {
                        full = false;
                        break;
                    }
                    if (p.getOriginPlayer()==5 && p.getPlayer()==6)
                        atLeast1=true;
                }
                break;
        }
        return full && atLeast1;
    }
    public void calculateMoves()
    {
        possibleMoves.clear();
        jumpMoves.clear();
        circle= new Circle(board.getPiece(selectedPiece).x,board.getPiece(selectedPiece).y,62);
        for (int i=0; i<board.pieces.size(); i++)
        {
            if (board.getPiece(i).overlaps(circle))
            {
                if (board.getPiece(i).getPlayer()==0)
                {
                    if (onlyJump == -1)
                    {
                        boolean check=true;
                        if (board.getPiece(i).getOriginPlayer()!=-1)
                        {
                            if ((playerTurn==1 || playerTurn==2) &&
                                    (board.getPiece(i).getOriginPlayer()!=1 && board.getPiece(i).getOriginPlayer()!=2) ||
                            (playerTurn==3 || playerTurn==4) &&
                                    (board.getPiece(i).getOriginPlayer()!=3 && board.getPiece(i).getOriginPlayer()!=4) ||
                            (playerTurn==5 || playerTurn==6) &&
                                    (board.getPiece(i).getOriginPlayer()!=5 && board.getPiece(i).getOriginPlayer()!=6)
                            )
                                check=false;
                        }
                        if (check)
                            possibleMoves.add(i);
                    }
                }
                else
                {
                    int diffX= (int) ((int)board.getPiece(i).x-board.getPiece(selectedPiece).x);
                    int diffY= (int) ((int)board.getPiece(i).y-board.getPiece(selectedPiece).y);
                    for (int j=0; j<board.pieces.size(); j++)
                    {
                        if (board.getPiece(j).getPlayer()==0 && board.getPiece(j).contains(board.getPiece(i).x+diffX, board.getPiece(i).y+diffY))
                        {
                            possibleMoves.add(j);
                            jumpMoves.add(j);
                            break;
                        }
                    }
                }
            }
        }
    }
    public Boolean humanMove()
    {
        if (stage.getActors().get(stage.getActors().size-1).isVisible())
            return false;
        Game.input.convertInput();
        for (int i=0; i< 121;i++)
        {
            if (Gdx.input.isTouched() && board.getPiece(i).contains(Game.input.x(),Game.input.y()))
            {
                if (board.getPiece(i).getPlayer()==playerTurn)
                {
                    if (onlyJump==-1) {
                        if (selectedPiece== null || selectedPiece!=i)
                            audio.playSelect();
                        selectedPiece = i;
                        beforeJump=i;
                    }
                    calculateMoves();
                }
                if (selectedPiece!= null && possibleMoves.contains(i) && board.move(selectedPiece,i,playerTurn))
                {
                    if (winCond(playerTurn))
                    {
                        audio.playWin();
                        return true;
                    }
                    if (jumpMoves.contains(i))
                    {
                        audio.playJump();
                        onlyJump = i;
                        selectedPiece = i;
                        if (onlyJump==beforeJump)
                        {
                            onlyJump=-1;
                            beforeJump=-1;
                            stage.getActors().get(0).setVisible(false);
                        }
                        else
                            stage.getActors().get(0).setVisible(true);
                    }
                    else
                    {
                        audio.playMove();
                        playerTurn++;
                        selectedPiece = null;
                    }
                    possibleMoves.clear();
                    jumpMoves.clear();
                }
                if (playerTurn==numPlayers+1)
                    playerTurn=1;
                board.turnChanged(playerTurn);
            }
        }
        return false;
    }
    public Boolean AIMove()
    {

        System.out.println("TODO");
        playerTurn++;
        if (playerTurn==numPlayers)
            playerTurn=2;
        board.turnChanged(playerTurn);
        return false;
    }
    public void dispose()
    {
        board.dispose();
        stage.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}
