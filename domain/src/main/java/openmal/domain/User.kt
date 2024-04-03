package openmal.domain

/**
 * Represents a user profile.
 *
 * @property id The unique identifier of the user.
 * @property name The name of the user.
 * @property pictureURL The URL of the user's profile picture.
 * @property gender The gender of the user.
 * @property birthday The birthday of the user.
 * @property location The location of the user.
 * @property joinDate The date when the user joined.
 * @property numItems The total number of anime titles in the user's list.
 * @property numEpisodes The total number of episodes watched by the user.
 * @property numDays The total cumulative anime watch time.
 * @property animeStats Statistics concerning anime episodes, for example Map<"watching", Number of titles in the watching list>
 * @property dayStats Statistics concerning time cumulative in each list, for example Map<Watching, Cumulative time of items in watching list, in unit of days>
 * @property reWatched The total number of titles re-watched by the user.
 * @property meanScore The mean score given by the user.
 */
data class User(
    val id: Int,
    val name: String,
    val pictureURL: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val location: String? = null,
    val joinDate: String? = null,
    val numItems: Int = 0,
    val numEpisodes: Int = 0,
    val numDays: Float = 0f,
    val animeStats: Map<ListStatus, Int> = mutableMapOf(),
    val dayStats: Map<ListStatus, Float> = mutableMapOf(),
    val reWatched: Int = 0,
    val meanScore: Float = 0f,
)
