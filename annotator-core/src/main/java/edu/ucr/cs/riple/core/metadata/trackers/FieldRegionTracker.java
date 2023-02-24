/*
 * MIT License
 *
 * Copyright (c) 2020 Nima Karimipour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.ucr.cs.riple.core.metadata.trackers;

import edu.ucr.cs.riple.core.Config;
import edu.ucr.cs.riple.core.ModuleInfo;
import edu.ucr.cs.riple.core.metadata.MetaData;
import edu.ucr.cs.riple.core.metadata.field.FieldDeclarationStore;
import edu.ucr.cs.riple.core.metadata.method.MethodDeclarationTree;
import edu.ucr.cs.riple.injector.location.Location;
import edu.ucr.cs.riple.injector.location.OnField;
import edu.ucr.cs.riple.scanner.Serializer;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Tracker for Fields. */
public class FieldRegionTracker extends MetaData<TrackerNode> implements RegionTracker {

  /**
   * Store for field declarations. This is used to determine if a field is initialized at
   * declaration.
   */
  private final FieldDeclarationStore fieldDeclarationStore;
  /** The method declaration tree. Used to retrieve constructors for a class */
  private final MethodDeclarationTree methodDeclarationTree;

  public FieldRegionTracker(
      Config config,
      ModuleInfo info,
      FieldDeclarationStore fieldDeclarationStore,
      MethodDeclarationTree methodDeclarationTree) {
    super(config, info.dir.resolve(Serializer.FIELD_GRAPH_FILE_NAME));
    this.fieldDeclarationStore = fieldDeclarationStore;
    this.methodDeclarationTree = methodDeclarationTree;
  }

  @Override
  protected TrackerNode addNodeByLine(String[] values) {
    return config.getAdapter().deserializeTrackerNode(values);
  }

  @Override
  public Optional<Set<Region>> getRegions(Location location) {
    if (!location.isOnField()) {
      return Optional.empty();
    }
    OnField field = location.toField();
    // Add all regions where the field is assigned a new value or read.
    Set<Region> ans =
        findNodesWithHashHint(
                candidate ->
                    candidate.calleeClass.equals(field.clazz)
                        && field.isOnFieldWithName(candidate.calleeMember),
                TrackerNode.hash(field.clazz))
            .map(trackerNode -> trackerNode.region)
            .collect(Collectors.toSet());
    ans.addAll(config.getAdapter().getFieldRegionScope(field));
    // Check if field is initialized at declaration.
    if (fieldDeclarationStore.isUninitializedField(field)) {
      // If not, add all constructors for the class.
      ans.addAll(
          methodDeclarationTree.getConstructorsForClass(field.clazz).stream()
              .map(onMethod -> new Region(onMethod.clazz, onMethod.method))
              .collect(Collectors.toSet()));
    }
    return Optional.of(ans);
  }
}
