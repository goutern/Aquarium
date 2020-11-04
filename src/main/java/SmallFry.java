import com.jogamp.opengl.util.gl2.GLUT;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import java.util.Random;


// Nicholas Goutermout
// CS 680
// PS3
// 11/4

// create a small fish object that will flock to other fish
// should also avoid the predator
// will chase down food
public class SmallFry {

    private float scale;
    private float tailAngle;
    private float tailDelta;
    private float angle;
    private float yAngle = 0.0f;

    private int body;
    private int tail;


    public float zSpeed = 0.005f;
    public float xSpeed = 0.005f;
    public float ySpeed = 0.005f;
    public boolean alive = true;


    private float rotationAngle;
    boolean collision = false;

    private float rotation = 0.0f;
    private float speed = 0.0f;
    private Random random = new Random(42);
    public Coord coord;

    // can be adjusted so our fishes run away faster
    float avoidPotential = -0.0007f;


    public SmallFry(float scale_, float x, float y, float z) {
        coord = new Coord(x, y, z);
        scale = scale_;

        // used to adjust the tail swivel
        tailAngle = 15.0f;
        tailDelta = 2.0f;
    }

    public void init(GL2 gl) {
        GLUT glut = new GLUT();
        body = gl.glGenLists(1);
        gl.glNewList(body, GL2.GL_COMPILE);
        gl.glPushMatrix();
        gl.glScalef(scale * 0.1f, scale * 0.15f, scale * 0.2f);
        glut.glutSolidSphere(scale * 1, 5, 5);
        gl.glPopMatrix();
        gl.glEndList();

        tail = gl.glGenLists(1);
        gl.glNewList(tail, GL2.GL_COMPILE);
        gl.glPushMatrix();
        gl.glTranslated(0, 0, scale * -.2);
        gl.glScalef(scale * 0.1f, scale * 0.15f, scale * 0.2f);
        glut.glutSolidCone(scale * 1, scale * 2, 2, 2);
        gl.glPopMatrix();
        gl.glEndList();

    }

    // Potential function helper
    private float[] potentialFunction(Coord p, Coord q, float pot) {
        float[] vals = new float[3];
        vals[0] = (float) (2 * (q.x - p.x) * pot);
        vals[1] = (float) (2 * (q.y - p.y) * pot);
        vals[2] = (float) (2 * (q.z - p.z) * pot);

        return vals;
    }

    public void update(GL gl) {

        // flip the tail back and forth
        if (tailAngle >= 15 || tailAngle <= -15) {
            tailDelta = -tailDelta;
        }
        tailAngle += tailDelta;

        // last place the fish was
        Coord tempCord = new Coord(coord.x + xSpeed, coord.y + ySpeed, coord.z + zSpeed);
        Coord delta = new Coord();
        float[] change = new float[3];
        for (SmallFry smallFry : Vivarium.smallFries) {
            if (smallFry == this || !smallFry.alive) {
                continue;
            }


            // avoid a colision
            if (collisionDetection(tempCord, smallFry.coord, 0.25f)) {
                change = potentialFunction(this.coord, smallFry.coord, -.01f);
                delta.x += change[0];
                delta.y += change[1];
                delta.z += change[2];
            }

            // check if there are nearby smallfry if so flock on over
            if (collisionDetection(coord, smallFry.coord, 1)) {
                change = potentialFunction(this.coord, smallFry.coord, 0.0001f);
                delta.x += change[0];
                delta.y += change[1];
                delta.z += change[2];
            }


        }


        for (Food food : Vivarium.food) {
            if (food.eaten) {
                continue;
            }

            // chase down the food
            if (collisionDetection(tempCord, food.coord, 3f)) {
                change = potentialFunction(this.coord, food.coord, 0.0001f);
                delta.x += change[0];
                delta.y += change[1];
                delta.z += change[2];
            }
        }


        // die if you come in close the the predator
        if (collisionDetection(coord, Vivarium.predator.coord, 0.3f)) {
            alive = false;
        }

        // try to avoid the predator but he is faster
        if (collisionDetection(tempCord, Vivarium.predator.coord, 1f)) {
            change = potentialFunction(this.coord, Vivarium.predator.coord, avoidPotential);
            delta.x += change[0];
            delta.y += change[1];
            delta.z += change[2];
        }


        // ge the potential of the walls so we can reflect off of them
        float[][] wallVals = new float[6][3];
        wallVals[0] = potentialFunction(tempCord, new Coord(-1.8, this.coord.y, this.coord.z), -0.01f);
        wallVals[1] = potentialFunction(tempCord, new Coord(1.8, this.coord.y, this.coord.z), -.01f);

        wallVals[2] = potentialFunction(tempCord, new Coord(this.coord.x, -1.8, this.coord.z), -.01f);
        wallVals[3] = potentialFunction(tempCord, new Coord(this.coord.x, 1.8, this.coord.z), -.01f);

        wallVals[4] = potentialFunction(tempCord, new Coord(this.coord.x, this.coord.y, -1.8), -.01f);
        wallVals[5] = potentialFunction(tempCord, new Coord(this.coord.x, this.coord.y, 1.8), -.01f);


        // create the wall to avoid them and reflect off of
        if (collisionDetection(tempCord, new Coord(-1.8, tempCord.y, tempCord.z), 0.25f)) {
            delta.x += wallVals[0][0];
        }
        if (collisionDetection(tempCord, new Coord(1.8, tempCord.y, tempCord.z), 0.25f)) {
            delta.x += wallVals[1][0];
        }
        if (collisionDetection(tempCord, new Coord(tempCord.x, -1.8, tempCord.z), 0.25f)) {
            delta.y += wallVals[2][1];
        }
        if (collisionDetection(tempCord, new Coord(tempCord.x, 1.8, tempCord.z), 0.25f)) {
            delta.y += wallVals[3][1];
        }
        if (collisionDetection(tempCord, new Coord(tempCord.x, tempCord.y, -1.8), 0.25f)) {
            delta.z += wallVals[4][2];
        }
        if (collisionDetection(tempCord, new Coord(tempCord.x, tempCord.y, 1.8), 0.25f)) {
            delta.z += wallVals[5][2];
        }

        if ((coord.x + xSpeed + delta.x) <= -1.8 || (coord.x + xSpeed + delta.x) >= 1.8) {
            collision = true;
        }

        if ((coord.y + ySpeed + delta.y) <= -1.8 || (coord.y + ySpeed + delta.y) >= 1.8) {
            collision = true;
        }

        if ((coord.z + zSpeed + delta.x) <= -1.8 || (coord.z + zSpeed + delta.x) >= 1.8) {
            collision = true;
        }


        // update the location of the fish
        coord.x += xSpeed + delta.x;
        coord.z += zSpeed + delta.z;

        //failed attampt at using the Y axis
//        coord.y += ySpeed + delta.y;


        float hyp = (float) Math.sqrt(Math.pow(xSpeed + delta.x, 2) + Math.pow(zSpeed + delta.z, 2));
        float hypY = (float) Math.sqrt(Math.pow(xSpeed + delta.x + zSpeed + delta.z, 2) + Math.pow(ySpeed + delta.y, 2));

        // change the heading depending on where the fish is going to attempt to go
        if ((xSpeed + delta.x) < 0) {
            angle = 270 + (float) Math.toDegrees(Math.asin((zSpeed + delta.z) / hyp));
        } else {
            angle = 90 - (float) Math.toDegrees(Math.asin((zSpeed + delta.z) / (hyp)));
        }

        //attempt to calculate the Y heading
//        if ((ySpeed + delta.y) > 0) {
//
//            yAngle = (float) Math.toDegrees(Math.asin((ySpeed + delta.y) / hypY));
//
//        } else {
////            if((ySpeed + delta.y) > 0) {
//            yAngle = -(float) Math.toDegrees(Math.asin((ySpeed + delta.y) / hypY));
//
//        }


        // recalcs the speed so movement is porportionate to the heading
        zSpeed = (float) Math.cos(Math.toRadians(angle)) * 0.005f;
        xSpeed = (float) Math.sin(Math.toRadians(angle)) * 0.005f;
        ySpeed = (float) Math.sin(Math.toRadians(yAngle)) * 0.005f;


    }

    // distance helper
    private float distance(Coord a, Coord b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

    //collision helper
    private boolean collisionDetection(Coord a, Coord b, float threshold) {
        if (distance(a, b) < threshold) {
            return true;
        }
        return false;
    }


    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glTranslated(coord.x, coord.y, coord.z);
        //another failed attempt at trying to get the Y axis correct
//        gl.glRotatef(yAngle, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
        gl.glColor3f(0.85f, 0.55f, 0.20f); // Orange
        gl.glPushMatrix();
        gl.glCallList(body);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glRotatef(tailAngle, 0.0f, 1.0f, 0.0f);
        gl.glCallList(tail);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glPopMatrix();


    }


}
