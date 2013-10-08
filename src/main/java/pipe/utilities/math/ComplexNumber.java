/*
*   Class   Complex
*
*   Defines a complex number as an object and includes
*   the methods needed for standard complex arithmetic
*
*   See class ComplexMatrix for complex matrix manipulations
*   See class ComplexPoly for complex polynomial manipulations
*   See class ComplexErrorProp for the error propogation in complex arithmetic
*
*   WRITTEN BY: Dr Michael Thomas Flanagan
*
*   DATE:    February 2002
*   UPDATED: 1 August 2006, 29 April 2007, 15 June 2007
*
*   DOCUMENTATION:
*   See Michael T Flanagan's Java library on-line web pages:
*   http://www.ee.ucl.ac.uk/~mflanaga/java/
*   http://www.ee.ucl.ac.uk/~mflanaga/java/Complex.html
*
*   Copyright (c) February 2002, April 2007    Michael Thomas Flanagan
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

public class ComplexNumber
{

        private double real = 0.0D;         // Real part of a complex number
        private double imag = 0.0D;         // Imaginary part of a complex number
       // private static char jori = 'j';     // i or j in a + j.b or a + i.b representaion
                                            // default value = j
        //private static boolean infOption = true;  // option determinimg how infinity is handled
                                            // if true (default option):
                                            //  multiplication with either complex number with either part = infinity returns infinity
                                            //      unless the one complex number is zero in both parts
                                            //  division by a complex number with either part = infinity returns zero
                                            //      unless the dividend is also infinite in either part
                                            // if false:
                                            //      standard arithmetic performed


/*********************************************************/

        // CONSTRUCTORS
        // default constructor - real and imag = zero
        public ComplexNumber()
        {
                this.real = 0.0D;
                this.imag = 0.0D;
        }

        // constructor - initialises both real and imag
        public ComplexNumber(double real, double imag)
        {
                this.real = real;
                this.imag = imag;
        }

        // constructor - initialises  real, imag = 0.0
        public ComplexNumber(double real)
        {
                this.real = real;
                this.imag = 0.0D;
        }

        // constructor - initialises both real and imag to the values of an existing ComplexNumber
        public ComplexNumber(ComplexNumber c)
        {
                this.real = c.real;
                this.imag = c.imag;
        }

/**
 * @param real*******************************************************/

        // PUBLIC METHODS

        // SET VALUES
        // Set the value of real
        public void setReal(double real){
        this.real = real;
        }
        // Set the value of imag
        public void setImag(double imag){
                this.imag = imag;
        }

        // Set the values of real and imag
        private void reset(double real, double imag){
                this.real = real;
                this.imag = imag;
        }

        // Set real and imag given the modulus and argument (in radians)
        public void polar(double mod, double arg){
                this.real = mod*Math.cos(arg);
                this.imag = mod*Math.sin(arg);
        }

        // GET VALUES
        // Get the value of real
        public double getReal(){
                return real;
        }

        // Get the value of imag
        public double getImag(){
                return imag;
        }

        // INPUT AND OUTPUT

        // READ A COMPLEX NUMBER
        // Read a complex number from the keyboard console after a prompt message
        // in a String format compatible with ComplexNumber.parse,
        // e.g 2+j3, 2 + j3, 2+i3, 2 + i3
        // prompt = Prompt message to vdu
        public static synchronized ComplexNumber readComplex(String prompt)
        {
                int ch = ' ';
                String cstring = "";
                boolean done = false;

                System.out.print(prompt + " ");
                System.out.flush();

                while (!done){
                        try{
                                ch = System.in.read();
                                if (ch < 0 || (char)ch == '\n')
                                        done = true;
                                else
                                        cstring = cstring + (char) ch;
                        }
                        catch(java.io.IOException e){
                                done = true;
                        }
                }
                return ComplexNumber.parseComplex(cstring);
        }

        // Read a complex number from the keyboard console after a prompt message (with String default option)
        // in a String format compatible with ComplexNumber.parse,
        // e.g 2+j3, 2 + j3, 2+i3, 2 + i3
        // prompt = Prompt message to vdu
        // dflt = default value
        public static synchronized ComplexNumber readComplex(String prompt, String dflt)
        {
                int ch = ' ';
                String cstring = "";
                boolean done = false;

                System.out.print(prompt + " [default value = " + dflt + "]  ");
                System.out.flush();

                int i=0;
                while (!done){
                        try{
                                ch = System.in.read();
                                if (ch < 0 || (char)ch == '\n' || (char)ch =='\r'){
                                        if(i==0){
                                            cstring = dflt;
                                            if((char)ch == '\r')ch = System.in.read();
                                        }
                                        done = true;
                                }
                                else{
                                        cstring = cstring + (char) ch;
                                        i++;
                                 }
                        }
                        catch(java.io.IOException e){
                                done = true;
                        }
                }
                return ComplexNumber.parseComplex(cstring);
        }

        // Read a complex number from the keyboard console after a prompt message (with ComplexNumber default option)
        // in a String format compatible with ComplexNumber.parse,
        // e.g 2+j3, 2 + j3, 2+i3, 2 + i3
        // prompt = Prompt message to vdu
        // dflt = default value
        public static synchronized ComplexNumber readComplex(String prompt, ComplexNumber dflt)
        {
                int ch = ' ';
                String cstring = "";
                boolean done = false;

                System.out.print(prompt + " [default value = " + dflt + "]  ");
                System.out.flush();

                int i=0;
                while (!done){
                        try{
                                ch = System.in.read();
                                if (ch < 0 || (char)ch == '\n' || (char)ch =='\r'){
                                        if(i==0){
                                            if((char)ch == '\r')ch = System.in.read();
                                            return dflt;
                                        }
                                        done = true;
                                }
                                else{
                                        cstring = cstring + (char) ch;
                                        i++;
                                 }
                        }
                        catch(java.io.IOException e){
                                done = true;
                        }
                }
                return ComplexNumber.parseComplex(cstring);
        }



        // Read a complex number from the keyboard console without a prompt message
        // in a String format compatible with ComplexNumber.parse,
        // e.g 2+j3, 2 + j3, 2+i3, 2 + i3
        // prompt = Prompt message to vdu
        public static synchronized ComplexNumber readComplex()
        {
                int ch = ' ';
                String cstring = "";
                boolean done = false;

                System.out.print(" ");
                System.out.flush();

                while (!done){
                        try{
                                ch = System.in.read();
                                if (ch < 0 || (char)ch == '\n')
                                        done = true;
                                else
                                        cstring = cstring + (char) ch;
                        }
                        catch(java.io.IOException e){
                                done = true;
                        }
                }
                return ComplexNumber.parseComplex(cstring);
        }

        // PRINT A COMPLEX NUMBER
        // Print to terminal window with text (message) and a line return
        public void println(String message){
                System.out.println(message + " " + this.toString());
        }

        // Print to terminal window without text (message) but with a line return
        public void println(){
                System.out.println(" " + this.toString());
        }

        // Print to terminal window with text (message) but without line return
        public void print(String message){
                System.out.print(message + " " + this.toString());
        }

        // Print to terminal window without text (message) and without line return
        public void print(){
                System.out.print(" " + this.toString());
        }

        // PRINT AN ARRAY OF COMLEX NUMBERS
        // Print an array to terminal window with text (message) and a line return
        public static void println(String message, ComplexNumber[] aa){
            System.out.println(message);
            for(ComplexNumber anAa : aa)
            {
                System.out.println(anAa.toString() + "  ");
            }
        }

        // Print an array to terminal window without text (message) but with a line return
        public static void println(ComplexNumber[] aa){
            for(ComplexNumber anAa : aa)
            {
                System.out.println(anAa.toString() + "  ");
            }
        }

        // Print an array to terminal window with text (message) but no line returns except at the end
        public static void print(String message, ComplexNumber[] aa){
            System.out.print(message+ " ");
            for(ComplexNumber anAa : aa)
            {
                System.out.print(anAa.toString() + "   ");
            }
            System.out.println();
        }

        // Print an array to terminal window without text (message) but with no line returns except at the end
        public static void print(ComplexNumber[] aa){
            for(ComplexNumber anAa : aa)
            {
                System.out.print(anAa.toString() + "  ");
            }
            System.out.println();
        }

        // TRUNCATION
        // Rounds the mantissae of both the real and imaginary parts of ComplexNumber to prec places
        // Static method
        public static ComplexNumber truncate(ComplexNumber x, int prec){
                if(prec<0)return x;

                double xR = x.getReal();
                double xI = x.getImag();
                ComplexNumber y = new ComplexNumber();

                xR = FlanaganMath.truncate(xR, prec);
                xI = FlanaganMath.truncate(xI, prec);

                y.reset(xR, xI);

                return y;
        }

        // instance method
        public ComplexNumber truncate(int prec){
                if(prec<0)return this;

                double xR = this.getReal();
                double xI = this.getImag();
                ComplexNumber y = new ComplexNumber();

                xR = FlanaganMath.truncate(xR, prec);
                xI = FlanaganMath.truncate(xI, prec);

                y.reset(xR, xI);

                return y;
        }


        // CONVERSIONS
        // Format a complex number as a string, a + jb or a + ib[instance method]
        // < value of real > < + or - > < j or i> < value of imag >
        // Choice of j or i is set by ComplexNumber.seti() or ComplexNumber.setj()
        // j is the default option for j or i
        // Overides java.lang.String.toString()
        public String toString(){
                char ch='+';
                if(this.imag<0.0D)ch='-';
                return this.real+" "+ch+" "+'i'+Math.abs(this.imag);
        }

        // Format a complex number as a string, a + jb or a + ib [static method]
        // See static method above for comments
        public static String toString(ComplexNumber aa){
                char ch='+';
                if(aa.imag<0.0D)ch='-';
                return aa.real+" "+ch+'i'+Math.abs(aa.imag);
        }

        /*// Sets the representation of the square root of minus one to j in Strings
        public static void setj(){
                jori = 'j';
        }

        // Sets the representation of the square root of minus one to i in Strings
        public static void seti(){
                jori = 'i';
        }
*/
        // Returns the representation of the square root of minus one (j or i) set for Strings
        public static char getjori(){
            return 'i';
        }

        // Parse a string to obtain ComplexNumber
        // accepts strings 'real''s''sign''s''x''imag'
        // where x may be i or j and s may be no spaces or any number of spaces
        // and sign may be + or -
        // e.g.  2+j3, 2 + j3, 2+i3, 2 + i3
        private static ComplexNumber parseComplex(String ss){
                ComplexNumber aa = new ComplexNumber();
                ss = ss.trim();
                double first = 1.0D;
                if(ss.charAt(0)=='-'){
                    first = -1.0D;
                    ss = ss.substring(1);
                }

                int i = ss.indexOf('j');
                if(i==-1){
                        i = ss.indexOf('i');
                }
                if(i==-1)throw new NumberFormatException("no i or j found");

                int imagSign=1;
                int j = ss.indexOf('+');

                if(j==-1){
                j = ss.indexOf('-');
                if(j>-1) imagSign=-1;
                }
                if(j==-1)throw new NumberFormatException("no + or - found");

                int r0=0;
                int r1=j;
                int i0=i+1;
                int i1=ss.length();
                String sreal=ss.substring(r0,r1);
                String simag=ss.substring(i0,i1);
                aa.real=first*Double.parseDouble(sreal);
                aa.imag=imagSign*Double.parseDouble(simag);
                return aa;
        }

        // Same method as parseComplex
        // Overides java.lang.Object.valueOf()
        public static ComplexNumber valueOf(String ss){
                return ComplexNumber.parseComplex(ss);
        }

        // Return a HASH CODE for the ComplexNumber number
        // Overides java.lang.Object.hashCode()
        public int hashCode()
        {
                long lreal = Double.doubleToLongBits(this.real);
                long limag = Double.doubleToLongBits(this.imag);
                int hreal = (int)(lreal^(lreal>>>32));
                int himag = (int)(limag^(limag>>>32));
                return 7*(hreal/10)+3*(himag/10);
        }


        // ARRAYS

        // Create a one dimensional array of ComplexNumber objects of length n
        // all real = 0 and all imag = 0
        private static ComplexNumber[] oneDarray(int n){
                ComplexNumber[] a =new ComplexNumber[n];
                for(int i=0; i<n; i++){
                        a[i]= ComplexNumber.zero();
                }
                return a;
        }

        // Create a one dimensional array of ComplexNumber objects of length n
        // all real = a and all imag = b
        public static ComplexNumber[] oneDarray(int n, double a, double b){
                ComplexNumber[] c =new ComplexNumber[n];
                for(int i=0; i<n; i++){
                        c[i]= ComplexNumber.zero();
                        c[i].reset(a, b);
                }
                return c;
        }

        // Arithmetic mean of a one dimensional array of complex numbers
        public static ComplexNumber mean(ComplexNumber[] aa){
                int n = aa.length;
                ComplexNumber sum = new ComplexNumber(0.0D, 0.0D);
                for(int i=0; i<n; i++){
                        sum = sum.plus(aa[i]);
                }
                return sum.over((double)n);
        }

        // Create a one dimensional array of ComplexNumber objects of length n
        // all = the ComplexNumber constant
        public static ComplexNumber[] oneDarray(int n, ComplexNumber constant){
                ComplexNumber[] c =new ComplexNumber[n];
                for(int i=0; i<n; i++){
                        c[i]= ComplexNumber.copy(constant);
                }
                return c;
        }

        // Create a two dimensional array of ComplexNumber objects of dimensions n and m
        // all real = zero and all imag = zero
        private static ComplexNumber[][] twoDarray(int n, int m){
                ComplexNumber[][] a =new ComplexNumber[n][m];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                a[i][j]= ComplexNumber.zero();
                        }
                }
                return a;
        }

        // Create a two dimensional array of ComplexNumber objects of dimensions n and m
        // all real = a and all imag = b
        public static ComplexNumber[][] twoDarray(int n, int m, double a, double b){
                ComplexNumber[][] c =new ComplexNumber[n][m];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                c[i][j]= ComplexNumber.zero();
                                c[i][j].reset(a, b);
                        }
                }
                return c;
        }

        // Create a two dimensional array of ComplexNumber objects of dimensions n and m
        // all  =  the ComplexNumber constant
        public static ComplexNumber[][] twoDarray(int n, int m, ComplexNumber constant){
                ComplexNumber[][] c =new ComplexNumber[n][m];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                c[i][j]= ComplexNumber.copy(constant);
                        }
                }
                return c;
        }

        // Create a three dimensional array of ComplexNumber objects of dimensions n,  m and l
        // all real = zero and all imag = zero
        private static ComplexNumber[][][] threeDarray(int n, int m, int l){
                ComplexNumber[][][] a =new ComplexNumber[n][m][l];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                for(int k=0; k<l; k++){
                                        a[i][j][k]= ComplexNumber.zero();
                                }
                        }
                }
                return a;
        }

        // Create a three dimensional array of ComplexNumber objects of dimensions n, m and l
        // all real = a and all imag = b
        public static ComplexNumber[][][] threeDarray(int n, int m, int l, double a, double b){
                ComplexNumber[][][] c =new ComplexNumber[n][m][l];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                for(int k=0; k<l; k++){
                                        c[i][j][k]= ComplexNumber.zero();
                                        c[i][j][k].reset(a, b);
                                }
                        }
                }
                return c;
        }

        // Create a three dimensional array of ComplexNumber objects of dimensions n, m and l
        // all  =  the ComplexNumber constant
        public static ComplexNumber[][][] threeDarray(int n, int m, int l, ComplexNumber constant){
                ComplexNumber[][][] c =new ComplexNumber[n][m][l];
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                for(int k=0; k<l; k++){
                                        c[i][j][k]= ComplexNumber.copy(constant);
                                }
                        }
                }
                return c;
        }

        // COPY
        // Copy a single complex number [static method]
        private static ComplexNumber copy(ComplexNumber a){
            if(a==null){
                return null;
            }
            else{
                ComplexNumber b = new ComplexNumber();
                b.real=a.real;
                b.imag=a.imag;
                return b;
            }
        }

        // Copy a single complex number [instance method]
        public ComplexNumber copy(){
            if(this==null){
                return null;
            }
            else{
                ComplexNumber b = new ComplexNumber();
                b.real=this.real;
                b.imag=this.imag;
                return b;
            }
        }


        // Copy a 1D array of complex numbers (deep copy)
        // static metod
        public static ComplexNumber[] copy(ComplexNumber[] a){
            if(a==null){
                return null;
            }
            else{
                int n =a.length;
                ComplexNumber[] b = ComplexNumber.oneDarray(n);
                for(int i=0; i<n; i++){
                        b[i]= ComplexNumber.copy(a[i]);
                }
                return b;
            }
        }

        // Copy a 2D array of complex numbers (deep copy)
        public static ComplexNumber[][] copy(ComplexNumber[][] a){
            if(a==null){
                return null;
            }
            else{
                int n =a.length;
                int m =a[0].length;
                ComplexNumber[][] b = ComplexNumber.twoDarray(n, m);
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                b[i][j]= ComplexNumber.copy(a[i][j]);
                        }
                }
                return b;
            }
        }

        // Copy a 3D array of complex numbers (deep copy)
        public static ComplexNumber[][][] copy(ComplexNumber[][][] a){
            if(a==null){
                return null;
            }
            else{
                int n = a.length;
                int m = a[0].length;
                int l = a[0][0].length;
                ComplexNumber[][][] b = ComplexNumber.threeDarray(n, m, l);
                for(int i=0; i<n; i++){
                        for(int j=0; j<m; j++){
                                for(int k=0; k<l; k++){
                                        b[i][j][k]= ComplexNumber.copy(a[i][j][k]);
                                }
                        }
                }
                return b;
            }
        }

        // CLONE
        // Overrides Java.Object method clone
        // Copy a single complex number [instance method]
        public Object clone(){
            Object ret = null;

            if(this!=null){
                    ComplexNumber b = new ComplexNumber();
                    b.real=this.real;
                    b.imag=this.imag;
                    ret = b;
            }

            return ret;
        }

        // ADDITION
        // Add two ComplexNumber numbers [static method]
        public static ComplexNumber plus(ComplexNumber a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
                c.real=a.real+b.real;
                c.imag=a.imag+b.imag;
                return c;
        }

        // Add a double to a ComplexNumber number [static method]
        public static ComplexNumber plus(ComplexNumber a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a.real+b;
                c.imag=a.imag;
                return c;
        }

        // Add a ComplexNumber number to a double [static method]
        public static ComplexNumber plus(double a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
                c.real=a+b.real;
                c.imag=b.imag;
                return c;
        }

        // Add a double number to a double and return sum as ComplexNumber [static method]
        public static ComplexNumber plus(double a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a+b;
                c.imag=0.0D;
                return c;
        }

        // Add a ComplexNumber number to this ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        private ComplexNumber plus(ComplexNumber a){
                ComplexNumber b = new ComplexNumber();
                b.real=this.real + a.real;
                b.imag=this.imag + a.imag;
                return b;
        }

        // Add double number to this ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber plus(double a ){
                ComplexNumber b = new ComplexNumber();
                b.real = this.real + a;
                b.imag = this.imag;
                return b;
        }

        // Add a ComplexNumber number to this ComplexNumber number and replace this with the sum
        public void plusEquals(ComplexNumber a ){
                this.real+=a.real;
                this.imag+=a.imag;
        }

        // Add double number to this ComplexNumber number and replace this with the sum
        public void plusEquals(double a ){
                this.real+=a;
        }

        //  SUBTRACTION
        // Subtract two ComplexNumber numbers [static method]
        public static ComplexNumber minus (ComplexNumber a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
                c.real=a.real-b.real;
                c.imag=a.imag-b.imag;
                return c;
        }

        // Subtract a double from a ComplexNumber number [static method]
        private static ComplexNumber minus(ComplexNumber a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a.real-b;
                c.imag=a.imag;
                return c;
        }

        // Subtract a ComplexNumber number from a double [static method]
        private static ComplexNumber minus(double a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
                c.real=a-b.real;
                c.imag=-b.imag;
                return c;
        }

        // Subtract a double number to a double and return difference as ComplexNumber [static method]
        public static ComplexNumber minus(double a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a-b;
                c.imag=0.0D;
                return c;
        }

        // Subtract a ComplexNumber number from this ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber minus(ComplexNumber a ){
                ComplexNumber b = new ComplexNumber();
                b.real=this.real-a.real;
                b.imag=this.imag-a.imag;
                return b;
        }

        // Subtract a double number from this ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        private ComplexNumber minus(double a){
                ComplexNumber b = new ComplexNumber();
                b.real=this.real-a;
                b.imag=this.imag;
                return b;
                }

        // Subtract this ComplexNumber number from a double number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber transposedMinus(double a ){
                ComplexNumber b = new ComplexNumber();
                b.real=a - this.real;
                b.imag=this.imag;
                return b;
        }

        // Subtract a ComplexNumber number from this ComplexNumber number and replace this by the difference
        public void minusEquals(ComplexNumber a ){
                this.real-=a.real;
                this.imag-=a.imag;
        }

        // Subtract a double number from this ComplexNumber number and replace this by the difference
        public void minusEquals(double a ){
                this.real-=a;
        }

        /*// MULTIPLICATION
        // Sets the infinity handling option in multiplication and division
        // infOption -> true; standard arithmetic overriden - see above (instance variable definitions) for details
        // infOption -> false: standard arithmetic used
        public static void setInfOption(boolean infOpt){
                ComplexNumber.infOption = infOpt;
        }

        // Sets the infinity handling option in multiplication and division
        // opt = 0:   infOption -> true; standard arithmetic overriden - see above (instance variable definitions) for details
        // opt = 1:   infOption -> false: standard arithmetic used
        public static void setInfOption(int opt){
                if(opt<0 || opt>1)throw new IllegalArgumentException("opt must be 0 or 1");
                ComplexNumber.infOption = true;
                if(opt==1)ComplexNumber.infOption = false;
        }*/

        // Gets the infinity handling option in multiplication and division
        // infOption -> true; standard arithmetic overriden - see above (instance variable definitions) for details
        // infOption -> false: standard arithmetic used
        public static boolean getInfOption(){
                return true;
        }

        // Multiply two ComplexNumber numbers [static method]
        private static ComplexNumber times(ComplexNumber a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber(0.0D, 0.0D);
            if(a.isInfinite() && !b.isZero())
            {
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }
            if(b.isInfinite() && !a.isZero()){
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }

            c.real=a.real*b.real-a.imag*b.imag;
                c.imag=a.real*b.imag+a.imag*b.real;
                return c;
        }

        // Multiply a ComplexNumber number by a double [static method]
        private static ComplexNumber times(ComplexNumber a, double b){
                ComplexNumber c = new ComplexNumber();
            if(a.isInfinite() && b != 0.0D)
            {
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }
            if(FlanaganMath.isInfinity(b) && !a.isZero()){
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }
            c.real=a.real*b;
                c.imag=a.imag*b;
                return c;
        }

        // Multiply a double by a ComplexNumber number [static method]
        public static ComplexNumber times(double a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
            if(b.isInfinite() && a != 0.0D)
            {
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }
            if(FlanaganMath.isInfinity(a) && !b.isZero()){
                c.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return c;
            }

            c.real=a*b.real;
                c.imag=a*b.imag;
                return c;
        }

        // Multiply a double number to a double and return product as ComplexNumber [static method]
        public static ComplexNumber times(double a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a*b;
                c.imag=0.0D;
                return c;
        }

        // Multiply this ComplexNumber number by a ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber times(ComplexNumber a){
                ComplexNumber b = new ComplexNumber();
            if(this.isInfinite() && !a.isZero())
            {
                b.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return b;
            }
            if(a.isInfinite() && !this.isZero()){
                b.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return b;
            }

            b.real=this.real*a.real-this.imag*a.imag;
                b.imag=this.real*a.imag+this.imag*a.real;
                return b;
        }

        // Multiply this ComplexNumber number by a double [instance method]
        // this ComplexNumber number remains unaltered
        private ComplexNumber times(double a){
                ComplexNumber b = new ComplexNumber();
            if(this.isInfinite() && a != 0.0D)
            {
                b.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return b;
            }
            if(FlanaganMath.isInfinity(a) && !this.isZero()){
                b.reset(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                return b;
            }

            b.real=this.real*a;
                b.imag=this.imag*a;
                return b;
        }

        // Multiply this ComplexNumber number by a ComplexNumber number and replace this by the product
        public void timesEquals(ComplexNumber a){
                ComplexNumber b = new ComplexNumber();
                boolean test = true;
            if((this.isInfinite() && !a.isZero()) || (a.isInfinite() && !this.isZero()))
            {
                this.real = Double.POSITIVE_INFINITY;
                this.imag = Double.POSITIVE_INFINITY;
                test = false;
            }
            if(test){
                    b.real=a.real*this.real-a.imag*this.imag;
                    b.imag=a.real*this.imag+a.imag*this.real;
                    this.real=b.real;
                    this.imag=b.imag;
               }
        }

        // Multiply this ComplexNumber number by a double and replace this by the product
        public void timesEquals(double a){
                boolean test = true;
            if((this.isInfinite() && a != 0.0D) || (FlanaganMath.isInfinity(a) && !this.isZero()))
            {
                this.real = Double.POSITIVE_INFINITY;
                this.imag = Double.POSITIVE_INFINITY;
                test = false;
            }
            if(test){
                    this.real=this.real*a;
                    this.imag=this.imag*a;
                }
        }


        // DIVISION
        // Division of two ComplexNumber numbers a/b [static method]
        private static ComplexNumber over(ComplexNumber a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber(0.0D,0.0D);
                if(!a.isInfinite() && b.isInfinite())return c;

                double denom = 0.0D, ratio = 0.0D;
                if(a.isZero()){
                        if(b.isZero()){
                                c.real=Double.NaN;
                                c.imag=Double.NaN;
                        }
                        else{
                                c.real=0.0D;
                                c.imag=0.0D;
                        }
                }
                else{
                        if(Math.abs(b.real)>=Math.abs(b.imag)){
                                ratio=b.imag/b.real;
                                denom=b.real+b.imag*ratio;
                                c.real=(a.real+a.imag*ratio)/denom;
                                c.imag=(a.imag-a.real*ratio)/denom;
                        }
                        else{
                                ratio=b.real/b.imag;
                                denom=b.real*ratio+b.imag;
                                c.real=(a.real*ratio+a.imag)/denom;
                                c.imag=(a.imag*ratio-a.real)/denom;
                        }
                }
                return c;
        }

        // Division of a ComplexNumber number, a, by a double, b [static method]
        public static ComplexNumber over(ComplexNumber a, double b){
                ComplexNumber c = new ComplexNumber(0.0D, 0.0D);
                if(FlanaganMath.isInfinity(b))return c;

                c.real=a.real/b;
                c.imag=a.imag/b;
                return c;
        }

        // Division of a double, a, by a ComplexNumber number, b  [static method]
        private static ComplexNumber over(double a, ComplexNumber b){
                ComplexNumber c = new ComplexNumber();
                if(!FlanaganMath.isInfinity(a) && b.isInfinite())return c;

                double denom, ratio;

                if(a==0.0D){
                        if(b.isZero()){
                                c.real=Double.NaN;
                                c.imag=Double.NaN;
                        }
                        else{
                                c.real=0.0D;
                                c.imag=0.0D;
                        }
                }
                else{
                        if(Math.abs(b.real)>=Math.abs(b.imag)){
                                ratio=b.imag/b.real;
                                denom=b.real+b.imag*ratio;
                                c.real=a/denom;
                                c.imag=-a*ratio/denom;
                        }
                        else{
                                ratio=b.real/b.imag;
                                denom=b.real*ratio+b.imag;
                                c.real=a*ratio/denom;
                                c.imag=-a/denom;
                        }
                }
                return c;
        }

        // Divide a double number by a double and return quotient as ComplexNumber [static method]
        public static ComplexNumber over(double a, double b){
                ComplexNumber c = new ComplexNumber();
                c.real=a/b;
                c.imag=0.0;
                return c;
        }

        // Division of this ComplexNumber number by a ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber over(ComplexNumber a){
                ComplexNumber b = new ComplexNumber(0.0D, 0.0D);
                if(!this.isInfinite() && a.isInfinite())return b;

                double denom = 0.0D, ratio = 0.0D;
                if(Math.abs(a.real)>=Math.abs(a.imag)){
                        ratio=a.imag/a.real;
                        denom=a.real+a.imag*ratio;
                        b.real=(this.real+this.imag*ratio)/denom;
                        b.imag=(this.imag-this.real*ratio)/denom;
                }
                else
                {
                        ratio=a.real/a.imag;
                        denom=a.real*ratio+a.imag;
                        b.real=(this.real*ratio+this.imag)/denom;
                        b.imag=(this.imag*ratio-this.real)/denom;
                }
                return b;
        }

        // Division of this ComplexNumber number by a double [instance method]
        // this ComplexNumber number remains unaltered
        private ComplexNumber over(double a){
                ComplexNumber b = new ComplexNumber(0.0D, 0.0D);

                b.real=this.real/a;
                b.imag=this.imag/a;
                return b;
        }

        // Division of a double by this ComplexNumber number [instance method]
        // this ComplexNumber number remains unaltered
        public ComplexNumber transposedOver(double a){
                ComplexNumber c = new ComplexNumber(0.0D, 0.0D);
                if(!FlanaganMath.isInfinity(a) && this.isInfinite())return c;

                double denom = 0.0D, ratio = 0.0D;
                if(Math.abs(this.real)>=Math.abs(this.imag)){
                        ratio=this.imag/this.real;
                        denom=this.real+this.imag*ratio;
                        c.real=a/denom;
                        c.imag=-a*ratio/denom;
                }
                else
                {
                        ratio=this.real/this.imag;
                        denom=this.real*ratio+this.imag;
                        c.real=a*ratio/denom;
                        c.imag=-a/denom;
                }
                return c;
        }

        // Division of this ComplexNumber number by a ComplexNumber number and replace this by the quotient
        public void overEquals(ComplexNumber b){
                ComplexNumber c = new ComplexNumber(0.0D, 0.0D);

                boolean test = true;
                if(!this.isInfinite() && b.isInfinite()){
                        this.real = 0.0D;
                        this.imag = 0.0D;
                        test=false;
                }
               if(test){
                    double denom = 0.0D, ratio = 0.0D;
                    if(Math.abs(b.real)>=Math.abs(b.imag)){
                        ratio=b.imag/b.real;
                        denom=b.real+b.imag*ratio;
                        c.real=(this.real+this.imag*ratio)/denom;
                        c.imag=(this.imag-this.real*ratio)/denom;
                    }
                    else
                    {
                        ratio=b.real/b.imag;
                        denom=b.real*ratio+b.imag;
                        c.real=(this.real*ratio+this.imag)/denom;
                        c.imag=(this.imag*ratio-this.real)/denom;
                    }
                    this.real = c.real;
                    this.imag = c.imag;
                }
        }

        // Division of this ComplexNumber number by a double and replace this by the quotient
        public void overEquals(double a){
                this.real=this.real/a;
                this.imag=this.imag/a;
        }

        // RECIPROCAL
        // Returns the reciprocal (1/a) of a ComplexNumber number (a) [static method]
        public static ComplexNumber inverse(ComplexNumber a){
                ComplexNumber b = new ComplexNumber(0.0D, 0.0D);
                if(a.isInfinite())return b;

                b = ComplexNumber.over(1.0D, a);
                return b;
        }

        // Returns the reciprocal (1/a) of a ComplexNumber number (a) [instance method]
        private ComplexNumber inverse(){
                ComplexNumber b = new ComplexNumber(0.0D, 0.0D);
                b = ComplexNumber.over(1.0D, this);
                return b;
        }

        // FURTHER MATHEMATICAL FUNCTIONS

        // Negates a ComplexNumber number [static method]
        public static ComplexNumber negate(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c.real=-a.real;
                c.imag=-a.imag;
                return c;
        }

        // Negates a ComplexNumber number [instance method]
        public ComplexNumber negate(){
                ComplexNumber c = new ComplexNumber();
                c.real=-this.real;
                c.imag=-this.imag;
                return c;
        }

        // Absolute value (modulus) of a complex number [static method]
        public static double abs(ComplexNumber a){
                double rmod = Math.abs(a.real);
                double imod = Math.abs(a.imag);
                double ratio = 0.0D;
                double res = 0.0D;

                if(rmod==0.0D){
                res=imod;
                }
                else{
                if(imod==0.0D){
                        res=rmod;
                }
                        if(rmod>=imod){
                                ratio=a.imag/a.real;
                                res=rmod*Math.sqrt(1.0D + ratio*ratio);
                        }
                        else{
                                ratio=a.real/a.imag;
                                res=imod*Math.sqrt(1.0D + ratio*ratio);
                        }
                }
                return res;
        }

        // Absolute value (modulus) of a complex number [instance method]
        public double abs(){
                double rmod = Math.abs(this.real);
                double imod = Math.abs(this.imag);
                double ratio = 0.0D;
                double res = 0.0D;

                if(rmod==0.0D){
                        res=imod;
                }
                else{
                        if(imod==0.0D){
                                res=rmod;
                        }
                        if(rmod>=imod){
                                ratio=this.imag/this.real;
                                res=rmod*Math.sqrt(1.0D + ratio*ratio);
                        }
                        else
                        {
                                ratio=this.real/this.imag;
                                res=imod*Math.sqrt(1.0D + ratio*ratio);
                        }
                }
                return res;
        }


        // Square of the absolute value (modulus) of a complex number [static method]
        public static double squareAbs(ComplexNumber a){
                return a.real*a.real + a.imag*a.imag;
        }

        // Square of the absolute value (modulus) of a complex number [instance method]
        public double squareAbs(){
                return this.real*this.real + this.imag*this.imag;
        }

        // Argument of a complex number [static method]
        public static double arg(ComplexNumber a){
                return Math.atan2(a.imag, a.real);
        }

        // Argument of a complex number [instance method]
        public double arg(){
                return Math.atan2(this.imag, this.real);
        }

        // ComplexNumber conjugate of a complex number [static method]
        public static ComplexNumber conjugate(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c.real=a.real;
                c.imag=-a.imag;
                return c;
        }

        // ComplexNumber conjugate of a complex number [instance method]
        public ComplexNumber conjugate(){
                ComplexNumber c = new ComplexNumber();
                c.real=this.real;
                c.imag=-this.imag;
                return c;
        }

        // Returns the length of the hypotenuse of a and b i.e. sqrt(abs(a)*abs(a)+abs(b)*abs(b))
        // where a and b are ComplexNumber [without unecessary overflow or underflow]
        public static double hypot(ComplexNumber aa, ComplexNumber bb){
                double amod= ComplexNumber.abs(aa);
                double bmod= ComplexNumber.abs(bb);
                double cc = 0.0D, ratio = 0.0D;

                if(amod==0.0D){
                        cc=bmod;
                }
                else{
                        if(bmod==0.0D){
                                cc=amod;
                        }
                        else{
                                if(amod>=bmod){
                                        ratio=bmod/amod;
                                        cc=amod*Math.sqrt(1.0 + ratio*ratio);
                                }
                                else{
                                        ratio=amod/bmod;
                                        cc=bmod*Math.sqrt(1.0 + ratio*ratio);
                                }
                        }
                }
                return cc;
        }

        // Exponential of a complex number
        private static ComplexNumber exp(ComplexNumber aa){
                ComplexNumber z = new ComplexNumber();

                double a = aa.real;
                double b = aa.imag;

                if(b==0.0D){
                        z.real=Math.exp(a);
                        z.imag=0.0D;
                }
                else{
                        if(a==0D){
                                z.real=Math.cos(b);
                                z.imag=Math.sin(b);
                        }
                        else{
                                double c=Math.exp(a);
                                z.real=c*Math.cos(b);
                                z.imag=c*Math.sin(b);
                        }
                }
                return z;
        }

        // Exponential of a real number returned as a complex number
        public static ComplexNumber exp(double aa){
                ComplexNumber bb = new ComplexNumber(aa, 0.0D);
                return ComplexNumber.exp(bb);
        }

        // Returns exp(j*arg) where arg is real (a double)
        public static ComplexNumber expPlusJayArg(double arg){
                ComplexNumber argc = new ComplexNumber(0.0D, arg);
                return ComplexNumber.exp(argc);
        }

        // Returns exp(-j*arg) where arg is real (a double)
        public static ComplexNumber expMinusJayArg(double arg){
                ComplexNumber argc = new ComplexNumber(0.0D, -arg);
                return ComplexNumber.exp(argc);
        }

        // Principal value of the natural log of an ComplexNumber number
        private static ComplexNumber log(ComplexNumber aa){

                double a=aa.real;
                double b=aa.imag;
                ComplexNumber c = new ComplexNumber();

                c.real=Math.log(ComplexNumber.abs(aa));
                c.imag=Math.atan2(b,a);

                return c;
        }

        // Roots
        // Principal value of the square root of a complex number
        private static ComplexNumber sqrt(ComplexNumber aa){
                double a=aa.real;
                double b=aa.imag;
                ComplexNumber c = new ComplexNumber();

                if(b==0.0D){
                        if(a>=0.0D){
                                c.real=Math.sqrt(a);
                                c.imag=0.0D;
                        }
                        else{
                                c.real=0.0D;
                                c.imag= Math.sqrt(-a);
                        }
                }
                else{
                        double w, ratio;
                        double amod=Math.abs(a);
                        double bmod=Math.abs(b);
                        if(amod>=bmod){
                                ratio=b/a;
                                w=Math.sqrt(amod)*Math.sqrt(0.5D*(1.0D + Math.sqrt(1.0D + ratio*ratio)));
                        }
                        else{
                                ratio=a/b;
                                w=Math.sqrt(bmod)*Math.sqrt(0.5D*(Math.abs(ratio) + Math.sqrt(1.0D + ratio*ratio)));
                        }
                        if(a>=0.0){
                                c.real=w;
                                c.imag=b/(2.0D*w);
                        }
                        else{
                                if(b>=0.0){
                                        c.imag=w;
                                        c.real=bmod/(2.0D*c.imag);
                                }
                                else{
                                        c.imag=-w;
                                        c.real=bmod/(2.0D*c.imag);
                                }
                        }
                }
                return c;
        }

        // Principal value of the nth root of a complex number (n = integer > 1)
        public static ComplexNumber nthRoot(ComplexNumber aa, int n ){
                if(n==0)throw new ArithmeticException("Division by zero (n = 0 - infinite root) attempted in ComplexNumber.nthRoot");

                double a=aa.real;
                double b=aa.imag;
                ComplexNumber c = new ComplexNumber();

                double d=(double) n;
                double r = Math.pow(ComplexNumber.abs(aa), 1.0D/d);
                double theta = Math.atan2(b,a)/d;
                c.real= r*Math.cos(theta);
                c.imag= r*Math.sin(theta);
                return c;
        }

        // Powers
        // Square of a complex number
        private static ComplexNumber square(ComplexNumber aa){
                ComplexNumber c = new ComplexNumber();
                c.real= aa.real*aa.real-aa.imag*aa.imag;
                c.imag= 2.0D*aa.real*aa.imag;
                return c;
        }

        // returns a ComplexNumber number raised to a ComplexNumber power
        public static ComplexNumber pow(ComplexNumber a, ComplexNumber b ){
                ComplexNumber c = new ComplexNumber();
                c= ComplexNumber.exp(ComplexNumber.times(b, ComplexNumber.log(a)));
                return c;
        }

        // returns a ComplexNumber number raised to a double power
        public static ComplexNumber pow(ComplexNumber a, double b){
                return  powDouble(a, b);
        }

        // returns a ComplexNumber number raised to an integer, i.e. int, power
        public static ComplexNumber pow(ComplexNumber a, int n ){
                double b = (double) n;
                return  powDouble(a, b);
        }

        // returns a double raised to a ComplexNumber power
        public static ComplexNumber pow(double a, ComplexNumber b ){
                ComplexNumber c = new ComplexNumber();
                double z = Math.pow(a, b.real);
                c= ComplexNumber.exp(ComplexNumber.times(ComplexNumber.plusJay(), b.imag * Math.log(a)));
                c= ComplexNumber.times(z, c);
                return c;
        }

        // ComplexNumber trigonometric functions

        // Sine of an ComplexNumber number
        private static ComplexNumber sin(ComplexNumber aa){
                ComplexNumber c = new ComplexNumber();
                double a = aa.real;
                double b = aa.imag;
                c.real = Math.sin(a)* FlanaganMath.cosh(b);
                c.imag = Math.cos(a)* FlanaganMath.sinh(b);
                return c;
        }

        // Cosine of an ComplexNumber number
        private static ComplexNumber cos(ComplexNumber aa){
                ComplexNumber c = new ComplexNumber();
                double a = aa.real;
                double b = aa.imag;
                c.real= Math.cos(a)* FlanaganMath.cosh(b);
                c.imag= -Math.sin(a)* FlanaganMath.sinh(b);
                return c;
        }

        // Secant of an ComplexNumber number
        private static ComplexNumber sec(ComplexNumber aa){
                ComplexNumber c = new ComplexNumber();
                double a = aa.real;
                double b = aa.imag;
                c.real= Math.cos(a)* FlanaganMath.cosh(b);
                c.imag= -Math.sin(a)* FlanaganMath.sinh(b);
                return c.inverse();
        }

        // Cosecant of an ComplexNumber number
        public static ComplexNumber csc(ComplexNumber aa ){
                ComplexNumber c = new ComplexNumber();
                double a = aa.real;
                double b = aa.imag;
                c.real = Math.sin(a)* FlanaganMath.cosh(b);
                c.imag = Math.cos(a)* FlanaganMath.sinh(b);
                return c.inverse();
        }

        // Tangent of an ComplexNumber number
        public static ComplexNumber tan(ComplexNumber aa ){
                ComplexNumber c = new ComplexNumber();
                double denom = 0.0D;
                double a = aa.real;
                double b = aa.imag;

                ComplexNumber x = new ComplexNumber(Math.sin(a)* FlanaganMath.cosh(b), Math.cos(a)* FlanaganMath.sinh(b));
                ComplexNumber y = new ComplexNumber(Math.cos(a)* FlanaganMath.cosh(b), -Math.sin(a)* FlanaganMath.sinh(b));
                c= ComplexNumber.over(x, y);
                return c;
        }

        // Cotangent of an ComplexNumber number
        public static ComplexNumber cot(ComplexNumber aa ){
                ComplexNumber c = new ComplexNumber();
                double denom = 0.0D;
                double a = aa.real;
                double b = aa.imag;

                ComplexNumber x = new ComplexNumber(Math.sin(a)* FlanaganMath.cosh(b), Math.cos(a)* FlanaganMath.sinh(b));
                ComplexNumber y = new ComplexNumber(Math.cos(a)* FlanaganMath.cosh(b), -Math.sin(a)* FlanaganMath.sinh(b));
                c= ComplexNumber.over(y, x);
                return c;
        }

        // Exsecant of an ComplexNumber number
        public static ComplexNumber exsec(ComplexNumber aa ){
                return ComplexNumber.sec(aa).minus(1.0D);
        }

        // Versine of an ComplexNumber number
        private static ComplexNumber vers(ComplexNumber aa){
                return ComplexNumber.plusOne().minus(ComplexNumber.cos(aa));
        }

        // Coversine of an ComplexNumber number
        public static ComplexNumber covers(ComplexNumber aa ){
                return ComplexNumber.plusOne().minus(ComplexNumber.sin(aa));
        }

        // Haversine of an ComplexNumber number
        public static ComplexNumber hav(ComplexNumber aa ){
                return ComplexNumber.vers(aa).over(2.0D);
        }

        // Hyperbolic sine of a ComplexNumber number
        private static ComplexNumber sinh(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c=a.times(plusJay());
                c=(ComplexNumber.minusJay()).times(ComplexNumber.sin(c));
                return c;
        }

        // Hyperbolic cosine of a ComplexNumber number
        private static ComplexNumber cosh(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c=a.times(ComplexNumber.plusJay());
                c= ComplexNumber.cos(c);
                return c;
        }

        // Hyperbolic tangent of a ComplexNumber number
        public static ComplexNumber tanh(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                c = (ComplexNumber.sinh(a)).over(ComplexNumber.cosh(a));
                return c;
        }

        // Hyperbolic cotangent of a ComplexNumber number
        public static ComplexNumber coth(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                c = (ComplexNumber.cosh(a)).over(ComplexNumber.sinh(a));
                return c;
        }

        // Hyperbolic secant of a ComplexNumber number
        public static ComplexNumber sech(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                c = (ComplexNumber.cosh(a)).inverse();
                return c;
        }

        // Hyperbolic cosecant of a ComplexNumber number
        public static ComplexNumber csch(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                c = (ComplexNumber.sinh(a)).inverse();
                return c;
        }

        // Inverse sine of a ComplexNumber number
        private static ComplexNumber asin(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c= ComplexNumber.sqrt(ComplexNumber.minus(1.0D, ComplexNumber.square(a)));
                c=(ComplexNumber.plusJay().times(a)).plus(c);
                c= ComplexNumber.minusJay().times(ComplexNumber.log(c));
                return c;
        }

        // Inverse cosine of a ComplexNumber number
        private static ComplexNumber acos(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                c= ComplexNumber.sqrt(ComplexNumber.minus(ComplexNumber.square(a), 1.0));
                c=a.plus(c);
                c= ComplexNumber.minusJay().times(ComplexNumber.log(c));
                return c;
        }

        // Inverse tangent of a ComplexNumber number
        private static ComplexNumber atan(ComplexNumber a){
                ComplexNumber c = new ComplexNumber();
                ComplexNumber d = new ComplexNumber();

                c= ComplexNumber.plusJay().plus(a);
                d= ComplexNumber.plusJay().minus(a);
                c=c.over(d);
                c= ComplexNumber.log(c);
                c= ComplexNumber.plusJay().times(c);
                c=c.over(2.0D);
                return c;
        }

        // Inverse cotangent of a ComplexNumber number
        public static ComplexNumber acot(ComplexNumber a ){
            return ComplexNumber.atan(a.inverse());
        }

        // Inverse secant of a ComplexNumber number
        public static ComplexNumber asec(ComplexNumber a ){
            return ComplexNumber.acos(a.inverse());
        }

        // Inverse cosecant of a ComplexNumber number
        public static ComplexNumber acsc(ComplexNumber a ){
            return ComplexNumber.asin(a.inverse());
        }

        // Inverse exsecant of a ComplexNumber number
        public static ComplexNumber aexsec(ComplexNumber a ){
            ComplexNumber c = a.plus(1.0D);
            return ComplexNumber.asin(c.inverse());
        }

        // Inverse versine of a ComplexNumber number
        public static ComplexNumber avers(ComplexNumber a ){
            ComplexNumber c = ComplexNumber.plusOne().plus(a);
            return ComplexNumber.acos(c);
        }

        // Inverse coversine of a ComplexNumber number
        public static ComplexNumber acovers(ComplexNumber a ){
            ComplexNumber c = ComplexNumber.plusOne().plus(a);
            return ComplexNumber.asin(c);
        }

        // Inverse haversine of a ComplexNumber number
        public static ComplexNumber ahav(ComplexNumber a ){
            ComplexNumber c = ComplexNumber.plusOne().minus(a.times(2.0D));
            return ComplexNumber.acos(c);
        }

        // Inverse hyperbolic sine of a ComplexNumber number
        public static ComplexNumber asinh(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber(0.0D, 0.0D);
                c= ComplexNumber.sqrt(ComplexNumber.square(a).plus(1.0D));
                c=a.plus(c);
                c= ComplexNumber.log(c);

                return c;
        }

        // Inverse hyperbolic cosine of a ComplexNumber number
        public static ComplexNumber acosh(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                c= ComplexNumber.sqrt(ComplexNumber.square(a).minus(1.0D));
                c=a.plus(c);
                c= ComplexNumber.log(c);
                return c;
        }

        // Inverse hyperbolic tangent of a ComplexNumber number
        public static ComplexNumber atanh(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                ComplexNumber d = new ComplexNumber();
                c= ComplexNumber.plusOne().plus(a);
                d= ComplexNumber.plusOne().minus(a);
                c=c.over(d);
                c= ComplexNumber.log(c);
                c=c.over(2.0D);
                return c;
        }

        // Inverse hyperbolic cotangent of a ComplexNumber number
        public static ComplexNumber acoth(ComplexNumber a ){
                ComplexNumber c = new ComplexNumber();
                ComplexNumber d = new ComplexNumber();
                c= ComplexNumber.plusOne().plus(a);
                d=a.plus(1.0D);
                c=c.over(d);
                c= ComplexNumber.log(c);
                c=c.over(2.0D);
                return c;
        }

        // Inverse hyperbolic secant of a ComplexNumber number
        public static ComplexNumber asech(ComplexNumber a ){
                ComplexNumber c = a.inverse();
                ComplexNumber d = (ComplexNumber.square(a)).minus(1.0D);
                return ComplexNumber.log(c.plus(ComplexNumber.sqrt(d)));
        }

        // Inverse hyperbolic cosecant of a ComplexNumber number
        public static ComplexNumber acsch(ComplexNumber a ){
                ComplexNumber c = a.inverse();
                ComplexNumber d = (ComplexNumber.square(a)).plus(1.0D);
                return ComplexNumber.log(c.plus(ComplexNumber.sqrt(d)));
        }

        // LOGICAL FUNCTIONS
        // Returns true if the ComplexNumber number has a zero imaginary part, i.e. is a real number
        public static boolean isReal(ComplexNumber a){
                boolean test = false;
                if(a.imag==0.0D)test = true;
                return test;
        }

        public boolean isReal(){
                boolean test = false;
                if(Math.abs(this.imag)==0.0D)test = true;
                return test;
        }

        // Returns true if the ComplexNumber number has a zero real and a zero imaginary part
        // i.e. has a zero modulus
        public static boolean isZero(ComplexNumber a){
                boolean test = false;
                if(Math.abs(a.real)==0.0D && Math.abs(a.imag)==0.0D)test = true;
                return test;
        }

        private boolean isZero(){
                boolean test = false;
                if(Math.abs(this.real)==0.0D && Math.abs(this.imag)==0.0D)test = true;
                return test;
        }

        // Returns true if either the real or the imaginary part of the ComplexNumber number
        // is equal to plus infinity
        public boolean isPlusInfinity(){
                boolean test = false;
                if(this.real==Double.POSITIVE_INFINITY || this.imag==Double.POSITIVE_INFINITY)test = true;
                return test;
        }

        public static boolean isPlusInfinity(ComplexNumber a){
                boolean test = false;
                if(a.real==Double.POSITIVE_INFINITY || a.imag==Double.POSITIVE_INFINITY)test = true;
                return test;
        }

        // Returns true if either the real or the imaginary part of the ComplexNumber number
        // is equal to minus infinity
        public boolean isMinusInfinity(){
                boolean test = false;
                if(this.real==Double.NEGATIVE_INFINITY || this.imag==Double.NEGATIVE_INFINITY)test = true;
                return test;
        }

        public static boolean isMinusInfinity(ComplexNumber a){
                boolean test = false;
                if(a.real==Double.NEGATIVE_INFINITY || a.imag==Double.NEGATIVE_INFINITY)test = true;
                return test;
        }


        // Returns true if either the real or the imaginary part of the ComplexNumber number
        // is equal to either infinity or minus plus infinity
        public static boolean isInfinite(ComplexNumber a){
        boolean test = false;
                if(a.real==Double.POSITIVE_INFINITY || a.imag==Double.POSITIVE_INFINITY)test = true;
                if(a.real==Double.NEGATIVE_INFINITY || a.imag==Double.NEGATIVE_INFINITY)test = true;
                return test;
        }

        private boolean isInfinite(){
                boolean test = false;
                if(this.real==Double.POSITIVE_INFINITY || this.imag==Double.POSITIVE_INFINITY)test = true;
                if(this.real==Double.NEGATIVE_INFINITY || this.imag==Double.NEGATIVE_INFINITY)test = true;
                return test;
        }

        // Returns true if the ComplexNumber number is NaN (Not a Number)
        // i.e. is the result of an uninterpretable mathematical operation
        private static boolean isNaN(ComplexNumber a){
                boolean test = false;
                if(a.real!=a.real || a.imag!=a.imag)test = true;
                return test;
        }

        private boolean isNaN(){
                boolean test = false;
                if(this.real!=this.real || this.imag!=this.imag)test = true;
                return test;
        }

        // Returns true if two ComplexNumber number are identical
        // Follows the Sun Java convention of treating all NaNs as equal
        // i.e. does not satisfies the IEEE 754 specification
        // but does let hashtables operate properly
        public boolean equals(ComplexNumber a){
                boolean test = false;
                if(this.isNaN()&&a.isNaN()){
                        test=true;
                }
                else{
                        if(this.real == a.real && this.imag == a.imag)test = true;
                }
                return test;
        }
        
        public boolean equals(Object a){
        	
            boolean test = false;
            if(!(a instanceof ComplexNumber))
            	return false;
            else if(this.isNaN()&& ((ComplexNumber)a).isNaN()){
                    test=true;
            }
            else{
                    if(this.real == ((ComplexNumber)a).real && this.imag == ((ComplexNumber)a).imag)test = true;
            }
            return test;
    }

        public boolean isEqual(ComplexNumber a){
                boolean test = false;
                if(this.isNaN()&&a.isNaN()){
                        test=true;
                }
                else{
                        if(this.real == a.real && this.imag == a.imag)test = true;
                }
                return test;
        }


        public static boolean isEqual(ComplexNumber a, ComplexNumber b){
                boolean test = false;
                if(isNaN(a)&&isNaN(b)){
                        test=true;
                }
                else{
                        if(a.real == b.real && a.imag == b.imag)test = true;
                }
                return test;
        }



        // returns true if the differences between the real and imaginary parts of two complex numbers
        // are less than fract times the larger real and imaginary part
        public boolean equalsWithinLimits(ComplexNumber a, double fract){
            return isEqualWithinLimits(a, fract);
        }

        private boolean isEqualWithinLimits(ComplexNumber a, double fract){
            boolean test = false;

            double rt = this.getReal();
            double ra = a.getReal();
            double it = this.getImag();
            double ia = a.getImag();
            double rdn = 0.0D;
            double idn = 0.0D;
            double rtest = 0.0D;
            double itest = 0.0D;

            if(rt==0.0D && it==0.0D && ra==0.0D && ia==0.0D)test=true;
            if(!test){
                rdn=Math.abs(rt);
                if(Math.abs(ra)>rdn)rdn=Math.abs(ra);
                if(rdn==0.0D){
                    rtest=0.0;
                }
                else{
                    rtest=Math.abs(ra-rt)/rdn;
                }
                idn=Math.abs(it);
                if(Math.abs(ia)>idn)idn=Math.abs(ia);
                if(idn==0.0D){
                    itest=0.0;
                }
                else{
                    itest=Math.abs(ia-it)/idn;
                }
                if(rtest<fract && itest<fract)test=true;
            }

            return test;
        }

        public static boolean isEqualWithinLimits(ComplexNumber a, ComplexNumber b, double fract){
            boolean test = false;

            double rb = b.getReal();
            double ra = a.getReal();
            double ib = b.getImag();
            double ia = a.getImag();
            double rdn = 0.0D;
            double idn = 0.0D;

            if(ra==0.0D && ia==0.0D && rb==0.0D && ib==0.0D)test=true;
            if(!test){
                rdn=Math.abs(rb);
                if(Math.abs(ra)>rdn)rdn=Math.abs(ra);
                idn=Math.abs(ib);
                if(Math.abs(ia)>idn)idn=Math.abs(ia);
                if(Math.abs(ra-rb)/rdn<fract && Math.abs(ia-ia)/idn<fract)test=true;
            }

            return test;
        }

        // SOME USEFUL NUMBERS
        // returns the number zero (0) as a complex number
        private static ComplexNumber zero(){
                ComplexNumber c = new ComplexNumber();
                c.real=0.0D;
                c.imag=0.0D;
                return c;
        }

        // returns the number one (+1) as a complex number
        private static ComplexNumber plusOne(){
                ComplexNumber c = new ComplexNumber();
                c.real=1.0D;
                c.imag=0.0D;
                return c;
        }

        // returns the number minus one (-1) as a complex number
        public static ComplexNumber minusOne(){
                ComplexNumber c = new ComplexNumber();
                c.real=-1.0D;
                c.imag=0.0D;
                return c;
        }

        // returns plus j
        private static ComplexNumber plusJay(){
                ComplexNumber c = new ComplexNumber();
                c.real=0.0D;
                c.imag=1.0D;
                return c;
        }

        // returns minus j
        private static ComplexNumber minusJay(){
                ComplexNumber c = new ComplexNumber();
                c.real=0.0D;
                c.imag=-1.0D;
                return c;
        }

        // returns pi as a ComplexNumber number
        public static ComplexNumber pi(){
                ComplexNumber c = new ComplexNumber();
                c.real=Math.PI;
                c.imag=0.0D;
                return c;
        }

        // returns 2.pi.j
        public static ComplexNumber twoPiJay(){
                ComplexNumber c = new ComplexNumber();
                c.real=0.0D;
                c.imag=2.0D*Math.PI;
                return c;
        }

        // PRIVATE METHODS
        // returns a ComplexNumber number raised to a double power
        // this method is used for calculation within this class file
        // see above for corresponding public method
        private static ComplexNumber powDouble(ComplexNumber a, double b){
                ComplexNumber z = new ComplexNumber();
                double re=a.real;
                double im=a.imag;

                if(im==0.0D){
                        z.real=Math.pow(re, b);
                        z.imag=0.0D;
                }
                else{
                        if(re==0.0D){
                                z= ComplexNumber.exp(ComplexNumber.times(b, ComplexNumber.log(a)));
                        }
                        else{
                                double c=Math.pow(re*re+im*im, b/2.0D);
                                double th=Math.atan2(im, re);
                                z.real=c*Math.cos(b*th);
                                z.imag=c*Math.sin(b*th);
                        }
                }
                return z;
        }
        
        public static boolean greaterThan(ComplexNumber a, ComplexNumber b) {
        	return (a.real > b.real || (a.real != b.real || (a.imag > b.imag || (a.imag != b.imag))));
        }
        
        
}

