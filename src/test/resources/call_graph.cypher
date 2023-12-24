CREATE (A:Source {method:'methoda'})
CREATE (B:Method {method:'methodb'})
CREATE (C0:Method {method:'methodc0'})
CREATE (C1:Method {method:'methodc1'})
CREATE (C2:Method {method:'methodc2'})
CREATE (D:Method {method:'methodd'})
CREATE (E:Method {method:'methode'})
CREATE (F:Method {method:'methodf'})
CREATE (S:Sink {method:'sink'})

CREATE (A)-[:Call {insn:'insnAB'}]->(B),
       (A)-[:Call {insn:'insnAC'}]->(C0),
       (A)-[:Call {insn:'insnAC'}]->(C1),
       (A)-[:Call {insn:'insnAC'}]->(C2),
       (C0)-[:Call {insn:'insnC0D'}]->(D),
       (C1)-[:Call {insn:'insnC1S'}]->(S),
       (C2)-[:Call {insn:'insnC2S'}]->(S),
       (D)-[:Call {insn:'insnDS'}]->(S),
       (B)-[:Call {insn:'insnBE'}]->(E),
       (E)-[:Call {insn:'insnEF'}]->(F),
       (F)-[:Call {insn:'insnFS'}]->(S)












CREATE (JoelS:Person {name:'Joel Silver', born:1952})
CREATE
  (Keanu)-[:ACTED_IN {roles:['Neo']}]->(TheMatrix),
  (Carrie)-[:ACTED_IN {roles:['Trinity']}]->(TheMatrix),
  (Laurence)-[:ACTED_IN {roles:['Morpheus']}]->(TheMatrix),
  (Hugo)-[:ACTED_IN {roles:['Agent Smith']}]->(TheMatrix),
  (LillyW)-[:DIRECTED]->(TheMatrix),
  (LanaW)-[:DIRECTED]->(TheMatrix),
  (JoelS)-[:PRODUCED]->(TheMatrix)