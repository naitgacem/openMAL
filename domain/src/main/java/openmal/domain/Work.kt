package openmal.domain

/**
 * @property mediaType: Anime or Manga
 * @property contentType The content type of the work. TV series, OVA, ...
 * @property releaseStatus Finished, Airing/publishing, ...
 * @property id The unique identifier of the work.
 * @property originalTitle The display title of the work.
 * @property numReleases The number of episodes/chapters released.
 * @property pictureURL The URL of the picture associated with the work.
 * @property userPreferredTitle The user-preferred title. English, Japanese ...
 * @property synonyms The list of synonyms or alternative titles of the work.
 * @property startDate The start date of the work. When it first appeared.
 * @property endDate The end date of the work. When it last appeared.
 * @property synopsis A brief overview or summary of the work.
 * @property meanScore The mean score of the work.
 * @property rank The rank of the work.
 * @property popularity The popularity score of the work.
 * @property members The number of members or followers of the work.
 * @property nsfw Indicates whether the work contains NSFW (Not Safe For Work) content.
 * @property genres The list of genres associated with the work.
 * @property createdAt The date and time when the work was created.
 * @property updatedAt The date and time when the work was last updated.
 * @property listStatus The user's list status for the work.
 * @property relatedWork The list of related works associated with the work.
 * @property recommendations The list of recommendations for the work.
 */
sealed interface Work {
    val mediaType: MediaType
    val contentType: String
    val releaseStatus: ReleaseStatus
    val id: Int
    val originalTitle: String
    val numReleases: Int
    val pictureURL: String?
    val pictures: List<String>
    val userPreferredTitle: String
    val synonyms: List<String>
    val startDate: String?
    val endDate: String?
    val synopsis: String
    val meanScore: Float?
    val rank: Int?
    val popularity: Int?
    val members: Int?
    val nsfw: Boolean
    val genres: List<Genre>
    val createdAt: String
    val updatedAt: String
    val listStatus: UserListStatus?
    val relatedWork: List<Pair<Work,String>>
    val recommendations: List<Pair<Work, Int>>
}

/**
 * Represents an anime work and implements the [Work] interface.
 * @property startSeason The start season of the Anime, represented as a pair of [Season] and year.
 * @property broadcastTime The broadcast time of the Anime. For example Pair<"Friday", "01:25">
 * @property source The original source of the work. Original, Manga, ...
 * @property avgEpDuration The average duration of each episode in seconds.
 * @property contentRating The content rating of the work. PG-13, R, ...
 * @property studios The list of [AnimeStudio] involved in producing the work.
 * @property statistics List of Pair<Watching/dropped/..., Number of users>.
 */

data class Anime(
    override val contentType: String = "",
    override val releaseStatus: ReleaseStatus = ReleaseStatus.OTHER,
    override val id: Int,
    override val originalTitle: String,
    override val numReleases: Int = 0,
    override val pictureURL: String?,
    override val pictures: List<String> = emptyList(),
    override val userPreferredTitle: String = "",
    override val synonyms: List<String> = emptyList(),
    override val startDate: String? = null,
    override val endDate: String? = null,
    override val synopsis: String = "",
    override val meanScore: Float? = null,
    override val rank: Int? = null,
    override val popularity: Int? = null,
    override val members: Int? = null,
    override val nsfw: Boolean = false,
    override val genres: List<Genre> = emptyList(),
    override val createdAt: String = "",
    override val updatedAt: String = "",
    override val listStatus: UserListStatus? = null,
    override val relatedWork: List<Pair<Work, String>> = emptyList(),
    override val recommendations: List<Pair<Work, Int>> = emptyList(),
    val startSeason: Pair<Season, Int>? = null,
    val broadcastTime: Pair<String, String> = Pair("unknown", "?"),
    val source: String = "",
    val avgEpDuration: Int = 0,
    val contentRating: ContentRating = ContentRating.UNKNOWN,
    val studios: List<AnimeStudio> = emptyList(),
    val statistics: List<Pair<ListStatus, Int>> = emptyList()
) : Work {
    override val mediaType: MediaType
        get() = MediaType.ANIME
}

/**
 * Represents a Manga work and implements the [Work] interface.
 * @property numVolumes Number of volumes of the Manga released so far.
 * @property authors List of authors represented as Triple<"First name", "Last name", "Role">
 */

data class Manga(
    override val contentType: String = "",
    override val releaseStatus: ReleaseStatus = ReleaseStatus.OTHER,
    override val id: Int,
    override val originalTitle: String,
    override val numReleases: Int = 0,
    override val pictureURL: String?,
    override val pictures: List<String> = emptyList(),
    override val userPreferredTitle: String = "",
    override val synonyms: List<String> = emptyList(),
    override val startDate: String? = null,
    override val endDate: String? = null,
    override val synopsis: String = "",
    override val meanScore: Float? = null,
    override val rank: Int? = null,
    override val popularity: Int? = null,
    override val members: Int?= null,
    override val nsfw: Boolean = false,
    override val genres: List<Genre> = emptyList(),
    override val createdAt: String = "",
    override val updatedAt: String = "",
    override val listStatus: UserListStatus? = null,
    override val relatedWork: List<Pair<Work, String>> = emptyList(),
    override val recommendations: List<Pair<Work, Int>> = emptyList(),
    val numVolumes: Int = 0,
    val authors: List<Triple<String, String, String>> = emptyList(),
) : Work {
    override val mediaType: MediaType
        get() = MediaType.MANGA
}