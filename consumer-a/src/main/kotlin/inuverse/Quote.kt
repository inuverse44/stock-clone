package inuverse


import io.quarkus.runtime.annotations.RegisterForReflection



/**
 * テスト用モデル
 * RabbitMQの疎通ができることを確認できればいい
 * Kotlinは引数なしコンストラクタが生成できないのでデフォルト値で代用している
 * DTOでprivateを使うメリットはほぼない。可視性を担保する。
 * valでimmutabilityは保持しておく
 * Data Classは自動でtoStringメソッドが自動生成される
 * https://ja.quarkus.io/guides/rabbitmq#the-quote-object
 */
@RegisterForReflection
data class Quote(
    val id: String = "",
    val price: Int = 0
)