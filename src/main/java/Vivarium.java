

import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.util.*;

public class Vivarium
{
  private Tank tank;
  public static ArrayList<SmallFry> smallFries = new ArrayList<SmallFry>();
  public static SmallFry predator;

  public Vivarium()
  {
    tank = new Tank( 4.0f, 4.0f, 4.0f );

    SmallFry smallFry = new SmallFry( .75f, 0, 0, 0 );
    SmallFry mediumFry = new SmallFry( .75f, .5f, 0, 0.5f );
    predator = new SmallFry(1, 1, 0 ,1);
    predator.predator = true;
    smallFries.add(smallFry);
    smallFries.add(mediumFry);
    smallFries.add(new SmallFry(.75f, -.5f, 0 , -.5f));
    smallFries.add(new SmallFry(.75f, .5f, 0 , -.5f));
    smallFries.add(new SmallFry(.75f, -.5f, 0 , .5f));
//    smallFries.add(new SmallFry(.75f, 1f, 0 , 1f));
    smallFries.add(new SmallFry(.75f, 1f, 0 , -1f));
    smallFries.add(new SmallFry(.75f, -1f, 0 , 1f));
    smallFries.add(new SmallFry(.75f, -1f, 0 , -1f));
  }

  public void init( GL2 gl )
  {
    tank.init( gl );
    for(SmallFry smallFry :smallFries){
      smallFry.init(gl);
    }
    predator.init(gl);
  }

  public void update( GL2 gl )
  {
    tank.update( gl );
    for(SmallFry smallFry :smallFries){
      if( smallFry.alive){
        smallFry.update(gl);
      }
    }
    predator.update(gl);
  }

  public void draw( GL2 gl )
  {
    tank.draw( gl );
    for(SmallFry smallFry :smallFries){
      if(smallFry.alive){
        smallFry.draw(gl);
      }
    }
    predator.draw(gl);
  }
}
