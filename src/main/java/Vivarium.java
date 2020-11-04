

import javax.media.opengl.*;

import java.util.*;
import java.util.Random;


// Nicholas Goutermout
// CS 680
// PS3
// 11/4
// Main vivarium class
// simple checks if it should draw food or smallfrys
public class Vivarium {
    private Tank tank;
    public static ArrayList<SmallFry> smallFries = new ArrayList<SmallFry>();
    public static Predator predator;
    public static ArrayList<Food> food = new ArrayList<Food>();
    private static Random random = new Random(42);


    public Vivarium() {
        tank = new Tank(4.0f, 4.0f, 4.0f);


        predator = new Predator(1f, 1.5f, 0, 1.5f);
        predator.predator = true;

        // Manually add small fry and food
        // finer control this way
        // an alternative would be to loop and randomly place
        SmallFry smallFry = new SmallFry(.75f, 0, 0, 0);
        smallFries.add(smallFry);
        smallFries.add(new SmallFry(.75f, -.5f, 0, -.5f));
        smallFries.add(new SmallFry(.75f, .5f, 0, -.5f));
        smallFries.add(new SmallFry(.75f, -.5f, 0, .5f));
        smallFries.add(new SmallFry(.75f, 1f, 0, 1f));
        smallFries.add(new SmallFry(.75f, 1f, 0, -1f));
        smallFries.add(new SmallFry(.75f, -1f, 0, 1f));
        smallFries.add(new SmallFry(.75f, -1f, 0, -1f));


        food.add(new Food(.75f, 1f, 0, 1f));
        food.add(new Food(.75f, 1f, 0, -1f));
        food.add(new Food(.75f, -1f, 0, 1f));
        food.add(new Food(.75f, -1f, 0, -1f));
        food.add(new Food(.75f, 0, 0, 0));


    }

    // Randomly enable a food for the small fry to chase
    public static void addFood() {
        food.get(random.nextInt(5)).eaten = false;
    }

    // init all of our objects
    public void init(GL2 gl) {
        tank.init(gl);
        for (SmallFry smallFry : smallFries) {
            smallFry.init(gl);
        }
        for (Food food : food) {
            food.init(gl);
        }
        predator.init(gl);
    }

    // update objects with appropriate checks
    // Note food is draw when it is not eaten
    public void update(GL2 gl) {
        tank.update(gl);
        for (SmallFry smallFry : smallFries) {
            if (smallFry.alive) {
                smallFry.update(gl);
            }
        }
        for (Food food : food) {
            if (!food.eaten) {
                food.update(gl);
            }
        }
        predator.update(gl);
    }

    // draw objects with appropriate checks
    // Note food is draw when it is not eaten
    public void draw(GL2 gl) {
        tank.draw(gl);
        for (SmallFry smallFry : smallFries) {
            if (smallFry.alive) {
                smallFry.draw(gl);
            }
        }
        for (Food food : food) {
            if (!food.eaten) {
                food.draw(gl);
            }
        }
        predator.draw(gl);
    }
}
