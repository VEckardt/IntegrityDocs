package com.ptc.services.utilities.docgen.models.relationship;

/**
 * Factory for creating RelationshipJGoXXX objects that go into
 * RelationshipJGoModel.
 */
public class RelationshipJGoFactory {

    /**
     * Create a relationship link, which could be a self relationship.
     *
     * @param from The source port of the relationship
     * @param to The target port of the relationship
     * @param label The name of the label for this relationship
     * @return RelationshipJGoLink if the source and target ports are different.
     * RelationshipJGoSelfLoop if the source and target ports are equal.
     */
    public static RelationshipJGoLink createLink(RelationshipJGoPort from, RelationshipJGoPort to, String label) {
        if (from.equals(to)) {
            return new RelationshipJGoSelfLoop(from.getNode(), label);
        } else {
            return new RelationshipJGoLink(from, to, label);
        }
    }
}
