/* Copyright 2015 Jan Potma, Jonathan Moerman, Mathis Sackers, Tom Nijholt
*
* This file is part of a:Dventure.
*
* a:Dventure is free software: you can redistribute it
* and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation, either version 2 of the
* License, or (at your option) any later version.
*
* a:Dventure is distributed in the hope that it will be
* useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
* Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with a:Dventure. If not, see http://www.gnu.org/licenses/.
*/
package tk.slicesofcheese.the_best_application_ever;

import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;

import tk.slicesofcheese.the_best_application_ever.Model.Corner;
import tk.slicesofcheese.the_best_application_ever.Model.Direction;
import tk.slicesofcheese.the_best_application_ever.Model.Entities.Enemy;
import tk.slicesofcheese.the_best_application_ever.Model.Entities.Player;
import tk.slicesofcheese.the_best_application_ever.Model.Level;
import tk.slicesofcheese.the_best_application_ever.Model.LevelGenerator;
import tk.slicesofcheese.the_best_application_ever.View.GameView;
import tk.slicesofcheese.the_best_application_ever.View.TouchOverlay;

/**
 * Controller for a:Dventure.
 */
public class Controller implements View.OnTouchListener, Serializable {

    private EnemyController eController;
    private Level level;
    private GameView gv;
    private TouchOverlay to;

    private final float buttonSize = 0.33f;

    public Controller (GameActivity ga) {
        LevelGenerator generator = new LevelGenerator();
        level = generator.genLevel();
        eController = new EnemyController(level);

        gv = (GameView)ga.findViewById(R.id.gameView);
        gv.setLevel(level);

        to = (TouchOverlay)ga.findViewById(R.id.touchOverlay);
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int[] c = gv.getCoordinates();

        int height = c[3] - c[1];
        int width = c[2] - c[0];
        float x = event.getX() - c[0];
        float y = event.getY() - c[1];

        if (event.getAction() == MotionEvent.ACTION_UP) {
            switch (location(x, y, height, width)) {
                case UP:
                    this.movePlayer(Direction.UP);
                    eController.moveEnemies();
                    gv.postInvalidate();
                    break;
                case RIGHT:
                    this.movePlayer(Direction.RIGTH);
                    eController.moveEnemies();
                    gv.postInvalidate();
                    break;
                case DOWN:
                    this.movePlayer(Direction.DOWN);
                    eController.moveEnemies();
                    gv.postInvalidate();
                    break;
                case LEFT:
                    this.movePlayer(Direction.LEFT);
                    eController.moveEnemies();
                    gv.postInvalidate();
                    break;
                case CENTER:
                    level.moveEnemies();
                    gv.postInvalidate();
                default:
                    break;
            }
            to.clear();
        }
        else {
            to.drawTouchArea(location(x, y, height, width), c, buttonSize);
        }

        return true;
    }

    /**
     * Returns the part of the screen that the coordinate is on.
     * @param x x coordinate of input
     * @param y y coordinate of input
     * @param height height of activity
     * @param width width of activity
     * @return the corner of the coordinate
     */
    private Corner location(float x, float y, int height, int width){
        float perX = x/(float)width;
        float perY = y/(float)height;

        if (perX > buttonSize && perX < 1-buttonSize && perY > buttonSize && perY < 1-buttonSize)
            return  Corner.CENTER;

        if (perX > perY){
            if(1 - perX > perY){
                return Corner.UP;
            } else {
                return Corner.RIGHT;
            }
        } else {
            if(1 - perX > perY){
                return Corner.LEFT;
            } else {
                return Corner.DOWN;
            }
        }
    }

    public Level getLevel(){
        return level;
    }

    public void setLevel(Level level){
        gv.setLevel(level);
        this.level = level;
    }

    private boolean movePlayer (Direction dir) {
        Player player = level.getPlayer();
        if (player == null)
            return false;

        int x = player.getX();
        int y = player.getY();

        switch (dir) {
            case LEFT:
                x--;
                break;
            case RIGTH:
                x++;
                break;
            case DOWN:
                y++;
                break;
            case UP:
                y--;
                break;
            default:
                break;
        }

        if (level.isValid(x, y)) {
            if (!level.isWall(x, y)) {
                if (level.isEnemy(x, y)) {
                    level.removeEnemy((Enemy) level.getEntity(x, y));
                }
                level.moveEntity(player.getX(), player.getY(), x, y);
                return true;
            }
        }

        return false;
    }
}
