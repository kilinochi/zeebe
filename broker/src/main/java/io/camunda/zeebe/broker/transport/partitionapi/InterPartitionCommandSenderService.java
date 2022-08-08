/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.1. You may not use this file
 * except in compliance with the Zeebe Community License 1.1.
 */
package io.camunda.zeebe.broker.transport.partitionapi;

import io.atomix.cluster.messaging.ClusterCommunicationService;
import io.camunda.zeebe.backup.api.CheckpointListener;
import io.camunda.zeebe.broker.partitioning.topology.TopologyPartitionListener;
import io.camunda.zeebe.engine.api.InterPartitionCommandSender;
import io.camunda.zeebe.protocol.impl.encoding.BrokerInfo;
import io.camunda.zeebe.protocol.record.ValueType;
import io.camunda.zeebe.protocol.record.intent.Intent;
import io.camunda.zeebe.scheduler.Actor;
import io.camunda.zeebe.util.buffer.BufferWriter;

public class InterPartitionCommandSenderService extends Actor
    implements InterPartitionCommandSender, CheckpointListener, TopologyPartitionListener {

  final InterPartitionCommandSenderImpl commandSender;
  final int partitionId;

  public InterPartitionCommandSenderService(
      final ClusterCommunicationService communicationService, final int partitionId) {
    commandSender = new InterPartitionCommandSenderImpl(communicationService);
    this.partitionId = partitionId;
  }

  @Override
  public void onNewCheckpointCreated(final long checkpointId) {
    actor.submit(() -> commandSender.setCheckpointId(checkpointId));
  }

  @Override
  public void sendCommand(
      final int receiverPartitionId,
      final ValueType valueType,
      final Intent intent,
      final BufferWriter command) {
    actor.submit(() -> commandSender.sendCommand(receiverPartitionId, valueType, intent, command));
  }

  @Override
  public void sendCommand(
      final int receiverPartitionId,
      final ValueType valueType,
      final Intent intent,
      final Long recordKey,
      final BufferWriter command) {
    actor.submit(
        () ->
            commandSender.sendCommand(receiverPartitionId, valueType, intent, recordKey, command));
  }

  @Override
  public void onPartitionLeaderUpdated(final int partitionId, final BrokerInfo member) {
    actor.submit(() -> commandSender.setCurrentLeader(partitionId, member.getNodeId()));
  }
}
