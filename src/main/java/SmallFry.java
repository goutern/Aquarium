import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import java.util.Random;

public class SmallFry {
    //  private int fish_object;
    private float scale;
    private float tailAngle;
    private float tailDelta;
    private float angle;
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
    public float ySpeed = 0.005f;
    public boolean xCol = false;
    public boolean zCol = false;
    public boolean yCol = false;
    public boolean alive = true;
    public boolean predator = false;


    private float rotationAngle;
    boolean collision = false;

    private float rotation = 0.0f;
    private float speed = 0.0f;
    private Random random = new Random(42);
    public Coord coord;
    float fishPotential = -0.2f;

    public SmallFry(float scale_, float x, float y, float z) {
        coord = new Coord(x, y, z);
        scale = scale_;


        tailAngle = 15.0f;
        tailDelta = 2.0f;
        zAccel = 0.1f;
        xAccel = 0.1f;
        yAccel = 0.001f;
        this.x = x;
        this.y = y;
        this.z = z;


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

    private Coord potentialFunction(Coord p, Coord q, float pot) {
        float x = (float) (2 * (q.x - p.x) * (pot));
        float y = (float) (2 * (q.y - p.y) * (pot));
        float z = (float) (2 * (q.z - p.z) * (pot));

        return new Coord(x, y, z);
    }

    public void update(GL gl) {

        if (tailAngle >= 15 || tailAngle <= -15) {
            tailDelta = -tailDelta;
        }
        tailAngle += tailDelta;
        Coord delta = new Coord();
        for (SmallFry smallFry : Vivarium.smallFries) {
            if (smallFry == this) {
                continue;
            }
            Coord change = new Coord();
//            System.out.println(distance(this.coord, smallFry.coord));
//            if (distance(this.coord, smallFry.coord) < 0.5) {
//                change = potentialFunction(this.coord, smallFry.coord, fishPotential);
//                delta.x += change.x;
//                delta.y += change.y;
//                delta.z += change.z;
//                System.out.println("DeltaX: " + delta.x);
//            }
            Coord tempCord = new Coord(coord.x + xSpeed, coord.y + ySpeed, coord.z + zSpeed);
            if (collisionDetection(tempCord, smallFry.coord, 0.27f)) {
                if (!predator) {
                    collision = true;
                }
//                smallFry.collision = true;
            }
            if (collisionDetection(coord, Vivarium.predator.coord, 0.3f)) {
                if (!predator) {
                    alive = false;
                }
            }

        }
//        zAccel = zSpeed * (float) delta.z;
//        xAccel = xSpeed * (float) delta.x;
//        zSpeed += zAccel;
//        xSpeed += xAccel;

        if ((coord.z + zSpeed) <= -1.8 || (coord.z + zSpeed) >= 1.8) {
            collision = true;
        }

        if ((coord.x + xSpeed) <= -1.8 || (coord.x + xSpeed) >= 1.8) {
            collision = true;
        }
        if ((coord.y + ySpeed) <= -1.8 || (coord.y + ySpeed) >= 1.8) {
            collision = true;
        }

//        System.out.println(x);
//        System.out.println(z);

//        if (collision) {
////            angle = angle + angleDelta;
//            collision = false;
//        } else {
//            angleDelta = -angleDelta;
//        }


        //calc walls
//        Coord wallx = potentialFunction(new Coord(x, y, z),new Coord(3.6, y, z), -.2f);
//        Coord wallnx = potentialFunction(new Coord(x, y, z),new Coord(-3.6, y, z), -.2f);
//        Coord wally = potentialFunction(new Coord(xSpeed, ySpeed, zSpeed),new Coord(-1.8, ySpeed, zSpeed), .5f);
//        Coord wallz = potentialFunction(new Coord(x, y, z),new Coord(x, y, 1.9), -.2f);
//        Coord wallnz = potentialFunction(new Coord(x, y, z),new Coord(x, y, -1.9), -.2f);
//        delta.x += wallx.x +  wallnx.x + wallnz.x + wallz.x;
//        delta.z += wallx.z + wallz.z+ wallnx.z + wallnz.z;

//        float xDirection = (float) delta.x;
//        float zDirection = (float) delta.z;
//        zAccel = zSpeed * xDirection;
//        xAccel = xSpeed * zDirection;
//        angle = (float) getAngleFromPoint(z + zSpeed,z,x + xSpeed,x );

//        System.out.println("Collision: " + collision);
        if (collision) {
            angle = angle + angleDelta;
            collision = false;
            ySpeed = -ySpeed;
        } else {
            angleDelta = random.nextFloat() * 10 - 5;
            coord.x += xSpeed;
            coord.z += zSpeed;
            coord.y += ySpeed;
        }
        ySpeed += (random.nextFloat() * 2 - 1) * 0.00002f;
        zSpeed = (float) Math.cos(Math.toRadians(angle)) * 0.005f;
        xSpeed = (float) Math.sin(Math.toRadians(angle)) * 0.005f;


//        System.out.println("Angle: " + angle);
//        if(collision){
//            xAccel = -xAccel;
//            zAccel = -zAccel;
//        }
//        System.out.println("Xspeed" + angle);

//        System.out.println("X: " + x + " Y: " + y + " Z: " + z);


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

    public double getAngleFromPoint(float x1, float x2, float z1, float z2) {

        if ((x2 > x1)) {
            return (Math.atan2((x2 - x1), (z1 - z2)) * 180 / Math.PI);
        } else if ((x2 < x1)) {
            return 360 - (Math.atan2((x1 - x2), (z1 - z2)) * 180 / Math.PI);
        }
        return Math.atan2(0, 0);

    }


    public void draw(GL2 gl) {
        gl.glPushMatrix();
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glTranslated(coord.x, coord.y, coord.z);
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
