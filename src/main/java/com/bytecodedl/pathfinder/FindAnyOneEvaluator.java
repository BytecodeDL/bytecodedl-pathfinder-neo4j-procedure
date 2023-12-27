package com.bytecodedl.pathfinder;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;


/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/16 16:40
 */
public class FindAnyOneEvaluator implements Evaluator {
    private Node endNode;
    private long maxLength;

    public FindAnyOneEvaluator(Node endNode, long maxLength){
        this.endNode = endNode;
        this.maxLength = maxLength;
    }

    @Override
    public Evaluation evaluate(Path path) {
        if (path.length() > this.maxLength){
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        Node pathEndNode = path.endNode();
        if (pathEndNode.equals(endNode)){
            return Evaluation.INCLUDE_AND_PRUNE;
        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }
}

