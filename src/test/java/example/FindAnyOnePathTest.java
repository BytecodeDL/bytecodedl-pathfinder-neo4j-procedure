package example;

import com.bytecodedl.pathfinder.FindAnyOnePath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FindAnyOnePathTest {

    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() throws IOException {

        var sw = new StringWriter();
        try (var in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/call_graph.cypher")))) {
            in.transferTo(sw);
            sw.flush();
        }

        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
            .withProcedure(FindAnyOnePath.class)
            .withFixture(sw.toString())
            .build();
    }

    @AfterAll
    void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }

    @Test
    void findAnyOnePath() {

        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
        ) {

            // language=cypher
            var records = session.run("match (start:Source)" +
                    "match (end: Sink) " +
                    "call bytecodedl.findOnePath(start, end, 10) yield path return path").list();

            for(Record record : records){
                //System.out.println(record.get("path"));
                printPath(record.get("path").asPath());
            }

        }
    }

    @Test
    void biFindOnePath() {

        try(
                var driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI());
                var session = driver.session()
        ) {

            // language=cypher
            var records = session.run("match (start:Source)" +
                    "match (end: Sink1) " +
                    "call bytecodedl.biFindOnePath(start, end, 10) yield path return path").list();

            for(Record record : records){
                //System.out.println(record.get("path"));
                printPath(record.get("path").asPath());
            }

        }
    }

    public void printPath(Path path){
        Node start = path.start();
        printNode(start);
        List<Node> nodeList = StreamSupport.stream(path.nodes().spliterator(), false).collect(Collectors.toList());
        List<Relationship> relationships = StreamSupport.stream(path.relationships().spliterator(), false).collect(Collectors.toList());
        int length = nodeList.size();
        for (int i = 0; i < length - 1; i++) {
            System.out.print("-" + relationships.get(i).get("insn")+ "->");
            printNode(nodeList.get(i+1));
        }
        System.out.println("");
    }

    public void printNode(Node node){
        System.out.print(node.get("method"));
    }
}
