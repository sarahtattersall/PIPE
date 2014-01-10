//package pipe.models.component;
//
//import java.util.Collection;
//
///**
// * This interface is used for classes that are associated with inbound and outbound arcs
// */
//public interface HasArcs <S extends Connectable, T extends Connectable>{
//
//
//    public Collection<Arc<T, S>> outboundArcs();
//
//    public Collection<Arc<S, T>> inboundArcs();
//
//    public void addInbound(Arc<S, T> arc);
//
//    public void addOutbound(Arc<T, S> arc);
//
//    public void removeOutboundArc(Arc<T,S> arc);
//
//    public void removeInboundArc(Arc<S,T> arc);
//}
