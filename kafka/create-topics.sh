#!/bin/bash

set -e

BOOTSTRAP_SERVERS="kafka-1:19092,kafka-2:19092,kafka-3:19092"

TOPICS=(
  "payments.lifecycle"
  "events.lifecycle"
)

echo "Waiting for Kafka..."

until /opt/kafka/bin/kafka-topics.sh \
    --bootstrap-server "$BOOTSTRAP_SERVERS" \
    --list > /dev/null 2>&1
do
    sleep 2
done

echo "Kafka is ready."

for TOPIC in "${TOPICS[@]}"; do
    /opt/kafka/bin/kafka-topics.sh \
        --bootstrap-server "$BOOTSTRAP_SERVERS" \
        --create \
        --if-not-exists \
        --topic "$TOPIC" \
        --partitions 3 \
        --replication-factor 3
done

echo "Kafka topics initialization completed."