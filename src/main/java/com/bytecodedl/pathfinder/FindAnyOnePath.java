package com.bytecodedl.pathfinder;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.util.PathImpl;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.nio.DoubleBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author daozhe@alibaba-inc.com
 * @date 2023/12/16 16:20
 */
public class FindAnyOnePath {
    @Context
    public Transaction tx;

    @Context
    public Log log;


    @Procedure(name = "bytecodedl.findOnePath", mode = Mode.READ)
    @Description("find one path from start to end under maxlength, also show first multi dispatch")
    public Stream<PathRecord> findOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name("callProperty") String callProperty){
        final Traverser traverse = tx.traversalDescription()
                .breadthFirst()
                .evaluator(Evaluators.toDepth((int)maxLength))
                .expand(PathExpanders.forTypeAndDirection(RelationshipType.withName("Call"), Direction.OUTGOING ))
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .traverse(start);

        Optional<Path> optionalPath = StreamSupport
                .stream(traverse.spliterator(), false).findFirst();

        if (optionalPath.isPresent()){
            Path path = optionalPath.get();
            List<Relationship> multiDispatchRelationship = getFirstMultiDispatch(path, callProperty);
            List<Path> pathList = multiDispatchRelationship.stream().map(this::relationShipToPath).collect(Collectors.toList());
            pathList.add(path);
            return pathList.stream().map(PathRecord::new);
        }else {
            return StreamSupport
                    .stream(traverse.spliterator(), false).map(PathRecord::new);
        }
    }

    @Procedure(name = "bytecodedl.biFindOnePath", mode = Mode.READ)
    @Description("find one path from start to end between minlength and maxlength, also show first multi dispatch")
    public Stream<PathRecord> biFindOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name("callProperty") String callProperty){
        TraversalDescription base = tx.traversalDescription().depthFirst().uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
        PathExpander expander = PathExpanders.forTypeAndDirection(RelationshipType.withName("Call"), Direction.OUTGOING );
        int maxDepth = (int) maxLength;

        final Traverser traverse = tx.bidirectionalTraversalDescription()
                .startSide(
                        base.expand(expander)
                                .evaluator(Evaluators.toDepth(maxDepth / 2))
                ).endSide(
                        base.expand(expander.reverse())
                                .evaluator(Evaluators.toDepth(maxDepth - maxDepth / 2))
                )
                .traverse(start, end);

        Optional<Path> optionalPath = StreamSupport
                .stream(traverse.spliterator(), false).findFirst();

        if (optionalPath.isPresent()){
            Path path = optionalPath.get();
//            System.out.println(path);
            List<Relationship> multiDispatchRelationship = getFirstMultiDispatch(path, callProperty);
            List<Path> pathList = multiDispatchRelationship.stream().map(this::relationShipToPath).collect(Collectors.toList());
            pathList.add(path);
            return pathList.stream().map(PathRecord::new);
        }else {
            return StreamSupport
                    .stream(traverse.spliterator(), false).map(PathRecord::new);
        }
    }

    public Path relationShipToPath(Relationship relationship){
        Node startNode = relationship.getStartNode();
        Path singleRelPath = new PathImpl.Builder(startNode).push(relationship).build();
        return singleRelPath;
    }

    // find first relationship of multi dispatch in path
    public List<Relationship> getFirstMultiDispatch(Path path, String callProperty){
        for (Relationship relationship : path.relationships()){
            Node startNode = relationship.getStartNode();
            if (!relationship.hasProperty(callProperty)){
                continue;
            }
            Object property = relationship.getProperty(callProperty);
            List<Relationship> relationships = startNode.getRelationships(relationship.getType()).stream().filter(
                    relation ->relation.hasProperty(callProperty) && relation.getProperty(callProperty).equals(property)
            ).collect(Collectors.toList());

            if (relationships.size() > 1){
                return relationships;
            }
        }

        return Collections.emptyList();
    }

    public static final class PathRecord {

        public final Path path;

        PathRecord(Path path) {
            this.path = path;
        }
    }

}
