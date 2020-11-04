

import javax.media.opengl.*;

import com.jogamp.opengl.util.*;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.Random;

public class Vivarium {
    private Tank tank;
    public static ArrayList<SmallFry> smallFries = new ArrayList<SmallFry>();
    public static Predator predator;
    public static ArrayList<Food> food = new ArrayList<Food>();
    private static Random random = new Random(42);


    public Vivarium() {
        tank = new Tank(4.0f, 4.0f, 4.0f);

        SmallFry smallFry = new SmallFry(.75f, 0, 0, 0);
//    SmallFry mediumFry = new SmallFry( .75f, .5f, 0, 0.5f );
        predator = new Predator(1f, 1.5f, 0, 1.5f);
        predator.predator = true;
        smallFries.add(smallFry);
//    smallFries.add(mediumFry);
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

    public static void addFood(){
      food.get(random.nextInt(5)).eaten = false;
    }

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
