package com.bytecodedl.pathfinder;

import org.neo4j.cypher.internal.runtime.Expander;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterable;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.internal.helpers.collection.Iterables;
import org.neo4j.logging.Log;

/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/23 13:53
 */
public class FindAnyOneExpander implements PathExpander<Boolean> {
    private PathExpander expander;
    private Log log;
    public FindAnyOneExpander(PathExpander expander, Log log){
        this.expander = expander;
        this.log = log;
    }
    @Override
    public ResourceIterable<Relationship> expand(Path path, BranchState<Boolean> branchState) {
        this.log.info("expand path length " + path.length());

        if (!branchState.getState()){
            return this.expander.expand(path, branchState);
        }
        return Iterables.emptyResourceIterable();
    }

    @Override
    public PathExpander<Boolean> reverse() {
        return new FindAnyOneExpander(this.expander.reverse(), this.log);
    }
}
