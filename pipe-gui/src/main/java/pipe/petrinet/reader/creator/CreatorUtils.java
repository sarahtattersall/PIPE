package pipe.petrinet.reader.creator;

public class CreatorUtils {
    /**
     *
     * @param value
     * @return double value of value or 0 if the input is empty
     */
    public static double zeroOrValueOf(String value)
    {
        return value.isEmpty() ? 0 : Double.valueOf(value);
    }

    public static boolean falseOrValueOf(String timed) {
        return Boolean.parseBoolean(timed);
    }

    public static int zeroOrValueOfInt(String value) {
        return value.isEmpty() ? 0 : Integer.valueOf(value);
    }
}
