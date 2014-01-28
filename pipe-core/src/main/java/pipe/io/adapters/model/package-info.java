/**
 * This package is for modelling the {@link pipe.models.component.PetriNetComponent}'s
 * for marshalling to XML. Each model should have a matching Adapted model in here.
 *
 * We have to do this because JAXB requires a default no-arg constructor.
 * The other option was to add default constructors to the petri net objects and set
 * all their attributes with getter/setters but it felt wrong since they require
 * some final fields. For example {@link pipe.models.component.place.Place} objects
 * should never be created without an id.
 */
package pipe.io.adapters.model;
