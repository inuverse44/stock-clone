# Semantic Mapping: GCP Architecture ⇄ OSS Simplified Clone

本ドキュメントは、GKE 上で稼働する本番マイクロサービス構成を、
個人環境・OSS のみで意味論レベルで縮退クローンするための対応表である。

本対応は API 互換性ではなく、以下を重視する：

* データ不変性
* イベント駆動性
* at-least-once 配信
* Backpressure の伝播
* ボトルネック再現性

---

## 1. コンポーネント対応

| 本番 (GCP)                   | OSS クローン                | 役割                         | 備考               |
| -------------------------- | ----------------------- | -------------------------- | ---------------- |
| Google Cloud Storage (GCS) | MinIO                   | 不変オブジェクトストレージ              | S3互換だが意味論はGCSと同型 |
| Object Finalize Event      | ObjectCreated Event     | イベント発火                     | 「データが置かれた事実」を通知  |
| Cloud Pub/Sub Topic        | NATS JetStream Stream   | イベント保持層                    | 永続化あり            |
| Cloud Pub/Sub Subscription | NATS JetStream Consumer | イベント消費                     | Pull + ACK       |
| GKE Consumer (Service A)   | Consumer A (Kotlin)     | Download / Split / Enqueue | IO + CPU 混合      |
| RabbitMQ                   | RabbitMQ                | 下流ワークキュー                   | 本番同一構成           |
| Downstream Consumers       | Consumer B              | 並列ワーカー                     | スケール対象           |

---

## 2. ストレージ（GCS ⇄ MinIO）の意味論対応

| 観点               | GCS            | MinIO          |
| ---------------- | -------------- | -------------- |
| データモデル           | Object Storage | Object Storage |
| オブジェクト不変性        | 不変             | 不変             |
| 書き込み             | Write-once     | Write-once     |
| 読み込み             | Read-many      | Read-many      |
| 大容量対応            | 可能             | 可能             |
| Range GET        | 可能             | 可能             |
| Multipart Upload | 可能             | 可能             |
| IO特性             | ネットワークIO       | ネットワークIO       |

---

## 3. イベントバス（Pub/Sub ⇄ JetStream）の意味論対応

| 観点           | Cloud Pub/Sub        | NATS JetStream |
| ------------ | -------------------- | -------------- |
| 抽象モデル        | Event Bus            | Event Bus      |
| 配信保証         | at-least-once        | at-least-once  |
| ACK方式        | 明示ACK                | 明示ACK          |
| ACK期限        | Ack deadline         | AckWait        |
| 再送           | 自動                   | MaxDeliver     |
| Pull / Push  | 両対応                  | 両対応            |
| Backlog      | Subscription backlog | Stream storage |
| Consumer障害耐性 | あり                   | あり             |

---

## 4. Consumer の責務と失敗モデル

| 観点       | 本番           | OSS クローン       |
| -------- | ------------ | -------------- |
| イベント受信   | Pub/Sub Pull | JetStream Pull |
| データ取得    | GCS GET      | MinIO GET      |
| 処理単位     | オブジェクト単位     | オブジェクト単位       |
| 分割処理     | アプリ実装        | アプリ実装          |
| ACKタイミング | 全処理成功後       | 全処理成功後         |
| 途中失敗     | ACKしない       | ACKしない         |
| 再処理      | 自動           | 自動             |

---

## 5. Backpressure とボトルネック伝播

| 層               | 本番での挙動          | クローンでの再現          |
| --------------- | --------------- | ----------------- |
| RabbitMQ 詰まり    | Queue depth 増加  | Queue depth 増加    |
| Consumer 停滞     | ACK遅延           | ACK遅延             |
| Pub/Sub backlog | 未ACKメッセージ増加     | Stream backlog 増加 |
| 再送発生            | Ack deadline 超過 | AckWait 超過        |
| 上流停止            | Pull抑制          | Pull抑制            |

---

## 6. 非対応・意図的に省略している点

| 項目                   | 理由        |
| -------------------- | --------- |
| GCS IAM              | 個人検証では不要  |
| Pub/Sub Exactly-once | 実運用でも限定的  |
| GKE Autoscaling      | 構造理解が目的   |
| マルチリージョン             | ボトルネック検証外 |

---

## 7. 結論

本 OSS 構成（MinIO + NATS JetStream + RabbitMQ）は、

* GCS / Cloud Pub/Sub の **意味論を保持**
* コストゼロで
* ボトルネック・再送・Backpressure を再現可能

な **検証用縮退クローン**として妥当である。
