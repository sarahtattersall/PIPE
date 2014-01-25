package pipe.calculations;

import pipe.models.interfaces.IDynamicMarking;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that handles the run-time creation of a DynamicMarkingImpl class that
 * implements the IDynamicMarking interface, according to logical expressions entered
 * by the user. It also compiles and loads the class in memory
 *
 * @author Oliver Haggarty
 */
public class DynamicMarkingCompiler {

    /**
     * Template file location. This file is a template for creating the IDynamicMarking
     * and contains areas for changing the source/target
     */
    private String templateFile;

    /**
     * Full package path to the DynamicMarking class
     */
    private final static String CLASS_PATH = "pipe.models.DynamicMarking";

    /**
     * Creates correct string names for the various files needed
     */
    public DynamicMarkingCompiler() {
        StringBuilder sb = new StringBuilder();
        sb.append("./extras");
        sb.append(File.separator);
        sb.append("dynamicCode");
        sb.append(File.separator);
        sb.append("rta");
        sb.append(File.separator);
        sb.append("pipe");
        sb.append(File.separator);
        sb.append("models");
        sb.append(File.separator);
        String templateFolder = sb.toString();
        templateFile = templateFolder + "template.java";
    }

    /**
     * Returns an instance of the DynamicMarkingImpl file most recently compiled
     *
     * @return DynamicMarking
     */
    public IDynamicMarking getDynamicMarking(String source, String target) {
        String src = getSourceCode(source, target);
        return compileAndLoadSource(src, CLASS_PATH);
    }

    /**
     * Compile the sourceCode created by setLogicalExpression
     * @return
     */
    private IDynamicMarking compileAndLoadSource(String src, String fullName) {
        // We get an instance of JavaCompiler. Then
        // we create a file manager
        // (our custom implementation of it)
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));

        // Dynamic compiling requires specifying
        // a list of "files" to compile. In our case
        // this is a list containing one "file" which is in our case
        // our own implementation (see details below)
        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(fullName, src));

        List<String> optionList = new ArrayList<String>();

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();


        // We specify a task to the compiler
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, jfiles);
        boolean success = task.call();
        if (!success) {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getCode());
                System.out.println(diagnostic.getKind());
                System.out.println(diagnostic.getPosition());
                System.out.println(diagnostic.getStartPosition());
                System.out.println(diagnostic.getEndPosition());
                System.out.println(diagnostic.getSource());
                System.out.println(diagnostic.getMessage(null));
            }
            return null;
        }

        try {
            Object instance = fileManager.getClassLoader(null).loadClass(fullName).newInstance();
            return (IDynamicMarking) instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a copy of template.java called DynamicMarkingImpl.java with startExp
     * and targetExp added at the correct position in the code
     *
     * @param startExp  A logical expression describing a start state
     * @param targetExp A logical expression describing a target state
     */
    private String getSourceCode(String startExp, String targetExp) {
        //Add the logical expressions to an if statement
        String fullStartExp = addIf(startExp);
        String fullTargetExp = addIf(targetExp);

        //Connect to the template file and the output file
        URL url = getClass().getClassLoader().getResource(templateFile);
        File tempf = new File(url.getPath());
        BufferedReader tempFile;
        try {
            tempFile = new BufferedReader(new FileReader(tempf));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        StringBuilder classBuffer = new StringBuilder();
        String input;
        try {
            while ((input = tempFile.readLine()) != null) {
                if (input.equals("//#$#ADDTARGETEXPRESSIONHERE")) {
                    classBuffer.append(fullTargetExp);
                    classBuffer.append("\n");
                } else if (input.equals("//#$#ADDSTARTEXPRESSIONHERE")) {
                    classBuffer.append(fullStartExp);
                    classBuffer.append("\n");
                } else {
                    classBuffer.append(input);
                    classBuffer.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classBuffer.toString();
    }

    /**
     * @param logicalExp logical expression to be wrapped in an if statement
     * @return the logical expression wrapped in an if(logicExp) statement, that is in Java syntax
     */
    private String addIf(String logicalExp) {
        return "\tif(" + logicalExp + ") ";
    }

    public static class JavaClassObject extends SimpleJavaFileObject {

        /**
         * Byte code created by the compiler will be stored in this
         * ByteArrayOutputStream so that we can later get the
         * byte array out of it
         * and put it in the memory as an instance of our class.
         */
        protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

        /**
         * Registers the compiled class object under URI
         * containing the class full name
         *
         * @param name Full name of the compiled class
         * @param kind Kind of the data. It will be CLASS in our case
         */
        public JavaClassObject(String name, Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        }

        /**
         * Will be used by our file manager to get the byte code that
         * can be put into memory to instantiate our class
         *
         * @return compiled byte code
         */
        public byte[] getBytes() {
            return bos.toByteArray();
        }

        /**
         * Will provide the compiler with an output stream that leads
         * to our byte array. This way the compiler will write everything
         * into the byte array that we will instantiate later
         */
        @Override
        public OutputStream openOutputStream() throws IOException {
            return bos;
        }
    }

    public static class CharSequenceJavaFileObject extends SimpleJavaFileObject {

        /**
         * CharSequence representing the source code to be compiled
         */
        private CharSequence content;

        /**
         * This constructor will store the source code in the
         * internal "content" variable and register it as a
         * source code, using a URI containing the class full name
         *
         * @param className name of the public class in the source code
         * @param content   source code to compile
         */
        public CharSequenceJavaFileObject(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension),
                    JavaFileObject.Kind.SOURCE);
            this.content = content;
        }

        /**
         * Answers the CharSequence to be compiled. It will give
         * the source code stored in variable "content"
         */
        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    public static class ClassFileManager extends ForwardingJavaFileManager {
        /**
         * Instance of JavaClassObject that will store the
         * compiled bytecode of our class
         */
        private JavaClassObject jclassObject;

        /**
         * Will initialize the manager with the specified
         * standard java file manager
         *
         * @param standardManager
         */
        public ClassFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);
        }

        /**
         * Will be used by us to get the class loader for our
         * compiled class. It creates an anonymous class
         * extending the SecureClassLoader which uses the
         * byte code created by the compiler and stored in
         * the JavaClassObject, and returns the Class for it
         */
        @Override
        public ClassLoader getClassLoader(Location location) {
            return new SecureClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    byte[] b = jclassObject.getBytes();
                    return super.defineClass(name, jclassObject.getBytes(), 0, b.length);
                }
            };
        }

        /**
         * Gives the compiler an instance of the JavaClassObject
         * so that the compiler can write the byte code into it.
         */
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                                   FileObject sibling) throws IOException {
            jclassObject = new JavaClassObject(className, kind);
            return jclassObject;
        }
    }
}
