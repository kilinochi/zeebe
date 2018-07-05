package org.camunda.operate.es.types;

import java.io.IOException;
import org.camunda.operate.property.OperateProperties;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventType extends StrictTypeMappingCreator {

  public static final String ID = "id";

  public static final String WORKFLOW_ID = "workflowId";
  public static final String WORKFLOW_INSTANCE_ID = "workflowInstanceId";
  public static final String BPMN_PROCESS_ID = "bpmnProcessId";

  public static final String ACTIVITY_ID = "activityId";
  public static final String ACTIVITY_INSTANCE_ID = "activityInstanceId";

  public static final String EVENT_SOURCE_TYPE = "eventSourceType";
  public static final String EVENT_TYPE = "eventType";
  public static final String DATE_TIME = "dateTime";
  public static final String PAYLOAD = "payload";

  public static final String JOB_TYPE = "jobType";
  public static final String JOB_RETRIES = "jobRetries";
  public static final String JOB_WORKER = "jobWorker";

  public static final String INCIDENT_ERROR_TYPE = "incidentErrorType";
  public static final String INCIDENT_ERROR_MSG = "incidentErrorMessage";

  @Autowired
  private OperateProperties operateProperties;

  @Override
  public String getType() {
    return operateProperties.getElasticsearch().getEventIndexName();
  }

  @Override
  protected XContentBuilder addProperties(XContentBuilder builder) throws IOException {
    XContentBuilder newBuilder =  builder
      .startObject(ID)
        .field("type", "keyword")
      .endObject()
      .startObject(WORKFLOW_ID)
        .field("type", "keyword")
      .endObject()
      .startObject(WORKFLOW_INSTANCE_ID)
        .field("type", "keyword")
      .endObject()
      .startObject(BPMN_PROCESS_ID)
        .field("type", "keyword")
      .endObject()
      .startObject(ACTIVITY_ID)
        .field("type", "keyword")
      .endObject()
      .startObject(ACTIVITY_INSTANCE_ID)
        .field("type", "keyword")
      .endObject()
      .startObject(EVENT_SOURCE_TYPE)
        .field("type", "keyword")
      .endObject()
      .startObject(EVENT_TYPE)
        .field("type", "keyword")
      .endObject()
      .startObject(DATE_TIME)
        .field("type", "date")
        .field("format", operateProperties.getElasticsearch().getDateFormat())
      .endObject()
      .startObject(PAYLOAD)
        .field("type", "keyword")   //TODO may be we should use Text data type here?
      .endObject()
      .startObject(JOB_RETRIES)
        .field("type", "integer")
      .endObject()
      .startObject(JOB_TYPE)
        .field("type", "keyword")
      .endObject()
      .startObject(JOB_WORKER)
        .field("type", "keyword")
      .endObject()
      .startObject(INCIDENT_ERROR_TYPE)
        .field("type", "keyword")
      .endObject()
      .startObject(INCIDENT_ERROR_MSG)
        .field("type", "keyword")
      .endObject()
      ;
    return newBuilder;
  }

}
