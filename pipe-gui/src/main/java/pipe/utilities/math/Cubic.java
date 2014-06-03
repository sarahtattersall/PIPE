package pipe.utilities.math;

/**
 * Cubic that uses the logic
 * a + b*u + c*u^2 + d*u^3
 */
public class Cubic {

   private final float a;
    private final float b;
    private final float c;


    public Cubic(float _a, float _b, float _c, float _d) {
      a = _a;
      b = _b;
      c = _c;
    }


   // Return first control point coordinate (calculated from coefficients)
   public float getX1() {
      return (b + 3*a)/3;
   }
   

   // Return second control point coordinate (calculated from coefficients)
   public float getX2() {
      return ((c + 2*b + 3*a)/3);
   }


}
