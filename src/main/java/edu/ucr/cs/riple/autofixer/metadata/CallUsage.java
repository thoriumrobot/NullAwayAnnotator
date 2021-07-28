package edu.ucr.cs.riple.autofixer.metadata;

import edu.ucr.cs.riple.injector.Fix;
import java.util.List;
import java.util.stream.Collectors;

public class CallUsage extends AbstractRelation<CallNode> implements UsageTracker {

  public CallUsage(String filePath) {
    super(filePath);
  }

  @Override
  protected CallNode addNodeByLine(String[] values) {
    return new CallNode(values[0], values[1], values[2], values[3]);
  }

  public List<String> getUserClassesOfMethod(String method, String inClass) {
    List<CallNode> nodes =
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

  @Override
  public List<Usage> getUsage(Fix fix) {
    List<CallNode> nodes =
        findAllNodes(
            candidate ->
                candidate.calleeClass.equals(fix.className)
                    && candidate.calleeMethod.equals(fix.method),
            fix.className,
            fix.method);
    return nodes
        .stream()
        .map(callUsageNode -> new Usage(callUsageNode.callerMethod, callUsageNode.callerClass))
        .collect(Collectors.toList());
  }
}
