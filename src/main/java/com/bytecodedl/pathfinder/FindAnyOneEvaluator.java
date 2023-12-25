package com.bytecodedl.pathfinder;

import org.neo4j.cypher.internal.expressions.False;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.PathEvaluator;
import org.neo4j.logging.Log;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/16 16:40
 */
public class FindAnyOneEvaluator extends PathEvaluator.Adapter {
    private boolean hasFound;
    private Node endNode;
    private int maxLength;
    private int minLength;

    private Log log;

    public FindAnyOneEvaluator(Node endNode, String maxLength, String minLength, Log log){
        this.hasFound = false;
        this.endNode = endNode;
        this.maxLength = Integer.parseInt(maxLength);
        this.minLength = Integer.parseInt(minLength);
        this.log = log;
    }

    @Override
    public Evaluation evaluate(Path path, BranchState state) {
        //String p = StreamSupport.stream(path.nodes().spliterator(), false).map(node -> (String)(node.getProperty("method"))).collect(Collectors.joining("->"));
        //System.out.println(p);
        this.log.info(path.toString());
        this.log.info("evaluate path length " + path.length());

        if (hasFound){
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        if (path.length() > this.maxLength){
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        Node pathEndNode = path.endNode();
        if ((path.length() >= this.minLength) &&(pathEndNode.equals(endNode))){
            this.hasFound = true;
            state.setState(true);
            this.log.info("found path " + path);
            //System.out.println("INCLUDE_AND_PRUNE");
            return Evaluation.INCLUDE_AND_PRUNE;
        }

        //System.out.println("EXCLUDE_AND_CONTINUE");
        return Evaluation.EXCLUDE_AND_CONTINUE;
    }
}
