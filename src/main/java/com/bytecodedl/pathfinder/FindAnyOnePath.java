package com.bytecodedl.pathfinder;

import org.neo4j.graphalgo.impl.util.PathImpl;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.logging.Log;
import org.neo4j.procedure.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    public Stream<PathRecord> findOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name(value = "relationshipType", defaultValue = "Call") String relationType, @Name(value = "callProperty", defaultValue = "insn") String callProperty){
        final Traverser traverser = tx.traversalDescription()
                .breadthFirst()
                .evaluator(new FindAnyOneEvaluator(end, maxLength))
                .expand(
                        PathExpanderBuilder.empty()
                                .add(RelationshipType.withName(relationType), Direction.OUTGOING)
                                // filter non delete relation
                                .addRelationshipFilter(relationship -> Objects.equals(relationship.getProperty("is_deleted", 0), 0)).build())
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .traverse(start);

        return returnWithFirstMultiDispatch(traverser, callProperty);
    }

    @Procedure(name = "bytecodedl.biFindOnePath", mode = Mode.READ)
    @Description("find one path from start to end between minlength and maxlength, also show first multi dispatch")
    public Stream<PathRecord> biFindOnePath(@Name("start") Node start, @Name("end") Node end, @Name("maxLength") long maxLength, @Name(value = "relationshipType", defaultValue = "Call") String relationType, @Name(value = "callProperty", defaultValue = "insn") String callProperty){
        TraversalDescription base = tx.traversalDescription().depthFirst().uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);
        PathExpander expander = PathExpanderBuilder.empty().add(RelationshipType.withName(relationType), Direction.OUTGOING).addRelationshipFilter(relationship -> Objects.equals(relationship.getProperty("is_deleted", 0), 0)).build();
        int maxDepth = (int) maxLength;

        final Traverser traverser = tx.bidirectionalTraversalDescription()
                .startSide(
                        base.expand(expander)
                                .evaluator(Evaluators.toDepth(maxDepth / 2))
                ).endSide(
                        base.expand(expander.reverse())
                                .evaluator(Evaluators.toDepth(maxDepth - maxDepth / 2))
                )
                .traverse(start, end);

        return returnWithFirstMultiDispatch(traverser, callProperty);

    }

    public Stream<PathRecord> returnWithFirstMultiDispatch(Traverser traverser, String callProperty){
        Optional<Path> optionalPath = StreamSupport
                .stream(traverser.spliterator(), false).findFirst();

        if (optionalPath.isPresent()){
            Path path = optionalPath.get();
            List<Relationship> multiDispatchRelationship = getFirstMultiDispatch(path, callProperty);
            List<Path> pathList = multiDispatchRelationship.stream().map(this::relationShipToPath).collect(Collectors.toList());
            pathList.add(path);
            return pathList.stream().map(PathRecord::new);
        }else {
            return StreamSupport
                    .stream(traverser.spliterator(), false).map(PathRecord::new);
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
                    relation ->relation.hasProperty(callProperty) && relation.getProperty(callProperty).equals(property) && Objects.equals(relation.getProperty("is_deleted", 0), 0)
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
