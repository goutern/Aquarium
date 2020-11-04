import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.util.Random;

public class Food {
    //  private int fish_object;
    private float scale;
    private float tailAngle;
    private float tailDelta;
    private float angle;
    private float yAngle = -25.0f;

    private float angleDelta = 0.5f;
    private int body;
    private int tail;
    private float z;
    private float x;
    private float y;
    private float zAccel;
    private float xAccel;
    private float yAccel;
    public float zSpeed = 0.005f;
    public float xSpeed = 0.005f;
    public float ySpeed = -0.001f;
    public boolean xCol = false;
    public boolean zCol = false;
    public boolean yCol = false;
    public boolean eaten = true;


    private float rotationAngle;
    boolean collision = false;

    private float rotation = 0.0f;
    private float speed = 0.0f;
    private Random random = new Random(42);
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


        Coord delta = new Coord();
        Coord change = new Coord();
        if (collisionDetection(coord, Vivarium.predator.coord, 0.2f)) {
            eaten = true;
        }
        for (SmallFry smallFry : Vivarium.smallFries) {
            if (collisionDetection(coord, smallFry.coord, .1f)) {
                eaten = true;
            }
        }

        if ((coord.y + ySpeed) <= -1.8 || (coord.y + ySpeed) >= 1.8) {
            collision = true;
            ySpeed = 0;
        }


//        coord.y += ySpeed;



    }

    private float distance(Coord a, Coord b) {
        return (float) Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2) + Math.pow(a.z - b.z, 2));
    }

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
        gl.glColor3f(0, 1, 0);
        gl.glPushMatrix();
        gl.glCallList(body);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glPopMatrix();
    }


}
