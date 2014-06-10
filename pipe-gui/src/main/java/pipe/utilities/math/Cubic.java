package pipe.utilities.math;

/**
 * Cubic that uses the logic
 * a + b*u + c*u^2 + d*u^3
 */
public class Cubic {

    /**
     * a in cubic expression
     */
   private final float a;

    /**
     * b in cubic expression
     */
    private final float b;

    /**
     * c in cubic expression
     */
    private final float c;


    /**
     * Cubic expression constructor
     * @param _a a in expression
     * @param _b b in expression
     * @param _c c in expression
     * @param _d unused
     */
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
      return (c + 2*b + 3*a)/3;
   }


}
