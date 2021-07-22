package edu.ucr.cs.riple.autofixer.metadata;

import java.util.List;
import java.util.stream.Collectors;

public class CallGraph extends AbstractRelation<CallGraphNode> {

  public CallGraph(String filePath) {
    super(filePath);
  }

  @Override
  protected CallGraphNode addNodeByLine(String[] values) {
    return new CallGraphNode(values[0], values[1], values[2]);
  }

  public List<String> getUserClassesOfMethod(String method, String inClass) {
    List<CallGraphNode> nodes =
        findAllNodes(
            candidate ->
                candidate.calleeClass.equals(inClass) && candidate.calleeMethod.equals(method),
            method,
            inClass);
    return nodes
        .stream()
        .map(callGraphNode -> callGraphNode.callerClass)
        .collect(Collectors.toList());
  }
}
