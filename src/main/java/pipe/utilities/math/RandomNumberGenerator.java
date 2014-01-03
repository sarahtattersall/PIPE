package pipe.utilities.math;

import java.util.Random;

public class RandomNumberGenerator {
    private static final Random RANDOM_GENERATOR = new Random();

    public static double getRandomNumber()
    {
        return RANDOM_GENERATOR.nextDouble();
    }
}
