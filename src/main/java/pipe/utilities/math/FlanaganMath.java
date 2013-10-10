/*
*   Class   Fmath
*
*   USAGE:  Mathematical class that supplements java.lang.Math and contains:
*               the main physical constants
*               trigonemetric functions absent from java.lang.Math
*               some useful additional mathematical functions
*               some conversion functions
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:    June 2002
*   AMENDED: 6 January 2006, 12 April 2006, 5 May 2006, 28 July 2006, 27 December 2006,
*            29 March 2007, 29 April 2007, 2,9 & 15 June 2007
*
*   DOCUMENTATION:
*   See Michael Thomas Flanagan's Java library on-line web pages:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*   http://www.ee.ucl.ac.uk/~mflanaga/java/Fmath.html
*
*   Copyright (c) June 2002, June 2007
*
*   PERMISSION TO COPY:
*   Permission to use, copy and modify this software and its documentation for
*   NON-COMMERCIAL purposes is granted, without fee, provided that an acknowledgement
*   to the author, Michael Thomas Flanagan at www.ee.ucl.ac.uk/~mflanaga, appears in all copies.
*
*   Dr Michael Thomas Flanagan makes no representations about the suitability
*   or fitness of the software for any or for a particular purpose.
*   Michael Thomas Flanagan shall not be liable for any damages suffered
*   as a result of using, modifying or distributing this software or its derivatives.
*
***************************************************************************************/

package pipe.utilities.math;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

class FlanaganMath
{

    // PHYSICAL CONSTANTS

    public static final double N_AVAGADRO = 6.0221419947e23;        /*      mol^-1          */
    public static final double K_BOLTZMANN = 1.380650324e-23;       /*      J K^-1          */
    private static final double H_PLANCK = 6.6260687652e-34;         /*      J s             */
    public static final double H_PLANCK_RED = H_PLANCK / (2 * Math.PI); /*      J s             */
    private static final double C_LIGHT = 2.99792458e8;              /*      m s^-1          */
    public static final double R_GAS = 8.31447215;                  /*      J K^-1 mol^-1   */
    public static final double F_FARADAY = 9.6485341539e4;          /*      C mol^-1        */
    private static final double T_ABS = -273.15;                     /*      Celsius         */
    private static final double Q_ELECTRON = -1.60217646263e-19;     /*      C               */
    public static final double M_ELECTRON = 9.1093818872e-31;       /*      kg              */
    public static final double M_PROTON = 1.6726215813e-27;         /*      kg              */
    public static final double M_NEUTRON = 1.6749271613e-27;        /*      kg              */
    public static final double EPSILON_0 = 8.854187817e-12;         /*      F m^-1          */
    public static final double MU_0 = Math.PI * 4e-7;                 /*      H m^-1 (N A^-2) */

    // MATHEMATICAL CONSTANTS
    public static final double EULER_CONSTANT_GAMMA = 0.5772156649015627;
    public static final double PI = Math.PI;                        /*  3.141592653589793D  */
    public static final double E = Math.E;                          /*  2.718281828459045D  */


    // METHODS




    // LOGARITHMS
    // Log to base 10 of a double number
    public static double log10(double a)
    {
        return Math.log(a) / Math.log(10.0D);
    }

    // Log to base 10 of a float number
    public static float log10(float a)
    {
        return (float) (Math.log((double) a) / Math.log(10.0D));
    }

    // Base 10 antilog of a double
    public static double antilog10(double x)
    {
        return Math.pow(10.0D, x);
    }

    // Base 10 antilog of a float
    public static float antilog10(float x)
    {
        return (float) Math.pow(10.0D, (double) x);
    }

    // Log to base e of a double number
    public static double log(double a)
    {
        return Math.log(a);
    }

    // Log to base e of a float number
    public static float log(float a)
    {
        return (float) Math.log((double) a);
    }

    // Base e antilog of a double
    public static double antilog(double x)
    {
        return Math.exp(x);
    }

    // Base e antilog of a float
    public static float antilog(float x)
    {
        return (float) Math.exp((double) x);
    }

    // Log to base 2 of a double number
    public static double log2(double a)
    {
        return Math.log(a) / Math.log(2.0D);
    }

    // Log to base 2 of a float number
    public static float log2(float a)
    {
        return (float) (Math.log((double) a) / Math.log(2.0D));
    }

    // Base 2 antilog of a double
    public static double antilog2(double x)
    {
        return Math.pow(2.0D, x);
    }

    // Base 2 antilog of a float
    public static float antilog2(float x)
    {
        return (float) Math.pow(2.0D, (double) x);
    }

    // Log to base b of a double number and double base
    public static double log10(double a, double b)
    {
        return Math.log(a) / Math.log(b);
    }

    // Log to base b of a double number and int base
    public static double log10(double a, int b)
    {
        return Math.log(a) / Math.log((double) b);
    }

    // Log to base b of a float number and flaot base
    public static float log10(float a, float b)
    {
        return (float) (Math.log((double) a) / Math.log((double) b));
    }

    // Log to base b of a float number and int base
    public static float log10(float a, int b)
    {
        return (float) (Math.log((double) a) / Math.log((double) b));
    }

    // Square of a double number
    public static double square(double a)
    {
        return a * a;
    }

    // Square of a float number
    public static float square(float a)
    {
        return a * a;
    }

    // Square of an int number
    public static int square(int a)
    {
        return a * a;
    }

    // factorial of n
    // argument and return are integer, therefore limited to 0<=n<=12
    // see below for long and double arguments
    public static int factorial(int n)
    {
        if(n < 0) throw new IllegalArgumentException("n must be a positive integer");
        if(n > 12)
            throw new IllegalArgumentException("n must less than 13 to avoid integer overflow\nTry long or double argument");
        int f = 1;
        for(int i = 2; i <= n; i++) f *= i;
        return f;
    }

    // factorial of n
    // argument and return are long, therefore limited to 0<=n<=20
    // see below for double argument
    public static long factorial(long n)
    {
        if(n < 0) throw new IllegalArgumentException("n must be a positive integer");
        if(n > 20)
            throw new IllegalArgumentException("n must less than 21 to avoid long integer overflow\nTry double argument");
        long f = 1;
        long iCount = 2L;
        while(iCount <= n)
        {
            f *= iCount;
            iCount += 1L;
        }
        return f;
    }

    // factorial of n
    // Argument is of type double but must be, numerically, an integer
    // factorial returned as double but is, numerically, should be an integer
    // numerical rounding may makes this an approximation after n = 21
    public static double factorial(double n)
    {
        if(n < 0 || (n - Math.floor(n)) != 0)
            throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [FlanaganMath.gamma(x)] more appropriate?");
        double f = 1.0D;
        double iCount = 2.0D;
        while(iCount <= n)
        {
            f *= iCount;
            iCount += 1.0D;
        }
        return f;
    }

    // log to base e of the factorial of n
    // log[e](factorial) returned as double
    // numerical rounding may makes this an approximation
    public static double logFactorial(int n)
    {
        if(n < 0 || (n - n) != 0)
            throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [FlanaganMath.gamma(x)] more appropriate?");
        double f = 0.0D;
        for(int i = 2; i <= n; i++) f += Math.log(i);
        return f;
    }

    // log to base e of the factorial of n
    // Argument is of type double but must be, numerically, an integer
    // log[e](factorial) returned as double
    // numerical rounding may makes this an approximation
    public static double logFactorial(long n)
    {
        if(n < 0 || (n - Math.floor(n)) != 0)
            throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [FlanaganMath.gamma(x)] more appropriate?");
        double f = 0.0D;
        long iCount = 2L;
        while(iCount <= n)
        {
            f += Math.log(iCount);
            iCount += 1L;
        }
        return f;
    }

    // log to base e of the factorial of n
    // Argument is of type double but must be, numerically, an integer
    // log[e](factorial) returned as double
    // numerical rounding may makes this an approximation
    public static double logFactorial(double n)
    {
        if(n < 0 || (n - Math.floor(n)) != 0)
            throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [FlanaganMath.gamma(x)] more appropriate?");
        double f = 0.0D;
        double iCount = 2.0D;
        while(iCount <= n)
        {
            f += Math.log(iCount);
            iCount += 1.0D;
        }
        return f;
    }

    // Maximum of a 1D array of doubles, aa
    public static double maximum(double[] aa)
    {
        int n = aa.length;
        double aamax = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] > aamax) aamax = aa[i];
        }
        return aamax;
    }

    // Maximum of a 1D array of floats, aa
    public static float maximum(float[] aa)
    {
        int n = aa.length;
        float aamax = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] > aamax) aamax = aa[i];
        }
        return aamax;
    }

    // Maximum of a 1D array of ints, aa
    public static int maximum(int[] aa)
    {
        int n = aa.length;
        int aamax = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] > aamax) aamax = aa[i];
        }
        return aamax;
    }

    // Maximum of a 1D array of longs, aa
    public static long maximum(long[] aa)
    {
        long n = aa.length;
        long aamax = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] > aamax) aamax = aa[i];
        }
        return aamax;
    }

    // Minimum of a 1D array of doubles, aa
    public static double minimum(double[] aa)
    {
        int n = aa.length;
        double aamin = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] < aamin) aamin = aa[i];
        }
        return aamin;
    }

    // Minimum of a 1D array of floats, aa
    public static float minimum(float[] aa)
    {
        int n = aa.length;
        float aamin = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] < aamin) aamin = aa[i];
        }
        return aamin;
    }

    // Minimum of a 1D array of ints, aa
    public static int minimum(int[] aa)
    {
        int n = aa.length;
        int aamin = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] < aamin) aamin = aa[i];
        }
        return aamin;
    }

    // Minimum of a 1D array of longs, aa
    public static long minimum(long[] aa)
    {
        long n = aa.length;
        long aamin = aa[0];
        for(int i = 1; i < n; i++)
        {
            if(aa[i] < aamin) aamin = aa[i];
        }
        return aamin;
    }

    // Reverse the order of the elements of a 1D array of doubles, aa
    public static double[] reverseArray(double[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[n - 1 - i];
        }
        return bb;
    }

    // Reverse the order of the elements of a 1D array of floats, aa
    public static float[] reverseArray(float[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[n - 1 - i];
        }
        return bb;
    }

    // Reverse the order of the elements of a 1D array of ints, aa
    public static int[] reverseArray(int[] aa)
    {
        int n = aa.length;
        int[] bb = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[n - 1 - i];
        }
        return bb;
    }

    // Reverse the order of the elements of a 1D array of longs, aa
    public static long[] reverseArray(long[] aa)
    {
        int n = aa.length;
        long[] bb = new long[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[n - 1 - i];
        }
        return bb;
    }

    // Reverse the order of the elements of a 1D array of char, aa
    public static char[] reverseArray(char[] aa)
    {
        int n = aa.length;
        char[] bb = new char[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[n - 1 - i];
        }
        return bb;
    }

    // return absolute values of an array of doubles
    public static double[] arrayAbs(double[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = Math.abs(aa[i]);
        }
        return bb;
    }

    // return absolute values of an array of floats
    public static float[] arrayAbs(float[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = Math.abs(aa[i]);
        }
        return bb;
    }

    // return absolute values of an array of long
    public static long[] arrayAbs(long[] aa)
    {
        int n = aa.length;
        long[] bb = new long[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = Math.abs(aa[i]);
        }
        return bb;
    }

    // return absolute values of an array of int
    public static int[] arrayAbs(int[] aa)
    {
        int n = aa.length;
        int[] bb = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = Math.abs(aa[i]);
        }
        return bb;
    }

    // multiple all elements by a constant double[] by double -> double[]
    public static double[] arrayMultByConstant(double[] aa, double constant)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[i] * constant;
        }
        return bb;
    }

    // multiple all elements by a constant int[] by double -> double[]
    public static double[] arrayMultByConstant(int[] aa, double constant)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) aa[i] * constant;
        }
        return bb;
    }

    // multiple all elements by a constant double[] by int -> double[]
    public static double[] arrayMultByConstant(double[] aa, int constant)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[i] * (double) constant;
        }
        return bb;
    }

    // multiple all elements by a constant int[] by int -> double[]
    public static double[] arrayMultByConstant(int[] aa, int constant)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) (aa[i] * constant);
        }
        return bb;
    }

    // invert all elements of an array of doubles
    public static double[] invertElements(double[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++) bb[i] = 1.0D / aa[i];
        return bb;
    }

    // invert all elements of an array of floats
    public static float[] invertElements(float[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++) bb[i] = 1.0F / aa[i];
        return bb;
    }

    // finds the index of the element equal to a given value in an array of doubles
    // returns -1 if none found
    public static int indexOf(double[] array, double value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of floats
    // returns -1 if none found
    public static int indexOf(float[] array, float value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of longs
    // returns -1 if none found
    public static int indexOf(long[] array, long value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of ints
    // returns -1 if none found
    public static int indexOf(int[] array, int value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of bytes
    // returns -1 if none found
    public static int indexOf(byte[] array, byte value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of shorts
    // returns -1 if none found
    public static int indexOf(short[] array, short value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of chars
    // returns -1 if none found
    public static int indexOf(char[] array, char value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter] == value)
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the index of the element equal to a given value in an array of Strings
    // returns -1 if none found
    public static int indexOf(String[] array, String value)
    {
        int index = -1;
        boolean test = true;
        int counter = 0;
        while(test)
        {
            if(array[counter].equals(value))
            {
                index = counter;
                test = false;
            }
            else
            {
                counter++;
                if(counter >= array.length) test = false;
            }
        }
        return index;
    }

    // finds the value of nearest element value in array to the argument value
    public static double nearestElementValue(double[] array, double value)
    {
        double diff = Math.abs(array[0] - value);
        double nearest = array[0];
        for(int i = 1; i < array.length; i++)
        {
            if(Math.abs(array[i] - value) < diff)
            {
                diff = Math.abs(array[i] - value);
                nearest = array[i];
            }
        }
        return nearest;
    }

    // finds the index of nearest element value in array to the argument value
    public static int nearestElementIndex(double[] array, double value)
    {
        double diff = Math.abs(array[0] - value);
        int nearest = 0;
        for(int i = 1; i < array.length; i++)
        {
            if(Math.abs(array[i] - value) < diff)
            {
                diff = Math.abs(array[i] - value);
                nearest = i;
            }
        }
        return nearest;
    }

    // finds the value of nearest lower element value in array to the argument value
    public static double nearestLowerElementValue(double[] array, double value)
    {
        double diff0 = 0.0D;
        double diff1 = 0.0D;
        double nearest = 0.0D;
        int ii = 0;
        boolean test = true;
        double min = array[0];
        while(test)
        {
            if(array[ii] < min) min = array[ii];
            if((value - array[ii]) >= 0.0D)
            {
                diff0 = value - array[ii];
                nearest = array[ii];
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = min;
                    diff0 = min - value;
                    test = false;
                }
            }
        }
        for(double anArray : array)
        {
            diff1 = value - anArray;
            if(diff1 >= 0.0D && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = anArray;
            }
        }
        return nearest;
    }

    // finds the index of nearest lower element value in array to the argument value
    public static int nearestLowerElementIndex(double[] array, double value)
    {
        double diff0 = 0.0D;
        double diff1 = 0.0D;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        double min = array[0];
        int minI = 0;
        while(test)
        {
            if(array[ii] < min)
            {
                min = array[ii];
                minI = ii;
            }
            if((value - array[ii]) >= 0.0D)
            {
                diff0 = value - array[ii];
                nearest = ii;
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = minI;
                    diff0 = min - value;
                    test = false;
                }
            }
        }
        for(int i = 0; i < array.length; i++)
        {
            diff1 = value - array[i];
            if(diff1 >= 0.0D && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = i;
            }
        }
        return nearest;
    }

    // finds the value of nearest higher element value in array to the argument value
    public static double nearestHigherElementValue(double[] array, double value)
    {
        double diff0 = 0.0D;
        double diff1 = 0.0D;
        double nearest = 0.0D;
        int ii = 0;
        boolean test = true;
        double max = array[0];
        while(test)
        {
            if(array[ii] > max) max = array[ii];
            if((array[ii] - value) >= 0.0D)
            {
                diff0 = value - array[ii];
                nearest = array[ii];
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = max;
                    diff0 = value - max;
                    test = false;
                }
            }
        }
        for(double anArray : array)
        {
            diff1 = anArray - value;
            if(diff1 >= 0.0D && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = anArray;
            }
        }
        return nearest;
    }

    // finds the index of nearest higher element value in array to the argument value
    public static int nearestHigherElementIndex(double[] array, double value)
    {
        double diff0 = 0.0D;
        double diff1 = 0.0D;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        double max = array[0];
        int maxI = 0;
        while(test)
        {
            if(array[ii] > max)
            {
                max = array[ii];
                maxI = ii;
            }
            if((array[ii] - value) >= 0.0D)
            {
                diff0 = value - array[ii];
                nearest = ii;
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = maxI;
                    diff0 = value - max;
                    test = false;
                }
            }
        }
        for(int i = 0; i < array.length; i++)
        {
            diff1 = array[i] - value;
            if(diff1 >= 0.0D && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = i;
            }
        }
        return nearest;
    }


    // finds the value of nearest element value in array to the argument value
    public static int nearestElementValue(int[] array, int value)
    {
        int diff = Math.abs(array[0] - value);
        int nearest = array[0];
        for(int i = 1; i < array.length; i++)
        {
            if(Math.abs(array[i] - value) < diff)
            {
                diff = Math.abs(array[i] - value);
                nearest = array[i];
            }
        }
        return nearest;
    }

    // finds the index of nearest element value in array to the argument value
    public static int nearestElementIndex(int[] array, int value)
    {
        int diff = Math.abs(array[0] - value);
        int nearest = 0;
        for(int i = 1; i < array.length; i++)
        {
            if(Math.abs(array[i] - value) < diff)
            {
                diff = Math.abs(array[i] - value);
                nearest = i;
            }
        }
        return nearest;
    }

    // finds the value of nearest lower element value in array to the argument value
    public static int nearestLowerElementValue(int[] array, int value)
    {
        int diff0 = 0;
        int diff1 = 0;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        int min = array[0];
        while(test)
        {
            if(array[ii] < min) min = array[ii];
            if((value - array[ii]) >= 0)
            {
                diff0 = value - array[ii];
                nearest = array[ii];
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = min;
                    diff0 = min - value;
                    test = false;
                }
            }
        }
        for(int anArray : array)
        {
            diff1 = value - anArray;
            if(diff1 >= 0 && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = anArray;
            }
        }
        return nearest;
    }

    // finds the index of nearest lower element value in array to the argument value
    public static int nearestLowerElementIndex(int[] array, int value)
    {
        int diff0 = 0;
        int diff1 = 0;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        int min = array[0];
        int minI = 0;
        while(test)
        {
            if(array[ii] < min)
            {
                min = array[ii];
                minI = ii;
            }
            if((value - array[ii]) >= 0)
            {
                diff0 = value - array[ii];
                nearest = ii;
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = minI;
                    diff0 = min - value;
                    test = false;
                }
            }
        }
        for(int i = 0; i < array.length; i++)
        {
            diff1 = value - array[i];
            if(diff1 >= 0 && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = i;
            }
        }
        return nearest;
    }

    // finds the value of nearest higher element value in array to the argument value
    public static int nearestHigherElementValue(int[] array, int value)
    {
        int diff0 = 0;
        int diff1 = 0;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        int max = array[0];
        while(test)
        {
            if(array[ii] > max) max = array[ii];
            if((array[ii] - value) >= 0)
            {
                diff0 = value - array[ii];
                nearest = array[ii];
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = max;
                    diff0 = value - max;
                    test = false;
                }
            }
        }
        for(int anArray : array)
        {
            diff1 = anArray - value;
            if(diff1 >= 0 && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = anArray;
            }
        }
        return nearest;
    }

    // finds the index of nearest higher element value in array to the argument value
    public static int nearestHigherElementIndex(int[] array, int value)
    {
        int diff0 = 0;
        int diff1 = 0;
        int nearest = 0;
        int ii = 0;
        boolean test = true;
        int max = array[0];
        int maxI = 0;
        while(test)
        {
            if(array[ii] > max)
            {
                max = array[ii];
                maxI = ii;
            }
            if((array[ii] - value) >= 0)
            {
                diff0 = value - array[ii];
                nearest = ii;
                test = false;
            }
            else
            {
                ii++;
                if(ii > array.length - 1)
                {
                    nearest = maxI;
                    diff0 = value - max;
                    test = false;
                }
            }
        }
        for(int i = 0; i < array.length; i++)
        {
            diff1 = array[i] - value;
            if(diff1 >= 0 && diff1 < diff0)
            {
                diff0 = diff1;
                nearest = i;
            }
        }
        return nearest;
    }

    // Sum of all array elements - double array
    public static double arraySum(double[] array)
    {
        double sum = 0.0D;
        for(double i : array) sum += i;
        return sum;
    }

    // Sum of all array elements - float array
    public static float arraySum(float[] array)
    {
        float sum = 0.0F;
        for(float i : array) sum += i;
        return sum;
    }

    // Sum of all array elements - int array
    public static int arraySum(int[] array)
    {
        int sum = 0;
        for(int i : array) sum += i;
        return sum;
    }

    // Sum of all array elements - long array
    public static long arraySum(long[] array)
    {
        long sum = 0L;
        for(long i : array) sum += i;
        return sum;
    }

    // Product of all array elements - double array
    public static double arrayProduct(double[] array)
    {
        double product = 1.0D;
        for(double i : array) product *= i;
        return product;
    }

    // Product of all array elements - float array
    public static float arrayProduct(float[] array)
    {
        float product = 1.0F;
        for(float i : array) product *= i;
        return product;
    }

    // Product of all array elements - int array
    public static int arrayProduct(int[] array)
    {
        int product = 1;
        for(int i : array) product *= i;
        return product;
    }

    // Product of all array elements - long array
    public static long arrayProduct(long[] array)
    {
        long product = 1L;
        for(long i : array) product *= i;
        return product;
    }

    // Concatenate two double arrays
    public static double[] concatenate(double[] aa, double[] bb)
    {
        int aLen = aa.length;
        int bLen = bb.length;
        int cLen = aLen + bLen;
        double[] cc = new double[cLen];
        System.arraycopy(aa, 0, cc, 0, aLen);
        System.arraycopy(bb, 0, cc, 0 + aLen, bLen);

        return cc;
    }

    // Concatenate two float arrays
    public static float[] concatenate(float[] aa, float[] bb)
    {
        int aLen = aa.length;
        int bLen = bb.length;
        int cLen = aLen + bLen;
        float[] cc = new float[cLen];
        System.arraycopy(aa, 0, cc, 0, aLen);
        System.arraycopy(bb, 0, cc, 0 + aLen, bLen);

        return cc;
    }

    // Concatenate two int arrays
    public static int[] concatenate(int[] aa, int[] bb)
    {
        int aLen = aa.length;
        int bLen = bb.length;
        int cLen = aLen + bLen;
        int[] cc = new int[cLen];
        System.arraycopy(aa, 0, cc, 0, aLen);
        System.arraycopy(bb, 0, cc, 0 + aLen, bLen);

        return cc;
    }

    // Concatenate two long arrays
    public static long[] concatenate(long[] aa, long[] bb)
    {
        int aLen = aa.length;
        int bLen = bb.length;
        int cLen = aLen + bLen;
        long[] cc = new long[cLen];
        System.arraycopy(aa, 0, cc, 0, aLen);
        System.arraycopy(bb, 0, cc, 0 + aLen, bLen);

        return cc;
    }

    // recast an array of float as doubles
    public static double[] floatTOdouble(float[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) aa[i];
        }
        return bb;
    }

    // recast an array of int as double
    public static double[] intTOdouble(int[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) aa[i];
        }
        return bb;
    }

    // recast an array of int as float
    public static float[] intTOfloat(int[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (float) aa[i];
        }
        return bb;
    }

    // recast an array of int as long
    public static long[] intTOlong(int[] aa)
    {
        int n = aa.length;
        long[] bb = new long[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (long) aa[i];
        }
        return bb;
    }

    // recast an array of long as double
    // BEWARE POSSIBLE LOSS OF PRECISION
    public static double[] longTOdouble(long[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) aa[i];
        }
        return bb;
    }

    // recast an array of long as float
    // BEWARE POSSIBLE LOSS OF PRECISION
    public static float[] longTOfloat(long[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (float) aa[i];
        }
        return bb;
    }

    // recast an array of short as double
    public static double[] shortTOdouble(short[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (double) aa[i];
        }
        return bb;
    }

    // recast an array of short as float
    public static float[] shortTOfloat(short[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (float) aa[i];
        }
        return bb;
    }

    // recast an array of short as long
    public static long[] shortTOlong(short[] aa)
    {
        int n = aa.length;
        long[] bb = new long[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (long) aa[i];
        }
        return bb;
    }

    // recast an array of short as int
    public static int[] shortTOint(short[] aa)
    {
        int n = aa.length;
        int[] bb = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (int) aa[i];
        }
        return bb;
    }

    // recast an array of byte as double
    public static double[] byteTOdouble(byte[] aa)
    {
        int n = aa.length;
        double[] bb = new double[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (int) aa[i];
        }
        return bb;
    }

    // recast an array of byte as float
    public static float[] byteTOfloat(byte[] aa)
    {
        int n = aa.length;
        float[] bb = new float[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (float) aa[i];
        }
        return bb;
    }

    // recast an array of byte as long
    public static long[] byteTOlong(byte[] aa)
    {
        int n = aa.length;
        long[] bb = new long[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (long) aa[i];
        }
        return bb;
    }

    // recast an array of byte as int
    public static int[] byteTOint(byte[] aa)
    {
        int n = aa.length;
        int[] bb = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (int) aa[i];
        }
        return bb;
    }

    // recast an array of byte as short
    public static short[] byteTOshort(byte[] aa)
    {
        int n = aa.length;
        short[] bb = new short[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (short) aa[i];
        }
        return bb;
    }

    // recast an array of double as int
    // BEWARE OF LOSS OF PRECISION
    public static int[] doubleTOint(double[] aa)
    {
        int n = aa.length;
        int[] bb = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = (int) aa[i];
        }
        return bb;
    }

    // print an array of doubles to screen
    // No line returns except at the end
    public static void print(double[] aa)
    {
        for(double anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of doubles to screen
    // with line returns
    public static void println(double[] aa)
    {
        for(double anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // print an array of floats to screen
    // No line returns except at the end
    public static void print(float[] aa)
    {
        for(float anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of floats to screen
    // with line returns
    public static void println(float[] aa)
    {
        for(float anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // print an array of ints to screen
    // No line returns except at the end
    public static void print(int[] aa)
    {
        for(int anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of ints to screen
    // with line returns
    public static void println(int[] aa)
    {
        for(int anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // print an array of longs to screen
    // No line returns except at the end
    public static void print(long[] aa)
    {
        for(long anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of longs to screen
    // with line returns
    public static void println(long[] aa)
    {
        for(long anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // print an array of char to screen
    // No line returns except at the end
    public static void print(char[] aa)
    {
        for(char anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of char to screen
    // with line returns
    public static void println(char[] aa)
    {
        for(char anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // print an array of String to screen
    // No line returns except at the end
    public static void print(String[] aa)
    {
        for(String anAa : aa)
        {
            System.out.print(anAa + "   ");
        }
        System.out.println();
    }

    // print an array of Strings to screen
    // with line returns
    public static void println(String[] aa)
    {
        for(String anAa : aa)
        {
            System.out.println(anAa + "   ");
        }
    }

    // sort elements in an array of doubles into ascending order
    // using selection sort method
    // returns Vector containing the original array, the sorted array
    //  and an array of the indices of the sorted array
    public static Vector<Object> selectSortVector(double[] aa)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        double holdb = 0.0D;
        int holdi = 0;
        double[] bb = new double[n];
        int[] indices = new int[n];
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[i];
            indices[i] = i;
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdb = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = holdb;
            holdi = indices[index];
            indices[index] = indices[lastIndex];
            indices[lastIndex] = holdi;
        }
        Vector<Object> vec = new Vector<Object>();
        vec.addElement(aa);
        vec.addElement(bb);
        vec.addElement(indices);
        return vec;
    }

    // sort elements in an array of doubles into ascending order
    // using selection sort method
    public static double[] selectionSort(double[] aa)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        double hold = 0.0D;
        double[] bb = new double[n];
        System.arraycopy(aa, 0, bb, 0, n);

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            hold = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = hold;
        }
        return bb;
    }

    // sort elements in an array of floats into ascending order
    // using selection sort method
    public static float[] selectionSort(float[] aa)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        float hold = 0.0F;
        float[] bb = new float[n];
        System.arraycopy(aa, 0, bb, 0, n);

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            hold = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = hold;
        }
        return bb;
    }

    // sort elements in an array of ints into ascending order
    // using selection sort method
    public static int[] selectionSort(int[] aa)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int hold = 0;
        int[] bb = new int[n];
        System.arraycopy(aa, 0, bb, 0, n);

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            hold = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = hold;
        }
        return bb;
    }

    // sort elements in an array of longs into ascending order
    // using selection sort method
    public static long[] selectionSort(long[] aa)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        long hold = 0L;
        long[] bb = new long[n];
        System.arraycopy(aa, 0, bb, 0, n);

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            hold = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = hold;
        }
        return bb;
    }

    // sort elements in an array of doubles into ascending order
    // using selection sort method
    // returns Vector containing the original array, the sorted array
    //  and an array of the indices of the sorted array
    public static void selectionSort(double[] aa, double[] bb, int[] indices)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        double holdb = 0.0D;
        int holdi = 0;
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[i];
            indices[i] = i;
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdb = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = holdb;
            holdi = indices[index];
            indices[index] = indices[lastIndex];
            indices[lastIndex] = holdi;
        }
    }

    // sort the elements of an array into ascending order with matching switches in an array of the length
    // using selection sort method
    // array determining the order is the first argument
    // matching array  is the second argument
    // sorted arrays returned as third and fourth arguments resopectively
    public static void selectionSort(double[] aa, double[] bb, double[] cc, double[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        double holdx = 0.0D;
        double holdy = 0.0D;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(float[] aa, float[] bb, float[] cc, float[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        float holdx = 0.0F;
        float holdy = 0.0F;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(long[] aa, long[] bb, long[] cc, long[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        long holdx = 0L;
        long holdy = 0L;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(int[] aa, int[] bb, int[] cc, int[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        int holdx = 0;
        int holdy = 0;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(double[] aa, long[] bb, double[] cc, long[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        double holdx = 0.0D;
        long holdy = 0L;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(long[] aa, double[] bb, long[] cc, double[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        long holdx = 0L;
        double holdy = 0.0D;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }


    public static void selectionSort(double[] aa, int[] bb, double[] cc, int[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        double holdx = 0.0D;
        int holdy = 0;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(int[] aa, double[] bb, int[] cc, double[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        int holdx = 0;
        double holdy = 0.0D;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(long[] aa, int[] bb, long[] cc, int[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        long holdx = 0L;
        int holdy = 0;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }

    public static void selectionSort(int[] aa, long[] bb, int[] cc, long[] dd)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(n != m)
            throw new IllegalArgumentException("First argument array, aa, (length = " + n + ") and the second argument array, bb, (length = " + m + ") should be the same length");
        int nn = cc.length;
        if(nn < n)
            throw new IllegalArgumentException("The third argument array, cc, (length = " + nn + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int mm = dd.length;
        if(mm < m)
            throw new IllegalArgumentException("The fourth argument array, dd, (length = " + mm + ") should be at least as long as the second argument array, bb, (length = " + m + ")");

        int holdx = 0;
        long holdy = 0L;


        for(int i = 0; i < n; i++)
        {
            cc[i] = aa[i];
            dd[i] = bb[i];
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(cc[i] < cc[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdx = cc[index];
            cc[index] = cc[lastIndex];
            cc[lastIndex] = holdx;
            holdy = dd[index];
            dd[index] = dd[lastIndex];
            dd[lastIndex] = holdy;
        }
    }


    // sort elements in an array of doubles (first argument) into ascending order
    // using selection sort method
    // returns the sorted array as second argument
    //  and an array of the indices of the sorted array as the third argument
    public static void selectSort(double[] aa, double[] bb, int[] indices)
    {
        int index = 0;
        int lastIndex = -1;
        int n = aa.length;
        int m = bb.length;
        if(m < n)
            throw new IllegalArgumentException("The second argument array, bb, (length = " + m + ") should be at least as long as the first argument array, aa, (length = " + n + ")");
        int k = indices.length;
        if(m < n)
            throw new IllegalArgumentException("The third argument array, indices, (length = " + k + ") should be at least as long as the first argument array, aa, (length = " + n + ")");

        double holdb = 0.0D;
        int holdi = 0;
        for(int i = 0; i < n; i++)
        {
            bb[i] = aa[i];
            indices[i] = i;
        }

        while(lastIndex != n - 1)
        {
            index = lastIndex + 1;
            for(int i = lastIndex + 2; i < n; i++)
            {
                if(bb[i] < bb[index])
                {
                    index = i;
                }
            }
            lastIndex++;
            holdb = bb[index];
            bb[index] = bb[lastIndex];
            bb[lastIndex] = holdb;
            holdi = indices[index];
            indices[index] = indices[lastIndex];
            indices[lastIndex] = holdi;
        }
    }

    /*      returns -1 if x < 0 else returns 1   */
    //  double version
    public static double sign(double x)
    {
        if(x < 0.0)
        {
            return -1.0;
        }
        else
        {
            return 1.0;
        }
    }

    /*      returns -1 if x < 0 else returns 1   */
    //  float version
    public static float sign(float x)
    {
        if(x < 0.0F)
        {
            return -1.0F;
        }
        else
        {
            return 1.0F;
        }
    }

    /*      returns -1 if x < 0 else returns 1   */
    //  int version
    public static int sign(int x)
    {
        if(x < 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

    /*      returns -1 if x < 0 else returns 1   */
    // long version
    public static long sign(long x)
    {
        if(x < 0)
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

    // UNIT CONVERSIONS

    // Converts radians to degrees
    public static double radToDeg(double rad)
    {
        return rad * 180.0D / Math.PI;
    }

    // Converts degrees to radians
    public static double degToRad(double deg)
    {
        return deg * Math.PI / 180.0D;
    }

    // Converts electron volts(eV) to corresponding wavelength in nm
    public static double evToNm(double ev)
    {
        return 1e+9 * C_LIGHT / (-ev * Q_ELECTRON / H_PLANCK);
    }

    // Converts wavelength in nm to matching energy in eV
    public static double nmToEv(double nm)
    {
        return C_LIGHT / (-nm * 1e-9) * H_PLANCK / Q_ELECTRON;
    }

    // Converts moles per litre to percentage weight by volume
    public static double molarToPercentWeightByVol(double molar, double molWeight)
    {
        return molar * molWeight / 10.0D;
    }

    // Converts percentage weight by volume to moles per litre
    public static double percentWeightByVolToMolar(double perCent, double molWeight)
    {
        return perCent * 10.0D / molWeight;
    }

    // Converts Celsius to Kelvin
    public static double celsiusToKelvin(double cels)
    {
        return cels - T_ABS;
    }

    // Converts Kelvin to Celsius
    public static double kelvinToCelsius(double kelv)
    {
        return kelv + T_ABS;
    }

    // Converts Celsius to Fahrenheit
    public static double celsiusToFahren(double cels)
    {
        return cels * (9.0 / 5.0) + 32.0;
    }

    // Converts Fahrenheit to Celsius
    public static double fahrenToCelsius(double fahr)
    {
        return (fahr - 32.0) * 5.0 / 9.0;
    }

    // Converts calories to Joules
    public static double calorieToJoule(double cal)
    {
        return cal * 4.1868;
    }

    // Converts Joules to calories
    public static double jouleToCalorie(double joule)
    {
        return joule * 0.23884;
    }

    // Converts grams to ounces
    public static double gramToOunce(double gm)
    {
        return gm / 28.3459;
    }

    // Converts ounces to grams
    public static double ounceToGram(double oz)
    {
        return oz * 28.3459;
    }

    // Converts kilograms to pounds
    private static double kgToPound(double kg)
    {
        return kg / 0.4536;
    }

    // Converts pounds to kilograms
    private static double poundToKg(double pds)
    {
        return pds * 0.4536;
    }

    // Converts kilograms to tons
    public static double kgToTon(double kg)
    {
        return kg / 1016.05;
    }

    // Converts tons to kilograms
    public static double tonToKg(double tons)
    {
        return tons * 1016.05;
    }

    // Converts millimetres to inches
    public static double millimetreToInch(double mm)
    {
        return mm / 25.4;
    }

    // Converts inches to millimetres
    public static double inchToMillimetre(double in)
    {
        return in * 25.4;
    }

    // Converts feet to metres
    private static double footToMetre(double ft)
    {
        return ft * 0.3048;
    }

    // Converts metres to feet
    public static double metreToFoot(double metre)
    {
        return metre / 0.3048;
    }

    // Converts yards to metres
    public static double yardToMetre(double yd)
    {
        return yd * 0.9144;
    }

    // Converts metres to yards
    public static double metreToYard(double metre)
    {
        return metre / 0.9144;
    }

    // Converts miles to kilometres
    public static double mileToKm(double mile)
    {
        return mile * 1.6093;
    }

    // Converts kilometres to miles
    public static double kmToMile(double km)
    {
        return km / 1.6093;
    }

    // Converts UK gallons to litres
    public static double gallonToLitre(double gall)
    {
        return gall * 4.546;
    }

    // Converts litres to UK gallons
    public static double litreToGallon(double litre)
    {
        return litre / 4.546;
    }

    // Converts UK quarts to litres
    public static double quartToLitre(double quart)
    {
        return quart * 1.137;
    }

    // Converts litres to UK quarts
    public static double litreToQuart(double litre)
    {
        return litre / 1.137;
    }

    // Converts UK pints to litres
    public static double pintToLitre(double pint)
    {
        return pint * 0.568;
    }

    // Converts litres to UK pints
    public static double litreToPint(double litre)
    {
        return litre / 0.568;
    }

    // Converts UK gallons per mile to litres per kilometre
    public static double gallonPerMileToLitrePerKm(double gallPmile)
    {
        return gallPmile * 2.825;
    }

    // Converts litres per kilometre to UK gallons per mile
    public static double litrePerKmToGallonPerMile(double litrePkm)
    {
        return litrePkm / 2.825;
    }

    // Converts miles per UK gallons to kilometres per litre
    public static double milePerGallonToKmPerLitre(double milePgall)
    {
        return milePgall * 0.354;
    }

    // Converts kilometres per litre to miles per UK gallons
    public static double kmPerLitreToMilePerGallon(double kmPlitre)
    {
        return kmPlitre / 0.354;
    }

    // Converts UK fluid ounce to American fluid ounce
    public static double fluidOunceUKtoUS(double flOzUK)
    {
        return flOzUK * 0.961;
    }

    // Converts American fluid ounce to UK fluid ounce
    public static double fluidOunceUStoUK(double flOzUS)
    {
        return flOzUS * 1.041;
    }

    // Converts UK pint to American liquid pint
    public static double pintUKtoUS(double pintUK)
    {
        return pintUK * 1.201;
    }

    // Converts American liquid pint to UK pint
    public static double pintUStoUK(double pintUS)
    {
        return pintUS * 0.833;
    }

    // Converts UK quart to American liquid quart
    public static double quartUKtoUS(double quartUK)
    {
        return quartUK * 1.201;
    }

    // Converts American liquid quart to UK quart
    public static double quartUStoUK(double quartUS)
    {
        return quartUS * 0.833;
    }

    // Converts UK gallon to American gallon
    public static double gallonUKtoUS(double gallonUK)
    {
        return gallonUK * 1.201;
    }

    // Converts American gallon to UK gallon
    public static double gallonUStoUK(double gallonUS)
    {
        return gallonUS * 0.833;
    }

    // Converts UK pint to American cup
    public static double pintUKtoCupUS(double pintUK)
    {
        return pintUK / 0.417;
    }

    // Converts American cup to UK pint
    public static double cupUStoPintUK(double cupUS)
    {
        return cupUS * 0.417;
    }

    // Calculates body mass index (BMI) from height (m) and weight (kg)
    public static double calcBMImetric(double height, double weight)
    {
        return weight / (height * height);
    }

    // Calculates body mass index (BMI) from height (ft) and weight (lbs)
    public static double calcBMIimperial(double height, double weight)
    {
        height = FlanaganMath.footToMetre(height);
        weight = FlanaganMath.poundToKg(weight);
        return weight / (height * height);
    }

    // Calculates weight (kg) to give a specified BMI for a given height (m)
    public static double calcWeightFromBMImetric(double bmi, double height)
    {
        return bmi * height * height;
    }

    // Calculates weight (lbs) to give a specified BMI for a given height (ft)
    public static double calcWeightFromBMIimperial(double bmi, double height)
    {
        height = FlanaganMath.footToMetre(height);
        double weight = bmi * height * height;
        weight = FlanaganMath.kgToPound(weight);
        return weight;
    }


    // ADDITIONAL TRIGONOMETRIC FUNCTIONS

    // Returns the length of the hypotenuse of a and b
    // i.e. sqrt(a*a+b*b) [without unecessary overflow or underflow]
    // double version
    private static double hypot(double aa, double bb)
    {
        double amod = Math.abs(aa);
        double bmod = Math.abs(bb);
        double cc = 0.0D, ratio = 0.0D;
        if(amod == 0.0)
        {
            cc = bmod;
        }
        else
        {
            if(bmod == 0.0)
            {
                cc = amod;
            }
            else
            {
                if(amod >= bmod)
                {
                    ratio = bmod / amod;
                    cc = amod * Math.sqrt(1.0 + ratio * ratio);
                }
                else
                {
                    ratio = amod / bmod;
                    cc = bmod * Math.sqrt(1.0 + ratio * ratio);
                }
            }
        }
        return cc;
    }

    // Returns the length of the hypotenuse of a and b
    // i.e. sqrt(a*a+b*b) [without unecessary overflow or underflow]
    // float version
    public static float hypot(float aa, float bb)
    {
        return (float) hypot((double) aa, (double) bb);
    }

    // Angle (in radians) subtended at coordinate C
    // given x, y coordinates of all apices, A, B and C, of a triangle
    private static double angle(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC)
    {

        double ccos = FlanaganMath.cos(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
        return Math.acos(ccos);
    }

    // Angle (in radians) between sides sideA and sideB given all side lengths of a triangle
    private static double angle(double sideAC, double sideBC, double sideAB)
    {

        double ccos = FlanaganMath.cos(sideAC, sideBC, sideAB);
        return Math.acos(ccos);
    }

    // Sine of angle subtended at coordinate C
    // given x, y coordinates of all apices, A, B and C, of a triangle
    public static double sin(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC)
    {
        double angle = FlanaganMath.angle(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
        return Math.sin(angle);
    }

    // Sine of angle between sides sideA and sideB given all side lengths of a triangle
    public static double sin(double sideAC, double sideBC, double sideAB)
    {
        double angle = FlanaganMath.angle(sideAC, sideBC, sideAB);
        return Math.sin(angle);
    }

    // Sine given angle in radians
    // for completion - returns Math.sin(arg)
    public static double sin(double arg)
    {
        return Math.sin(arg);
    }

    // Inverse sine
    // FlanaganMath.asin Checks limits - Java Math.asin returns NaN if without limits
    public static double asin(double a)
    {
        if(a < -1.0D && a > 1.0D)
            throw new IllegalArgumentException("FlanaganMath.asin argument (" + a + ") must be >= -1.0 and <= 1.0");
        return Math.asin(a);
    }

    // Cosine of angle subtended at coordinate C
    // given x, y coordinates of all apices, A, B and C, of a triangle
    private static double cos(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC)
    {
        double sideAC = FlanaganMath.hypot(xAtA - xAtC, yAtA - yAtC);
        double sideBC = FlanaganMath.hypot(xAtB - xAtC, yAtB - yAtC);
        double sideAB = FlanaganMath.hypot(xAtA - xAtB, yAtA - yAtB);
        return FlanaganMath.cos(sideAC, sideBC, sideAB);
    }

    // Cosine of angle between sides sideA and sideB given all side lengths of a triangle
    private static double cos(double sideAC, double sideBC, double sideAB)
    {
        return 0.5D * (sideAC / sideBC + sideBC / sideAC - (sideAB / sideAC) * (sideAB / sideBC));
    }

    // Cosine given angle in radians
    // for completion - returns Java Math.cos(arg)
    public static double cos(double arg)
    {
        return Math.cos(arg);
    }

    // Inverse cosine
    // FlanaganMath.asin Checks limits - Java Math.asin returns NaN if without limits
    private static double acos(double a)
    {
        if(a < -1.0D || a > 1.0D)
            throw new IllegalArgumentException("FlanaganMath.acos argument (" + a + ") must be >= -1.0 and <= 1.0");
        return Math.acos(a);
    }

    // Tangent of angle subtended at coordinate C
    // given x, y coordinates of all apices, A, B and C, of a triangle
    public static double tan(double xAtA, double yAtA, double xAtB, double yAtB, double xAtC, double yAtC)
    {
        double angle = FlanaganMath.angle(xAtA, yAtA, xAtB, yAtB, xAtC, yAtC);
        return Math.tan(angle);
    }

    // Tangent of angle between sides sideA and sideB given all side lengths of a triangle
    public static double tan(double sideAC, double sideBC, double sideAB)
    {
        double angle = FlanaganMath.angle(sideAC, sideBC, sideAB);
        return Math.tan(angle);
    }

    // Tangent given angle in radians
    // for completion - returns Math.tan(arg)
    public static double tan(double arg)
    {
        return Math.tan(arg);
    }

    // Inverse tangent
    // for completion - returns Math.atan(arg)
    public static double atan(double a)
    {
        return Math.atan(a);
    }

    // Inverse tangent - ratio numerator and denominator provided
    // for completion - returns Math.atan2(arg)
    public static double atan2(double a, double b)
    {
        return Math.atan2(a, b);
    }

    // Cotangent
    public static double cot(double a)
    {
        return 1.0D / Math.tan(a);
    }

    // Inverse cotangent
    public static double acot(double a)
    {
        return Math.atan(1.0D / a);
    }

    // Inverse cotangent - ratio numerator and denominator provided
    public static double acot2(double a, double b)
    {
        return Math.atan2(b, a);
    }

    // Secant
    public static double sec(double a)
    {
        return 1.0 / Math.cos(a);
    }

    // Inverse secant
    public static double asec(double a)
    {
        if(a < 1.0D && a > -1.0D) throw new IllegalArgumentException("asec argument (" + a + ") must be >= 1 or <= -1");
        return Math.acos(1.0 / a);
    }

    // Cosecant
    public static double csc(double a)
    {
        return 1.0D / Math.sin(a);
    }

    // Inverse cosecant
    public static double acsc(double a)
    {
        if(a < 1.0D && a > -1.0D) throw new IllegalArgumentException("acsc argument (" + a + ") must be >= 1 or <= -1");
        return Math.asin(1.0 / a);
    }

    // Exsecant
    public static double exsec(double a)
    {
        return (1.0 / Math.cos(a) - 1.0D);
    }

    // Inverse exsecant
    public static double aexsec(double a)
    {
        if(a < 0.0D && a > -2.0D)
            throw new IllegalArgumentException("aexsec argument (" + a + ") must be >= 0.0 and <= -2");
        return Math.asin(1.0D / (1.0D + a));
    }

    // Versine
    private static double vers(double a)
    {
        return (1.0D - Math.cos(a));
    }

    // Inverse  versine
    public static double avers(double a)
    {
        if(a < 0.0D && a > 2.0D) throw new IllegalArgumentException("avers argument (" + a + ") must be <= 2 and >= 0");
        return Math.acos(1.0D - a);
    }

    // Coversine
    public static double covers(double a)
    {
        return (1.0D - Math.sin(a));
    }

    // Inverse coversine
    public static double acovers(double a)
    {
        if(a < 0.0D && a > 2.0D)
            throw new IllegalArgumentException("acovers argument (" + a + ") must be <= 2 and >= 0");
        return Math.asin(1.0D - a);
    }

    // Haversine
    public static double hav(double a)
    {
        return 0.5D * FlanaganMath.vers(a);
    }

    // Inverse haversine
    public static double ahav(double a)
    {
        if(a < 0.0D && a > 1.0D) throw new IllegalArgumentException("ahav argument (" + a + ") must be >= 0 and <= 1");
        return FlanaganMath.acos(1.0D - 2.0D * a);
    }

    // Sinc
    public static double sinc(double a)
    {
        if(Math.abs(a) < 1e-40)
        {
            return 1.0D;
        }
        else
        {
            return Math.sin(a) / a;
        }
    }

    //Hyperbolic sine of a double number
    public static double sinh(double a)
    {
        return 0.5D * (Math.exp(a) - Math.exp(-a));
    }

    // Inverse hyperbolic sine of a double number
    public static double asinh(double a)
    {
        double sgn = 1.0D;
        if(a < 0.0D)
        {
            sgn = -1.0D;
            a = -a;
        }
        return sgn * Math.log(a + Math.sqrt(a * a + 1.0D));
    }

    //Hyperbolic cosine of a double number
    public static double cosh(double a)
    {
        return 0.5D * (Math.exp(a) + Math.exp(-a));
    }

    // Inverse hyperbolic cosine of a double number
    public static double acosh(double a)
    {
        if(a < 1.0D) throw new IllegalArgumentException("acosh real number argument (" + a + ") must be >= 1");
        return Math.log(a + Math.sqrt(a * a - 1.0D));
    }

    //Hyperbolic tangent of a double number
    private static double tanh(double a)
    {
        return sinh(a) / cosh(a);
    }

    // Inverse hyperbolic tangent of a double number
    public static double atanh(double a)
    {
        double sgn = 1.0D;
        if(a < 0.0D)
        {
            sgn = -1.0D;
            a = -a;
        }
        if(a > 1.0D)
            throw new IllegalArgumentException("atanh real number argument (" + sgn * a + ") must be >= -1 and <= 1");
        return 0.5D * sgn * (Math.log(1.0D + a) - Math.log(1.0D - a));
    }

    //Hyperbolic cotangent of a double number
    public static double coth(double a)
    {
        return 1.0D / tanh(a);
    }

    // Inverse hyperbolic cotangent of a double number
    public static double acoth(double a)
    {
        double sgn = 1.0D;
        if(a < 0.0D)
        {
            sgn = -1.0D;
            a = -a;
        }
        if(a < 1.0D)
            throw new IllegalArgumentException("acoth real number argument (" + sgn * a + ") must be <= -1 or >= 1");
        return 0.5D * sgn * (Math.log(1.0D + a) - Math.log(a - 1.0D));
    }

    //Hyperbolic secant of a double number
    public static double sech(double a)
    {
        return 1.0D / cosh(a);
    }

    // Inverse hyperbolic secant of a double number
    public static double asech(double a)
    {
        if(a > 1.0D || a < 0.0D)
            throw new IllegalArgumentException("asech real number argument (" + a + ") must be >= 0 and <= 1");
        return 0.5D * (Math.log(1.0D / a + Math.sqrt(1.0D / (a * a) - 1.0D)));
    }

    //Hyperbolic cosecant of a double number
    public static double csch(double a)
    {
        return 1.0D / sinh(a);
    }

    // Inverse hyperbolic cosecant of a double number
    public static double acsch(double a)
    {
        double sgn = 1.0D;
        if(a < 0.0D)
        {
            sgn = -1.0D;
            a = -a;
        }
        return 0.5D * sgn * (Math.log(1.0 / a + Math.sqrt(1.0D / (a * a) + 1.0D)));
    }

    // MANTISSA ROUNDING (TRUNCATING)
    // returns a value of xDouble truncated to trunc decimal places
    public static double truncate(double xDouble, int trunc)
    {
        double xTruncated = xDouble;
        if(!FlanaganMath.isNaN(xDouble))
        {
            if(!FlanaganMath.isPlusInfinity(xDouble))
            {
                if(!FlanaganMath.isMinusInfinity(xDouble))
                {
                    if(xDouble != 0.0D)
                    {
                        String xString = ((new Double(xDouble)).toString()).trim();
                        xTruncated = Double.parseDouble(truncateProcedure(xString, trunc));
                    }
                }
            }
        }
        return xTruncated;
    }

    // returns a value of xFloat truncated to trunc decimal places
    public static float truncate(float xFloat, int trunc)
    {
        float xTruncated = xFloat;
        if(!FlanaganMath.isNaN(xFloat))
        {
            if(!FlanaganMath.isPlusInfinity(xFloat))
            {
                if(!FlanaganMath.isMinusInfinity(xFloat))
                {
                    if(xFloat != 0.0D)
                    {
                        String xString = ((new Float(xFloat)).toString()).trim();
                        xTruncated = Float.parseFloat(truncateProcedure(xString, trunc));
                    }
                }
            }
        }
        return xTruncated;
    }

    // private method for truncating a float or double expressed as a String
    private static String truncateProcedure(String xValue, int trunc)
    {

        String xTruncated = xValue;
        String xWorking = xValue;
        String exponent = " ";
        String first = "+";
        int expPos = xValue.indexOf('E');
        int dotPos = xValue.indexOf('.');
        int minPos = xValue.indexOf('-');

        if(minPos != -1)
        {
            if(minPos == 0)
            {
                xWorking = xWorking.substring(1);
                first = "-";
                dotPos--;
                expPos--;
            }
        }
        if(expPos > -1)
        {
            exponent = xWorking.substring(expPos);
            xWorking = xWorking.substring(0, expPos);
        }
        String xPreDot = null;
        String xPostDot = "0";
        String xDiscarded = null;
        String tempString = null;
        double tempDouble = 0.0D;
        if(dotPos > -1)
        {
            xPreDot = xWorking.substring(0, dotPos);
            xPostDot = xWorking.substring(dotPos + 1);
            int xLength = xPostDot.length();
            if(trunc < xLength)
            {
                xDiscarded = xPostDot.substring(trunc);
                tempString = xDiscarded.substring(0, 1) + ".";
                if(xDiscarded.length() > 1)
                {
                    tempString += xDiscarded.substring(1);
                }
                else
                {
                    tempString += "0";
                }
                tempDouble = Math.round(Double.parseDouble(tempString));

                if(trunc > 0)
                {
                    if(tempDouble >= 5.0)
                    {
                        int[] xArray = new int[trunc + 1];
                        xArray[0] = 0;
                        for(int i = 0; i < trunc; i++)
                        {
                            xArray[i + 1] = Integer.parseInt(xPostDot.substring(i, i + 1));
                        }
                        boolean test = true;
                        int iCounter = trunc;
                        while(test)
                        {
                            xArray[iCounter] += 1;
                            if(iCounter > 0)
                            {
                                if(xArray[iCounter] < 10)
                                {
                                    test = false;
                                }
                                else
                                {
                                    xArray[iCounter] = 0;
                                    iCounter--;
                                }
                            }
                            else
                            {
                                test = false;
                            }
                        }
                        int preInt = Integer.parseInt(xPreDot);
                        preInt += xArray[0];
                        xPreDot = (new Integer(preInt)).toString();
                        tempString = "";
                        for(int i = 1; i <= trunc; i++)
                        {
                            tempString += (new Integer(xArray[i])).toString();
                        }
                        xPostDot = tempString;
                    }
                    else
                    {
                        xPostDot = xPostDot.substring(0, trunc);
                    }
                }
                else
                {
                    if(tempDouble >= 5.0)
                    {
                        int preInt = Integer.parseInt(xPreDot);
                        preInt++;
                        xPreDot = (new Integer(preInt)).toString();
                    }
                    xPostDot = "0";
                }
            }
            xTruncated = first + xPreDot.trim() + "." + xPostDot.trim() + exponent;
        }
        return xTruncated.trim();
    }

    // Returns true if x is infinite, i.e. is equal to either plus or minus infinity
    // x is double
    public static boolean isInfinity(double x)
    {
        boolean test = false;
        if(x == Double.POSITIVE_INFINITY || x == Double.NEGATIVE_INFINITY) test = true;
        return test;
    }

    // Returns true if x is infinite, i.e. is equal to either plus or minus infinity
    // x is float
    public static boolean isInfinity(float x)
    {
        boolean test = false;
        if(x == Float.POSITIVE_INFINITY || x == Float.NEGATIVE_INFINITY) test = true;
        return test;
    }

    // Returns true if x is plus infinity
    // x is double
    private static boolean isPlusInfinity(double x)
    {
        boolean test = false;
        if(x == Double.POSITIVE_INFINITY) test = true;
        return test;
    }

    // Returns true if x is plus infinity
    // x is float
    private static boolean isPlusInfinity(float x)
    {
        boolean test = false;
        if(x == Float.POSITIVE_INFINITY) test = true;
        return test;
    }

    // Returns true if x is minus infinity
    // x is double
    private static boolean isMinusInfinity(double x)
    {
        boolean test = false;
        if(x == Double.NEGATIVE_INFINITY) test = true;
        return test;
    }

    // Returns true if x is minus infinity
    // x is float
    private static boolean isMinusInfinity(float x)
    {
        boolean test = false;
        if(x == Float.NEGATIVE_INFINITY) test = true;
        return test;
    }


    // Returns true if x is 'Not a Number' (NaN)
    // x is double
    private static boolean isNaN(double x)
    {
        boolean test = false;
        if(x != x) test = true;
        return test;
    }

    // Returns true if x is 'Not a Number' (NaN)
    // x is float
    private static boolean isNaN(float x)
    {
        boolean test = false;
        if(x != x) test = true;
        return test;
    }

    // Returns true if x equals y
    // x and y are double
    // x may be float within range, PLUS_INFINITY, NEGATIVE_INFINITY, or NaN
    // NB!! This method treats two NaNs as equal
    public static boolean isEqual(double x, double y)
    {
        boolean test = false;
        if(FlanaganMath.isNaN(x))
        {
            if(FlanaganMath.isNaN(y)) test = true;
        }
        else
        {
            if(FlanaganMath.isPlusInfinity(x))
            {
                if(FlanaganMath.isPlusInfinity(y)) test = true;
            }
            else
            {
                if(FlanaganMath.isMinusInfinity(x))
                {
                    if(FlanaganMath.isMinusInfinity(y)) test = true;
                }
                else
                {
                    if(x == y) test = true;
                }
            }
        }
        return test;
    }

    // Returns true if x equals y
    // x and y are float
    // x may be float within range, PLUS_INFINITY, NEGATIVE_INFINITY, or NaN
    // NB!! This method treats two NaNs as equal
    public static boolean isEqual(float x, float y)
    {
        boolean test = false;
        if(FlanaganMath.isNaN(x))
        {
            if(FlanaganMath.isNaN(y)) test = true;
        }
        else
        {
            if(FlanaganMath.isPlusInfinity(x))
            {
                if(FlanaganMath.isPlusInfinity(y)) test = true;
            }
            else
            {
                if(FlanaganMath.isMinusInfinity(x))
                {
                    if(FlanaganMath.isMinusInfinity(y)) test = true;
                }
                else
                {
                    if(x == y) test = true;
                }
            }
        }
        return test;
    }

    // Returns true if x equals y
    // x and y are int
    public static boolean isEqual(int x, int y)
    {
        boolean test = false;
        if(x == y) test = true;
        return test;
    }

    // Returns true if x equals y
    // x and y are char
    public static boolean isEqual(char x, char y)
    {
        boolean test = false;
        if(x == y) test = true;
        return test;
    }

    // Returns true if x equals y
    // x and y are Strings
    public static boolean isEqual(String x, String y)
    {
        boolean test = false;
        if(x.equals(y)) test = true;
        return test;
    }

    // Returns true if x is an even number, false if x is an odd number
    // x is int
    public static boolean isEven(int x)
    {
        boolean test = false;
        if(x % 2 == 0.0D) test = true;
        return test;
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are double
    public static int compare(double x, double y)
    {
        Double X = new Double(x);
        Double Y = new Double(y);
        return X.compareTo(Y);
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are int
    public static int compare(int x, int y)
    {
        Integer X = new Integer(x);
        Integer Y = new Integer(y);
        return X.compareTo(Y);
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are long
    public static int compare(long x, long y)
    {
        Long X = new Long(x);
        Long Y = new Long(y);
        return X.compareTo(Y);
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are float
    public static int compare(float x, float y)
    {
        Float X = new Float(x);
        Float Y = new Float(y);
        return X.compareTo(Y);
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are short
    public static int compare(byte x, byte y)
    {
        Byte X = new Byte(x);
        Byte Y = new Byte(y);
        return X.compareTo(Y);
    }

    // Returns 0 if x == y
    // Returns -1 if x < y
    // Returns 1 if x > y
    // x and y are short
    public static int compare(short x, short y)
    {
        Short X = new Short(x);
        Short Y = new Short(y);
        return X.compareTo(Y);
    }

    // Returns true if x is an even number, false if x is an odd number
    // x is float but must hold an integer value
    public static boolean isEven(float x)
    {
        double y = Math.floor(x);
        if(((double) x - y) != 0.0D) throw new IllegalArgumentException("the argument is not an integer");
        boolean test = false;
        y = Math.floor(x / 2.0F);
        if(((double) (x / 2.0F) - y) == 0.0D) test = true;
        return test;
    }

    // Returns true if x is an even number, false if x is an odd number
    // x is double but must hold an integer value
    public static boolean isEven(double x)
    {
        double y = Math.floor(x);
        if((x - y) != 0.0D) throw new IllegalArgumentException("the argument is not an integer");
        boolean test = false;
        y = Math.floor(x / 2.0F);
        if((x / 2.0D - y) == 0.0D) test = true;
        return test;
    }

    // Returns true if x is an odd number, false if x is an even number
    // x is int
    public static boolean isOdd(int x)
    {
        boolean test = true;
        if(x % 2 == 0.0D) test = false;
        return test;
    }

    // Returns true if x is an odd number, false if x is an even number
    // x is float but must hold an integer value
    public static boolean isOdd(float x)
    {
        double y = Math.floor(x);
        if(((double) x - y) != 0.0D) throw new IllegalArgumentException("the argument is not an integer");
        boolean test = true;
        y = Math.floor(x / 2.0F);
        if(((double) (x / 2.0F) - y) == 0.0D) test = false;
        return test;
    }

    // Returns true if x is an odd number, false if x is an even number
    // x is double but must hold an integer value
    public static boolean isOdd(double x)
    {
        double y = Math.floor(x);
        if((x - y) != 0.0D) throw new IllegalArgumentException("the argument is not an integer");
        boolean test = true;
        y = Math.floor(x / 2.0F);
        if((x / 2.0D - y) == 0.0D) test = false;
        return test;
    }

    // Returns true if year (argument) is a leap year
    private static boolean leapYear(int year)
    {
        boolean test = false;

        if(year % 4 != 0)
        {
            test = false;
        }
        else
        {
            if(year % 400 == 0)
            {
                test = true;
            }
            else
            {
                test = year % 100 != 0;
            }
        }
        return test;
    }

    // Returns milliseconds since 0 hours 0 minutes 0 seconds on 1 Jan 1970
    public static long dateToJavaMilliS(int year, int month, int day, int hour, int min, int sec)
    {

        long[] monthDays = {0L, 31L, 28L, 31L, 30L, 31L, 30L, 31L, 31L, 30L, 31L, 30L, 31L};
        long ms = 0L;

        long yearDiff = 0L;
        int yearTest = year - 1;
        while(yearTest >= 1970)
        {
            yearDiff += 365;
            if(FlanaganMath.leapYear(yearTest)) yearDiff++;
            yearTest--;
        }
        yearDiff *= 24L * 60L * 60L * 1000L;

        long monthDiff = 0L;
        int monthTest = month - 1;
        while(monthTest > 0)
        {
            monthDiff += monthDays[monthTest];
            if(FlanaganMath.leapYear(year)) monthDiff++;
            monthTest--;
        }

        monthDiff *= 24L * 60L * 60L * 1000L;

        ms = yearDiff + monthDiff + day * 24L * 60L * 60L * 1000L + hour * 60L * 60L * 1000L + min * 60L * 1000L + sec * 1000L;

        return ms;
    }

    // Returns a copy of the object
    // An exception will be thrown if an attempt to copy a non-serialisable object is made.
    // Taken, with minor changes,  from { Java Techniques }
    // http://javatechniques.com/blog/
    public static Object copyObject(Object obj)
    {
        Object objCopy = null;
        try
        {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            // Make an input stream from the byte array and
            // read a copy of the object back in.
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            objCopy = ois.readObject();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        return objCopy;
    }
}



