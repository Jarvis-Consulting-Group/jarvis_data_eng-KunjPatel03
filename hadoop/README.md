# Table of Contents
* [Introduction](#Introduction)
* [Hadoop Cluster](#Hadoop-Cluster)
* [Hive Project](#Hive-Project)
* [Improvements](#Improvements)

# Introduction
This project centers around optimizing core Hadoop components—HDFS, MapReduce, and YARN—for processing and analyzing substantial datasets. The primary goal is to elevate the performance of these components, thereby unleashing the full potential of the Hadoop ecosystem in the realm of distributed data storage and processing. This project harnesses the power of Apache Hive and the interactive capabilities of the Zeppelin Notebook to address a real-world business challenge. By leveraging Google Cloud Platform's Data Proc function, we've engineered a Hadoop cluster capable of efficiently managing an extensive dataset containing over 21 million records. This cluster is composed of a meticulously designed master node and two proficient worker nodes, enabling Hive to execute powerful HQL statements for insightful data exploration.

## Technologies and Tools
- Apache Hadoop
- Google Cloud Platform
- Hive, YARN
- Zeppelin
- Git / GitHub

# Hadoop Cluster
The robust Hadoop cluster, hosted on the Google Cloud Platform, comprises one master node and two worker nodes.

![Cluster Architecture](assets/Architecture.jpeg)

- The Hadoop cluster is strategically set up on the Google Cloud Platform, housing a master node and two worker nodes, each meticulously configured.
- Hardware Specifications:
  - Master node: 2 vCPUs, 13GB RAM, 100GB disk
  - Worker nodes: 2 vCPUs, 13GB RAM, 100GB disk per node

## Big Data Tools
- **HDFS** (Hadoop Distributed File System): The bedrock of Hadoop, providing scalable, fault-tolerant storage for vast datasets.
- **NameNode**: The orchestrator of metadata, managing data block locations and accessibility.
- **DataNode**: Worker nodes responsible for storing and managing data blocks, contributing to data distribution.
- **Hive**: A versatile data warehousing tool, instrumental in ETL processes and data analysis.
  - **CLI (Beeline)**: Command-line interface facilitating interaction with Hive via JDBC, ensuring seamless data exploration.
  - **Zeppelin**: A dynamic, web-based notebook offering an interactive platform for collaborative data-driven insights.
- **YARN** (Yet Another Resource Negotiator): A pivotal element managing resources and job scheduling efficiently.
- **MapReduce**: A potent programming model for parallel data processing and analytics.

# Hive Project
The Hive project exemplifies the prowess of Hadoop in optimizing data processing and analysis.

![Project Overview](assets/Hive_project_notebook.png)

# Improvements
As we journey further in refining this project, we've identified pivotal areas for enhancement and growth:
1. **Extended Comparative Analysis**: Thoroughly examine varied columnar file formats and their performance against the Parquet format.
2. **Performance Benchmarking**: Rigorously evaluate the columnar format approach against the Spark approach, focusing on runtime efficiencies.
3. **Project Expansion**: Broaden the project's horizon by incorporating intricate data manipulations and in-depth comparisons.