package com.lightbend.kafka.sparkeventexporter.internal
import com.lightbend.kafka.kafkametricstools.Domain.TopicPartition
import com.lightbend.kafka.kafkametricstools.MetricsSink.{GaugeDefinition, Message, Metric, MetricDefinitions}

object Metrics {
  sealed trait SparkOffsetMetric extends Message with Metric {
    def clusterName: String
    def sparkAppId: String
    def name: String
    def topicPartition: TopicPartition

    override def labels: List[String] =
      List(
        clusterName,
        name,
        topicPartition.topic,
        topicPartition.partition.toString
      )
  }

  final case class LatestOffsetMetric(clusterName: String, sparkAppId: String, name: String, topicPartition: TopicPartition, value: Double) extends SparkOffsetMetric
  final case class LastOffsetMetric(clusterName: String, sparkAppId: String, name: String, topicPartition: TopicPartition, value: Double) extends SparkOffsetMetric
  final case class OffsetLagMetric(clusterName: String, sparkAppId: String, name: String, topicPartition: TopicPartition, value: Double) extends SparkOffsetMetric
  final case class TimeLagMetric(clusterName: String, sparkAppId: String, name: String, topicPartition: TopicPartition, value: Double) extends SparkOffsetMetric

  sealed trait SparkThroughputMetric extends Message with Metric {
    def clusterName: String
    def sparkAppId: String
    def name: String
    def sourceTopics: String

    override def labels: List[String] =
      List(
        clusterName,
        name,
        sourceTopics
      )
  }

  final case class InputRecordsPerSecondMetric(clusterName: String, sparkAppId: String, name: String, sourceTopics: String, value: Double) extends SparkThroughputMetric
  final case class ProcessedRecordsPerSecondMetric(clusterName: String, sparkAppId: String, name: String, sourceTopics: String, value: Double) extends SparkThroughputMetric

  val metricDefinitions: MetricDefinitions = Map(
    classOf[LatestOffsetMetric] -> GaugeDefinition(
      "spark_kafka_partition_latest_offset",
      "Latest offset of a partition",
      "cluster_name", "provided_name", "topic", "partition"
    ),
    classOf[LastOffsetMetric] -> GaugeDefinition(
      "spark_kafka_last_offset",
      "Last consumed offset of a partition",
      "cluster_name", "provided_name", "topic", "partition"
    ),
    classOf[OffsetLagMetric] -> GaugeDefinition(
      "spark_kafka_last_offset_lag",
      "Last consumed offset lag of a partition",
      "cluster_name", "provided_name", "topic", "partition"
    ),
    classOf[TimeLagMetric] -> GaugeDefinition(
      "spark_kafka_last_offset_lag_seconds",
      "Last consumed offset time lag of a partition",
      "cluster_name", "provided_name", "topic", "partition"
    ),
    classOf[InputRecordsPerSecondMetric] -> GaugeDefinition(
      "spark_kafka_input_records_per_second",
      "Input records per second for a source",
      "cluster_name", "provided_name", "source_topics"
    ),
    classOf[ProcessedRecordsPerSecondMetric] -> GaugeDefinition(
      "spark_kafka_processed_records_per_second",
      "Processed records per second for a source",
      "cluster_name", "provided_name", "source_topics"
    )
  )
}
