import com.jogamp.opengl.util.gl2.GLUT;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.util.Random;

public class Predator {
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
    public float zSpeed = 0.0005f;
    public float xSpeed = 0.0005f;
    public float ySpeed = 0.0005f;
    public boolean xCol = false;
    public boolean zCol = false;
    public boolean yCol = false;
    public boolean predator = false;
    private float yAngle = 0.0f;


    private float rotationAngle;
    boolean collision = false;

    private float rotation = 0.0f;
    private float speed = 0.0f;
    private Random random = new Random(42);
    public Coord coord;
    public Coord coordPrime;
    float chasePotential = 0.0008f;
    float anglex = 0;

    public Predator(float scale_, float x, float y, float z) {
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

    private float[] potentialFunction(Coord p, Coord q, float pot) {
        float[] vals = new float[3];
        vals[0] = (float) (2 * (q.x - p.x) * pot);
        vals[1] = (float) (2 * (q.y - p.y) * pot);
        vals[2] = (float) (2 * (q.z - p.z) * pot);

        return vals;
    }

    public void update(GL gl) {

        if (tailAngle >= 15 || tailAngle <= -15) {
            tailDelta = -tailDelta;
        }
        tailAngle += tailDelta;
        Coord delta = new Coord();

        float[] vals;
        for (SmallFry smallFry : Vivarium.smallFries) {
            if (smallFry.alive) {
                if (collisionDetection(Vivarium.predator.coord, smallFry.coord, 1f)) {
                    vals = potentialFunction(Vivarium.predator.coord, smallFry.coord, chasePotential);
                    delta.x += vals[0];
                    delta.y += vals[1];
                    delta.z += vals[2];
                }
            }
        }

        Coord tempCord = new Coord(coord.x + xSpeed, coord.y + ySpeed, coord.z + zSpeed);
        float[][] wallVals = new float[6][3];
        wallVals[0] = potentialFunction(tempCord, new Coord(-1.8, this.coord.y, this.coord.z), -0.01f);
        wallVals[1] = potentialFunction(tempCord, new Coord(1.8, this.coord.y, this.coord.z), -.01f);

        wallVals[2] = potentialFunction(tempCord, new Coord(this.coord.x, -1.8, this.coord.z), -.01f);
        wallVals[3] = potentialFunction(tempCord, new Coord(this.coord.x, 1.8, this.coord.z), -.01f);

        wallVals[4] = potentialFunction(tempCord, new Coord(this.coord.x, this.coord.y, -1.8), -.01f);
        wallVals[5] = potentialFunction(tempCord, new Coord(this.coord.x, this.coord.y, 1.8), -.01f);

        if (collisionDetection(tempCord, new Coord(-1.8, this.coord.y, this.coord.z), 0.25f)) {
            delta.x += wallVals[0][0];
        }
        if (collisionDetection(tempCord, new Coord(1.8, this.coord.y, this.coord.z), 0.25f)) {
            delta.x += wallVals[1][0];
        }
        if (collisionDetection(tempCord, new Coord(this.coord.x, -1.8, this.coord.z), 0.25f)) {
            delta.y += wallVals[2][1];
        }
        if (collisionDetection(tempCord, new Coord(this.coord.x, 1.8, this.coord.z), 0.25f)) {
            delta.y += wallVals[3][1];
        }
        if (collisionDetection(tempCord, new Coord(this.coord.x, this.coord.y, -1.8), 0.25f)) {
            delta.z += wallVals[4][2];
        }
        if (collisionDetection(tempCord, new Coord(this.coord.x, this.coord.y, 1.8), 0.25f)) {
            delta.z += wallVals[5][2];
        }


        if ((coord.x + xSpeed) <= -1.8 || (coord.x + xSpeed) >= 1.8) {
            collision = true;

        }

        if ((coord.y + ySpeed) <= -1.8 || (coord.y + ySpeed) >= 1.8) {
            collision = true;
        }

        if ((coord.z + zSpeed) <= -1.8 || (coord.z + zSpeed) >= 1.8) {
            collision = true;
        }


        coordPrime = new Coord(coord.x, coord.y, coord.z);

        angleDelta = random.nextFloat() * 10 - 5;

        coord.x += xSpeed + delta.x;
        coord.z += zSpeed + delta.z;
//        coord.y += ySpeed + delta.y;


        float hyp = (float) Math.sqrt(Math.pow(xSpeed + delta.x, 2) + Math.pow(zSpeed + delta.z, 2));
        float hypY = (float) Math.sqrt(Math.pow(xSpeed + delta.x + zSpeed + delta.z, 2) + Math.pow(ySpeed + delta.y, 2));


        if ((xSpeed + delta.x) < 0) {
            angle = 270 + (float) Math.toDegrees(Math.asin((zSpeed + delta.z) / hyp));
        } else {
            angle = 90 - (float) Math.toDegrees(Math.asin((zSpeed + delta.z) / hyp));
        }

        if ((ySpeed + delta.y) > 0) {

            yAngle = -(float) Math.toDegrees(Math.asin((ySpeed + delta.y) / hypY));

        } else {

            yAngle = (float) Math.toDegrees(Math.asin((ySpeed + delta.y) / hypY));

        }



        zSpeed = (float) Math.cos(Math.toRadians(angle)) * 0.005f;
        xSpeed = (float) Math.sin(Math.toRadians(angle)) * 0.005f;
        ySpeed = (float) Math.sin(Math.toRadians(yAngle)) * 0.005f;




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

    public double getAngleFromPoint(double x1, double x2, double z1, double z2) {

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

//        float deltaX = (float)(coordPrime.x - coord.x);
//        float deltaZ = (float)(coordPrime.z - coord.z);
//        float magnitude = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
//        float[] v = new float[3];
//        v[0] = deltaX / magnitude;
//        v[1] = 0.0f;
//        v[2] = deltaZ / magnitude;
//
//        float[] upVector = {0.0f, 1.0f, 0.0f};
//        float[] rotate = { v[1] * upVector[2] - upVector[1] * v[2],
//                v[0] * upVector[2] - upVector[0] * v[2], v[0] * upVector[1] - upVector[0] * v[1] };
//
//        magnitude = (float) Math.sqrt(rotate[0] * rotate[0] + rotate[1] * rotate[1] + rotate[2]
//                * rotate[2]);
//
//        rotate[0] = rotate[0]/magnitude;
//        rotate[1] = rotate[1]/magnitude;
//        rotate[2] = rotate[2]/magnitude;
//
//        float[] perpendicular = { v[1] * rotate[2] - rotate[1] * v[2],
//                v[0] * rotate[2] - rotate[0] * v[2],
//                v[0] * rotate[1] - rotate[0] * v[1] };
//
//        magnitude =(float) Math.sqrt(perpendicular[0] * perpendicular[0] + perpendicular[1] * perpendicular[1]
//                + perpendicular[2] * perpendicular[2]);
//
//        perpendicular[0] = perpendicular[0] / magnitude;
//        perpendicular[1] = perpendicular[1] / magnitude;
//        perpendicular[2] = perpendicular[2] / magnitude;
//
//        float[] matrix = {rotate[0], rotate[1], rotate[2], perpendicular[0], perpendicular[1], perpendicular[2], 0.0f, v[0],v[1], v[2], 0.0f,(float) coord.x, (float)coord.y, (float)coord.z, 1.0f};
//        gl.glMultMatrixf(matrix, 0);


        gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);
        gl.glColor3f(1f, 0.0f, 0.0f); // Orange
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
