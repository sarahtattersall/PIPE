package pipe.io.adapters.valueAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntValueAdapter extends XmlAdapter<IntValueAdapter.IntAdapter, Integer> {
    @Override
    public Integer unmarshal(IntAdapter intAdapter) {
        return intAdapter.value;
    }

    @Override
    public IntAdapter marshal(Integer integer) {
        IntAdapter adapter = new IntAdapter();
        adapter.value = integer;
        return adapter;
    }

    public static class IntAdapter {
        public int value;
    }
}
