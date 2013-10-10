package pipe.utilities.math;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * A writable comparable class for complex number. Used by Hadoop to represent
 * ComplexNumber numbers when used as the key for a mapreduce task
 *
 * @author Oliver Haggarty August 2007
 */
public class ComplexNumberWritable implements WritableComparable
{

    private double real, imaginary;

    public ComplexNumberWritable()
    {
    }

    public ComplexNumberWritable(final double real, final double imaginary)
    {
        this.set(real, imaginary);
    }

    public int compareTo(final Object o)
    {
        final double thisReal = this.real;
        final double thatReal = ((ComplexNumberWritable) o).real;
        final double thisImag = this.imaginary;
        final double thatImag = ((ComplexNumberWritable) o).imaginary;
        return thisReal < thatReal ? -1
                : thisReal == thatReal ? (thisImag < thatImag ? -1
                : thisImag == thatImag ? 0
                : 1)
                : 1;
    }

    @Override
    public boolean equals(final Object other)
    {
        if(!(other instanceof ComplexNumberWritable))
            return false;
        final ComplexNumberWritable complexNumberWritable = (ComplexNumberWritable) other;
        return this.real == complexNumberWritable.real && this.imaginary == complexNumberWritable.imaginary;
    }

    public ComplexNumber get()
    {
        return new ComplexNumber(this.real, this.imaginary);
    }

    @Override
    public int hashCode()
    {
        return (int) (this.real + this.imaginary);
    }

    public void readFields(final DataInput in) throws IOException
    {
        this.real = in.readDouble();
        this.imaginary = in.readDouble();
    }

    void set(final double real, final double imaginary)
    {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Override
    public String toString()
    {
        return "Re: " + Double.toString(this.real) + ", Im: " + Double.toString(this.imaginary);
    }

    public void write(final DataOutput out) throws IOException
    {
        out.writeDouble(this.real);
        out.writeDouble(this.imaginary);
    }
}