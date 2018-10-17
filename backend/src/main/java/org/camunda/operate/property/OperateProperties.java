package org.camunda.operate.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * This class contains all project configuration parameters.
 */
@Component
@ConfigurationProperties(OperateProperties.PREFIX)
public class OperateProperties {

  public static final String PREFIX = "camunda.bpm.operate";

  private boolean startLoadingDataOnStartup = true;

  private Long batchOperationMaxSize;

  @NestedConfigurationProperty
  private ElasticsearchProperties elasticsearch = new ElasticsearchProperties();

  //TODO not all properties make sense
  @NestedConfigurationProperty
  private ZeebeElasticsearchProperties zeebeElasticsearch = new ZeebeElasticsearchProperties();

  @NestedConfigurationProperty
  private ZeebeProperties zeebe = new ZeebeProperties();

  @NestedConfigurationProperty
  private OperationExecutorProperties operationExecutor = new OperationExecutorProperties();

  public boolean isStartLoadingDataOnStartup() {
    return startLoadingDataOnStartup;
  }

  public void setStartLoadingDataOnStartup(boolean startLoadingDataOnStartup) {
    this.startLoadingDataOnStartup = startLoadingDataOnStartup;
  }

  public Long getBatchOperationMaxSize() {
    return batchOperationMaxSize;
  }

  public void setBatchOperationMaxSize(Long batchOperationMaxSize) {
    this.batchOperationMaxSize = batchOperationMaxSize;
  }

  public ElasticsearchProperties getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchProperties elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

  public ZeebeElasticsearchProperties getZeebeElasticsearch() {
    return zeebeElasticsearch;
  }

  public void setZeebeElasticsearch(ZeebeElasticsearchProperties zeebeElasticsearch) {
    this.zeebeElasticsearch = zeebeElasticsearch;
  }

  public ZeebeProperties getZeebe() {
    return zeebe;
  }

  public void setZeebe(ZeebeProperties zeebe) {
    this.zeebe = zeebe;
  }

  public OperationExecutorProperties getOperationExecutor() {
    return operationExecutor;
  }

  public void setOperationExecutor(OperationExecutorProperties operationExecutor) {
    this.operationExecutor = operationExecutor;
  }
}
