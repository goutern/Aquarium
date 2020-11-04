import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

// Nicholas Goutermout
// CS 680
// PS3
// 11/4

// Class that controls a simple food object
// floats to the bottom slowly if y axis is enabled
// INPUTS : float scale_, float x, float y, float z
// float scale  - Scale of the food
// float x  - starting x location
// float y - starting y location
// float z - starting z location
// OUTPUTS: None
// this class is sued to create a food object for use in the vivarium class

public class Food {
    private float scale;
    private int body;
    public float ySpeed = -0.001f;

    // this is enabled as a user presses the F key
    public boolean eaten = true;
    public Coord coord;


    public Food(float scale_, float x, float y, float z) {
        coord = new Coord(x, y, z);
        scale = scale_;

    }

    public void init(GL2 gl) {
        GLUT glut = new GLUT();
        body = gl.glGenLists(1);
        gl.glNewList(body, GL2.GL_COMPILE);
        gl.glPushMatrix();
        gl.glScalef(0.025f, 0.025f, 0.025f);
        glut.glutSolidSphere(1, 5, 5);
        gl.glPopMatrix();
        gl.glEndList();


    }


    public void update(GL gl) {


        // shark destroys the food on touch
        if (collisionDetection(coord, Vivarium.predator.coord, 0.2f)) {
            eaten = true;
        }

        // when it get eaten by a fish it disappears
        for (SmallFry smallFry : Vivarium.smallFries) {
            if (collisionDetection(coord, smallFry.coord, .1f)) {
                eaten = true;
            }
        }

        // control the sinking so it doesnt go through the bottom
        if ((coord.y + ySpeed) <= -1.8 || (coord.y + ySpeed) >= 1.8) {
            ySpeed = 0;
        }

    }


    //distance helper
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
        gl.glColor3f(0, 1, 0); // green
        gl.glPushMatrix();
        gl.glCallList(body);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glPopMatrix();
    }


}
