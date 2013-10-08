package pipe.utilities.math;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleTupleWritable implements WritableComparable
{
    private double value1, value2;

    public DoubleTupleWritable()
    {
    }

    public DoubleTupleWritable(final double v1, final double v2)
    {
        this.set(v1, v2);
    }

    /**
     * Compares two DoubleWritables.
     */
    public int compareTo(final Object o)
    {
        final double thisValue1 = this.value1;
        final double thatValue1 = ((DoubleTupleWritable) o).value1;
        final double thisValue2 = this.value2;
        final double thatValue2 = ((DoubleTupleWritable) o).value2;
        return thisValue1 < thatValue1 ? -1
                : thisValue1 == thatValue1 ? (thisValue2 < thatValue2 ? -1
                : thisValue2 == thatValue2 ? 0
                : 1)
                : 1;
    }

    @Override
    public boolean equals(final Object o)
    {
        if(!(o instanceof DoubleTupleWritable))
        {
            return false;
        }
        final DoubleTupleWritable other = (DoubleTupleWritable) o;
        return this.value1 == other.value1 && this.value2 == other.value2;
    }

    public double get1()
    {
        return this.value1;
    }

    public double get2()
    {
        return this.value2;
    }

    @Override
    public int hashCode()
    {
        return ((int) this.value1 + (int) this.value2) / 2;
    }

    public void readFields(final DataInput in) throws IOException
    {
        this.value1 = in.readDouble();
        this.value2 = in.readDouble();
    }

    void set(final double v1, final double v2)
    {
        this.value1 = v1;
        this.value2 = v2;
    }

    @Override
    public String toString()
    {
        return Double.toString(this.value1) + ", " + Double.toString(this.value2);
    }

    /**
     * Writes the value of this DoubleWritable to a DataOutput stream
     */
    public void write(final DataOutput out) throws IOException
    {
        out.writeDouble(this.value1);
        out.writeDouble(this.value2);
    }

}
