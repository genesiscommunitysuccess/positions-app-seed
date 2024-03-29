systemDefinition {
  global {
    item(name = "DbLayer", value = env["DB_LAYER", "FDB"])
    item(name = "DbHost", value = env["DB_HOST", "localhost"])
    item(name = "DEPLOYED_PRODUCT", value = "position")
    item(name = "MqLayer", value = env["MQ_LAYER", "ZeroMQ"])

    item(name = "HookStateStore", value = env["HOOK_STATE_STORE", "FILE"])
    item(name = "DictionarySource", value = "DB")
    item(name = "AliasSource", value = "DB")
    item(name = "MetricsEnabled", value = "false")
    item(name = "ZeroMQProxyInboundPort", value = "5001")
    item(name = "ZeroMQProxyOutboundPort", value = "5000")
    env["DB_USERNAME"].takeIf { it.isNotBlank() }?.let { item(name = "DbUsername", value = it) }
    env["DB_PASSWORD"].takeIf { it.isNotBlank() }?.let { item(name = "DbPassword", value = it) }
    env["DB_SQL_CONNECTION_POOL_SIZE"].takeIf { it.isNotBlank() }?.let { item(name = "DbSqlConnectionPoolSize", value = it) }
    item(name = "DbMode", value = "VANILLA")
    item(name = "GenesisNetProtocol", value = "V2")
    item(name = "ResourcePollerTimeout", value = "5")
    item(name = "ReqRepTimeout", value = "60")
    item(name = "MetadataChronicleMapAverageKeySizeBytes", value = "128")
    item(name = "MetadataChronicleMapAverageValueSizeBytes", value = "1024")
    item(name = "MetadataChronicleMapEntriesCount", value = "512")
    item(name = "DaemonServerPort", value = "4568")
    item(name = "DaemonHealthPort", value = "4569")
    item(
      name = "JVM_OPTIONS",
      value = "-XX:MaxHeapFreeRatio=70 -XX:MinHeapFreeRatio=30 -XX:+UseG1GC -XX:+UseStringDeduplication -XX:OnOutOfMemoryError=\"handleOutOfMemoryError.sh %p\""
    )

    item(name = "SYSTEM_DEFAULT_USER_NAME", value = "GenesisGlobal")
    item(name = "SYSTEM_DEFAULT_EMAIL", value = "notifications@genesis.global")
    item(name = "EMAIL_SMTP_HOST", value = "smtp.office365.com")
    item(name = "EMAIL_SMTP_PORT", value = "587")
    item(name = "EMAIL_SMTP_USER", value = "notifications@genesis.global")
    item(name = "EMAIL_SMTP_PW", env["SMTPPASSWORD", "foo"])
    item(name = "EMAIL_SMTP_PROTOCOL", value = "SMTP_TLS")

    item(name = "ADMIN_PERMISSION_ENTITY_TABLE", value = "COUNTERPARTY")
    item(name = "ADMIN_PERMISSION_ENTITY_FIELD", value = "COUNTERPARTY_ID")
    // item(name = "ClusterMode", value = "CONSUL")
    item(name = "ConsulNodeName", value = env["CONSUL_NODE_NAME"])
    item(name = "MqttBrokerUrl", value = env["MQTT_BROKER_URL"])
    item(name = "MqttQos", value = env["MQTT_QOS"])
    item(name = "DefaultKeystoreLocation", value = "/keystore.jks")
    item(name = "DefaultKeystorePassword", value = env["KEY_STORE_PASSWORD"])
    item(name = "DefaultCertificate", value = "/certificate.crt")
    item(name = "ConsulServiceNamePattern", value = "PETER_{{PROCESS_NAME}}")
  }

  systems {

    system(name = "DEV") {

      hosts {
        host(LOCAL_HOST)
      }

      item(name = "DbNamespace", value = env["DB_NAMESPACE", "genesis"])
      item(name = "ClusterPort", value = "6000")
      item(name = "location", value = "LO")
      item(name = "LogFramework", value = "LOG4J2")
      item(name = "LogFrameworkConfig", value = "log4j2-default.xml")
    }

  }

}
