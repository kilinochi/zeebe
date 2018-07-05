package org.camunda.operate.zeebe;

import java.util.HashSet;
import java.util.Set;
import org.camunda.operate.entities.EventEntity;
import org.camunda.operate.entities.IncidentEntity;
import org.camunda.operate.es.writer.EntityStorage;
import org.camunda.operate.util.ZeebeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.zeebe.client.api.events.IncidentEvent;
import io.zeebe.client.api.events.IncidentState;
import io.zeebe.client.api.subscription.IncidentEventHandler;


@Component
public class IncidentEventTransformer extends AbstractEventTransformer implements IncidentEventHandler {

  private Logger logger = LoggerFactory.getLogger(WorkflowInstanceEventTransformer.class);

  private final static Set<IncidentState> EVENTS = new HashSet<>();

  static {
    EVENTS.add(IncidentState.CREATED);
    EVENTS.add(IncidentState.RESOLVED);
    EVENTS.add(IncidentState.DELETED);
  }

  @Autowired
  private EntityStorage entityStorage;

  @Override
  public void onIncidentEvent(IncidentEvent event) throws Exception {

    ZeebeUtil.ALL_EVENTS_LOGGER.debug(event.toJson());

    convertEvent(event);

    if (EVENTS.contains(event.getState())) {

      logger.debug(event.toJson());

      IncidentEntity incidentEntity = new IncidentEntity();

      incidentEntity.setId(String.valueOf(event.getMetadata().getKey()));
      incidentEntity.setErrorType(event.getErrorType());
      incidentEntity.setErrorMessage(event.getErrorMessage());
      incidentEntity.setActivityId(event.getActivityId());
      if (event.getActivityInstanceKey() != null) {
        incidentEntity.setActivityInstanceId(String.valueOf(event.getActivityInstanceKey()));
      }
      if (event.getJobKey() != null) {
        incidentEntity.setJobId(String.valueOf(event.getJobKey()));
      }
      if (event.getWorkflowInstanceKey() != null) {
        incidentEntity.setWorkflowInstanceId(String.valueOf(event.getWorkflowInstanceKey()));
      }
//      incidentEntity.setProcessDefinitionKey(event.getBpmnProcessId());

      org.camunda.operate.entities.IncidentState incidentState = org.camunda.operate.entities.IncidentState.fromZeebeIncidentState(event.getState());

      incidentEntity.setState(incidentState);

      updateMetadataFields(incidentEntity, event);

      //TODO will wait till capacity available, can throw InterruptedException
      entityStorage.getOperateEntititesQueue(event.getMetadata().getTopicName()).put(incidentEntity);
    }
  }

  private void convertEvent(IncidentEvent event) throws InterruptedException {
    //we will store sequence flows separately, no need to store them in events
    if (!event.getState().equals(io.zeebe.client.api.events.WorkflowInstanceState.SEQUENCE_FLOW_TAKEN)) {
      EventEntity eventEntity = new EventEntity();
      loadEventGeneralData(event, eventEntity);
      eventEntity.setWorkflowInstanceId(String.valueOf(event.getWorkflowInstanceKey()));
      eventEntity.setBpmnProcessId(event.getBpmnProcessId());
      eventEntity.setActivityId(event.getActivityId());
      eventEntity.setActivityInstanceId(String.valueOf(event.getActivityInstanceKey()));
      eventEntity.setIncidentErrorType(event.getErrorType());
      eventEntity.setIncidentErrorMessage(event.getErrorMessage());

      //TODO will wait till capacity available, can throw InterruptedException
      entityStorage.getOperateEntititesQueue(event.getMetadata().getTopicName()).put(eventEntity);
    }
  }
}
