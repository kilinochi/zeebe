/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.camunda.zeebe.topology.changes;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.atomix.cluster.MemberId;
import io.camunda.zeebe.scheduler.future.CompletableActorFuture;
import io.camunda.zeebe.test.util.asserts.EitherAssert;
import io.camunda.zeebe.topology.ClusterTopologyAssert;
import io.camunda.zeebe.topology.state.ClusterTopology;
import io.camunda.zeebe.topology.state.MemberState;
import io.camunda.zeebe.topology.state.PartitionState;
import io.camunda.zeebe.topology.state.PartitionState.State;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class PartitionLeaveApplierTest {

  private final PartitionTopologyChangeExecutor partitionTopologyChangeExecutor =
      mock(PartitionTopologyChangeExecutor.class);
  private final MemberId localMemberId = MemberId.from("1");
  final ClusterTopology initialClusterTopology =
      ClusterTopology.init().addMember(localMemberId, MemberState.initializeAsActive(Map.of()));

  final PartitionLeaveApplier partitionLeaveApplier =
      new PartitionLeaveApplier(1, localMemberId, partitionTopologyChangeExecutor);

  @Test
  void shouldRejectLeaveWhenPartitionDoesNotExist() {
    // when - then
    EitherAssert.assertThat(partitionLeaveApplier.init(initialClusterTopology))
        .isLeft()
        .left()
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldUpdateStateToLeavingOnInit() {
    // given
    final ClusterTopology topologyWithPartition =
        initialClusterTopology.updateMember(
            localMemberId, m -> m.addPartition(1, PartitionState.active(1)));

    // when
    final var resultingTopology =
        topologyWithPartition.updateMember(
            localMemberId, partitionLeaveApplier.init(topologyWithPartition).get());

    // then
    ClusterTopologyAssert.assertThatClusterTopology(resultingTopology)
        .member(localMemberId)
        .hasPartitionWithState(1, new PartitionState(State.LEAVING, 1));
  }

  @Test
  void shouldExecuteLeaveOnApply() {
    // given
    final var topologyWithPartition =
        initialClusterTopology.updateMember(
            localMemberId, m -> m.addPartition(1, PartitionState.active(1)));
    final var topologyAfterInit =
        topologyWithPartition.updateMember(
            localMemberId, partitionLeaveApplier.init(topologyWithPartition).get());

    when(partitionTopologyChangeExecutor.leave(1))
        .thenReturn(CompletableActorFuture.completed(null));

    // when
    final var stateUpdater = partitionLeaveApplier.apply().join();
    final var resultingTopology = topologyAfterInit.updateMember(localMemberId, stateUpdater);

    // then
    verify(partitionTopologyChangeExecutor).leave(1);
    ClusterTopologyAssert.assertThatClusterTopology(resultingTopology)
        .member(localMemberId)
        .doesNotContainPartition(1);
  }
}
