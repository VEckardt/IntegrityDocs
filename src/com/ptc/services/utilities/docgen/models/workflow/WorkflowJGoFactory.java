package com.ptc.services.utilities.docgen.models.workflow;

/**
 * Factory for creating WorkflowJGoXXX objects that go into WorkflowJGoModel.
 */
public class WorkflowJGoFactory {

    /**
     * Create a state transition link, which could be a self transition.
     *
     * @param from The source port of the transition
     * @param to The target port of the transition
     * @return WorkflowJGoLink if the source and target ports are different.
     * WorkflowJGoSelfLoop if the source and target ports are equal.
     */
    public static WorkflowJGoLink createLink(WorkflowJGoPort from, WorkflowJGoPort to) {
        if (from.equals(to)) {
            return new WorkflowJGoSelfLoop(from.getNode());
        } else {
            return new WorkflowJGoLink(from, to);
        }
    }
}
