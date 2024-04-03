package openmal.domain

/**
 * User status regarding a [Work] in the user library.
 * @property mediaType Anime or Manga
 * @property currentStatus Watching, on hold, ...
 * @property score Score given by the user to this [Work]
 * @property progressCount How many episodes watched/ chapters read.
 * @property isRevisiting Whether the user is re-watching or re-reading the [Work]
 * @property startDate Date when the user started watching/reading.
 * @property finishDate Date when the user finished watching/reading.
 * @property priority Priority to read/watch this [Work]. Low, Mid, or High.
 * @property numRevisited How many times the [Work] is being revisited.
 * @property revisitValue How likely it is that you'll rewatch an anime / reread a manga in the future.
 * @property tags Comma separated list of tags.
 * @property comments Notes about the [Work]
 * @property updatedAt Last update date to this [Work] in the user library.
 */

data class UserListStatus (
    val mediaType: MediaType,
    val currentStatus: ListStatus = ListStatus.IN_PROGRESS,
    val score: Int = 0,
    val progressCount: Int = 0,
    val isRevisiting: Boolean = false,
    val startDate: String = "",
    val finishDate: String = "",
    val priority: Priority = Priority.LOW,
    val numRevisited: Int = 0,
    val revisitValue: Int = 0,
    val tags: List<String> = emptyList(),
    val comments: String = "",
    val updatedAt: String = "",
)
