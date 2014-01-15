package pipe.petrinet.adapters.valueAdapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntValueAdapter extends XmlAdapter<IntValueAdapter.IntAdapter, Integer> {
    @Override
    public Integer unmarshal(IntAdapter intAdapter) throws Exception {
        return intAdapter.value;
    }

    @Override
    public IntAdapter marshal(Integer integer) throws Exception {
        IntAdapter adapter = new IntAdapter();
        adapter.value = integer;
        return adapter;
    }

    public static class IntAdapter {
        public int value;
    }
}
