package com.bytecodedl.pathfinder;

import org.neo4j.cypher.internal.runtime.Expander;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.internal.helpers.collection.Iterables;

/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/23 13:53
 */
public class FindAnyOneExpander implements PathExpander<Double> {
    @Override
    public ResourceIterable<Relationship> expand(Path path, BranchState<Double> branchState) {
        return Iterables.emptyResourceIterable();
    }

    @Override
    public PathExpander<Double> reverse() {
        return null;
    }
}
